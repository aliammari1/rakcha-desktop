package com.esprit.tests;

import com.esprit.models.Cinema;
import com.esprit.models.Salle;
import com.esprit.models.Seance;
import com.esprit.services.CinemaService;
import com.esprit.services.SalleService;
import com.esprit.services.SeanceService;

public class MainProg {

    public static void main(String[] args) {
        // CinemaService cs = new CinemaService();
        //  cs.create(new Cinema("pathé", "tunis", "Emma", "image"));
       // cs.delete(new Cinema(2, "pathé", "tunis", "Emma", "image"));
       //System.out.println(cs.read());
        // SalleService ss = new SalleService();
        // ss.create(new Salle(3, 200, 100, 1, "non complet"));
        // ss.create(new Salle(2, 150, 50, 2, "non complet"));
        // ss.delete(new Salle(3, 2, 150, 50, 2, "non complet"));
        // System.out.println(ss.read());
        SeanceService ses = new SeanceService();
        System.out.println(ses.read());



    }
}
