package com.esprit.services.products;

import com.esprit.models.products.Order;
import com.esprit.models.products.OrderItem;
import com.esprit.models.products.Product;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "order_id", "product_id", "quantity"
    };
    private final Connection connection;

    /**
     * Constructs a new OrderItemService instance.
     * Initializes database connection and creates tables if they don't exist.
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
        final String req = "INSERT into order_items(product_id, quantity, order_id, unit_price) values (?, ?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, orderItem.getProduct().getId());
            pst.setInt(2, orderItem.getQuantity());
            pst.setLong(3, orderItem.getOrder().getId());
            pst.setDouble(4, orderItem.getProduct().getPrice());
            pst.executeUpdate();
            OrderItemService.LOGGER.info("order item created!");
        } catch (final SQLException e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

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
                    .order(cs.getOrderById(rs.getLong("order_id"))).build();
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
                    .order(cs.getOrderById(rs.getLong("order_id"))).build();
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

    @Override
    public boolean exists(Long id) {
        String query = "SELECT COUNT(*) FROM order_items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking order item existence: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Retrieves the OrderItemsByOrder value.

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
                    .order(cs.getOrderById(rs.getLong("order_id"))).build();
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
            JOIN categories c ON p.category_id = c.id \
            JOIN orders o ON oi.order_id = o.id \
            WHERE c.name = ? AND DATE(o.order_date) = ?\
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
                final Order order = cs.getOrderById(rs.getLong("order_id"));
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
        final String req = "SELECT oi.product_id, SUM(oi.quantity) as quantity, o.id as order_id, oi.id FROM order_items oi join orders o on oi.order_id = o.id join products p on oi.product_id = p.id WHERE o.status = 'COMPLETED' GROUP BY oi.product_id, oi.id, o.id ORDER BY quantity DESC";
        final List<OrderItem> averageRated = new ArrayList<>();
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final ProductService ps = new ProductService();
            final OrderService cs = new OrderService();
            while (rs.next()) {
                final OrderItem orderItem = OrderItem.builder().id(rs.getLong("id")).quantity(rs.getInt("quantity"))
                    .product(ps.getProductById(rs.getLong("product_id")))
                    .order(cs.getOrderById(rs.getLong("order_id"))).build();
                averageRated.add(orderItem);
            }
        } catch (final Exception e) {
            OrderItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return averageRated.stream().sorted((OrderItem c1, OrderItem c2) -> c2.getQuantity() - c1.getQuantity())
            .collect(Collectors.toList());
    }

    @Override
    /**
     * Retrieves all order items from the database.
     *
     * @return a list of all order items
     */
    public List<OrderItem> getAll() {
        final List<OrderItem> items = new ArrayList<>();
        final String query = "SELECT * FROM order_items ORDER BY id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                final ProductService productService = new ProductService();
                while (rs.next()) {
                    try {
                        items.add(OrderItem.builder()
                            .id(rs.getLong("id"))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .quantity(rs.getInt("quantity"))
                            .build());
                    } catch (Exception e) {
                        log.warn("Error loading order item for ID: " + rs.getLong("id"), e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all order items: {}", e.getMessage(), e);
        }
        return items;
    }

    @Override
    /**
     * Retrieves an order item by its ID.
     *
     * @param id the ID of the order item
     * @return the order item if found, null otherwise
     */
    public OrderItem getById(Long id) {
        final String query = "SELECT * FROM order_items WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    final ProductService productService = new ProductService();
                    return OrderItem.builder()
                        .id(rs.getLong("id"))
                        .product(productService.getProductById(rs.getLong("product_id")))
                        .quantity(rs.getInt("quantity"))
                        .build();
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving order item by id: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    /**
     * Searches for order items based on product ID or quantity.
     *
     * @param query the search query
     * @return a list of order items matching the search criteria
     */
    public List<OrderItem> search(String query) {
        final List<OrderItem> orderItems = new ArrayList<>();
        final String sql = "SELECT oi.* FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE p.name LIKE ? OR p.description LIKE ? ORDER BY oi.id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                final ProductService productService = new ProductService();
                final OrderService orderService = new OrderService();
                while (rs.next()) {
                    try {
                        orderItems.add(OrderItem.builder()
                            .id(rs.getLong("id"))
                            .order(orderService.getOrderById(rs.getLong("order_id")))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .quantity(rs.getInt("quantity"))
                            .build());
                    } catch (Exception e) {
                        log.warn("Error loading order item for ID: " + rs.getLong("id"), e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error searching order items: {}", e.getMessage(), e);
        }
        return orderItems;
    }

    @Override
    public Page<OrderItem> read(PageRequest pageRequest) {
        final List<OrderItem> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM order_items";

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
                final ProductService productService = new ProductService();

                while (rs.next()) {
                    try {
                        content.add(OrderItem.builder()
                            .id(rs.getLong("id"))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .quantity(rs.getInt("quantity"))
                            .build());
                    } catch (Exception e) {
                        log.warn("Error loading order item for ID: " + rs.getLong("id"), e);
                    }
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated order items: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    @Override
    public int count() {
        final String req = "SELECT COUNT(*) as count FROM order_items";
        try {
            final Statement st = this.connection.createStatement();
            final ResultSet rs = st.executeQuery(req);
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (final SQLException e) {
            log.error("Error counting order items: {}", e.getMessage(), e);
        }
        return 0;
    }
}
