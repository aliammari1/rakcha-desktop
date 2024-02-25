package com.esprit.services;

import com.esprit.models.Actor;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ActorService implements IService<Actor> {
    Connection connection;

    public ActorService() {
        connection = DataSource.getInstance().getConnection();

    }

    @Override
    public void create(Actor actor) {
        String req = "insert into actor (nom,image) values (?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setBlob(2, actor.getImage());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Actor> read() {
        return null;
    }

    @Override
    public void update(Actor actor) {

    }

    @Override
    public void delete(Actor actor) {

    }
}
