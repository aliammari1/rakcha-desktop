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
        String req = "INSERT into seance(id_film, id_salle, HD, HF, date) values (?, ?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, seance.getId_film());
            pst.setInt(2, seance.getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
            pst.executeUpdate();
            System.out.println("Seance ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(Seance seance) {
        String req = "UPDATE seance set id_film = ?, id_salle = ?, HD = ?, HF = ?, date = ? where id_seance = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(6, seance.getId_seance());
            pst.setInt(1, seance.getId_film());
            pst.setInt(2, seance.getId_salle());
            pst.setTime(3, seance.getHD());
            pst.setTime(4, seance.getHF());
            pst.setDate(5, seance.getDate());
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

        String req = "SELECT * from seance";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                seances.add(new Seance(rs.getInt("id_seance"), rs.getInt("id_film"), rs.getInt("id_salle"), rs.getTime("HD"), rs.getTime("HF"), rs.getDate("date")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return seances;
    }
}
