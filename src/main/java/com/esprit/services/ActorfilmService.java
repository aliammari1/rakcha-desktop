package com.esprit.services;

import com.esprit.models.Actor;
import com.esprit.models.Actorfilm;
import com.esprit.models.Film;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorfilmService implements IService<Actorfilm> {
    Connection connection;

    public ActorfilmService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Actorfilm actorfilm) {
        String req = "INSERT INTO actorfilm (idfilm,idactor) VALUES (?,?)";
        try {
            Actor actor = actorfilm.getIdactor();
            String[] actorNames = actor.getNom().split(", ");
            PreparedStatement statement = connection.prepareStatement(req);
            for (String actorname : actorNames) {
                System.out.println(actorname);
                statement.setInt(1, FilmService.getFilmLastInsertID());
                statement.setInt(2, new ActorService().getActorByNom(actorname).getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Actorfilm> read() {
        List<Actorfilm> actorfilmArrayList = new ArrayList<>();
        String req = "SELECT film.*,actor.id,actor.image,actor.biographie, GROUP_CONCAT(actor.nom SEPARATOR ', ') AS ActorNames from actorfilm JOIN actor  ON actorfilm.idactor  = actor.id JOIN film on actorfilm.idfilm  = film.id GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                actorfilmArrayList.add(new Actorfilm(new Actor(rs.getInt("actor.id"), rs.getString("actor_names"), rs.getBlob("actor.image"), rs.getString("actor.biographie")), new Film(rs.getInt("film.id"), rs.getNString("film.nom"), rs.getBlob("image"), rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation"), rs.getInt("idcinema"))));
                //     System.out.println(filmArrayList.get(i));
                //       i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return actorfilmArrayList;
    }

    @Override
    public void update(Actorfilm actorfilm) {

    }

    @Override
    public void delete(Actorfilm actorfilm) {

    }

    public String getActorsNames(int id) {
        String s = "";
        String req = "SELECT GROUP_CONCAT(actor.nom SEPARATOR ', ') AS ActorNames from actorfilm JOIN actor  ON actorfilm.idactor  = actor.id JOIN film on actorfilm.idfilm  = film.id where film.id = ? GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            rs.next();
            s = rs.getString("ActorNames");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return s;
    }
}
