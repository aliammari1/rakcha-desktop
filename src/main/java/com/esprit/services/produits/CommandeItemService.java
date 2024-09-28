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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandeItemService implements IService<CommandeItem> {
    private static final Logger LOGGER = Logger.getLogger(CommandeItemService.class.getName());
    private final Connection connection;

    public CommandeItemService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param commandeItem
     */
    @Override
    public void create(final CommandeItem commandeItem) {
        final String req = "INSERT into commandeitem(id_produit,quantity,idCommande) values (?, ?,?)  ;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(2, commandeItem.getQuantity());
            pst.setInt(1, commandeItem.getProduit().getId_produit());
            pst.setInt(3, commandeItem.getCommande().getIdCommande());
            pst.executeUpdate();
            CommandeItemService.LOGGER.info("panier remplit !");
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<CommandeItem>
     */
    @Override
    public List<CommandeItem> read() {
        final List<CommandeItem> commandeitem = new ArrayList<>();
        final String req = "SELECT * from commandeitem";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final ProduitService ps = new ProduitService();
            final CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commandeitem;
    }

    public List<CommandeItem> readCommandeItem(final int idCommande) {
        final List<CommandeItem> commandeitem = new ArrayList<>();
        final String req = "Select * FROM commandeitem where idCommande=?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, idCommande);
            final ResultSet rs = pst.executeQuery();
            final ProduitService ps = new ProduitService();
            final CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commandeitem;
    }

    @Override
    public void update(final CommandeItem commandeItem) {
    }

    @Override
    public void delete(final CommandeItem commandeItem) {
    }

    public List<CommandeItem> getCommandeItemsByCommande(final int idCommande) {
        final List<CommandeItem> commandeItems = new ArrayList<>();
        final String req = "SELECT * FROM commandeitem WHERE idCommande = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, idCommande);
            final ResultSet rs = pst.executeQuery();
            final ProduitService ps = new ProduitService();
            final CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeItems.add(new CommandeItem(
                        rs.getInt("idCommandeItem"),
                        rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")),
                        cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commandeItems;
    }

    public int getTotalQuantityByCategoryAndDate(final String nomCategorie, final String formattedDate) {
        int totalQuantity = 0;
        final String req = """
                SELECT SUM(ci.quantity) AS totalQuantity \
                FROM commandeitem ci \
                JOIN produit p ON ci.id_produit = p.id_produit \
                JOIN categorie_produit cp ON p.id_categorieProduit = cp.id_categorie \
                JOIN commande c ON ci.idCommande = c.idCommande \
                WHERE cp.nom_categorie = ? AND DATE(c.dateCommande) = ?\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, nomCategorie);
            pst.setString(2, formattedDate);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                totalQuantity = rs.getInt("totalQuantity");
            }
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return totalQuantity;
    }

    public List<CommandeItem> getItemsByCommande(final int idCommande) {
        final List<CommandeItem> commandeItems = new ArrayList<>();
        final String req = "SELECT * FROM commandeitem WHERE idCommande = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, idCommande);
            final ResultSet rs = pst.executeQuery();
            final ProduitService ps = new ProduitService();
            final CommandeService cs = new CommandeService();
            while (rs.next()) {
                final Produit produit = ps.getProduitById(rs.getInt("id_produit"));
                final Commande commande = cs.getCommandeByID(rs.getInt("idCommande"));
                final CommandeItem commandeItem = new CommandeItem(
                        rs.getInt("idCommandeItem"),
                        rs.getInt("quantity"),
                        produit,
                        commande);
                commandeItems.add(commandeItem);
            }
        } catch (final SQLException e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commandeItems;
    }

    public List<CommandeItem> getavergeratingSorted() {
        final String req = "SELECT id_produit, quantity FROM commandeitem WHERE statu LIKE 'payee' GROUP BY id_produit ORDER BY quantity DESC";
        final List<CommandeItem> aver = new ArrayList<>();
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final ProduitService ps = new ProduitService();
            final CommandeService cs = new CommandeService();
            while (rs.next()) {
                aver.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (final Exception e) {
            CommandeItemService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return aver;
    }
}
