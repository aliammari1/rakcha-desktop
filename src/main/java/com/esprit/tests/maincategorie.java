package com.esprit.tests;

import com.esprit.models.categorie;
import com.esprit.models.episode;
import com.esprit.models.serie;
import com.esprit.services.categorie.CategorieService;
import com.esprit.services.episode.EpisodeService;
import com.esprit.services.serie.SerieService;
import com.esprit.utils.DataSource;

import java.sql.SQLException;

public class maincategorie {
    public static void main(String[] args) {

            DataSource d = DataSource.getInstance();
          //
             //EpisodeService es = new EpisodeService();
            //es.create(new episode(3,"Premier episode",1,1));
           //es.create(new episode(4,"Deuxieme episode",1,1));
          //es.create(new episode(5,"Troixieme episode",1,1));
         //es.delete(new  episode(5,"Troixieme episode",1,1));
        //:System.out.println(es.read());

        SerieService ss = new SerieService();
        ss.create(new serie(3,"Serie1","Resumeserie1","directeur","pays"));
        ss.create(new serie(4,"Serie2","Resumeserie2","directeur","paysd"));
        ss.create(new serie(5,"Serie3","Resumeserie3","directeur","pays"));


        ss.delete(new serie(3,"Serie1","Resumeserie","directeurdelaserie","paysdeproduction"));

        System.out.println(ss.read());




    }}