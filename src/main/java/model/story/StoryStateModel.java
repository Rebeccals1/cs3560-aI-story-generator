package model.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoryStateModel {

    /** Project specification: 10 chapters total */
    public static final int MAX_CHAPTERS = 10;

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

    /** Used during save/load */
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
}
