package service;

import dao.repository.IQRCodeService;
import dao.repository.ITicketService;
import model.Estancia;
import model.Operator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.text.SimpleDateFormat;

/**
 * Implementación final del servicio de impresión.
 * Utiliza PrinterJob para un control total y "quema" (fija) las dimensiones del papel
 * para asegurar la máxima compatibilidad con impresoras térmicas de 58mm.
 */
public class RealTicketPrinterServiceImpl implements ITicketService {

    private final IQRCodeService qrCodeService;

    public RealTicketPrinterServiceImpl() {
        this.qrCodeService = new QRCodeServiceImpl();
    }

    @Override
    public void printTicket(Estancia estancia, Operator operator) {
        // 1. Obtenemos el PrinterJob, que nos da control total sobre la impresión.
        PrinterJob printerJob = PrinterJob.getPrinterJob();

        // 2. Creamos un formato de página personalizado.
        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = pageFormat.getPaper();

        // --- LA PARTE CLAVE: "QUEMAMOS" LAS DIMENSIONES DEL PAPEL ---
        // Fijamos los valores en "puntos" (1 pulgada = 72 puntos) para un rollo de 58mm.
        // Esto ignora cualquier configuración incorrecta del driver de la impresora.
        double width = 165;  // Ancho de 58mm en puntos.
        double height = 842; // Una altura grande, segura para papel de rollo.
        paper.setSize(width, height);

        // Fijamos los márgenes. 5 puntos es un margen pequeño y seguro.
        double margin = 5;
        paper.setImageableArea(margin, margin, width - (margin * 2), height - (margin * 2));

        pageFormat.setPaper(paper);
        // --- FIN DE LA CONFIGURACIÓN DEL PAPEL ---

        // 3. Asignamos nuestro objeto "dibujable" (Printable) que contiene el diseño del ticket.
        Printable ticketContent = new TicketPrintable(estancia, operator);
        printerJob.setPrintable(ticketContent, pageFormat);

        // 4. Mostramos el diálogo de impresión estándar del sistema operativo.
        //    Este diálogo permite al usuario seleccionar la impresora.
        //    Si el usuario presiona "Imprimir", el método devuelve 'true'.
        if (printerJob.printDialog()) {
            try {
                // 5. Ejecutamos la impresión.
                printerJob.print();
                JOptionPane.showMessageDialog(null, "Ticket enviado a la impresora.", "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocurrió un error al imprimir el ticket: " + ex.getMessage(), "Error de Impresión", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clase interna que dibuja el contenido del ticket.
     * Su lógica no necesita cambiar, ya que se adapta al PageFormat que le pasemos.
     */
    private class TicketPrintable implements Printable {
        private final Estancia estancia;
        private final Operator operator;

        public TicketPrintable(Estancia estancia, Operator operator) {
            this.estancia = estancia;
            this.operator = operator;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) return NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            int availableWidth = (int) pageFormat.getImageableWidth();

            try {
                String ticketText = buildTicketText(estancia, operator);
                String[] lines = ticketText.split("\n");

                long timestamp = estancia.getEntryDate().getTime() / 1000;
                String qrContent = String.format("TICKET:%d|PLATE:%s|DATE:%d", estancia.getStay_id(), estancia.getLicense_plate(), timestamp);
                BufferedImage qrImage = qrCodeService.generateQRCodeImage(qrContent, 100, 100);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));

                int lineHeight = 12;
                int y = lineHeight;
                for (String line : lines) {
                    g2d.drawString(line, 0, y);
                    y += lineHeight;
                }
                y += 5;

                int qrX = (availableWidth - qrImage.getWidth()) / 2;
                g2d.drawImage(qrImage, qrX, y, null);

            } catch (Exception e) {
                throw new PrinterException("Error al generar contenido del ticket: " + e.getMessage());
            }

            return PAGE_EXISTS;
        }
    }

    /**
     * Helper que construye el texto del ticket. Sin cambios.
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