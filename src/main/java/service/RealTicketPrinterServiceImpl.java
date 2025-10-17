package service;

import dao.repository.IQRCodeService;
import dao.repository.ITicketService;
import model.Estancia;
import model.Operator;

import javax.print.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;

/**
 * Service implementation for printing tickets to a real hardware printer.
 */
public class RealTicketPrinterServiceImpl implements ITicketService {

    private final IQRCodeService qrCodeService;

    public RealTicketPrinterServiceImpl() {
        this.qrCodeService = new QRCodeServiceImpl();
    }

    /**
     * Creates a ticket image and sends it to the default system printer.
     *
     * @param estancia The stay details.
     * @param operator The operator who registered the entry.
     */
    @Override
    public void printTicket(Estancia estancia, Operator operator) {
        try {
            // 1. Create the entire ticket as a single image
            BufferedImage ticketImage = createTicketImage(estancia, operator);

            // 2. Find the default printer
            PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
            if (defaultPrinter == null) {
                JOptionPane.showMessageDialog(null, "No se encontró una impresora predeterminada.", "Error de Impresión", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Create a print job
            DocPrintJob printJob = defaultPrinter.createPrintJob();

            // 4. Convert our BufferedImage to a format the printer can understand
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(ticketImage, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            // Specify the data format (PNG image)
            DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
            Doc doc = new SimpleDoc(bais, flavor, null);

            // 5. Send the document to the printer
            printJob.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar imprimir el ticket: " + e.getMessage(), "Error de Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renders the complete ticket (text and QR code) into a single BufferedImage.
     * This gives us full control over the layout.
     */
    private BufferedImage createTicketImage(Estancia estancia, Operator operator) throws Exception {
        // --- Ticket Content ---
        String ticketText = buildTicketText(estancia, operator);
        String[] lines = ticketText.split("\n");

        // --- QR Code Generation ---
        long timestamp = estancia.getEntryDate().getTime() / 1000;
        String qrContent = String.format("TICKET:%d|PLATE:%s|DATE:%d", estancia.getId(), estancia.getPlate(), timestamp);
        BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrContent, 120, 120);

        // --- Image Rendering ---
        // Dimensions for a typical thermal printer ticket (e.g., 58mm width)
        int ticketWidth = 220; // in pixels
        int lineHeight = 15;
        int topMargin = 10;
        int leftMargin = 10;
        int ticketHeight = (lines.length * lineHeight) + qrImage.getHeight() + topMargin * 2;

        BufferedImage image = new BufferedImage(ticketWidth, ticketHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ticketWidth, ticketHeight);

        // Black text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

        // Draw each line of text
        int y = topMargin + lineHeight;
        for (String line : lines) {
            g2d.drawString(line, leftMargin, y);
            y += lineHeight;
        }

        // Draw the QR code at the bottom
        int qrX = (ticketWidth - qrImage.getWidth()) / 2; // Centered
        g2d.drawImage(qrImage, qrX, y, null);

        g2d.dispose();
        return image;
    }

    /**
     * Helper method to build the text part of the ticket (reused from simulation).
     */
    private String buildTicketText(Estancia estancia, Operator operator) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "=============================\n" +
                "     CrudPark - Crudzaso\n" +
                "=============================\n" +
                "Ticket #: " + String.format("%06d", estancia.getId()) + "\n" +
                "Placa: " + estancia.getPlate() + "\n" +
                "Tipo: " + estancia.getStayType() + "\n" +
                "Ingreso: " + sdf.format(estancia.getEntryDate()) + "\n" +
                "Operador: " + operator.getFullName() + "\n" +
                "-----------------------------\n"; // QR will be drawn below this
    }
}
