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
     * Constructs a new FilmService instance.
     * Initializes database connection and creates tables if they don't exist.
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
     * @return int
     */
    public static int getFilmLastInsertID() {
        return FilmService.filmLastInsertID;
    }


    /**
     * @param query
     * @return String
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
     * Helper method to build Film object from ResultSet.
     *
     * @param rs the ResultSet containing film data
     * @return the Film object
     * @throws SQLException if there's an error reading from ResultSet
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
     * Retrieves the Film value.
     *
     * @return the Film value
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
     * Retrieves the Film value.
     *
     * @return the Film value
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
     * Retrieves the FilmByName value.
     *
     * @return the FilmByName value
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
     * Retrieves the TrailerFilm value.
     *
     * @return the TrailerFilm value
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

