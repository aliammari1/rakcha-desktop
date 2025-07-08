package com.esprit.services.series;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.esprit.models.series.Episode;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Service class providing business logic for the RAKCHA application. Implements
 * CRUD operations and business rules for data management.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class IServiceEpisodeImpl implements IService<Episode> {
    private static final Logger LOGGER = Logger.getLogger(IServiceEpisodeImpl.class.getName());
    private final Connection connection;

    /**
     * Constructs a new IServiceEpisodeImpl instance.
     * Initializes database connection.
     */
    public IServiceEpisodeImpl() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    /**
     * Creates a new entity in the database.
     *
     * @param entity
     *               the entity to create
     */
    public void create(final Episode episode) {
        final String req = "INSERT INTO episode (title, episode_number, season, image, video, series_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setString(1, episode.getTitle());
            st.setInt(2, episode.getEpisodeNumber());
            st.setInt(3, episode.getSeason());
            st.setString(4, episode.getImage());
            st.setString(5, episode.getVideo());
            st.setInt(6, episode.getSeriesId());
            st.executeUpdate();
            LOGGER.info("Episode created successfully");
        } catch (SQLException e) {
            log.error("Error creating episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create episode", e);
        }
    }

    @Override
    /**
     * Updates an existing entity in the database.
     *
     * @param entity
     *               the entity to update
     */
    public void update(final Episode episode) {
        final String req = "UPDATE episode SET title = ?, episode_number = ?, season = ?, image = ?, video = ?, series_id = ? WHERE idepisode = ?";
        try (final PreparedStatement st = this.connection.prepareStatement(req)) {
            st.setString(1, episode.getTitle());
            st.setInt(2, episode.getEpisodeNumber());
            st.setInt(3, episode.getSeason());
            st.setString(4, episode.getImage());
            st.setString(5, episode.getVideo());
            st.setInt(6, episode.getSeriesId());
            st.setLong(7, episode.getId());
            st.executeUpdate();
            LOGGER.info("Episode updated successfully");
        } catch (SQLException e) {
            log.error("Error updating episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update episode", e);
        }
    }

    @Override
    /**
     * Deletes an entity from the database.
     *
     * @param id
     *           the ID of the entity to delete
     */
    public void delete(final Episode episode) {
        if (episode == null) {
            log.error("Cannot delete null episode");
            throw new IllegalArgumentException("Episode cannot be null");
        }
        if (episode.getId() == null) {
            log.error("Cannot delete episode with null ID");
            throw new IllegalArgumentException("Episode ID cannot be null");
        }
        deleteById(episode.getId());
    }

    /**
     * Deletes an episode by its ID.
     *
     * @param id the ID of the episode to delete
     */
    public void deleteById(final Long id) {
        final String req = "DELETE FROM episode WHERE idepisode = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            LOGGER.info("Episode deleted successfully");
        } catch (SQLException e) {
            log.error("Error deleting episode: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete episode", e);
        }
    }

    @Override
    /**
     * Reads all episodes from the database.
     *
     * @return list of all episodes
     */
    public List<Episode> read() {
        final List<Episode> episodeDtos = new ArrayList<>();
        final String req = "SELECT * FROM episode";
        try (final Statement st = this.connection.createStatement(); final ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                final Episode e = Episode.builder().id(rs.getLong("idepisode")).title(rs.getString("title"))
                        .episodeNumber(rs.getInt("episode_number")).season(rs.getInt("season"))
                        .image(rs.getString("image")).video(rs.getString("video")).seriesId(rs.getInt("series_id"))
                        .build();
                episodeDtos.add(e);
            }
        } catch (SQLException e) {
            log.error("Error reading episodes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read episodes", e);
        }
        return episodeDtos;
    }

    /**
     * Retrieves episodes by series ID.
     *
     * @param seriesId the ID of the series to retrieve episodes for
     * @return list of episodes for the specified series
     */
    public List<Episode> retrieveBySeries(final Long seriesId) {
        final List<Episode> episodes = new ArrayList<>();
        final String req = "SELECT * FROM episode WHERE series_id = ?";
        try (final PreparedStatement ps = this.connection.prepareStatement(req)) {
            ps.setLong(1, seriesId);
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Episode episode = Episode.builder().id(rs.getLong("idepisode")).title(rs.getString("title"))
                            .episodeNumber(rs.getInt("episode_number")).season(rs.getInt("season"))
                            .image(rs.getString("image")).video(rs.getString("video")).seriesId(rs.getInt("series_id"))
                            .build();
                    episodes.add(episode);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving episodes by series {}: {}", seriesId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve episodes by series", e);
        }
        return episodes;
    }
}
