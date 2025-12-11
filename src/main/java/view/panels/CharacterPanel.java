package view.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterPanel extends JPanel {

    public interface Listener {
        void onCharacterContinue(String name, List<String> traits, String backstory);
    }

    private final JTextField nameField = new JTextField(20);
    private final JCheckBox brave = new JCheckBox("Brave");
    private final JCheckBox curious = new JCheckBox("Curious");
    private final JCheckBox clever = new JCheckBox("Clever");
    private final JTextArea backstoryArea = new JTextArea(5, 30);

    public CharacterPanel(Listener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        
        // Name field
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.EAST;
        add(new JLabel("Name:"), gc);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        add(nameField, gc);

        // Traits
        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.NORTHEAST;
        add(new JLabel("Traits:"), gc);
        JPanel traitsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        traitsPanel.add(brave); traitsPanel.add(curious); traitsPanel.add(clever);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        add(traitsPanel, gc);

        // Backstory
        gc.gridy++; gc.gridx=0; gc.anchor=GridBagConstraints.NORTHEAST;
        add(new JLabel("Backstory:"), gc);
        backstoryArea.setLineWrap(true); backstoryArea.setWrapStyleWord(true);
        gc.gridx=1; gc.anchor=GridBagConstraints.WEST;
        add(new JScrollPane(backstoryArea), gc);

        // Button
        gc.gridy++; gc.gridx=1; gc.anchor=GridBagConstraints.EAST;
        JButton next = new JButton("Save & Continue");
        next.addActionListener(e -> handleSubmit(listener));
        add(next, gc);
    }
    
    private void handleSubmit(Listener listener) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }
        List<String> traits = new ArrayList<>();
        if (brave.isSelected()) traits.add("Brave");
        if (curious.isSelected()) traits.add("Curious");
        if (clever.isSelected()) traits.add("Clever");
        String back = backstoryArea.getText().trim();
        listener.onCharacterContinue(name, traits, back);
    }
}
