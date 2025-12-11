package controller;

import model.story.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MainController logic.
 * Focuses on data/state correctness without UI or API calls.
 */
public class MainControllerTest {

    private FakeMainFrame frame;
    private MainController controller;

    @BeforeEach
    void setup() {
        frame = new FakeMainFrame();
        controller = new MainController(frame);
        frame.setController(controller);
    }

    /* =========================================================
       GENRE / CHARACTER / WORLD / CONTROLS
       ========================================================= */

    @Test
    void testGenreSelectionUpdatesModel() {
        controller.onGenreSelected("Fantasy");
        assertEquals("Fantasy", controller.getStoryModel().getGenre());
    }

    @Test
    void testCharacterSetup() {
        controller.onCharacterEntered("Lyria",
                List.of("Kind", "Smart"),
                "Unknown orphan"
        );

        CharacterModel c = controller.getStoryModel().getCharacter();

        assertEquals("Lyria", c.getName());
        assertEquals(List.of("Kind", "Smart"), c.getTraits());
        assertEquals("Unknown orphan", c.getBackstory());
    }

    @Test
    void testWorldSetup() {
        controller.onWorldEntered("Forest", "Magic forbidden", "Ancient ruins");

        WorldModel w = controller.getStoryModel().getWorld();

        assertEquals("Forest", w.getLocation());
        assertEquals("Magic forbidden", w.getRule());
        assertEquals("Ancient ruins", w.getHistory());
    }

    @Test
    void testControlsSelection() {
        controller.onControlsSelected("Long", "Complex", "Cinematic");

        assertEquals("Long", controller.getSelectedLength());
        assertEquals("Complex", controller.getSelectedComplexity());
        assertEquals("Cinematic", controller.getSelectedStyle());
    }

    /* =========================================================
       APPLY CHOICE
       ========================================================= */

    @Test
    void testApplyChoiceAddsToHistory() {

        // SceneModel(String storyText, ChoiceModel a, ChoiceModel b, ChoiceModel c, boolean ending)
        SceneModel scene = new SceneModel(
                "Story begins...",
                new ChoiceModel("A", "Go left"),
                new ChoiceModel("B", "Go right"),
                new ChoiceModel("C", "Hide"),
                false
        );

        // Inject initial scene
        controller.getStoryModel().setCurrentScene(scene);

        int chapterBefore = controller.getStoryModel().getState().getChapter();

        controller.applyChoice("A");

        StoryStateModel state = controller.getStoryModel().getState();

        assertEquals(chapterBefore + 1, state.getChapter());
        assertEquals(1, state.getChoiceHistory().size());

        ChoiceRecordModel record = state.getChoiceHistory().get(0);
        assertEquals("A", record.getChoiceId());
        assertEquals("Go left", record.getChoiceDescription());
    }

    @Test
    void testCannotApplyChoiceOnEndingScene() {

        SceneModel ending = new SceneModel(
                "THE END",
                null, null, null,
                true // ending = true
        );

        controller.getStoryModel().setCurrentScene(ending);

        controller.applyChoice("A");

        // Should remain at chapter 1 and record NO choices
        assertEquals(1, controller.getStoryModel().getState().getChapter());
        assertTrue(controller.getStoryModel().getState().getChoiceHistory().isEmpty());
    }

    /* =========================================================
       START GAME RESET
       ========================================================= */

    @Test
    void testStartGameResetsStateProperly() {

        StoryStateModel s = controller.getStoryModel().getState();
        s.nextChapter();
        s.addChoiceRecord(new ChoiceRecordModel(1, "A", "Test"));

        controller.startGame();

        StoryModel m = controller.getStoryModel();

        assertEquals(1, m.getState().getChapter());
        assertTrue(m.getState().getChoiceHistory().isEmpty());
        assertNull(m.getCurrentScene());   // Scene is generated async later
    }

    /* =========================================================
       LOAD SAVED GAME
       ========================================================= */

    @Test
    void testLoadGameRestoresAllModelState() {

        SavedStoryModel saved = new SavedStoryModel(
                "My Story",
                "SciFi",
                new CharacterModel("Nova"),
                new WorldModel("Mars", "No oxygen", "War-torn past"),
                List.of(
                        new SceneModel("Scene 1", null, null, null, false),
                        new SceneModel("Scene 2", null, null, null, false)
                ),
                "{}"
        );

        saved.setChoiceHistory(List.of(
                new ChoiceRecordModel(1, "A", "Yes"),
                new ChoiceRecordModel(2, "B", "No")
        ));

        controller.restoreLoadedStory(saved);

        StoryModel m = controller.getStoryModel();

        assertEquals("SciFi", m.getGenre());
        assertEquals("Nova", m.getCharacter().getName());
        assertEquals(2, m.getAllScenes().size());
        assertEquals(2, m.getState().getChoiceHistory().size());
        assertEquals(2, m.getState().getChapter());
        assertEquals("Scene 2", m.getCurrentScene().getStoryText());
    }
}
