package com.esprit.models.films;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a category of films.
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
public class Category {

    private Long id;

    private String name;

    private String description;

    @Builder.Default
    private List<Film> films = new ArrayList<>();

    @Builder.Default
    private List<Actor> actors = new ArrayList<>();

    /**
     * Create a Category with the specified id, name, and description.
     *
     * @param id the category identifier
     * @param name the category name
     * @param description the category description
     */
    public Category(final Long id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.films = new ArrayList<>();
        this.actors = new ArrayList<>();
    }


    /**
     * Creates a Category with the given name and description and initializes film and actor lists as empty.
     *
     * @param name the category name
     * @param description the category description
     */
    public Category(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.films = new ArrayList<>();
        this.actors = new ArrayList<>();
    }

}
