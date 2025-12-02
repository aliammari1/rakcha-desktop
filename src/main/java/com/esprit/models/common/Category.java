package com.esprit.models.common;

import com.esprit.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class Category {

    private Long id;

    private String name;

    private String description;

    private CategoryType type;

    /**
     * Creates a Category with the given name and description and initializes film and actor lists as empty.
     *
     * @param name        the category name
     * @param description the category description
     * @param type        the category type
     */
    public Category(final String name, final String description, final CategoryType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }
}


