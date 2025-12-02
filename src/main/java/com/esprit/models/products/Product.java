package com.esprit.models.products;

import com.esprit.models.common.Category;
import com.esprit.models.common.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private String description;

    private String imageUrl;

    private Double price;

    private int stockQuantity;

    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * Get the product's categories.
     *
     * @return the list of associated ProductCategory objects (may be empty)
     */
    public List<Category> getCategories() {
        return this.categories;
    }

    /**
     * Replace the product's category list.
     *
     * @param categories the new list of categories for the product; may be null
     */
    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }

    /**
     * Get the product's category names joined by ", ".
     * <p>
     * If the product has no categories or the category list is null, returns null.
     *
     * @return a single string with category names separated by ", ", or null if
     * there are no categories
     */
    public String getCategoryNames() {
        return this.categories != null && !this.categories.isEmpty()
            ? this.categories.stream().map(Category::getName).reduce((a, b) -> a + ", " + b)
            .orElse(null)
            : null;
    }

    /**
     * Get the product stock quantity (convenience method).
     * @return the current stock quantity
     */
    public int getStock() {
        return this.stockQuantity;
    }

    /**
     * Set the product stock quantity (convenience method).
     * @param stock the stock quantity to set
     */
    public void setStock(int stock) {
        this.stockQuantity = stock;
    }

    /**
     * Get the product category as a string (convenience method).
     * @return the category names joined by ", " or null
     */
    public String getCategory() {
        return this.getCategoryNames();
    }

    /**
     * Get the product original price (convenience method for discount calculation).
     * For now, returns the current price. In a full system, would be a separate field.
     * @return the original price
     */
    public double getOriginalPrice() {
        // If there was an originalPrice field, return it
        // For now, return the current price (no discount by default)
        return this.price != null ? this.price : 0.0;
    }

    /**
     * Get the product image URL (convenience method).
     * @return the image URL
     */
    public String getImage() {
        return this.imageUrl;
    }

    /**
     * Set the product image URL (convenience method).
     * @param imageUrl the image URL to set
     */
    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Get the product name/title (alias for compatibility).
     * @return the product name
     */
    public String getNom() {
        return this.name;
    }

    /**
     * Set the product name/title (alias for compatibility).
     * @param nom the name to set
     */
    public void setNom(String nom) {
        this.name = nom;
    }

    /**
     * Get the product price (alias for compatibility).
     * @return the price
     */
    public Double getPrix() {
        return this.price;
    }

    /**
     * Set the product price (alias for compatibility).
     * @param prix the price to set
     */
    public void setPrix(Double prix) {
        this.price = prix;
    }

}


