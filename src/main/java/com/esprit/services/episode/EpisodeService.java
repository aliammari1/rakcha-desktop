package com.esprit.services.episode;

import com.esprit.models.episode;
import com.esprit.services.IService;
import com.esprit.services.serie.SerieService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpisodeService implements IService<episode> {
    private Connection connection;
    public EpisodeService() { connection = DataSource.getInstance().getConnection(); }
    public void create(episode episode) {
        String req = "INSERT into episode(titre, numeroepisode, saison,image,idserie) values (?, ?,?,?,?,?)  ;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, episode.getTitre());
            pst.setInt(2, episode.getNumeroepisode());
            pst.setInt(3, episode.getSaison());
            pst.setBlob(4, episode.getImage());
            pst.setInt(5, episode.getIdserie());


            pst.executeUpdate();
            System.out.println("Episode ajoutée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(episode episode) {
        String req = "UPDATE episode e " +
                "INNER JOIN serie c ON e.idserie = c.idserie " +
                "SET e.idserie = ?, e.titre = ?, e.numeroepisode = ?, e.saison = ?, e.image = ?" +
                "WHERE e.idepisode = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(6, episode.getIdepisode());
            pst.setString(2, episode.getTitre());
            pst.setInt(3, episode.getNumeroepisode());
            pst.setInt(4, episode.getSaison());
            pst.setBlob(5, episode.getImage());
            pst.setInt(1, episode.getSerie().getIdserie());


            pst.executeUpdate();
            System.out.println("Episode modifiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(episode episode) {
        String req = "DELETE from episode where idepisode= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, episode.getIdepisode());
            pst.executeUpdate();
            System.out.println("Episode supprmiée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<episode> read() {
        List<episode> episodes = new ArrayList<>();
        String req = "SELECT  episode.* , serie.nom from episode  JOIN serie ON episode.idepisode = serie.idserie";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            SerieService cs = new SerieService();
            int i = 0;
            while (rs.next()) {
                episodes.add(new episode(rs.getInt("idepisode"), rs.getString("titre"), rs.getInt("numeroepisode"), rs.getInt("saison"), rs.getBlob("image"),cs.getSerie(rs.getInt("idserie"))));
                        System.out.println(episodes.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        };

        return episodes;
    }
        }
