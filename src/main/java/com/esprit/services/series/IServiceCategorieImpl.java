package com.esprit.services.series;

import com.esprit.models.series.Categorie;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IServiceCategorieImpl implements IServiceCategorie<Categorie> {
    private static final Logger LOGGER = Logger.getLogger(IServiceCategorieImpl.class.getName());
    private static List<Categorie> categories;
    private final Connection connection;

    public IServiceCategorieImpl() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param categorie
     * @throws SQLException
     */
    @Override
    public void ajouter(final Categorie categorie) throws SQLException {
        final String req = "INSERT INTO categories (nom, description) VALUES (?, ?)";
        final PreparedStatement st = this.connection.prepareStatement(req);
        st.setString(1, categorie.getNom());
        st.setString(2, categorie.getDescription());
        st.executeUpdate();
        IServiceCategorieImpl.LOGGER.info("Categorie ajoutee avec succes");
    }

    /**
     * @param categorie
     * @throws SQLException
     */
    @Override
    public void modifier(final Categorie categorie) throws SQLException {
        final String req = "UPDATE categories SET nom = ?, description = ? WHERE idcategorie = ?";
        final PreparedStatement os = this.connection.prepareStatement(req);
        os.setString(1, categorie.getNom());
        os.setString(2, categorie.getDescription());
        os.setInt(3, categorie.getIdcategorie());
        os.executeUpdate();
        IServiceCategorieImpl.LOGGER.info("Categorie modifiee avec succes");
    }

    @Override
    public void supprimer(final int id) throws SQLException {
        final String req = "DELETE FROM categories WHERE idcategorie = ?";
        final PreparedStatement os = this.connection.prepareStatement(req);
        os.setInt(1, id);
        os.executeUpdate();
        IServiceCategorieImpl.LOGGER.info("Categorie supprimee avec succes");
    }

    @Override
    public List<Categorie> recuperer() throws SQLException {
        final List<Categorie> categories = new ArrayList<>();
        final String req = "SELECT * FROM categories";
        final Statement st = this.connection.createStatement();
        final ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            final Categorie categorie = new Categorie();
            categorie.setIdcategorie(rs.getInt("idcategorie"));
            categorie.setNom(rs.getString("nom"));
            categorie.setDescription(rs.getString("description"));
            categories.add(categorie);
        }
        IServiceCategorieImpl.LOGGER.info(categories.toString());
        return categories;
    }

    @Override
    public Map<Categorie, Long> getCategoriesStatistics() {
        final Map<Categorie, Long> statistics = new HashMap<>();
        try {
            final String query = """
                    SELECT categories.*, COUNT(series.idserie) as series_count \
                    FROM categories \
                    LEFT JOIN series ON categories.idcategorie = series.idcategorie \
                    GROUP BY categories.idcategorie\
                    """;
            final PreparedStatement statement = this.connection.prepareStatement(query);
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Categorie categorie = new Categorie();
                categorie.setIdcategorie(resultSet.getInt("idcategorie"));
                categorie.setNom(resultSet.getString("nom"));
                categorie.setDescription(resultSet.getString("description"));
                final long seriesCount = resultSet.getLong("series_count");
                statistics.put(categorie, seriesCount);
            }
        } catch (final SQLException e) {
            IServiceCategorieImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return statistics;
    }
}
