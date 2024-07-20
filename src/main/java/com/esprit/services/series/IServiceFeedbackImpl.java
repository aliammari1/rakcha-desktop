package com.esprit.services.series;

import com.esprit.models.series.Feedback;
import com.esprit.services.produits.AvisService;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IServiceFeedbackImpl implements IServiceFeedback<Feedback> {
    public Connection conx;
    public Statement stm;
    private static final Logger LOGGER = Logger.getLogger(IServiceFeedbackImpl.class.getName());

    public IServiceFeedbackImpl() {
        conx = DataSource.getInstance().getConnection();
    }

    /**
     * @param a
     */
    @Override
    public void ajouter(Feedback a) {
        String req = """
                INSERT INTO feedback\
                (id_user,description,date,id_episode)\
                VALUES(?,?,?,?)\
                """;
        try {
            PreparedStatement ps = conx.prepareStatement(req);
            ps.setInt(1, a.getId_user());
            ps.setString(2, a.getDescription());
            ps.setDate(3, new Date(a.getDate().getTime()));
            ps.setInt(4, a.getId_episode());
            ps.executeUpdate();
            LOGGER.info("FeedBack Ajoutée !!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param a
     */
    @Override
    public void modifier(Feedback a) {
        try {
            String req = "UPDATE feedback SET id_user=?, description=?, date=?, id_episode=? WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(5, a.getId());
            pst.setInt(1, a.getId_user());
            pst.setString(2, a.getDescription());
            pst.setDate(3, new Date(a.getDate().getTime()));
            pst.setInt(4, a.getId_episode());
            pst.executeUpdate();
            LOGGER.info("FeedBack Modifiée !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM feedback WHERE id=?";
        try {
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            LOGGER.info("FeedBack suprimée !");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Feedback> Show() {
        List<Feedback> list = new ArrayList<>();
        try {
            String req = "SELECT * from feedback";
            Statement st = conx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Feedback(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getString("description"), rs.getDate("date"), rs.getInt("id_episode")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
}
