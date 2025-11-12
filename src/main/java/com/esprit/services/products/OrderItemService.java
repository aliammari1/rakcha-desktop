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
public class OrderItemService implements IService<OrderItem> {
    private static final Logger LOGGER = Logger.getLogger(OrderItemService.class.getName());
    private final Connection connection;

    /**
     * Initialize the service by obtaining a database connection and ensuring the `order_items` table exists.
     *
     * The constructor obtains a JDBC Connection from the shared DataSource and creates the `order_items`
     * table with columns `id`, `product_id`, `quantity`, and `order_id` if it does not already exist.
     */
    public OrderItemService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);
        tableCreator.createTableIfNotExists("order_items", """
                    CREATE TABLE order_items (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        product_id BIGINT,
                        quantity INT NOT NULL,
                        order_id BIGINT
                    )
                """);
    }


    /**
     * Insert the given OrderItem into the order_items database table.
     *
     * The method persists the order item's product id, quantity, and order id as a new row.
     *
     * @param orderItem the OrderItem to persist; its product and order must have valid ids that will be stored
     */
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
        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Retrieve all order items from the database.
     *
     * Each returned OrderItem has its product and order populated.
     *
     * @return a list of OrderItem objects from the order_items table; an empty list if no rows are found or an error occurs
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

        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orderItems;
    }


    /**
     * Retrieve order items associated with a specific order.
     *
     * @param orderId the identifier of the order whose items should be returned
     * @return a list of OrderItem objects for the given order id; returns an empty list if no items are found or an SQL error occurs
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

        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orderItems;
    }


    /**
     * Update the database row for the provided OrderItem.
     *
     * @param orderItem the OrderItem containing updated values; its `id` is used to locate the row to update
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final OrderItem orderItem) {
    }


    /**
     * Removes the given OrderItem from persistent storage.
     *
     * @param orderItem the OrderItem whose corresponding database record should be deleted; must have a valid id
     */
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
     * Retrieves all OrderItem entries associated with the specified order ID.
     *
     * @param orderId the identifier of the order to fetch items for
     * @return a list of OrderItem objects belonging to the given order, or an empty list if none are found
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

        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orderItems;
    }


    /**
     * Compute the total quantity of items sold for a product category on a specific date.
     *
     * @param categoryName  the product category name to filter by
     * @param formattedDate the date to filter orders by, formatted as "YYYY-MM-DD"
     * @return the sum of `quantity` for matching order items, or `0` if no matching rows are found or an error occurs
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

        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return totalQuantity;
    }


    /**
         * Retrieve order items belonging to the specified order.
         *
         * @param orderId the identifier of the order whose items should be fetched
         * @return a list of OrderItem objects for the specified order; an empty list if no items are found
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

        }
 catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return orderItems;
    }


    /**
     * Retrieves order items associated with orders with status "paid", with each item's product and order populated, sorted by quantity in descending order.
     *
     * @return a list of OrderItem objects with product and order set, sorted by quantity (highest first); empty list if no matching items are found
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

        }
 catch (final Exception e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return averageRated.stream().sorted((OrderItem c1, OrderItem c2) -> c2.getQuantity() - c1.getQuantity())
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a page of OrderItem records according to the provided paging and sorting parameters.
     *
     * @param pageRequest the paging and sorting parameters that specify page number, size, and sort order
     * @return a Page containing OrderItem objects for the requested page
     * @throws UnsupportedOperationException if the method is not implemented
     */
    @Override
    public Page<OrderItem> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
