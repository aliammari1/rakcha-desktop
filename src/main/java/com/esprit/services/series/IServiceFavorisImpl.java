package com.esprit.services.series;

import com.esprit.models.series.Favoris;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IServiceFavorisImpl implements IServiceFavoris<Favoris> {
    private static final Logger LOGGER = Logger.getLogger(IServiceFavorisImpl.class.getName());
    public Connection conx;
    public Statement stm;

    public IServiceFavorisImpl() {
        this.conx = DataSource.getInstance().getConnection();
    }

    /**
     * @param a
     */
    @Override
    public void ajouter(final Favoris a) {
        final String req = """
                INSERT INTO favoris\
                (id_user,id_serie)\
                VALUES(?,?)\
                """;
        try {
            final PreparedStatement ps = this.conx.prepareStatement(req);
            ps.setInt(1, a.getId_user());
            ps.setInt(2, a.getId_serie());
            ps.executeUpdate();
            IServiceFavorisImpl.LOGGER.info("Favoris Ajoutée !!");
        } catch (final SQLException e) {
            IServiceFavorisImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param a
     */
    @Override
    public void modifier(final Favoris a) {
        try {
            final String req = "UPDATE favoris SET id_user=?, id_serie=? WHERE id=?";
            final PreparedStatement pst = this.conx.prepareStatement(req);
            pst.setInt(3, a.getId());
            pst.setInt(1, a.getId_user());
            pst.setInt(2, a.getId_serie());
            pst.executeUpdate();
            IServiceFavorisImpl.LOGGER.info("Favoris Modifiée !");
        } catch (final SQLException e) {
            IServiceFavorisImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(final int id) throws SQLException {
        final String req = "DELETE FROM favoris WHERE id=?";
        try {
            final PreparedStatement pst = this.conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            IServiceFavorisImpl.LOGGER.info("Favoris suprimée !");
        } catch (final SQLException e) {
            IServiceFavorisImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Favoris> Show() {
        final List<Favoris> list = new ArrayList<>();
        try {
            final String req = "SELECT * from favoris";
            final Statement st = this.conx.createStatement();
            final ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Favoris(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getInt("id_serie")));
            }
        } catch (final SQLException e) {
            IServiceFavorisImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    public Favoris getByIdUserAndIdSerie(final int userId, final int serieId) throws SQLException {
        Favoris fav = null;
        final String query = "SELECT * FROM favoris WHERE id_user = ? AND id_serie = ?";
        try (final PreparedStatement ps = this.conx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, serieId);
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming Serie is a class representing your series data
                    fav = new Favoris();
                    fav.setId(rs.getInt("id"));
                    fav.setId_user(rs.getInt("id_user"));
                    fav.setId_serie(rs.getInt("id_serie"));
                    // Set other properties accordingly
                }
            }
        }
        return fav;
    }

    public List<Favoris> afficherListeFavoris(final int userId) {
        final List<Favoris> list = new ArrayList<>();
        try {
            final String req = "SELECT * from favoris WHERE id_user = ?";
            final PreparedStatement ps = this.conx.prepareStatement(req);
            ps.setInt(1, userId);
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Favoris(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getInt("id_serie")));
            }
        } catch (final SQLException e) {
            IServiceFavorisImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}
