package com.esprit.tests;

import com.esprit.models.categorie;
import com.esprit.models.episode;
import com.esprit.models.serie;
import com.esprit.services.categorie.CategorieService;
import com.esprit.services.episode.EpisodeService;
import com.esprit.services.serie.SerieService;
import com.esprit.utils.DataSource;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.SQLException;

import java.io.File;
import java.nio.file.Files;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

public class maincategorie {
    public static void main(String[] args) {

        DataSource d = DataSource.getInstance();
        //


        // EpisodeService es = new EpisodeService();
        // es.create(new episode(3, "Premier episode", 1, 1, null, new Serie()));
        // System.out.println(es.read());


//SerieService ss = new SerieService();
        //ss.create(new serie(3,"Serie1","Resumeserie1","directeur","pays","image1"));
        //ss.create(new serie(4,"Serie2","Resumeserie2","directeur","paysd","image2"));
        //ss.create(new serie(5,"Serie3","Resumeserie3","directeur","pays","image3"));


        //ss.delete(new serie(3,"Serie1","Resumeserie","directeurdelaserie","pays","image1"));

        //System.out.println(ss.read());
        //SerieService ss = new SerieService();


        SerieService ss = new SerieService();


// Création de la série avec la catégorie associée
        //CategorieService categorieService = new CategorieService();
        //categorie categorie = categorieService.getCategorie(5);

        //if (categorie != null && categorie.getNom() != null) {
        //  serie nouvelleSerie = new serie(0, "nom", "Resume", "directeur1", "pays", "image1", categorie.getIdcategorie());
        //ss.create(nouvelleSerie);
        //System.out.println(ss.read());
        //} else {
        //  System.out.println("Unable to retrieve the category with ID 5.");
        //}

        //}
        //Création d'une nouvelle catégorie sans la récupérer de la base de données
        categorie nouvelleCategorie = new categorie();
        nouvelleCategorie.setIdcategorie(5); // Remplacez par l'ID que vous souhaitez
        nouvelleCategorie.setNom("NomCategorie");
        nouvelleCategorie.setDescription("DescriptionCategorie");
        try {
            File logoFile = new File("C:\\Users\\LENOVO\\Downloads\\421267029_398380526010741_29775440009430911_n.png");
            byte[] logoBytes = Files.readAllBytes(logoFile.toPath());
            try {
                Blob logoBlob = new SerialBlob(logoBytes);
                serie nouvelleSerie = new serie(0, "NomSerie", "ResumeSerie", "DirecteurSerie", logoBlob, "ImageSerie", nouvelleCategorie.getIdcategorie());
                ss.create(nouvelleSerie);
                System.out.println(ss.read());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Utilisation de la nouvelle catégorie pour créer une série


        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier logo : " + e.getMessage());
        }
    }
}









