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
     * Constructor with id, name, and description for database mapping.
     *
     * @param id
     *                    the id of the category
     * @param name
     *                    the name of the category
     * @param description
     *                    the description of the category
     */
    public Category(final Long id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.films = new ArrayList<>();
        this.actors = new ArrayList<>();
    }


    /**
     * Constructor without id for creating new category instances.
     *
     * @param name
     *                    the name of the category
     * @param description
     *                    the description of the category
     */
    public Category(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.films = new ArrayList<>();
        this.actors = new ArrayList<>();
    }

}

