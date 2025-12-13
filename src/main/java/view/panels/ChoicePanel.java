package view.panels;

import model.story.SceneModel;
import model.story.ChoiceModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * ChoicePanel
 *
 * Displays three vertically stacked choice buttons.
 * Buttons support:
 *
 *   • Light gray gradient background
 *   • Hover + pressed highlight
 *   • Rounded corners
 *   • Wrapped HTML text for long choice descriptions
 */
public class ChoicePanel extends JPanel {

    /* -----------------------------------------------------------
       Fields
       ----------------------------------------------------------- */

    private final ChoiceButton btnA = new ChoiceButton();
    private final ChoiceButton btnB = new ChoiceButton();
    private final ChoiceButton btnC = new ChoiceButton();

    /** Controller callback for user clicking A/B/C */
    private Consumer<String> callback;

    /* -----------------------------------------------------------
       Constructor
       ----------------------------------------------------------- */

    public ChoicePanel() {
        setLayout(new GridLayout(3, 1, 0, 12));
        setBorder(new EmptyBorder(15, 20, 20, 20));

        styleButton(btnA);
        styleButton(btnB);
        styleButton(btnC);

        btnA.addActionListener(e -> fireChoice("A"));
        btnB.addActionListener(e -> fireChoice("B"));
        btnC.addActionListener(e -> fireChoice("C"));

        add(btnA);
        add(btnB);
        add(btnC);
    }

    /* -----------------------------------------------------------
       Button Styling
       ----------------------------------------------------------- */

    private void styleButton(ChoiceButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 18, 12, 18));
        btn.setForeground(Color.BLACK);
    }

    /* -----------------------------------------------------------
       Controller Callback
       ----------------------------------------------------------- */

    public void setChoiceCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    private void fireChoice(String id) {
        if (callback != null) {
            callback.accept(id);
        }
    }

    /* -----------------------------------------------------------
       External API
       ----------------------------------------------------------- */

    public void setButtonsEnabled(boolean enabled) {
        btnA.setEnabled(enabled);
        btnB.setEnabled(enabled);
        btnC.setEnabled(enabled);
    }

    public void setChoices(SceneModel scene) {
        if (scene == null) {
            btnA.setText("Choice A");
            btnB.setText("Choice B");
            btnC.setText("Choice C");
            return;
        }

        setButtonText(btnA, scene.getChoiceA());
        setButtonText(btnB, scene.getChoiceB());
        setButtonText(btnC, scene.getChoiceC());
    }

    private void setButtonText(JButton btn, ChoiceModel choice) {
        if (choice == null || choice.getText() == null) {
            btn.setText("<html><b>Unavailable</b></html>");
            return;
        }

        btn.setText("<html><b>" + choice.getId() + ".</b> " + choice.getText() + "</html>");
    }

    /* -----------------------------------------------------------
       Light-Gray Gradient Button (Inner Class)
       ----------------------------------------------------------- */

    private static class ChoiceButton extends JButton {

        /* Neutral light-gray palette */
        private final Color topNormal    = new Color(245, 245, 245);
        private final Color bottomNormal = new Color(225, 225, 225);

        private final Color topHover     = new Color(235, 235, 235);
        private final Color bottomHover  = new Color(210, 210, 210);

        private final Color topPressed   = new Color(215, 215, 215);
        private final Color bottomPressed= new Color(195, 195, 195);

        private boolean hover = false;
        private boolean pressed = false;

        public ChoiceButton() {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    pressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color top, bottom;

            if (!isEnabled()) {
                top = bottom = new Color(220, 220, 220);
            } else if (pressed) {
                top = topPressed;
                bottom = bottomPressed;
            } else if (hover) {
                top = topHover;
                bottom = bottomHover;
            } else {
                top = topNormal;
                bottom = bottomNormal;
            }

            GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.setColor(new Color(180, 180, 180));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
