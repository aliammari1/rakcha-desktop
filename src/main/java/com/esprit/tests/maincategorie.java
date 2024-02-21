package com.esprit.tests;

import com.esprit.models.categorie;
import com.esprit.models.episode;
import com.esprit.models.serie;
import com.esprit.services.categorie.CategorieService;
import com.esprit.services.episode.EpisodeService;
import com.esprit.services.serie.SerieService;
import com.esprit.utils.DataSource;

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

        CategorieService cs = new CategorieService();
        SerieService ss = new SerieService();

// Création de la catégorie
        categorie nouvelleCategorie = new categorie("Drama", "DescriptionCategorie");
        cs.create(nouvelleCategorie);

// Création de la série avec la catégorie associée
        serie nouvelleSerie = new serie(0, "Serie1", "Resume", "directeur1", "pays", "image1", nouvelleCategorie);
        ss.create(nouvelleSerie);
        System.out.println(ss.read());





    }

}







