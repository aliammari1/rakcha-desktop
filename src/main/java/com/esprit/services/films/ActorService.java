package com.esprit.services.films;

import com.esprit.models.films.Actor;
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

public class ActorService implements IService<Actor> {
    private static final Logger LOGGER = Logger.getLogger(ActorService.class.getName());
    Connection connection;

    public ActorService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param actor
     */
    @Override
    public void create(final Actor actor) {
        final String req = "insert into actor (nom,image,biographie) values (?,?,?) ";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setString(2, actor.getImage());
            statement.setString(3, actor.getBiographie());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Actor>
     */
    @Override
    public List<Actor> read() {
        final List<Actor> actorArrayList = new ArrayList<>();
        final String req = "SELECT * from actor";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                actorArrayList.add(new Actor(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getString("biographie")));
                ActorService.LOGGER.info(actorArrayList.get(i).toString());
                i++;
            }
        } catch (final SQLException e) {
            ActorService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return actorArrayList;
    }

    @Override
    public void update(final Actor actor) {
        final String req = "UPDATE actor set nom=?,image=?,biographie=? where id=?;";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setString(2, actor.getImage());
            statement.setString(3, actor.getBiographie());
            statement.setInt(4, actor.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Actor getActorByNom(final String nom) {
        Actor actor = null;
        final String req = "SELECT * FROM actor where nom LIKE ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, nom);
            final ResultSet rs = statement.executeQuery();
            rs.next();
            actor = new Actor(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getString("biographie"));
            ActorService.LOGGER.info("actor: " + actor);
        } catch (final SQLException e) {
            ActorService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return actor;
    }

    @Override
    public void delete(final Actor actor) {
        final String req = " DELETE  FROM actor where id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, actor.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Actor getActorByPlacement(final int actorPlacement) {
        final String req = """
                SELECT a.*, COUNT(af.film_id) AS NumberOfAppearances \
                FROM actor a \
                JOIN film_actor af ON a.id = af.actor_id \
                GROUP BY a.id, a.nom \
                ORDER BY NumberOfAppearances DESC \
                LIMIT ?, 1;\
                """; // Use parameter placeholder for the limit
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, actorPlacement - 1); // Placement starts from 1 but index starts from 0
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                final int id = rs.getInt("id");
                final String img = rs.getString("image");
                final String nom = rs.getString("nom");
                final int numberOfAppearances = rs.getInt("NumberOfAppearances");
                final String bio = rs.getString("biographie");
                return new Actor(id, nom, img, bio, numberOfAppearances); // Assuming Actor class has a constructor that
                // accepts id, nom, img, and
                // numberOfAppearances
            }
            rs.close();
            statement.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if no actor found at the specified placement
    }
}
