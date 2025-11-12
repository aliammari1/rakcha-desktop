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
public class ReviewService implements IService<Review> {
    private static final Logger LOGGER = Logger.getLogger(ReviewService.class.getName());

    private final Connection connection;

    /**
     * Initializes the ReviewService by obtaining a JDBC connection and ensuring the reviews table exists.
     *
     * <p>If table creation fails, the error is logged.</p>
     */
    public ReviewService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create reviews table
            String createReviewsTable = """
                    CREATE TABLE reviews (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                        product_id BIGINT NOT NULL,
                        review_text TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            tableCreator.createTableIfNotExists("reviews", createReviewsTable);

        } catch (Exception e) {
            log.error("Error creating tables for ReviewService", e);
        }

    }


    /**
     * Persist a Review into the reviews table.
     *
     * The stored row uses the review's client id, rating, and product id.
     *
     * @param review the Review whose client, rating, and product identifiers will be persisted
     */
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


    /**
     * Retrieves all reviews from the database.
     *
     * Each returned Review contains its id, associated Client and Product, and rating.
     *
     * @return a list of Review objects; empty if no reviews are found or if an error occurs
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


    /**
     * Update the stored rating for the given review record identified by its id.
     *
     * @param review the Review whose id identifies the database row and whose rating will replace the stored value
     */
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


    /**
     * Deletes the review matching the provided review's client and product identifiers from the database.
     *
     * @param review the Review whose client and product IDs identify the row to remove
     */
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
     * Compute the average rating for the specified product.
     *
     * @param productId the ID of the product to compute the average rating for
     * @return the average rating for the product, or 0.0 if the product has no ratings or an error occurs
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
     * Retrieve products paired with their average review ratings, ordered from highest to lowest average.
     *
     * Each returned Review has its product populated and its rating set to the product's average rating
     * truncated to an integer; the client field is null. The method returns an empty list if no ratings
     * are found or if an error occurs.
     *
     * @return a list of Review objects representing products and their average ratings (sorted descending)
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
     * Finds a review for the specified product by the specified user.
     *
     * Looks up a review matching the given productId and userId and returns it if present.
     *
     * @param productId the ID of the product to check
     * @param userId the ID of the user who may have created the review
     * @return the matching Review, or `null` if no review exists for the given product and user
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


    /**
     * Retrieves a page of Review objects based on the provided pagination and sorting parameters.
     *
     * @param pageRequest the pagination and sorting request used to select which reviews to return
     * @return a page containing matching reviews
     * @throws UnsupportedOperationException always thrown as this method is not implemented
     */
    @Override
    public Page<Review> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
