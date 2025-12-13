package model.story;

public class SceneModel {

    private String storyText;
    private ChoiceModel choiceA;
    private ChoiceModel choiceB;
    private ChoiceModel choiceC;
    private boolean ending;

    /** Required by Jackson */
    public SceneModel() { }

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

    public SceneModel(String storyText) {
        this(storyText, null, null, null, false);
    }

    public String getStoryText() { return storyText; }
    public ChoiceModel getChoiceA() { return choiceA; }
    public ChoiceModel getChoiceB() { return choiceB; }
    public ChoiceModel getChoiceC() { return choiceC; }
    public boolean isEnding() { return ending; }

    public void setStoryText(String storyText) { this.storyText = storyText; }
    public void setChoiceA(ChoiceModel choiceA) { this.choiceA = choiceA; }
    public void setChoiceB(ChoiceModel choiceB) { this.choiceB = choiceB; }
    public void setChoiceC(ChoiceModel choiceC) { this.choiceC = choiceC; }
    public void setEnding(boolean ending) { this.ending = ending; }
}
