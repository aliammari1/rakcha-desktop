package com.esprit.models.products;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Comment class represents a comment made by a client on a product.
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
public class Comment {

    private Long id;

    private Client client;

    private String commentText;

    private Product product;

    /**
     * Constructor without id for creating new comment instances.
     *
     * @param client
     *                    The client who made the comment.
     * @param commentText
     *                    The comment text.
     * @param product
     *                    The product associated with the comment.
     */
    public Comment(final Client client, final String commentText, final Product product) {
        this.client = client;
        this.commentText = commentText;
        this.product = product;
    }

}

