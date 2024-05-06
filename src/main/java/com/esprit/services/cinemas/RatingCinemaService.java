package com.esprit.services.cinemas;

import com.esprit.models.cinemas.RatingCinema;

import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingCinemaService implements IService<RatingCinema> {
    Connection connection;

    public RatingCinemaService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(RatingCinema ratingCinema) {
        String req = "INSERT INTO ratingcinema (id_cinema,id_user,rate) VALUES (?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, ratingCinema.getId_cinema().getId_cinema());
            statement.setInt(2, ratingCinema.getId_user().getId());
            statement.setInt(3, ratingCinema.getRate());
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RatingCinema> read() {
        String req = "SELECT * FROM ratingcinema ";
        return null;
    }

    @Override
    public void update(RatingCinema ratingCinema) {

    }

    @Override
    public void delete(RatingCinema ratingCinema) {
        String req = "DELETE FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, ratingCinema.getId_cinema().getId_cinema());
            preparedStatement.setInt(2, ratingCinema.getId_user().getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public double getAverageRating(int cinemaId) {
        String req = "SELECT AVG(rate) AS average FROM ratingcinema WHERE id_cinema=?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, cinemaId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("average");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retourne 0 si aucun résultat n'est trouvé ou s'il y a une exception
    }


}

