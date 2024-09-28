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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RatingFilmService implements IService<RatingFilm> {
    private static final Logger LOGGER = Logger.getLogger(RatingFilmService.class.getName());
    Connection connection;

    public RatingFilmService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param ratingfilm
     */
    @Override
    public void create(final RatingFilm ratingfilm) {
        final String req = "INSERT INTO ratingfilm (id_film,id_user,rate) VALUES (?,?,?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, ratingfilm.getId_film().getId());
            statement.setInt(2, ratingfilm.getId_user().getId());
            statement.setInt(3, ratingfilm.getRate());
            statement.executeUpdate();
        } catch (final Exception e) {
            RatingFilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<RatingFilm>
     */
    @Override
    public List<RatingFilm> read() {
        final String req = "SELECT * FROM ratingfilm ";
        return null;
    }

    @Override
    public void update(final RatingFilm ratingFilm) {
    }

    @Override
    public void delete(final RatingFilm ratingFilm) {
        final String req = "DELETE FROM ratingfilm WHERE id_film=? AND id_user=?";
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, ratingFilm.getId_film().getId());
            preparedStatement.setInt(2, ratingFilm.getId_user().getId());
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            RatingFilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public double getavergerating(final int id_film) {
        final String req = "SELECT AVG(rate) AS averageRate FROM ratingfilm WHERE id_film =? ";
        double aver = 0.0;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            aver = resultSet.getDouble("averageRate");
        } catch (final Exception e) {
            RatingFilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return aver;
    }

    public List<RatingFilm> getavergeratingSorted() {
        final String req = "SELECT id_film, AVG(rate) AS averageRate FROM ratingfilm GROUP BY id_film  ORDER BY averageRate DESC ";
        final List<RatingFilm> aver = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                aver.add(new RatingFilm(new FilmService().getFilm(resultSet.getInt("id_film")), null,
                        (int) resultSet.getDouble("averageRate")));
            }
        } catch (final Exception e) {
            RatingFilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return aver;
    }

    public RatingFilm ratingExiste(final int id_film, final int id_user) {
        final String req = "SELECT *  FROM ratingfilm WHERE id_film =? AND id_user=? ";
        RatingFilm rate = null;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, id_film);
            preparedStatement.setInt(2, id_user);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rate = new RatingFilm(new FilmService().getFilm(id_film),
                        (Client) new UserService().getUserById(id_user), resultSet.getInt("rate"));
            }
        } catch (final Exception e) {
            RatingFilmService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return rate;
    }
}
