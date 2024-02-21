package com.esprit.services;

import com.esprit.models.Categorie;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategorieService  implements IService<Categorie> {

    private Connection connection;

    public CategorieService() {
        connection = DataSource.getInstance().getConnection();
    }


    @Override
    public void add(Categorie categorie) {

        String req = "INSERT into categorie_evenement(id, nom_categorie, description) values (?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie.getId_categorie());
            pst.setString(2, categorie.getNom_categorie());
            pst.setString(3, categorie.getDescription());
            pst.executeUpdate();
            System.out.println("Categorie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Categorie categorie) {
        String req = "UPDATE categorie_evenement set nom_categorie = ?,  description = ? where id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(3, categorie.getId_categorie());
            pst.setString(1, categorie.getNom_categorie());
            pst.setString(2, categorie.getDescription());
            pst.executeUpdate();
            System.out.println("categorie modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Categorie categorie) {
        String req = "DELETE from categorie_evenement where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie.getId_categorie());
            pst.executeUpdate();
            System.out.println("categorie supprimée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Categorie> show() {

        List<Categorie> categories = new ArrayList<>();

        String req = "SELECT * from categorie_evenement";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("ID"), rs.getString("Nom_Categorie"), rs.getString("Description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }
}
