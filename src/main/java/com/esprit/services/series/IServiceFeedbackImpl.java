package com.esprit.services.series;

import com.esprit.models.series.Feedback;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IServiceFeedbackImpl implements IServiceFeedback<Feedback> {
    private static final Logger LOGGER = Logger.getLogger(IServiceFeedbackImpl.class.getName());
    public Connection conx;
    public Statement stm;

    public IServiceFeedbackImpl() {
        this.conx = DataSource.getInstance().getConnection();
    }

    /**
     * @param a
     */
    @Override
    public void ajouter(final Feedback a) {
        final String req = """
                INSERT INTO feedback\
                (id_user,description,date,id_episode)\
                VALUES(?,?,?,?)\
                """;
        try {
            final PreparedStatement ps = this.conx.prepareStatement(req);
            ps.setInt(1, a.getId_user());
            ps.setString(2, a.getDescription());
            ps.setDate(3, new Date(a.getDate().getTime()));
            ps.setInt(4, a.getId_episode());
            ps.executeUpdate();
            IServiceFeedbackImpl.LOGGER.info("FeedBack Ajoutée !!");
        } catch (final SQLException e) {
            IServiceFeedbackImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param a
     */
    @Override
    public void modifier(final Feedback a) {
        try {
            final String req = "UPDATE feedback SET id_user=?, description=?, date=?, id_episode=? WHERE id=?";
            final PreparedStatement pst = this.conx.prepareStatement(req);
            pst.setInt(5, a.getId());
            pst.setInt(1, a.getId_user());
            pst.setString(2, a.getDescription());
            pst.setDate(3, new Date(a.getDate().getTime()));
            pst.setInt(4, a.getId_episode());
            pst.executeUpdate();
            IServiceFeedbackImpl.LOGGER.info("FeedBack Modifiée !");
        } catch (final SQLException e) {
            IServiceFeedbackImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(final int id) throws SQLException {
        final String req = "DELETE FROM feedback WHERE id=?";
        try {
            final PreparedStatement pst = this.conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            IServiceFeedbackImpl.LOGGER.info("FeedBack suprimée !");
        } catch (final SQLException e) {
            IServiceFeedbackImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Feedback> Show() {
        final List<Feedback> list = new ArrayList<>();
        try {
            final String req = "SELECT * from feedback";
            final Statement st = this.conx.createStatement();
            final ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Feedback(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getString("description"), rs.getDate("date"), rs.getInt("id_episode")));
            }
        } catch (final SQLException e) {
            IServiceFeedbackImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}
