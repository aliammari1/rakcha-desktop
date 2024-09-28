package com.esprit.services.produits;

import com.esprit.models.produits.Commentaire;
import com.esprit.models.users.Client;
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

public class CommentaireService implements IService<Commentaire> {
    private static final Logger LOGGER = Logger.getLogger(CommentaireService.class.getName());
    private final Connection connection;

    public CommentaireService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param commentaire
     */
    @Override
    public void create(final Commentaire commentaire) {
        final String req = "INSERT into commentaire_produit (id_client_id, commentaire, id_produit) values (?, ?, ?);";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setInt(1, commentaire.getClient().getId());
            pst.setString(2, commentaire.getCommentaire());
            pst.setInt(3, commentaire.getProduit().getId_produit());
            pst.executeUpdate();
            CommentaireService.LOGGER.info("Commentaire ajouté !");
        } catch (final SQLException e) {
            CommentaireService.LOGGER.info("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }

    /**
     * @return List<Commentaire>
     */
    @Override
    public List<Commentaire> read() {
        final List<Commentaire> commentaire = new ArrayList<>();
        final String req = "SELECT * from commentaire_produit";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ProduitService produitsevice = new ProduitService();
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Commentaire(rs.getInt("id"),
                        (Client) new UserService().getUserById(rs.getInt("id_client_id")), rs.getString("commentaire"),
                        produitsevice.getProduitById(rs.getInt("id_produit"))));
            }
        } catch (final SQLException e) {
            CommentaireService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commentaire;
    }

    @Override
    public void update(final Commentaire commentaire) {
    }

    @Override
    public void delete(final Commentaire commentaire) {
    }

    public Commentaire readByClientId(final int clientId) {
        Commentaire commentaire = null;
        final String req = "SELECT * FROM commentaire_produit WHERE id_client_id = ? LIMIT 1";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, clientId);
            final ResultSet rs = pst.executeQuery();
            final ProduitService produitsevice = new ProduitService();
            if (rs.next()) {
                commentaire = new Commentaire(
                        rs.getInt("idcommentaire"),
                        (Client) new UserService().getUserById(rs.getInt("idClient")),
                        rs.getString("commentaire"),
                        produitsevice.getProduitById(rs.getInt("idProduit")));
            }
        } catch (final SQLException e) {
            CommentaireService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commentaire;
    }

    public List<Commentaire> getCommentsByProduitId(final int produitId) {
        final List<Commentaire> commentaires = new ArrayList<>();
        final String req = "SELECT * FROM commentaire_produit join produit WHERE commentaire_produit.id_produit = produit.id_produit AND id_produit = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, produitId);
            final ResultSet rs = pst.executeQuery();
            final ProduitService produitService = new ProduitService();
            while (rs.next()) {
                final Commentaire commentaire = new Commentaire(
                        rs.getInt("idcommentaire"),
                        (Client) new UserService().getUserById(rs.getInt("idClient")),
                        rs.getString("commentaire"),
                        produitService.getProduitById(rs.getInt("idProduit")));
                commentaires.add(commentaire);
            }
        } catch (final SQLException e) {
            CommentaireService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commentaires;
    }
}
