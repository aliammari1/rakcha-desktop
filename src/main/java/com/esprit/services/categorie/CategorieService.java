package com.esprit.services.categorie;

import com.esprit.models.categorie;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategorieService implements IService<categorie> {

    private Connection connection;
    public CategorieService() { connection = DataSource.getInstance().getConnection(); }
    public void create(categorie categorie) {
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
    public void update(categorie categorie) {
        String req = "UPDATE categorie set nom = ?, description = ? where idcategorie = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(3, categorie.getIdcategorie());
            pst.setString(1, categorie.getNom());
            pst.setString(2, categorie.getDescription());
            pst.executeUpdate();
            System.out.println("Categorie modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(categorie categorie) {
        String req = "DELETE from categorie where idcategorie= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie.getIdcategorie());
            pst.executeUpdate();
            System.out.println("Categorie supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<categorie> read() {
        List<categorie> categories = new ArrayList<>();

        String req = "SELECT * from categorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new categorie(rs.getInt("idcategorie"), rs.getString("nom"), rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }


   // public Object getClass(int idcategorie) {
     //   return null;
   // }

    //public categorie getCategorie(int i) {
      //  return null;
    //}
    public Object getClass(int idcategorie) {
        return getCategorie(idcategorie);
    }

    public categorie getCategorie(int idcategorie) {
        categorie categorie = null;

        String req = "SELECT * FROM categorie WHERE idcategorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idcategorie);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                categorie = new categorie(rs.getInt("idcategorie"), rs.getString("nom"), rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categorie;
    }
}

