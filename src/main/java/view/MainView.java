package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The main operational view for the parking operator.
 * This window serves as the dashboard after a successful login.
 */
public class MainView extends JFrame {

    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JTextField entryPlateField;
    private JButton registerEntryButton;
    private JTextField exitPlateField;
    private JButton registerExitButton;
    private JTable vehiclesTable;
    private DefaultTableModel tableModel;

    public MainView() {
        // --- Main Frame Setup ---
        setTitle("CrudPark - Panel de Operaciones");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout with gaps

        // --- North Panel: Welcome Message and Logout ---
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        welcomeLabel = new JLabel("Bienvenido, [Operador]");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton = new JButton("Cerrar Sesión");
        northPanel.add(welcomeLabel);
        northPanel.add(logoutButton);
        add(northPanel, BorderLayout.NORTH);

        // --- Center Panel: Table of Vehicles Currently Inside ---
        String[] columnNames = {"Placa", "Tipo", "Fecha y Hora de Ingreso"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make table cells non-editable
                return false;
            }
        };
        vehiclesTable = new JTable(tableModel);
        vehiclesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehiclesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(vehiclesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Vehículos Actualmente en el Parqueadero"));
        add(scrollPane, BorderLayout.CENTER);

        // --- South Panel: Action Forms ---
        JPanel southPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, with a gap
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Entry Form Panel
        JPanel entryPanel = new JPanel(new FlowLayout());
        entryPanel.setBorder(BorderFactory.createTitledBorder(null, "Registrar Ingreso", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), Color.BLUE));
        entryPanel.add(new JLabel("Placa:"));
        entryPlateField = new JTextField(10);
        registerEntryButton = new JButton("Registrar Ingreso");
        entryPanel.add(entryPlateField);
        entryPanel.add(registerEntryButton);

        // Exit Form Panel
        JPanel exitPanel = new JPanel(new FlowLayout());
        exitPanel.setBorder(BorderFactory.createTitledBorder(null, "Registrar Salida", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), Color.RED));
        exitPanel.add(new JLabel("Placa:"));
        exitPlateField = new JTextField(10);
        registerExitButton = new JButton("Registrar Salida");
        exitPanel.add(exitPlateField);
        exitPanel.add(registerExitButton);

        southPanel.add(entryPanel);
        southPanel.add(exitPanel);
        add(southPanel, BorderLayout.SOUTH);
    }

    // --- Getters for Controller ---
    public String getEntryPlate() {
        return entryPlateField.getText().trim().toUpperCase();
    }

    public String getExitPlate() {
        return exitPlateField.getText().trim().toUpperCase();
    }

    // --- Methods to interact with the view ---
    public void setWelcomeMessage(String operatorName) {
        welcomeLabel.setText("Bienvenido, " + operatorName);
    }

    public void addVehicleToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public void removeVehicleFromTable(String plate) {
        // Find and remove the row corresponding to the plate
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(plate)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }

    public void clearEntryPlateField() {
        entryPlateField.setText("");
    }

    public void clearExitPlateField() {
        exitPlateField.setText("");
    }

    // --- Action Listeners for Controller ---
    public void addRegisterEntryListener(ActionListener listener) {
        registerEntryButton.addActionListener(listener);
    }

    public void addRegisterExitListener(ActionListener listener) {
        registerExitButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}
