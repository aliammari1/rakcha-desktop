package com.esprit.services.films;

import com.esprit.models.films.Actor;
import com.esprit.models.films.Actorfilm;
import com.esprit.models.films.Film;
import com.esprit.services.IService;
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
        String req = "SELECT film.*,GROUP_CONCAT(actor.nom SEPARATOR ', ') AS actorNames,actor.id,actor.image,actor.biographie from actorfilm JOIN actor  ON actorfilm.idactor  = actor.id JOIN film on actorfilm.idfilm  = film.id GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                actorfilmArrayList.add(new Actorfilm(new Actor(rs.getInt("actor.id"), rs.getString("actorNames"), rs.getString("actor.image"), rs.getString("actor.biographie")), new Film(rs.getInt("film.id"), rs.getNString("film.nom"), rs.getString("image"), rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation"))));
                //     System.out.println(filmArrayList.get(i));
                //       i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actorfilmArrayList;
    }

    @Override
    public void update(Actorfilm actorfilm) {
        String req = "UPDATE actorFilm " +
                "INNER JOIN actor ON actorFilm.idactor = actor.id " +
                "INNER JOIN film ON actorFilm.idfilm = film.id " +
                "SET actorFilm.idfilm = ?, " +
                "    actor.nom = ? " +
                "WHERE actor.id = ? AND film.id = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateActors(Film film, List<String> actorNames) {
        FilmService filmService = new FilmService();
        ActorService actorService = new ActorService();
        filmService.update(film);
        System.out.println("filmCategory---------------: " + film);
        String reqDelete = "DELETE FROM actorfilm WHERE idfilm = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String req = "INSERT INTO actorfilm (idfilm, idactor) VALUES (?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (String actorname : actorNames) {
                statement.setInt(2, new ActorService().getActorByNom(actorname).getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(Actorfilm actorfilm) {

    }

    public String getActorsNames(int id) {
        String s = "";
        String req = "SELECT GROUP_CONCAT(actor.nom SEPARATOR ', ') AS actorNames from actorfilm JOIN actor  ON actorfilm.idactor  = actor.id JOIN film on actorfilm.idfilm  = film.id where film.id = ? GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            rs.next();
            System.out.println(rs.getMetaData());
            s = rs.getString("actorNames");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }
}
