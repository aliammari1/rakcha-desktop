package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Comment;
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
public class CommentService implements IService<Comment> {
    private static final Logger LOGGER = Logger.getLogger(CommentService.class.getName());
    private final Connection connection;

    /**
     * Performs CommentService operation.
     *
     * @return the result of the operation
     */
    public CommentService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *            the entity to create
     */
    public void create(final Comment comment) {
        final String req = "INSERT into comments (client_id, comment_text, product_id) values (?, ?, ?)";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, comment.getClient().getId());
            pst.setString(2, comment.getCommentText());
            pst.setLong(3, comment.getProduct().getId());
            pst.executeUpdate();
            CommentService.LOGGER.info("Comment added!");
        } catch (final SQLException e) {
            CommentService.LOGGER.info("Error adding comment: " + e.getMessage());
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Comment> read() {
        final List<Comment> comments = new ArrayList<>();
        final String req = "SELECT * from comments";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ProductService productService = new ProductService();
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Comment comment = Comment.builder().id(rs.getLong("id"))
                        .client((Client) new UserService().getUserById(rs.getLong("client_id")))
                        .commentText(rs.getString("comment_text"))
                        .product(productService.getProductById(rs.getLong("product_id"))).build();
                comments.add(comment);
            }
        } catch (final SQLException e) {
            CommentService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return comments;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *            the entity to update
     */
    public void update(final Comment comment) {
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *            the ID of the entity to delete
     */
    public void delete(final Comment comment) {
    }

    /**
     * Performs readByClientId operation.
     *
     * @return the result of the operation
     */
    public Comment readByClientId(final int clientId) {
        Comment comment = null;
        final String req = "SELECT * FROM comments WHERE client_id = ? LIMIT 1";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, clientId);
            final ResultSet rs = pst.executeQuery();
            final ProductService productService = new ProductService();
            if (rs.next()) {
                comment = Comment.builder().id(rs.getLong("id"))
                        .client((Client) new UserService().getUserById(rs.getLong("client_id")))
                        .commentText(rs.getString("comment_text"))
                        .product(productService.getProductById(rs.getLong("product_id"))).build();
            }
        } catch (final SQLException e) {
            CommentService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return comment;
    }

    /**
     * Retrieves the CommentsByProductId value.
     *
     * @return the CommentsByProductId value
     */
    public List<Comment> getCommentsByProductId(final int productId) {
        final List<Comment> comments = new ArrayList<>();
        final String req = "SELECT * FROM comments c join products p on c.product_id = p.id WHERE c.product_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, productId);
            final ResultSet rs = pst.executeQuery();
            final ProductService productService = new ProductService();
            while (rs.next()) {
                final Comment comment = Comment.builder().id(rs.getLong("id"))
                        .client((Client) new UserService().getUserById(rs.getLong("client_id")))
                        .commentText(rs.getString("comment_text"))
                        .product(productService.getProductById(rs.getLong("product_id"))).build();
                comments.add(comment);
            }
        } catch (final SQLException e) {
            CommentService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return comments;
    }
}
