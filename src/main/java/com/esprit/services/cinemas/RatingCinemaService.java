package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.RatingCinema;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RatingCinemaService implements IService<RatingCinema> {
    private static final Logger LOGGER = Logger.getLogger(RatingCinemaService.class.getName());
    Connection connection;
    CinemaService cinemaService = new CinemaService();

    public RatingCinemaService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param ratingCinema
     */
    @Override
    public void create(final RatingCinema ratingCinema) {
        // Supprimer l'ancienne note du même utilisateur pour le même cinéma s'il existe
        this.deleteByUserAndCinema(ratingCinema.getId_user().getId(), ratingCinema.getId_cinema().getId_cinema());
        // Ajouter la nouvelle note à la base de données
        final String req = "INSERT INTO ratingcinema (id_cinema,id_user,rate) VALUES (?,?,?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, ratingCinema.getId_cinema().getId_cinema());
            statement.setInt(2, ratingCinema.getId_user().getId());
            statement.setInt(3, ratingCinema.getRate());
            statement.executeUpdate();
        } catch (final Exception e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param clientId
     * @param cinemaId
     * @return int
     */
    public int getRatingForClientAndCinema(final int clientId, final int cinemaId) {
        final String req = "SELECT rate FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, cinemaId);
            statement.setInt(2, clientId);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("rate");
            }
        } catch (final SQLException e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return -1; // Retourne -1 si aucun résultat n'est trouvé ou s'il y a une exception
    }

    // Méthode pour supprimer l'ancienne note du même utilisateur pour le même
    // cinéma
    private void deleteByUserAndCinema(final int userId, final int cinemaId) {
        final String req = "DELETE FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, cinemaId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<RatingCinema> read() {
        final String req = "SELECT * FROM ratingcinema ";
        return null;
    }

    @Override
    public void update(final RatingCinema ratingCinema) {
    }

    @Override
    public void delete(final RatingCinema ratingCinema) {
        final String req = "DELETE FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, ratingCinema.getId_cinema().getId_cinema());
            preparedStatement.setInt(2, ratingCinema.getId_user().getId());
            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public double getAverageRating(final int cinemaId) {
        final String req = "SELECT AVG(rate) AS average FROM ratingcinema WHERE id_cinema=?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, cinemaId);
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("average");
            }
        } catch (final SQLException e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0; // Retourne 0 si aucun résultat n'est trouvé ou s'il y a une exception
    }

    public List<Cinema> getTopRatedCinemas() {
        final List<Cinema> topRatedCinemas = new ArrayList<>();
        final String req = """
                SELECT id_cinema, AVG(rate) AS average_rating \
                FROM ratingcinema \
                GROUP BY id_cinema \
                ORDER BY average_rating DESC \
                LIMIT 3\
                """; // Sélectionne les 3 premiers cinémas les mieux notés
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final int cinemaId = resultSet.getInt("id_cinema");
                RatingCinemaService.LOGGER.info(" ------------ " + cinemaId);
                final Cinema cinema = this.cinemaService.getCinema(cinemaId);
                if (null != cinema) {
                    topRatedCinemas.add(cinema);
                }
            }
        } catch (final SQLException e) {
            RatingCinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return topRatedCinemas;
    }
}
