package com.esprit.services.cinemas;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class CinemaService implements IService<Cinema> {
    private final Connection connection;
    public CinemaService() {
        connection = DataSource.getInstance().getConnection();
    }
    /** 
     * @param cinema
     */
    public void create(Cinema cinema) {
        String req = "INSERT into cinema(nom, adresse, responsable, logo, Statut) values (?, ?, ?, ?, ?);";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setInt(3, cinema.getResponsable().getId());
            pst.setString(4, cinema.getLogo());
            // Définition de la valeur par défaut pour le champ Statut
            pst.setString(5, cinema.getStatut() != null ? cinema.getStatut() : "Pending");
            pst.executeUpdate();
            System.out.println("Cinéma ajouté !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** 
     * @param cinema
     */
    public void update(Cinema cinema) {
        String req = "UPDATE cinema set nom = ?, adresse = ?, logo = ?, Statut = ? where id_cinema = ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(5, cinema.getId_cinema());
            pst.setString(1, cinema.getNom());
            pst.setString(2, cinema.getAdresse());
            pst.setString(3, cinema.getLogo());
            pst.setString(4, cinema.getStatut());
            pst.executeUpdate();
            System.out.println("Cinéma modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(Cinema cinema) {
        String req = "DELETE from cinema where id_cinema= ?;";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, cinema.getId_cinema());
            pst.executeUpdate();
            System.out.println("Cinema supprmiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Cinema> read() {
        List<Cinema> cinemas = new ArrayList<>();
        String req = "SELECT * from cinema";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                        (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                        rs.getString("logo"), rs.getString("Statut")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinemas;
    }
    public List<Cinema> sort(String p) {
        List<Cinema> cinemas = new ArrayList<>();
        String req = "SELECT * from cinema ORDER BY " + p;
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cinemas.add(new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                        (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                        rs.getString("logo"), rs.getString("Statut")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinemas;
    }
    public Cinema getCinema(int cinema_id) {
        Cinema cinema = null;
        String req = "SELECT * from cinema where id_cinema = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, cinema_id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            cinema = new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                    (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                    rs.getString("logo"), rs.getString("Statut"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinema;
    }
    public Cinema getCinemaByName(String nom_cinema) {
        Cinema cinema = null;
        String req = "SELECT * from cinema where nom = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, nom_cinema);
            ResultSet rs = pst.executeQuery();
            rs.next();
            cinema = new Cinema(rs.getInt("id_cinema"), rs.getString("nom"), rs.getString("adresse"),
                    (Responsable_de_cinema) new UserService().getUserById(rs.getInt("responsable")),
                    rs.getString("logo"), rs.getString("Statut"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cinema;
    }
}
