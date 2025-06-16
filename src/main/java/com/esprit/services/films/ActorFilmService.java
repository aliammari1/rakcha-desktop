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
public class ActorFilmService {
    private static final Logger LOGGER = Logger.getLogger(ActorFilmService.class.getName());
    private final Connection connection;

    /**
     * Performs ActorFilmService operation.
     *
     * @return the result of the operation
     */
    public ActorFilmService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * Performs createFilmActorAssociation operation.
     *
     * @return the result of the operation
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
     * Retrieves the ActorsForFilm value.
     *
     * @return the ActorsForFilm value
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
     * Retrieves the FilmsForActor value.
     *
     * @return the FilmsForActor value
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
     * Performs updateActors operation.
     *
     * @return the result of the operation
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
     * Retrieves the ActorsNames value.
     *
     * @return the ActorsNames value
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
     * Performs deleteFilmActorAssociation operation.
     *
     * @return the result of the operation
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
