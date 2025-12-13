package service;

import model.story.SceneModel;

/**
 * StoryGenerator
 *
 * Abstraction for any story generation source
 * (OpenAI, offline fallback, mock generator, etc.)
 */
public interface StoryGenerator {

    SceneModel generateScene(String prompt) throws Exception;

    boolean isEnabled();
}
