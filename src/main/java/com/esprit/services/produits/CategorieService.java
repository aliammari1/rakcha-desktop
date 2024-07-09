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
public class CategorieService implements IService<Categorie_Produit> {
    private final Connection connection;
    public CategorieService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param categorieProduit
     */
    @Override
    public void create(Categorie_Produit categorieProduit) {
        String req = "INSERT into categorie_produit(nom_categorie, description) values (?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorieProduit.getNom_categorie());
            pst.setString(2, categorieProduit.getDescription());
            pst.executeUpdate();
            System.out.println("Categorie ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
     * @return List<Categorie_Produit>
     */
    @Override
    public List<Categorie_Produit> read() {
        List<Categorie_Produit> categories = new ArrayList<>();
        String req = "SELECT * from categorie_produit";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    @Override
    public void update(Categorie_Produit categorieProduit) {
        String req = "UPDATE categorie_produit set nom_categorie = ?,  description = ? where id_categorie=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorieProduit.getNom_categorie());
            pst.setString(2, categorieProduit.getDescription());
            pst.setInt(3, categorieProduit.getId_categorie());
            pst.executeUpdate();
            System.out.println("categorie modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Categorie_Produit categorieProduit) {
        String req = "DELETE from categorie_produit where id_categorie = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorieProduit.getId_categorie());
            pst.executeUpdate();
            System.out.println("categorie supprmiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Categorie_Produit getCategorie(int categorie_id) {
        Categorie_Produit category = null;
        String req = "SELECT * from categorie_produit where id_categorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, categorie_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                    rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
    public Categorie_Produit getCategorieByNom(String categorie_nom) {
        Categorie_Produit category = null;
        String req = "SELECT * from categorie_produit where nom_categorie = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, categorie_nom);
            ResultSet rs = pst.executeQuery();
            rs.next();
            category = new Categorie_Produit(rs.getInt("id_categorie"), rs.getString("nom_categorie"),
                    rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
    // Ajoutez cette méthode pour récupérer tous les noms de catégories
    public List<String> getAllCategoriesNames() {
        List<String> categorieNames = new ArrayList<>();
        // Remplacez "getAllCategories()" par la méthode réelle qui récupère toutes les
        // catégories
        List<Categorie_Produit> categories = read();
        // Ajoutez les noms de catégories à la liste
        for (Categorie_Produit categorieProduit : categories) {
            categorieNames.add(categorieProduit.getNom_categorie());
        }
        return categorieNames;
    }
    public List<String> getAllCategories() {
        List<String> categorieNames = new ArrayList<>();
        // Remplacez "getAllCategories()" par la méthode réelle qui récupère toutes les
        // catégories
        List<Categorie_Produit> categories = read();
        // Ajoutez les noms de catégories à la liste
        for (Categorie_Produit categorieProduit : categories) {
            categorieNames.add(categorieProduit.getNom_categorie());
        }
        return categorieNames;
    }
    public List<Categorie_Produit> searchCategoriesByName(String searchKeyword) {
        List<Categorie_Produit> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM categorie_produit WHERE nom_categorie LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + searchKeyword + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idCategorie = resultSet.getInt("id_categorie");
                String nomCategorie = resultSet.getString("nom_categorie");
                // Ajoutez d'autres colonnes si nécessaire
                Categorie_Produit categorie = new Categorie_Produit(idCategorie, nomCategorie, null);
                // Initialisez d'autres propriétés si nécessaire
                result.add(categorie);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
