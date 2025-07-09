package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class providing business logic for managing associations between
 * actors and films in the RAKCHA application.
 * This class handles the many-to-many relationship between actors and films,
 * allowing for creating, retrieving,
 * updating, and deleting these associations.
 *
 * 
 * Key features include:
 * <ul>
 * <li>Creating actor-film associations</li>
 * <li>Retrieving actors for a specific film</li>
 * <li>Retrieving films for a specific actor</li>
 * <li>Updating actor associations for a film</li>
 * <li>Getting actor names as a formatted string</li>
 * <li>Deleting specific actor-film associations</li>
 * </ul>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ActorFilmService {
    private static final Logger LOGGER = Logger.getLogger(ActorFilmService.class.getName());
    private final Connection connection;

    /**
     * Constructor that initializes the database connection and creates tables if
     * they don't exist.
     */
    public ActorFilmService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create actor_film junction table
            String createActorFilmTable = """
                    CREATE TABLE actor_film (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        actor_id BIGINT NOT NULL,
                        film_id BIGINT NOT NULL,
                        role VARCHAR(255),
                        UNIQUE(actor_id, film_id)
                    )
                    """;
            tableCreator.createTableIfNotExists("actor_film", createActorFilmTable);

        } catch (Exception e) {
            log.error("Error creating tables for ActorFilmService", e);
        }
    }

    /**
     * Creates associations between a film and multiple actors based on actor names.
     * 
     * @param film       the film to associate actors with
     * @param actorNames list of actor names to be associated with the film
     */
    public void createFilmActorAssociation(Film film, List<String> actorNames) {
        final String req = "INSERT INTO actor_film (film_id, actor_id) VALUES (?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            for (final String actorName : actorNames) {
                Actor actor = new ActorService().getActorByNom(actorName);
                if (actor != null) {
                    statement.setLong(1, film.getId());
                    statement.setLong(2, actor.getId());
                    statement.executeUpdate();
                }
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating film-actor associations", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the list of actors associated with a specific film.
     *
     * @param filmId the ID of the film to get actors for
     * @return the list of actors associated with the film
     */
    public List<Actor> getActorsForFilm(int filmId) {
        final List<Actor> actors = new ArrayList<>();
        final String req = "SELECT a.* FROM actors a JOIN actor_film af ON a.id = af.actor_id WHERE af.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                actors.add(Actor.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                        .biography(rs.getString("biography")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting actors for film: " + filmId, e);
        }
        return actors;
    }

    /**
     * Retrieves the list of films associated with a specific actor.
     *
     * @param actorId the ID of the actor to get films for
     * @return the list of films the actor has appeared in
     */
    public List<Film> getFilmsForActor(int actorId) {
        final List<Film> films = new ArrayList<>();
        final String req = "SELECT f.* FROM films f JOIN actor_film af ON f.id = af.film_id WHERE af.actor_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, actorId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                films.add(Film.builder().id(rs.getLong("id")).name(rs.getString("name")).image(rs.getString("image"))
                        .duration(rs.getTime("duration")).description(rs.getString("description"))
                        .releaseYear(rs.getInt("release_year")).build());
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting films for actor: " + actorId, e);
        }
        return films;
    }

    /**
     * Updates the actors associated with a film by first removing all existing
     * associations
     * and then creating new ones based on the provided actor names.
     *
     * @param film       the film to update actor associations for
     * @param actorNames list of actor names to be associated with the film
     */
    public void updateActors(final Film film, final List<String> actorNames) {
        // Delete existing associations
        final String reqDelete = "DELETE FROM actor_film WHERE film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(reqDelete)) {
            statement.setLong(1, film.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting existing actor associations", e);
            throw new RuntimeException(e);
        }

        // Create new associations
        createFilmActorAssociation(film, actorNames);
    }

    /**
     * Gets a comma-separated string of actor names for a specific film.
     *
     * @param filmId the ID of the film to get actor names for
     * @return a comma-separated string of actor names
     */
    public String getActorsNames(final Long filmId) {
        final String req = "SELECT GROUP_CONCAT(a.name SEPARATOR ', ') AS actorNames FROM actors a JOIN actor_film af ON a.id = af.actor_id WHERE af.film_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmId);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("actorNames");
            }
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting actor names for film: " + filmId, e);
        }
        return "";
    }

    /**
     * Deletes the association between a specific film and actor.
     *
     * @param filmId  the ID of the film to remove from the association
     * @param actorId the ID of the actor to remove from the association
     */
    public void deleteFilmActorAssociation(int filmId, int actorId) {
        final String req = "DELETE FROM actor_film WHERE film_id = ? AND actor_id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, filmId);
            statement.setInt(2, actorId);
            statement.executeUpdate();
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting film-actor association", e);
            throw new RuntimeException(e);
        }
    }
}
