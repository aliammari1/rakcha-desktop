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
        connection = DataSource.getInstance().getConnection();

    }

    @Override
    public void create(Ticket ticket) {

        String req = "insert into ticket (id_user,id_seance,prix,nbrdeplace) values (?,?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, ticket.getId_user().getId());
            statement.setInt(2, ticket.getId_seance().getId_seance());
            statement.setFloat(3, ticket.getPrix());
            statement.setInt(4, ticket.getNbrdeplace());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ticket> read() {

        return null;
    }

    @Override
    public void update(Ticket ticket) {

    }

    @Override
    public void delete(Ticket ticket) {

    }
}