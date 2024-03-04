package com.esprit.services.produits;

import com.esprit.models.produits.CommandeItem;
import com.esprit.models.produits.Produit;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandeItemService implements IService<CommandeItem> {

    private Connection connection;


    public CommandeItemService() {
        connection = DataSource.getInstance().getConnection();
    }
    @Override
    public void create(CommandeItem commandeItem) {

        String req = "INSERT into commandeitem(id_produit,quantity) values (?, ?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(2, commandeItem.getQuantity());
            pst.setInt(1, commandeItem.getProduit().getId_produit());

            pst.executeUpdate();
            System.out.println("panier remplit !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<CommandeItem> read() {
        List<CommandeItem> panier = new ArrayList<>();
        String req = "SELECT  commandeitem.* , commandeitem.nom from commandeitem  JOIN commandeitem  ON commandeitem.id_produit = commandeitem.id_produit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            int i = 0;
            while (rs.next()) {

                panier.add(new CommandeItem( rs.getInt("idCommandeItem"),  ps.getProduitById (rs.getInt("id_produit")), rs.getInt("quantity")));
                System.out.println(panier.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return panier;
    }

    @Override
    public void update(CommandeItem commandeItem) {

        String req = "UPDATE commandeitem c " +
                "INNER JOIN produit p ON p.id_Produit = c.id_produit " +
                "SET c.id_Produit = ?, c.quantite = ? " +
                "WHERE c.idCommandeItem = ?;";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(3, commandeItem.getIdCommandeItem());
            pst.setInt(2, commandeItem.getQuantity());
            pst.setInt(1, commandeItem.getProduit().getId_produit());
            pst.executeUpdate();
            System.out.println("panier modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    @Override
    public void delete(CommandeItem commandeItem) {

        String req = "DELETE from commandeitem where idCommandeItem = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commandeItem.getIdCommandeItem());
            pst.executeUpdate();
            System.out.println("panier supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    public CommandeItem getCommandeItem(int id_commandeitem) {
        CommandeItem commandeitem = null;

        String req = "SELECT commandeitem.*, produit.nom FROM commandeitem JOIN produit ON produit.id_produit = commandeitem.id_produit WHERE idCommandeItem = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id_commandeitem);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                ProduitService ps = new ProduitService();

                commandeitem = new CommandeItem(
                        rs.getInt("idCommandeItem"),
                        ps.getProduitById (rs.getInt("id_produit")),
                        rs.getInt("quantity")

                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return commandeitem;
    }


    public int calculerTotalQuantite(List<CommandeItem> panier) {
        int totalQuantite = 1;

        for (CommandeItem item : panier) {
            totalQuantite += item.getQuantity();
        }

        return totalQuantite;
    }

    // Méthode pour calculer le total de la quantité par produit
    public int calculerQuantiteProduitDansPanier(int idProduit) {
        String req = "SELECT SUM(quantity) AS totalQuantite FROM commandeitem WHERE id_produit = ?";
        int totalQuantite = 0;

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idProduit);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                totalQuantite = rs.getInt("totalQuantite");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalQuantite;
    }

    public double calculerSommeTotaleProduit(int idProduit) {
        String req = "SELECT SUM(quantity) AS totalQuantite FROM commandeitem WHERE id_produit = ?";
        double prixProduit = 0;

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idProduit);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int totalQuantite = rs.getInt("totalQuantite");

                // Récupérer le prix du produit depuis la base de données ou ailleurs
                ProduitService produitService = new ProduitService();
                Produit produit = produitService.getProduitById(idProduit);

                // Calculer la somme totale du prix du produit
                prixProduit = totalQuantite * produit.getPrix();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return prixProduit;
    }


}
