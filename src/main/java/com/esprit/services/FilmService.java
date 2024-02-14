package com.esprit.services;

import com.esprit.models.Film;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmService implements IService<Film> {
    Connection connection;

    public FilmService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void create(Film film) {

        String req = "insert into film (nom,image,duree,description,annederalisation,idcategory) values (?,?,?,?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setBlob(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.setInt(6, film.getIdcategory());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Film> read() {
        List<Film> filmArrayList = new ArrayList<>();
        String req = "SELECT * FROM film ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getBlob("image"), rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation"), rs.getInt("idcategory")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return filmArrayList;
    }


    @Override
    public void update(Film film) {

        String req = "UPDATE film set nom=?,image=?,duree=?,description=?,annederalisation=?,idcategory=? where id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setBlob(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.setInt(6, film.getIdcategory());
            statement.setInt(7, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void delete(Film film) {
        String req = " DELETE  FROM film";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            //statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
