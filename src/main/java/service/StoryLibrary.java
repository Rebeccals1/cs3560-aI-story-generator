package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.story.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * StoryLibrary
 *
 * Responsibilities:
 *  • Manage the permanent story library in stories.json (old behavior)
 *  • Provide file-based save/load from the "saves" folder
 *  • Snapshot a StoryModel into a SavedStoryModel and write it as JSON
 *  • List all save files and load a specific one back into memory
 *
 * Summary:
 *  - Library (stories.json) is still available but the UI Save/Load
 *    now uses <CharacterName>_chapterX.json files in "saves/".
 */
public class StoryLibrary {

    /* -------- old library file (still supported) -------- */
    private static final String LIBRARY_PATH = "stories.json";

    /* -------- NEW: per-game save files -------- */
    private static final String SAVES_DIR = "saves";

    private static StoryLibrary instance;

    /** JSON mapper */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Permanent library stories (stories.json) */
    private final List<SavedStoryModel> stories = new ArrayList<>();

    /* ============================================================
       Singleton
       ============================================================ */
    public static synchronized StoryLibrary getInstance() {
        if (instance == null) instance = new StoryLibrary();
        return instance;
    }

    private StoryLibrary() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        loadLibraryFromDisk();
    }

    /* ===============  LIBRARY (stories.json)  =================== */

    public List<SavedStoryModel> getStories() {
        return Collections.unmodifiableList(stories);
    }

    public void saveStory(
            StoryModel storyModel,
            String genre,
            String title,
            String length,
            String complexity,
            String style
    ) {
        SavedStoryModel saved = new SavedStoryModel(
                title,
                genre,
                storyModel.getCharacter(),
                storyModel.getWorld(),
                new ArrayList<>(storyModel.getAllScenes()),
                buildConfigJson(length, complexity, style)
        );

        saved.setChoiceHistory(
                new ArrayList<>(storyModel.getState().getChoiceHistory())
        );

        stories.add(saved);
        saveLibraryToDisk();
    }

    private String buildConfigJson(String length, String complexity, String style) {
        return "{ \"length\": \"" + length + "\", " +
                "\"complexity\": \"" + complexity + "\", " +
                "\"style\": \"" + style + "\" }";
    }

    private void loadLibraryFromDisk() {
        File file = new File(LIBRARY_PATH);
        if (!file.exists()) return;

        try {
            List<SavedStoryModel> data =
                    mapper.readValue(file, new TypeReference<>() {});

            stories.clear();

            for (SavedStoryModel s : data) {
                List<SceneModel> fixedScenes = new ArrayList<>();

                if (s.getScenes() != null) {
                    for (SceneModel scene : s.getScenes()) {
                        ChoiceModel a = scene.getChoiceA() != null
                                ? scene.getChoiceA()
                                : new ChoiceModel("A", "No choice");
                        ChoiceModel b = scene.getChoiceB() != null
                                ? scene.getChoiceB()
                                : new ChoiceModel("B", "No choice");
                        ChoiceModel c = scene.getChoiceC() != null
                                ? scene.getChoiceC()
                                : new ChoiceModel("C", "No choice");

                        fixedScenes.add(new SceneModel(
                                scene.getStoryText(),
                                a, b, c,
                                scene.isEnding()
                        ));
                    }
                }

                SavedStoryModel rebuilt = new SavedStoryModel(
                        s.getTitle(),
                        s.getGenre(),
                        s.getCharacter(),
                        s.getWorld(),
                        fixedScenes,
                        s.getStoryConfiguration()
                );

                if (s.getChoiceHistory() != null) {
                    rebuilt.setChoiceHistory(s.getChoiceHistory());
                }

                stories.add(rebuilt);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load stories.json: " + ex.getMessage());
        }
    }

    private void saveLibraryToDisk() {
        File file = new File(LIBRARY_PATH);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, stories);
        } catch (IOException ex) {
            System.err.println("Failed to save stories.json: " + ex.getMessage());
        }
    }

    public SavedStoryModel getStoryById(String id) {
        for (SavedStoryModel s : stories) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public boolean deleteStory(String id) {
        Iterator<SavedStoryModel> it = stories.iterator();
        while (it.hasNext()) {
            SavedStoryModel s = it.next();
            if (s.getId().equals(id)) {
                it.remove();
                saveLibraryToDisk();
                return true;
            }
        }
        return false;
    }

    /* ===============  SAVES FOLDER  =================== */

    /** Ensure "saves" directory exists and return it. */
    private File getSavesDir() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok) {
                System.err.println("Warning: could not create saves directory: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }

    /**
     * Auto-save the current story into "saves/<CharacterName>_chapterX.json".
     *
     * Returns the File that was written.
     */
    public File autoSaveStory(
            StoryModel storyModel,
            String genre,
            String length,
            String complexity,
            String style
    ) throws IOException {

        File dir = getSavesDir();

        String charName = "Hero";
        if (storyModel.getCharacter() != null &&
                storyModel.getCharacter().getName() != null &&
                !storyModel.getCharacter().getName().isBlank()) {

            charName = storyModel.getCharacter().getName().trim().replaceAll("\\s+", "_");
        }

        int chapter = storyModel.getState().getChapter();
        String fileName = charName + "_chapter" + chapter + ".json";

        File file = new File(dir, fileName);

        SavedStoryModel snapshot = new SavedStoryModel(
                null,                   // let SavedStoryModel pick default title
                genre,
                storyModel.getCharacter(),
                storyModel.getWorld(),
                new ArrayList<>(storyModel.getAllScenes()),
                buildConfigJson(length, complexity, style)
        );
        snapshot.setChoiceHistory(
                new ArrayList<>(storyModel.getState().getChoiceHistory())
        );

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, snapshot);
        return file;
    }

    /** List all .json save files in "saves", newest first. */
    public List<File> listSaveFiles() {
        File dir = getSavesDir();
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));
        if (files == null) return new ArrayList<>();

        List<File> result = new ArrayList<>(Arrays.asList(files));
        result.sort(Comparator.comparingLong(File::lastModified).reversed());
        return result;
    }

    /** Load a single save file from "saves" into a SavedStoryModel. */
    public SavedStoryModel loadGameFile(File file) {
        try {
            return mapper.readValue(file, SavedStoryModel.class);
        } catch (IOException ex) {
            System.err.println("Failed to load save file " + file + ": " + ex.getMessage());
            return null;
        }
    }
}
