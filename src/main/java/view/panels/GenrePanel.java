package view.panels;

import javax.swing.*;
import java.awt.*;

public class GenrePanel extends JPanel {

    public interface Listener { void onGenreChosen(String genreKey); }

    public GenrePanel(Listener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10,10,10,10);
        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.CENTER;

        add(new JLabel("<html><h1>Choose a Genre</h1></html>"), gc);
        gc.gridy++;

        String[] genres = {"Fantasy", "Sci-Fi", "Mystery", "Romance", "Horror"};
        JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
        
        for (String genre : genres) {
            JButton button = new JButton(genre);
            button.addActionListener(e -> listener.onGenreChosen(genre.toLowerCase()));
            grid.add(button);
        }
        
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(grid, gc);
    }
}
