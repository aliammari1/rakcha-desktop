package com.esprit.utils;

import com.esprit.models.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserPDF {
    public void generate(List<User> userData) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        document.open();
        List<String> attributes = new ArrayList<>() {{
            add("id");
            add("nom");
            add("prenom");
            add("num_telephone");
            add("email");
            add("role");
        }};
        float[] widths = {50, 50, 50, 80, 50, 50};
        PdfPTable table = new PdfPTable(widths);
        addTableHeader(table, attributes);
        addRows(table, userData);
        // addCustomRows(table);

        try {
            document.add(table);
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        document.close();
    }

    private void addTableHeader(PdfPTable table, List<String> attributes) {

        attributes.stream().forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setIndent(10);
            Phrase phrase = new Phrase();
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }

    private void addRows(PdfPTable table, List<User> userData) {
        for (User user : userData) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getNom());
            table.addCell(user.getPrenom());
            table.addCell(String.valueOf(user.getNum_telephone()));
            table.addCell(user.getEmail());
            table.addCell(user.getRole());
        }
    }
//
//    private void addCustomRows(PdfPTable table) {
//        Path path = null;
//        try {
//            path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
//        } catch (URISyntaxException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
//        Image img = null;
//        try {
//            img = Image.getInstance(path.toAbsolutePath().toString());
//        } catch (BadElementException | IOException e) {
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
//        img.scalePercent(10);
//
//        PdfPCell imageCell = new PdfPCell(img);
//        table.addCell(imageCell);
//
//        PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
//        horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(horizontalAlignCell);
//
//        PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
//        verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//        table.addCell(verticalAlignCell);
//    }
}
