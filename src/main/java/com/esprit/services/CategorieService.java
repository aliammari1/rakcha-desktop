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
    public void create(Categorie categorie) {
        String req = "INSERT into categorie_produit(nom_categorie, description) values (?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie.getNom_categorie());

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

        String req = "SELECT * from categorie_produit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("id_categorie"), rs.getString("nom_categorie"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    @Override
    public void update(Categorie categorie) {
        String req = "UPDATE categorie_produit set nom_categorie = ?,  description = ? where id_categorie=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie.getNom_categorie());
            pst.setString(2, categorie.getDescription());
            pst.setInt(3, categorie.getId_categorie());

            pst.executeUpdate();
            System.out.println("categorie modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Categorie categorie) {
        String req = "DELETE from categorie_produit where id_categorie = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie.getId_categorie());
            pst.executeUpdate();
            System.out.println("categorie supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public Categorie getCategorie(int categorie_id) {

        Categorie category = null;

        String req = "SELECT * from categorie_produit where id_categorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
                category = new Categorie(rs.getInt("id_categorie"), rs.getString("nom_categorie"), rs.getString("description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return category;
    }

    public Categorie getCategorieByNom(String categorie_nom) {

        Categorie category = null;

        String req = "SELECT * from categorie_produit where nom_categorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie_nom);
            ResultSet rs = pst.executeQuery();
            rs.next();
                category = new Categorie(rs.getInt("id_categorie"), rs.getString("nom_categorie"), rs.getString("description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return category;
    }

}
