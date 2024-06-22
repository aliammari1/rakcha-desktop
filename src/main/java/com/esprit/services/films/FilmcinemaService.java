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
public class FilmcinemaService implements IService<Filmcinema> {
    Connection connection;
    public FilmcinemaService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param filmcinema
     */
    @Override
    public void create(Filmcinema filmcinema) {
        String req = "INSERT INTO film_cinema (film_id,cinema_id) VALUES (?,?)";
        try {
            Cinema cinema = filmcinema.getId_cinema();
            String[] cinemanames = cinema.getNom().split(", ");
            PreparedStatement statement = connection.prepareStatement(req);
            for (String cinemaname : cinemanames) {
                System.out.println(cinemaname);
                statement.setInt(1, FilmService.getFilmLastInsertID());
                statement.setInt(2, new CinemaService().getCinemaByName(cinemaname).getId_cinema());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /** 
     * @return List<Filmcinema>
     */
    @Override
    public List<Filmcinema> read() {
        List<Filmcinema> actorfilmArrayList = new ArrayList<>();
        String req = "SELECT film.*,cinema.*, GROUP_CONCAT(cinema.nom SEPARATOR ', ') AS cinemaNames from film_cinema JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema JOIN film on film_cinema.film_id = film.id GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                actorfilmArrayList.add(new Filmcinema(
                        new Film(rs.getInt("film.id"), rs.getString("film.nom"), rs.getString("image"),
                                rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation")),
                        new Cinema(rs.getInt("cinema.id_cinema"), rs.getString("cinemaNames"),
                                rs.getString("cinema.adresse"),
                                (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                                rs.getString("cinema.logo"), rs.getString("cinema.Statut"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actorfilmArrayList;
    }
    @Override
    public void update(Filmcinema filmcinema) {
    }
    @Override
    public void delete(Filmcinema filmcinema) {
    }
    public void updatecinemas(Film film, List<String> cinemaNames) {
        FilmService filmService = new FilmService();
        CinemaService cinemaService = new CinemaService();
        filmService.update(film);
        System.out.println("filmcinema............: " + film);
        String reqDelete = "DELETE FROM film_cinema WHERE film_id = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String req = "INSERT INTO film_cinema (film_id, cinema_id) VALUES (?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (String cinemaname : cinemaNames) {
                statement.setInt(2, cinemaService.getCinemaByName(cinemaname).getId_cinema());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getcinemaNames(int id) {
        String s = "";
        String req = "SELECT GROUP_CONCAT(cinema.nom SEPARATOR ', ') AS cinemaNames from film_cinema JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema JOIN film on film_cinema.film_id = film.id where film.id = ? GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            s = rs.next() ? rs.getString("cinemaNames") : "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }
    public List<Film> readMoviesForCinema(int cinemaId) {
        List<Film> moviesForCinema = new ArrayList<>();
        String query = "SELECT film.*,cinema.* from film_cinema  JOIN cinema  ON film_cinema.cinema_id = cinema.id_cinema  JOIN film on film_cinema.film_id = film.id where cinema.id_cinema = ? GROUP BY film.id;";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, cinemaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Film film = new Film(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getTime("duree"),
                        rs.getString("description"), rs.getInt("annederalisation"));
                moviesForCinema.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moviesForCinema;
    }
}
