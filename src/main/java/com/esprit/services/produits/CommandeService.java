package com.esprit.services.produits;

import com.esprit.models.produits.Commande;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeService implements IService<Commande> {

    private final Connection connection;


    public CommandeService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Commande commande) {
        String req = "INSERT into commande(dateCommande,statu , idClient,num_telephone,adresse) values ( ?,?,?,?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);

            //pst.setInt(1, commande.getCommandeItem().());
            pst.setDate(1, (Date) commande.getDateCommande());
            pst.setString(2, commande.getStatu() != null ? commande.getStatu() : "En_Cours");
            pst.setInt(3, commande.getIdClient().getId());
            pst.setInt(4, commande.getNum_telephone());
            pst.setString(5, commande.getAdresse());


            pst.executeUpdate();
            System.out.println("commande remplit !");
        } catch (SQLException e) {
            System.out.println("commande nom remplit ");
        }


    }

    public int createcommande(Commande commande) throws SQLException {
        int commandeId = 0;
        String req = "INSERT into commande(dateCommande,statu , idClient,num_telephone,adresse) values ( ?,?,?,?,?)  ;";
        PreparedStatement pst = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        pst.setDate(1, (Date) commande.getDateCommande());
        pst.setString(2, commande.getStatu() != null ? commande.getStatu() : "En_Cours");
        pst.setInt(3, commande.getIdClient().getId());
        pst.setInt(4, commande.getNum_telephone());
        pst.setString(5, commande.getAdresse());


        pst.executeUpdate();
        ResultSet rs = pst.getGeneratedKeys();

        if (rs.next()) {
            commandeId = rs.getInt(1);
        }
        return commandeId;


    }


    @Override
    public List<Commande> read() {
        CommandeItemService commandeItemService = new CommandeItemService();
        List<Commande> commande = new ArrayList<>();
        String req = "SELECT * from commande ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CommandeItemService cs = new CommandeItemService();
            UserService us = new UserService();


            while (rs.next()) {
                Commande c1 = new Commande(rs.getInt("idCommande"), rs.getDate("dateCommande"), rs.getString("statu"), (Client) us.getUserById(rs.getInt("idClient")), rs.getInt("num_telephone"), rs.getString("adresse"));
                c1.setCommandeItem(commandeItemService.readCommandeItem(c1.getIdCommande()));
                commande.add(c1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commande;
    }


    public List<Commande> readClient() {
        CommandeItemService commandeItemService = new CommandeItemService();
        List<Commande> commande = new ArrayList<>();
        String req = "SELECT * from commande WHERE idClient=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CommandeItemService cs = new CommandeItemService();
            UserService us = new UserService();


            while (rs.next()) {
                Commande c1 = new Commande(rs.getInt("idCommande"), rs.getDate("dateCommande"), rs.getString("statuCommande"), (Client) us.getUserById(rs.getInt("idClient")), rs.getInt("num_telephone"), rs.getString("adresse"));
                c1.setCommandeItem(commandeItemService.readCommandeItem(c1.getIdCommande()));
                commande.add(c1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commande;
    }


    @Override
    public void update(Commande commande) {

        String req = "UPDATE commande c" +
                " JOIN commandeitem ci ON c.idCommande = ci.idCommande " +
                "JOIN users cl ON c.idClient = cl.id " +
                "SET c.dateCommande = ?, c.statu = ?, c.num_telephone=? , c.adresse=? " +
                " WHERE c.idCommande = ? ";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setDate(1, (Date) commande.getDateCommande());
            pst.setString(2, commande.getStatu());
            pst.setInt(3, commande.getNum_telephone());
            pst.setString(4, commande.getAdresse());
            pst.setInt(5, commande.getIdCommande());

            System.out.println("commande modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

    }


    public Commande getCommandeByID(int idCommande) throws SQLException {
        UserService usersService = new UserService();
        Commande commande = new Commande();
        String req = "SELECT * from commande  WHERE idCommande=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, idCommande);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            commande.setIdCommande(rs.getInt("idCommande"));
            commande.setDateCommande(rs.getDate("dateCommande"));
            commande.setIdClient((Client) usersService.getUserById(rs.getInt("idClient")));
            commande.setAdresse(rs.getString("adresse"));
            commande.setNum_telephone(rs.getInt("num_telephone"));

        }
        return commande;
    }

    public List<Commande> getCommandesPayees() {
        List<Commande> commandesPayees = new ArrayList<>();
        String req = "SELECT * FROM commande WHERE etat like 'payée'";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            UserService usersService = new UserService();
            CommandeItemService commandeItemService = new CommandeItemService();

            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("idCommande"),
                        rs.getDate("dateCommande"),
                        rs.getString("statu"),
                        (Client) usersService.getUserById(rs.getInt("idClient")),
                        rs.getInt("num_telephone"),
                        rs.getString("adresse")
                );
                commande.setCommandeItem(commandeItemService.readCommandeItem(commande.getIdCommande()));
                commandesPayees.add(commande);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des commandes payées : " + e.getMessage());
        }
        return commandesPayees;
    }


    // Compter le nombre d'achats d'un produit donné
    public Map<Integer, Integer> getTop3ProduitsAchetes() {
        String req = "SELECT ci.idProduit, SUM(ci.quantite) AS nombreAchats FROM commandeitem ci JOIN commande c ON ci.idCommande = c.idCommande WHERE c.etat = 'PAYEE' GROUP BY ci.idProduit ORDER BY nombreAchats DESC LIMIT 3";
        Map<Integer, Integer> produitsAchats = new HashMap<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int idProduit = resultSet.getInt("idProduit");
                int nombreAchats = resultSet.getInt("nombreAchats");
                produitsAchats.put(idProduit, nombreAchats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return produitsAchats;
    }


}
