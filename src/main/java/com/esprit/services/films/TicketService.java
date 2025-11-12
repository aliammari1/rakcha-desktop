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
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
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
public class TicketService implements IService<Ticket> {
    private final Connection connection;
    private final UserService userService;
    private final MovieSessionService moviesessionService;

    /**
     * Initialize the TicketService, its database connection, and required related services.
     *
     * Attempts to create the tickets table if it does not exist; any exception raised while
     * creating tables is caught and logged.
    public TicketService() {
        this.connection = DataSource.getInstance().getConnection();
        this.userService = new UserService();
        this.moviesessionService = new MovieSessionService();

        // Create tables if they don't exist
        try {
            TableCreator tableCreator = new TableCreator(this.connection);

            // Create tickets table
            String createTicketsTable = """
                    CREATE TABLE tickets (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        client_id BIGINT NOT NULL,
                        session_id BIGINT NOT NULL,
                        seat_number VARCHAR(50),
                        price DECIMAL(10, 2),
                        purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) DEFAULT 'ACTIVE'
                    )
                    """;
            tableCreator.createTableIfNotExists("tickets", createTicketsTable);

        }
 catch (Exception e) {
            log.error("Error creating tables for TicketService", e);
        }

    }


    /**
     * Inserts the given Ticket into the tickets table.
     *
     * <p>The method persists the ticket's client id, movie session id, number of seats, and price.</p>
     *
     * @param ticket the Ticket to persist; its client and movieSession must have valid ids
     * @throws RuntimeException if a database error occurs while inserting the ticket
     */
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
        }
 catch (final SQLException e) {
            log.error("Error creating ticket", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Loads all tickets from the database, resolving each ticket's client and movie session; tickets whose related entities cannot be loaded are omitted.
     *
     * @return a list of fully populated Ticket objects; empty if no tickets are found or if the query fails
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
                    }
 else {
                        log.warn("Missing required entities for ticket ID: " + rs.getLong("id"));
                    }

                }
 catch (Exception e) {
                    log.warn("Error loading ticket relationships for ticket ID: " + rs.getLong("id"), e);
                }

            }

        }
 catch (final SQLException e) {
            log.error("Error reading tickets", e);
        }

        return tickets;
    }


    /**
     * Update an existing ticket record in the database.
     *
     * @param ticket the Ticket whose database row (identified by its `id`) will be updated;
     *               the row is set to the ticket's client id, movie session id, number of seats, and price
     */
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
        }
 catch (final SQLException e) {
            log.error("Error updating ticket", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Delete the given ticket from the database.
     *
     * @param ticket the ticket whose record (by id) will be removed from the tickets table
     * @throws RuntimeException if a database error occurs while deleting the ticket
     */
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
        }
 catch (final SQLException e) {
            log.error("Error deleting ticket", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * Read a paginated page of Ticket entities according to the provided paging and sorting parameters.
     *
     * @param pageRequest paging and sorting parameters for the requested page
     * @return a Page containing the Tickets for the requested page
     * @throws UnsupportedOperationException always thrown because this method is not implemented
     */
    @Override
    public Page<Ticket> read(PageRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

}
