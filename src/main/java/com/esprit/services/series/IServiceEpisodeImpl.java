package com.esprit.services.series;

import com.esprit.models.series.Episode;
import com.esprit.services.series.DTO.EpisodeDto;
import com.esprit.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IServiceEpisodeImpl implements IServiceEpisode<Episode> {
    private static final Logger LOGGER = Logger.getLogger(IServiceEpisodeImpl.class.getName());
    private final Connection connection;

    public IServiceEpisodeImpl() {
        this.connection = DataSource.getInstance().getConnection();
    }

    /**
     * @param episode
     * @throws SQLException
     */
    @Override
    public void ajouter(final Episode episode) throws SQLException {
        final String req = "INSERT INTO episodes (titre, numeroepisode, saison, image, video, idserie) VALUES (?, ?, ?, ?, ?, ?)";
        final PreparedStatement st = this.connection.prepareStatement(req);
        st.setString(1, episode.getTitre());
        st.setInt(2, episode.getNumeroepisode());
        st.setInt(3, episode.getSaison());
        st.setString(4, episode.getImage());
        st.setString(5, episode.getVideo());
        st.setInt(6, episode.getIdserie());
        st.executeUpdate();
        IServiceEpisodeImpl.LOGGER.info("episode ajoutee avec succes");
    }

    /**
     * @param episode
     * @throws SQLException
     */
    @Override
    public void modifier(final Episode episode) throws SQLException {
        final String req = "UPDATE episodes set titre = ?, numeroepisode = ?,saison = ?, image = ?,video = ? ,idserie = ?  where idepisode = ?;";
        final PreparedStatement st = this.connection.prepareStatement(req);
        st.setString(1, episode.getTitre());
        st.setInt(2, episode.getNumeroepisode());
        st.setInt(3, episode.getSaison());
        st.setString(4, episode.getImage());
        st.setString(5, episode.getVideo());
        st.setInt(6, episode.getIdserie());
        st.setInt(7, episode.getIdepisode());
        st.executeUpdate();
        IServiceEpisodeImpl.LOGGER.info("episode modifier avec succes");
    }

    @Override
    public void supprimer(final int id) throws SQLException {
        final String req = "DELETE FROM episodes WHERE idepisode = ?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        IServiceEpisodeImpl.LOGGER.info("episode supprimee avec succes");
    }

    @Override
    public List<EpisodeDto> recuperer() throws SQLException {
        final List<EpisodeDto> episodeDtos = new ArrayList<>();
        final String req = "SELECT * FROM episodes ";
        final Statement st = this.connection.createStatement();
        final ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            final EpisodeDto e = new EpisodeDto();
            e.setIdepisode(rs.getInt("idepisode"));
            e.setTitre(rs.getString("titre"));
            e.setNumeroepisode(rs.getInt("numeroepisode"));
            e.setSaison(rs.getInt("saison"));
            e.setImage(rs.getString("image"));
            e.setVideo(rs.getString("video"));
            e.setIdserie(rs.getInt("idserie"));
            final String req2 = "SELECT series.nom FROM series WHERE idserie = ? ";
            final PreparedStatement ps = this.connection.prepareStatement(req2);
            ps.setInt(1, e.getIdserie());
            final ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                e.setNomSerie(rs2.getString("nom"));
            }
            episodeDtos.add(e);
        }
        return episodeDtos;
    }

    @Override
    public List<Episode> recupuerselonSerie(final int id) throws SQLException {
        final List<Episode> episodes = new ArrayList<>();
        final String req = "SELECT * FROM episodes Where idserie=?";
        final PreparedStatement ps = this.connection.prepareStatement(req);
        ps.setInt(1, id);
        final ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            final Episode episode = new Episode();
            episode.setIdepisode(rs.getInt("idepisode"));
            episode.setTitre(rs.getString("titre"));
            episode.setNumeroepisode(rs.getInt("numeroepisode"));
            episode.setSaison(rs.getInt("saison"));
            episode.setImage(rs.getString("image"));
            episode.setVideo(rs.getString("video"));
            episode.setIdserie(rs.getInt("idserie"));
            episodes.add(episode);
        }
        return episodes;
    }
}
