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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActorfilmService implements IService<Actorfilm> {
    private static final Logger LOGGER = Logger.getLogger(ActorfilmService.class.getName());
    Connection connection;

    public ActorfilmService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param actorfilm
     */
    @Override
    public void create(final Actorfilm actorfilm) {
        final String req = "INSERT INTO film_actor (film_id,actor_id) VALUES (?,?)";
        try {
            final Actor actor = actorfilm.getIdactor();
            final String[] actorNames = actor.getNom().split(", ");
            final PreparedStatement statement = this.connection.prepareStatement(req);
            for (final String actorname : actorNames) {
                ActorfilmService.LOGGER.info(actorname);
                statement.setInt(1, FilmService.getFilmLastInsertID());
                statement.setInt(2, new ActorService().getActorByNom(actorname).getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Actorfilm>
     */
    @Override
    public List<Actorfilm> read() {
        final List<Actorfilm> actorfilmArrayList = new ArrayList<>();
        final String req = "SELECT film.*,GROUP_CONCAT(actor.nom SEPARATOR ', ') AS actorNames,actor.id,actor.image,actor.biographie from film_actor JOIN actor  ON film_actor.actor_id = actor.id JOIN film on film_actor.film_id = film.id GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                actorfilmArrayList.add(new Actorfilm(
                        new Actor(rs.getInt("actor.id"), rs.getString("actorNames"), rs.getString("actor.image"),
                                rs.getString("actor.biographie")),
                        new Film(rs.getInt("film.id"), rs.getNString("film.nom"), rs.getString("image"),
                                rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation"))));
                // LOGGER.info(filmArrayList.get(i));
                // i++;
            }
        } catch (final SQLException e) {
            ActorfilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return actorfilmArrayList;
    }

    @Override
    public void update(final Actorfilm actorfilm) {
        final String req = """
                UPDATE film_actor \
                INNER JOIN actor ON film_actor.actor_id = actor.id \
                INNER JOIN film ON film_actor.film_id = film.id \
                SET film_actor.film_id = ?, \
                    actor.nom = ? \
                WHERE actor.id = ? AND film.id = ?;\
                """;
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
        } catch (final SQLException e) {
            ActorfilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void updateActors(final Film film, final List<String> actorNames) {
        final FilmService filmService = new FilmService();
        final ActorService actorService = new ActorService();
        filmService.update(film);
        ActorfilmService.LOGGER.info("filmCategory---------------: " + film);
        final String reqDelete = "DELETE FROM film_actor WHERE film_id = ?;";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            ActorfilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        final String req = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (final String actorname : actorNames) {
                statement.setInt(2, new ActorService().getActorByNom(actorname).getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final Actorfilm actorfilm) {
    }

    public String getActorsNames(final int id) {
        String s = "";
        final String req = "SELECT GROUP_CONCAT(actor.nom SEPARATOR ', ') AS actorNames from film_actor JOIN actor  ON film_actor.actor_id = actor.id JOIN film on film_actor.film_id = film.id where film.id = ? GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            rs.next();
            s = rs.getString("actorNames");
        } catch (final SQLException e) {
            ActorfilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return s;
    }
}
