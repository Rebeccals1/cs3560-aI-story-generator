package model.story;

public class WorldModel {

    private String location;
    private String rule;
    private String history;

    /** REQUIRED for Jackson */
    public WorldModel() {
        this("", null, null);
    }

    /**
     * Main constructor — ALL defaults live here
     */
    public WorldModel(String location, String rule, String history) {
        this.location = location != null ? location : "";
        this.rule = rule != null ? rule : "No rules";
        this.history = history != null ? history : "An ancient war scarred the land";
    }

    /**
     * Convenience constructor — delegates properly
     */
    public WorldModel(String location) {
        this(location, null, null);
    }

    /**
     * Convenience constructor — delegates properly
     */
    public WorldModel(String location, String rule) {
        this(location, rule, null);
    }

    /* ---------------- GETTERS ---------------- */

    public String getLocation() {
        return location;
    }

    public String getRule() {
        return rule;
    }

    public String getHistory() {
        return history;
    }

    /* ---------------- SETTERS ---------------- */

    public void setLocation(String location) {
        this.location = location != null ? location : "";
    }

    public void setRule(String rule) {
        this.rule = rule != null ? rule : "No rules";
    }

    public void setHistory(String history) {
        this.history = history != null ? history : "An ancient war scarred the land";
    }
}
