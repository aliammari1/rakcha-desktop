package com.esprit.services.films;

import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.FilmYoutubeTrailer;
import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmService implements IService<Film> {
    private static final Logger LOGGER = Logger.getLogger(FilmService.class.getName());
    static private int filmLastInsertID;
    Connection connection;

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
            } while (123 != statusCode);
            // Read the response
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                responseBuilder.append(line);
            }
            final String response = "{" + responseBuilder + "}";
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
            } else {
                FilmService.LOGGER.info("No results found.");
            }
        } catch (final Exception e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return "imdb.com";
    }

    @Override
    public void create(final Film film) {
        final String req = "insert into film (nom,image,duree,description,annederalisation) values (?,?,?,?,?) ";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.executeUpdate();
            final String selectSql = "SELECT LAST_INSERT_ID()";
            final PreparedStatement selectPs = this.connection.prepareStatement(selectSql);
            final ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                FilmService.filmLastInsertID = rs.getInt(1);
                FilmService.LOGGER.info(String.valueOf(FilmService.filmLastInsertID));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Film> read() {
        final List<Film> filmArrayList = new ArrayList<>();
        final String req = "SELECT * from film";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation")));
                // LOGGER.info(filmArrayList.get(i));
                // i++;
            }
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return filmArrayList;
    }

    public List<Film> sort(final String p) {
        final List<Film> filmArrayList = new ArrayList<>();
        final String req = "SELECT * from film ORDER BY %s".formatted(p);
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation")));
                // LOGGER.info(filmArrayList.get(i));
                // i++;
            }
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return filmArrayList;
    }

    public Film getCinema(final int id) {
        Film film = null;
        final String req = "SELECT * from Film where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return film;
    }

    @Override
    public void update(final Film film) {
        final String req = "UPDATE film set nom=?,image=?,duree=?,description=?,annederalisation=? where id=?;";
        try {
            FilmService.LOGGER.info("uodate: " + film);
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.setInt(6, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final Film film) {
        final String req = "DELETE FROM film where id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Film getFilm(final int film_id) {
        Film film = null;
        final String req = "SELECT * from film where id = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, film_id);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return film;
    }

    public Film getFilmByName(final String nom_film) {
        Film film = null;
        final String req = "SELECT * from film where nom = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, nom_film);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (final SQLException e) {
            FilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return film;
    }

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
}
