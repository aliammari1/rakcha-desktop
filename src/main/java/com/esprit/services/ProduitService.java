package com.esprit.services;

import com.esprit.models.Produit;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService  implements IService<Produit>{

    private Connection connection;

    public ProduitService() {
        connection = DataSource.getInstance().getConnection();
    }



    @Override
    public void create(Produit produit) {
        String req = "INSERT into produit(nom, prix,image,description) values (?, ?,?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getPrix());
            pst.setString(3, produit.getImage());
            pst.setString(4, produit.getDescription());

            pst.executeUpdate();
            System.out.println("Produit ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Produit> read() {
        List<Produit> produits = new ArrayList<>();

        String req = "SELECT * from produit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(rs.getInt("id_produit") ,rs.getString("nom"), rs.getString("prix"),rs.getString("description"),rs.getString("image")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return produits;
    }

    @Override
    public void update(Produit produit) {
        String req = "UPDATE personne set nom = ?, prix = ? , description = ? , image = ? where id_produit = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(5, produit.getId_produit());
            pst.setString(1, produit.getNom());
            pst.setString(2, produit.getPrix());
            pst.setString(3, produit.getDescription());
            pst.setString(4, produit.getImage());
            pst.executeUpdate();
            System.out.println("Personne modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Produit produit) {

        String req = "DELETE from personne where id_produit = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produit.getId_produit());
            pst.executeUpdate();
            System.out.println("produit supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
