package com.esprit.services.cinemas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaComment;
import com.esprit.models.cinemas.CinemaRating;
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
public class CinemaRatingService implements IService<CinemaRating> {
    private final Connection connection;
    private final CinemaService cinemaService;
    private final UserService userService;

    /**
     * Performs CinemaRatingService operation.
     *
     * @return the result of the operation
     */
    public CinemaRatingService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaService = new CinemaService();
        this.userService = new UserService();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *            the entity to create
     */
    public void create(CinemaRating cinemaRating) {
        // Delete existing rating from same user for same cinema
        deleteByClientAndCinema(cinemaRating.getClient().getId(), cinemaRating.getCinema().getId());

        String query = "INSERT INTO cinema_rating (cinema_id, client_id, rating) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaRating.getCinema().getId());
            stmt.setLong(2, cinemaRating.getClient().getId());
            stmt.setInt(3, cinemaRating.getRating());
            stmt.executeUpdate();
            log.info("Cinema rating created successfully");
        } catch (SQLException e) {
            log.error("Error creating cinema rating", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *            the entity to update
     */
    public void update(CinemaRating cinemaRating) {
        String query = "UPDATE cinema_rating SET rating = ? WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cinemaRating.getRating());
            stmt.setLong(2, cinemaRating.getCinema().getId());
            stmt.setLong(3, cinemaRating.getClient().getId());
            stmt.executeUpdate();
            log.info("Cinema rating updated successfully");
        } catch (SQLException e) {
            log.error("Error updating cinema rating", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *            the ID of the entity to delete
     */
    public void delete(CinemaRating cinemaRating) {
        String query = "DELETE FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaRating.getCinema().getId());
            stmt.setLong(2, cinemaRating.getClient().getId());
            stmt.executeUpdate();
            log.info("Cinema rating deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting cinema rating", e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<CinemaRating> read() {
        List<CinemaRating> ratings = new ArrayList<>();
        String query = "SELECT * FROM cinema_rating";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CinemaRating rating = buildCinemaRating(rs);
                if (rating != null) {
                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading cinema ratings", e);
        }
        return ratings;
    }

    /**
     * Retrieves the RatingForClientAndCinema value.
     *
     * @return the RatingForClientAndCinema value
     */
    public Integer getRatingForClientAndCinema(Long clientId, Long cinemaId) {
        String query = "SELECT rating FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            stmt.setLong(2, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            log.error("Error getting rating for client and cinema", e);
        }
        return null;
    }

    /**
     * Retrieves the AverageRating value.
     *
     * @return the AverageRating value
     */
    public Double getAverageRating(Long cinemaId) {
        String query = "SELECT AVG(rating) as average FROM cinema_rating WHERE cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average");
                }
            }
        } catch (SQLException e) {
            log.error("Error getting average rating for cinema: " + cinemaId, e);
        }
        return 0.0;
    }

    /**
     * Retrieves the TopRatedCinemas value.
     *
     * @return the TopRatedCinemas value
     */
    public List<Cinema> getTopRatedCinemas() {
        List<Cinema> topRatedCinemas = new ArrayList<>();
        String query = "SELECT cinema_id, AVG(rating) as average_rating FROM cinema_rating GROUP BY cinema_id ORDER BY average_rating DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long cinemaId = rs.getLong("cinema_id");
                Cinema cinema = cinemaService.getCinemaById(cinemaId);
                if (cinema != null) {
                    topRatedCinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting top rated cinemas", e);
        }
        return topRatedCinemas;
    }

    private void deleteByClientAndCinema(Long clientId, Long cinemaId) {
        String query = "DELETE FROM cinema_rating WHERE cinema_id = ? AND client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            stmt.setLong(2, clientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting existing rating", e);
        }
    }

    /**
     * Retrieves the CommentsByCinemaId value.
     *
     * @return the CommentsByCinemaId value
     */
    public List<CinemaComment> getCommentsByCinemaId(Long cinemaId) {
        List<CinemaComment> comments = new ArrayList<>();
        String query = "SELECT * FROM cinema_comment WHERE cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cinemaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CinemaComment comment = buildCinemaComment(rs);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error getting comments by cinema id: " + cinemaId, e);
        }
        return comments;
    }

    private CinemaRating buildCinemaRating(ResultSet rs) {
        try {
            Cinema cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
            Client client = (Client) userService.getUserById(rs.getLong("client_id"));

            if (cinema == null || client == null) {
                log.warn("Missing required entities for cinema rating id: " + rs.getLong("id"));
                return null;
            }

            return CinemaRating.builder().id(rs.getLong("id")).cinema(cinema).client(client).rating(rs.getInt("rating"))
                    .build();
        } catch (SQLException e) {
            log.error("Error building cinema rating from ResultSet", e);
            return null;
        }
    }

    private CinemaComment buildCinemaComment(ResultSet rs) {
        try {
            Cinema cinema = cinemaService.getCinemaById(rs.getLong("cinema_id"));
            Client client = (Client) userService.getUserById(rs.getLong("client_id"));

            if (cinema == null || client == null) {
                log.warn("Missing required entities for cinema comment id: " + rs.getLong("id"));
                return null;
            }

            return CinemaComment.builder().id(rs.getLong("id")).cinema(cinema).client(client)
                    .commentText(rs.getString("comment_text")).sentiment(rs.getString("sentiment")).build();
        } catch (SQLException e) {
            log.error("Error building cinema comment from ResultSet", e);
            return null;
        }
    }
}
