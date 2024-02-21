package com.esprit.tests;
import com.esprit.models.Categorie;
import com.esprit.models.Sponsor;
import com.esprit.models.Evenement;
import com.esprit.services.CategorieService;
import com.esprit.services.EvenementService;
import com.esprit.services.SponsorService;

import java.io.IOException;
import java.sql.SQLException;

import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainProg {

    public static void main(String[] args) {
        //Evenement
        /*EvenementService es = new EvenementService();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
       try {
            java.util.Date utildateDebut = dateFormat.parse("02/12/2024");
            java.util.Date utildateFin = dateFormat.parse("04/12/2024");

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqldateDebut = new java.sql.Date(utildateDebut.getTime());
            java.sql.Date sqldateFin = new java.sql.Date(utildateFin.getTime());

            es.add(new Evenement(1, "Soiree", sqldateDebut, sqldateFin, "Sfax", 12, "en cours", "soiree jeune"));
            es.update(new Evenement(1,"soiree2",sqldateDebut,sqldateFin,"Tunis",13,"fini","soiree jeune"));
           System.out.println(es.show());
           es.delete(new Evenement(1,"ahmed",null,null,null,13,null,null));
        } catch (ParseException e) {
            System.out.println("Erreur date invalide : " + e.getMessage());
        }
*/
        //Sponsor
       /* SponsorService ss = new SponsorService();
        try {
            File logoFile = new File("C:\\Users\\GMI\\Desktop\\logotest2.png");
            byte[] logoBytes = Files.readAllBytes(logoFile.toPath());

        // Ajouter le sponsor avec le logo
           ss.add(new Sponsor(1, "celio", logoBytes));
            ss.update(new Sponsor(1,"bershka",logoBytes));
            System.out.println(ss.show());
            ss.delete(new Sponsor(1,null,null));
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier logo : " + e.getMessage());
        }*/

        //Categorie
        /*CategorieService cs = new CategorieService();
        cs.add(new Categorie(1,"horreur","cest un film dhorreur"));
        cs.update(new Categorie(1,"comedie","cest un film de comedie"));
        System.out.println(cs.show());
        cs.delete(new Categorie(1,null,null)); */
    }
}

