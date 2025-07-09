package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.esprit.models.films.Film;
import com.esprit.models.films.FilmComment;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.TableCreator;

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
public class FilmCommentService implements IService<FilmComment> {
    private static final Logger LOGGER = Logger.getLogger(FilmCommentService.class.getName());
    private final Connection connection;

    /**
     * Constructs a new FilmCommentService instance.
     * Initializes database connection and creates tables if they don't exist.
     */
    public FilmCommentService() {
        this.connection = DataSource.getInstance().getConnection();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create film_comments table
            String createFilmCommentsTable = """
                    CREATE TABLE film_comments (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        comment TEXT NOT NULL,
                        user_id BIGINT NOT NULL,
                        film_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            tableCreator.createTableIfNotExists("film_comments", createFilmCommentsTable);

        } catch (Exception e) {
            log.error("Error creating tables for FilmCommentService", e);
        }
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final FilmComment filmComment) {
        final String req = "INSERT into film_comments(comment,user_id,film_id) values (?,?,?);";
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            pst.setString(1, filmComment.getComment());
            pst.setLong(2, filmComment.getClient().getId());
            pst.setLong(3, filmComment.getFilm().getId());
            pst.executeUpdate();
            log.info("Comment added successfully!");
        } catch (final SQLException e) {
            log.error("Error creating comment", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<FilmComment> read() {
        final List<FilmComment> commentaire = new ArrayList<>();
        final String req = "SELECT * FROM film_comments";
        try (final PreparedStatement pst = this.connection.prepareStatement(req);
                final ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                try {
                    Client client = (Client) new UserService().getUserById(rs.getLong("user_id"));
                    Film film = new FilmService().getFilm(rs.getLong("film_id"));

                    if (client != null && film != null) {
                        commentaire.add(FilmComment.builder().id(rs.getLong("id")).comment(rs.getString("comment"))
                                .client(client).film(film).build());
                    } else {
                        log.warn("Missing required entities for comment ID: " + rs.getLong("id"));
                    }
                } catch (Exception e) {
                    log.warn("Error loading comment relationships for comment ID: " + rs.getLong("id"), e);
                }
            }
        } catch (final SQLException e) {
            log.error("Error reading comments", e);
        }
        return commentaire;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final FilmComment filmComment) {
        final String req = "UPDATE film_comments SET comment=? WHERE id=?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setString(1, filmComment.getComment());
            statement.setLong(2, filmComment.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error updating comment", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final FilmComment filmComment) {
        final String req = "DELETE FROM film_comments WHERE id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, filmComment.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error deleting comment", e);
            throw new RuntimeException(e);
        }
    }
}
