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

public class CategoryService implements IService<Category> {
    Connection connection;

    public CategoryService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Category category) {

        String req = "insert into category (nom,description) values (?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, category.getNom());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Category> read() {
        List<Category> categoryArrayList = new ArrayList<>();
        String req = "SELECT * FROM category";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                categoryArrayList.add(new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryArrayList;
    }


    @Override
    public void update(Category category) {

        String req = "UPDATE category set nom=?,description=? where id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, category.getNom());
            statement.setString(2, category.getDescription());
            statement.setInt(3, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void delete(Category category) {
        String req = "DELETE FROM category where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Category getCategory(int id) {
        Category category = null;
        String req = "SELECT * FROM category where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();
            category = new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public Category getCategoryByNom(String nom) {
        Category category = null;
        String req = "SELECT * FROM category where nom LIKE ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, nom);
            ResultSet rs = statement.executeQuery();
            rs.next();
            category = new Category(rs.getInt("id"), rs.getString("nom"), rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
}
