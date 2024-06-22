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
public class FilmService implements IService<Film> {
    static private int filmLastInsertID;
    Connection connection;
    public FilmService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @return int
     */
    public static int getFilmLastInsertID() {
        return filmLastInsertID;
    }
    /** 
     * @param query
     * @return String
     */
    public static String getIMDBUrlbyNom(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query="
                    + encodedQuery;
            System.out.println(scriptUrl);
            // Send the request
            URL url = new URL(scriptUrl);
            int statusCode = 403;
            HttpURLConnection conn = null;
            do {
                System.out.println("Code=" + statusCode);
                // Send the request
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                statusCode = conn.getInputStream().read();
                System.out.println("Status Code: " + conn.getResponseCode());
            } while (statusCode != 123);
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            String response = "{" + responseBuilder + "}";
            reader.close();
            // Parse the JSON response
            System.out.println(response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("results");
            // Extract the IMDb URL of the first result
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                String imdbUrl = firstResult.getString("imdb");
                System.out.println("IMDb URL of the first result: " + imdbUrl);
                return (imdbUrl);
            } else {
                System.out.println("No results found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "imdb.com";
    }
    @Override
    public void create(Film film) {
        String req = "insert into film (nom,image,duree,description,annederalisation) values (?,?,?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.executeUpdate();
            String selectSql = "SELECT LAST_INSERT_ID()";
            PreparedStatement selectPs = connection.prepareStatement(selectSql);
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                filmLastInsertID = rs.getInt(1);
                System.out.println(filmLastInsertID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Film> read() {
        List<Film> filmArrayList = new ArrayList<>();
        String req = "SELECT * from film";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation")));
                // System.out.println(filmArrayList.get(i));
                // i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filmArrayList;
    }
    public List<Film> sort(String p) {
        List<Film> filmArrayList = new ArrayList<>();
        String req = "SELECT * from film ORDER BY " + p;
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation")));
                // System.out.println(filmArrayList.get(i));
                // i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filmArrayList;
    }
    public Film getCinema(int id) {
        Film film = null;
        String req = "SELECT * from Film where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return film;
    }
    @Override
    public void update(Film film) {
        String req = "UPDATE film set nom=?,image=?,duree=?,description=?,annederalisation=? where id=?;";
        try {
            System.out.println("uodate: " + film);
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setString(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.setInt(6, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void delete(Film film) {
        String req = "DELETE FROM film where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Film getFilm(int film_id) {
        Film film = null;
        String req = "SELECT * from film where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, film_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return film;
    }
    public Film getFilmByName(String nom_film) {
        Film film = null;
        String req = "SELECT * from film where nom = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, nom_film);
            ResultSet rs = pst.executeQuery();
            rs.next();
            film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                    rs.getString("description"), rs.getInt("annederalisation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return film;
    }
    public String getTrailerFilm(String nomFilm) {
        String s = "";
        try {
            FilmYoutubeTrailer filmYoutubeTrailer = new FilmYoutubeTrailer();
            s = filmYoutubeTrailer.watchTrailer(nomFilm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
