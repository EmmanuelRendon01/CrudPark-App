import controller.LoginController;
import view.LoginView;

import javax.swing.*;

/**
 * Main class for the CrudPark Desktop Application.
 * This class is the entry point of the program.
 */
public class Main {

    /**
     * The main method that starts the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // It's a best practice to initialize Swing components on the Event Dispatch Thread (EDT).
        // SwingUtilities.invokeLater ensures that our GUI code runs safely.

        SwingUtilities.invokeLater(() -> {
            // 1. Create an instance of the view.
            LoginView loginView = new LoginView();

            // 2. Create an instance of the controller, passing the view to it.
            // The controller will then take charge of the view's logic.
            new LoginController(loginView);

            // 3. Make the view visible to the user.
            loginView.setVisible(true);
        });
    }
}