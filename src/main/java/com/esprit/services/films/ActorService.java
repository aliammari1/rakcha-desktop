package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.esprit.models.films.Actor;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ActorService implements IService<Actor> {
    private static final Logger LOGGER = Logger.getLogger(ActorService.class.getName());
    Connection connection;

    /**
     * Performs ActorService operation.
     *
     * @return the result of the operation
     */
    public ActorService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param actor
     */
    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *            the entity to create
     */
    public void create(final Actor actor) {
        final String req = "insert into actors (name,image,biography) values (?,?,?) ";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, actor.getName());
            statement.setString(2, actor.getImage());
            statement.setString(3, actor.getBiography());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error creating actor", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Actor>
     */
    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Actor> read() {
        final List<Actor> actorArrayList = new ArrayList<>();
        final String req = "SELECT * from actors";
        try (final PreparedStatement pst = this.connection.prepareStatement(req);
                final ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                actorArrayList.add(Actor.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .image(rs.getString("image")).biography(rs.getString("biography"))
                        .numberOfAppearances(rs.getInt("number_of_appearances")).build());
                log.info("Loaded actor: " + actorArrayList.get(actorArrayList.size() - 1).toString());
            }
        } catch (final SQLException e) {
            log.error("Error reading actors", e);
        }
        return actorArrayList;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *            the entity to update
     */
    public void update(final Actor actor) {
        final String req = "UPDATE actors set name=?,image=?,biography=? where id=?;";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, actor.getName());
            statement.setString(2, actor.getImage());
            statement.setString(3, actor.getBiography());
            statement.setLong(4, actor.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error updating actor", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the ActorByNom value.
     *
     * @return the ActorByNom value
     */
    public Actor getActorByNom(final String nom) {
        Actor actor = null;
        final String req = "SELECT * FROM actors where name LIKE ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, nom);
            try (final ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    actor = Actor.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                            .biography(rs.getString("biography"))
                            .numberOfAppearances(rs.getInt("number_of_appearances")).build();
                    log.info("Found actor: " + actor);
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting actor by name: " + nom, e);
        }
        return actor;
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *            the ID of the entity to delete
     */
    public void delete(final Actor actor) {
        final String req = " DELETE  FROM actors where id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, actor.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error deleting actor", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the ActorByPlacement value.
     *
     * @return the ActorByPlacement value
     */
    public Actor getActorByPlacement(final int actorPlacement) {
        final String req = """
                SELECT a.*, COUNT(af.film_id) AS NumberOfAppearances
                FROM actors a
                JOIN actor_film af ON a.id = af.actor_id
                GROUP BY a.id, a.name
                ORDER BY NumberOfAppearances DESC
                LIMIT ?, 1;
                """;
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, actorPlacement - 1);
            try (final ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Actor.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                            .biography(rs.getString("biography")).numberOfAppearances(rs.getInt("NumberOfAppearances"))
                            .build();
                }
            }
        } catch (final SQLException e) {
            log.error("Error getting actor by placement: " + actorPlacement, e);
            throw new RuntimeException(e);
        }
        return null;
    }
}
