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
    private final Connection connection;
    public CommandeItemService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param commandeItem
     */
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
            e.printStackTrace();
        }
    }
    /** 
     * @return List<CommandeItem>
     */
    @Override
    public List<CommandeItem> read() {
        List<CommandeItem> commandeitem = new ArrayList<>();
        String req = "SELECT * from commandeitem";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeitem;
    }
    public List<CommandeItem> readCommandeItem(int idCommande) {
        List<CommandeItem> commandeitem = new ArrayList<>();
        String req = "Select * FROM commandeitem where idCommande=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeitem.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeitem;
    }
    @Override
    public void update(CommandeItem commandeItem) {
    }
    @Override
    public void delete(CommandeItem commandeItem) {
    }
    public List<CommandeItem> getCommandeItemsByCommande(int idCommande) {
        List<CommandeItem> commandeItems = new ArrayList<>();
        String req = "SELECT * FROM commandeitem WHERE idCommande = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idCommande);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs = new CommandeService();
            while (rs.next()) {
                commandeItems.add(new CommandeItem(
                        rs.getInt("idCommandeItem"),
                        rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")),
                        cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeItems;
    }
    public int getTotalQuantityByCategoryAndDate(String nomCategorie, String formattedDate) {
        int totalQuantity = 0;
        String req = "SELECT SUM(ci.quantity) AS totalQuantity " +
                "FROM commandeitem ci " +
                "JOIN produit p ON ci.id_produit = p.id_produit " +
                "JOIN categorie_produit cp ON p.id_categorieProduit = cp.id_categorie " +
                "JOIN commande c ON ci.idCommande = c.idCommande " +
                "WHERE cp.nom_categorie = ? AND DATE(c.dateCommande) = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, nomCategorie);
            pst.setString(2, formattedDate);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                totalQuantity = rs.getInt("totalQuantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalQuantity;
    }
    public List<CommandeItem> getItemsByCommande(int idCommande) {
        List<CommandeItem> commandeItems = new ArrayList<>();
        String req = "SELECT * FROM commandeitem WHERE idCommande = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idCommande);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs = new CommandeService();
            while (rs.next()) {
                Produit produit = ps.getProduitById(rs.getInt("id_produit"));
                Commande commande = cs.getCommandeByID(rs.getInt("idCommande"));
                CommandeItem commandeItem = new CommandeItem(
                        rs.getInt("idCommandeItem"),
                        rs.getInt("quantity"),
                        produit,
                        commande);
                commandeItems.add(commandeItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandeItems;
    }
    public List<CommandeItem> getavergeratingSorted() {
        String req = "SELECT id_produit, quantity FROM commandeitem WHERE statu LIKE 'payee' GROUP BY id_produit ORDER BY quantity DESC";
        List<CommandeItem> aver = new ArrayList<>();
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            ProduitService ps = new ProduitService();
            CommandeService cs = new CommandeService();
            while (rs.next()) {
                aver.add(new CommandeItem(rs.getInt("idCommandeItem"), rs.getInt("quantity"),
                        ps.getProduitById(rs.getInt("id_produit")), cs.getCommandeByID(rs.getInt("idCommande"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aver;
    }
}
