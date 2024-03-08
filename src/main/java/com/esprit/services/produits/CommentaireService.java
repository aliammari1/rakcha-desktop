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

public class CommentaireService implements IService<Commentaire> {


    private Connection connection;
    public CommentaireService() {
        connection = DataSource.getInstance().getConnection();
    }



    @Override
    public void create(Commentaire commentaire) {

        String req = "INSERT into commentaire(idClient,commentaire ) values (?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commentaire.getClient().getId() );
            pst.setString(2, commentaire.getCommentaire() );



            pst.executeUpdate();
            System.out.println("commentaire ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }




    }

    @Override
    public List<Commentaire> read() {
        List<Commentaire> commentaire = new ArrayList<>();

        String req = "SELECT * from commentaire";
        try {
            PreparedStatement pst = connection.prepareStatement(req);


            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Commentaire(rs.getInt("idcommentaire"),(Client) new UserService().getUserById(rs.getInt("idClient")), rs.getString("commentaire")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

        String req = "SELECT * FROM commentaire WHERE idClient = ? LIMIT 1";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, clientId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                commentaire = new Commentaire(
                        rs.getInt("idcommentaire"),
                        (Client) new UserService().getUserById(rs.getInt("idClient")),
                        rs.getString("commentaire")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return commentaire;
    }

}
