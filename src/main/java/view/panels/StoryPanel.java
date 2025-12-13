package view.panels;

import model.story.SceneModel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * StoryPanel (Upgraded Version)
 *
 * Features:
 *  • Beautiful typography using JTextPane + StyledDocument
 *  • Automatic paragraph splitting and indentation
 *  • Adjustable line spacing for a cleaner reading experience
 *  • Soft fade-in animation for new scenes
 *  • Generous padding around the text
 *  • Scrollable, responsive layout
 *
 * This creates a modern "visual novel" style reading surface.
 */
public class StoryPanel extends JPanel {

    private final JTextPane textPane = new JTextPane();
    private final JScrollPane scrollPane;
    private final JLabel loadingLabel;

    public StoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /* ---------- TEXT AREA (JTextPane for rich formatting) ---------- */
        textPane.setEditable(false);
        textPane.setFont(new Font("georgia", Font.PLAIN, 16));
        textPane.setMargin(new Insets(20, 40, 20, 40)); // top, left, bottom, right
        textPane.setBackground(new Color(250, 248, 245)); // soft paper-like background

        scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        /* ---------- LOADING LABEL ---------- */
        loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setVisible(false);
        loadingLabel.setFont(new Font("georgia", Font.BOLD, 18));
        add(loadingLabel, BorderLayout.SOUTH);
    }

    /* ================================================================
       PUBLIC API
       ================================================================ */

    public void showScene(SceneModel scene) {
        String formatted = formatParagraphs(scene.getStoryText());
        renderText(formatted);
        startFadeInAnimation();
    }

    public void setStoryText(String text) {
        renderText(formatParagraphs(text));
    }

    public void showLoadingState(boolean loading) {
        loadingLabel.setVisible(loading);
    }

    /* ================================================================
       TEXT RENDERING
       ================================================================ */

    private void renderText(String text) {
        StyledDocument doc = textPane.getStyledDocument();
        doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

        try {
            doc.remove(0, doc.getLength()); // clear

            String[] paragraphs = text.split("\n\n");

            for (String p : paragraphs) {
                insertStyledParagraph(doc, p.trim());
                doc.insertString(doc.getLength(), "\n\n", null);
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        textPane.setCaretPosition(0);
    }

    private void insertStyledParagraph(StyledDocument doc, String text) throws BadLocationException {
        SimpleAttributeSet attrs = new SimpleAttributeSet();

        // Indent first line
        StyleConstants.setFirstLineIndent(attrs, 30f);

        // Line spacing
        StyleConstants.setLineSpacing(attrs, 0.2f);

        // Text color
        StyleConstants.setForeground(attrs, new Color(45, 38, 32));

        doc.insertString(doc.getLength(), text, attrs);
    }

    /* ================================================================
       PARAGRAPH AUTO-FORMATTING
       ================================================================ */

    private String formatParagraphs(String text) {
        if (text == null) return "";

        text = text.trim().replace("\r", "");

        // If already spaced, keep it as-is
        if (text.contains("\n\n")) return text;

        // Insert breaks after sentence endings
        text = text.replaceAll("\\.\\s+(?=[A-Z])", ".\n\n");
        text = text.replaceAll("([!?])\\s+(?=[A-Z])", "$1\n\n");

        return text;
    }

    /* ================================================================
       FADE-IN EFFECT FOR NEW SCENES
       ================================================================ */

    private void startFadeInAnimation() {
        textPane.setForeground(new Color(45, 38, 32, 0));

        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            Color c = textPane.getForeground();
            int alpha = Math.min(255, c.getAlpha() + 15);
            textPane.setForeground(new Color(45, 38, 32, alpha));
            if (alpha >= 255) timer.stop();
        });

        timer.start();
    }
}
