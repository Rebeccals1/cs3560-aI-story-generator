package model.strategy;

import model.story.StoryModel;

/**
 * StoryModeStrategy
 *
 * STRATEGY PATTERN:
 *   - Encapsulates "mode" behavior for how the story should be written.
 *   - Examples:
 *       • Child-friendly mode
 *       • Adult mode
 *   - Each implementation injects different tone, content limits, and detail rules
 *     into the AI prompt.
 */
public interface StoryModeStrategy {

    /**
     * Append mode-specific rules to the prompt.
     *
     * Implementations should describe:
     *   - Tone and emotional feel
     *   - Complexity of language
     *   - Content boundaries (violence, romance, horror, etc.)
     *   - How length / style preferences should be interpreted
     *
     * @param sb        The prompt StringBuilder being constructed.
     * @param story     Current StoryModel (character, world, state, genre).
     * @param length    User-selected length ("Short", "Medium", "Long").
     * @param complexity User-selected complexity ("Child-Friendly", "Adult").
     * @param style     User-selected style ("Descriptive", "Neutral", "Terse").
     */
    void applyModeRules(StringBuilder sb,
                        StoryModel story,
                        String length,
                        String complexity,
                        String style);
}
