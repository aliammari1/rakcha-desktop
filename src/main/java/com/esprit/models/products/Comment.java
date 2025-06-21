package com.esprit.models.products;

import jakarta.persistence.*;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Comment class represents a comment made by a client on a product.
 */
@Entity(name = "ProductComment")
@Table(name = "product_comments")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
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
