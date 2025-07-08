package com.esprit.services.products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.products.Product;
import com.esprit.models.products.Review;
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
public class ReviewService implements IService<Review> {
    private static final Logger LOGGER = Logger.getLogger(ReviewService.class.getName());

    private final Connection connection;

    /**
     * Constructs a new ReviewService instance.
     * Initializes database connection.
     */
    public ReviewService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Review review) {
        final String req = "INSERT into reviews(user_id, rating, product_id) values (?, ?, ?)";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, review.getClient().getId());
            pst.setInt(2, review.getRating());
            pst.setLong(3, review.getProduct().getId());
            pst.executeUpdate();
            ReviewService.LOGGER.info("review added!");
        } catch (final SQLException e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Review> read() {
        final List<Review> reviewsList = new ArrayList<>();
        final String req = "SELECT * FROM reviews";
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Long userId = resultSet.getLong("user_id");
                final int rating = resultSet.getInt("rating");
                final Long productId = resultSet.getLong("product_id");
                final Client user = (Client) new UserService().getUserById(userId);
                final Product product = new ProductService().getProductById(productId);
                final Review review = Review.builder().id(resultSet.getLong("id")).client(user).rating(rating)
                        .product(product).build();
                reviewsList.add(review);
            }
        } catch (final SQLException e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return reviewsList;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Review review) {
        final String req = "UPDATE avis set note = ? where id=?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, review.getRating());
            pst.executeUpdate();
            ReviewService.LOGGER.info("review updated!");
        } catch (final SQLException e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Review review) {
        final String req = "DELETE from reviews where user_id = ? and product_id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setLong(1, review.getClient().getId());
            pst.setLong(2, review.getProduct().getId());
            pst.executeUpdate();
            ReviewService.LOGGER.info("review deleted!");
        } catch (final SQLException e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Retrieves the AverageRating value.
     *
     * @return the AverageRating value
     */
    public double getAverageRating(final Long productId) {
        final String req = "SELECT AVG(rating) AS averageRate FROM reviews WHERE product_id = ?";
        double averageRating = 0.0;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setLong(1, productId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            averageRating = resultSet.getDouble("averageRate");
        } catch (final Exception e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return averageRating;
    }

    /**
     * Retrieves the AverageRatingSorted value.
     *
     * @return the AverageRatingSorted value
     */
    public List<Review> getAverageRatingSorted() {
        final String req = "SELECT product_id, AVG(rating) AS averageRate FROM reviews GROUP BY product_id ORDER BY averageRate DESC";
        final List<Review> reviews = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reviews.add(Review.builder().client(null).rating((int) resultSet.getDouble("averageRate"))
                        .product(new ProductService().getProductById(resultSet.getLong("product_id"))).build());
            }
        } catch (final Exception e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return reviews;
    }

    /**
     * Performs ratingExists operation.
     *
     * @return the result of the operation
     */
    public Review ratingExists(final Long productId, final Long userId) {
        final String req = "SELECT * FROM reviews WHERE product_id = ? AND user_id = ?";
        Review rating = null;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setLong(1, productId);
            preparedStatement.setLong(2, userId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rating = Review.builder().id(resultSet.getLong("id"))
                        .client((Client) new UserService().getUserById(userId)).rating(resultSet.getInt("rating"))
                        .product(new ProductService().getProductById(productId)).build();
            }
        } catch (final Exception e) {
            ReviewService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return rating;
    }
}
