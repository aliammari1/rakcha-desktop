package com.esprit.services.films;

import com.esprit.models.films.Actor;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "name", "image_url", "biography"
    };
    Connection connection;

    /**
     * Constructor that initializes the database connection and creates tables if
     * they don't exist.
     */
    public ActorService() {
        this.connection = DataSource.getInstance().getConnection();

    }

    /**
     * Creates a new actor in the database.
     *
     * @param actor the actor entity to create
     */
    /**
     * Insert the given Actor into the actors database table.
     *
     * @param actor the Actor to insert; the actor's name, image, and biography
     *              fields are persisted
     * @throws RuntimeException if a database error prevents inserting the actor
     */
    @Override
    public void create(final Actor actor) {
        final String req = "insert into actors (name,image_url,biography) values (?,?,?) ";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, actor.getName());
            statement.setString(2, actor.getImageUrl());
            statement.setString(3, actor.getBiography());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error creating actor", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * Retrieve a page of actors according to the provided pagination and optional
     * sorting parameters.
     * <p>
     * If the requested sort column is not allowed, the sort is ignored and results
     * use default ordering.
     * On database errors the method returns a page with whatever content was
     * collected (possibly empty)
     * and a totalElements value of 0.
     *
     * @param pageRequest pagination and sorting parameters; if the sort column is
     *                    invalid it will be ignored
     * @return a Page of Actor containing the current page content, page index, page
     * size, and total element count
     */
    @Override
    /**
     * Retrieves actors with pagination support.
     *
     * @param pageRequest the pagination parameters
     * @return a page of actors
     */
    public Page<Actor> read(PageRequest pageRequest) {
        final List<Actor> content = new ArrayList<>();
        final String baseQuery = "SELECT * FROM actors";

        // Validate sort column to prevent SQL injection
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.getSortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.getSortBy());
            pageRequest = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        }

        try {
            // Get total count
            final String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            final long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            final String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);

            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    content.add(Actor.builder().id(rs.getLong("id")).name(rs.getString("name"))
                        .imageUrl(rs.getString("image_url")).biography(rs.getString("biography"))
                        .build());
                }

            }

            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements);

        } catch (final SQLException e) {
            log.error("Error retrieving paginated actors: {}", e.getMessage(), e);
            return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), 0);
        }

    }

    /**
     * Updates an existing actor in the database.
     *
     * @param actor the actor to update
     */
    @Override
    public void update(final Actor actor) {
        final String req = "UPDATE actors set name=?,image_url=?,biography=? where id=?;";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, actor.getName());
            statement.setString(2, actor.getImageUrl());
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
                    actor = Actor.builder().id(rs.getLong("id")).name(rs.getString("name")).imageUrl(rs.getString("image_url"))
                        .biography(rs.getString("biography"))
                        .build();
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

    @Override
    public List<Actor> getAll() {
        final List<Actor> actors = new ArrayList<>();
        final String query = "SELECT * FROM actors";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                actors.add(Actor.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .imageUrl(rs.getString("image_url"))
                    .biography(rs.getString("biography"))
                    .build());
            }
        } catch (SQLException e) {
            log.error("Error retrieving all actors", e);
        }

        return actors;
    }

    /**
     * Retrieves an actor by their placement ranking (based on number of
     * appearances).
     *
     * @param actorPlacement the 1-based placement to retrieve (1 = most
     *                       appearances)
     * @return the actor with the specified placement, or `null` if no actor exists
     * at that placement
     * @throws RuntimeException if a database error occurs while querying
     */
    public Actor getActorByPlacement(final int actorPlacement) {
        final String req = """
            SELECT a.id, a.name, a.image_url, a.biography, COUNT(ma.movie_id) AS number_of_appearances
            FROM actors a
            LEFT JOIN movie_actors ma ON a.id = ma.actor_id
            GROUP BY a.id, a.name, a.image_url, a.biography
            ORDER BY number_of_appearances DESC
            LIMIT 1 OFFSET ?
            """;
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setInt(1, actorPlacement - 1);
            try (final ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Actor.builder().id(rs.getLong("id")).name(rs.getString("name")).imageUrl(rs.getString("image_url"))
                        .biography(rs.getString("biography"))
                        .build();
                }

            }

        } catch (final SQLException e) {
            log.error("Error getting actor by placement: " + actorPlacement, e);
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    /**
     * Checks if an actor exists by its ID.
     *
     * @param id the ID of the actor to check
     * @return true if the actor exists, false otherwise
     */
    public boolean exists(final Long id) {
        return getActorById(id) != null;
    }

    @Override
    /**
     * Counts the total number of actors in the database.
     *
     * @return the total count of actors
     */
    public int count() {
        String query = "SELECT COUNT(*) as count FROM actors";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            log.error("Error counting actors", e);
        }
        return 0;
    }

    @Override
    public Actor getById(Long id) {
        return getActorById(id);
    }

    /**
     * Retrieves an actor by its ID.
     *
     * @param id the ID of the actor
     * @return the actor if found, null otherwise
     */
    public Actor getActorById(Long id) {
        String query = "SELECT * FROM actors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Actor.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .imageUrl(rs.getString("image_url"))
                        .biography(rs.getString("biography"))
                        .build();
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving actor", e);
        }
        return null;
    }

    @Override
    /**
     * Searches for actors by name or biography.
     *
     * @param query the search query
     * @return a list of actors matching the search query
     */
    public List<Actor> search(final String query) {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT * FROM actors WHERE name LIKE ? OR biography LIKE ? ORDER BY name";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            String pattern = "%" + query + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(Actor.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .imageUrl(rs.getString("image_url"))
                        .biography(rs.getString("biography"))
                        .build());
                }
            }
        } catch (final SQLException e) {
            log.error("Error searching actors", e);
            throw new RuntimeException(e);
        }
        return actors;
    }

}
