package com.esprit.models.products;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esprit.models.users.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Date orderDate;

    private String status;

    private Client client;

    private List<OrderItem> orderItems = new ArrayList<>();

    private int phoneNumber;

    private String address;

    /**
     * Create a new Order instance without an identifier.
     *
     * @param orderDate  the date when the order was placed
     * @param status     the order's current status (e.g., pending, completed)
     * @param client     the client who placed the order
     * @param phoneNumber the contact telephone number for the order
     * @param address    the delivery or billing address for the order
     */
    public Order(final Date orderDate, final String status, final Client client, final int phoneNumber,
            final String address) {
        this.orderDate = orderDate;
        this.status = status;
        this.client = client;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}
