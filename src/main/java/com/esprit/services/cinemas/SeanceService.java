package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.films.Filmcinema;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeanceService implements IService<Seance> {

    private final Connection connection;

    public SeanceService() {
        connection = DataSource.getInstance().getConnection();
    }

    public void create(Seance seance) {
        String req = "INSERT into seance(id_film, id_salle, HD, HF, date, id_cinema, prix) values (?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, seance.getFilmcinema().getId_film().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getFilmcinema().getId_cinema().getId_cinema());
            pst.setDouble(7, seance.getPrix());
            pst.executeUpdate();
            System.out.println("Seance ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(Seance seance) {
        String req = "UPDATE seance " +
                "JOIN cinema ON seance.id_cinema = cinema.id_cinema " +
                "JOIN film ON seance.id_film = film.id " +
                "JOIN salle ON seance.id_salle = salle.id_salle " +
                "SET seance.id_film = ?, seance.id_salle = ?, seance.HD = ?, seance.HF = ?, seance.date = ?, " +
                "seance.id_cinema = ?, seance.prix = ? " +
                "WHERE seance.id_seance = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, seance.getFilmcinema().getId_film().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getFilmcinema().getId_cinema().getId_cinema());
            pst.setDouble(7, seance.getPrix());
            pst.setInt(8, seance.getId_seance());
            pst.executeUpdate();
            System.out.println("Seance modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void delete(Seance seance) {
        String req = "DELETE from seance where id_seance= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, seance.getId_seance());
            pst.executeUpdate();
            System.out.println("Seance supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Seance> readLoujain(int id) {
        List<Seance> seances = new ArrayList<>();

        String req = "SELECT * FROM seance where id_film = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            CinemaService cs = new CinemaService();
            SalleService ss = new SalleService();
            FilmService fs = new FilmService();
            int i = 0;
            while (rs.next()) {
                seances.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"), rs.getTime("HF"), rs.getDate("date"), rs.getInt("prix"), new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return seances;
    }

    public List<Seance> read() {
        List<Seance> seances = new ArrayList<>();

        String req = "SELECT seance.*, cinema.*, film.*, salle.* " +
                "FROM seance " +
                "JOIN cinema ON seance.id_cinema = cinema.id_cinema " +
                "JOIN film ON seance.id_film = film.id " +
                "JOIN salle ON seance.id_salle = salle.id_salle";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CinemaService cs = new CinemaService();
            SalleService ss = new SalleService();
            FilmService fs = new FilmService();
            int i = 0;
            while (rs.next()) {
                seances.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"), rs.getTime("HF"), rs.getDate("date"), rs.getInt("prix"), new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return seances;
    }

    // Méthode pour récupérer les séances dans une plage de dates spécifiée
    public Map<LocalDate, List<Seance>> getSeancesByDateRangeAndCinema(LocalDate startDate, LocalDate endDate, Cinema cinema) {
        Map<LocalDate, List<Seance>> seancesByDate = new HashMap<>();

        try {
            // Créer la requête SQL pour récupérer les séances dans la plage de dates spécifiée et pour le cinéma donné
            String query = "SELECT * FROM seance WHERE date BETWEEN ? AND ? AND id_cinema = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, Date.valueOf(startDate)); // Utilisez java.sql.Date.valueOf
            statement.setDate(2, Date.valueOf(endDate)); // Utilisez java.sql.Date.valueOf
            statement.setInt(3, cinema.getId_cinema()); // Supposons que getId() renvoie l'identifiant du cinéma

            // Exécuter la requête
            ResultSet rs = statement.executeQuery();
            CinemaService cs = new CinemaService();
            SalleService ss = new SalleService();
            FilmService fs = new FilmService();

            // Parcourir les résultats de la requête et créer des objets Seance
            while (rs.next()) {
                LocalDate seanceDate = rs.getDate("date").toLocalDate(); // Convertir java.sql.Date en java.time.LocalDate
                List<Seance> seancesForDate = seancesByDate.getOrDefault(seanceDate, new ArrayList<>());
                seancesForDate.add(new Seance(rs.getInt("id_seance"), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"), rs.getTime("HF"), seanceDate, rs.getInt("prix"), new Filmcinema(fs.getFilm(rs.getInt("id_film")), cs.getCinema(rs.getInt("id_cinema")))));
                seancesByDate.put(seanceDate, seancesForDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seancesByDate;
    }

}
