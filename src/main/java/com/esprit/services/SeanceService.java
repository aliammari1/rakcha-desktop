package com.esprit.services;

import com.esprit.models.Salle;
import com.esprit.models.Seance;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeanceService implements IService<Seance> {

    private Connection connection;
    public SeanceService() { connection = DataSource.getInstance().getConnection(); }
    public void create(Seance seance) {
        String req = "INSERT into seance(id_film, id_salle, HD, HF, date, id_cinema, prix) values (?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, seance.getFilm().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getCinema().getId_cinema());
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
            pst.setInt(1, seance.getFilm().getId());
            pst.setInt(2, seance.getSalle().getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.setInt(6, seance.getCinema().getId_cinema());
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
                seances.add(new Seance(rs.getInt("id_seance"), fs.getFilm(rs.getInt("id_film")), ss.getSalle(rs.getInt("id_salle")), rs.getTime("HD"), rs.getTime("HF"), rs.getDate("date"), cs.getCinema(rs.getInt("id_cinema")), rs.getInt("prix")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return seances;
    }
}
