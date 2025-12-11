package controller;

import model.story.*;
import service.OpenAIService;
import service.PromptBuilder;
import service.StorySaveSystem;
import view.MainFrame;

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
 * In summary:
 *   This class is the “brain” of the story generator.
 */
public class MainController {

    /* ---------------------------------------------------------
       Fields
       --------------------------------------------------------- */

    private final MainFrame mainFrame;
    private final StorySaveSystem saveSystem = new StorySaveSystem();

    /** Not final — replaced entirely when loading a saved game. */
    private StoryModel storyModel = new StoryModel();

    private final PromptBuilder promptBuilder = new PromptBuilder();
    private final OpenAIService api = new OpenAIService();

    private String selectedGenre;
    private String selectedLength;
    private String selectedComplexity;
    private String selectedStyle;


    /* ---------------------------------------------------------
       Constructor
       --------------------------------------------------------- */

    public MainController(MainFrame frame) {
        this.mainFrame = frame;
        frame.setController(this);
    }


    /* =========================================================
       GENRE / CHARACTER / WORLD / CONTROLS SETUP
       ========================================================= */

    public void onGenreSelected(String g) {
        this.selectedGenre = g;
        storyModel.setGenre(g);
    }

    public void onCharacterEntered(String name, List<String> traits, String backstory) {
        storyModel.setCharacter(new CharacterModel(name, traits, backstory));
    }

    public void onWorldEntered(String location, String rule, String history) {
        storyModel.setWorld(new WorldModel(location, rule, history));
    }

    public void onControlsSelected(String length, String complexity, String style) {
        this.selectedLength = length;
        this.selectedComplexity = complexity;
        this.selectedStyle = style;
    }


    /* =========================================================
       START NEW STORY
       ========================================================= */

    public void startGame() {
        storyModel.reset();
        requestNextScene(null);
        mainFrame.showView(MainFrame.STORY);
    }


    /* =========================================================
       APPLY PLAYER CHOICE
       ========================================================= */

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
       GENERATE NEXT SCENE (ASYNC OPENAI CALL)
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
                    storyModel.setCurrentScene(scene);

                    mainFrame.showScene(scene);

                    boolean enable = !(scene.isEnding() || storyModel.isComplete());
                    mainFrame.getChoicePanel().setButtonsEnabled(enable);

                } catch (Exception ex) {
                    mainFrame.showError("AI Error", ex);
                } finally {
                    mainFrame.hideLoading();
                }
            }

        }.execute();
    }


    /* =========================================================
       LIBRARY NAVIGATION
       ========================================================= */

    public void openLibrary() {
        mainFrame.showView(MainFrame.LIBRARY);
    }


    /* =========================================================
       SAVE CURRENT STORY → /saves/<name>_chapterX.json
       ========================================================= */

    public void saveCurrentStory() {
        try {
            SavedStoryModel saved = new SavedStoryModel(
                    null,                               // auto-generate title
                    storyModel.getGenre(),
                    storyModel.getCharacter(),
                    storyModel.getWorld(),
                    storyModel.getAllScenes(),
                    "{}"                                // settings placeholder
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
            mainFrame.showError("Save Error", ex);
        }
    }


    /* =========================================================
       LOAD STORY FROM A SAVE FILE
       ========================================================= */

    public void loadSaveFile(File file) {
        try {
            SavedStoryModel saved = saveSystem.loadGame(file);

            if (saved == null) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Save file was empty or invalid."
                );
                return;
            }

            /* --- Replace StoryModel entirely --- */
            storyModel = new StoryModel();

            storyModel.setGenre(saved.getGenre());
            storyModel.setCharacter(saved.getCharacter());
            storyModel.setWorld(saved.getWorld());

            storyModel.setScenes(new ArrayList<>(saved.getScenes()));

            if (saved.getChoiceHistory() != null) {
                storyModel.setChoiceHistory(
                        new ArrayList<>(saved.getChoiceHistory())
                );
            }

            int chapterCount = saved.getScenes() != null
                    ? saved.getScenes().size()
                    : 0;

            if (chapterCount == 0) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Save file contains no scenes."
                );
                return;
            }

            storyModel.setCurrentChapter(chapterCount);
            storyModel.restoreCurrentSceneAfterLoad();

            SceneModel last = storyModel.getCurrentScene();

            mainFrame.showScene(last);
            mainFrame.showView(MainFrame.STORY);

            boolean enable = !(last.isEnding() || storyModel.isComplete());
            mainFrame.getChoicePanel().setButtonsEnabled(enable);

        } catch (Exception ex) {
            mainFrame.showError("Load Error", ex);
        }
    }


    /* =========================================================
       TEST SUPPORT (for JUnit)
       ========================================================= */

    public StoryModel getStoryModel() {
        return storyModel;
    }

    public void restoreLoadedStory(SavedStoryModel saved) {
        if (saved == null) return;

        storyModel = new StoryModel();

        storyModel.setGenre(saved.getGenre());
        storyModel.setCharacter(saved.getCharacter());
        storyModel.setWorld(saved.getWorld());

        storyModel.setScenes(saved.getScenes());
        storyModel.restoreCurrentSceneAfterLoad();

        storyModel.setChoiceHistory(saved.getChoiceHistory());

        int chapter = (saved.getScenes() != null)
                ? saved.getScenes().size()
                : 1;

        storyModel.setCurrentChapter(chapter);
    }

    public String getSelectedLength() { return selectedLength; }
    public String getSelectedComplexity() { return selectedComplexity; }
    public String getSelectedStyle() { return selectedStyle; }
}
