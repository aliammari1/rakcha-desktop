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
     * Create a product category with the given name and description.
     *
     * <p>The new instance will have its `id` left unset (null) and `products` left as the class default (an empty list).</p>
     *
     * @param categoryName the category's name
     * @param description  a short description of the category
     */
    public ProductCategory(final String categoryName, final String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

}
