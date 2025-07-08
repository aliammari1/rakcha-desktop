package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Product;
import com.esprit.services.IService;
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
public class OrderItemService implements IService<OrderItem> {
    private static final Logger LOGGER = Logger.getLogger(OrderItemService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new OrderItemService instance.
     * Initializes database connection.
     */
    public OrderItemService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final OrderItem orderItem) {
        final String req = "INSERT into order_items(product_id, quantity, order_id) values (?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, orderItem.getProduct().getId());
            pst.setInt(2, orderItem.getQuantity());
            pst.setLong(3, orderItem.getOrder().getId());
            pst.executeUpdate();
            OrderItemService.LOGGER.info("order item created!");
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<OrderItem> read() {
        final List<OrderItem> orderItems = new ArrayList<>();
        final String req = "SELECT * from order_items";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                        .product(ps.getProductById(rs.getLong("product_id")))
                        .order(cs.getOrderById(rs.getInt("order_id"))).build();
                orderItems.add(orderItem);
            }
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return orderItems;
    }

    /**
     * Performs readOrderItem operation.
     *
     * @return the result of the operation
     */
    public List<OrderItem> readOrderItem(final Long orderId) {
        final List<OrderItem> orderItems = new ArrayList<>();
        final String req = "SELECT * FROM order_items where order_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, orderId);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                        .product(ps.getProductById(rs.getLong("product_id")))
                        .order(cs.getOrderById(rs.getInt("order_id"))).build();
                orderItems.add(orderItem);
            }
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return orderItems;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final OrderItem orderItem) {
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final OrderItem orderItem) {
    }

    /**
     * Retrieves the OrderItemsByOrder value.
     *
     * @return the OrderItemsByOrder value
     */
    public List<OrderItem> getOrderItemsByOrder(final int orderId) {
        final List<OrderItem> orderItems = new ArrayList<>();
        final String req = "SELECT * FROM order_items WHERE order_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, orderId);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                        .product(ps.getProductById(rs.getLong("product_id")))
                        .order(cs.getOrderById(rs.getInt("order_id"))).build();
                orderItems.add(orderItem);
            }
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return orderItems;
    }

    /**
     * Retrieves the TotalQuantityByCategoryAndDate value.
     *
     * @return the TotalQuantityByCategoryAndDate value
     */
    public int getTotalQuantityByCategoryAndDate(final String categoryName, final String formattedDate) {
        int totalQuantity = 0;
        final String req = """
                SELECT SUM(oi.quantity) AS totalQuantity \
                FROM order_items oi \
                JOIN products p ON oi.product_id = p.id \
                JOIN product_category pc ON p.id = pc.product_id \
                JOIN product_categories cat ON pc.category_id = cat.id \
                JOIN orders o ON oi.order_id = o.id \
                WHERE cat.category_name = ? AND DATE(o.order_date) = ?\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, categoryName);
            pst.setString(2, formattedDate);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                totalQuantity = rs.getInt("totalQuantity");
            }
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return totalQuantity;
    }

    /**
     * Retrieves the ItemsByOrder value.
     *
     * @return the ItemsByOrder value
     */
    public List<OrderItem> getItemsByOrder(final int orderId) {
        final List<OrderItem> orderItems = new ArrayList<>();
        final String req = "SELECT * FROM order_items WHERE order_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, orderId);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final Product product = ps.getProductById(rs.getLong("product_id"));
                final Order order = cs.getOrderById(rs.getInt("order_id"));
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                        .product(product).order(order).build();
                orderItems.add(orderItem);
            }
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return orderItems;
    }

    /**
     * Retrieves the AverageRatingSorted value.
     *
     * @return the AverageRatingSorted value
     */
    public List<OrderItem> getAverageRatingSorted() {
        final String req = "SELECT product_id, quantity, o.id as order_id, oi.id FROM order_items oi join orders o on oi.order_id = o.id join products p on oi.product_id = p.id WHERE status = 'paid' GROUP BY product_id";
        final List<OrderItem> averageRated = new ArrayList<>();
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                        .product(ps.getProductById(rs.getLong("product_id")))
                        .order(cs.getOrderById(rs.getInt("order_id"))).build();
                averageRated.add(orderItem);
            }
        } catch (final Exception e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return averageRated.stream().sorted((OrderItem c1, OrderItem c2) -> c2.getQuantity() - c1.getQuantity())
                .collect(Collectors.toList());
    }
}
