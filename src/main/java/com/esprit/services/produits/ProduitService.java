package com.esprit.services.produits;

import com.esprit.models.produits.Produit;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProduitService implements IService<Produit> {
    private static final Logger LOGGER = Logger.getLogger(ProduitService.class.getName());
    private final Connection connection;

    public ProduitService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param produit
     */
    @Override
    public void create(final Produit produit) {
        final String req = "INSERT into produit(nom, prix,image,description,quantiteP,id_categorieProduit) values (?, ?,?,?,?,?)  ;";
        try {
            ProduitService.LOGGER.info("peoduit: " + produit);
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setInt(2, produit.getPrix());
            pst.setString(3, produit.getImage());
            pst.setString(4, produit.getDescription());
            pst.setInt(5, produit.getQuantiteP());
            pst.setInt(6, produit.getCategorie().getId_categorie());
            pst.executeUpdate();
            ProduitService.LOGGER.info("Produit ajoutée !");
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Produit>
     */
    @Override
    public List<Produit> read() {
        final List<Produit> produits = new ArrayList<>();
        final String req = "SELECT  produit.* , categorie_produit.nom_categorie from produit  JOIN categorie_produit  ON produit.id_categorieProduit = categorie_produit.id_categorie";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            final CategorieService cs = new CategorieService();
            int i = 0;
            while (rs.next()) {
                produits.add(new Produit(rs.getInt("id_produit"), rs.getString("nom"), rs.getInt("prix"),
                        rs.getString("image"), rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")), rs.getInt("quantiteP")));
                ProduitService.LOGGER.info(produits.get(i).toString());
                i++;
            }
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return produits;
    }

    public List<Produit> sort(final String sortBy) {
        // A list of valid column names to prevent SQL injection
        final List<String> validColumns = Arrays.asList("id_produit", "nom", "prix", "description", "categorieProduit",
                "quantiteP");
        final List<Produit> produits = new ArrayList<>();
        // Check if sortBy is a valid column name
        if (!validColumns.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        final String req = "SELECT *, id_categorieProduit FROM produit ORDER BY %s".formatted(sortBy);
        try (final PreparedStatement pst = this.connection.prepareStatement(req)) {
            final ResultSet rs = pst.executeQuery();
            final CategorieService cs = new CategorieService();
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getInt("prix"),
                        rs.getString("image"),
                        rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")),
                        rs.getInt("quantiteP")));
            }
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return produits;
    }

    @Override
    public void update(final Produit produit) {
        final String req = """
                UPDATE produit p \
                INNER JOIN categorie_produit c ON p.id_categorieProduit = c.id_categorie \
                SET p.id_categorieProduit = ?, p.nom = ?, p.prix = ?, p.description = ?, p.image = ?, p.quantiteP = ? \
                WHERE p.id_produit = ?;\
                """;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(7, produit.getId_produit());
            pst.setString(2, produit.getNom());
            pst.setInt(3, produit.getPrix());
            pst.setString(4, produit.getDescription());
            pst.setString(5, produit.getImage());
            pst.setInt(6, produit.getQuantiteP());
            pst.setInt(1, produit.getCategorie().getId_categorie());
            pst.executeUpdate();
            ProduitService.LOGGER.info("produit modifiée !");
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void delete(final Produit produit) {
        final String req = "DELETE from produit where id_produit = ?;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, produit.getId_produit());
            pst.executeUpdate();
            ProduitService.LOGGER.info("produit supprmiée !");
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public Produit getProduitById(final int produitId) {
        Produit produit = null;
        final String req = "SELECT produit.*, categorie_produit.nom_categorie FROM produit JOIN categorie_produit ON produit.id_categorieProduit = categorie_produit.id_categorie WHERE id_produit = ?";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, produitId);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                final CategorieService cs = new CategorieService();
                produit = new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getInt("prix"),
                        rs.getString("image"),
                        rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")),
                        rs.getInt("quantiteP"));
            }
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return produit;
    }

    // Vérifier le stock disponible pour un produit donné
    public boolean verifierStockDisponible(final int produitId, final int quantiteDemandee) {
        final Produit produit = this.getProduitById(produitId); // Utilisez votre méthode existante pour récupérer le produit
        if (null != produit) {
            return produit.getQuantiteP() >= quantiteDemandee;
        } else {
            return false; // Le produit n'existe pas
        }
    }

    public double getPrixProduit(final int idProduit) {
        final String req = "SELECT prix FROM produit WHERE id_produit = ?";
        double prixProduit = 0;
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, idProduit);
            final ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                prixProduit = rs.getDouble("prix");
            }
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return prixProduit;
    }

    public List<Produit> getProduitsOrderByQuantityAndStatus() {
        final List<Produit> produits = new ArrayList<>();
        final String req = """
                SELECT p.id_produit, p.nom, p.prix, p.image, p.description, p.id_categorieProduit, SUM(ci.quantity) AS total_quantity \
                FROM produit p \
                JOIN commandeitem ci ON p.id_produit = ci.id_produit \
                JOIN commande c ON ci.idCommande = c.idCommande \
                WHERE c.statu = 'payee' \
                GROUP BY p.id_produit, p.nom, p.prix, p.image, p.description, p.id_categorieProduit \
                ORDER BY total_quantity DESC;\
                """;
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement(req);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final CategorieService cs = new CategorieService();
                final Produit produit = new Produit(
                        resultSet.getInt("id_produit"),
                        resultSet.getString("nom"),
                        resultSet.getInt("prix"),
                        resultSet.getString("image"),
                        resultSet.getString("description"),
                        cs.getCategorie(resultSet.getInt("id_categorieProduit")),
                        resultSet.getInt("total_quantity")); // Assuming quantiteP should be total_quantity
                produits.add(produit);
            }
        } catch (final SQLException e) {
            ProduitService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return produits;
    }
}
