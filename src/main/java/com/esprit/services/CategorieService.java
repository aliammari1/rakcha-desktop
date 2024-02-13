package com.esprit.services;

import com.esprit.models.Categorie;
import com.esprit.models.Produit;
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
    public void create(Categorie categorie) {
        String req = "INSERT into categorie(nom, description) values (?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie.getNom());

            pst.setString(2, categorie.getDescription());

            pst.executeUpdate();
            System.out.println("Categorie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Categorie> read() {

        List<Categorie> categories = new ArrayList<>();

        String req = "SELECT * from categorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("id_categoriProduit") ,rs.getString("nom"),rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    @Override
    public void update(Categorie categorie) {
        String req = "UPDATE categorie set nom = ?,  description = ? ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie.getNom());
            pst.setString(2, categorie.getDescription());

            pst.executeUpdate();
            System.out.println("Personne modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Categorie categorie) {
        String req = "INSERT into categorie(nom, description) values (?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie.getNom());

            pst.setString(2, categorie.getDescription());

            pst.executeUpdate();
            System.out.println("Categorie ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
