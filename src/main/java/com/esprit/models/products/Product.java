package com.esprit.models.products;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The `Product` class represents a product.
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
public class Product {

    private Long id;

    private String name;

    private int price;

    private String image;

    private String description;

    private List<ProductCategory> categories = new ArrayList<>();

    private int quantity;

    private List<Comment> comments = new ArrayList<>();

    private List<Review> reviews = new ArrayList<>();

    private List<ShoppingCart> shoppingCarts = new ArrayList<>();

    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Create a Product instance without an identifier.
     *
     * @param name        the product name
     * @param price       the product price in cents (or smallest currency unit)
     * @param image       the image URL or path for the product
     * @param description the product description
     * @param categories  the product categories; if `null`, an empty list is used
     * @param quantity    the available quantity
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
     * Create a Product instance containing only its identifier for use as a reference.
     *
     * @param productId the product identifier
     */
    public Product(final Long productId) {
        this.id = productId;
    }


    /**
     * Get the product's categories.
     *
     * @return the list of associated ProductCategory objects (may be empty)
     */
    public List<ProductCategory> getCategories() {
        return this.categories;
    }


    /**
     * Replace the product's category list.
     *
     * @param categories the new list of categories for the product; may be null
     */
    public void setCategories(final List<ProductCategory> categories) {
        this.categories = categories;
    }


    /**
     * Get the product's category names joined by ", ".
     *
     * If the product has no categories or the category list is null, returns null.
     *
     * @return a single string with category names separated by ", ", or null if there are no categories
     */
    public String getCategoryNames() {
        return this.categories != null && !this.categories.isEmpty()
                ? this.categories.stream().map(ProductCategory::getCategoryName).reduce((a, b) -> a + ", " + b)
                        .orElse(null)
                : null;
    }

}
