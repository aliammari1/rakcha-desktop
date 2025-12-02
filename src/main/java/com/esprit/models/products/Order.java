package com.esprit.models.products;

import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Order class represents an order made by a client.
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

public class Order {

    private Long id;

    private Client client;

    private User user;

    private java.time.LocalDateTime orderDate;

    private String status;

    private String shippingAddress;

    private String deliveryAddress;

    private String city;

    private String postalCode;

    private String country;

    private String phoneNumber;

    private String paymentMethod;

    private Double totalAmount;

    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Create a new Order instance without an identifier.
     *
     * @param client          the client who placed the order
     * @param orderDate       the date when the order was placed
     * @param status          the order's current status (e.g., pending, completed)
     * @param shippingAddress the delivery or billing address for the order
     * @param phoneNumber     the contact telephone number for the order
     * @param totalAmount     the total amount of the order
     */
    public Order(final Client client, final java.time.LocalDateTime orderDate, final String status,
                 final String shippingAddress, final String phoneNumber, final Double totalAmount) {
        this.client = client;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
        this.orderItems = new ArrayList<>();
    }

    /**
     * Legacy constructor with Date type.
     *
     * @deprecated Use LocalDateTime constructor instead
     */
    @Deprecated(forRemoval = true)
    public Order(final Date orderDate, final String status, final Client client, final String phoneNumber,
                 final String shippingAddress, final float totalAmount) {
        this.orderDate = new java.sql.Timestamp(orderDate.getTime()).toLocalDateTime();
        this.status = status;
        this.client = client;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.totalAmount = Double.valueOf(totalAmount);
        this.orderItems = new ArrayList<>();
    }

    /**
     * Convenience method to get created date (alias for orderDate).
     * @return the order date
     */
    public java.time.LocalDateTime getCreatedAt() {
        return this.orderDate;
    }

    /**
     * Convenience method to set created date (alias for orderDate).
     * @param createdAt the order date
     */
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.orderDate = createdAt;
    }

    /**
     * Convenience method to get items (alias for orderItems).
     * @return the order items
     */
    public List<OrderItem> getItems() {
        return this.orderItems;
    }

    /**
     * Convenience method to set items (alias for orderItems).
     * @param items the order items
     */
    public void setItems(List<OrderItem> items) {
        this.orderItems = items;
    }

    /**
     * Convenience method to set total amount (alias for totalAmount).
     * @param total the total amount
     */
    public void setTotal(Double total) {
        this.totalAmount = total;
    }

    /**
     * Convenience method to set total amount.
     * @param totalAmount the total amount
     */
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Convenience method to get total amount.
     * @return the total amount
     */
    public Double getTotal() {
        return this.totalAmount;
    }

    /**
     * Convenience method to get payment method.
     * Currently returns a default value based on order status.
     * Can be extended to include actual payment method field if needed.
     * @return the payment method
     */
    public String getPaymentMethod() {
        return "Credit Card";
    }

    /**
     * Convenience method to set user (alias for client).
     * @param user the user who placed the order
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Convenience method to get user (alias for client).
     * @return the user who placed the order
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Convenience method to set delivery address.
     * @param deliveryAddress the delivery address
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Convenience method to get delivery address.
     * @return the delivery address
     */
    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    /**
     * Convenience method to set city.
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Convenience method to get city.
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Convenience method to set postal code.
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Convenience method to get postal code.
     * @return the postal code
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /**
     * Convenience method to set country.
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Convenience method to get country.
     * @return the country
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * Convenience method to set payment method.
     * @param paymentMethod the payment method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}


