package model.story;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * SavedStoryModel
 *
 * Represents a fully saved story state stored on disk.
 *
 * Includes:
 *   • Metadata (title, genre, timestamps)
 *   • CharacterModel + WorldModel
 *   • All generated scenes
 *   • Full canonical choice history
 *   • JSON configuration block (length/complexity/style)
 *
 * Designed for:
 *   • StorySaveSystem.saveGame()
 *   • StorySaveSystem.loadGame()
 *   • LibraryPanel preview + load
 *
 * Notes:
 *   • Backward compatible with older save formats
 *   • Jackson will ignore unknown fields
 *   • Provides auto-title fallback: "<Name>'s Journey"
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavedStoryModel {

    /* ============================================================
       FIELDS
       ============================================================ */

    private String id;

    private String title;
    private String genre;

    private CharacterModel character;
    private WorldModel world;

    private List<SceneModel> scenes;
    private List<ChoiceRecordModel> choiceHistory;

    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    private int totalChapters;

    private String storyConfiguration;


    /* ============================================================
       CONSTRUCTORS
       ============================================================ */

    /** Required by Jackson */
    public SavedStoryModel() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public SavedStoryModel(
            String title,
            String genre,
            CharacterModel character,
            WorldModel world,
            List<SceneModel> scenes,
            String storyConfig) {

        this();

        this.character = character;
        this.world = world;
        this.genre = genre;
        this.scenes = scenes;
        this.storyConfiguration = storyConfig;
        this.totalChapters = (scenes != null ? scenes.size() : 0);

        /* Auto-generate default title */
        if (title == null || title.isBlank()) {
            if (character != null && character.getName() != null) {
                this.title = character.getName() + "'s Journey";
            } else {
                this.title = "Unnamed Hero's Journey";
            }
        } else {
            this.title = title;
        }
    }


    /* ============================================================
       GETTERS & SETTERS
       ============================================================ */

    public String getId() { return id; }
    public void setId(String id) { this.id = id; updateModified(); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; updateModified(); }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; updateModified(); }

    public CharacterModel getCharacter() { return character; }
    public void setCharacter(CharacterModel c) { this.character = c; updateModified(); }

    public WorldModel getWorld() { return world; }
    public void setWorld(WorldModel w) { this.world = w; updateModified(); }

    public List<SceneModel> getScenes() { return scenes; }
    public void setScenes(List<SceneModel> s) {
        this.scenes = s;
        this.totalChapters = (s != null ? s.size() : 0);
        updateModified();
    }

    public List<ChoiceRecordModel> getChoiceHistory() {
        return choiceHistory;
    }

    /**
     * REQUIRED FIX (OPTION A)
     * Allows controller + StoryLibrary to restore choice history.
     */
    public void setChoiceHistory(List<ChoiceRecordModel> history) {
        this.choiceHistory = history;
        updateModified();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }

    public int getTotalChapters() { return totalChapters; }
    public void setTotalChapters(int t) { this.totalChapters = t; updateModified(); }

    public String getStoryConfiguration() { return storyConfiguration; }
    public void setStoryConfiguration(String sc) { this.storyConfiguration = sc; updateModified(); }


    /* ============================================================
       UTILITY
       ============================================================ */

    private void updateModified() {
        this.lastModified = LocalDateTime.now();
    }

    public String getFormattedCreatedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    /** For preview display */
    public String getShortSummary() {
        if (scenes == null || scenes.isEmpty()) return "No content";

        String text = scenes.get(0).getStoryText();
        if (text == null) return "No content";

        return text.length() > 150 ? text.substring(0, 150) + "..." : text;
    }

    /**
     * Safe display title:
     *  - Manual title if available
     *  - Else "<Name>'s Adventure in <Location>"
     */
    public String getDisplayTitle() {
        if (title != null && !title.isBlank()) return title;

        String charName = (character != null && character.getName() != null)
                ? character.getName()
                : "Hero";

        String loc = (world != null && world.getLocation() != null)
                ? world.getLocation()
                : "Unknown Land";

        return charName + "'s Adventure in " + loc;
    }

    @Override
    public String toString() {
        return "SavedStory{" +
                "title='" + getDisplayTitle() + '\'' +
                ", genre='" + genre + '\'' +
                ", chapters=" + totalChapters +
                ", created=" + getFormattedCreatedDate() +
                '}';
    }
}
