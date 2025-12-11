package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.story.SavedStoryModel;

import java.io.File;
import java.io.IOException;

/**
 * StorySaveSystem
 *
 * Handles save/load for individual story progress files.
 *
 * Responsibilities:
 *   • Ensure /saves folder exists
 *   • Save JSON files in format:
 *        <CharacterName>_chapter<X>.json
 *   • Load SavedStoryModel from a file
 *   • List all .json files inside /saves
 *
 * This system REPLACES StoryLibrary for saving/loading progress.
 */
public class StorySaveSystem {

    private final ObjectMapper mapper = new ObjectMapper();
    private final File saveFolder = new File("saves");

    public StorySaveSystem() {
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
    }

    /* ============================================================
       LIST SAVES
       ============================================================ */
    /**
     * @return All .json save files in /saves directory
     */
    public File[] listSaves() {
        if (!saveFolder.exists()) return new File[0];

        return saveFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
    }

    /* ============================================================
       SAVE GAME
       ============================================================ */
    /**
     * Saves a complete SavedStoryModel to disk.
     * Filename format: <CharacterName>_chapter<X>.json
     */
    public File saveGame(SavedStoryModel model) throws IOException {

        String charName =
                (model.getCharacter() != null && model.getCharacter().getName() != null)
                        ? model.getCharacter().getName().replace(" ", "_")
                        : "Hero";

        int chapter = (model.getScenes() != null)
                ? model.getScenes().size()
                : 1;

        String filename = charName + "_chapter" + chapter + ".json";

        File outFile = new File(saveFolder, filename);
        mapper.writeValue(outFile, model);

        return outFile;
    }

    /* ============================================================
       LOAD GAME
       ============================================================ */
    /**
     * Loads a JSON file into a SavedStoryModel.
     */
    public SavedStoryModel loadGame(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("Save file does not exist.");
        }
        return mapper.readValue(file, SavedStoryModel.class);
    }
}
