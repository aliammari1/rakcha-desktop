package com.esprit.services.films;

import com.esprit.models.films.Filmcoment;
import com.esprit.models.users.Client;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilmcomentService implements IService<Filmcoment> {
    private static final Logger LOGGER = Logger.getLogger(FilmcomentService.class.getName());
    private final Connection connection;

    public FilmcomentService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param filmcoment
     */
    @Override
    public void create(final Filmcoment filmcoment) {
        final String req = "INSERT into filmcoment(comment,user_id,film_id ) values (?,?,?);";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            pst.setString(1, filmcoment.getComment());
            pst.setInt(2, filmcoment.getUser_id().getId());
            pst.setInt(3, filmcoment.getFilm_id().getId());
            pst.executeUpdate();
            FilmcomentService.LOGGER.info("commentaire ajoutée !");
        } catch (final SQLException e) {
            FilmcomentService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @return List<Filmcoment>
     */
    @Override
    public List<Filmcoment> read() {
        final List<Filmcoment> commentaire = new ArrayList<>();
        final String req = "SELECT * FROM filmcoment";
        try {
            final PreparedStatement pst = this.connection.prepareStatement(req);
            final ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire.add(new Filmcoment(
                        rs.getInt("id"),
                        rs.getString("comment"),
                        (Client) new UserService().getUserById(rs.getInt("user_id")),
                        new FilmService().getFilm(rs.getInt("film_id"))));
            }
        } catch (final SQLException e) {
            FilmcomentService.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return commentaire;
    }

    @Override
    public void update(final Filmcoment filmcoment) {
    }

    @Override
    public void delete(final Filmcoment filmcoment) {
    }
}
