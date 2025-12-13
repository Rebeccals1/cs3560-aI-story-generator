package model.story;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoryModelTest {

    @Test
    void testSetCharacterAndWorldAndGenre() {
        StoryModel m = new StoryModel();

        CharacterModel c = new CharacterModel("Aria");
        WorldModel w = new WorldModel("Eldoria", "Magic rules", "Ancient wars");

        m.setCharacter(c);
        m.setWorld(w);
        m.setGenre("Fantasy");

        assertEquals("Aria", m.getCharacter().getName());
        assertEquals("Eldoria", m.getWorld().getLocation());
        assertEquals("Fantasy", m.getGenre());
    }

    @Test
    void testSetCurrentSceneAppendsToList() {
        StoryModel m = new StoryModel();

        SceneModel s1 = new SceneModel("Scene 1");
        SceneModel s2 = new SceneModel("Scene 2");

        m.setCurrentScene(s1);
        m.setCurrentScene(s2);

        assertEquals(2, m.getAllScenes().size());
        assertEquals("Scene 2", m.getCurrentScene().getStoryText());
    }

    @Test
    void testSetScenesReplacesAllScenes() {
        StoryModel m = new StoryModel();

        SceneModel old1 = new SceneModel("Old A");
        SceneModel old2 = new SceneModel("Old B");

        m.setCurrentScene(old1);
        m.setCurrentScene(old2);

        assertEquals(2, m.getAllScenes().size());

        List<SceneModel> newScenes = List.of(
                new SceneModel("New 1"),
                new SceneModel("New 2"),
                new SceneModel("New 3")
        );

        m.setScenes(newScenes);

        assertEquals(3, m.getAllScenes().size());
        assertEquals("New 1", m.getAllScenes().get(0).getStoryText());
    }

    @Test
    void testRestoreCurrentSceneAfterLoad() {
        StoryModel m = new StoryModel();

        List<SceneModel> scenes = List.of(
                new SceneModel("First"),
                new SceneModel("Second"),
                new SceneModel("Third")
        );

        m.setScenes(scenes);
        m.restoreCurrentSceneAfterLoad();

        assertNotNull(m.getCurrentScene());
        assertEquals("Third", m.getCurrentScene().getStoryText());
    }

    @Test
    void testSetCurrentChapterUpdatesState() {
        StoryModel m = new StoryModel();
        m.setCurrentChapter(5);
        assertEquals(5, m.getState().getChapter());
    }

    @Test
    void testSetChoiceHistory() {
        StoryModel m = new StoryModel();

        List<ChoiceRecordModel> history = List.of(
                new ChoiceRecordModel(1, "A", "Go"),
                new ChoiceRecordModel(2, "B", "Stay")
        );

        m.setChoiceHistory(history);

        assertEquals(2, m.getState().getChoiceHistory().size());
        assertEquals("A", m.getState().getChoiceHistory().get(0).getChoiceId());
    }

    @Test
    void testNextChapter() {
        StoryModel m = new StoryModel();
        assertEquals(1, m.getState().getChapter());

        m.nextChapter();
        assertEquals(2, m.getState().getChapter());
    }

    @Test
    void testResetClearsScenesAndHistory() {
        StoryModel m = new StoryModel();

        m.setCurrentScene(new SceneModel("Scene X"));
        m.getState().addChoiceRecord(new ChoiceRecordModel(1, "A", "Test"));

        assertEquals(1, m.getAllScenes().size());
        assertEquals(1, m.getState().getChoiceHistory().size());

        m.reset();

        assertEquals(0, m.getAllScenes().size());
        assertEquals(0, m.getState().getChoiceHistory().size());
        assertEquals(1, m.getState().getChapter());
        assertNull(m.getCurrentScene());
    }
}
