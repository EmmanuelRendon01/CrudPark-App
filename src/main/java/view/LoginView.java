package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The login view for the application.
 * It provides the graphical user interface for operator authentication.
 * This class is designed to be visually appealing and user-friendly.
 * It does not contain any business logic.
 */
public class LoginView extends JFrame {

    private final JTextField userField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginView() {
        // --- Set up the main frame ---
        setTitle("CrudPark - Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null); // Center the window on the screen
        setResizable(false);

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Create the main panel with GridBagLayout for precise control ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        mainPanel.setBackground(new Color(80, 80, 80)); //Gray
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Acceso Operador");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE); //
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(0, 0, 25, 0); // Bottom margin
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);

        // --- Username Label and Field ---
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE); //
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Align to the right
        gbc.insets = new Insets(0, 0, 10, 10);
        mainPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Align to the left
        mainPanel.add(userField, gbc);

        // --- Password Label and Field ---
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE); //
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(passwordField, gbc);

        // --- Login Button ---
        loginButton = new JButton("Ingresar");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

// --- SOLUCIÓN APLICADA AQUÍ ---
        loginButton.setOpaque(true); // Permite que setBackground funcione correctamente en Windows.
        loginButton.setBorderPainted(false); // (Opcional) Quita el borde por defecto que puede verse mal con un color sólido.
        loginButton.setBackground(new Color(50, 50, 50)); // Fondo negro
        loginButton.setForeground(Color.WHITE); // Texto blanco
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0); // Top margin
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(loginButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
    }

    /**
     * Retrieves the username entered by the user.
     * This method is called by the controller.
     * @return The username as a String.
     */
    public String getEmail() {
        return userField.getText();
    }

    /**
     * Retrieves the password entered by the user.
     * This method is called by the controller.
     * @return The password as a char array.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Clears the password field.
     * Useful for the controller to call after a login attempt for security.
     */
    public void clearPasswordField() {
        passwordField.setText("");
    }

    /**
     * Adds an ActionListener to the login button.
     * The controller will use this method to listen for click events.
     * @param listener The ActionListener to be added.
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /**
     * Displays a message dialog to the user.
     * This method is called by the controller to show feedback.
     * @param message The message to display.
     * @param title The title of the dialog window.
     * @param messageType The type of message (e.g., JOptionPane.ERROR_MESSAGE).
     */
    public void displayMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}