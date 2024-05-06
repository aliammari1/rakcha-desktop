package com.esprit.utils;

import com.esprit.models.evenements.Evenement;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EventPDF {
    public void generate(final List<Evenement> eventData) {
        final Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("EvenementsPDF.pdf"));
        } catch (final DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        document.open();

        Paragraph title = new Paragraph("Detailed Events' List :                                                                                                                                          ");
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD); // Exemple : taille 16 et police Helvetica en gras
        title.setFont(titleFont);
        title.setAlignment(Element.ALIGN_CENTER);


        try {
            document.add(title);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        final List<String> attributes = new ArrayList<>() {{
            this.add("ID");
            this.add("Event Name");
            this.add("Category");
            this.add("Start Date");
            this.add("End Date");
            this.add("Localistaion");
            this.add("Status");
            this.add("Description");
        }};
        final float[] widths = {50, 120, 100, 100, 100, 120, 80, 120};
        final PdfPTable table = new PdfPTable(widths);
        this.addTableHeader(table, attributes);
        this.addRows(table, eventData);

        try {
            document.add(table);
        } catch (final DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        document.close();
    }

    private void addTableHeader(final PdfPTable table, final List<String> attributes) {
        final int fontSize = 10;

        Font font = FontFactory.getFont(FontFactory.HELVETICA, fontSize);

        attributes.forEach(columnTitle -> {
            final PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.MAGENTA);
            header.setBorderWidth(1);
            header.setIndent(3);


            header.setPhrase(new Phrase(columnTitle, font));
            table.addCell(header);
        });
    }

    private void addRows(final PdfPTable table, final List<Evenement> eventData) {
        final int fontSize = 9;

        Font font = FontFactory.getFont(FontFactory.HELVETICA, fontSize);

        for (final Evenement event : eventData) {
            // Création de la phrase avec une taille de police spécifiée
            Phrase phrase = new Phrase(String.valueOf(event.getId()), font);
            table.addCell(phrase);

            // Répétez le processus pour chaque cellule de données
            phrase = new Phrase(event.getNom(), font);
            table.addCell(phrase);

            phrase = new Phrase(event.getNom_categorieEvenement(), font);
            table.addCell(phrase);

            phrase = new Phrase(String.valueOf(event.getDateDebut()), font);
            table.addCell(phrase);

            phrase = new Phrase(String.valueOf(event.getDateFin()), font);
            table.addCell(phrase);

            phrase = new Phrase(event.getLieu(), font);
            table.addCell(phrase);

            phrase = new Phrase(event.getEtat(), font);
            table.addCell(phrase);

            phrase = new Phrase(event.getDescription(), font);
            table.addCell(phrase);
        }


    }
}