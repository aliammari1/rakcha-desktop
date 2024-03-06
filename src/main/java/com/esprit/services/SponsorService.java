package com.esprit.services;
import com.esprit.models.Sponsor;
import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SponsorService implements IService<Sponsor> {

    private Connection connection;

    public SponsorService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Sponsor sponsor) {
        String req = "INSERT into sponsor(nomSociete,logo) values (?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, sponsor.getNomSociete());
            pst.setBlob(2, sponsor.getLogo());
            pst.executeUpdate();
            System.out.println("Sponsor ajouté !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Sponsor sponsor) {
        String req = "UPDATE sponsor set nomSociete = ?, logo = ? where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(3, sponsor.getId());
            pst.setString(1, sponsor.getNomSociete());
            pst.setBlob(2, sponsor.getLogo());
            pst.executeUpdate();
            System.out.println("Sponsor modifié !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Sponsor sponsor) {
        String req = "DELETE from sponsor where id = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, sponsor.getId());
            pst.executeUpdate();
            System.out.println("Sponsor supprimé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Sponsor> show() {
        List<Sponsor> sponsors = new ArrayList<>();

        String req = "SELECT * from sponsor";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                sponsors.add(new Sponsor(rs.getInt("ID"), rs.getString("NomSociete"), rs.getBlob("Logo")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return sponsors;
    }
}

