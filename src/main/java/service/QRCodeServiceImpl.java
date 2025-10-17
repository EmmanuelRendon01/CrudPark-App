package service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dao.repository.IQRCodeService;

import java.awt.image.BufferedImage;

/**
 * Service to handle QR code generation.
 */
public class QRCodeServiceImpl implements IQRCodeService {

    /**
     * Generates a QR code image from a given text.
     *
     * @param text The text to encode in the QR code.
     * @param width The desired width of the QR code image.
     * @param height The desired height of the QR code image.
     * @return A BufferedImage object representing the QR code.
     * @throws WriterException if there is an error during QR code generation.
     */
    @Override
    public BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
