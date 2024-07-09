package com.esprit.services.series;
import com.esprit.models.series.Favoris;
import com.esprit.utils.mydatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class IServiceFavorisImpl implements IServiceFavoris<Favoris> {
    public Connection conx;
    public Statement stm;
    public IServiceFavorisImpl() {
        conx = mydatabase.getInstance().getConnection();
    }
    /** 
     * @param a
     */
    @Override
    public void ajouter(Favoris a) {
        String req =
                "INSERT INTO favoris"
                        + "(id_user,id_serie)"
                        + "VALUES(?,?)";
        try {
            PreparedStatement ps = conx.prepareStatement(req);
            ps.setInt(1, a.getId_user());
            ps.setInt(2, a.getId_serie());
            ps.executeUpdate();
            System.out.println("Favoris Ajoutée !!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    /** 
     * @param a
     */
    @Override
    public void modifier(Favoris a) {
        try {
            String req = "UPDATE favoris SET id_user=?, id_serie=? WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(3, a.getId());
            pst.setInt(1, a.getId_user());
            pst.setInt(2, a.getId_serie());
            pst.executeUpdate();
            System.out.println("Favoris Modifiée !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM favoris WHERE id=?";
        try {
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Favoris suprimée !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @Override
    public List<Favoris> Show() {
        List<Favoris> list = new ArrayList<>();
        try {
            String req = "SELECT * from favoris";
            Statement st = conx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Favoris(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getInt("id_serie")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
    public Favoris getByIdUserAndIdSerie(int userId, int serieId) throws SQLException {
        Favoris fav = null;
        String query = "SELECT * FROM favoris WHERE id_user = ? AND id_serie = ?";
        try (PreparedStatement ps = conx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, serieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming Serie is a class representing your series data
                    fav = new Favoris();
                    fav.setId(rs.getInt("id"));
                    fav.setId_user(rs.getInt("id_user"));
                    fav.setId_serie(rs.getInt("id_serie"));
                    // Set other properties accordingly
                }
            }
        }
        return fav;
    }
    public List<Favoris> afficherListeFavoris(int userId) {
        List<Favoris> list = new ArrayList<>();
        try {
            String req = "SELECT * from favoris WHERE id_user = ?";
            PreparedStatement ps = conx.prepareStatement(req);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Favoris(rs.getInt("id"), rs.getInt("id_user"),
                        rs.getInt("id_serie")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
}
