package com.esprit.services.evenements;

import com.esprit.models.evenements.Participation;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService implements IService<Participation> {

    private final Connection connection;

    public ParticipationService() {
        connection = DataSource.getInstance().getConnection();
    }


    @Override
    public void create(Participation participation) {
        String req = "INSERT into participation_evenement(id_evenement, id_user, quantity) values (?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, participation.getEvent().getId());
            pst.setInt(2, participation.getUserevenement());
            pst.setInt(3, participation.getQuantity());
            pst.executeUpdate();
            System.out.println("Participation added !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Participation participation) {

        String req = "UPDATE participation_evenement set id_evenement = ?, id_user = ?, quantity = ? where id_participation = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(4, participation.getId_participation());
            pst.setInt(1, participation.getEvent().getId());
            pst.setInt(2, participation.getUserevenement());
            pst.setInt(3, participation.getQuantity());
            pst.executeUpdate();
            System.out.println("Participation Updated !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(Participation participation) {

        String req = "DELETE from participation_evenement where id_participation = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, participation.getId_participation());
            pst.executeUpdate();
            System.out.println("Participation Deleted !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public List<Participation> read() {
        List<Participation> participations = new ArrayList<>();

        String req = "SELECT participation_evenement.* , evenement.nom from participation_evenement JOIN evenement ON participation_evenement.id_evenement = evenement.id ";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            EvenementService es = new EvenementService();
            UserService us = new UserService();
            while (rs.next()) {
                participations.add(new Participation(rs.getInt("id_participation"), es.getEvenement(rs.getInt("id_evenement")), rs.getInt("id_user"), rs.getInt("quantity")));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return participations;
    }

    public List<Participation> generateEventHistory(int id_user) {
        List<Participation> participations = new ArrayList<>();

        String req = "SELECT * from participation_evenement where id_user=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id_user);
            ResultSet rs = pst.executeQuery();
            EvenementService es = new EvenementService();
            while (rs.next()) {
                participations.add(new Participation(es.getEvenementById(rs.getInt("id_evenement"))));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return participations;

    }
}