package model.story;

public class WorldModel {
    private String location;
    private String rule;
    private String history;

    /** REQUIRED for Jackson library */
    public WorldModel() { }

    // Main constructor - uses defaults for missing values
    public WorldModel(String location, String rule, String history) {
        this.location = location != null ? location : "";
        this.rule = rule != null ? rule : "No rules";
        this.history = history != null ? history : "An ancient war scarred the land";
    }

    // Convenience constructors delegate to main constructor
    public WorldModel(String location) {
        this(location, null, null);
    }

    public WorldModel(String location, String rule) {
        this(location, rule, null);
    }

    // Getters + Setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
