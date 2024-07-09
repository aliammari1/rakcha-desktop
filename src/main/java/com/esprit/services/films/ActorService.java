package com.esprit.services.films;
import com.esprit.models.films.Actor;
import com.esprit.services.IService;
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
    /** 
     * @param actor
     */
    @Override
    public void create(Actor actor) {
        String req = "insert into actor (nom,image,biographie) values (?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setString(2, actor.getImage());
            statement.setString(3, actor.getBiographie());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /** 
     * @return List<Actor>
     */
    @Override
    public List<Actor> read() {
        List<Actor> actorArrayList = new ArrayList<>();
        String req = "SELECT * from actor";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                actorArrayList.add(new Actor(rs.getInt("id"), rs.getString("nom"), rs.getString("image"),
                        rs.getString("biographie")));
                System.out.println(actorArrayList.get(i));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actorArrayList;
    }
    @Override
    public void update(Actor actor) {
        String req = "UPDATE actor set nom=?,image=?,biographie=? where id=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, actor.getNom());
            statement.setString(2, actor.getImage());
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
            actor = new Actor(rs.getInt("id"), rs.getString("nom"), rs.getString("image"), rs.getString("biographie"));
            System.out.println("actor: " + actor);
        } catch (SQLException e) {
            e.printStackTrace();
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
    public Actor getActorByPlacement(int actorPlacement) {
        String req = "SELECT a.*, COUNT(af.film_id) AS NumberOfAppearances " +
                "FROM actor a " +
                "JOIN film_actor af ON a.id = af.actor_id " +
                "GROUP BY a.id, a.nom " +
                "ORDER BY NumberOfAppearances DESC " +
                "LIMIT ?, 1;"; // Use parameter placeholder for the limit
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, actorPlacement - 1); // Placement starts from 1 but index starts from 0
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String img = rs.getString("image");
                String nom = rs.getString("nom");
                int numberOfAppearances = rs.getInt("NumberOfAppearances");
                String bio = rs.getString("biographie");
                return new Actor(id, nom, img, bio, numberOfAppearances); // Assuming Actor class has a constructor that
                                                                          // accepts id, nom, img, and
                                                                          // numberOfAppearances
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if no actor found at the specified placement
    }
}
