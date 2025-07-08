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
     * Initializes database connection and user service.
     */
    public OrderService() {
        this.connection = DataSource.getInstance().getConnection();
    }

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
     * Performs createOrder operation.
     *
     * @return the result of the operation
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

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
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
     * Performs readClientOrders operation.
     *
     * @return the result of the operation
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
     * Retrieves the OrderById value.
     *
     * @return the OrderById value
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
     * Retrieves the PaidOrders value.
     *
     * @return the PaidOrders value
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
     * Retrieves the Top3PurchasedProducts value.
     *
     * @return the Top3PurchasedProducts value
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
}
