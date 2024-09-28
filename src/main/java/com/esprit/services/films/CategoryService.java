package com.esprit.services.films;

import com.esprit.models.films.Category;
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

public class CategoryService implements IService<Category> {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    Connection connection;

    public CategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param category
     */
    @Override
    public void create(final Category category) {
        final String req = "insert into category (nom,description) values (?,?) ";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getNom());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Category>
     */
    @Override
    public List<Category> read() {
        final List<Category> categoryArrayList = new ArrayList<>();
        final String req = "SELECT * FROM category";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categoryArrayList.add(new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description")));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryArrayList;
    }

    @Override
    public void update(final Category category) {
        final String req = "UPDATE category set nom=?,description=? where id=?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, category.getNom());
            statement.setString(2, category.getDescription());
            statement.setInt(3, category.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final Category category) {
        final String req = "DELETE FROM category where id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, category.getId());
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Category getCategory(final int id) {
        Category category = null;
        final String req = "SELECT * FROM category where id = ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, id);
            final ResultSet rs = statement.executeQuery();
            rs.next();
            category = new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description"));
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return category;
    }

    public Category getCategoryByNom(final String nom) {
        Category category = null;
        final String req = "SELECT * FROM category where nom LIKE ?";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setString(1, nom);
            final ResultSet rs = statement.executeQuery();
            rs.next();
            category = new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description"));
        } catch (final SQLException e) {
            CategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return category;
    }
}
