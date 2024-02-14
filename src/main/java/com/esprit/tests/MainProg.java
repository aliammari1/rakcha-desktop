package com.esprit.tests;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;

public class MainProg {

    public static void main(String[] args) {
        CinemaService cs = new CinemaService();
      //  cs.create(new Cinema("pathé", "tunis", "Emma", "image"));
        cs.delete(new Cinema(2, "pathé", "tunis", "Emma", "image"));
       //System.out.println(cs.read());

    }
}
