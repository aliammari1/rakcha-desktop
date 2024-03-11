package com.esprit.services.series;

import com.esprit.models.series.Categorie;
import com.esprit.utils.mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IServiceCategorieImpl implements IServiceCategorie<Categorie> {
    private static List<Categorie> categories;
    private final Connection connection;

    public IServiceCategorieImpl() {
        connection = mydatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Categorie categorie) throws SQLException {

        String req = "INSERT INTO categories (nom, description) VALUES ('" + categorie.getNom() + "','" + categorie.getDescription() + "')";
        Statement st = connection.createStatement();
        st.executeUpdate(req);
        System.out.println("Categorie ajoutee avec succes");
    }

    @Override
    public void modifier(Categorie categorie) throws SQLException {
        String req = "UPDATE categories SET nom = ?, description = ? WHERE idcategorie = ?";
        PreparedStatement os = connection.prepareStatement(req);
        os.setString(1, categorie.getNom());
        os.setString(2, categorie.getDescription());
        os.setInt(3, categorie.getIdcategorie());
        os.executeUpdate();
        System.out.println("Categorie modifiee avec succes");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM categories WHERE idcategorie = ?";
        PreparedStatement os = connection.prepareStatement(req);
        os.setInt(1, id);
        os.executeUpdate();
        System.out.println("Categorie supprimee avec succes");
    }

    @Override
    public List<Categorie> recuperer() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String req = "SELECT * FROM categories";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);


        while (rs.next()) {
            Categorie categorie = new Categorie();
            categorie.setIdcategorie(rs.getInt("idcategorie"));
            categorie.setNom(rs.getString("nom"));
            categorie.setDescription(rs.getString("description"));
            categories.add(categorie);

        }
        System.out.println(categories);
        return categories;

    }


    public Map<Categorie, Long> getCategoriesStatistics() {
        Map<Categorie, Long> statistics = new HashMap<>();

        try {
            String query = "SELECT categories.*, COUNT(series.idserie) as series_count " +
                    "FROM categories " +
                    "LEFT JOIN series ON categories.idcategorie = series.idcategorie " +
                    "GROUP BY categories.idcategorie";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Categorie categorie = new Categorie();
                categorie.setIdcategorie(resultSet.getInt("idcategorie"));
                categorie.setNom(resultSet.getString("nom"));
                categorie.setDescription(resultSet.getString("description"));

                long seriesCount = resultSet.getLong("series_count");
                statistics.put(categorie, seriesCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statistics;
    }
}



