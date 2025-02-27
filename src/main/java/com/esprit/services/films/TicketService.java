package com.esprit.services.films;

import com.esprit.models.films.Ticket;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TicketService implements IService<Ticket> {
    Connection connection;

    public TicketService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param ticket
     */
    @Override
    public void create(final Ticket ticket) {
        final String req = "insert into ticket (id_user,id_seance) values (?,?) ";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, ticket.getId_user().getId());
            statement.setInt(2, ticket.getId_seance().getId_seance());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Ticket>
     */
    @Override
    public List<Ticket> read() {
        return null;
    }

    @Override
    public void update(final Ticket ticket) {
    }

    @Override
    public void delete(final Ticket ticket) {
    }
}
