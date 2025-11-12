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
     * Constructs a new FilmRatingService instance.
     * Initializes database connection, related services, and creates tables if they
     * don't exist.
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
     * Retrieves the AvergeRating value.
     *
     * @return the AvergeRating value
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
     * Retrieves the AverageRatingSorted value.
     *
     * @return the AverageRatingSorted value
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
     * Performs ratingExists operation.
     *
     * @return the result of the operation
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
     * Retrieves the UserRatings value.
     *
     * @return the UserRatings value
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
     * Retrieves the TopRatedFilms value.
     *
     * @return the TopRatedFilms value
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


    @Override
    public Page<FilmRating> read(PageRequest pageRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}

