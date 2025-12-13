package model.story;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StoryStateModel.
 * Ensures chapter tracking, choice history, and reset logic work correctly.
 */
class StoryStateModelTest {

    @Test
    void testInitialState() {
        StoryStateModel state = new StoryStateModel();

        assertEquals(1, state.getChapter());
        assertTrue(state.getChoiceHistory().isEmpty());
        assertNull(state.getLastChoice());
    }

    @Test
    void testSetChapterWithinBounds() {
        StoryStateModel state = new StoryStateModel();

        state.setChapter(5);
        assertEquals(5, state.getChapter());

        state.setChapter(5);
        assertEquals(5, state.getChapter());
    }

    @Test
    void testSetChapterOutOfBounds() {
        StoryStateModel state = new StoryStateModel();

        state.setChapter(0);   // below min
        assertEquals(1, state.getChapter());

        state.setChapter(20);  // above max
        assertEquals(StoryStateModel.MAX_CHAPTERS, state.getChapter());
    }

    @Test
    void testNextChapter() {
        StoryStateModel state = new StoryStateModel();
        assertEquals(1, state.getChapter());

        state.nextChapter();
        assertEquals(2, state.getChapter());
    }

    @Test
    void testNextChapterDoesNotExceedMax() {
        StoryStateModel state = new StoryStateModel();

        // advance to max
        for (int i = 0; i < 20; i++) {
            state.nextChapter();
        }

        assertEquals(StoryStateModel.MAX_CHAPTERS, state.getChapter());
    }

    @Test
    void testSetChoiceHistory() {
        StoryStateModel state = new StoryStateModel();

        List<ChoiceRecordModel> history = List.of(
                new ChoiceRecordModel(1, "A", "Went left"),
                new ChoiceRecordModel(2, "B", "Went right")
        );

        state.setChoiceHistory(history);

        assertEquals(2, state.getChoiceHistory().size());
        assertEquals("A", state.getChoiceHistory().get(0).getChoiceId());
        assertEquals("Went left", state.getChoiceHistory().get(0).getChoiceDescription());
    }

    @Test
    void testAddChoiceRecord() {
        StoryStateModel state = new StoryStateModel();

        ChoiceRecordModel rec = new ChoiceRecordModel(1, "A", "Accepted the offer");
        state.addChoiceRecord(rec);

        assertEquals(1, state.getChoiceHistory().size());
        assertEquals("A", state.getChoiceHistory().get(0).getChoiceId());
    }

    @Test
    void testResetClearsState() {
        StoryStateModel state = new StoryStateModel();

        // simulate progress
        state.nextChapter();
        state.addChoiceRecord(new ChoiceRecordModel(1, "A", "Go"));

        state.reset();

        assertEquals(1, state.getChapter());
        assertTrue(state.getChoiceHistory().isEmpty());
        assertNull(state.getLastChoice());
    }

    @Test
    void testIsComplete() {
        StoryStateModel state = new StoryStateModel();

        assertFalse(state.isComplete());

        state.setChapter(StoryStateModel.MAX_CHAPTERS);
        assertTrue(state.isComplete());
    }
}
