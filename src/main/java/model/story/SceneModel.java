package model.story;

/**
 * SceneModel
 *
 * Represents a single chapter of AI-generated narrative.
 *
 * Contains:
 *   - storyText : Main narrative text for this scene
 *   - choiceA/B/C : Three possible branching options (may be null)
 *   - ending : Whether this scene concludes the story
 *
 * Supports:
 *   - JSON serialization (no-arg constructor required)
 *   - Full constructor for AI results and test usage
 */
public class SceneModel {

    private String storyText;
    private ChoiceModel choiceA;
    private ChoiceModel choiceB;
    private ChoiceModel choiceC;
    private boolean ending;

    /* ============================================================
       CONSTRUCTORS
       ============================================================ */

    /** Required by Jackson */
    public SceneModel() { }

    /**
     * Full constructor for modern game flow.
     */
    public SceneModel(String storyText,
                      ChoiceModel choiceA,
                      ChoiceModel choiceB,
                      ChoiceModel choiceC,
                      boolean ending) {

        this.storyText = storyText;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.ending = ending;
    }

    /**
     * Convenience constructor used in many unit tests.
     * Creates a non-ending scene with no choices.
     */
    public SceneModel(String storyText) {
        this(storyText, null, null, null, false);
    }

    /* ============================================================
       GETTERS
       ============================================================ */

    public String getStoryText() { return storyText; }
    public ChoiceModel getChoiceA() { return choiceA; }
    public ChoiceModel getChoiceB() { return choiceB; }
    public ChoiceModel getChoiceC() { return choiceC; }
    public boolean isEnding() { return ending; }

    /* ============================================================
       SETTERS
       ============================================================ */

    public void setStoryText(String storyText) { this.storyText = storyText; }
    public void setChoiceA(ChoiceModel choiceA) { this.choiceA = choiceA; }
    public void setChoiceB(ChoiceModel choiceB) { this.choiceB = choiceB; }
    public void setChoiceC(ChoiceModel choiceC) { this.choiceC = choiceC; }
    public void setEnding(boolean ending) { this.ending = ending; }
}
