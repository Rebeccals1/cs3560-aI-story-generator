package model.story;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorldModelTest {

    @Test
    void testMainConstructor() {
        WorldModel w = new WorldModel("Forest", "Magic outlawed", "Ancient battle");

        assertEquals("Forest", w.getLocation());
        assertEquals("Magic outlawed", w.getRule());
        assertEquals("Ancient battle", w.getHistory());
    }

    @Test
    void testDefaults() {
        WorldModel w = new WorldModel("Forest");

        assertEquals("Forest", w.getLocation());
        assertEquals("No rules", w.getRule());
        assertEquals("An ancient war scarred the land", w.getHistory());
    }

    @Test
    void testSetters() {
        WorldModel w = new WorldModel("A");
        w.setLocation("B");
        w.setRule("C");
        w.setHistory("D");

        assertEquals("B", w.getLocation());
        assertEquals("C", w.getRule());
        assertEquals("D", w.getHistory());
    }
}
