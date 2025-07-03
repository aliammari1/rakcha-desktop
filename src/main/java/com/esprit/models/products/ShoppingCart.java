package com.esprit.models.products;

import jakarta.persistence.*;

import com.esprit.models.users.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Is used to represent a shopping cart containing various products and users.
 */
@Entity
@Table(name = "shopping_cart")
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
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Constructor without id for creating new shopping cart instances.
     *
     * @param quantity
     *                 The quantity of the product in the cart.
     * @param product
     *                 The product associated with the cart.
     * @param user
     *                 The user associated with the cart.
     */
    public ShoppingCart(final int quantity, final Product product, final User user) {
        this.quantity = quantity;
        this.product = product;
        this.user = user;
    }
}
