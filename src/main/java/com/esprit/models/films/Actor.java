package com.esprit.models.films;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * The Actor class represents an actor in a film.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Actor {

    private Long id;

    private String name;

    private String image;

    private String biography;

    private int numberOfAppearances;

    @Builder.Default
    private List<Film> films = new ArrayList<>();

    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    /**
     * Constructor without id for creating new actor instances.
     * 
     * @param name      the name of the actor
     * @param image     the image path or URL for the actor
     * @param biography the biographical text about the actor
     */
    public Actor(final String name, final String image, final String biography) {
        this.name = name;
        this.image = image;
        this.biography = biography;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }


    /**
     * Create an Actor with the given id, name, image, and biography.
     *
     * @param id        unique identifier of the actor
     * @param name      the actor's full name
     * @param image     path or URL to the actor's image
     * @param biography biographical text describing the actor
     */
    public Actor(final Long id, final String name, final String image, final String biography) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.biography = biography;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }


    /**
         * Create an Actor with the given id and initialize films and categories to empty lists.
         *
         * @param id the actor's unique identifier
         */
    public Actor(final Long id) {
        this.id = id;
        this.films = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

}
