package controller;

import model.story.SceneModel;
import view.MainFrame;

/**
 * Fake MainFrame used for unit testing MainController logic
 * without launching a Swing UI or calling SwingWorker.
 */
public class FakeMainFrame extends MainFrame {

    private SceneModel lastSceneShown;
    private String lastViewKey;

    public FakeMainFrame() {
        super();
    }

    @Override
    public void showScene(SceneModel scene) {
        this.lastSceneShown = scene;
    }

    @Override
    public void showView(String key) {
        this.lastViewKey = key;
    }

    public SceneModel getLastScene() {
        return lastSceneShown;
    }

    public String getLastViewKey() {
        return lastViewKey;
    }
}
