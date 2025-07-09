package com.esprit.services.cinemas;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;
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
public class MovieSessionService implements IService<MovieSession> {
    private final Connection connection;
    private final CinemaService cinemaService;
    private final CinemaHallService cinemaHallService;
    private final FilmService filmService;

    /**
     * Constructs a new MovieSessionService instance.
     * Initializes database connection and related services.
     * Creates tables if they don't exist.
     */
    public MovieSessionService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaService = new CinemaService();
        this.cinemaHallService = new CinemaHallService();
        this.filmService = new FilmService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create movie_session table
            String createMovieSessionTable = """
                    CREATE TABLE movie_session (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        film_id BIGINT NOT NULL,
                        cinema_hall_id BIGINT NOT NULL,
                        start_time TIME NOT NULL,
                        end_time TIME NOT NULL,
                        session_date DATE NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            tableCreator.createTableIfNotExists("movie_session", createMovieSessionTable);

        } catch (Exception e) {
            log.error("Error creating tables for MovieSessionService", e);
        }
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(MovieSession movieSession) {
        String query = "INSERT INTO movie_session (film_id, cinema_hall_id, start_time, end_time, session_date, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getFilm().getId());
            stmt.setLong(2, movieSession.getCinemaHall().getId());
            stmt.setTime(3, movieSession.getStartTime());
            stmt.setTime(4, movieSession.getEndTime());
            stmt.setDate(5, movieSession.getSessionDate());
            stmt.setDouble(6, movieSession.getPrice());
            stmt.executeUpdate();
            log.info("Movie session created successfully");
        } catch (SQLException e) {
            log.error("Error creating movie session", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(MovieSession movieSession) {
        String query = "UPDATE movie_session SET film_id = ?, cinema_hall_id = ?, start_time = ?, end_time = ?, session_date = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getFilm().getId());
            stmt.setLong(2, movieSession.getCinemaHall().getId());
            stmt.setTime(3, movieSession.getStartTime());
            stmt.setTime(4, movieSession.getEndTime());
            stmt.setDate(5, movieSession.getSessionDate());
            stmt.setDouble(6, movieSession.getPrice());
            stmt.setLong(7, movieSession.getId());
            stmt.executeUpdate();
            log.info("Movie session updated successfully");
        } catch (SQLException e) {
            log.error("Error updating movie session", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(MovieSession movieSession) {
        String query = "DELETE FROM movie_session WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getId());
            stmt.executeUpdate();
            log.info("Movie session deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting movie session", e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<MovieSession> read() {
        List<MovieSession> movieSessions = new ArrayList<>();
        String query = "SELECT * FROM movie_session";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MovieSession session = buildMovieSession(rs);
                if (session != null) {
                    movieSessions.add(session);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading movie sessions", e);
        }
        return movieSessions;
    }

    /**
     * Retrieves the SessionsByFilmAndCinema value.
     *
     * @return the SessionsByFilmAndCinema value
     */
    public List<MovieSession> getSessionsByFilmAndCinema(Long filmId, Long cinemaId) {
        List<MovieSession> movieSessions = new ArrayList<>();
        String query = "SELECT ms.* FROM movie_session ms " + "JOIN cinema_hall ch ON ms.cinema_hall_id = ch.id "
                + "WHERE ms.film_id = ? AND ch.cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, filmId);
            stmt.setLong(2, cinemaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MovieSession session = buildMovieSession(rs);
                if (session != null) {
                    movieSessions.add(session);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting sessions by film and cinema", e);
        }
        return movieSessions;
    }

    /**
     * Retrieves the SessionsByDateRangeAndCinema value.
     *
     * @return the SessionsByDateRangeAndCinema value
     */
    public Map<LocalDate, List<MovieSession>> getSessionsByDateRangeAndCinema(LocalDate startDate, LocalDate endDate,
            Cinema cinema) {
        Map<LocalDate, List<MovieSession>> sessionsByDate = new HashMap<>();
        if (cinema == null) {
            log.warn("Cinema is null");
            return sessionsByDate;
        }

        String query = "SELECT ms.* FROM movie_session ms " + "JOIN cinema_hall ch ON ms.cinema_hall_id = ch.id "
                + "WHERE ms.session_date BETWEEN ? AND ? AND ch.cinema_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setLong(3, cinema.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate sessionDate = rs.getDate("session_date").toLocalDate();
                    MovieSession session = buildMovieSession(rs);
                    if (session != null) {
                        sessionsByDate.computeIfAbsent(sessionDate, k -> new ArrayList<>()).add(session);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error getting sessions by date range and cinema", e);
        }
        return sessionsByDate;
    }

    /**
     * Retrieves the FirstSessionForFilm value.
     *
     * @return the FirstSessionForFilm value
     */
    public MovieSession getFirstSessionForFilm(Long filmId) {
        String query = "SELECT * FROM movie_session WHERE film_id = ? AND session_date >= CURRENT_DATE ORDER BY session_date, start_time LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, filmId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildMovieSession(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting first session for film: " + filmId, e);
        }
        return null;
    }

    /**
     * Retrieves the MovieSessionById value.
     *
     * @return the MovieSessionById value
     */
    public MovieSession getMovieSessionById(int id) {
        String query = "SELECT * FROM movie_session WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildMovieSession(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting movie session by id: " + id, e);
        }
        return null;
    }

    /**
     * @param rs
     * @return MovieSession
     */
    private MovieSession buildMovieSession(ResultSet rs) {
        try {
            Film film = filmService.getFilm(rs.getLong("film_id"));
            CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(rs.getLong("cinema_hall_id"));

            if (film == null || cinemaHall == null) {
                log.warn("Missing required entities for movie session id: " + rs.getLong("id"));
                return null;
            }

            return MovieSession.builder().id(rs.getLong("id")).film(film).cinemaHall(cinemaHall)
                    .startTime(rs.getTime("start_time")).endTime(rs.getTime("end_time"))
                    .sessionDate(rs.getDate("session_date")).price(rs.getDouble("price")).build();
        } catch (SQLException e) {
            log.error("Error building movie session from ResultSet", e);
            return null;
        }
    }
}
