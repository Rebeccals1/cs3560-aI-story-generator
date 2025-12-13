package model.story;

/**
 * ChoiceRecordModel
 *
 * Immutable record for:
 *   - chapter number
 *   - choice ID ("A"/"B"/"C")
 *   - human-readable description text from the AI
 */
public class ChoiceRecordModel {

    private int chapter;
    private String choiceId;
    private String choiceDescription;

    /** Required for Jackson library */
    public ChoiceRecordModel() {
        this.chapter = 0;
        this.choiceId = "";
        this.choiceDescription = "";
    }

    public ChoiceRecordModel(int chapter, String choiceId, String choiceDescription) {
        this.chapter = chapter;
        this.choiceId = choiceId;
        this.choiceDescription = choiceDescription;
    }

    public int getChapter() { return chapter; }
    public String getChoiceId() { return choiceId; }
    public String getChoiceDescription() { return choiceDescription; }

    /** ✅ Make sure this exists (same name!) */
    public void setChapter(int chapter) { this.chapter = chapter; }
    public void setChoiceId(String choiceId) { this.choiceId = choiceId; }
    public void setChoiceDescription(String choiceDescription) { this.choiceDescription = choiceDescription; }
}
