package com.esprit.services.films;
import com.esprit.models.films.Filmcoment;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class FilmcomentService implements IService<Filmcoment> {
    private final Connection connection;
    public FilmcomentService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param filmcoment
     */
    @Override
    public void create(Filmcoment filmcoment) {
        String req = "INSERT into filmcoment(comment,user_id,film_id ) values (?,?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, filmcoment.getComment());
            pst.setInt(2, filmcoment.getUser_id().getId());
            pst.setInt(3, filmcoment.getFilm_id().getId());
            pst.executeUpdate();
            System.out.println("commentaire ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
     * @return List<Filmcoment>
     */
    @Override
    public List<Filmcoment> read() {
        List<Filmcoment> commentaire = new ArrayList<>();
        String req = "SELECT * FROM filmcoment";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Filmcoment(
                        rs.getInt("id"),
                        rs.getString("comment"),
                        (Client) new UserService().getUserById(rs.getInt("user_id")),
                        new FilmService().getFilm(rs.getInt("film_id"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaire;
    }
    @Override
    public void update(Filmcoment filmcoment) {
    }
    @Override
    public void delete(Filmcoment filmcoment) {
    }
}
