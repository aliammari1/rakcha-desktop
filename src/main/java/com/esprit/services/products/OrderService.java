package com.esprit.services.products;

import com.esprit.enums.OrderStatus;
import com.esprit.exceptions.InsufficientStockException;
import com.esprit.exceptions.InvalidStatusTransitionException;
import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
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
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "order_date", "status", "phone_number", "address"
    };
    private final Connection connection;

    /**
     * Constructs a new OrderService instance.
     * Initializes database connection and creates tables if they don't exist.
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
        final String req = "INSERT into orders(order_date, status, user_id, phone_number, shipping_address, total_amount) values (?, ?, ?, ?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrderDate()));
            pst.setString(2, null != order.getStatus() ? order.getStatus() : "PENDING");
            pst.setLong(3, order.getClient().getId());
            pst.setString(4, order.getPhoneNumber());
            pst.setString(5, order.getShippingAddress());
            pst.setDouble(6, order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
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
        final String req = "INSERT into orders(order_date, status, user_id, phone_number, shipping_address, total_amount) values (?, ?, ?, ?, ?, ?)";
        final PreparedStatement pst = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        pst.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrderDate()));
        pst.setString(2, null != order.getStatus() ? order.getStatus() : "PENDING");
        pst.setLong(3, order.getClient().getId());
        pst.setString(4, order.getPhoneNumber());
        pst.setString(5, order.getShippingAddress());
        pst.setDouble(6, order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
        pst.executeUpdate();
        final ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            orderId = rs.getLong(1);
        }
        return orderId;
    }

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
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getTimestamp("order_date").toLocalDateTime())
                    .status(rs.getString("status")).client((Client) us.getUserById(rs.getLong("user_id")))
                    .phoneNumber(rs.getString("phone_number")).shippingAddress(rs.getString("shipping_address"))
                    .totalAmount(rs.getDouble("total_amount"))
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
        final String req = "SELECT * from orders WHERE user_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService us = new UserService();
            while (rs.next()) {
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getTimestamp("order_date").toLocalDateTime())
                    .status(rs.getString("status")).client((Client) us.getUserById(rs.getLong("user_id")))
                    .phoneNumber(rs.getString("phone_number")).shippingAddress(rs.getString("shipping_address"))
                    .totalAmount(rs.getDouble("total_amount"))
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
        final String req = "UPDATE orders SET order_date = ?, status = ?, phone_number = ?, shipping_address = ?, total_amount = ? WHERE id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrderDate()));
            pst.setString(2, order.getStatus());
            pst.setString(3, order.getPhoneNumber());
            pst.setString(4, order.getShippingAddress());
            pst.setDouble(5, order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
            pst.setLong(6, order.getId());
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
    public Order getOrderById(final Long orderId) {
        final UserService usersService = new UserService();
        Order order = null;
        final String req = "SELECT * from orders WHERE id = ?";
        try {
            final PreparedStatement ps = this.connection.prepareStatement(req);
            ps.setLong(1, orderId);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                order = Order.builder().id(rs.getLong("id")).orderDate(rs.getTimestamp("order_date").toLocalDateTime())
                    .status(rs.getString("status"))
                    .client((Client) usersService.getUserById(rs.getLong("user_id")))
                    .phoneNumber(rs.getString("phone_number")).shippingAddress(rs.getString("shipping_address"))
                    .totalAmount(rs.getDouble("total_amount")).build();
            }
            return order;
        } catch (final SQLException e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Retrieves the PaidOrders value.
     *
     * @return the PaidOrders value
     */
    public List<Order> getPaidOrders() {
        final List<Order> paidOrders = new ArrayList<>();
        final String req = "SELECT * FROM orders WHERE status = 'COMPLETED'";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService usersService = new UserService();
            final OrderItemService orderItemService = new OrderItemService();
            while (rs.next()) {
                final Order order = Order.builder().id(rs.getLong("id")).orderDate(rs.getTimestamp("order_date").toLocalDateTime())
                    .status(rs.getString("status"))
                    .client((Client) usersService.getUserById(rs.getLong("user_id")))
                    .phoneNumber(rs.getString("phone_number")).shippingAddress(rs.getString("shipping_address"))
                    .totalAmount(rs.getDouble("total_amount"))
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
        final String req = "SELECT oi.product_id, SUM(oi.quantity) AS purchaseCount FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.status = 'COMPLETED' GROUP BY oi.product_id ORDER BY purchaseCount DESC LIMIT 3";
        final Map<Integer, Integer> productPurchases = new HashMap<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int productId = resultSet.getInt("product_id");
                final int purchaseCount = resultSet.getInt("purchaseCount");
                productPurchases.put(productId, purchaseCount);
            }
        } catch (final Exception e) {
            OrderService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return productPurchases;
    }

    @Override
    public Page<Order> read(PageRequest pageRequest) {
        final List<Order> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM orders";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                 ResultSet rs = stmt.executeQuery()) {
                final UserService userService = new UserService();
                final OrderItemService orderItemService = new OrderItemService();

                while (rs.next()) {
                    try {
                        final Order order = Order.builder()
                            .id(rs.getLong("id"))
                            .orderDate(rs.getTimestamp("order_date").toLocalDateTime())
                            .status(rs.getString("status"))
                            .client((Client) userService.getUserById(rs.getLong("user_id")))
                            .phoneNumber(rs.getString("phone_number"))
                            .shippingAddress(rs.getString("shipping_address"))
                            .totalAmount(rs.getDouble("total_amount"))
                            .orderItems(orderItemService.readOrderItem(rs.getLong("id")))
                            .build();
                        content.add(order);
                    } catch (Exception e) {
                        log.warn("Error loading order relationships for order ID: " + rs.getLong("id"), e);
                    }
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated orders: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Updates the status of an order with workflow validation and automatic stock
     * management.
     *
     * @param orderId   the order ID
     * @param newStatus the new status to set
     * @throws InvalidStatusTransitionException if the transition is invalid
     * @throws InsufficientStockException       if stock is insufficient
     */
    public void updateOrderStatus(Long orderId, OrderStatus newStatus)
        throws InvalidStatusTransitionException, InsufficientStockException {

        // Get current order
        Order order;
        order = getOrderById(orderId);


        if (order == null) {
            throw new InvalidStatusTransitionException("Order not found with ID: " + orderId);
        }

        // Get current status
        OrderStatus currentStatus = OrderStatus.fromValue(order.getStatus());

        // Validate transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }

        // If transitioning TO paid, deduct stock
        if (newStatus == OrderStatus.PAID && currentStatus != OrderStatus.PAID) {
            deductStockForOrder(order);
        }

        // If transitioning FROM paid TO cancelled, restore stock
        if (currentStatus == OrderStatus.PAID && newStatus == OrderStatus.CANCELLED) {
            restoreStockForOrder(order);
        }

        // Update status in database
        String req = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, newStatus.getValue());
            pst.setLong(2, orderId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info(String.format("Order %d status updated from %s to %s",
                    orderId, currentStatus.getDisplayName(), newStatus.getDisplayName()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order status", e);
            throw new InvalidStatusTransitionException("Database error while updating order status");
        }
    }

    /**
     * Cancels an order and restores stock if it was paid.
     *
     * @param orderId the order ID
     * @throws InvalidStatusTransitionException if order cannot be cancelled
     */
    public void cancelOrder(Long orderId) throws InvalidStatusTransitionException {
        Order order;
        order = getOrderById(orderId);
        if (order == null) {
            throw new InvalidStatusTransitionException("Order not found with ID: " + orderId);
        }

        OrderStatus currentStatus = OrderStatus.fromValue(order.getStatus());

        // Validate that order can be cancelled
        if (!currentStatus.canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidStatusTransitionException(
                String.format("Cannot cancel order in %s status", currentStatus.getDisplayName()));
        }

        // If order was paid, restore stock
        if (currentStatus == OrderStatus.PAID) {
            restoreStockForOrder(order);
        }

        // Update status to cancelled
        String req = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, OrderStatus.CANCELLED.getValue());
            pst.setLong(2, orderId);
            pst.executeUpdate();
            LOGGER.info(String.format("Order %d cancelled. Stock restored if applicable.", orderId));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error cancelling order", e);
            throw new InvalidStatusTransitionException("Database error while cancelling order");
        }
    }

    /**
     * Deducts stock for all items in an order.
     *
     * @param order the order
     * @throws InsufficientStockException if any product has insufficient stock
     */
    private void deductStockForOrder(Order order) throws InsufficientStockException {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            LOGGER.warning("Order " + order.getId() + " has no items to deduct stock for");
            return;
        }

        ProductService productService = new ProductService();
        OrderItemService orderItemService = new OrderItemService();

        // Get order items if not already loaded
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty() && order.getId() != null) {
            orderItems = orderItemService.readOrderItem(order.getId());
        }

        // Validate stock for all items first (fail fast)
        for (OrderItem item : orderItems) {
            if (!productService.hasStock(item.getProduct().getId(), item.getQuantity())) {
                int available = productService.getAvailableStock(item.getProduct().getId());
                throw new InsufficientStockException(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    available);
            }
        }

        // Deduct stock for all items
        for (OrderItem item : orderItems) {
            productService.reduceStock(item.getProduct().getId(), item.getQuantity());
        }

        LOGGER.info(String.format("Stock deducted for order %d (%d items)",
            order.getId(), orderItems.size()));
    }

    /**
     * Restores stock for all items in an order.
     *
     * @param order the order
     */
    private void restoreStockForOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            LOGGER.warning("Order " + order.getId() + " has no items to restore stock for");
            return;
        }

        ProductService productService = new ProductService();
        OrderItemService orderItemService = new OrderItemService();

        // Get order items if not already loaded
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty() && order.getId() != null) {
            orderItems = orderItemService.readOrderItem(order.getId());
        }

        // Restore stock for all items
        for (OrderItem item : orderItems) {
            productService.restoreStock(item.getProduct().getId(), item.getQuantity());
        }

        LOGGER.info(String.format("Stock restored for order %d (%d items)",
            order.getId(), orderItems.size()));
    }

    @Override
    /**
     * Counts the total number of orders in the database.
     *
     * @return the total count of orders
     */
    public int count() {
        String query = "SELECT COUNT(*) FROM orders";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting orders", e);
        }
        return 0;
    }

    @Override
    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order with the specified ID, or null if not found
     */
    public Order getById(final Long id) {
        return getOrderById(id);
    }

    @Override
    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     */
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = buildOrderFromResultSet(rs);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all orders", e);
        }
        return orders;
    }

    @Override
    /**
     * Searches for orders by address, phone number, or status.
     *
     * @param query the search query
     * @return a list of orders matching the search query
     */
    public List<Order> search(final String query) {
        List<Order> orders = new ArrayList<>();
        final String req = "SELECT * FROM orders WHERE address LIKE ? OR phone_number LIKE ? OR status LIKE ? ORDER BY id DESC";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Order order = buildOrderFromResultSet(rs);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching orders", e);
        }
        return orders;
    }

    @Override
    /**
     * Checks if an order exists by its ID.
     *
     * @param id the ID of the order to check
     * @return true if the order exists, false otherwise
     */
    public boolean exists(final Long id) {
        return getOrderById(id) != null;
    }

    /**
     * Helper method to build an Order from a ResultSet.
     *
     * @param rs the ResultSet containing order data
     * @return the constructed Order object
     * @throws SQLException if a database error occurs
     */
    private Order buildOrderFromResultSet(ResultSet rs) throws SQLException {
        UserService usersService = new UserService();
        return Order.builder()
            .id(rs.getLong("id"))
            .orderDate(rs.getTimestamp("order_date") != null ? rs.getTimestamp("order_date").toLocalDateTime() : null)
            .status(rs.getString("status"))
            .client((Client) usersService.getUserById(rs.getLong("user_id")))
            .phoneNumber(rs.getString("phone_number"))
            .shippingAddress(rs.getString("shipping_address"))
            .totalAmount(rs.getDouble("total_amount"))
            .build();
    }

    /**
     * Get all orders by a specific user ID.
     *
     * @param userId the ID of the user
     * @return list of orders for the specified user
     */
    public List<Order> getOrdersByUser(final Long userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = buildOrderFromResultSet(rs);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving orders for user: " + userId, e);
        }
        return orders;
    }

    /**
     * Get the total spending for a user in a specific date range.
     *
     * @param userId    the user ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return the total spending amount
     */
    public double getMonthlySpending(Long userId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        double totalSpending = 0.0;
        String query = "SELECT COALESCE(SUM(total_amount), 0) as total FROM orders WHERE user_id = ? AND order_date BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSpending = rs.getDouble("total");
            }
        } catch (SQLException e) {
            log.error("Error calculating monthly spending for user: " + userId, e);
        }

        return totalSpending;
    }
}

