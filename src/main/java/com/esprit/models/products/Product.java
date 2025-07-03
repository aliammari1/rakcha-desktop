package com.esprit.models.products;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The `Product` class represents a product.
 */
@Entity
@Table(name = "products")
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "image")
    private String image;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<ProductCategory> categories = new ArrayList<>();

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShoppingCart> shoppingCarts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Constructor without id for creating new product instances.
     *
     * @param name
     *                    the name of the product
     * @param price
     *                    the price of the product
     * @param image
     *                    the image URL of the product
     * @param description
     *                    the description of the product
     * @param categories
     *                    the categories of the product
     * @param quantity
     *                    the quantity of the product
     */
    public Product(final String name, final int price, final String image, final String description,
            final List<ProductCategory> categories, final int quantity) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.quantity = quantity;
    }

    /**
     * Constructor with only id for creating product references.
     *
     * @param productId
     *                  the ID of the product
     */
    public Product(final Long productId) {
        this.id = productId;
    }

    /**
     * Returns the categories of the product.
     *
     * @return the categories of the product
     */
    public List<ProductCategory> getCategories() {
        return this.categories;
    }

    /**
     * Sets the categories of the product.
     *
     * @param categories
     *                   the categories of the product
     */
    public void setCategories(final List<ProductCategory> categories) {
        this.categories = categories;
    }

    /**
     * Returns the name of the categories of the product, concatenated as a string.
     *
     * @return the name of the categories of the product
     */
    public String getCategoryNames() {
        return this.categories != null && !this.categories.isEmpty()
                ? this.categories.stream().map(ProductCategory::getCategoryName).reduce((a, b) -> a + ", " + b)
                        .orElse(null)
                : null;
    }
}
