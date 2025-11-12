package com.esprit.services.films;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.FilmYoutubeTrailer;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
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
public class FilmService implements IService<Film> {
    private static final Logger LOGGER = Logger.getLogger(FilmService.class.getName());
    static private int filmLastInsertID;
    Connection connection;

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
            "id", "name", "duration", "description", "release_year"
    }
;

    /**
     * Initialize the FilmService by establishing a database connection and ensuring required schema exists.
     *
     * <p>Creates the following tables if they do not already exist: {@code film_categories}, {@code actors},
     * {@code films}, {@code film_category}, {@code film_actor}, {@code film_comments}, and {@code film_ratings}.</p>
     */
    public FilmService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        TableCreator tableCreator = new TableCreator(connection);

        // Create film categories table
        tableCreator.createTableIfNotExists("film_categories", """
                    CREATE TABLE film_categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT NOT NULL
                    )
                """);

        // Create actors table
        tableCreator.createTableIfNotExists("actors", """
                    CREATE TABLE actors (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        image TEXT NOT NULL,
                        biography TEXT NOT NULL
                    )
                """);

        // Create films table
        tableCreator.createTableIfNotExists("films", """
                    CREATE TABLE films (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        image TEXT,
                        duration TIME NOT NULL,
                        description TEXT NOT NULL,
                        release_year INT NOT NULL,
                        isBookmarked BOOLEAN NOT NULL DEFAULT FALSE
                    )
                """);

        // Create film-category junction table
        tableCreator.createTableIfNotExists("film_category", """
                    CREATE TABLE film_category (
                        film_id BIGINT NOT NULL,
                        category_id BIGINT NOT NULL,
                        PRIMARY KEY (film_id, category_id)
                    )
                """);

        // Create film-actor junction table
        tableCreator.createTableIfNotExists("film_actor", """
                    CREATE TABLE film_actor (
                        film_id BIGINT NOT NULL,
                        actor_id BIGINT NOT NULL,
                        PRIMARY KEY (film_id, actor_id)
                    )
                """);

        // Create film comments table
        tableCreator.createTableIfNotExists("film_comments", """
                    CREATE TABLE film_comments (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        comment VARCHAR(255) NOT NULL,
                        user_id BIGINT NOT NULL,
                        film_id BIGINT NOT NULL
                    )
                """);

        // Create film ratings table
        tableCreator.createTableIfNotExists("film_ratings", """
                    CREATE TABLE film_ratings (
                        film_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        rating INT,
                        PRIMARY KEY (film_id, user_id)
                    )
                """);
    }


    /**
     * Retrieve the last inserted film ID.
     *
     * @return the ID of the most recently created film, or 0 if no film has been inserted yet
     */
    public static int getFilmLastInsertID() {
        return FilmService.filmLastInsertID;
    }


    /**
     * Retrieves the IMDb URL for the first search result matching the given query.
     *
     * @param query the search string (e.g., a film title) used to find IMDb results
     * @return the IMDb URL of the first match, or the fallback string "imdb.com" if no result is found or an error occurs
     */
    public static String getIMDBUrlbyNom(final String query) {
        try {
            final String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            final String scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query="
                    + encodedQuery;
            FilmService.LOGGER.info(scriptUrl);
            // Send the request
            final URL url = new URL(scriptUrl);
            int statusCode = 403;
            HttpURLConnection conn = null;
            do {
                FilmService.LOGGER.info("Code=" + statusCode);
                // Send the request
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                statusCode = conn.getInputStream().read();
                FilmService.LOGGER.info("Status Code: " + conn.getResponseCode());
            }
 while (123 != statusCode);
            // Read the response
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                responseBuilder.append(line);
            }

            final String response = "{" + responseBuilder + "}
";
            reader.close();
            // Parse the JSON response
            FilmService.LOGGER.info(response);
            final JSONObject jsonResponse = new JSONObject(response);
            final JSONArray results = jsonResponse.getJSONArray("results");
            // Extract the IMDb URL of the first result
            if (0 < results.length()) {
                final JSONObject firstResult = results.getJSONObject(0);
                final String imdbUrl = firstResult.getString("imdb");
                FilmService.LOGGER.info("IMDb URL of the first result: " + imdbUrl);
                return imdbUrl;
            }
 else {
                FilmService.LOGGER.info("No results found.");
            }

        }
 catch (final Exception e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return "imdb.com";
    }


    /**
     * Insert the given Film into the database and record its generated primary key.
     *
     * @param film the Film to persist; its generated ID is stored in {@code FilmService.filmLastInsertID} when available
     * @throws RuntimeException if a database error occurs while inserting the film
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Film film) {
        final String req = "insert into films (name,image,duration,description,release_year) values (?,?,?,?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, film.getName());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuration());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getReleaseYear());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        FilmService.filmLastInsertID = generatedKeys.getInt(1);
                        log.info("Film created with ID: " + FilmService.filmLastInsertID);
                    }

                }

            }

        }
 catch (final SQLException e) {
            log.error("Error creating film", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Retrieve a page of films according to the given pagination and optional sorting parameters.
     *
     * The method returns a Page containing the films for the requested page along with pagination
     * metadata. If a database error occurs, an empty Page is returned with total elements set to 0.
     *
     * @param pageRequest pagination parameters (page index, page size, and optional sort column/direction)
     * @return a Page of Film objects for the requested page and pagination metadata; an empty Page with total 0 on error
     */
    @Override
    /**
     * Retrieves films with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of films
     */
    public Page<Film> read(PageRequest pageRequest) {
        final List<Film> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM films";

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

        }
 catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving paginated films: " + e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }


    /**
     * Constructs a Film from the current row of the given ResultSet.
     *
     * @param rs the ResultSet positioned at a film row
     * @return the Film populated with values from the ResultSet row
     * @throws SQLException if reading any column from the ResultSet fails
     */
    private Film buildFilmFromResultSet(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .image(rs.getString("image"))
                .duration(rs.getTime("duration"))
                .description(rs.getString("description"))
                .releaseYear(rs.getInt("release_year"))
                .build();
    }


    /**
     * Returns a page of films sorted by the specified column.
     *
     * The method validates the provided sort column against allowed columns; if the column is not allowed,
     * it returns results using the service's default sorting. On SQL errors it logs the failure and returns
     * a page containing any films successfully read before the error.
     *
     * @param pageRequest pagination parameters (page index and page size)
     * @param p the column name to sort by; must be one of the allowed sort columns
     * @return a Page of Film objects sorted by the specified column (or by default ordering if `p` is invalid)
     */
    public Page<Film> sort(PageRequest pageRequest, final String p) {
        final List<Film> filmArrayList = new ArrayList<>();

        // Validate sort column to prevent SQL injection
        if (!PaginationQueryBuilder.isValidSortColumn(p, ALLOWED_SORT_COLUMNS)) {
            LOGGER.warning("Invalid sort column: " + p + ". Using default sorting by id.");
            return read(pageRequest); // Return default sorted results
        }


        final String req = "SELECT * from films ORDER BY " + p;
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                filmArrayList.add(buildFilmFromResultSet(rs));
            }

        }
 catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return new Page<>(filmArrayList, pageRequest.getPage(), pageRequest.getSize(), filmArrayList.size());
    }


    /**
     * Fetches the film with the given id from the database.
     *
     * @param id the film's primary key
     * @return the Film with the specified id, or {@code null} if no film was found
     */
    public Film getFilm(final int id) {
        Film film = null;
        final String req = "SELECT * from films where id = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                film = buildFilmFromResultSet(rs);
            }

        }
 catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return film;
    }


    /**
     * Update the database record for the given film using its id.
     *
     * The film's name, image, duration, description, and release year are written to the row identified by film.getId().
     *
     * @param film the Film whose id selects the row to update; its name, image, duration, description, and releaseYear are applied
     */
    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Film film) {
        final String req = "UPDATE films set name=?,image=?,duration=?,description=?,release_year=? where id=?;";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            log.info("update: " + film);
            statement.setString(1, film.getName());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuration());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getReleaseYear());
            statement.setLong(6, film.getId());
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            log.error("Error updating film", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Remove the database record for the given film.
     *
     * Deletes the row from the `films` table identified by the film's id.
     *
     * @param film the Film whose persistent record should be removed
     * @throws RuntimeException if a database error prevents deletion
     */
    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Film film) {
        final String req = "DELETE FROM films where id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        }
 catch (final SQLException e) {
            log.error("Error deleting film", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Retrieves the film with the specified database ID.
     *
     * @param film_id the film's database ID
     * @return the Film for the specified ID, or null if no matching record exists
     */
    public Film getFilm(final Long film_id) {
        String query = "SELECT * FROM films WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, film_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildFilmFromResultSet(rs);
                }

            }

        }
 catch (SQLException e) {
            log.error("Error getting film: " + film_id, e);
        }

        return null;
    }


    /**
     * Finds a film by its exact name.
     *
     * @param nom_film the exact film name to search for
     * @return the Film with the given name, or {@code null} if no matching film exists
     */
    public Film getFilmByName(final String nom_film) {
        Film film = null;
        final String req = "SELECT * from films where name = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setString(1, nom_film);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                film = buildFilmFromResultSet(rs);
            }

        }
 catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return film;
    }


    /**
     * Retrieve the YouTube trailer URL for a film by its name.
     *
     * @param nomFilm the film name to search for a trailer
     * @return the trailer URL if available, or an empty string when no trailer is found or an error occurs
     */
    public String getTrailerFilm(final String nomFilm) {
        String s = "";
        try {
            final FilmYoutubeTrailer filmYoutubeTrailer = new FilmYoutubeTrailer();
            s = filmYoutubeTrailer.watchTrailer(nomFilm);
        }
 catch (final Exception e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return s;
    }

}
