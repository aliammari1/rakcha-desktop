package com.esprit.services;

import com.esprit.models.Cinema;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaService implements IService<Cinema> {

    private Connection connection;
    public CinemaService() { connection = DataSource.getInstance().getConnection(); }
    public void create(Cinema cinema) {
        String req = "INSERT into cinema(nom, adresse, responsable, image) values (?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setString(3, cinema.getResponsable());
            pst.setString(4, cinema.getImage());
            pst.executeUpdate();
            System.out.println("Cinéma ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(Cinema cinema) {
        String req = "UPDATE cinema set nom = ?, adresse = ?, responsable = ?, image = ? where id_cinema = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(5, cinema.getId_cinema());
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setString(3, cinema.getResponsable());
            pst.setString(4, cinema.getImage());
            pst.executeUpdate();
            System.out.println("Cinéma modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(Cinema cinema) {
        String req = "DELETE from cinema where id_cinema= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, cinema.getId_cinema());
            pst.executeUpdate();
            System.out.println("Cinema supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Cinema> read() {
        List<Cinema> cinemas = new ArrayList<>();

        String req = "SELECT * from cinema";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"), rs.getString("responsable"), rs.getString("image")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cinemas;
    }
}
