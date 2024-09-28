package com.esprit.services.produits;

import com.esprit.models.produits.Avis;
import com.esprit.models.produits.Produit;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvisService implements IService<Avis> {
    private static final Logger LOGGER = Logger.getLogger(AvisService.class.getName());

    private final Connection connection;

    public AvisService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param avis
     */
    @Override
    public void create(final Avis avis) {
        final String req = "INSERT into avis(idusers,note,id_produit) values (?,?,?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId());
            pst.setInt(2, avis.getNote());
            pst.setInt(3, avis.getProduit().getId_produit());
            pst.executeUpdate();
            AvisService.LOGGER.info("avis ajoutée !");
        } catch (final SQLException e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Avis>
     */
    @Override
    public List<Avis> read() {
        final List<Avis> avisList = new ArrayList<>();
        final String req = "SELECT * FROM avis;";
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final int userId = resultSet.getInt("idusers");
                final int note = resultSet.getInt("note");
                final int produitId = resultSet.getInt("id_produit");
                // Remplacez les appels aux services par les méthodes correspondantes
                final Client user = (Client) new UserService().getUserById(userId);
                final Produit produit = new ProduitService().getProduitById(produitId);
                final Avis avis = new Avis(user, note, produit);
                avisList.add(avis);
            }
        } catch (final SQLException e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return avisList;
    }

    @Override
    public void update(final Avis avis) {
        final String req = "UPDATE avis set note = ? where id=?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, avis.getNote());
            pst.executeUpdate();
            AvisService.LOGGER.info("avis modifiée !");
        } catch (final SQLException e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Avis avis) {
        final String req = "DELETE from avis where idusers = ? and id_produit=?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId());
            pst.setInt(2, avis.getProduit().getId_produit());
            pst.executeUpdate();
            AvisService.LOGGER.info("avis supprmiée !");
        } catch (final SQLException e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public double getavergerating(final int id_produit) {
        final String req = "SELECT AVG(note) AS averageRate FROM avis WHERE id_produit =? ";
        double aver = 0.0;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, id_produit);
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            aver = resultSet.getDouble("averageRate");
        } catch (final Exception e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return aver;
    }

    public List<Avis> getavergeratingSorted() {
        final String req = "SELECT id_produit, AVG(note) AS averageRate FROM avis GROUP BY id_produit  ORDER BY averageRate DESC ";
        final List<Avis> avis = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                avis.add(new Avis(null, (int) resultSet.getDouble("averageRate"),
                        new ProduitService().getProduitById(resultSet.getInt("id_ptoduit"))));
            }
        } catch (final Exception e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return avis;
    }

    public Avis ratingExiste(final int id_produit, final int iduseres) {
        final String req = "SELECT *  FROM avis WHERE id_produit =? AND idusers=? ";
        Avis rate = null;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
            preparedStatement.setInt(1, id_produit);
            preparedStatement.setInt(2, iduseres);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rate = new Avis((Client) new UserService().getUserById(iduseres),
                        (int) resultSet.getDouble("averageRate"),
                        new ProduitService().getProduitById(resultSet.getInt("id_ptoduit")));
            }
        } catch (final Exception e) {
            AvisService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return rate;
    }
}
