package com.esprit.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.users.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserPDF {
    private static final Logger LOGGER = Logger.getLogger(UserPDF.class.getName());

    /**
     * @param userData
     */
    public void generate(List<User> userData) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            UserPDF.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        document.open();
        final List<String> attributes = Arrays.asList("id", "nom", "prenom", "num_telephone", "email", "role");
        float[] widths = { 50, 50, 50, 80, 50, 50 }
;
        PdfPTable table = new PdfPTable(widths);
        addTableHeader(table, attributes);
        addRows(table, userData);
        // addCustomRows(table);
        try {
            document.add(table);
        } catch (DocumentException e) {
            UserPDF.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        document.close();
    }


    /**
     * @param table
     * @param attributes
     */
    private void addTableHeader(PdfPTable table, List<String> attributes) {
        attributes.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setIndent(10);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        }
);
    }


    /** 
     * @param table
     * @param userData
     */
    private void addRows(PdfPTable table, List<User> userData) {
        for (User user : userData) {
            table.addCell(String.valueOf(user.getId().intValue()));
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(String.valueOf(user.getPhoneNumber()));
            table.addCell(user.getEmail());
            table.addCell(user.getRole());
        }

    }

    //
    // private void addCustomRows(PdfPTable table) {
    // Path path = null;
    // try {
    // path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
    // } catch (URISyntaxException e) {
    // LOGGER.log(Level.SEVERE, e.getMessage(), e);
    // throw new RuntimeException(e);
    // }

    // Image img = null;
    // try {
    // img = Image.getInstance(path.toAbsolutePath().toString());
    // } catch (BadElementException | IOException e) {
    // LOGGER.log(Level.SEVERE, e.getMessage(), e);
    // throw new RuntimeException(e);
    // }

    // img.scalePercent(10);
    //
    // PdfPCell imageCell = new PdfPCell(img);
    // table.addCell(imageCell);
    //
    // PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
    // horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    // table.addCell(horizontalAlignCell);
    //
    // PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
    // verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
    // table.addCell(verticalAlignCell);
    // }

}

