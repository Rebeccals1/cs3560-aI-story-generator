import javax.swing.SwingUtilities;
import view.MainFrame;
import controller.MainController;

/**
 * Main
 *
 * Entry point for the AI Story Generator desktop application.
 *
 * Responsibilities:
 *   • Bootstraps Swing safely using the EDT
 *   • Creates MainFrame (UI) and MainController (logic)
 *   • Wires the controller to the frame (done inside controller constructor)
 *   • Displays the window
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // Create UI
            MainFrame frame = new MainFrame();

            // Create controller (this automatically calls frame.setController(this))
            new MainController(frame);

            // Display UI
            frame.setVisible(true);
        });
    }
}
