package com.esprit.services.films;

import com.esprit.models.films.Category;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcategory;
import com.esprit.services.IService;
import com.esprit.services.produits.AvisService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmcategoryService implements IService<Filmcategory> {
    Connection connection;
    private static final Logger LOGGER = Logger.getLogger(FilmcategoryService.class.getName());

    public FilmcategoryService() {
        connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param filmcategory
     */
    @Override
    public void create(Filmcategory filmcategory) {
        FilmService filmService = new FilmService();
        filmService.create(filmcategory.getFilmId());
        String req = "INSERT INTO film_category (film_id, category_id) VALUES (LAST_INSERT_ID(),?)";
        try {
            Category category = filmcategory.getCategoryId();
            String[] categoryNames = category.getNom().split(", ");
            PreparedStatement statement = connection.prepareStatement(req);
            for (String categoryname : categoryNames) {
                statement.setInt(1, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Filmcategory>
     */
    @Override
    public List<Filmcategory> read() {
        List<Filmcategory> filmcategoryArrayList = new ArrayList<>();
        String req = "SELECT film.*,category.id,category.description, GROUP_CONCAT(category.nom SEPARATOR ', ') AS category_names from film_category JOIN category  ON film_category.category_id = category.id JOIN film on film_category.film_id = film.id GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmcategoryArrayList.add(new Filmcategory(
                        new Category(rs.getInt("category.id"), rs.getString("category_names"),
                                rs.getString("category.description")),
                        new Film(rs.getInt("film.id"), rs.getNString("film.nom"), rs.getString("image"),
                                rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation"))));
                // LOGGER.info(filmArrayList.get(i));
                // i++;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return filmcategoryArrayList;
    }

    @Override
    public void update(Filmcategory filmcategory) {
        FilmService filmService = new FilmService();
        CategoryService categoryService = new CategoryService();
        filmService.update(filmcategory.getFilmId());
        LOGGER.info("filmCategory---------------: " + filmcategory);
        String reqDelete = "DELETE FROM film_category WHERE film_id = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(reqDelete);
            statement.setInt(1, filmcategory.getFilmId().getId());
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        String req = "INSERT INTO film_category (film_id, category_id) VALUES (?,?)";
        try {
            Category category = filmcategory.getCategoryId();
            String[] categoryNames = category.getNom().split(", ");
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(2, filmcategory.getFilmId().getId());
            for (String categoryname : categoryNames) {
                statement.setInt(2, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCategories(Film film, List<String> categoryNames) {
        FilmService filmService = new FilmService();
        CategoryService categoryService = new CategoryService();
        filmService.update(film);
        LOGGER.info("filmCategory---------------: " + film);
        String reqDelete = "DELETE FROM film_category WHERE film_id = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        String req = "INSERT INTO film_category (film_id, category_id) VALUES (?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (String categoryname : categoryNames) {
                statement.setInt(2, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Filmcategory filmcategory) {
    }

    public String getCategoryNames(int id) {
        String s = "";
        String req = "SELECT GROUP_CONCAT(category.nom SEPARATOR ', ') AS categoryNames from film_category JOIN category  ON film_category.category_id = category.id JOIN film on film_category.film_id = film.id where film.id = ? GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            rs.next();
            s = rs.getString("categoryNames");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return s;
    }
}
