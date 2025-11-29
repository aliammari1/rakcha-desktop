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
     * Create a Review for a given client, rating, and product without setting an id.
     *
     * @param client  the client who submitted the review
     * @param rating  the rating value assigned by the client
     * @param product the product being reviewed
     */
    public Review(final Client client, final int rating, final Product product) {
        this.client = client;
        this.rating = rating;
        this.product = product;
    }

}
