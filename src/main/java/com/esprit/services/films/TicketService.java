package com.esprit.services.films;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Ticket;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.users.UserService;
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
public class TicketService implements IService<Ticket> {
    private final Connection connection;
    private final UserService userService;
    private final MovieSessionService moviesessionService;

    /**
     * Performs TicketService operation.
     *
     * @return the result of the operation
     */
    public TicketService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
        this.moviesessionService = new MovieSessionService();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Ticket ticket) {
        final String req = "INSERT INTO tickets (user_id, movie_session_id, number_of_seats, price) VALUES (?,?,?,?)";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, ticket.getClient().getId());
            statement.setLong(2, ticket.getMovieSession().getId());
            statement.setInt(3, ticket.getNumberOfSeats());
            statement.setFloat(4, ticket.getPrice());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error creating ticket", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    /**
     * Performs read operation.
     *
     * @return the result of the operation
     */
    public List<Ticket> read() {
        final List<Ticket> tickets = new ArrayList<>();
        final String req = "SELECT * FROM tickets";
        try (final PreparedStatement statement = this.connection.prepareStatement(req);
                final ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                try {
                    Client client = (Client) userService.getUserById(rs.getLong("user_id"));
                    MovieSession movieSession = moviesessionService.getMovieSessionById(rs.getInt("movie_session_id"));

                    if (client != null && movieSession != null) {
                        tickets.add(Ticket.builder().id(rs.getLong("id")).numberOfSeats(rs.getInt("number_of_seats"))
                                .client(client).movieSession(movieSession).price(rs.getFloat("price")).build());
                    } else {
                        log.warn("Missing required entities for ticket ID: " + rs.getLong("id"));
                    }
                } catch (Exception e) {
                    log.warn("Error loading ticket relationships for ticket ID: " + rs.getLong("id"), e);
                }
            }
        } catch (final SQLException e) {
            log.error("Error reading tickets", e);
        }
        return tickets;
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Ticket ticket) {
        final String req = "UPDATE tickets SET user_id=?, movie_session_id=?, number_of_seats=?, price=? WHERE id=?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, ticket.getClient().getId());
            statement.setLong(2, ticket.getMovieSession().getId());
            statement.setInt(3, ticket.getNumberOfSeats());
            statement.setFloat(4, ticket.getPrice());
            statement.setLong(5, ticket.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error updating ticket", e);
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
    public void delete(final Ticket ticket) {
        final String req = "DELETE FROM tickets WHERE id = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(req)) {
            statement.setLong(1, ticket.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            log.error("Error deleting ticket", e);
            throw new RuntimeException(e);
        }
    }
}
