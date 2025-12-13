package service;

/**
 * StoryGeneratorFactory
 *
 * FACTORY PATTERN:
 * Chooses the appropriate StoryGenerator at runtime.
 */
public class StoryGeneratorFactory {

    private StoryGeneratorFactory() {}

    public static StoryGenerator create(OpenAIService api) {
        if (api.isEnabled()) {
            return api;
        }
        return new OfflineStoryGenerator();
    }
}
