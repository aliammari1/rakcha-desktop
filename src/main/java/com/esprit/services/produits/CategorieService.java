package com.esprit.services.produits;

import com.esprit.models.produits.Categorie_Produit;
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

public class CategorieService implements IService<Categorie_Produit> {
    private static final Logger LOGGER = Logger.getLogger(CategorieService.class.getName());
    private final Connection connection;

    public CategorieService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param categorieProduit
     */
    @Override
    public void create(final Categorie_Produit categorieProduit) {
        final String req = "INSERT into categorie_produit(nom_categorie, description) values (?, ?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, categorieProduit.getNom_categorie());
            pst.setString(2, categorieProduit.getDescription());
            pst.executeUpdate();
            CategorieService.LOGGER.info("Categorie ajoutée !");
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Categorie_Produit>
     */
    @Override
    public List<Categorie_Produit> read() {
        final List<Categorie_Produit> categories = new ArrayList<>();
        final String req = "SELECT * from categorie_produit";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                        rs.getString("description")));
            }
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return categories;
    }

    @Override
    public void update(final Categorie_Produit categorieProduit) {
        final String req = "UPDATE categorie_produit set nom_categorie = ?,  description = ? where id_categorie=?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, categorieProduit.getNom_categorie());
            pst.setString(2, categorieProduit.getDescription());
            pst.setInt(3, categorieProduit.getId_categorie());
            pst.executeUpdate();
            CategorieService.LOGGER.info("categorie modifiée !");
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Categorie_Produit categorieProduit) {
        final String req = "DELETE from categorie_produit where id_categorie = ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, categorieProduit.getId_categorie());
            pst.executeUpdate();
            CategorieService.LOGGER.info("categorie supprmiée !");
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Categorie_Produit getCategorie(final int categorie_id) {
        Categorie_Produit category = null;
        final String req = "SELECT * from categorie_produit where id_categorie = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, categorie_id);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                    rs.getString("description"));
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return category;
    }

    public Categorie_Produit getCategorieByNom(final String categorie_nom) {
        Categorie_Produit category = null;
        final String req = "SELECT * from categorie_produit where nom_categorie = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, categorie_nom);
            final ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                    rs.getString("description"));
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return category;
    }

    // Ajoutez cette méthode pour récupérer tous les noms de catégories
    public List<String> getAllCategoriesNames() {
        final List<String> categorieNames = new ArrayList<>();
        // Remplacez "getAllCategories()" par la méthode réelle qui récupère toutes les
        // catégories
        final List<Categorie_Produit> categories = this.read();
        // Ajoutez les noms de catégories à la liste
        for (final Categorie_Produit categorieProduit : categories) {
            categorieNames.add(categorieProduit.getNom_categorie());
        }
        return categorieNames;
    }

    public List<String> getAllCategories() {
        final List<String> categorieNames = new ArrayList<>();
        // Remplacez "getAllCategories()" par la méthode réelle qui récupère toutes les
        // catégories
        final List<Categorie_Produit> categories = this.read();
        // Ajoutez les noms de catégories à la liste
        for (final Categorie_Produit categorieProduit : categories) {
            categorieNames.add(categorieProduit.getNom_categorie());
        }
        return categorieNames;
    }

    public List<Categorie_Produit> searchCategoriesByName(final String searchKeyword) {
        final List<Categorie_Produit> result = new ArrayList<>();
        try {
            final String query = "SELECT * FROM categorie_produit WHERE nom_categorie LIKE ?";
            final PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, "%" + searchKeyword + "%");
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final int idCategorie = resultSet.getInt("id_categorie");
                final String nomCategorie = resultSet.getString("nom_categorie");
                // Ajoutez d'autres colonnes si nécessaire
                final Categorie_Produit categorie = new Categorie_Produit(idCategorie, nomCategorie, null);
                // Initialisez d'autres propriétés si nécessaire
                result.add(categorie);
            }
            statement.close();
            resultSet.close();
        } catch (final SQLException e) {
            CategorieService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
}
