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
        connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param avis
     */
    @Override
    public void create(Avis avis) {
        String req = "INSERT into avis(idusers,note,id_produit) values (?,?,?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId());
            pst.setInt(2, avis.getNote());
            pst.setInt(3, avis.getProduit().getId_produit());
            pst.executeUpdate();
            LOGGER.info("avis ajoutée !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Avis>
     */
    @Override
    public List<Avis> read() {
        List<Avis> avisList = new ArrayList<>();
        String req = "SELECT * FROM avis;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("idusers");
                int note = resultSet.getInt("note");
                int produitId = resultSet.getInt("id_produit");
                // Remplacez les appels aux services par les méthodes correspondantes
                Client user = (Client) new UserService().getUserById(userId);
                Produit produit = new ProduitService().getProduitById(produitId);
                Avis avis = new Avis(user, note, produit);
                avisList.add(avis);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return avisList;
    }

    @Override
    public void update(Avis avis) {
        String req = "UPDATE avis set note = ? where id=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getNote());
            pst.executeUpdate();
            LOGGER.info("avis modifiée !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(Avis avis) {
        String req = "DELETE from avis where idusers = ? and id_produit=?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, avis.getUser().getId());
            pst.setInt(2, avis.getProduit().getId_produit());
            pst.executeUpdate();
            LOGGER.info("avis supprmiée !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public double getavergerating(int id_produit) {
        String req = "SELECT AVG(note) AS averageRate FROM avis WHERE id_produit =? ";
        double aver = 0.0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_produit);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            aver = resultSet.getDouble("averageRate");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return aver;
    }

    public List<Avis> getavergeratingSorted() {
        String req = "SELECT id_produit, AVG(note) AS averageRate FROM avis GROUP BY id_produit  ORDER BY averageRate DESC ";
        List<Avis> avis = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                avis.add(new Avis(null, (int) resultSet.getDouble("averageRate"),
                        new ProduitService().getProduitById(resultSet.getInt("id_ptoduit"))));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return avis;
    }

    public Avis ratingExiste(int id_produit, int iduseres) {
        String req = "SELECT *  FROM avis WHERE id_produit =? AND idusers=? ";
        Avis rate = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(req);
            preparedStatement.setInt(1, id_produit);
            preparedStatement.setInt(2, iduseres);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                rate = new Avis((Client) new UserService().getUserById(iduseres),
                        (int) resultSet.getDouble("averageRate"),
                        new ProduitService().getProduitById(resultSet.getInt("id_ptoduit")));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return rate;
    }
}
