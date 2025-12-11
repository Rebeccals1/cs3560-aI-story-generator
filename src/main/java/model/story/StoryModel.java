package model.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StoryModel
 *
 * Central domain model for the AI Story Generator.
 *
 * Stores:
 *   - StoryStateModel (chapter, full choice history)
 *   - CharacterModel (name, traits, backstory)
 *   - WorldModel (location, rule, history)
 *   - List of all generated scenes
 *   - Current scene
 *
 * Updated to support:
 *   - Loading saved games
 *   - Setting scenes + choice history from StorySaveSystem
 *   - Restoring last scene as current
 */
public class StoryModel {

    private final StoryStateModel state = new StoryStateModel();

    private CharacterModel character;
    private WorldModel world;
    private String genre;

    /** Canonical story scenes, in order */
    private final List<SceneModel> scenes = new ArrayList<>();

    /** The current active scene */
    private SceneModel currentScene;


    /* ==========================================================
       GETTERS
       ========================================================== */

    public StoryStateModel getState() {
        return state;
    }

    public CharacterModel getCharacter() {
        return character;
    }

    public WorldModel getWorld() {
        return world;
    }

    public SceneModel getCurrentScene() {
        return currentScene;
    }

    /** Unmodifiable list of all scenes in order */
    public List<SceneModel> getAllScenes() {
        return Collections.unmodifiableList(scenes);
    }

    public String getGenre() {
        return genre;
    }


    /* ==========================================================
       SETTERS
       ========================================================== */

    public void setCharacter(CharacterModel c) {
        this.character = c;
    }

    public void setWorld(WorldModel w) {
        this.world = w;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Normal progression:
     * Sets current scene AND adds it to scenes list.
     */
    public void setCurrentScene(SceneModel scene) {
        this.currentScene = scene;
        if (scene != null) {
            scenes.add(scene);
        }
    }


    /* ==========================================================
       LOADING SAVED GAMES
       ========================================================== */

    /**
     * Replace all scenes with those from a save file.
     */
    public void setScenes(List<SceneModel> loadedScenes) {
        scenes.clear();
        if (loadedScenes != null) {
            scenes.addAll(loadedScenes);
        }
    }

    /**
     * After loading a save, set currentScene = the last scene.
     */
    public void restoreCurrentSceneAfterLoad() {
        if (!scenes.isEmpty()) {
            currentScene = scenes.get(scenes.size() - 1);
        } else {
            currentScene = null;
        }
    }

    /**
     * Restore chapter number from save file.
     */
    public void setCurrentChapter(int chapter) {
        state.setChapter(chapter);
    }

    /**
     * Restore choice history from save file.
     */
    public void setChoiceHistory(List<ChoiceRecordModel> history) {
        state.setChoiceHistory(history);   // ✔ FIXED — this method exists
    }


    /* ==========================================================
       CHAPTER MANAGEMENT
       ========================================================== */

    public void nextChapter() {
        state.nextChapter();
    }

    public boolean isComplete() {
        return state.getChapter() >= StoryStateModel.MAX_CHAPTERS;
    }


    /* ==========================================================
       RESET STORY
       ========================================================== */

    public void reset() {
        scenes.clear();
        currentScene = null;
        state.reset();
    }
}
