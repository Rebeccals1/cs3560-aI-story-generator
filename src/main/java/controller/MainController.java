package controller;

import model.strategy.AdultMode;
import model.strategy.ChildFriendlyMode;
import model.strategy.StoryModeStrategy;
import model.story.*;
import service.OpenAIService;
import service.PromptBuilder;
import service.StorySaveSystem;
import service.CharacterFactory;
import service.WorldFactory;
import view.MainFrame;
import view.components.ErrorDialog;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MainController
 *
 * Core application logic:
 *   • Handles all UI-driven events
 *   • Manages the StoryModel lifecycle
 *   • Generates new scenes using OpenAI (async)
 *   • Saves / loads progress via StorySaveSystem
 *
 * DESIGN PATTERNS:
 *   - MVC Controller
 *   - Strategy Pattern (StoryModeStrategy)
 *   - Factory Pattern (CharacterFactory, WorldFactory)
 */
public class MainController {

    /* ---------------------------------------------------------
       Fields
       --------------------------------------------------------- */

    private final MainFrame mainFrame;
    private final StorySaveSystem saveSystem = new StorySaveSystem();

    /** Replaced entirely when loading a saved game */
    private StoryModel storyModel = new StoryModel();

    private final PromptBuilder promptBuilder = new PromptBuilder();
    private final OpenAIService api = new OpenAIService();

    private String selectedGenre;
    private String selectedLength;
    private String selectedComplexity;
    private String selectedStyle;

    /** Strategy selected at runtime */
    private StoryModeStrategy modeStrategy;

    /* ---------------------------------------------------------
       Constructor
       --------------------------------------------------------- */

    public MainController(MainFrame frame) {
        this.mainFrame = frame;
        frame.setController(this);

        // Show AI-disabled popup once at startup
        if (!api.isEnabled()) {
            SwingUtilities.invokeLater(() ->
                    ErrorDialog.showAIDisabled(mainFrame)
            );
        }
    }

    /* =========================================================
       SETUP METHODS
       ========================================================= */

    public void onGenreSelected(String g) {
        selectedGenre = g;
        storyModel.setGenre(g);
    }

    public void onCharacterEntered(String name, List<String> traits, String backstory) {
        storyModel.setCharacter(
                CharacterFactory.create(name, traits, backstory)
        );
    }

    public void onWorldEntered(String location, String rule, String history) {
        storyModel.setWorld(
                WorldFactory.create(location, rule, history)
        );
    }

    public void onControlsSelected(String length, String complexity, String style) {
        selectedLength = length;
        selectedComplexity = complexity;
        selectedStyle = style;

        modeStrategy = "Child-Friendly".equalsIgnoreCase(complexity)
                ? new ChildFriendlyMode()
                : new AdultMode();

        promptBuilder.setModeStrategy(modeStrategy);
    }

    /* =========================================================
       STORY FLOW
       ========================================================= */

    public void startGame() {
        storyModel.reset();
        requestNextScene(null);
        mainFrame.showView(MainFrame.STORY);
    }

    public void applyChoice(String id) {

        SceneModel current = storyModel.getCurrentScene();
        if (current == null || current.isEnding() || storyModel.isComplete())
            return;

        ChoiceModel chosen = switch (id) {
            case "A" -> current.getChoiceA();
            case "B" -> current.getChoiceB();
            case "C" -> current.getChoiceC();
            default -> null;
        };

        if (chosen == null) return;

        int chapter = storyModel.getState().getChapter();
        storyModel.getState().addChoiceRecord(
                new ChoiceRecordModel(chapter, id, chosen.getText())
        );

        storyModel.nextChapter();
        requestNextScene(id);
    }

    /* =========================================================
       ASYNC AI REQUEST
       ========================================================= */

