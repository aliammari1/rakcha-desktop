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
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class providing business logic for managing Actor entities in the
 * RAKCHA application.
 * Implements the IService interface to provide CRUD operations for Actor
 * objects.
 * 
 * <p>
 * This service handles all database operations related to actors, including:
 * <ul>
 * <li>Creating new actors</li>
 * <li>Retrieving all actors or specific actors by ID, name, or placement</li>
 * <li>Updating existing actor information</li>
 * <li>Deleting actors from the database</li>
 * </ul>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ActorService implements IService<Actor> {
    private static final Logger LOGGER = Logger.getLogger(ActorService.class.getName());
    Connection connection;

    /**
     * Constructor that initializes the database connection and creates tables if
     * they don't exist.
     */
    public ActorService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create actors table
            String createActorsTable = """
                    CREATE TABLE actors (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        bio TEXT,
                        birth_date DATE,
                        nationality VARCHAR(100),
                        placement VARCHAR(100)
                    )
                    """;
            tableCreator.createTableIfNotExists("actors", createActorsTable);

        } catch (Exception e) {
            log.error("Error creating tables for ActorService", e);
        }
    }

    /**
     * Creates a new actor in the database.
     * 
     * @param actor the actor entity to create
     */
    /**
     * Creates a new actor in the database.
     *
     * @param actor the actor entity to create
     */
    @Override
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
     * Retrieves all actors from the database.
     * 
     * @return a list of all actors
     */
    /**
     * Retrieves all actors from the database.
     * 
     * @return a list of all actors
     */
    @Override
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

    /**
     * Updates an existing actor in the database.
     *
     * @param actor the actor to update
     */
    @Override
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
     * Retrieves an actor by their name.
     *
     * @param nom the name of the actor to retrieve
     * @return the actor with the specified name, or null if not found
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

    /**
     * Deletes an actor from the database.
     *
     * @param actor the actor to delete
     */
    @Override
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
     * Retrieves an actor by their placement ranking (based on number of
     * appearances).
     *
     * @param actorPlacement the placement ranking to search for
     * @return the actor with the specified placement, or null if not found
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
