package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CinemaService implements IService<Cinema> {
    private static final Logger LOGGER = Logger.getLogger(CinemaService.class.getName());
    private final Connection connection;

    public CinemaService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param cinema
     */
    @Override
    public void create(final Cinema cinema) {
        final String req = "INSERT into cinema(nom, adresse, responsable, logo, Statut) values (?, ?, ?, ?, ?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setInt(3, cinema.getResponsable().getId());
            pst.setString(4, cinema.getLogo());
            // Définition de la valeur par défaut pour le champ Statut
            pst.setString(5, null != cinema.getStatut() ? cinema.getStatut() : "Pending");
            pst.executeUpdate();
            CinemaService.LOGGER.info("Cinéma ajouté !");
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param cinema
     */
    @Override
    public void update(final Cinema cinema) {
        final String req = "UPDATE cinema set nom = ?, adresse = ?, logo = ?, Statut = ? where id_cinema = ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(5, cinema.getId_cinema());
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setString(3, cinema.getLogo());
            pst.setString(4, cinema.getStatut());
            pst.executeUpdate();
            CinemaService.LOGGER.info("Cinéma modifiée !");
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Cinema cinema) {
        final String req = "DELETE from cinema where id_cinema= ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, cinema.getId_cinema());
            pst.executeUpdate();
            CinemaService.LOGGER.info("Cinema supprmiée !");
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Cinema> read() {
        final List<Cinema> cinemas = new ArrayList<>();
        final String req = "SELECT * from cinema";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                        (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                        rs.getString("logo"), rs.getString("Statut")));
            }
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return cinemas;
    }

    public List<Cinema> sort(final String p) {
        final List<Cinema> cinemas = new ArrayList<>();
        final String req = "SELECT * from cinema ORDER BY %s".formatted(p);
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                        (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                        rs.getString("logo"), rs.getString("Statut")));
            }
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return cinemas;
    }

    public Cinema getCinema(final int cinema_id) {
        Cinema cinema = null;
        final String req = "SELECT * from cinema where id_cinema = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, cinema_id);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            cinema = new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                    (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                    rs.getString("logo"), rs.getString("Statut"));
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return cinema;
    }

    public Cinema getCinemaByName(final String nom_cinema) {
        Cinema cinema = null;
        final String req = "SELECT * from cinema where nom = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, nom_cinema);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            cinema = new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                    (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                    rs.getString("logo"), rs.getString("Statut"));
        } catch (final SQLException e) {
            CinemaService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return cinema;
    }
}
