package com.esprit.services.films;

import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.FilmYoutubeTrailer;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FilmService implements IService<Film> {

    private static final Logger LOGGER = Logger.getLogger(FilmService.class.getName());
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "title", "duration_min", "description", "release_year"
    };
    static private int filmLastInsertID;
    Connection connection;

    /**
     * Constructs a new FilmService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public FilmService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @return int
     */
    public static int getFilmLastInsertID() {
        return FilmService.filmLastInsertID;
    }

    /**
     * Gets the IMDB URL for a movie by scraping IMDB search results.
     * Uses IMDBScraper utility class for web scraping.
     *
     * @param query The movie name to search for
     * @return The IMDB URL of the movie, or a fallback URL if not found
     */
    public static String getIMDBUrlbyNom(final String query) {
        try {
            FilmService.LOGGER.info("Searching IMDB for: " + query);
            final String imdbUrl = com.esprit.utils.IMDBScraper.getIMDBUrl(query);
            FilmService.LOGGER.info("Found IMDB URL: " + imdbUrl);
            return imdbUrl;
        } catch (final Exception e) {
            FilmService.LOGGER.log(Level.WARNING, "Error getting IMDB URL for: " + query, e);
            // Return a search URL as fallback
            final String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            return "https://www.imdb.com/find/?q=" + encodedQuery;
        }
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param film
     *             the entity to create
     */
    public void create(final Film film) {
        final String req = "insert into movies (title,image_url,duration_min,description,release_year,trailer_url) values (?,?,?,?,?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req,
            PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, film.getTitle());
            statement.setString(2, film.getImageUrl());
            statement.setInt(3, film.getDurationMin());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getReleaseYear());
            statement.setString(6, film.getTrailerUrl());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        FilmService.filmLastInsertID = (int) generatedId;
                        film.setId(generatedId);
                        log.info("Film created with ID: " + generatedId);
                    }
                }
            }
        } catch (final SQLException e) {
            log.error("Error creating film", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    /**
     * Retrieves films with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of films
     */
    public Page<Film> read(PageRequest pageRequest) {
        final List<Film> content = new ArrayList<>();
        final String baseQuery = "SELECT * from movies";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + pageRequest.getSortBy() + ". Using default sorting.");
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (final PreparedStatement pst = this.connection.prepareStatement(paginatedQuery)) {
                final ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    final Film film = buildFilmFromResultSet(rs);
                    content.add(film);
                }
            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated films: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }
    }

    /**
     * Helper method to build Film object from ResultSet.
     *
     * @param rs the ResultSet containing film data
     * @return the Film object
     * @throws SQLException if there's an error reading from ResultSet
     */
    private Film buildFilmFromResultSet(ResultSet rs) throws SQLException {
        return Film.builder()
            .id(rs.getLong("id"))
            .title(rs.getString("title"))
            .imageUrl(rs.getString("image_url"))
            .durationMin(rs.getInt("duration_min"))
            .description(rs.getString("description"))
            .releaseYear(rs.getInt("release_year"))
            .trailerUrl(rs.getString("trailer_url"))
            .build();
    }

    /**
     * Performs sort operation.
     *
     * @return the result of the operation
     */
    public Page<Film> sort(PageRequest pageRequest, final String p) {
        final List<Film> filmArrayList = new ArrayList<>();

        // Validate sort column to prevent SQL injection
        if (!PaginationQueryBuilder.isValidSortColumn(p, ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + p + ". Using default sorting by id.");
            return read(pageRequest); // Return default sorted results
        }

        final String req = "SELECT * from movies ORDER BY " + p;
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                filmArrayList.add(buildFilmFromResultSet(rs));
            }
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return new Page<>(filmArrayList, pageRequest.getPage(), pageRequest.getSize(), filmArrayList.size());
    }

    /**
     * Retrieves the Film value.
     *
     * @return the Film value
     */
    public Film getFilm(final int id) {
        Film film = null;
        final String req = "SELECT * from movies where id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                film = buildFilmFromResultSet(rs);
            }
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return film;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Film film) {
        final String req = "UPDATE movies set title=?,image_url=?,duration_min=?,description=?,release_year=?,trailer_url=? where id=?;";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            log.info("update: " + film);
            statement.setString(1, film.getTitle());
            statement.setString(2, film.getImageUrl());
            statement.setInt(3, film.getDurationMin());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getReleaseYear());
            statement.setString(6, film.getTrailerUrl());
            statement.setLong(7, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error updating film", e);
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
    public void delete(final Film film) {
        final String req = "DELETE from movies where id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error deleting film", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the Film value.
     *
     * @return the Film value
     */
    public Film getFilm(final Long film_id) {
        String query = "SELECT * from movies WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, film_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildFilmFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting film: " + film_id, e);
        }
        return null;
    }

    /**
     * Retrieves the FilmByName value.
     *
     * @return the FilmByName value
     */
    public Film getFilmByName(final String nom_film) {
        Film film = null;
        final String req = "SELECT * from movies where title = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setString(1, nom_film);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                film = buildFilmFromResultSet(rs);
            }
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return film;
    }

    /**
     * Retrieves the TrailerFilm value.
     *
     * @return the TrailerFilm value
     */
    public String getTrailerFilm(final String nomFilm) {
        String s = "";
        try {
            final FilmYoutubeTrailer filmYoutubeTrailer = new FilmYoutubeTrailer();
            s = filmYoutubeTrailer.watchTrailer(nomFilm);
        } catch (final Exception e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return s;
    }

    /**
     * Get all films from the database.
     * @return list of all films
     */
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        String query = "SELECT * FROM movies";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Film film = buildFilmFromResultSet(rs);
                if (film != null) {
                    films.add(film);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all films", e);
        }
        return films;
    }

    @Override
    /**
     * Counts the total number of films in the database.
     *
     * @return the total count of films
     */
    public int count() {
        String query = "SELECT COUNT(*) FROM movies";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting films", e);
        }
        return 0;
    }

    @Override
    /**
     * Retrieves a film by its ID.
     *
     * @param id the ID of the film to retrieve
     * @return the film with the specified ID, or null if not found
     */
    public Film getById(final Long id) {
        return getFilmById(id);
    }

    /**
     * Alias for getFilm() to match naming conventions.
     *
     * @param id the ID of the film to retrieve
     * @return the film with the specified ID, or null if not found
     */
    public Film getFilmById(final Long id) {
        return getFilm(id);
    }

    @Override
    /**
     * Retrieves all films from the database.
     *
     * @return a list of all films
     */
    public List<Film> getAll() {
        return getAllFilms();
    }

    @Override
    /**
     * Searches for films by title or description.
     *
     * @param query the search query
     * @return a list of films matching the search query
     */
    public List<Film> search(final String query) {
        List<Film> films = new ArrayList<>();
        final String req = "SELECT * FROM movies WHERE title LIKE ? OR description LIKE ? ORDER BY title";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final String searchPattern = "%" + query + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Film film = buildFilmFromResultSet(rs);
                if (film != null) {
                    films.add(film);
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching films", e);
        }
        return films;
    }

    @Override
    /**
     * Checks if a film exists by its ID.
     *
     * @param id the ID of the film to check
     * @return true if the film exists, false otherwise
     */
    public boolean exists(final Long id) {
        return getFilmById(id) != null;
    }

    /**
     * Get recommended films for a user based on watch history.
     * @param userId the ID of the user
     * @param limit the maximum number of recommendations
     * @return list of recommended films
     */
    public List<Film> getRecommendationsForUser(Long userId, int limit) {
        // For now, return top-rated films
        List<Film> films = getAll();
        if (films == null) {
            return new ArrayList<>();
        }
        
        return films.stream()
            .limit(Math.max(limit, 0))
            .collect(Collectors.toList());
    }
}

