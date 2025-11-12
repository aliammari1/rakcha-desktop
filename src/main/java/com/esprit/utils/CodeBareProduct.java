package com.esprit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CodeBareProduct {
    private static final Logger LOGGER = Logger.getLogger(CodeBareProduct.class.getName());
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 100;
    private static final String DEFAULT_FORMAT = "PNG";

    /**
     * Generate a barcode image for a product
     *
     * @param productCode
     *                    The product code to encode
     * @param filePath
     *                    The path where the barcode image should be saved
     * @return boolean indicating if generation was successful
     */
    public static boolean generateBarcode(String productCode, Path filePath) {
        try {
            if (productCode == null || productCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Product code cannot be empty");
            }


            BitMatrix matrix = new MultiFormatWriter().encode(productCode, BarcodeFormat.CODE_128, DEFAULT_WIDTH,
                    DEFAULT_HEIGHT);

            MatrixToImageWriter.writeToPath(matrix, DEFAULT_FORMAT, filePath);
            LOGGER.info("Barcode generated successfully: " + filePath);
            return true;

        }
 catch (WriterException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate barcode", e);
            return false;
        }

    }


    /**
     * Read a barcode from an image file
     *
     * @param barcodeImage
     *                     The image file containing the barcode
     * @return The decoded barcode text, or null if reading failed
     */
    public static String readBarcode(File barcodeImage) {
        try {
            BufferedImage image = ImageIO.read(barcodeImage);
            if (image == null) {
                throw new IOException("Failed to read image file");
            }


            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();

        }
 catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to read barcode", e);
            return null;
        }

    }

}

