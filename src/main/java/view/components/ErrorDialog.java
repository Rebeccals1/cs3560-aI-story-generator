package view.components;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog {

    /* =========================================================
       EXCEPTION-BASED ERROR
       ========================================================= */
    public static void show(Component parent, String title, Exception ex) {

        String message = title + "\n\n"
                + ex.getClass().getSimpleName()
                + ": " + ex.getMessage();

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /* =========================================================
       GENERIC MESSAGE POPUP
       ========================================================= */
    public static void showMessage(
            Component parent,
            String title,
            String message,
            int type
    ) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                type
        );
    }

    /* =========================================================
       AI DISABLED POPUP (USED ON STARTUP)
       ========================================================= */
    public static void showAIDisabled(Component parent) {

        String message = """
            AI features are disabled.

            Reason:
            Missing OpenAI API key or configuration file.

            To enable AI:
            1. Create: src/main/resources/config.properties
            2. Add:
               OPENAI_API_KEY=your_key_here

            The application will continue
            running in offline mode.
            """;

        showMessage(
                parent,
                "AI Disabled",
                message,
                JOptionPane.WARNING_MESSAGE
        );
    }
}
