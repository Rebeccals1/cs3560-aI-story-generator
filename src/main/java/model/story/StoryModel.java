package model.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StoryModel
 *
 * Central domain model for the AI Story Generator.
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

    public StoryStateModel getState() { return state; }
    public CharacterModel getCharacter() { return character; }
    public WorldModel getWorld() { return world; }
    public SceneModel getCurrentScene() { return currentScene; }

    /** Unmodifiable list of all scenes in order */
    public List<SceneModel> getAllScenes() {
        return Collections.unmodifiableList(scenes);
    }

    public String getGenre() { return genre; }

    /* ==========================================================
       SETTERS
       ========================================================== */

    public void setCharacter(CharacterModel c) { this.character = c; }
    public void setWorld(WorldModel w) { this.world = w; }
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Normal gameplay progression ONLY.
     * Adds scene to history and sets it current.
     * Do NOT call this method during load operations.
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

    public void setScenes(List<SceneModel> loadedScenes) {
        scenes.clear();
        if (loadedScenes != null) {
            scenes.addAll(loadedScenes);
        }
    }

    public void restoreCurrentSceneAfterLoad() {
        currentScene = scenes.isEmpty() ? null : scenes.get(scenes.size() - 1);
    }

    public void setCurrentChapter(int chapter) {
        state.setChapter(chapter);
    }

    public void setChoiceHistory(List<ChoiceRecordModel> history) {
        state.setChoiceHistory(history);
    }

    /* ==========================================================
       CHAPTER MANAGEMENT
       ========================================================== */

    public void nextChapter() { state.nextChapter(); }

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
