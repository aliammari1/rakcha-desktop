package com.esprit.services.evenements;

import com.esprit.models.evenements.Feedback;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackEvenementService implements IService<Feedback> {

    private final Connection connection;

    public FeedbackEvenementService() {
        connection = DataSource.getInstance().getConnection();
    }


    @Override
    public void create(Feedback feedback) {
        String req = "INSERT into feedback_evenement(id_evenement,id_user, commentaire) values (?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, feedback.getFeedbackevenement().getId());
            pst.setInt(2, feedback.getId_user());
            pst.setString(3, feedback.getCommentaire());
            pst.executeUpdate();
            System.out.println("Feedback added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Feedback feedback) {

        String req = "UPDATE feedback_evenement set id_evenement = ?, id_user = ?, commentaire = ? where id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(4, feedback.getIDFeedback());
            pst.setInt(1, feedback.getFeedbackevenement().getId());
            pst.setInt(2, feedback.getId_user());
            pst.setString(3, feedback.getCommentaire());
            pst.executeUpdate();
            System.out.println("Feedback Updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Feedback feedback) {

        String req = "DELETE from feedback_evenement where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, feedback.getIDFeedback());
            pst.executeUpdate();
            System.out.println("Feedback Deleted !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Feedback> read() {
        List<Feedback> feedbacks = new ArrayList<>();

        String req = "SELECT feedback_evenement.* , evenement.nom from feedback_evenement JOIN evenement ON feedback_evenement.id_evenement = evenement.id ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            EvenementService es = new EvenementService();
            while (rs.next()) {
                feedbacks.add(new Feedback(rs.getInt("ID"), es.getEvenement(rs.getInt("id_evenement")), rs.getInt("id_user"), rs.getString("Commentaire")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return feedbacks;
    }
}