package com.esprit.services;

import com.esprit.models.Actor;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorService implements IService<Actor> {
    Connection connection;

    public ActorService() {
        connection = DataSource.getInstance().getConnection();

    }

    @Override
    public void create(Actor actor) {
        String req = "insert into actor (nom,image,biographie) values (?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setBlob(2, actor.getImage());
            statement.setString(3, actor.getBiographie());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Actor> read() {
        List<Actor> actorArrayList = new ArrayList<>();
        String req = "SELECT * from actor";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                actorArrayList.add(new Actor(rs.getInt("id"), rs.getString("nom"), rs.getBlob("image"), rs.getString("biographie")));
                System.out.println(actorArrayList.get(i));
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return actorArrayList;
    }

    @Override
    public void update(Actor actor) {
        String req = "UPDATE actor set nom=?,image=?,biographie=? where id=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setBlob(2, actor.getImage());
            statement.setString(3, actor.getBiographie());
            statement.setInt(4, actor.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Actor getActorByNom(String nom) {
        Actor actor = null;
        String req = "SELECT * FROM actor where nom LIKE ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, nom);
            ResultSet rs = statement.executeQuery();
            rs.next();
            actor = new Actor(rs.getInt("id"), rs.getString("nom"), rs.getBlob("image"), rs.getString("biographie"));
            System.out.println(actor);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return actor;
    }

    @Override
    public void delete(Actor actor) {
        String req = " DELETE  FROM actor where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, actor.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
