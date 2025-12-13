package model.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StoryStateModel
 *
 * Tracks:
 *   - Current chapter number (1–10)
 *   - Last selected choice
 *   - Full choice history (for save/load)
 *
 * Supports:
 *   - Resetting for new story
 *   - Restoring from SavedStoryModel
 */
public class StoryStateModel {

    public static final int MAX_CHAPTERS = 5;

    private int chapter = 1;

    private ChoiceModel lastChoice;

    private final List<ChoiceRecordModel> history = new ArrayList<>();

    public StoryStateModel() { }

    /* -----------------------------------------------------------
       GETTERS / SETTERS
       ----------------------------------------------------------- */

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        if (chapter < 1) chapter = 1;
        if (chapter > MAX_CHAPTERS) chapter = MAX_CHAPTERS;
        this.chapter = chapter;
    }

    public ChoiceModel getLastChoice() {
        return lastChoice;
    }

    public void setLastChoice(ChoiceModel c) {
        this.lastChoice = c;
    }

    public List<ChoiceRecordModel> getChoiceHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * REQUIRED by save/load system.
     * Replaces the entire choice history list.
     */
    public void setChoiceHistory(List<ChoiceRecordModel> restored) {
        history.clear();
        if (restored != null) {
            history.addAll(restored);
        }
    }

    /* -----------------------------------------------------------
       STATE MANAGEMENT
       ----------------------------------------------------------- */

    public void reset() {
        chapter = 1;
        lastChoice = null;
        history.clear();
    }

    public void nextChapter() {
        if (chapter < MAX_CHAPTERS) {
            chapter++;
        }
    }

    public boolean isComplete() {
        return chapter >= MAX_CHAPTERS;
    }

    /* -----------------------------------------------------------
       CHOICE LOGGING
       ----------------------------------------------------------- */

    public void addChoiceRecord(ChoiceRecordModel record) {
        if (record != null) {
            history.add(record);
        }
    }

    /** INTERNAL — allows MainController to access raw history if needed */
    public List<ChoiceRecordModel> getMutableChoiceHistory() {
        return history;
    }
}
