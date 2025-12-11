package view.components;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog {
    public static void show(Component parent, String title, Exception ex) {
        String message = title + "\n\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
