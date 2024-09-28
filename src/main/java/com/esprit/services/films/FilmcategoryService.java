package com.esprit.services.films;

import com.esprit.models.films.Category;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcategory;
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

public class FilmcategoryService implements IService<Filmcategory> {
    private static final Logger LOGGER = Logger.getLogger(FilmcategoryService.class.getName());
    Connection connection;

    public FilmcategoryService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param filmcategory
     */
    @Override
    public void create(final Filmcategory filmcategory) {
        final FilmService filmService = new FilmService();
        filmService.create(filmcategory.getFilmId());
        final String req = "INSERT INTO film_category (film_id, category_id) VALUES (LAST_INSERT_ID(),?)";
        try {
            final Category category = filmcategory.getCategoryId();
            final String[] categoryNames = category.getNom().split(", ");
            final PreparedStatement statement = this.connection.prepareStatement(req);
            for (final String categoryname : categoryNames) {
                statement.setInt(1, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return List<Filmcategory>
     */
    @Override
    public List<Filmcategory> read() {
        final List<Filmcategory> filmcategoryArrayList = new ArrayList<>();
        final String req = "SELECT film.*,category.id,category.description, GROUP_CONCAT(category.nom SEPARATOR ', ') AS category_names from film_category JOIN category  ON film_category.category_id = category.id JOIN film on film_category.film_id = film.id GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
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
        } catch (final SQLException e) {
            FilmcategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return filmcategoryArrayList;
    }

    @Override
    public void update(final Filmcategory filmcategory) {
        final FilmService filmService = new FilmService();
        final CategoryService categoryService = new CategoryService();
        filmService.update(filmcategory.getFilmId());
        FilmcategoryService.LOGGER.info("filmCategory---------------: " + filmcategory);
        final String reqDelete = "DELETE FROM film_category WHERE film_id = ?;";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(reqDelete);
            statement.setInt(1, filmcategory.getFilmId().getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            FilmcategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        final String req = "INSERT INTO film_category (film_id, category_id) VALUES (?,?)";
        try {
            final Category category = filmcategory.getCategoryId();
            final String[] categoryNames = category.getNom().split(", ");
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(2, filmcategory.getFilmId().getId());
            for (final String categoryname : categoryNames) {
                statement.setInt(2, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCategories(final Film film, final List<String> categoryNames) {
        final FilmService filmService = new FilmService();
        final CategoryService categoryService = new CategoryService();
        filmService.update(film);
        FilmcategoryService.LOGGER.info("filmCategory---------------: " + film);
        final String reqDelete = "DELETE FROM film_category WHERE film_id = ?;";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(reqDelete);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            FilmcategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        final String req = "INSERT INTO film_category (film_id, category_id) VALUES (?,?)";
        try {
            final PreparedStatement statement = this.connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            for (final String categoryname : categoryNames) {
                statement.setInt(2, new CategoryService().getCategoryByNom(categoryname).getId());
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            FilmcategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final Filmcategory filmcategory) {
    }

    public String getCategoryNames(final int id) {
        String s = "";
        final String req = "SELECT GROUP_CONCAT(category.nom SEPARATOR ', ') AS categoryNames from film_category JOIN category  ON film_category.category_id = category.id JOIN film on film_category.film_id = film.id where film.id = ? GROUP BY film.id;";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setInt(1, id);
            final ResultSet rs = pst.executeQuery();
            // int i = 0;
            rs.next();
            s = rs.getString("categoryNames");
        } catch (final SQLException e) {
            FilmcategoryService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return s;
    }
}
