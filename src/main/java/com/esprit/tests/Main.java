package com.esprit.tests;

import com.esprit.models.Category;
import com.esprit.models.Film;
import com.esprit.services.CategoryService;
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
        film.setId(25);
        film.setAnnederalisation(2022);
        filmService.update(film);
        List<Film> lf2 = filmService.read();
        for (Film filmm : lf2) {
            System.out.println(filmm);
        }
        Category cat = new Category("Action",
                "La catégorie \"Action\" est dédiée aux œuvres mettant en scène des événements palpitants et des situations dynamiques, souvent caractérisées par des scènes de combat, des courses-poursuites, des explosions et d'autres formes d'excitation intense. Les histoires d'action sont souvent centrées autour de protagonistes courageux confrontés à des défis extrêmes, qu'il s'agisse de combattre des méchants, de sauver des vies ou de protéger des innocents. Les films, séries, livres et jeux vidéo de cette catégorie offrent une dose d'adrénaline aux spectateurs et aux joueurs, les transportant dans des univers remplis de suspense, de danger et d'héroïsme.");
        CategoryService categoryService = new CategoryService();
        categoryService.create(cat);
        List<Category> ca = categoryService.read();
        for (Category catt : ca) {
            System.out.println(catt);
        }
        cat.setId(4);
        cat.setDescription("action description");
        categoryService.update(cat);
        List<Category> ca2 = categoryService.read();
        for (Category catt2 : ca2) {
            System.out.println(catt2);
        }

    }

}
