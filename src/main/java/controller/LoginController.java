package controller;

import dao.repository.IAuthService;
import model.Operator;
import service.AuthServiceImpl;
import view.LoginView;
import view.MainView;

import javax.swing.*;
import java.util.Optional;

/**
 * Controller for the login functionality.
 * It handles user interactions from the LoginView, communicates with the AuthService,
 * and determines the application's flow based on the authentication result.
 */
public class LoginController {

    private final LoginView view;
    private final IAuthService authService;

    /**
     * Constructor for the LoginController.
     * It initializes the view and the authentication service.
     *
     * @param view The LoginView instance this controller will manage.
     */
    public LoginController(LoginView view) {
        this.view = view;
        // In a real application with dependency injection, the service would be injected.
        this.authService = new AuthServiceImpl();

        // Attach the event listener from the controller to the view's button.
        this.view.addLoginListener(e -> performLogin());
    }

    /**
     * Executes the login process.
     * This method is called when the user clicks the login button.
     * It retrieves credentials from the view, validates them through the auth service,
     * and handles the outcome.
     */
    private void performLogin() {
        String username = view.getUsername();
        char[] passwordChars = view.getPassword();
        String password = new String(passwordChars);

        // Basic input validation.
        if (username.isEmpty() || password.isEmpty()) {
            view.displayMessage("El usuario y la contraseña no pueden estar vacíos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the service layer to perform authentication.
        Optional<Operator> operatorOptional = authService.login(username, password);

        // Handle the result from the service.
        if (operatorOptional.isPresent()) {
            // If login is successful
            Operator loggedInOperator = operatorOptional.get();
            System.out.println("Login successful for user: " + loggedInOperator.getFullName());

            // Close the login window
            view.dispose();

            // Create and show the main application window
            MainView mainView = new MainView();
            new MainController(mainView, loggedInOperator); // Pass the operator to the new controller
            mainView.setVisible(true);

        } else {
            // If login fails
            view.displayMessage("Usuario o contraseña incorrectos, o el usuario está inactivo.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }

        // Clear the password field for security after the attempt.
        // (This is a good practice)
        // view.clearPasswordField();
    }
}