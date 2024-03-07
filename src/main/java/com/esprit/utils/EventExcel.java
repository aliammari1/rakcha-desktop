package com.esprit.utils;

import com.esprit.models.Evenement;
import com.esprit.models.Categorie_evenement;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventExcel {
    public void generate(List<Evenement> eventData) {


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Evenements");

        // Création de l'en-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Event Name", "Category", "Start Date", "End Date", "Localisation", "Status", "Description"};
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Remplissage des données
        int rowNum = 1;
        for (Evenement event : eventData) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(event.getId());
            row.createCell(1).setCellValue(event.getNom());
            row.createCell(2).setCellValue(event.getNom_categorieEvenement());
            row.createCell(3).setCellValue(event.getDateDebut().toString());
            row.createCell(4).setCellValue(event.getDateFin().toString());
            row.createCell(5).setCellValue(event.getLieu());
            row.createCell(6).setCellValue(event.getEtat());
            row.createCell(7).setCellValue(event.getDescription());
        }

        // Ajuster la largeur des colonnes
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écriture dans le fichier Excel
        try (FileOutputStream fileOut = new FileOutputStream("EvenementsExcel.xlsx")) {
            workbook.write(fileOut);
            System.out.println("Excel file generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Fermeture du classeur Excel
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}