package com.esprit.services.cinemas;

import com.esprit.models.cinemas.Salle;
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

public class SalleService implements IService<Salle> {
    private static final Logger LOGGER = Logger.getLogger(SalleService.class.getName());
    private final Connection connection;

    public SalleService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param salle
     */
    @Override
    public void create(final Salle salle) {
        final String req = "INSERT into salle(id_cinema, nb_places, nom_salle) values (?, ?, ?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setString(3, salle.getNom_salle());
            pst.executeUpdate();
            SalleService.LOGGER.info("Salle ajoutée !");
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param salle
     */
    @Override
    public void update(final Salle salle) {
        final String req = "UPDATE salle set id_cinema = ?, nb_places = ?, nom_salle = ? where id_salle = ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(4, salle.getId_salle());
            pst.setInt(1, salle.getId_cinema());
            pst.setInt(2, salle.getNb_places());
            pst.setString(3, salle.getNom_salle());
            pst.executeUpdate();
            SalleService.LOGGER.info("Salle modifiée !");
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Salle salle) {
        final String req = "DELETE from salle where id_salle= ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, salle.getId_salle());
            pst.executeUpdate();
            SalleService.LOGGER.info("Salle supprmiée !");
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Salle> read() {
        final List<Salle> salles = new ArrayList<>();
        final String req = "SELECT * from salle";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                salles.add(new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"),
                        rs.getString("nom_salle")));
            }
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return salles;
    }

    public Salle getSalle(final int salle_id) {
        Salle salle = null;
        final String req = "SELECT * from salle where id_salle = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, salle_id);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"),
                    rs.getString("nom_salle"));
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return salle;
    }

    public Salle getSalleByName(final String nom_salle) {
        Salle salle = null;
        final String req = "SELECT * from salle where nom_salle = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, nom_salle);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"),
                    rs.getString("nom_salle"));
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return salle;
    }

    public List<Salle> readRoomsForCinema(final int cinemaId) {
        final List<Salle> roomsForCinema = new ArrayList<>();
        final String query = "SELECT * FROM salle WHERE id_cinema = ?";
        try (final PreparedStatement pst = this.connection.prepareStatement(query)) {
            pst.setInt(1, cinemaId);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                final Salle salle = new Salle(rs.getInt("id_salle"), rs.getInt("id_cinema"), rs.getInt("nb_places"),
                        rs.getString("nom_salle"));
                roomsForCinema.add(salle);
            }
        } catch (final SQLException e) {
            SalleService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return roomsForCinema;
    }
}
