package service;

import model.story.SceneModel;
import model.story.ChoiceModel;

/**
 * OfflineStoryGenerator
 *
 * Fallback generator when AI is unavailable.
 */
public class OfflineStoryGenerator implements StoryGenerator {

    @Override
    public SceneModel generateScene(String prompt) {
        return new SceneModel(
                "AI is currently unavailable.\n\n" +
                        "This is an offline placeholder scene.\n" +
                        "Please configure your API key to enable full story generation.",
                new ChoiceModel("A", "Retry"),
                new ChoiceModel("B", "Restart Story"),
                new ChoiceModel("C", "Exit"),
                false
        );
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
