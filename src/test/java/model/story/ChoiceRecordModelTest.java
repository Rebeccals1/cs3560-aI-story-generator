package model.story;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChoiceRecordModelTest {

    @Test
    void testConstructorAndGetters() {
        ChoiceRecordModel r = new ChoiceRecordModel(2, "A", "Turn left");

        assertEquals(2, r.getChapter());
        assertEquals("A", r.getChoiceId());
        assertEquals("Turn left", r.getChoiceDescription());
    }

    @Test
    void testSetters() {
        ChoiceRecordModel r = new ChoiceRecordModel();

        r.setChapter(3);
        r.setChoiceId("B");
        r.setChoiceDescription("Run away");

        assertEquals(3, r.getChapter());
        assertEquals("B", r.getChoiceId());
        assertEquals("Run away", r.getChoiceDescription());
    }
}
