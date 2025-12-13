package model.story;

import java.util.ArrayList;
import java.util.List;

/**
 * CharacterModel
 *
 * Pure domain model representing the story protagonist.
 */
public class CharacterModel {

    private String name;
    private List<String> traits;
    private String backstory;

    /** Required for Jackson */
    public CharacterModel() {
        this("", null, null);
    }

    /** Main constructor */
    public CharacterModel(String name, List<String> traits, String backstory) {
        this.name = name != null ? name : "";
        this.traits = traits != null ? traits : new ArrayList<>();
        this.backstory = (backstory != null && !backstory.isBlank())
                ? backstory
                : "Unknown";
    }

    /** For Testing */
    public CharacterModel(String name) {
        this(name, null, null);
    }

    /* -----------------------------
       Getters / Setters
       ----------------------------- */

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits != null ? traits : new ArrayList<>();
    }

    public String getBackstory() { return backstory; }

    public void setBackstory(String backstory) {
        this.backstory = (backstory != null && !backstory.isBlank())
                ? backstory
                : "Unknown";
    }
}
