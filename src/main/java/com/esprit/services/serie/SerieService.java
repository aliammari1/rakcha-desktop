package com.esprit.services.serie;

import com.esprit.models.serie;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieService implements Iserie<serie>{
    private Connection connection;

    public SerieService() { connection = DataSource.getInstance().getConnection(); }
    public void create(serie serie) {
        String req = "INSERT into serie(nom, resume,directeur,pays) values (?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, serie.getNom());
            pst.setString(2, serie.getResume());
            pst.setString(3, serie.getDirecteur());
            pst.setString(4, serie.getPays());
            pst.executeUpdate();
            System.out.println("Serie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(serie serie) {
        String req = "UPDATE serie set nom = ?, resume = ?,directeur = ?, pays = ? where idserie = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(5, serie.getIdserie());
            pst.setString(1, serie.getNom());
            pst.setString(2, serie.getResume());
            pst.setString(3, serie.getDirecteur());
            pst.setString(4, serie.getPays());
            pst.executeUpdate();
            System.out.println("Serie modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(serie serie) {
        String req = "DELETE from serie where idserie= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, serie.getIdserie());
            pst.executeUpdate();
            System.out.println("Serie supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<serie> read() {
        List<serie> series = new ArrayList<>();

        String req = "SELECT * from serie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                series.add(new serie(rs.getInt("idserie"), rs.getString("nom"), rs.getString("resume"),rs.getString("directeur"),rs.getString("pays")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return series;
    }
}



