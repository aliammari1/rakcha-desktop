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
    static private int filmLastInsertID;
    Connection connection;

    public FilmService() {
        connection = DataSource.getInstance().getConnection();
    }

    public static int getFilmLastInsertID() {
        return filmLastInsertID;
    }

    @Override
    public void create(Film film) {

        String req = "insert into film (nom,image,duree,description,annederalisation) values (?,?,?,?,?) ";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setBlob(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.executeUpdate();

            String selectSql = "SELECT LAST_INSERT_ID()";
            PreparedStatement selectPs = connection.prepareStatement(selectSql);
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                filmLastInsertID = rs.getInt(1);
                System.out.println(filmLastInsertID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Film> read() {
        List<Film> filmArrayList = new ArrayList<>();
        String req = "SELECT * from film";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            // int i = 0;
            while (rs.next()) {
                filmArrayList.add(new Film(rs.getInt("id"), rs.getString("nom"), rs.getBlob("image"), rs.getTime("duree"), rs.getString("description"), rs.getInt("annederalisation"), rs.getInt("idcinema")));
                //     System.out.println(filmArrayList.get(i));
                //       i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return filmArrayList;
    }

    @Override
    public void update(Film film) {

        String req = "UPDATE film set nom=?,image=?,duree=?,description=?,annederalisation=? where id=?;";
        try {
            System.out.println("uodate: " + film);
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setString(1, film.getNom());
            statement.setBlob(2, film.getImage());
            statement.setTime(3, film.getDuree());
            statement.setString(4, film.getDescription());
            statement.setInt(5, film.getAnnederalisation());
            statement.setInt(6, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void delete(Film film) {
        String req = "DELETE FROM film where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, film.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
