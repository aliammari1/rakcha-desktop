package com.esprit.services.produits;
import com.esprit.models.produits.Commentaire;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CommentaireService implements IService<Commentaire> {
    private final Connection connection;
    public CommentaireService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param commentaire
     */
    @Override
    public void create(Commentaire commentaire) {
        String req = "INSERT into commentaire_produit (id_client_id, commentaire, id_produit) values (?, ?, ?);";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, commentaire.getClient().getId());
            pst.setString(2, commentaire.getCommentaire());
            pst.setInt(3, commentaire.getProduit().getId_produit());
            pst.executeUpdate();
            System.out.println("Commentaire ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }
    /** 
     * @return List<Commentaire>
     */
    @Override
    public List<Commentaire> read() {
        List<Commentaire> commentaire = new ArrayList<>();
        String req = "SELECT * from commentaire_produit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ProduitService produitsevice = new ProduitService();
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Commentaire(rs.getInt("id"),
                        (Client) new UserService().getUserById(rs.getInt("id_client_id")), rs.getString("commentaire"),
                        produitsevice.getProduitById(rs.getInt("id_produit"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaire;
    }
    @Override
    public void update(Commentaire commentaire) {
    }
    @Override
    public void delete(Commentaire commentaire) {
    }
    public Commentaire readByClientId(int clientId) {
        Commentaire commentaire = null;
        String req = "SELECT * FROM commentaire_produit WHERE id_client_id = ? LIMIT 1";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, clientId);
            ResultSet rs = pst.executeQuery();
            ProduitService produitsevice = new ProduitService();
            if (rs.next()) {
                commentaire = new Commentaire(
                        rs.getInt("idcommentaire"),
                        (Client) new UserService().getUserById(rs.getInt("idClient")),
                        rs.getString("commentaire"),
                        produitsevice.getProduitById(rs.getInt("idProduit")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaire;
    }
    public List<Commentaire> getCommentsByProduitId(int produitId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String req = "SELECT * FROM commentaire_produit join produit WHERE commentaire_produit.id_produit = produit.id_produit AND id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produitId);
            ResultSet rs = pst.executeQuery();
            ProduitService produitService = new ProduitService();
            while (rs.next()) {
                Commentaire commentaire = new Commentaire(
                        rs.getInt("idcommentaire"),
                        (Client) new UserService().getUserById(rs.getInt("idClient")),
                        rs.getString("commentaire"),
                        produitService.getProduitById(rs.getInt("idProduit")));
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaires;
    }
}
