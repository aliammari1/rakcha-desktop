package com.esprit.services.common;

import com.esprit.models.common.Review;
import com.esprit.models.films.Film;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.FilmService;
import com.esprit.services.products.ProductService;
import com.esprit.services.series.SeriesService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
public class ReviewService implements IService<Review> {

    private static final Logger LOGGER = Logger.getLogger(ReviewService.class.getName());
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id",
            "comment",
            "rating",
            "user_id",
            "movie_id",
            "series_id",
            "product_id",
            "created_at"
    };
    private final Connection connection;
    private final UserService userService;
    private final FilmService filmService;
    private final CinemaService cinemaService;
    private final ProductService productService;
    private final SeriesService seriesService;

    /**
     * Constructs a new FilmCommentService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public ReviewService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
        this.filmService = new FilmService();
        this.cinemaService = new CinemaService();
        this.productService = new ProductService();
        this.seriesService = new SeriesService();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Review review) {
        // The SQL query must include ALL possible foreign keys
        final String req = "INSERT INTO reviews " +
                "(user_id, rating, comment, sentiment, movie_id, series_id, product_id, cinema_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {

            pst.setLong(1, review.getUser().getId());

            if (review.getRating() != null) {
                pst.setObject(2, review.getRating(), java.sql.Types.INTEGER);
            } else {
                pst.setNull(2, java.sql.Types.INTEGER);
            }

            if (review.getComment() != null) {
                pst.setString(3, review.getComment());
            } else {
                pst.setNull(3, java.sql.Types.VARCHAR);
            }

            if (review.getSentiment() != null) {
                pst.setString(4, review.getSentiment());
            } else {
                pst.setNull(4, java.sql.Types.VARCHAR);
            }

            if (review.getFilm() != null) {
                pst.setLong(5, review.getFilm().getId());
            } else {
                pst.setNull(5, java.sql.Types.INTEGER);
            }

            if (review.getSeries() != null) {
                pst.setLong(6, review.getSeries().getId());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }

            if (review.getProduct() != null) {
                pst.setLong(7, review.getProduct().getId());
            } else {
                pst.setNull(7, java.sql.Types.INTEGER);
            }

            if (review.getCinema() != null) {
                pst.setLong(8, review.getCinema().getId());
            } else {
                pst.setNull(8, java.sql.Types.INTEGER);
            }

            pst.executeUpdate();
            log.info("Review created successfully!");

        } catch (final SQLException e) {
            log.error("Error creating review", e);
            throw new RuntimeException("Database error during review creation", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Review review) {
        // We update Comment, Rating, and Sentiment.
        // We do NOT update the Foreign Keys (user_id, movie_id, etc.) as the target
        // shouldn't change.
        final String req = "UPDATE reviews SET rating = ?, comment = ?, sentiment = ? WHERE id = ?";

        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            if (review.getRating() != null) {
                statement.setObject(1, review.getRating(), java.sql.Types.INTEGER);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            if (review.getComment() != null) {
                statement.setString(2, review.getComment());
            } else {
                statement.setNull(2, java.sql.Types.VARCHAR);
            }

            if (review.getSentiment() != null) {
                statement.setString(3, review.getSentiment());
            } else {
                statement.setNull(3, java.sql.Types.VARCHAR);
            }

            statement.setLong(4, review.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                log.info("Review updated successfully!");
            } else {
                log.warn("Review update failed: No review found with ID " + review.getId());
            }

        } catch (final SQLException e) {
            log.error("Error updating review", e);
            throw new RuntimeException("Database error during review update", e);
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
        final String req = "DELETE FROM reviews WHERE id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, review.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error deleting comment", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Review> read(PageRequest pageRequest) {
        final List<Review> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM reviews"; // Make sure table name matches your schema

        if (pageRequest.hasSorting() &&
                !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                    ResultSet rs = stmt.executeQuery()) {

                final UserService userService = new UserService();
                final FilmService filmService = new FilmService();
                final SeriesService seriesService = new SeriesService();
                final ProductService productService = new ProductService();
                final CinemaService cinemaService = new CinemaService();

                while (rs.next()) {
                    try {
                        Review.ReviewBuilder builder = Review.builder();

                        builder.id(rs.getLong("id"));

                        builder.comment(rs.getString("comment"));

                        Integer ratingVal = rs.getInt("rating");
                        if (!rs.wasNull()) {
                            builder.rating(ratingVal);
                        } else {
                            builder.rating(null);
                        }

                        String sentimentStr = rs.getString("sentiment");
                        if (sentimentStr != null) {
                            try {
                                builder.sentiment(sentimentStr);
                            } catch (IllegalArgumentException e) {
                                log.warn("Unknown sentiment value in DB: {}", sentimentStr);
                            }
                        }

                        long userId = rs.getLong("user_id");
                        if (userId > 0) {
                            builder.user((Client) userService.getUserById(userId));
                        }

                        long movieId = rs.getLong("movie_id");
                        if (!rs.wasNull() && movieId > 0) {
                            builder.film(filmService.getFilm(movieId));
                        }

                        long seriesId = rs.getLong("series_id");
                        if (!rs.wasNull() && seriesId > 0) {
                            builder.series(seriesService.getByIdSeries(seriesId));
                        }

                        long productId = rs.getLong("product_id");
                        if (!rs.wasNull() && productId > 0) {
                            builder.product(productService.getProductById(productId));
                        }

                        long cinemaId = rs.getLong("cinema_id");
                        if (!rs.wasNull() && cinemaId > 0) {
                            builder.cinema(cinemaService.getCinemaById(cinemaId));
                        }

                        content.add(builder.build());

                    } catch (Exception e) {
                        log.warn("Error mapping review row ID " + rs.getLong("id"), e);
                    }
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated reviews: {}", e.getMessage(), e);
            return new Page<>(new ArrayList<>(), pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Retrieves the AverageRating value.
     * Updated to use the 'reviews' table and 'movie_id'.
     *
     * @return the AverageRating value
     */
    public double getAverageRating(final Long id_film) {
        // UPDATED: Table is now 'reviews', column is 'movie_id'
        final String req = "SELECT AVG(rating) AS averageRate FROM reviews WHERE movie_id = ?";

        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req)) {
            preparedStatement.setLong(1, id_film);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Returns 0.0 if there are no ratings (standard SQL behavior for AVG of null)
                    return resultSet.getDouble("averageRate");
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting average rating for film: " + id_film, e);
        }
        return 0.0;
    }

    /**
     * Retrieves the AverageRatingSorted value.
     *
     * @return the AverageRatingSorted value
     */
    public List<Review> getAverageRatingSorted() {
        // UPDATED: Filter by movie_id IS NOT NULL so we don't get Series or Product
        // ratings
        final String req = "SELECT movie_id, AVG(rating) AS averageRate " +
                "FROM reviews " +
                "WHERE movie_id IS NOT NULL " +
                "GROUP BY movie_id " +
                "ORDER BY averageRate DESC";

        final List<Review> aver = new ArrayList<>();
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
                final ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Note: Make sure to map 'movie_id' from the result set
                Film film = filmService.getFilm(resultSet.getLong("movie_id"));
                if (film != null) {
                    aver.add(Review.builder()
                            .film(film)
                            .rating((int) Math.round(resultSet.getDouble("averageRate"))) // Rounding is usually safer
                                                                                          // for int casting
                            .build());
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting average rating sorted", e);
        }
        return aver;
    }

    /**
     * Checks if a rating exists for a specific user and film.
     *
     * @return the result of the operation
     */
    public Review ratingExists(final Long id_film, final Long id_user) {
        // Check 'reviews' table with correct column names
        final String req = "SELECT * FROM reviews WHERE movie_id = ? AND user_id = ? LIMIT 1";

        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req)) {
            preparedStatement.setLong(1, id_film);
            preparedStatement.setLong(2, id_user);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Film film = filmService.getFilm(id_film);
                    Client client = (Client) userService.getUserById(id_user);

                    if (film != null && client != null) {
                        return Review.builder()
                                .id(resultSet.getLong("id"))
                                .film(film)
                                .user(client)
                                .rating(resultSet.getInt("rating"))
                                .comment(resultSet.getString("comment"))
                                .build();
                    }
                }
            }
        } catch (final SQLException e) {
            log.error("Error checking if rating exists", e);
        }
        return null;
    }

    /**
     * Retrieves the UserRatings for FILMS only.
     *
     * @return the UserRatings value
     */
    public List<Review> getUserRatings(Long userId) {
        List<Review> ratings = new ArrayList<>();
        // UPDATED: Ensure we only get rows where movie_id is NOT null
        // (We don't want Series ratings here if this returns List<FilmRating>)
        String query = "SELECT * FROM reviews WHERE user_id = ? AND movie_id IS NOT NULL";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client user = (Client) userService.getUserById(rs.getLong("user_id"));
                    Film film = filmService.getFilm(rs.getLong("movie_id")); // Map movie_id

                    if (user != null && film != null) {
                        ratings.add(Review.builder()
                                .id(rs.getLong("id"))
                                .film(film)
                                .user(user)
                                .rating(rs.getInt("rating"))
                                .build());
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error getting user ratings: " + userId, e);
        }
        return ratings;
    }

    /**
     * Retrieves the TopRatedFilms value.
     *
     * @return the TopRatedFilms value
     */
    public List<Review> getTopRatedFilms() {
        // UPDATED: movie_id filtering
        final String req = "SELECT movie_id, AVG(rating) AS averageRate " +
                "FROM reviews " +
                "WHERE movie_id IS NOT NULL " +
                "GROUP BY movie_id " +
                "ORDER BY averageRate DESC " +
                "LIMIT 10";

        final List<Review> topRated = new ArrayList<>();
        try (final PreparedStatement statement = this.connection.prepareStatement(req);
                final ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Film film = filmService.getFilm(rs.getLong("movie_id"));
                if (film != null) {
                    topRated.add(Review.builder()
                            .film(film)
                            .rating((int) Math.round(rs.getDouble("averageRate")))
                            .build());
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting top rated films", e);
        }
        return topRated;
    }

    /**
     * Retrieves the top rated cinemas based on average review ratings.
     *
     * @param limit the maximum number of cinemas to return
     * @return list of Reviews containing cinema and average rating
     */
    public List<Review> getTopRatedCinemas(int limit) {
        final String req = "SELECT cinema_id, AVG(rating) AS averageRate " +
                "FROM reviews " +
                "WHERE cinema_id IS NOT NULL " +
                "GROUP BY cinema_id " +
                "ORDER BY averageRate DESC " +
                "LIMIT ?";

        final List<Review> topRated = new ArrayList<>();
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, limit);
            try (final ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
                    if (cinema != null) {
                        topRated.add(Review.builder()
                                .cinema(cinema)
                                .rating((int) Math.round(rs.getDouble("averageRate")))
                                .build());
                    }
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting top rated cinemas", e);
        }
        return topRated;
    }

    /**
     * Reads a review by user ID (specifically for products).
     * * @param userId the ID of the user/client
     *
     * @return the Review object, or null if not found
     */
    public Review readByClientId(final long userId) {
        Review review = null;
        // Filter by product_id IS NOT NULL to ignore Movie/Series reviews
        final String req = "SELECT * FROM reviews WHERE user_id = ? AND product_id IS NOT NULL LIMIT 1";

        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, userId);

            try (final ResultSet rs = pst.executeQuery()) {
                final ProductService productService = new ProductService();
                final UserService userService = new UserService();

                if (rs.next()) {
                    review = Review.builder()
                            .id(rs.getLong("id"))
                            .user((Client) userService.getUserById(rs.getLong("user_id")))
                            .comment(rs.getString("comment"))
                            .rating(rs.getInt("rating"))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .build();
                }
            }
        } catch (final SQLException e) {
            log.error("Error fetching review for user: " + userId, e);
        }
        return review;
    }

    /**
     * Retrieves reviews by product ID.
     *
     * @param productId the ID of the product
     * @return list of reviews for the specified product
     */
    public List<Review> getCommentsByProductId(final long productId) {
        final List<Review> reviews = new ArrayList<>();
        final String req = "SELECT * FROM reviews WHERE product_id = ?";

        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setLong(1, productId);

            try (final ResultSet rs = pst.executeQuery()) {
                final UserService userService = new UserService();
                final ProductService productService = new ProductService();

                while (rs.next()) {
                    final Review review = Review.builder()
                            .id(rs.getLong("id"))
                            .user((Client) userService.getUserById(rs.getLong("user_id")))
                            .comment(rs.getString("comment"))
                            .rating(rs.getInt("rating"))
                            .sentiment(rs.getString("sentiment"))
                            .product(productService.getProductById(rs.getLong("product_id")))
                            .build();

                    reviews.add(review);
                }
            }
        } catch (final SQLException e) {
            log.error("Error fetching reviews for product: " + productId, e);
        }
        return reviews;
    }

    /**
     * Counts the total number of reviews in the database.
     *
     * @return the total count of reviews
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM reviews";
        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting reviews", e);
        }
        return 0;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param id the ID of the review to retrieve
     * @return the review with the specified ID, or null if not found
     */
    @Override
    public Review getById(final Long id) {
        String query = "SELECT * FROM reviews WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Review.builder()
                        .id(rs.getLong("id"))
                        .user((Client) userService.getUserById(rs.getLong("user_id")))
                        .comment(rs.getString("comment"))
                        .rating(rs.getInt("rating"))
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error retrieving review by id: " + id, e);
        }
        return null;
    }

    /**
     * Retrieves all reviews from the database.
     *
     * @return a list of all reviews
     */
    @Override
    public List<Review> getAll() {
        final List<Review> reviews = new ArrayList<>();
        final String query = "SELECT * FROM reviews ORDER BY id DESC";
        try (final PreparedStatement stmt = this.connection.prepareStatement(query);
                final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final Review review = Review.builder()
                        .id(rs.getLong("id"))
                        .user((Client) userService.getUserById(rs.getLong("user_id")))
                        .comment(rs.getString("comment"))
                        .rating(rs.getInt("rating"))
                        .build();
                reviews.add(review);
            }
        } catch (final SQLException e) {
            log.error("Error fetching all reviews", e);
        }
        return reviews;
    }

    /**
     * Searches for reviews by comment text or sentiment.
     *
     * @param query the search query
     * @return a list of reviews matching the search query
     */
    @Override
    public List<Review> search(final String query) {
        final List<Review> reviews = new ArrayList<>();
        final String req = "SELECT * FROM reviews WHERE comment LIKE ? OR sentiment LIKE ? ORDER BY id DESC";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            try (final ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    final Review review = Review.builder()
                            .id(rs.getLong("id"))
                            .user((Client) userService.getUserById(rs.getLong("user_id")))
                            .comment(rs.getString("comment"))
                            .rating(rs.getInt("rating"))
                            .sentiment(rs.getString("sentiment"))
                            .build();
                    reviews.add(review);
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching reviews", e);
        }
        return reviews;
    }

    /**
     * Checks if a review exists by its ID.
     *
     * @param id the ID of the review to check
     * @return true if the review exists, false otherwise
     */
    @Override
    public boolean exists(final Long id) {
        return getById(id) != null;
    }

    /**
     * Alias for getUserRatings - get reviews by user ID.
     *
     * @param userId the ID of the user
     * @return list of reviews created by the user
     */
    public List<Review> getReviewsByUser(final Long userId) {
        return getUserRatings(userId);
    }

    /**
     * Delete a review by ID.
     *
     * @param reviewId the ID of the review to delete
     */
    public void deleteReview(final Long reviewId) {
        Review review = getById(reviewId);
        if (review != null) {
            delete(review);
        }
    }

    /**
     * Update a review.
     *
     * @param review the review to update
     */
    public void updateReview(final Review review) {
        update(review);
    }

    /**
     * Get reviews for a specific content (film, series, product, cinema).
     *
     * @param contentType the type of content (e.g., "film", "series", "product",
     *                    "cinema")
     * @param contentId   the ID of the content
     * @return list of reviews for the content
     */
    public List<Review> getReviewsByContent(String contentType, Long contentId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE content_type = ? AND content_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, contentType);
            stmt.setLong(2, contentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = buildReviewFromResultSet(rs);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving reviews by content", e);
        }

        return reviews;
    }

    /**
     * Get a specific review for a user and content.
     *
     * @param userId      the user ID
     * @param contentType the type of content
     * @param contentId   the ID of the content
     * @return the review if found, null otherwise
     */
    public Review getUserReview(Long userId, String contentType, Long contentId) {
        String query = "SELECT * FROM reviews WHERE user_id = ? AND content_type = ? AND content_id = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setString(2, contentType);
            stmt.setLong(3, contentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildReviewFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving user review", e);
        }

        return null;
    }

    /**
     * Build a Review object from a ResultSet.
     *
     * @param rs the result set containing review data
     * @return a Review object or null if user not found
     */
    private Review buildReviewFromResultSet(ResultSet rs) {
        try {
            Review.ReviewBuilder reviewBuilder = Review.builder();
            reviewBuilder.id(rs.getLong("id"));
            reviewBuilder.rating(rs.getInt("rating"));
            reviewBuilder.comment(rs.getString("comment"));
            reviewBuilder.sentiment(rs.getString("sentiment"));
            reviewBuilder.contentType(rs.getString("content_type"));
            reviewBuilder.contentId(rs.getLong("content_id"));
            reviewBuilder.contentTitle(rs.getString("content_title"));
            reviewBuilder.contentImage(rs.getString("content_image"));
            reviewBuilder.helpfulCount(rs.getInt("helpful_count"));
            reviewBuilder.text(rs.getString("text"));

            // Handle timestamp conversion
            java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
            if (timestamp != null) {
                reviewBuilder.createdAt(timestamp.toLocalDateTime());
            }

            // Load user if available
            Long userId = rs.getLong("user_id");
            if (userId != null && userId > 0) {
                UserService userService = new UserService();
                User user = userService.getById(userId);
                if (user instanceof Client) {
                    reviewBuilder.user((Client) user);
                }
            }

            return reviewBuilder.build();
        } catch (SQLException e) {
            log.error("Error building review from result set", e);
            return null;
        }
    }

    /**
     * Create a new review.
     *
     * @param review the review to create
     */
    public void createReview(Review review) {
        create(review);
    }

    /**
     * Mark a review as helpful.
     *
     * @param reviewId the review ID
     * @param userId   the user ID marking it helpful
     */
    public void markHelpful(Long reviewId, Long userId) {
        String query = "UPDATE reviews SET helpful_count = helpful_count + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, reviewId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error marking review as helpful", e);
        }
    }

    /**
     * Report a review.
     *
     * @param reviewId the review ID
     * @param userId   the user ID reporting
     * @param reason   the reason for reporting
     */
    public void reportReview(Long reviewId, Long userId, String reason) {
        // This could be stored in a separate reviews_reports table
        // For now, just log it
        log.info("Review {} reported by user {} - Reason: {}", reviewId, userId, reason);
    }
}
