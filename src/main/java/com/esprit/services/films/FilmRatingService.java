package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.films.Film;
import com.esprit.models.films.FilmRating;
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
public class FilmRatingService implements IService<FilmRating> {
    private final Connection connection;
    private final UserService userService;
    private final FilmService filmService;

    /**
     * Initialize the service by acquiring a database connection, creating required dependent services, and ensuring the film_ratings table exists.
     *
     * <p>This constructor obtains a JDBC connection, instantiates UserService and FilmService, and creates the
     * film_ratings table with columns (id, film_id, user_id, rating, created_at) and a unique constraint on (film_id, user_id)
     * if the table does not already exist. Errors during table creation are logged.</p>
     */
    public FilmRatingService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
        this.filmService = new FilmService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create film_ratings table
            String createFilmRatingsTable = """
                    CREATE TABLE film_ratings (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        film_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(film_id, user_id)
                    )
                    """;
            tableCreator.createTableIfNotExists("film_ratings", createFilmRatingsTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for FilmRatingService", e);
        }

    }


    /**
     * Persist the given film rating to the database.
     *
     * @param filmRating the FilmRating whose film id, user id, and rating will be inserted into the film_ratings table
     * @throws RuntimeException if a database error prevents inserting the rating
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final FilmRating filmRating) {
        final String req = "INSERT INTO film_ratings (film_id, user_id, rating) VALUES (?,?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmRating.getFilm().getId());
            statement.setLong(2, filmRating.getClient().getId());
            statement.setInt(3, filmRating.getRating());
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            log.error("Error creating film rating", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Retrieves all film ratings from the database and resolves each rating's associated
     * Film and Client; ratings whose related Film or Client cannot be loaded are skipped.
     *
     * @return a list of FilmRating objects containing id, film, client, and rating;
     *         returns an empty list if no ratings are found or if an error occurs
     */
    public List<FilmRating> read() {
        final List<FilmRating> ratings = new ArrayList<>();
        final String req = "SELECT * FROM film_ratings";
        try (final PreparedStatement statement = this.connection.prepareStatement(req);
                final ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                try {
                    Film film = filmService.getFilm(rs.getLong("film_id"));
                    Client client = (Client) userService.getUserById(rs.getLong("user_id"));
                    if (film != null && client != null) {
                        ratings.add(FilmRating.builder().id(rs.getLong("id")).film(film).client(client)
                                .rating(rs.getInt("rating")).build());
                    }
 else {
                        log.warn("Missing required entities for rating ID: " + rs.getLong("id"));
                    }

                }
 catch (Exception e) {
                    log.warn("Error loading rating relationships for rating ID: " + rs.getLong("id"), e);
                }

            }

        }
 catch (final SQLException e) {
            log.error("Error reading ratings", e);
        }

        return ratings;
    }


    /**
     * Updates the stored rating for the given film and client.
     *
     * @param filmRating the FilmRating containing the film, client, and new rating to persist
     * @throws RuntimeException if a database error occurs while updating the rating
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final FilmRating filmRating) {
        final String req = "UPDATE film_ratings SET rating=? WHERE film_id=? AND user_id=?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmRating.getRating());
            statement.setLong(2, filmRating.getFilm().getId());
            statement.setLong(3, filmRating.getClient().getId());
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            log.error("Error updating rating", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Deletes the film rating identified by the film and user contained in the provided object.
     *
     * @param filmRating the FilmRating whose film ID and user ID identify the row to remove
     * @throws RuntimeException if a database access error occurs while deleting the rating
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final FilmRating filmRating) {
        final String req = "DELETE FROM film_ratings WHERE film_id=? AND user_id=?";
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req)) {
            preparedStatement.setLong(1, filmRating.getFilm().getId());
            preparedStatement.setLong(2, filmRating.getClient().getId());
            preparedStatement.executeUpdate();
        }
 catch (final SQLException e) {
            log.error("Error deleting rating", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Fetches the average rating for a film.
     *
     * @param id_film the film's id to compute the average rating for
     * @return the average rating for the specified film, or 0.0 if no ratings exist or an error occurs
     */
    public double getAvergeRating(final Long id_film) {
        final String req = "SELECT AVG(rating) AS averageRate FROM film_ratings WHERE film_id =?";
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req)) {
            preparedStatement.setLong(1, id_film);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("averageRate");
                }

            }

        }
 catch (final SQLException e) {
            log.error("Error getting average rating for film: " + id_film, e);
        }

        return 0.0;
    }


    /**
     * Retrieves films with their average ratings sorted from highest to lowest.
     *
     * <p>Only films that can be resolved via the FilmService are included. Each returned
     * FilmRating contains the Film and its average rating converted to an integer by
     * truncating any fractional part.</p>
     *
     * @return a list of FilmRating objects ordered by average rating descending
     */
    public List<FilmRating> getAverageRatingSorted() {
        final String req = "SELECT film_id, AVG(rating) AS averageRate FROM film_ratings GROUP BY film_id ORDER BY averageRate DESC ";
        final List<FilmRating> aver = new ArrayList<>();
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
                final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Film film = filmService.getFilm(resultSet.getLong("film_id"));
                if (film != null) {
                    aver.add(FilmRating.builder().film(film).rating((int) resultSet.getDouble("averageRate")).build());
                }

            }

        }
 catch (final SQLException e) {
            log.error("Error getting average rating sorted", e);
        }

        return aver;
    }


    /**
         * Retrieve the film rating for a specific film and user if one exists.
         *
         * @param id_film the ID of the film
         * @param id_user the ID of the user
         * @return the `FilmRating` for the given film and user, or `null` if no rating exists or related entities cannot be loaded
         */
    public FilmRating ratingExists(final Long id_film, final Long id_user) {
        final String req = "SELECT * FROM film_ratings WHERE film_id =? AND user_id=? ";
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req)) {
            preparedStatement.setLong(1, id_film);
            preparedStatement.setLong(2, id_user);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Film film = filmService.getFilm(id_film);
                    Client client = (Client) userService.getUserById(id_user);
                    if (film != null && client != null) {
                        return FilmRating.builder().id(resultSet.getLong("id")).film(film).client(client)
                                .rating(resultSet.getInt("rating")).build();
                    }

                }

            }

        }
 catch (final SQLException e) {
            log.error("Error checking if rating exists", e);
        }

        return null;
    }


    /**
     * Retrieve all film ratings submitted by the specified user.
     *
     * Only includes ratings where both the user and the film exist in their respective services.
     *
     * @param userId the ID of the user whose ratings to retrieve
     * @return a list of FilmRating objects for the given user; empty if none are found
     */
    public List<FilmRating> getUserRatings(Long userId) {
        List<FilmRating> ratings = new ArrayList<>();
        String query = "SELECT * FROM film_ratings WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client user = (Client) userService.getUserById(rs.getLong("user_id"));
                    Film film = filmService.getFilm(rs.getLong("film_id"));
                    if (user != null && film != null) {
                        ratings.add(FilmRating.builder().id(rs.getLong("id")).film(film).client(user)
                                .rating(rs.getInt("rating")).build());
                    }

                }

            }

        }
 catch (SQLException e) {
            log.error("Error getting user ratings: " + userId, e);
        }

        return ratings;
    }


    /**
     * Retrieve the top 10 films ranked by average user rating.
     *
     * Each returned FilmRating contains the Film and its average rating rounded to the nearest integer;
     * films without any stored ratings are excluded.
     *
     * @return a list of FilmRating objects for the top 10 films ordered by descending average rating; an empty list if none are available
     */
    public List<FilmRating> getTopRatedFilms() {
        final String req = "SELECT film_id, AVG(rating) AS averageRate FROM film_ratings GROUP BY film_id ORDER BY averageRate DESC LIMIT 10";
        final List<FilmRating> topRated = new ArrayList<>();
        try (final PreparedStatement statement = this.connection.prepareStatement(req);
                final ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Film film = filmService.getFilm(rs.getLong("film_id"));
                if (film != null) {
                    topRated.add(FilmRating.builder().film(film).rating((int) Math.round(rs.getDouble("averageRate")))
                            .build());
                }

            }

        }
 catch (final SQLException e) {
            log.error("Error getting top rated films", e);
        }

        return topRated;
    }


    /**
     * Placeholder for paginated retrieval of FilmRating entities; currently unimplemented.
     *
     * @param pageRequest pagination and sorting parameters for the requested page
     * @throws UnsupportedOperationException always thrown with message "Unimplemented method 'read'"
     */
    @Override
    public Page<FilmRating> read(PageRequest pageRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
