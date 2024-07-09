package com.esprit.services.films;
import com.esprit.models.films.RatingFilm;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class RatingFilmService implements IService<RatingFilm> {
    Connection connection;
    public RatingFilmService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param ratingfilm
     */
    @Override
    public void create(RatingFilm ratingfilm) {
        String req = "INSERT INTO ratingfilm (id_film,id_user,rate) VALUES (?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, ratingfilm.getId_film().getId());
            statement.setInt(2, ratingfilm.getId_user().getId());
            statement.setInt(3, ratingfilm.getRate());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 
     * @return List<RatingFilm>
     */
    @Override
    public List<RatingFilm> read() {
        String req = "SELECT * FROM ratingfilm ";
        return null;
    }
    @Override
    public void update(RatingFilm ratingFilm) {
    }
    @Override
    public void delete(RatingFilm ratingFilm) {
        String req = "DELETE FROM ratingfilm WHERE id_film=? AND id_user=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, ratingFilm.getId_film().getId());
            preparedStatement.setInt(2, ratingFilm.getId_user().getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public double getavergerating(int id_film) {
        String req = "SELECT AVG(rate) AS averageRate FROM ratingfilm WHERE id_film =? ";
        double aver = 0.0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            aver = resultSet.getDouble("averageRate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aver;
    }
    public List<RatingFilm> getavergeratingSorted() {
        String req = "SELECT id_film, AVG(rate) AS averageRate FROM ratingfilm GROUP BY id_film  ORDER BY averageRate DESC ";
        List<RatingFilm> aver = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                aver.add(new RatingFilm(new FilmService().getFilm(resultSet.getInt("id_film")), null,
                        (int) resultSet.getDouble("averageRate")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aver;
    }
    public RatingFilm ratingExiste(int id_film, int id_user) {
        String req = "SELECT *  FROM ratingfilm WHERE id_film =? AND id_user=? ";
        RatingFilm rate = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            preparedStatement.setInt(2, id_user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                rate = new RatingFilm(new FilmService().getFilm(id_film),
                        (Client) new UserService().getUserById(id_user), resultSet.getInt("rate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rate;
    }
}
