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
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanierService implements IService<Panier> {
    private static final Logger LOGGER = Logger.getLogger(PanierService.class.getName());
    private final Connection connection;

    public PanierService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param panier
     */
    @Override
    public void create(final Panier panier) {
        final String req = "INSERT into panier(id_produit,quantite,idClient) values (?, ?,?)  ;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.setInt(2, panier.getQuantity());
            pst.setInt(3, panier.getUser().getId());
            pst.executeUpdate();
            PanierService.LOGGER.info("panier remplit !");
        } catch (final SQLException e) {
            PanierService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Panier>
     */
    @Override
    public List<Panier> read() {
        return null;
    }

    public List<Panier> readUserPanier(final int iduser) throws SQLException {
        final UserService usersService = new UserService();
        final ProduitService produitService = new ProduitService();
        final List<Panier> paniers = new ArrayList<>();
        final String req = "SELECT * from panier  WHERE idClient=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, iduser);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final Panier panier = new Panier();
            panier.setUser(usersService.getUserById(iduser));
            panier.setProduit(produitService.getProduitById(rs.getInt("id_produit")));
            panier.setQuantity(rs.getInt("quantite"));
            paniers.add(panier);
        }
        return paniers;
    }

    @Override
    public void update(final Panier panier) {
        final String req = """
                UPDATE panier p \
                INNER JOIN produit pro ON pro.id_produit = p.id_produit \
                INNER JOIN users u ON u.id = p.idClient \
                SET p.id_produit = ?, p.quantite = ?, p.idClient = ? \
                WHERE p.idpanier = ?;\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(4, panier.getIdPanier());
            pst.setInt(3, panier.getUser().getId());
            pst.setInt(2, panier.getQuantity());
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.executeUpdate();
            PanierService.LOGGER.info("panier modifiée !");
        } catch (final SQLException e) {
            PanierService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Panier panier) {
        final String req = "DELETE from panier where id_produit = ? and idClient=?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, panier.getProduit().getId_produit());
            pst.setInt(2, panier.getUser().getId());
            pst.executeUpdate();
            PanierService.LOGGER.info("panier supprmiée !");
        } catch (final SQLException e) {
            PanierService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
