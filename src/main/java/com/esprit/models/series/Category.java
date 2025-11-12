package com.esprit.models.series;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Series category entity class for the RAKCHA application. Represents series
 * category data with Hibernate/JPA annotations for database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    private Long id;

    private String name;

    private String description;

    private List<Series> seriesList;

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
    }

}

