package service;

import model.story.WorldModel;

/**
 * WorldFactory
 *
 * FACTORY PATTERN:
 * Creates WorldModel instances with safe defaults.
 */
public class WorldFactory {

    private WorldFactory() { }

    public static WorldModel create(
            String location,
            String rule,
            String history
    ) {

        if (location == null || location.isBlank()) {
            location = "An unknown land";
        }

        if (rule == null || rule.isBlank()) {
            rule = "The rules of this world are unclear.";
        }

        if (history == null || history.isBlank()) {
            history = "A forgotten history shapes this world.";
        }

        return new WorldModel(location, rule, history);
    }
}
