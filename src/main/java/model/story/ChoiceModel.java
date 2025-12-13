package model.story;

/**
 * ChoiceModel
 *
 * Represents a single selectable choice (A / B / C) in a SceneModel.
 *
 * Responsibilities:
 *   • Stores the choice ID ("A", "B", "C")
 *   • Stores the full human-readable choice text
 *   • Safe for Jackson serialization/deserialization
 *
 * Notes:
 *   • Text may be long; UI (ChoicePanel) handles wrapping
 *   • Null-safe behaviors prevent missing data from breaking the UI
 */
public class ChoiceModel {

    /** Choice letter (A, B, C). May be null when loading older saves. */
    private String id;

    /** Full displayed choice text. */
    private String text;

    /** NO-ARG constructor required by Jackson. */
    public ChoiceModel() { }

    public ChoiceModel(String id, String text) {
        this.id = normalizeId(id);
        this.text = normalizeText(text);
    }

    /* ============================================================
       GETTERS
       ============================================================ */

    public String getId() {
        return id;
    }

    public String getText() {
        return (text != null) ? text : "";
    }

    /* ============================================================
       SETTERS
       ============================================================ */

    public void setId(String id) {
        this.id = normalizeId(id);
    }

    public void setText(String text) {
        this.text = normalizeText(text);
    }

    /* ============================================================
       NORMALIZATION HELPERS
       ============================================================ */

    /**
     * Ensures IDs follow the expected form: "A", "B", or "C".
     */
    private String normalizeId(String raw) {
        if (raw == null) return null;
        raw = raw.trim().toUpperCase();
        return (raw.equals("A") || raw.equals("B") || raw.equals("C")) ? raw : raw;
    }

    /**
     * Prevents null text from creating UI exceptions.
     */
    private String normalizeText(String raw) {
        if (raw == null || raw.isBlank()) return "No description available.";
        return raw.trim();
    }

    /* ============================================================
       DEBUGGING SUPPORT
       ============================================================ */

    @Override
    public String toString() {
        return "Choice{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
