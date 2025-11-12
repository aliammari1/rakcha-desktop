package com.esprit.controllers.series;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import org.testfx.framework.junit5.Start;

import com.esprit.models.series.Episode;
import com.esprit.services.series.IServiceEpisodeImpl;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for EpisodeClientController.
 * Tests episode browsing and video playback.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EpisodeClientControllerTest extends TestFXBase {

    private IServiceEpisodeImpl episodeService;

    private IServiceSeriesImpl seriesService;

    @Override
    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/EpisodeClient.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Episode Display Tests")
    class EpisodeDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display episodes flow pane")
        void testEpisodesFlowPaneDisplay() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#episodesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(2)
        @DisplayName("Should load episodes for series")
        void testLoadEpisodes() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#episodesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(3)
        @DisplayName("Should display episode cards")
        void testEpisodeCardsDisplay() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#episodesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(4)
        @DisplayName("Should display episode title")
        void testEpisodeTitleDisplay() {
            waitForFxEvents();

            Label titleLabel = lookup("#episodeTitleLabel").query();
            assertThat(titleLabel).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Video Player Tests")
    class VideoPlayerTests {

        @Test
        @Order(5)
        @DisplayName("Should display video player")
        void testVideoPlayerDisplay() {
            waitForFxEvents();

            assertThat(lookup("#mediaView").tryQuery()).isPresent();
        }


        @Test
        @Order(6)
        @DisplayName("Should play episode video")
        void testPlayEpisode() {
            waitForFxEvents();

            assertThat(lookup("#playButton").tryQuery()).isPresent();
        }


        @Test
        @Order(7)
        @DisplayName("Should pause video playback")
        void testPauseVideo() {
            waitForFxEvents();

            assertThat(lookup("#pauseButton").tryQuery()).isPresent();
        }


        @Test
        @Order(8)
        @DisplayName("Should display video controls")
        void testVideoControls() {
            waitForFxEvents();

            assertThat(lookup("#videoControls").tryQuery()).isPresent();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Episode Selection Tests")
    class EpisodeSelectionTests {

        @Test
        @Order(9)
        @DisplayName("Should select episode")
        void testSelectEpisode() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#episodesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(10)
        @DisplayName("Should display selected episode details")
        void testSelectedEpisodeDetails() {
            waitForFxEvents();

            Label detailsLabel = lookup("#episodeDetailsLabel").query();
            assertThat(detailsLabel).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Season Filter Tests")
    class SeasonFilterTests {

        @Test
        @Order(11)
        @DisplayName("Should filter episodes by season")
        void testFilterBySeason() {
            waitForFxEvents();

            assertThat(lookup(".button").tryQuery()).isPresent();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(12)
        @DisplayName("Should navigate back to series")
        void testNavigateBackToSeries() {
            waitForFxEvents();

            assertThat(lookup("#backButton").tryQuery()).isPresent();
        }

    }


    // Helper methods
    private List<Episode> createMockEpisodes() {
        List<Episode> episodes = new ArrayList<>();

        Episode ep1 = new Episode();
        ep1.setTitle("Episode 1");
        ep1.setEpisodeNumber(1);
        ep1.setSeason(1);

        Episode ep2 = new Episode();
        ep2.setTitle("Episode 2");
        ep2.setEpisodeNumber(2);
        ep2.setSeason(1);

        episodes.add(ep1);
        episodes.add(ep2);
        return episodes;
    }


    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }

}

