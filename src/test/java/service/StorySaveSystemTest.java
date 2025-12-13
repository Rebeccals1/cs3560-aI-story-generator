package service;

import model.story.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StorySaveSystemTest {

    private StorySaveSystem saveSystem;

    @BeforeEach
    void setup() {
        saveSystem = new StorySaveSystem();
    }

    @Test
    void testSaveAndLoadGame() throws Exception {

        CharacterModel c = new CharacterModel("Luna");
        SceneModel s = new SceneModel("Hello", null, null, null, false);

        SavedStoryModel saved = new SavedStoryModel(
                "Test Story", "Fantasy", c, null, List.of(s), "{}"
        );

        File file = saveSystem.saveGame(saved);
        assertTrue(file.exists());

        SavedStoryModel loaded = saveSystem.loadGame(file);

        assertEquals("Test Story", loaded.getTitle());
        assertEquals(1, loaded.getScenes().size());
        assertEquals("Luna", loaded.getCharacter().getName());

        file.delete(); // cleanup
    }

    @Test
    void testListSaves() {
        File[] saves = saveSystem.listSaves();
        assertNotNull(saves);
    }
}
