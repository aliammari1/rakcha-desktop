package com.esprit.services.series;
import com.esprit.models.series.Serie;
import com.esprit.services.series.DTO.SerieDto;
import com.esprit.utils.mydatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class IServiceSerieImpl implements IServiceSerie<Serie> {
    private final Connection connection;
    private List<Serie> seriesList; // Assurez-vous d'initialiser cette liste
    public IServiceSerieImpl() {
        connection = mydatabase.getInstance().getConnection();
    }
    /** 
     * @param serie
     * @throws SQLException
     */
    @Override
    public void ajouter(Serie serie) throws SQLException {
        String req = "INSERT INTO series (nom, resume, directeur, pays, image, liked, nbLikes ,disliked, nbDislikes,idcategorie) VALUES (?, ?, ?, ?,?,?, ?,?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, serie.getNom());
        ps.setString(2, serie.getResume());
        ps.setString(3, serie.getDirecteur());
        ps.setString(4, serie.getPays());
        ps.setString(5, serie.getImage());
        ps.setInt(6, 0);
        ps.setInt(7, 0);
        ps.setInt(8, 0);
        ps.setInt(9, 0);
        ps.setInt(10, serie.getIdcategorie());
        ps.executeUpdate();
        ps.close();
    }
    /** 
     * @param serie
     * @throws SQLException
     */
    @Override
    public void modifier(Serie serie) throws SQLException {
        String req = "UPDATE series set nom = ?, resume = ?,directeur = ?, pays = ?,image = ?,liked = ?,nbLikes = ? ,idcategorie = ? where idserie = ?;";
        PreparedStatement st = connection.prepareStatement(req);
        st.setString(1, serie.getNom());
        st.setString(2, serie.getResume());
        st.setString(3, serie.getDirecteur());
        st.setString(4, serie.getPays());
        st.setString(5, serie.getImage());
        st.setInt(6, serie.getLiked());
        st.setInt(7, serie.getNbLikes());
        st.setInt(8, serie.getIdcategorie());
        st.setInt(9, serie.getIdserie());
        st.executeUpdate();
        System.out.println("serie modifier avec succes");
    }
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM series WHERE idserie = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("serie supprimee avec succes");
    }
    @Override
    public List<SerieDto> recuperer() throws SQLException {
        List<SerieDto> serieDto = new ArrayList<>();
        String req = "SELECT * FROM series ";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            SerieDto s = new SerieDto();
            s.setIdserie(rs.getInt("idserie"));
            s.setNom(rs.getString("nom"));
            s.setResume(rs.getString("resume"));
            s.setDirecteur(rs.getString("directeur"));
            s.setPays(rs.getString("pays"));
            s.setImage(rs.getString("image"));
            s.setLiked(rs.getInt("liked"));
            s.setNbLikes(rs.getInt("nbLikes"));
            s.setLiked(rs.getInt("disliked"));
            s.setNbLikes(rs.getInt("nbDislikes"));
            s.setIdcategorie(rs.getInt("idcategorie"));
            String req2 = "SELECT categories.nom FROM categories WHERE idcategorie = ? ";
            PreparedStatement ps = connection.prepareStatement(req2);
            System.out.println(s.getIdcategorie());
            ps.setInt(1, s.getIdcategorie());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                s.setNomCategories(rs2.getString("nom"));
            }
            serieDto.add(s);
        }
        return serieDto;
    }
    @Override
    public List<Serie> recuperers() throws SQLException {
        List<Serie> series = new ArrayList<>();
        String req = "SELECT * FROM series";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Serie serie = new Serie();
            serie.setIdserie(rs.getInt("idserie"));
            serie.setNom(rs.getString("nom"));
            serie.setResume(rs.getString("resume"));
            serie.setDirecteur(rs.getString("directeur"));
            serie.setPays(rs.getString("pays"));
            serie.setImage(rs.getString("image"));
            serie.setLiked(rs.getInt("liked"));
            serie.setNbLikes(rs.getInt("nbLikes"));
            serie.setDisliked(rs.getInt("disliked"));
            serie.setNbDislikes(rs.getInt("nbDislikes"));
            serie.setIdcategorie(rs.getInt("idcategorie"));
            series.add(serie);
        }
        return series;
    }
    @Override
    public List<Serie> recuperes(int id) throws SQLException {
        List<Serie> series = new ArrayList<>();
        String req = "SELECT * FROM series Where idcategorie=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Serie serie = new Serie();
            serie.setIdserie(rs.getInt("idserie"));
            serie.setNom(rs.getString("nom"));
            serie.setResume(rs.getString("resume"));
            serie.setDirecteur(rs.getString("directeur"));
            serie.setPays(rs.getString("pays"));
            serie.setImage(rs.getString("image"));
            serie.setLiked(rs.getInt("liked"));
            serie.setNbLikes(rs.getInt("nbLikes"));
            serie.setLiked(rs.getInt("disliked"));
            serie.setNbLikes(rs.getInt("nbDislikes"));
            serie.setIdcategorie(rs.getInt("idcategorie"));
            series.add(serie);
        }
        return series;
    }
    public void ajouterLike(Serie serie) throws SQLException {
        String req = "UPDATE series set liked = ?,nbLikes = ? where idserie = ?;";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, 1);
        ps.setInt(2, serie.getNbLikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }
    public void removeLike(Serie serie) throws SQLException {
        String req = "UPDATE series set liked = ?,nbLikes = ? where idserie = ?;";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, serie.getLiked());
        ps.setInt(2, serie.getNbLikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }
    public void ajouterDislike(Serie serie) throws SQLException {
        String req = "UPDATE series set disliked = ?,nbDislikes = ? where idserie = ?;";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, 1);
        ps.setInt(2, serie.getNbDislikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }
    public void removeDislike(Serie serie) throws SQLException {
        String req = "UPDATE series set disliked = ?,nbDislikes = ? where idserie = ?;";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, serie.getDisliked());
        ps.setInt(2, serie.getNbDislikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }
    public List<Serie> findMostLiked() {
        List<Serie> series = new ArrayList<>();
        String sql = "SELECT * FROM series ORDER BY nbLikes DESC LIMIT 3";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Serie serie = new Serie();
                serie.setIdserie(rs.getInt("idserie"));
                serie.setNom(rs.getString("nom"));
                serie.setResume(rs.getString("resume"));
                serie.setDirecteur(rs.getString("directeur"));
                serie.setPays(rs.getString("pays"));
                serie.setImage(rs.getString("image"));
                serie.setLiked(rs.getInt("liked"));
                serie.setNbLikes(rs.getInt("nbLikes"));
                serie.setDisliked(rs.getInt("disliked"));
                serie.setNbDislikes(rs.getInt("nbDislikes"));
                serie.setIdcategorie(rs.getInt("idcategorie"));
                series.add(serie);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
        return series;
    }
    ///
    public Map<Serie, Integer> getLikesStatistics() {
        Map<Serie, Integer> likesStatistics = new HashMap<>();
        try {
            String query = "SELECT idserie, nbLikes FROM series";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Serie serie = new Serie();
                serie.setIdserie(resultSet.getInt("idserie"));
                int nbLikes = resultSet.getInt("nbLikes");
                likesStatistics.put(serie, nbLikes);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
        return likesStatistics;
    }
    public Serie getByIdSerie(int serieId) throws SQLException {
        Serie serie = null;
        String query = "SELECT * FROM series WHERE idserie = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, serieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming Serie is a class representing your series data
                    serie = new Serie();
                    serie.setIdserie(rs.getInt("idserie"));
                    serie.setNom(rs.getString("nom"));
                    serie.setResume(rs.getString("resume"));
                    serie.setDirecteur(rs.getString("directeur"));
                    serie.setPays(rs.getString("pays"));
                    serie.setImage(rs.getString("image"));
                    serie.setLiked(rs.getInt("liked"));
                    serie.setNbLikes(rs.getInt("nbLikes"));
                    serie.setDisliked(rs.getInt("disliked"));
                    serie.setNbDislikes(rs.getInt("nbDislikes"));
                    serie.setIdcategorie(rs.getInt("idcategorie"));
                }
            }
        }
        return serie;
    }
}
