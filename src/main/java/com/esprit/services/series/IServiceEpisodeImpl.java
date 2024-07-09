package com.esprit.services.series;
import com.esprit.models.series.Episode;
import com.esprit.services.series.DTO.EpisodeDto;
import com.esprit.utils.mydatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class IServiceEpisodeImpl implements IServiceEpisode<Episode> {
    private final Connection connection;
    public IServiceEpisodeImpl() {
        connection = mydatabase.getInstance().getConnection();
    }
    /** 
     * @param episode
     * @throws SQLException
     */
    @Override
    public void ajouter(Episode episode) throws SQLException {
        String req = "INSERT INTO episodes (titre,numeroepisode,saison,image,video,idserie) VALUES('" + episode.getTitre() + "'," + episode.getNumeroepisode() + ",'" + episode.getSaison() + "','" + episode.getImage() + "','" + episode.getVideo() + "','" + episode.getIdserie() + "')";
        Statement st = connection.createStatement();
        st.executeUpdate(req);
        System.out.println("episode ajoutee avec succes");
    }
    /** 
     * @param episode
     * @throws SQLException
     */
    @Override
    public void modifier(Episode episode) throws SQLException {
        String req = "UPDATE episodes set titre = ?, numeroepisode = ?,saison = ?, image = ?,video = ? ,idserie = ?  where idepisode = ?;";
        PreparedStatement st = connection.prepareStatement(req);
        st.setString(1, episode.getTitre());
        st.setInt(2, episode.getNumeroepisode());
        st.setInt(3, episode.getSaison());
        st.setString(4, episode.getImage());
        st.setString(5, episode.getVideo());
        st.setInt(6, episode.getIdserie());
        st.setInt(7, episode.getIdepisode());
        st.executeUpdate();
        System.out.println("episode modifier avec succes");
    }
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM episodes WHERE idepisode = ?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("episode supprimee avec succes");
    }
    @Override
    public List<EpisodeDto> recuperer() throws SQLException {
        List<EpisodeDto> episodeDtos = new ArrayList<>();
        String req = "SELECT * FROM episodes ";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            EpisodeDto e = new EpisodeDto();
            e.setIdepisode(rs.getInt("idepisode"));
            e.setTitre(rs.getString("titre"));
            e.setNumeroepisode(rs.getInt("numeroepisode"));
            e.setSaison(rs.getInt("saison"));
            e.setImage(rs.getString("image"));
            e.setVideo(rs.getString("video"));
            e.setIdserie(rs.getInt("idserie"));
            String req2 = "SELECT series.nom FROM series WHERE idserie = ? ";
            PreparedStatement ps = connection.prepareStatement(req2);
            ps.setInt(1, e.getIdserie());
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                e.setNomSerie(rs2.getString("nom"));
            }
            episodeDtos.add(e);
        }
        return episodeDtos;
    }
    @Override
    public List<Episode> recupuerselonSerie(int id) throws SQLException {
        List<Episode> episodes = new ArrayList<>();
        String req = "SELECT * FROM episodes Where idserie=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Episode episode = new Episode();
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
