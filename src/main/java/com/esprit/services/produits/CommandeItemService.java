package com.esprit.services.produits;

import com.esprit.models.produits.Commande;
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

        String req = "INSERT into commandeitem(id_produit,quantity,idCommande) values (?, ?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(2, commandeItem.getQuantity());
            pst.setInt(1, commandeItem.getProduit().getId_produit());
            pst.setInt(3, commandeItem.getCommande().getIdCommande());

            pst.executeUpdate();
            System.out.println("panier remplit !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<CommandeItem> read() {
        List<CommandeItem> commandeitem = new ArrayList<>();
        String req = "SELECT * from commandeitem";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs =new CommandeService();

            while (rs.next()) {
               commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),ps.getProduitById(rs.getInt("id_produit")),cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return commandeitem;
    }

    public List<CommandeItem> readCommandeItem(int idCommande){
        List<CommandeItem> commandeitem = new ArrayList<>();
        String req = "Select * FROM commandeitem where idCommande=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs =new CommandeService();
            while (rs.next()) {
                commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),ps.getProduitById(rs.getInt("id_produit")),cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return commandeitem;
    }








    @Override
    public void update(CommandeItem commandeItem) {



    }



    @Override
    public void delete(CommandeItem commandeItem) {



    }



}