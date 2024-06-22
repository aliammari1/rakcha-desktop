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
public class RatingCinemaService implements IService<RatingCinema> {
    Connection connection;
    public RatingCinemaService() {
        connection = DataSource.getInstance().getConnection();
    }
    CinemaService cinemaService = new CinemaService();
    /** 
     * @param ratingCinema
     */
    @Override
    public void create(RatingCinema ratingCinema) {
        // Supprimer l'ancienne note du même utilisateur pour le même cinéma s'il existe
        deleteByUserAndCinema(ratingCinema.getId_user().getId(), ratingCinema.getId_cinema().getId_cinema());
        // Ajouter la nouvelle note à la base de données
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
    /** 
     * @param clientId
     * @param cinemaId
     * @return int
     */
    public int getRatingForClientAndCinema(int clientId, int cinemaId) {
        String req = "SELECT rate FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, cinemaId);
            statement.setInt(2, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si aucun résultat n'est trouvé ou s'il y a une exception
    }
    // Méthode pour supprimer l'ancienne note du même utilisateur pour le même cinéma
    private void deleteByUserAndCinema(int userId, int cinemaId) {
        String req = "DELETE FROM ratingcinema WHERE id_cinema=? AND id_user=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, cinemaId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
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
    public List<Cinema> getTopRatedCinemas() {
        List<Cinema> topRatedCinemas = new ArrayList<>();
        String req = "SELECT id_cinema, AVG(rate) AS average_rating "
                + "FROM ratingcinema "
                + "GROUP BY id_cinema "
                + "ORDER BY average_rating DESC "
                + "LIMIT 3"; // Sélectionne les 3 premiers cinémas les mieux notés
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int cinemaId = resultSet.getInt("id_cinema");
                System.out.println(" ------------ " + cinemaId);
                Cinema cinema = cinemaService.getCinema(cinemaId);
                if (cinema != null) {
                    topRatedCinemas.add(cinema);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topRatedCinemas;
    }
}
