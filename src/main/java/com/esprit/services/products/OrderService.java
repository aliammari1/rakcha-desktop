package com.esprit.services.products;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Order;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class OrderService implements IService<Order> {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new OrderService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public OrderService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);
        tableCreator.createTableIfNotExists("orders", """
                    CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_date DATE NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'pending',
                        phone_number INT NOT NULL,
                        address VARCHAR(50) NOT NULL,
                        client_id BIGINT
                    )
                """);
    }


    /**
     * Inserts the given Order into the database's orders table.
     *
     * If the order's status is null, the status is stored as "in progress".
     *
     * @param order the Order to persist (must contain a client with a valid id)
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Order order) {
        final String req = "INSERT into orders(order_date, status, client_id, phone_number, address) values (?, ?, ?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setDate(1, (Date) order.getOrderDate());
            pst.setString(2, null != order.getStatus() ? order.getStatus() : "in progress");
            pst.setLong(3, order.getClient().getId());
            pst.setInt(4, order.getPhoneNumber());
            pst.setString(5, order.getAddress());
            pst.executeUpdate();
            OrderService.LOGGER.info("order created!");
        } catch (final SQLException e) {
            OrderService.LOGGER.info("order not created");
        }

    }


    /**
     * Insert the given Order into the database and return its generated primary key.
     *
     * If the order's status is null, the string "in progress" will be used before insertion.
     *
     * @param order the Order to persist
     * @return the generated order ID, or 0 if no generated key was returned
     * @throws SQLException if a database access error occurs
     */
    public Long createOrder(final Order order) throws SQLException {
        Long orderId = 0L;
        final String req = "INSERT into orders(order_date, status, client_id, phone_number, address) values (?, ?, ?, ?, ?)";
        final PreparedStatement pst = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        pst.setDate(1, (Date) order.getOrderDate());
        pst.setString(2, null != order.getStatus() ? order.getStatus() : "in progress");
        pst.setLong(3, order.getClient().getId());
        pst.setInt(4, order.getPhoneNumber());
        pst.setString(5, order.getAddress());
        pst.executeUpdate();
        final ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            orderId = rs.getLong(1);
        }

        return orderId;
    }


    /**
     * Retrieves all orders from the orders table, including each order's client and associated order items.
     *
     * @return a list of Order objects populated with id, orderDate, status, client, phoneNumber, address, and orderItems; returns an empty list if no orders are found or if an error occurs
     */
    public List<Order> read() {
        final OrderItemService orderItemService = new OrderItemService();
        final List<Order> orders = new ArrayList<>();
        final String req = "SELECT * from orders";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService us = new UserService();
            while (rs.next()) {
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getDate("order_date"))
                        .status(rs.getString("status")).client((Client) us.getUserById(rs.getLong("client_id")))
                        .phoneNumber(rs.getInt("phone_number")).address(rs.getString("address"))
                        .orderItems(orderItemService.readOrderItem(rs.getLong("id"))).build();
                orders.add(order);
            }

        } catch (final SQLException e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orders;
    }


    /**
     * Fetches all orders belonging to the currently associated client.
     *
     * @return a list of Order objects for the current client; an empty list if none are found or if a database error occurs
     */
    public List<Order> readClientOrders() {
        final OrderItemService orderItemService = new OrderItemService();
        final List<Order> orders = new ArrayList<>();
        final String req = "SELECT * from orders WHERE client_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService us = new UserService();
            while (rs.next()) {
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getDate("order_date"))
                        .status(rs.getString("status")).client((Client) us.getUserById(rs.getLong("client_id")))
                        .phoneNumber(rs.getInt("phone_number")).address(rs.getString("address"))
                        .orderItems(orderItemService.readOrderItem(rs.getLong("id"))).build();
                orders.add(order);
            }

        } catch (final SQLException e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orders;
    }


    /**
     * Update the database record for the given order using its `id`.
     *
     * Updates the order's order_date, status, phone_number, and address columns to match the provided Order.
     *
     * @param order the Order whose database row will be updated (identified by its `id`)
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Order order) {
        final String req = "UPDATE orders SET order_date = ?, status = ?, phone_number = ?, address = ? WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setDate(1, (Date) order.getOrderDate());
            pst.setString(2, order.getStatus());
            pst.setInt(3, order.getPhoneNumber());
            pst.setString(4, order.getAddress());
            pst.setLong(5, order.getId());
            pst.executeUpdate();
            OrderService.LOGGER.info("order updated!");
        } catch (final SQLException e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Delete the specified order from the database by its identifier.
     *
     * @param order the order to delete (its `id` is used to identify the row)
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Order order) {
        final String req = "DELETE from orders where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, order.getId());
            pst.executeUpdate();
            OrderService.LOGGER.info("order deleted!");
        } catch (final SQLException e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Retrieve an order by its database ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return the Order with the specified ID, or null if no matching order is found
     * @throws SQLException if a database access error occurs
     */
    public Order getOrderById(final int orderId) throws SQLException {
        final UserService usersService = new UserService();
        Order order = null;
        final String req = "SELECT * from orders WHERE id = ?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, orderId);
        final ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            order = Order.builder().id(rs.getLong("id")).orderDate(rs.getDate("order_date"))
                    .status(rs.getString("status")).client((Client) usersService.getUserById(rs.getLong("client_id")))
                    .phoneNumber(rs.getInt("phone_number")).address(rs.getString("address")).build();
        }

        return order;
    }


    /**
     * Retrieve all orders whose status is 'paid'.
     *
     * Each returned Order includes its associated client and order items where available.
     *
     * @return a list of Order objects with status 'paid', or an empty list if none are found
     */
    public List<Order> getPaidOrders() {
        final List<Order> paidOrders = new ArrayList<>();
        final String req = "SELECT * FROM orders WHERE status = 'paid'";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService usersService = new UserService();
            final OrderItemService orderItemService = new OrderItemService();
            while (rs.next()) {
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getDate("order_date"))
                        .status(rs.getString("status"))
                        .client((Client) usersService.getUserById(rs.getLong("client_id")))
                        .phoneNumber(rs.getInt("phone_number")).address(rs.getString("address"))
                        .orderItems(orderItemService.readOrderItem(rs.getLong("id"))).build();
                paidOrders.add(order);
            }

        } catch (final SQLException e) {
            OrderService.LOGGER.info("Error retrieving paid orders: " + e.getMessage());
        }

        return paidOrders;
    }


    /**
     * Retrieve the top three most purchased product IDs and their total purchase counts for orders with status "paid".
     *
     * @return a map from product ID to total units purchased for the top three products, ordered by decreasing count (may contain fewer than three entries)
     */
    public Map<Integer, Integer> getTop3PurchasedProducts() {
        final String req = "SELECT ci.id_produit, SUM(ci.quantity) AS purchaseCount FROM orderitem ci JOIN order c ON ci.idOrder = c.idOrder WHERE c.statu = 'paid' GROUP BY ci.id_produit ORDER BY purchaseCount DESC LIMIT 3";
        final Map<Integer, Integer> productPurchases = new HashMap<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int productId = resultSet.getInt("idProduct");
                final int purchaseCount = resultSet.getInt("purchaseCount");
                productPurchases.put(productId, purchaseCount);
            }

        } catch (final Exception e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return productPurchases;
    }


    /**
     * Placeholder for paginated retrieval of orders; not implemented.
     *
     * @throws UnsupportedOperationException always to indicate the method is not implemented
     */
    @Override
    public Page<Order> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
