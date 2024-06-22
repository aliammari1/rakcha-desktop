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
public class ProduitService implements IService<Produit> {
    private final Connection connection;
    public ProduitService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param produit
     */
    @Override
    public void create(Produit produit) {
        String req = "INSERT into produit(nom, prix,image,description,quantiteP,id_categorieProduit) values (?, ?,?,?,?,?)  ;";
        try {
            System.out.println("peoduit: " + produit);
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, produit.getNom());
            pst.setInt(2, produit.getPrix());
            pst.setString(3, produit.getImage());
            pst.setString(4, produit.getDescription());
            pst.setInt(5, produit.getQuantiteP());
            pst.setInt(6, produit.getCategorie().getId_categorie());
            pst.executeUpdate();
            System.out.println("Produit ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
     * @return List<Produit>
     */
    @Override
    public List<Produit> read() {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT  produit.* , categorie_produit.nom_categorie from produit  JOIN categorie_produit  ON produit.id_categorieProduit = categorie_produit.id_categorie";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
            int i = 0;
            while (rs.next()) {
                produits.add(new Produit(rs.getInt("id_produit"), rs.getString("nom"), rs.getInt("prix"),
                        rs.getString("image"), rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")), rs.getInt("quantiteP")));
                System.out.println(produits.get(i));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    public List<Produit> sort(String sortBy) {
        // A list of valid column names to prevent SQL injection
        List<String> validColumns = Arrays.asList("id_produit", "nom", "prix", "description", "categorieProduit",
                "quantiteP");
        List<Produit> produits = new ArrayList<>();
        // Check if sortBy is a valid column name
        if (!validColumns.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
        String req = "SELECT *, id_categorieProduit FROM produit ORDER BY " + sortBy;
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            ResultSet rs = pst.executeQuery();
            CategorieService cs = new CategorieService();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
    @Override
    public void update(Produit produit) {
        String req = "UPDATE produit p " +
                "INNER JOIN categorie_produit c ON p.id_categorieProduit = c.id_categorie " +
                "SET p.id_categorieProduit = ?, p.nom = ?, p.prix = ?, p.description = ?, p.image = ?, p.quantiteP = ? "
                +
                "WHERE p.id_produit = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(7, produit.getId_produit());
            pst.setString(2, produit.getNom());
            pst.setInt(3, produit.getPrix());
            pst.setString(4, produit.getDescription());
            pst.setString(5, produit.getImage());
            pst.setInt(6, produit.getQuantiteP());
            pst.setInt(1, produit.getCategorie().getId_categorie());
            pst.executeUpdate();
            System.out.println("produit modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Produit produit) {
        String req = "DELETE from produit where id_produit = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produit.getId_produit());
            pst.executeUpdate();
            System.out.println("produit supprmiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Produit getProduitById(int produitId) {
        Produit produit = null;
        String req = "SELECT produit.*, categorie_produit.nom_categorie FROM produit JOIN categorie_produit ON produit.id_categorieProduit = categorie_produit.id_categorie WHERE id_produit = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, produitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                CategorieService cs = new CategorieService();
                produit = new Produit(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getInt("prix"),
                        rs.getString("image"),
                        rs.getString("description"),
                        cs.getCategorie(rs.getInt("id_categorieProduit")),
                        rs.getInt("quantiteP"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }
    // Vérifier le stock disponible pour un produit donné
    public boolean verifierStockDisponible(int produitId, int quantiteDemandee) {
        Produit produit = getProduitById(produitId); // Utilisez votre méthode existante pour récupérer le produit
        if (produit != null) {
            return produit.getQuantiteP() >= quantiteDemandee;
        } else {
            return false; // Le produit n'existe pas
        }
    }
    public double getPrixProduit(int idProduit) {
        String req = "SELECT prix FROM produit WHERE id_produit = ?";
        double prixProduit = 0;
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, idProduit);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                prixProduit = rs.getDouble("prix");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prixProduit;
    }
    public List<Produit> getProduitsOrderByQuantityAndStatus() {
        List<Produit> produits = new ArrayList<>();
        String req = "SELECT p.*, ci.*, c.*, SUM(ci.quantity) AS total_quantity FROM produit p JOIN commandeitem ci ON p.id_produit = ci.id_produit JOIN commande c ON ci.idCommande = c.idCommande WHERE c.statu = 'payee' GROUP BY p.id_produit ORDER BY total_quantity DESC;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CategorieService cs = new CategorieService();
                Produit produit = new Produit(
                        resultSet.getInt("id_produit"),
                        resultSet.getString("nom"),
                        resultSet.getInt("prix"),
                        resultSet.getString("image"),
                        resultSet.getString("description"),
                        cs.getCategorie(resultSet.getInt("id_categorieProduit")),
                        resultSet.getInt("quantiteP"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}
