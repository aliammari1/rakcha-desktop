package com.esprit.services.cinemas;

import com.esprit.models.cinemas.CommentaireCinema;
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

public class CommentaireCinemaService implements IService<CommentaireCinema> {

    private final Connection connection;

    public CommentaireCinemaService() {
        connection = DataSource.getInstance().getConnection();
    }


    @Override
    public void create(CommentaireCinema commentaire) {

        String req = "INSERT into commentairecinema(idCinema,idClient,commentaire,Sentiment ) values (?,?,?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, commentaire.getCinema().getId_cinema());
            pst.setInt(2, commentaire.getClient().getId());
            pst.setString(3, commentaire.getCommentaire());
            pst.setString(4, commentaire.getSentiment());


            pst.executeUpdate();
            System.out.println("commentaire ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CommentaireCinema> read() {
        List<CommentaireCinema> commentaire = new ArrayList<>();

        String req = "SELECT * FROM commentairecinema";
        try {
            PreparedStatement pst = connection.prepareStatement(req);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new CommentaireCinema(
                        rs.getInt("id"),
                        new CinemaService().getCinema(rs.getInt("idcinema")),
                        (Client) new UserService().getUserById(rs.getInt("idclient")),
                        rs.getString("commentaire"), rs.getString("Sentiment")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentaire;
    }


    @Override
    public void update(CommentaireCinema commentaire) {


    }

    @Override
    public void delete(CommentaireCinema commentaire) {

    }


//    public Commentaire readByClientId(int clientId) {
//        Commentaire commentaire = null;
//
//        String req = "SELECT * FROM commentairecinema WHERE idClient = ? LIMIT 1";
//        try {
//            PreparedStatement pst = connection.prepareStatement(req);
//            pst.setInt(1, clientId);
//
//            ResultSet rs = pst.executeQuery();
//            if (rs.next()) {
//                commentaire = new Commentaire(
//                        rs.getInt("idcommentaire"),
//                        (Client) new UserService().getUserById(rs.getInt("idClient")),
//                        rs.getString("commentaire")
//                );
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return commentaire;
//    }

}
