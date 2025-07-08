package com.esprit.models.products;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a category of products.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Product management entity class for the RAKCHA application. Manages product
 * data and relationships with database persistence.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProductCategory {

    private Long id;

    private String categoryName;

    private String description;

    private List<Product> products = new ArrayList<>();

    /**
     * Constructor without id for creating new category instances.
     *
     * @param categoryName
     *                     The name of the category.
     * @param description
     *                     The description of the category.
     */
    public ProductCategory(final String categoryName, final String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
}
