package com.esprit.utils;
import com.esprit.models.users.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
public class UserPDF {
    /** 
     * @param userData
     */
    public void generate(final List<User> userData) {
        final Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));
        } catch (final DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        document.open();
        List<String> attributes = Arrays.asList("id", "nom", "prenom", "num_telephone", "email", "role");
        final float[] widths = { 50, 50, 50, 80, 50, 50 };
        final PdfPTable table = new PdfPTable(widths);
        this.addTableHeader(table, attributes);
        this.addRows(table, userData);
        // addCustomRows(table);
        try {
            document.add(table);
        } catch (final DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        document.close();
    }
    /** 
     * @param table
     * @param attributes
     */
    private void addTableHeader(final PdfPTable table, final List<String> attributes) {
        attributes.forEach(columnTitle -> {
            final PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setIndent(10);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }
    private void addRows(final PdfPTable table, final List<User> userData) {
        for (final User user : userData) {
            table.addCell(String.valueOf(user.getId()));
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
    // e.printStackTrace();
    // throw new RuntimeException(e);
    // }
    // Image img = null;
    // try {
    // img = Image.getInstance(path.toAbsolutePath().toString());
    // } catch (BadElementException | IOException e) {
    // e.printStackTrace();
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
