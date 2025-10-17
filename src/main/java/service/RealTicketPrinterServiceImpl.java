package service;

import dao.repository.IQRCodeService;
import dao.repository.ITicketService;
import model.Estancia;
import model.Operator;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

/**
 * Service implementation for printing tickets.
 * This version uses image rasterization to ensure maximum compatibility
 * with all types of printers, avoiding PostScript conversion issues.
 */
public class RealTicketPrinterServiceImpl implements ITicketService {

    private final IQRCodeService qrCodeService;

    public RealTicketPrinterServiceImpl() {
        this.qrCodeService = new QRCodeServiceImpl();
    }

    @Override
    public void printTicket(Estancia estancia, Operator operator) {
        // --- SECCIÓN DE SELECCIÓN DE IMPRESORA (Funciona bien, no cambia) ---
        PrintService selectedPrinter = selectPrinter();
        if (selectedPrinter == null) {
            return; // El usuario canceló o no hay impresoras
        }
        // --- FIN DE SELECCIÓN ---

        try {
            // 1. Renderizar el ticket completo como una imagen en memoria.
            //    Esto nos da control total y evita que el driver genere PostScript.
            BufferedImage ticketImage = createTicketImage(estancia, operator);

            // 2. Crear un trabajo de impresión con la impresora seleccionada.
            DocPrintJob printJob = selectedPrinter.createPrintJob();

            // 3. Convertir nuestra BufferedImage a un flujo de bytes en un formato estándar (PNG).
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(ticketImage, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            // 4. CLAVE: Especificar que estamos enviando una imagen PNG.
            //    Este DocFlavor es el correcto para enviar una imagen rasterizada.
            //    No le da al driver la oportunidad de interpretar mal los comandos de dibujo.
            DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
            Doc doc = new SimpleDoc(bais, flavor, null);

            // 5. Enviar la imagen directamente a la impresora.
            printJob.print(doc, null);

            JOptionPane.showMessageDialog(null, "Ticket enviado a la impresora: " + selectedPrinter.getName(), "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar imprimir el ticket: " + e.getMessage(), "Error de Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to handle printer selection UI.
     * @return The selected PrintService, or null if canceled.
     */
    private PrintService selectPrinter() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices.length == 0) {
            JOptionPane.showMessageDialog(null, "No se encontró ninguna impresora instalada.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String[] printerNames = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
        }

        String selectedPrinterName = (String) JOptionPane.showInputDialog(null, "Seleccione una impresora:",
                "Imprimir Ticket", JOptionPane.QUESTION_MESSAGE, null, printerNames, printerNames[0]);

        if (selectedPrinterName == null) {
            return null; // Usuario canceló
        }

        for (PrintService printer : printServices) {
            if (printer.getName().equals(selectedPrinterName)) {
                return printer;
            }
        }
        return null;
    }

    /**
     * Renders the complete ticket (text and QR code) into a single BufferedImage.
     * This is the core of our rasterization strategy.
     */
    private BufferedImage createTicketImage(Estancia estancia, Operator operator) throws Exception {
        String ticketText = buildTicketText(estancia, operator);
        String[] lines = ticketText.split("\n");

        long timestamp = estancia.getEntryDate().getTime() / 1000;
        String qrContent = String.format("TICKET:%d|PLATE:%s|DATE:%d", estancia.getStay_id(), estancia.getLicense_plate(), timestamp);
        BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrContent, 120, 120);

        // Dimensiones para impresora térmica (aprox 58mm). Ajusta si es necesario.
        int ticketWidth = 220;
        int lineHeight = 14;
        int topMargin = 10;
        int leftMargin = 10;
        int spacingAfterText = 5;
        int ticketHeight = (lines.length * lineHeight) + qrImage.getHeight() + topMargin * 2 + spacingAfterText;

        // Usamos TYPE_BYTE_GRAY para una imagen en blanco y negro, más eficiente para impresoras térmicas.
        BufferedImage image = new BufferedImage(ticketWidth, ticketHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = image.createGraphics();

        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ticketWidth, ticketHeight);

        // Texto negro
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

        // Dibujar texto
        int y = topMargin + lineHeight;
        for (String line : lines) {
            g2d.drawString(line, leftMargin, y);
            y += lineHeight;
        }

        y += spacingAfterText;

        // Dibujar QR centrado
        int qrX = (ticketWidth - qrImage.getWidth()) / 2;
        g2d.drawImage(qrImage, qrX, y, null);

        g2d.dispose();
        return image;
    }

    /**
     * Helper method to build the text part of the ticket.
     */
    private String buildTicketText(Estancia estancia, Operator operator) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "===========================\n" +
                "     CrudPark - Crudzaso\n" +
                "===========================\n" +
                "Ticket #: " + String.format("%06d", estancia.getStay_id()) + "\n" +
                "Placa: " + estancia.getLicense_plate() + "\n" +
                "Tipo Vehículo: " + estancia.getVehicleType() + "\n" +
                "Tipo Estancia: " + estancia.getStayType() + "\n" +
                "Ingreso: " + sdf.format(estancia.getEntryDate()) + "\n" +
                "Operador: " + operator.getFullName() + "\n" +
                "---------------------------\n";
    }
}