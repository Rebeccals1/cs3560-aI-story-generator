package view;

import controller.MainController;
import model.story.SceneModel;
import view.components.ErrorDialog;
import view.components.LoadingIndicator;
import view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MainFrame
 *
 * The main window that holds all screens and routes UI events to MainController.
 */
public class MainFrame extends JFrame
        implements GenrePanel.Listener,
        CharacterPanel.Listener,
        WorldPanel.Listener,
        ControlsPanel.Listener {

    public static final String GENRE = "GENRE";
    public static final String CHARACTER = "CHARACTER";
    public static final String WORLD = "WORLD";
    public static final String CONTROLS = "CONTROLS";
    public static final String STORY = "STORY";
    public static final String LIBRARY = "LIBRARY";

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHolder = new JPanel(cards);

    private final StoryPanel storyPanel = new StoryPanel();
    private final ChoicePanel choicePanel = new ChoicePanel();

    private final LoadingIndicator loading = new LoadingIndicator();

    private MainController controller;

    public MainFrame() {

        super("AI Story Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        add(cardHolder, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        GenrePanel genrePanel = new GenrePanel(this);
        CharacterPanel characterPanel = new CharacterPanel(this);
        WorldPanel worldPanel = new WorldPanel(this);
        ControlsPanel controlsPanel = new ControlsPanel(this);

        LibraryPanel libraryPanel = new LibraryPanel();
        libraryPanel.setMainFrame(this);

        cardHolder.add(genrePanel, GENRE);
        cardHolder.add(characterPanel, CHARACTER);
        cardHolder.add(worldPanel, WORLD);
        cardHolder.add(controlsPanel, CONTROLS);

        // Build story view (story text + vertical choices)
        JPanel storyView = new JPanel(new BorderLayout());
        storyView.add(storyPanel, BorderLayout.CENTER);
        storyView.add(choicePanel, BorderLayout.SOUTH);

        // wire buttons
        choicePanel.setChoiceCallback(id -> {
            if (controller != null) controller.applyChoice(id);
        });

        cardHolder.add(storyView, STORY);
        cardHolder.add(libraryPanel, LIBRARY);

        setGlassPane(loading);
        showView(GENRE);
        pack();
    }

    /* Controller linking */
    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }

    /* Show scene + update choices */
    public void showScene(SceneModel scene) {
        storyPanel.showScene(scene);
        choicePanel.setChoices(scene); // NEW — updates vertical buttons
    }

    public ChoicePanel getChoicePanel() {
        return choicePanel;
    }

    /* Toolbar + status */
    private JToolBar buildToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton back = new JButton("Back");
        JButton library = new JButton("Library");
        JButton save = new JButton("Save");
        JButton help = new JButton("Help");

        back.addActionListener(e -> cards.previous(cardHolder));
        library.addActionListener(e -> controller.openLibrary());
        save.addActionListener(e -> controller.saveCurrentStory());

        help.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Choose genre, character, world, then read and choose."));

        tb.add(back);
        tb.add(library);
        tb.add(save);
        tb.add(Box.createHorizontalGlue());
        tb.add(help);

        return tb;
    }

    private JPanel buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Ready"), BorderLayout.WEST);
        return p;
    }

    public void showLoading(String msg) { loading.show(msg); }
    public void hideLoading() { loading.hideIt(); }
    public void showError(String title, Exception ex) { ErrorDialog.show(this, title, ex); }

    /* Navigation */
    public void showView(String key) { cards.show(cardHolder, key); }

    /* Panel listeners */
    @Override
    public void onGenreChosen(String genreKey) {
        controller.onGenreSelected(genreKey);
        showView(CHARACTER);
    }

    @Override
    public void onCharacterContinue(String name, List<String> traits, String backstory) {
        controller.onCharacterEntered(name, traits, backstory);
        showView(WORLD);
    }

    @Override
    public void onWorldContinue(String location, String rule, String history) {
        controller.onWorldEntered(location, rule, history);
        showView(CONTROLS);
    }

    @Override
    public void onControlsBegin(String length, String complexity, String style) {
        controller.onControlsSelected(length, complexity, style);
        controller.startGame();
        showView(STORY);
    }
}
