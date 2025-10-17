package dao.repository;

import com.google.zxing.WriterException;
import java.awt.image.BufferedImage;

public interface IQRCodeService {
    BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException;
}
