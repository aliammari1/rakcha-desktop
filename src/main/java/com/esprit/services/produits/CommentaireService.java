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


    @Override
    public void create(Commentaire commentaire) {
        String req = "INSERT into commentaireproduit(idClient, commentaire, idProduit, datecommantaire) values (?, ?, ?, ?);";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, commentaire.getClient().getId());
            pst.setString(2, commentaire.getCommentaire());
            pst.setInt(3, commentaire.getProduit().getId_produit());
            pst.setDate(4, (Date) commentaire.getDatecommentaire());

            pst.executeUpdate();
            System.out.println("Commentaire ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }


    @Override
    public List<Commentaire> read() {
        List<Commentaire> commentaire = new ArrayList<>();

        String req = "SELECT * from commentaireproduit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ProduitService produitsevice = new ProduitService();


            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Commentaire(rs.getInt("idcommentaire"), (Client) new UserService().getUserById(rs.getInt("idClient")), rs.getString("commentaire"), produitsevice.getProduitById(rs.getInt("idProduit")), rs.getDate("datecommantaire")));
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


        String req = "SELECT * FROM commentaireproduit WHERE idClient = ? LIMIT 1";
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
                        produitsevice.getProduitById(rs.getInt("idProduit")),
                        rs.getDate("datecommantaire")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentaire;
    }

    public List<Commentaire> getCommentsByProduitId(int produitId) {
        List<Commentaire> commentaires = new ArrayList<>();

        String req = "SELECT * FROM commentaireproduit join produit WHERE commentaireproduit.idProduit = produit.id_produit AND idProduit = ?";
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
                        produitService.getProduitById(rs.getInt("idProduit")),
                        rs.getDate("datecommantaire")
                );
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentaires;
    }

}
