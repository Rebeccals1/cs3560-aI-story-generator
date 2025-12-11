package view.components;

import javax.swing.*;
import java.awt.*;

public class LoadingIndicator extends JComponent {
    private final JLabel label = new JLabel("Loading…", SwingConstants.CENTER);

    public LoadingIndicator() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 160));
        JPanel inner = new JPanel(new BorderLayout(10,10));
        inner.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        inner.setBackground(new Color(250,250,250));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        inner.add(label, BorderLayout.NORTH);
        inner.add(bar, BorderLayout.CENTER);
        panel.add(inner, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        setVisible(false);
    }

    public void show(String msg) { label.setText(msg); setVisible(true); }
    public void hideIt() { setVisible(false); }
}
