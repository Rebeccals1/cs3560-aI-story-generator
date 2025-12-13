package model.story;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavedStoryModelTest {

    @Test
    void testDefaultTitleGeneration() {
        CharacterModel c = new CharacterModel("Luna");
        SavedStoryModel saved = new SavedStoryModel(
                null, "Fantasy", c, null, List.of(), "{}"
        );

        assertEquals("Luna's Journey", saved.getTitle());
    }

    @Test
    void testChaptersAndScenes() {
        SceneModel s = new SceneModel("Test", null, null, null, false);

        SavedStoryModel saved = new SavedStoryModel(
                "My Story", "Adventure", null, null, List.of(s), "{}"
        );

        assertEquals(1, saved.getTotalChapters());
        assertEquals(1, saved.getScenes().size());
    }

    @Test
    void testChoiceHistorySetter() {
        ChoiceRecordModel r =
                new ChoiceRecordModel(1, "A", "Explore cave");

        SavedStoryModel saved = new SavedStoryModel();
        saved.setChoiceHistory(List.of(r));

        assertNotNull(saved.getChoiceHistory());
        assertEquals(1, saved.getChoiceHistory().size());
        assertEquals("Explore cave",
                saved.getChoiceHistory().get(0).getChoiceDescription());
    }

    @Test
    void testMetadataUpdates() {
        SavedStoryModel saved = new SavedStoryModel();

        saved.setTitle("NewTitle");
        saved.setGenre("SciFi");

        assertEquals("NewTitle", saved.getTitle());
        assertEquals("SciFi", saved.getGenre());
    }
}