    private void requestNextScene(String lastChoiceId) {

        int chapter = storyModel.getState().getChapter();
        mainFrame.showLoading("Generating chapter " + chapter + "...");

        new SwingWorker<SceneModel, Void>() {

            @Override
            protected SceneModel doInBackground() throws Exception {
                String prompt = promptBuilder.buildStoryPrompt(
                        storyModel,
                        lastChoiceId,
                        selectedLength,
                        selectedComplexity,
                        selectedStyle
                );
                return api.generateScene(prompt);
            }

            @Override
            protected void done() {
                try {
                    SceneModel scene = get();

                    // ✅ Force final chapter to become an ending scene
                    scene = forceEndingIfFinalChapter(scene);

                    storyModel.setCurrentScene(scene);

                    mainFrame.showScene(scene);

                    boolean enable = !(scene.isEnding() || storyModel.isComplete());
                    mainFrame.getChoicePanel().setButtonsEnabled(enable);

                } catch (Exception ex) {

                    // Graceful fallback text instead of blank panel
                    SceneModel fallback = new SceneModel(
                            "AI story generation is unavailable.\n\n" +
                                    "Please configure your OpenAI API key to continue.",
                            null, null, null,
                            true
                    );

                    storyModel.setCurrentScene(fallback);
                    mainFrame.showScene(fallback);
                    mainFrame.getChoicePanel().setButtonsEnabled(false);

                    ErrorDialog.show(mainFrame, "AI Error", ex);

                } finally {
                    mainFrame.hideLoading();
                }
            }
        }.execute();
    }

    /**
     * Ensures the final chapter ALWAYS ends with:
     *   --- THE END ---
     * and disables choices permanently.
     */
    private SceneModel forceEndingIfFinalChapter(SceneModel scene) {

        int chapter = storyModel.getState().getChapter();
        if (chapter != StoryStateModel.MAX_CHAPTERS) {
            return scene;
        }

        String text = (scene != null && scene.getStoryText() != null)
                ? scene.getStoryText().trim()
                : "";

        if (!text.isEmpty()) text += "\n\n";
        text += "--- THE END ---";

        return new SceneModel(
                text,
                new ChoiceModel("A", "The End"),
                new ChoiceModel("B", "The End"),
                new ChoiceModel("C", "The End"),
                true
        );
    }

    /* =========================================================
       LIBRARY
       ========================================================= */

    public void openLibrary() {
        mainFrame.showView(MainFrame.LIBRARY);
    }

    /* =========================================================
       SAVE / LOAD
       ========================================================= */

    public void saveCurrentStory() {
        try {
            SavedStoryModel saved = new SavedStoryModel(
                    null,
                    storyModel.getGenre(),
                    storyModel.getCharacter(),
                    storyModel.getWorld(),
                    storyModel.getAllScenes(),
                    "{}"
            );

            saved.setChoiceHistory(
                    new ArrayList<>(storyModel.getState().getChoiceHistory())
            );

            File saveFile = saveSystem.saveGame(saved);

            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Saved:\n" + saveFile.getName()
            );

        } catch (Exception ex) {
            ErrorDialog.show(mainFrame, "Save Error", ex);
        }
    }

    public void loadSaveFile(File file) {
        try {
            SavedStoryModel saved = saveSystem.loadGame(file);
            if (saved == null) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid save file.");
                return;
            }

            restoreLoadedStory(saved);

            SceneModel last = storyModel.getCurrentScene();
            mainFrame.showScene(last);
            mainFrame.showView(MainFrame.STORY);

        } catch (Exception ex) {
            ErrorDialog.show(mainFrame, "Load Error", ex);
        }
    }

    /* =========================================================
       RESTORE STORY (TEST + UI SHARED LOGIC)
       ========================================================= */

    public void restoreLoadedStory(SavedStoryModel saved) {

        if (saved == null) {
            throw new IllegalArgumentException("Saved story cannot be null");
        }

        storyModel = new StoryModel();

        storyModel.setGenre(saved.getGenre());
        storyModel.setCharacter(saved.getCharacter());
        storyModel.setWorld(saved.getWorld());

        storyModel.setScenes(new ArrayList<>(saved.getScenes()));
        storyModel.setChoiceHistory(saved.getChoiceHistory());

        // NOTE: you said "chapters, not scenes" — we can adjust this next if needed.
        storyModel.setCurrentChapter(saved.getScenes().size());
        storyModel.restoreCurrentSceneAfterLoad();
    }

    /* =========================================================
       TEST SUPPORT
       ========================================================= */

    public StoryModel getStoryModel() { return storyModel; }
    public String getSelectedLength() { return selectedLength; }
    public String getSelectedComplexity() { return selectedComplexity; }
    public String getSelectedStyle() { return selectedStyle; }
}
