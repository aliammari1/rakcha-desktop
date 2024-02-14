package com.esprit.tests;

import com.esprit.models.Film;
import com.esprit.services.FilmService;

import java.sql.Time;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Film film = new Film("BatMan", "C:\\Users\\louja\\Downloads\\telecharge.jpg", new Time(2, 30, 15), "Plot. On Halloween, Gotham City mayor Don Mitchell Jr. is murdered by the masked serial killer the Riddler. Reclusive billionaire Bruce Wayne, who has operated for two years as the vigilante Batman, investigates the murder alongside the Gotham City Police Department (GCPD).", 2023, 2);

        FilmService filmService = new FilmService();
        filmService.create(film);
        // filmService.create(film2);
        // filmService.create(film3);
        List<Film> lf = filmService.read();
        for (Film filmm : lf) {
            System.out.println(filmm);
        }
        film.setAnnederalisation(2022);
        filmService.update(film);
        List<Film> lf2 = filmService.read();
        for (Film filmm : lf2) {
            System.out.println(filmm);
        }

    }
}
