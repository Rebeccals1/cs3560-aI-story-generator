package view.panels;

import javax.swing.*;
import java.awt.*;

public class WorldPanel extends JPanel {

    public interface Listener {
        void onWorldContinue(String location, String rule, String history);
    }

    private final JTextField location = new JTextField(24);
    private final JTextField rule = new JTextField(24);
    private final JTextArea history = new JTextArea(5, 30);

    public WorldPanel(Listener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.EAST;

        add(new JLabel("Location:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        add(location, gc);

        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.EAST;
        add(new JLabel("Rule:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        add(rule, gc);

        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.NORTHEAST;
        add(new JLabel("History:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        history.setLineWrap(true); history.setWrapStyleWord(true);
        add(new JScrollPane(history), gc);

        gc.gridy++; gc.gridx=1; gc.anchor=GridBagConstraints.EAST;
        JButton next = new JButton("Save & Continue");
        next.addActionListener(e -> {
            String loc = location.getText().trim();
            if (loc.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a location."); return; }
            listener.onWorldContinue(loc, rule.getText().trim(), history.getText().trim());
        });
        add(next, gc);
    }
}
