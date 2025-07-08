package com.esprit.models.products;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Review class represents a user's review of a product.
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
public class Review {

    private Long id;

    private Client client;

    private int rating;

    private Product product;

    /**
     * Constructor without id for creating new review instances.
     *
     * @param client
     *                the client who gave the review
     * @param rating
     *                the rating given by the client
     * @param product
     *                the product associated with the review
     */
    public Review(final Client client, final int rating, final Product product) {
        this.client = client;
        this.rating = rating;
        this.product = product;
    }
}
