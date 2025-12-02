package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "movie_id", "hall_id", "start_time", "end_time", "price"
    };
    private final Connection connection;
    private final CinemaHallService cinemaHallService;
    private final FilmService filmService;

    /**
     * Initialize the MovieSessionService, its required service dependencies, and ensure the
     * movie_session database table exists.
     *
     * <p>Sets up the database connection, constructs CinemaHallService, and
     * FilmService instances, and creates the movie_session table if it is not already present.
     */
    public MovieSessionService() {
        this.connection = DataSource.getInstance().getConnection();
        this.cinemaHallService = new CinemaHallService();
        this.filmService = new FilmService();
    }


    /**
     * Inserts a new MovieSession row into the database.
     *
     * @param movieSession the MovieSession to persist; must have non-null Film and CinemaHall with valid IDs, and non-null startTime, endTime, and sessionDate
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(MovieSession movieSession) {
        String query = "INSERT INTO screenings (movie_id, hall_id, start_time, end_time, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getFilm().getId());
            stmt.setLong(2, movieSession.getCinemaHall().getId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(movieSession.getStartTime()));
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(movieSession.getEndTime()));

            stmt.setDouble(5, movieSession.getPrice());
            stmt.executeUpdate();
            log.info("Movie session created successfully");
        } catch (SQLException e) {
            log.error("Error creating movie session", e);
        }

    }


    /**
     * Update an existing movie_session row with values from the provided MovieSession.
     * <p>
     * The MovieSession's id determines which row to update; the film, cinemaHall,
     * startTime, endTime, sessionDate, and price fields are written to the database.
     *
     * @param movieSession the MovieSession whose id identifies the row to update and whose fields supply new values
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(MovieSession movieSession) {
        String query = "UPDATE screenings SET movie_id = ?, hall_id = ?, start_time = ?, end_time = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getFilm().getId());
            stmt.setLong(2, movieSession.getCinemaHall().getId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(movieSession.getStartTime()));
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(movieSession.getEndTime()));
            stmt.setDouble(5, movieSession.getPrice());
            stmt.setLong(6, movieSession.getId());
            stmt.executeUpdate();
            log.info("Movie session updated successfully");
        } catch (SQLException e) {
            log.error("Error updating movie session", e);
        }

    }


    /**
     * Delete the specified movie session record from the database.
     *
     * @param movieSession the MovieSession whose database row will be removed; its `id` must be set
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(MovieSession movieSession) {
        String query = "DELETE FROM screenings WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, movieSession.getId());
            stmt.executeUpdate();
            log.info("Movie session deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting movie session", e);
        }

    }


    /**
     * Retrieve a paginated page of MovieSession records.
     * <p>
     * Validates the requested sort column and falls back to default sorting if the sort column is not allowed.
     *
     * @param pageRequest pagination and sorting parameters; if the requested sort column is invalid it will be ignored and default sorting will be used
     * @return a Page<MovieSession> containing the sessions for the requested page and the total number of matching sessions; if a database error occurs, returns an empty page with totalElements set to 0
     */
    @Override
    /**
     * Retrieves movie sessions with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of movie sessions
     */
    public Page<MovieSession> read(PageRequest pageRequest) {
        final List<MovieSession> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM screenings";

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
                while (rs.next()) {
                    MovieSession session = buildMovieSession(rs);
                    if (session != null) {
                        content.add(session);
                    }

                }

            }


            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated movie sessions: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Retrieve movie sessions for a given film at a specific cinema.
     *
     * @param filmId   the ID of the film to filter sessions by
     * @param cinemaId the ID of the cinema to filter sessions by
     * @return a list of MovieSession objects for the specified film and cinema; an empty list if none are found
     */
    public List<MovieSession> getSessionsByFilmAndCinema(Long filmId, Long cinemaId) {
        List<MovieSession> movieSessions = new ArrayList<>();
        String query = "SELECT ms.* FROM screenings ms " + "JOIN cinema_halls ch ON ms.hall_id = ch.id "
            + "WHERE ms.movie_id = ? AND ch.cinema_id = ?";
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
     * Retrieve movie sessions for the given cinema grouped by session date within the specified date range.
     *
     * @param startDate the start of the date range (inclusive)
     * @param endDate   the end of the date range (inclusive)
     * @param cinema    the cinema to filter sessions by; if null the method returns an empty map
     * @return a map keyed by session date where each value is the list of MovieSession objects occurring on that date
     */
    public Map<LocalDate, List<MovieSession>> getSessionsByDateRangeAndCinema(LocalDate startDate, LocalDate endDate,
                                                                              Cinema cinema) {
        Map<LocalDate, List<MovieSession>> sessionsByDate = new HashMap<>();
        if (cinema == null) {
            log.warn("Cinema is null");
            return sessionsByDate;
        }


        String query = "SELECT ms.* FROM screenings ms JOIN cinema_halls ch ON ms.hall_id = ch.id WHERE ms.start_time >= ? AND ms.start_time < ? AND ch.cinema_id = ? ORDER BY ms.start_time";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(endDate.atStartOfDay()));
            stmt.setLong(3, cinema.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate sessionDate = rs.getTimestamp("start_time").toLocalDateTime().toLocalDate();
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
     * Finds the earliest upcoming session for the specified film (sessions with session_date >= current date),
     * ordered by session_date then start_time.
     *
     * @param filmId the film's id to search sessions for
     * @return the earliest upcoming MovieSession for the film, or {@code null} if no matching session exists
     */
    public MovieSession getFirstSessionForFilm(Long filmId) {
        String query = "SELECT * FROM screenings WHERE movie_id = ? AND start_time >= CURRENT_DATE ORDER BY start_time LIMIT 1";
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
     * Retrieves the movie session with the specified id.
     *
     * @return the MovieSession with the specified id, or null if no session exists with that id or an error occurs
     */
    public MovieSession getMovieSessionById(Long id) {
        String query = "SELECT * FROM screenings WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
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
     * Constructs a MovieSession from the current row of the given ResultSet.
     * <p>
     * Builds a MovieSession populated with Film and CinemaHall looked up by their IDs from the row;
     * returns `null` if a required related entity is missing or if an SQL error occurs while reading the row.
     *
     * @param rs the ResultSet positioned at the row to convert into a MovieSession
     * @return the constructed MovieSession, or `null` when related entities are missing or an error prevents construction
     */
    private MovieSession buildMovieSession(ResultSet rs) {
        try {
            Film film = filmService.getFilm(rs.getLong("movie_id"));
            CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(rs.getLong("hall_id"));

            if (film == null || cinemaHall == null) {
                log.warn("Missing required entities for movie session id: " + rs.getLong("id"));
                return null;
            }


            return MovieSession.builder().id(rs.getLong("id")).film(film).cinemaHall(cinemaHall)
                .startTime(rs.getTimestamp("start_time").toLocalDateTime()).endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .price(rs.getDouble("price")).build();
        } catch (SQLException e) {
            log.error("Error building movie session from ResultSet", e);
            return null;
        }

    }

    /**
     * Check if a movie session exists with the given id.
     *
     * @param id the ID of the movie session
     * @return true if a movie session with the given id exists, false otherwise
     */
    @Override
    public boolean exists(Long id) {
        String query = "SELECT 1 FROM screenings WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if movie session exists with id: " + id, e);
            return false;
        }
    }

    /**
     * Retrieve a movie session by its id.
     *
     * @param id the ID of the movie session
     * @return the MovieSession with the specified id, or null if not found
     */
    @Override
    public MovieSession getById(Long id) {
        return getMovieSessionById(id);
    }

    /**
     * Retrieve all movie sessions without pagination.
     *
     * @return a list of all movie sessions
     */
    @Override
    public List<MovieSession> getAll() {
        List<MovieSession> sessions = new ArrayList<>();
        String query = "SELECT * FROM screenings";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MovieSession session = buildMovieSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all movie sessions", e);
        }
        return sessions;
    }

    /**
     * Get the total count of movie sessions in the database.
     *
     * @return the total number of movie sessions
     */
    @Override
    public int count() {
        String query = "SELECT COUNT(*) FROM screenings";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting movie sessions", e);
        }
        return 0;
    }

    /**
     * Search for movie sessions by a search term.
     *
     * @param searchTerm the search term to look for
     * @return a list of movie sessions matching the search term
     */
    @Override
    public List<MovieSession> search(String searchTerm) {
        List<MovieSession> sessions = new ArrayList<>();
        String query = "SELECT ms.* FROM screenings ms " +
            "JOIN cinema_halls ch ON ms.hall_id = ch.id " +
            "JOIN films f ON ms.movie_id = f.id " +
            "WHERE f.title LIKE ? OR ch.name LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String likePattern = "%" + searchTerm + "%";
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MovieSession session = buildMovieSession(rs);
                    if (session != null) {
                        sessions.add(session);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error searching movie sessions", e);
        }
        return sessions;
    }

    /**
     * Get all movie sessions from the database.
     * @return list of all movie sessions
     */
    public List<MovieSession> getAllSessions() {
        List<MovieSession> sessions = new ArrayList<>();
        String query = "SELECT * FROM movie_sessions";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MovieSession session = buildMovieSession(rs);
                if (session != null) {
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all movie sessions", e);
        }
        return sessions;
    }

    /**
     * Create a new movie session (convenience alias).
     * @param session the movie session to create
     */
    public void createSession(MovieSession session) {
        this.create(session);
    }

    /**
     * Update an existing movie session (convenience alias).
     * @param session the movie session to update
     */
    public void updateSession(MovieSession session) {
        this.update(session);
    }

    /**
     * Delete a movie session by ID (convenience method).
     * @param id the ID of the movie session to delete
     */
    public void deleteSession(Long id) {
        try {
            String query = "DELETE FROM movie_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error deleting movie session with id: " + id, e);
        }
    }

}
