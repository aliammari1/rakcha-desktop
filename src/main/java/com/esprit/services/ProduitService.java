package com.esprit.services;

import com.esprit.models.Categorie_Produit;
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
        String req = "INSERT into produit(nom, prix,image,description,quantiteP,id_categorieProduit) values (?, ?,?,?,?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setInt(2, produit.getPrix());
            pst.setBlob(3, produit.getImage());
            pst.setString(4, produit.getDescription());
            pst.setInt(5, produit.getQuantiteP());
            pst.setInt(6, produit.getCategorie().getId_categorie());

            pst.executeUpdate();
            System.out.println("Produit ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Produit> read() {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT  produit.* , categorie_produit.nom_categorie from produit  JOIN categorie_produit  ON produit.id_categorieProduit = categorie_produit.id_categorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
            int i = 0;
            while (rs.next()) {

                 produits.add(new Produit( rs.getInt("id_produit"), rs.getString("nom"), rs.getInt("prix"), rs.getBlob("image"), rs.getString("description"), cs.getCategorie(rs.getInt("id_categorieProduit")),  rs.getInt("quantiteP")));
                System.out.println(produits.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return produits;
    }

    @Override
    public void update(Produit produit) {

        String req = "UPDATE produit p " +
                "INNER JOIN categorie_produit c ON p.id_categorieProduit = c.id_categorie " +
                "SET p.id_categorieProduit = ?, p.nom = ?, p.prix = ?, p.description = ?, p.image = ?, p.quantiteP = ? " +
                "WHERE p.id_produit = ?;";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(7, produit.getId_produit());
            pst.setString(2, produit.getNom());
            pst.setInt(3, produit.getPrix());
            pst.setString(4, produit.getDescription());
            pst.setBlob(5, produit.getImage());
            pst.setInt(6, produit.getQuantiteP());
            pst.setInt(1, produit.getCategorie().getId_categorie());
            pst.executeUpdate();
            System.out.println("produit modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Produit produit) {

        String req = "DELETE from produit where id_produit = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produit.getId_produit());
            pst.executeUpdate();
            System.out.println("produit supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    public Produit getProduitById(int produitId) {
        Produit produit = null;

        String req = "SELECT produit.*, categorie_produit.nom_categorie FROM produit JOIN categorie_produit ON produit.id_categorieProduit = categorie_produit.id_categorie WHERE id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produitId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                CategorieService cs = new CategorieService();

                produit = new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getInt("prix"),
                        rs.getBlob("image"),
                        rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")),
                        rs.getInt("quantiteP")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return produit;
    }



    // Vérifier le stock disponible pour un produit donné
    public boolean verifierStockDisponible(int produitId, int quantiteDemandee) {
        Produit produit = getProduitById(produitId); // Utilisez votre méthode existante pour récupérer le produit
        if (produit != null) {
            return produit.getQuantiteP() >= quantiteDemandee;
        } else {
            return false; // Le produit n'existe pas
        }
    }






}





