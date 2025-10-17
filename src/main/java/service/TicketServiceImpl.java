package service;


import dao.repository.IQRCodeService;
import dao.repository.ITicketService;
import model.Estancia;
import model.Operator;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Service to handle ticket creation and simulated printing.
 */
public class TicketServiceImpl implements ITicketService {

    private final IQRCodeService qrCodeService;

    public TicketServiceImpl() {
        this.qrCodeService = new QRCodeServiceImpl();
    }

    /**
     * Creates and displays a ticket for a given stay.
     *
     * @param estancia The stay details.
     * @param operator The operator who registered the entry.
     */
    @Override
    public void printTicket(Estancia estancia, Operator operator) {
        // 1. Format the ticket content as a String
        String ticketContent = buildTicketText(estancia, operator);

        // 2. Create the QR Code content string
        // TICKET:{id}|PLATE:{placa}|DATE:{timestamp_seconds}
        long timestamp = estancia.getEntryDate().getTime() / 1000; // Convert ms to seconds
        String qrContent = String.format("TICKET:%d|PLATE:%s|DATE:%d",
                estancia.getId(), estancia.getPlate(), timestamp);

        try {
            // 3. Generate the QR code image
            BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrContent, 200, 200);

            // 4. Display the ticket in a dialog for simulation
            showTicketDialog(ticketContent, new ImageIcon(qrImage));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No se pudo generar el código QR del ticket.", "Error de Ticket", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to build the text part of the ticket.
     */
    private String buildTicketText(Estancia estancia, Operator operator) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return "==============================\n" +
                "     CrudPark - Crudzaso\n" +
                "==============================\n" +
                "Ticket #: " + String.format("%06d", estancia.getId()) + "\n" +
                "Placa: " + estancia.getPlate() + "\n" +
                "Tipo: " + estancia.getStayType() + "\n" +
                "Ingreso: " + sdf.format(estancia.getEntryDate()) + "\n" +
                "Operador: " + operator.getFullName() + "\n" +
                "------------------------------\n" +
                "Gracias por su visita.\n" +
                "==============================";
    }

    /**
     * Shows a JOptionPane with the formatted ticket text and the QR image.
     */
    private void showTicketDialog(String text, ImageIcon qrIcon) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JLabel qrLabel = new JLabel(qrIcon);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(qrLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, panel, "Ticket de Ingreso", JOptionPane.INFORMATION_MESSAGE);
    }
}
