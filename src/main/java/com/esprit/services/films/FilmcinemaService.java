package com.esprit.services.films;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcinema;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.services.IService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmcinemaService implements IService<Filmcinema> {
    private static final Logger LOGGER = Logger.getLogger(FilmcinemaService.class.getName());
    Connection connection;

    public FilmcinemaService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param filmcinema
     */
    @Override
    public void create(final Filmcinema filmcinema) {
        final String req = "INSERT INTO film_cinema (film_id,cinema_id) VALUES (?,?)";
        try {
            final Cinema cinema = filmcinema.getId_cinema();
            final String[] cinemanames = cinema.getNom().split(", ");
            final PreparedStatement statement = this.connection.prepareStatement(req);
            for (final String cinemaname : cinemanames) {
                FilmcinemaService.LOGGER.info(cinemaname);
                statement.setInt(1, FilmService.getFilmLastInsertID());
                statement.setInt(2, new CinemaService().getCinemaByName(cinemaname).getId_cinema());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Filmcinema>
     */
    @Override
    public List<Filmcinema> read() {
        final List<Filmcinema> actorfilmArrayList = new ArrayList<>();
        final String req = "SELECT film.*,cinema.*, GROUP_CONCAT(cinema.nom SEPARATOR ', ') AS cinemaNames from film_cinema JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema JOIN film on film_cinema.film_id = film.id GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                actorfilmArrayList.add(new Filmcinema(
                        new Film(rs.getInt("film.id"), rs.getString("film.nom"), rs.getString("image"),
                                rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation")),
                        new Cinema(rs.getInt("cinema.id_cinema"), rs.getString("cinemaNames"),
                                rs.getString("cinema.adresse"),
                                (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                                rs.getString("cinema.logo"), rs.getString("cinema.Statut"))));
            }
        } catch (final SQLException e) {
            FilmcinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return actorfilmArrayList;
    }

    @Override
    public void update(final Filmcinema filmcinema) {
    }

    @Override
    public void delete(final Filmcinema filmcinema) {
    }

    public void updatecinemas(final Film film, final List<String> cinemaNames) {
        final FilmService filmService = new FilmService();
        final CinemaService cinemaService = new CinemaService();
        filmService.update(film);
        FilmcinemaService.LOGGER.info("filmcinema............: " + film);
        final String reqDelete = "DELETE FROM film_cinema WHERE film_id = ?;";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            FilmcinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        final String req = "INSERT INTO film_cinema (film_id, cinema_id) VALUES (?,?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (final String cinemaname : cinemaNames) {
                statement.setInt(2, cinemaService.getCinemaByName(cinemaname).getId_cinema());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getcinemaNames(final int id) {
        String s = "";
        final String req = "SELECT GROUP_CONCAT(cinema.nom SEPARATOR ', ') AS cinemaNames from film_cinema JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema JOIN film on film_cinema.film_id = film.id where film.id = ? GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            s = rs.next() ? rs.getString("cinemaNames") : "";
        } catch (final SQLException e) {
            FilmcinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return s;
    }

    public List<Film> readMoviesForCinema(final int cinemaId) {
        final List<Film> moviesForCinema = new ArrayList<>();
        final String query = "SELECT film.*,cinema.* from film_cinema  JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema  JOIN film on film_cinema.film_id = film.id where cinema.id_cinema = ? GROUP BY film.id;";
        try (final PreparedStatement pst = this.connection.prepareStatement(query)) {
            pst.setInt(1, cinemaId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Film film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                        rs.getString("description"), rs.getInt("annederalisation"));
                moviesForCinema.add(film);
            }
        } catch (final SQLException e) {
            FilmcinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return moviesForCinema;
    }
}
