package com.esprit.services.series;

import com.esprit.models.series.Serie;
import com.esprit.services.series.DTO.SerieDto;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IServiceSerieImpl implements IServiceSerie<Serie> {
    private static final Logger LOGGER = Logger.getLogger(IServiceSerieImpl.class.getName());
    private final Connection connection;
    private List<Serie> seriesList; // Assurez-vous d'initialiser cette liste

    public IServiceSerieImpl() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param serie
     * @throws SQLException
     */
    @Override
    public void ajouter(final Serie serie) throws SQLException {
        final String req = "INSERT INTO series (nom, resume, directeur, pays, image, liked, nbLikes ,disliked, nbDislikes,idcategorie) VALUES (?, ?, ?, ?,?,?, ?,?, ?, ?)";
        final PreparedStatement ps = this.connection.prepareStatement(req);
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
    public void modifier(final Serie serie) throws SQLException {
        final String req = "UPDATE series set nom = ?, resume = ?,directeur = ?, pays = ?,image = ?,liked = ?,nbLikes = ? ,idcategorie = ? where idserie = ?;";
        final PreparedStatement st = this.connection.prepareStatement(req);
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
        IServiceSerieImpl.LOGGER.info("serie modifier avec succes");
    }

    @Override
    public void supprimer(final int id) throws SQLException {
        final String req = "DELETE FROM series WHERE idserie = ?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        IServiceSerieImpl.LOGGER.info("serie supprimee avec succes");
    }

    @Override
    public List<SerieDto> recuperer() throws SQLException {
        final List<SerieDto> serieDto = new ArrayList<>();
        final String req = "SELECT * FROM series ";
        final Statement st = this.connection.createStatement();
        final ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            final SerieDto s = new SerieDto();
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
            final String req2 = "SELECT categories.nom FROM categories WHERE idcategorie = ? ";
            final PreparedStatement ps = this.connection.prepareStatement(req2);
            IServiceSerieImpl.LOGGER.info(String.valueOf(s.getIdcategorie()));
            ps.setInt(1, s.getIdcategorie());
            final ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                s.setNomCategories(rs2.getString("nom"));
            }
            serieDto.add(s);
        }
        return serieDto;
    }

    @Override
    public List<Serie> recuperers() throws SQLException {
        final List<Serie> series = new ArrayList<>();
        final String req = "SELECT * FROM series";
        final Statement st = this.connection.createStatement();
        final ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            final Serie serie = new Serie();
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
    public List<Serie> recuperes(final int id) throws SQLException {
        final List<Serie> series = new ArrayList<>();
        final String req = "SELECT * FROM series Where idcategorie=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, id);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final Serie serie = new Serie();
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

    public void ajouterLike(final Serie serie) throws SQLException {
        final String req = "UPDATE series set liked = ?,nbLikes = ? where idserie = ?;";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, 1);
        ps.setInt(2, serie.getNbLikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }

    public void removeLike(final Serie serie) throws SQLException {
        final String req = "UPDATE series set liked = ?,nbLikes = ? where idserie = ?;";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, serie.getLiked());
        ps.setInt(2, serie.getNbLikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }

    public void ajouterDislike(final Serie serie) throws SQLException {
        final String req = "UPDATE series set disliked = ?,nbDislikes = ? where idserie = ?;";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, 1);
        ps.setInt(2, serie.getNbDislikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }

    public void removeDislike(final Serie serie) throws SQLException {
        final String req = "UPDATE series set disliked = ?,nbDislikes = ? where idserie = ?;";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, serie.getDisliked());
        ps.setInt(2, serie.getNbDislikes());
        ps.setInt(3, serie.getIdserie());
        ps.executeUpdate();
        ps.close();
    }

    public List<Serie> findMostLiked() {
        final List<Serie> series = new ArrayList<>();
        final String sql = "SELECT * FROM series ORDER BY nbLikes DESC LIMIT 3";
        try {
            final Statement st = this.connection.createStatement();
            final ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                final Serie serie = new Serie();
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
        } catch (final SQLException e) {
            IServiceSerieImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Handle the exception appropriately in your application
        }
        return series;
    }

    ///
    public Map<Serie, Integer> getLikesStatistics() {
        final Map<Serie, Integer> likesStatistics = new HashMap<>();
        try {
            final String query = "SELECT idserie, nbLikes FROM series";
            final Statement statement = this.connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                final Serie serie = new Serie();
                serie.setIdserie(resultSet.getInt("idserie"));
                final int nbLikes = resultSet.getInt("nbLikes");
                likesStatistics.put(serie, nbLikes);
            }
        } catch (final SQLException e) {
            IServiceSerieImpl.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Handle the exception appropriately in your application
        }
        return likesStatistics;
    }

    public Serie getByIdSerie(final int serieId) throws SQLException {
        Serie serie = null;
        final String query = "SELECT * FROM series WHERE idserie = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(query)) {
            ps.setInt(1, serieId);
            try (final ResultSet rs = ps.executeQuery()) {
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
