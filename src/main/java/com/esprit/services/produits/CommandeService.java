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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandeService implements IService<Commande> {
    private static final Logger LOGGER = Logger.getLogger(CommandeService.class.getName());
    private final Connection connection;

    public CommandeService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param commande
     */
    @Override
    public void create(final Commande commande) {
        final String req = "INSERT into commande(dateCommande,statu , idClient,num_telephone,adresse) values ( ?,?,?,?,?)  ;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            // pst.setInt(1, commande.getCommandeItem().());
            pst.setDate(1, (Date) commande.getDateCommande());
            pst.setString(2, null != commande.getStatu() ? commande.getStatu() : "en cours");
            pst.setInt(3, commande.getIdClient().getId());
            pst.setInt(4, commande.getNum_telephone());
            pst.setString(5, commande.getAdresse());
            pst.executeUpdate();
            CommandeService.LOGGER.info("commande remplit !");
        } catch (final SQLException e) {
            CommandeService.LOGGER.info("commande nom remplit ");
        }
    }

    /**
     * @param commande
     * @return int
     * @throws SQLException
     */
    public int createcommande(final Commande commande) throws SQLException {
        int commandeId = 0;
        final String req = "INSERT into commande(dateCommande,statu , idClient,num_telephone,adresse) values ( ?,?,?,?,?)  ;";
        final PreparedStatement pst = this.connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
        pst.setDate(1, (Date) commande.getDateCommande());
        pst.setString(2, null != commande.getStatu() ? commande.getStatu() : "en cours");
        pst.setInt(3, commande.getIdClient().getId());
        pst.setInt(4, commande.getNum_telephone());
        pst.setString(5, commande.getAdresse());
        pst.executeUpdate();
        final ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            commandeId = rs.getInt(1);
        }
        return commandeId;
    }

    @Override
    public List<Commande> read() {
        final CommandeItemService commandeItemService = new CommandeItemService();
        final List<Commande> commande = new ArrayList<>();
        final String req = "SELECT * from commande ";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final CommandeItemService cs = new CommandeItemService();
            final UserService us = new UserService();
            while (rs.next()) {
                final Commande c1 = new Commande(rs.getInt("idCommande"), rs.getDate("dateCommande"), rs.getString("statu"),
                        (Client) us.getUserById(rs.getInt("idClient")), rs.getInt("num_telephone"),
                        rs.getString("adresse"));
                c1.setCommandeItem(commandeItemService.readCommandeItem(c1.getIdCommande()));
                commande.add(c1);
            }
        } catch (final SQLException e) {
            CommandeService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commande;
    }

    public List<Commande> readClient() {
        final CommandeItemService commandeItemService = new CommandeItemService();
        final List<Commande> commande = new ArrayList<>();
        final String req = "SELECT * from commande WHERE idClient=?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final CommandeItemService cs = new CommandeItemService();
            final UserService us = new UserService();
            while (rs.next()) {
                final Commande c1 = new Commande(rs.getInt("idCommande"), rs.getDate("dateCommande"),
                        rs.getString("statuCommande"), (Client) us.getUserById(rs.getInt("idClient")),
                        rs.getInt("num_telephone"), rs.getString("adresse"));
                c1.setCommandeItem(commandeItemService.readCommandeItem(c1.getIdCommande()));
                commande.add(c1);
            }
        } catch (final SQLException e) {
            CommandeService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commande;
    }

    @Override
    public void update(final Commande commande) {
        final String req = """
                UPDATE commande c\
                 JOIN commandeitem ci ON c.idCommande = ci.idCommande \
                JOIN users cl ON c.idClient = cl.id \
                SET c.dateCommande = ?, c.statu = ?, c.num_telephone=? , c.adresse=? \
                 WHERE c.idCommande = ? \
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setDate(1, (Date) commande.getDateCommande());
            pst.setString(2, commande.getStatu());
            pst.setInt(3, commande.getNum_telephone());
            pst.setString(4, commande.getAdresse());
            pst.setInt(5, commande.getIdCommande());
            CommandeService.LOGGER.info("commande modifiée !");
        } catch (final SQLException e) {
            CommandeService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Commande commande) {
        final String req = "DELETE from commande where idCommande = ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, commande.getIdCommande());
            pst.executeUpdate();
            CommandeService.LOGGER.info("commande supprmiée !");
        } catch (final SQLException e) {
            CommandeService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Commande getCommandeByID(final int idCommande) throws SQLException {
        final UserService usersService = new UserService();
        final Commande commande = new Commande();
        final String req = "SELECT * from commande  WHERE idCommande=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, idCommande);
        final ResultSet rs = ps.executeQuery();
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
        final List<Commande> commandesPayees = new ArrayList<>();
        final String req = "SELECT * FROM commande WHERE statu like 'payee'";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final UserService usersService = new UserService();
            final CommandeItemService commandeItemService = new CommandeItemService();
            while (rs.next()) {
                final Commande commande = new Commande(
                        rs.getInt("idCommande"),
                        rs.getDate("dateCommande"),
                        rs.getString("statu"),
                        (Client) usersService.getUserById(rs.getInt("idClient")),
                        rs.getInt("num_telephone"),
                        rs.getString("adresse"));
                commande.setCommandeItem(commandeItemService.readCommandeItem(commande.getIdCommande()));
                commandesPayees.add(commande);
            }
        } catch (final SQLException e) {
            CommandeService.LOGGER.info("Erreur lors de la récupération des commandes payees : " + e.getMessage());
        }
        return commandesPayees;
    }

    // Compter le nombre d'achats d'un produit donné
    public Map<Integer, Integer> getTop3ProduitsAchetes() {
        final String req = "SELECT ci.id_produit, SUM(ci.quantity) AS nombreAchats FROM commandeitem ci JOIN commande c ON ci.idCommande = c.idCommande WHERE c.statu = 'payee' GROUP BY ci.id_produit ORDER BY nombreAchats DESC LIMIT 3";
        final Map<Integer, Integer> produitsAchats = new HashMap<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int idProduit = resultSet.getInt("idProduit");
                final int nombreAchats = resultSet.getInt("nombreAchats");
                produitsAchats.put(idProduit, nombreAchats);
            }
        } catch (final Exception e) {
            CommandeService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return produitsAchats;
    }
}
