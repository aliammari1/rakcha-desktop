package com.esprit.services.produits;

import com.esprit.models.produits.Commande;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService implements IService<Commande> {

    private Connection connection;


    public CommandeService() {
        connection = DataSource.getInstance().getConnection();
    }
    @Override
    public void create(Commande commande) {
        String req = "INSERT into commande(idCommandeItem,dateCommande,etat , idClient) values (?, ?,?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setInt(1, commande.getCommandeItem().getIdCommandeItem());
            pst.setDate(2, (Date) commande.getDateCommande());
            pst.setString(3, commande.getEtatCommande());
            pst.setInt(2, commande.getIdClient().getId());

            pst.executeUpdate();
            System.out.println("commande remplit !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    @Override
    public List<Commande> read() {
        List<Commande> commande = new ArrayList<>();
        String req = "SELECT  commande.* ,  from commandeitem  JOIN commandeitem  ON commande.idCommandeitem = commandeitem.idCommandeItem JOIN users ON commande.idClient=users.id ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CommandeItemService cs = new CommandeItemService();
            UsersService us = new UsersService();

            int i = 0;
            while (rs.next()) {

                commande.add(new Commande( rs.getInt("idCommande"),rs.getDate("dateCommande") , rs.getString("etatCommande"),us.getUsers(rs.getInt("idClient")) , cs.getCommandeItem (rs.getInt("idCommandeItem"))));
                System.out.println(commande.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return commande;
    }

    @Override
    public void update(Commande commande) {

        String req = "UPDATE commande c"+
       " JOIN commande_item ci ON c.id_commande = ci.id_commande"+
        "JOIN client cl ON c.id_client = cl.id_client"+
        "SET c.date_commande = ?, c.etat = ?, " +
       " WHERE c.id_commande = ? ";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(7, (Date) commande.getDateCommande());
            pst.setString(2, commande.getEtatCommande());
            pst.setInt(3, commande.getIdCommande());

            System.out.println("commande modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Commande commande) {

        String req = "DELETE from commande where idCommande = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commande.getIdCommande());
            pst.executeUpdate();
            System.out.println("commande supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


}
