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
public class CommentService implements IService<Comment> {
    private static final Logger LOGGER = Logger.getLogger(CommentService.class.getName());
    private final Connection connection;

    /**
     * Initializes the CommentService by obtaining a JDBC connection and ensuring the comments table exists.
     *
     * The ensured table has columns: id (BIGINT primary key auto-increment), client_id (BIGINT not null),
     * comment_text (TEXT not null), product_id (BIGINT not null), and created_at (TIMESTAMP default current timestamp).
     */
    public CommentService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create comments table
            String createCommentsTable = """
                    CREATE TABLE comments (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        client_id BIGINT NOT NULL,
                        comment_text TEXT NOT NULL,
                        product_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            tableCreator.createTableIfNotExists("comments", createCommentsTable);

        } catch (Exception e) {
            log.error("Error creating tables for CommentService", e);
        }

    }


    /**
     * Inserts the provided Comment into the comments table, persisting its client id, comment text, and product id.
     *
     * @param comment the Comment to persist; its client and product must have valid ids
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
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


    /**
     * Retrieve all comments from the data store and return them as Comment objects.
     *
     * Each returned Comment includes its associated client and product resolved via the user and product services.
     *
     * @return a list of all comments; the list is empty if no comments are found or an error occurs
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


    /**
     * Placeholder for updating an existing comment; currently not implemented.
     *
     * @param comment the Comment to update (ignored)
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Comment comment) {
    }


    /**
     * Removes the specified comment from persistent storage.
     *
     * @param comment the Comment whose record should be deleted (identified by its id)
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Comment comment) {
    }


    /**
     * Retrieves the first comment found for the given client ID.
     *
     * @param clientId the client's database identifier
     * @return the first matching Comment, or null if none found
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
     * Retrieves comments by product ID.
     *
     * @param productId the ID of the product to get comments for
     * @return list of comments for the specified product
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


    /**
     * Retrieve a page of comments for the given paging request.
     *
     * @param pageRequest paging and sorting parameters that specify which page of comments to return
     * @return a Page of Comment representing the requested page of results
     * @throws UnsupportedOperationException always; this method is not implemented
     */
    @Override
    public Page<Comment> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
