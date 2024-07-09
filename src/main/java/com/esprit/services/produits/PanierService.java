package com.esprit.services.produits;
import com.esprit.models.produits.Panier;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class PanierService implements IService<Panier> {
    private final Connection connection;
    public PanierService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param panier
     */
    @Override
    public void create(Panier panier) {
        String req = "INSERT into panier(id_produit,quantite,idClient) values (?, ?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.setInt(2, panier.getQuantity());
            pst.setInt(3, panier.getUser().getId());
            pst.executeUpdate();
            System.out.println("panier remplit !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
     * @return List<Panier>
     */
    @Override
    public List<Panier> read() {
        return null;
    }
    public List<Panier> readUserPanier(int iduser) throws SQLException {
        UserService usersService = new UserService();
        ProduitService produitService = new ProduitService();
        List<Panier> paniers = new ArrayList<>();
        String req = "SELECT * from panier  WHERE idClient=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, iduser);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Panier panier = new Panier();
            panier.setUser(usersService.getUserById(iduser));
            panier.setProduit(produitService.getProduitById(rs.getInt("id_produit")));
            panier.setQuantity(rs.getInt("quantite"));
            paniers.add(panier);
        }
        return paniers;
    }
    @Override
    public void update(Panier panier) {
        String req = "UPDATE panier p " +
            "INNER JOIN produit pro ON pro.id_produit = p.id_produit " +
            "INNER JOIN users u ON u.id = p.idClient " +
            "SET p.id_produit = ?, p.quantite = ?, p.idClient = ? " +
            "WHERE p.idpanier = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(4, panier.getIdPanier());
            pst.setInt(3, panier.getUser().getId());
            pst.setInt(2, panier.getQuantity());
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.executeUpdate();
            System.out.println("panier modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Panier panier) {
        String req = "DELETE from panier where id_produit = ? and idClient=?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.setInt(2, panier.getUser().getId());
            pst.executeUpdate();
            System.out.println("panier supprmiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
