package controller;


import dao.repository.IEstanciaService;
import dao.repository.ITicketService;
import model.Operator;
import model.Estancia;
import service.EstanciaServiceImpl;
import service.RealTicketPrinterServiceImpl;
import service.TicketServiceImpl;
import view.MainView;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Controller for the MainView.
 * It handles the primary operations of the application after login,
 * such as vehicle entry and exit.
 */
public class MainController {

    private final MainView view;
    private final Operator currentOperator;
    private final IEstanciaService estanciaService;
    private final ITicketService simulationTicketService;
    private final ITicketService realTicketPrinterService;

    /**
     * Constructor for the MainController.
     * @param view The main view instance.
     * @param operator The operator who has logged in.
     */
    public MainController(MainView view, Operator operator) {
        this.view = view;
        this.currentOperator = operator;
        this.estanciaService = new EstanciaServiceImpl();
        this.simulationTicketService = new TicketServiceImpl();
        this.realTicketPrinterService = new RealTicketPrinterServiceImpl();


        // Initialize the view with operator's data
        this.view.setWelcomeMessage(operator.getFullName());

        // Attach listeners from this controller to the view's components
        this.view.addRegisterEntryListener(e -> handleVehicleEntry());
        this.view.addRegisterExitListener(e -> handleVehicleExit());
        this.view.addLogoutListener(e -> handleLogout());

        // When the controller starts, it should load the vehicles currently inside.
        // We will implement this logic later.
        loadInitialVehicles();
    }

    /**
     * Loads vehicles currently in the parking lot into the table when the view is initialized.
     * This method is called from the constructor.
     */
    private void loadInitialVehicles() {
        try {
            List<Estancia> activeStays = estanciaService.getActiveStays();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Estancia estancia : activeStays) {
                String formattedDate = sdf.format(estancia.getEntryDate());
                view.addVehicleToTable(new Object[]{
                        estancia.getLicense_plate(),
                        estancia.getStayType(),
                        formattedDate
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Error al cargar los vehículos activos. Verifique la conexión a la base de datos.",
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the logic for registering a vehicle's entry.
     * Refactored to ask for vehicle type.
     */
    private void handleVehicleEntry() {
        String plate = view.getEntryPlate();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor, ingrese una placa.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- INICIO DE LA REFACTORIZACIÓN ---

        // 1. Crear las opciones y el diálogo para preguntar el tipo de vehículo.
        String[] vehicleOptions = {"Carro", "Moto"};
        int choice = JOptionPane.showOptionDialog(
                view,
                "Seleccione el tipo de vehículo para la placa: " + plate,
                "Tipo de Vehículo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehicleOptions,
                vehicleOptions[0]
        );

        // 2. Si el usuario cierra el diálogo, cancelar la operación.
        if (choice == JOptionPane.CLOSED_OPTION) {
            return; // El usuario canceló, no hacer nada.
        }

        // 3. Obtener el tipo de vehículo seleccionado.
        String selectedVehicleType = vehicleOptions[choice];

        // --- FIN DE LA REFACTORIZACIÓN ---

        try {
            // 4. Llamar a la NUEVA versión del servicio, pasando el tipo de vehículo.
            Estancia newEstancia = estanciaService.registerVehicleEntry(plate, selectedVehicleType, currentOperator.getId());

            if (newEstancia != null) {
                // Actualiza la UI (esto no cambia)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = sdf.format(newEstancia.getEntryDate());

                // NUEVO: Añadir también el tipo de vehículo a la tabla si tienes una columna para ello.
                // Si no, esta parte puede quedar como estaba.
                view.addVehicleToTable(new Object[]{
                        newEstancia.getLicense_plate(),
                        // newEstancia.getVehicleType(), // <- Descomenta si tu tabla tiene esta columna
                        newEstancia.getStayType(),
                        formattedDate
                });

                JOptionPane.showMessageDialog(view, "Ingreso registrado exitosamente para la placa: " + plate, "Ingreso Exitoso", JOptionPane.INFORMATION_MESSAGE);
                view.clearEntryPlateField();

                // --- LÓGICA DE ELECCIÓN DE IMPRESIÓN ---
                askForPrintingChoice(newEstancia);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Error al Registrar Ingreso", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Asks the operator whether to show a simulation or print a real ticket.
     * This method is called after a successful vehicle entry.
     * @param estancia The newly created stay record.
     */
    private void askForPrintingChoice(Estancia estancia) {
        Object[] options = {"Imprimir Ticket", "Ver Simulación", "Omitir"};
        int choice = JOptionPane.showOptionDialog(view,
                "¿Qué desea hacer con el ticket?",
                "Acción de Ticket",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // "Imprimir Ticket"
                realTicketPrinterService.printTicket(estancia, currentOperator);
                break;
            case 1: // "Ver Simulación"
                simulationTicketService.printTicket(estancia, currentOperator);
                break;
            case 2: // "Omitir" or dialog closed
                // Do nothing
                break;
        }
    }

    private void handleVehicleExit() {
        String plate = view.getExitPlate();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor, ingrese una placa.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Calculate exit details
            Estancia estancia = estanciaService.calculateExitDetails(plate);
            double amountToPay = estancia.getAmountToPay();

            // 2. Handle payment confirmation
            boolean proceedWithExit = false;
            if (amountToPay > 0) {
                String message = String.format("Monto a cobrar: $%.2f\n¿Desea registrar el pago y la salida?", amountToPay);
                int response = JOptionPane.showConfirmDialog(view, message, "Confirmar Cobro y Salida", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    proceedWithExit = true;
                }
            } else {
                // No payment needed (monthly or grace period)
                String message = "Salida sin costo para la placa " + plate + ". ¿Confirmar salida?";
                int response = JOptionPane.showConfirmDialog(view, message, "Confirmar Salida", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    proceedWithExit = true;
                }
            }

            // 3. Finalize the exit if confirmed
            if (proceedWithExit) {
                // For now, we assume payment is in "Efectivo"
                estanciaService.finalizeExit(estancia, currentOperator.getId(), amountToPay, "Efectivo");

                // Update the UI
                view.removeVehicleFromTable(plate);
                view.clearExitPlateField();
                JOptionPane.showMessageDialog(view, "Salida registrada exitosamente.", "Salida Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Error al Registrar Salida", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the logout process.
     * Closes the main view and could reopen the login view.
     */
    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(view, "¿Está seguro de que desea cerrar sesión?", "Confirmar Cierre de Sesión", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            view.dispose(); // Close main window
            // Optionally, re-launch the login screen. This would be handled in the Main class.
            System.exit(0); // For now, we just exit the application.
        }
    }
}