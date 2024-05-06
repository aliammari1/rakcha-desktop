package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Salle;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalleService implements IService<Salle> {

    private final Connection connection;

    public SalleService() {
        connection = DataSource.getInstance().getConnection();
    }

    public void create(Salle salle) {
        String req = "INSERT into salle(id_cinema, nb_places, nom_salle) values (?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setString(3, salle.getNom_salle());
            pst.executeUpdate();
            System.out.println("Salle ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Salle salle) {
        String req = "UPDATE salle set id_cinema = ?, nb_places = ?, nom_salle = ? where id_salle = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(4, salle.getId_salle());
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setString(3, salle.getNom_salle());
            pst.executeUpdate();
            System.out.println("Salle modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Salle salle) {
        String req = "DELETE from salle where id_salle= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, salle.getId_salle());
            pst.executeUpdate();
            System.out.println("Salle supprmiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Salle> read() {
        List<Salle> salles = new ArrayList<>();

        String req = "SELECT * from salle";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                salles.add(new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"), rs.getString("nom_salle")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }

    public Salle getSalle(int salle_id) {

        Salle salle = null;

        String req = "SELECT * from salle where id_salle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, salle_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"), rs.getString("nom_salle"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salle;
    }

    public Salle getSalleByName(String nom_salle) {

        Salle salle = null;

        String req = "SELECT * from salle where nom_salle = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, nom_salle);
            ResultSet rs = pst.executeQuery();
            rs.next();
            salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"), rs.getString("nom_salle"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salle;
    }

    public List<Salle> readRoomsForCinema(int cinemaId) {
        List<Salle> roomsForCinema = new ArrayList<>();
        String query = "SELECT * FROM salle WHERE id_cinema = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, cinemaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Salle salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"), rs.getString("nom_salle"));
                roomsForCinema.add(salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomsForCinema;
    }

}
