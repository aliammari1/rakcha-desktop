package com.esprit.models.films;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.MovieSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a film.
 */

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Film {

    private Long id;

    private String name;

    private String image;

    private Time duration;

    private String description;

    private int releaseYear;

    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    private List<Actor> actors = new ArrayList<>();

    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    @Builder.Default
    private List<FilmComment> comments = new ArrayList<>();

    @Builder.Default
    private List<FilmRating> ratings = new ArrayList<>();

    /**
     * Constructor without id for creating new film instances.
     * 
     * @param name        the name of the film
     * @param image       the image path or URL for the film
     * @param duration    the duration of the film
     * @param description the description of the film
     * @param releaseYear the year the film was released
     */
    public Film(final String name, final String image, final Time duration, final String description,
            final int releaseYear) {
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.description = description;
        this.releaseYear = releaseYear;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }


    /**
     * Constructor with id for existing film instances.
     * 
     * @param id          the unique identifier for the film
     * @param name        the name of the film
     * @param image       the image path or URL for the film
     * @param duration    the duration of the film
     * @param description the description of the film
     * @param releaseYear the year the film was released
     */
    public Film(final Long id, final String name, final String image, final Time duration, final String description,
            final int releaseYear) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.duration = duration;
        this.description = description;
        this.releaseYear = releaseYear;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.ratings = new ArrayList<>();
    }


    /**
     * Create a new Film initialized with the same field values as the given Film.
     *
     * Primitive and object fields are copied directly. Each collection field is set to a new ArrayList
     * containing the same elements as the source (shallow copy). If a source list is null, the corresponding
     * field is initialized to an empty list.
     *
     * @param f the source Film to copy
     */
    public Film(Film f) {
        this.id = f.id;
        this.name = f.name;
        this.image = f.image;
        this.duration = f.duration;
        this.description = f.description;
        this.releaseYear = f.releaseYear;
        this.categories = new ArrayList<>(f.categories != null ? f.categories : new ArrayList<>());
        this.actors = new ArrayList<>(f.actors != null ? f.actors : new ArrayList<>());
        this.movieSessions = new ArrayList<>(f.movieSessions != null ? f.movieSessions : new ArrayList<>());
        this.comments = new ArrayList<>(f.comments != null ? f.comments : new ArrayList<>());
        this.ratings = new ArrayList<>(f.ratings != null ? f.ratings : new ArrayList<>());
    }


    /**
     * Constructor for creating a Film with just an ID.
     *
     * @param id the unique identifier for the film
     */
    public Film(final Long id) {
        this.id = id;
    }

}
