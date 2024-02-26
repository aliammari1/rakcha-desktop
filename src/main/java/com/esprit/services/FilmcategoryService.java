package com.esprit.services;

import com.esprit.models.Category;
import com.esprit.models.Film;
import com.esprit.models.Filmcategory;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmcategoryService implements IService<Filmcategory> {
    Connection connection;

    public FilmcategoryService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Filmcategory filmcategory) {
        FilmService filmService = new FilmService();
        filmService.create(filmcategory.getFilmId());
        String req = "INSERT INTO filmcategory (film_id, category_id) VALUES (LAST_INSERT_ID(),?)";
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

    @Override
    public List<Filmcategory> read() {
        List<Filmcategory> filmcategoryArrayList = new ArrayList<>();
        String req = "SELECT film.*,category.id,category.description, GROUP_CONCAT(category.nom SEPARATOR ', ') AS category_names from filmcategory JOIN category  ON filmcategory.category_id  = category.id JOIN film on filmcategory.film_id  = film.id GROUP BY film.id;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmcategoryArrayList.add(new Filmcategory(new Category(rs.getInt("category.id"), rs.getString("category_names"), rs.getString("category.description")), new Film(rs.getInt("film.id"), rs.getNString("film.nom"), rs.getBlob("image"), rs.getTime("duree"), rs.getString("film.description"), rs.getInt("annederalisation"), rs.getInt("idcinema"))));
                //     System.out.println(filmArrayList.get(i));
                //       i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return filmcategoryArrayList;
    }

    @Override
    public void update(Filmcategory filmcategory) {

    }

    @Override
    public void delete(Filmcategory filmcategory) {

    }
}
