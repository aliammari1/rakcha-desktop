package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
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
public class FilmCinemaService {
    private static final Logger LOGGER = Logger.getLogger(FilmCinemaService.class.getName());
    private final Connection connection;

    /**
     * Performs FilmCinemaService operation.
     *
     * @return the result of the operation
     */
    public FilmCinemaService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Performs createFilmCinemaAssociation operation.
     *
     * @return the result of the operation
     */
    public void createFilmCinemaAssociation(Film film, List<String> cinemaNames) {
        final String req = "INSERT INTO film_cinema (film_id, cinema_id) VALUES (?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            for (final String cinemaName : cinemaNames) {
                Cinema cinema = new CinemaService().getCinemaByName(cinemaName);
                if (cinema != null) {
                    statement.setLong(1, film.getId());
                    statement.setLong(2, cinema.getId());
                    statement.executeUpdate();
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating film-cinema associations", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the CinemasForFilm value.
     *
     * @return the CinemasForFilm value
     */
    public List<Cinema> getCinemasForFilm(int filmId) {
        final List<Cinema> cinemas = new ArrayList<>();
        final String req = "SELECT c.* FROM cinema c JOIN film_cinema fc ON c.id = fc.cinema_id WHERE fc.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                cinemas.add(Cinema.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .address(rs.getString("address")).logoPath(rs.getString("logo_path"))
                        .status(rs.getString("status")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cinemas for film: " + filmId, e);
        }
        return cinemas;
    }

    /**
     * Retrieves the FilmsForCinema value.
     *
     * @return the FilmsForCinema value
     */
    public List<Film> getFilmsForCinema(Long cinemaId) {
        final List<Film> films = new ArrayList<>();
        final String req = "SELECT f.* FROM films f JOIN film_cinema fc ON f.id = fc.film_id WHERE fc.cinema_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, cinemaId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                films.add(Film.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                        .duration(rs.getTime("duration")).description(rs.getString("description"))
                        .releaseYear(rs.getInt("release_year")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting films for cinema: " + cinemaId, e);
        }
        return films;
    }

    /**
     * Performs updateCinemas operation.
     *
     * @return the result of the operation
     */
    public void updateCinemas(final Film film, final List<String> cinemaNames) {
        // Update film first
        new FilmService().update(film);

        // Delete existing associations
        final String reqDelete = "DELETE FROM film_cinema WHERE film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(reqDelete)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting existing cinema associations", e);
            throw new RuntimeException(e);
        }

        // Create new associations
        createFilmCinemaAssociation(film, cinemaNames);
    }

    /**
     * Retrieves the CinemaNames value.
     *
     * @return the CinemaNames value
     */
    public String getCinemaNames(final Long filmId) {
        final String req = "SELECT GROUP_CONCAT(c.name SEPARATOR ', ') AS cinemaNames FROM cinema c JOIN film_cinema fc ON c.id = fc.cinema_id WHERE fc.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmId);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("cinemaNames");
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cinema names for film: " + filmId, e);
        }
        return "";
    }

    // Legacy method for backward compatibility
    /**
     * Performs readMoviesForCinema operation.
     *
     * @return the result of the operation
     */
    public List<Film> readMoviesForCinema(final Long cinemaId) {
        return getFilmsForCinema(cinemaId);
    }

    /**
     * Performs deleteFilmCinemaAssociation operation.
     *
     * @return the result of the operation
     */
    public void deleteFilmCinemaAssociation(int filmId, int cinemaId) {
        final String req = "DELETE FROM film_cinema WHERE film_id = ? AND cinema_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            statement.setInt(2, cinemaId);
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting film-cinema association", e);
            throw new RuntimeException(e);
        }
    }
}
