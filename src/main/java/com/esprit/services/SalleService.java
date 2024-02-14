package com.esprit.services;

import com.esprit.models.Salle;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalleService implements IService<Salle> {

    private Connection connection;
    public SalleService() { connection = DataSource.getInstance().getConnection(); }
    public void create(Salle salle) {
        String req = "INSERT into salle(id_cinema, nb_places, nb_places_reserve, num_salle, statut) values (?, ?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setInt(3, salle.getNb_places_reserve());
            pst.setInt(4, salle.getNum_salle());
            pst.setString(5, salle.getStatut());
            pst.executeUpdate();
            System.out.println("Salle ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(Salle salle) {
        String req = "UPDATE salle set id_cinema = ?, nb_places = ?, nb_places_reserve = ?, num_salle = ?, statut = ? where id_salle = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(6, salle.getId_salle());
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setInt(3, salle.getNb_places_reserve());
            pst.setInt(4, salle.getNum_salle());
            pst.setString(5, salle.getStatut());
            pst.executeUpdate();
            System.out.println("Salle modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

    public List<Salle> read() {
        List<Salle> salles = new ArrayList<>();

        String req = "SELECT * from salle";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                salles.add(new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"), rs.getInt("nb_places_reserve"), rs.getInt("num_salle"), rs.getString("statut")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return salles;
    }

}
