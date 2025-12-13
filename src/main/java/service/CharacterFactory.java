package service;

import model.story.CharacterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * CharacterFactory
 *
 * FACTORY PATTERN:
 * Centralizes character creation rules and defaults.
 */
public class CharacterFactory {

    private CharacterFactory() { }

    public static CharacterModel create(
            String name,
            List<String> traits,
            String backstory
    ) {

        if (name == null || name.isBlank()) {
            name = "Unnamed Hero";
        }

        if (traits == null) {
            traits = new ArrayList<>();
        }

        if (backstory == null || backstory.isBlank()) {
            backstory = "An unknown past.";
        }

        return new CharacterModel(name, traits, backstory);
    }
}
