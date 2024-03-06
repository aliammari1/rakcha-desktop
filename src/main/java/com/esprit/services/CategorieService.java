package com.esprit.services;

import com.esprit.models.Categorie_evenement;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategorieService  implements IService<Categorie_evenement> {

    private Connection connection;

    public CategorieService() {
        connection = DataSource.getInstance().getConnection();
    }


    @Override
    public void add(Categorie_evenement categorie) {

        String req = "INSERT into categorie_evenement(nom_categorie, description) values (?, ?);";
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
    public void update(Categorie_evenement categorie) {
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
    public void delete(Categorie_evenement categorie) {
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
    public List<Categorie_evenement> show() {

        List<Categorie_evenement> categories = new ArrayList<>();

        String req = "SELECT * from categorie_evenement";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie_evenement(rs.getInt("ID"), rs.getString("Nom_Categorie"), rs.getString("Description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    public Categorie_evenement getCategorie(int categorie_id) {

        Categorie_evenement category = null;

        String req = "SELECT * from categorie_evenement where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_evenement(rs.getInt("ID"), rs.getString("Nom_Categorie"), rs.getString("Description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return category;
    }
    public Categorie_evenement getCategorieByNom(String categorie_nom) {

        Categorie_evenement category = null;

        String req = "SELECT * from categorie_evenement where nom_categorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie_nom);
            ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_evenement(rs.getInt("id"), rs.getString("nom_categorie"), rs.getString("description")) ;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return category;
    }

    // Ajoutez cette méthode pour récupérer tous les noms de catégories
    public List<String> getAllCategoriesNames() {
        List<String> categorieNames = new ArrayList<>();

        // Remplacez "getAllCategories()" par la méthode réelle qui récupère toutes les catégories
        List<Categorie_evenement> categories = show();

        // Ajoutez les noms de catégories à la liste
        for (Categorie_evenement categorieEvenement : categories) {
            categorieNames.add(categorieEvenement.getNom_categorie());
        }

        return categorieNames;
    }

}
