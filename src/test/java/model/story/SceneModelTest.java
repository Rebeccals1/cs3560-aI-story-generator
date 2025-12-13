package model.story;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SceneModelTest {

    @Test
    void testConstructorSetsAllFields() {
        ChoiceModel a = new ChoiceModel("A", "Explore the forest");
        ChoiceModel b = new ChoiceModel("B", "Return to camp");
        ChoiceModel c = new ChoiceModel("C", "Climb a tree");

        SceneModel scene = new SceneModel(
                "You stand at the edge of an ancient forest.",
                a, b, c,
                false
        );

        assertEquals("You stand at the edge of an ancient forest.", scene.getStoryText());
        assertEquals(a, scene.getChoiceA());
        assertEquals(b, scene.getChoiceB());
        assertEquals(c, scene.getChoiceC());
        assertFalse(scene.isEnding());
    }

    @Test
    void testSettersUpdateFieldsCorrectly() {
        SceneModel scene = new SceneModel(
                "Original",
                null, null, null,
                false
        );

        scene.setStoryText("Updated text");
        scene.setChoiceA(new ChoiceModel("A", "Left"));
        scene.setChoiceB(new ChoiceModel("B", "Right"));
        scene.setChoiceC(new ChoiceModel("C", "Forward"));
        scene.setEnding(true);

        assertEquals("Updated text", scene.getStoryText());
        assertEquals("Left", scene.getChoiceA().getText());
        assertEquals("Right", scene.getChoiceB().getText());
        assertEquals("Forward", scene.getChoiceC().getText());
        assertTrue(scene.isEnding());
    }

    @Test
    void testEndingSceneOnlyFlag() {
        SceneModel scene = new SceneModel(
                "THE END.",
                null, null, null,
                true
        );

        assertTrue(scene.isEnding());
        assertEquals("THE END.", scene.getStoryText());
        assertNull(scene.getChoiceA());
        assertNull(scene.getChoiceB());
        assertNull(scene.getChoiceC());
    }
}
