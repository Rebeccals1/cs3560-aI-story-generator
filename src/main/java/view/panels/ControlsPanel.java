package view.panels;

import javax.swing.*;
import java.awt.*;

public class ControlsPanel extends JPanel {

    public interface Listener {
        void onControlsBegin(String length, String complexity, String style);
    }

    private final JComboBox<String> length = new JComboBox<>(new String[]{"Short","Medium","Long"});
    private final JComboBox<String> complexity = new JComboBox<>(new String[]{"Child-Friendly","Adult"});
    private final JComboBox<String> style = new JComboBox<>(new String[]{"Descriptive","Neutral","Terse"});

    public ControlsPanel(Listener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.EAST;

        add(new JLabel("Length:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST; add(length, gc);

        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.EAST;
        add(new JLabel("Complexity:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST; add(complexity, gc);

        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.EAST;
        add(new JLabel("Style:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST; add(style, gc);

        gc.gridy++; gc.gridx=1; gc.anchor=GridBagConstraints.EAST;
        JButton begin = new JButton("Begin Story");
        begin.addActionListener(e ->
                listener.onControlsBegin((String) length.getSelectedItem(),
                        (String) complexity.getSelectedItem(),
                        (String) style.getSelectedItem()));
        add(begin, gc);
    }
}
