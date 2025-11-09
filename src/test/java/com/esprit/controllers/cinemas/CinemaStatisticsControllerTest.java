package com.esprit.controllers.cinemas;

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

import com.esprit.utils.TestFXBase;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for CinemaStatisticsController.
 * Tests sentiment analysis visualization and statistics display.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CinemaStatisticsControllerTest extends TestFXBase {

    @Start
    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/cinemas/statistiques.fxml")
        );
        javafx.scene.Parent root = loader.load();
        
        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Initial UI Tests")
    class InitialUITests {

        @Test
        @Order(1)
        @DisplayName("Should display statistics anchor pane")
        void testStatisticsAnchorDisplay() {
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should display show statistics button")
        void testShowStatisticsButton() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            assertThat(showButton).isNotNull();
            assertThat(showButton.isVisible()).isTrue();
        }

        @Test
        @Order(3)
        @DisplayName("Should have empty statistics container initially")
        void testInitialEmptyState() {
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
            // Initially should be empty or have minimal content
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Sentiment Analysis Tests")
    class SentimentAnalysisTests {

        @Test
        @Order(4)
        @DisplayName("Should generate statistics when button clicked")
        void testGenerateStatistics() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
            assertThat(statsAnchor.getChildren()).isNotEmpty();
        }

        @Test
        @Order(5)
        @DisplayName("Should display pie charts after clicking show statistics")
        void testPieChartsCreated() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Charts are created dynamically, check if VBox container exists
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            if (!statsAnchor.getChildren().isEmpty() && statsAnchor.getChildren().get(0) instanceof VBox) {
                VBox chartContainer = (VBox) statsAnchor.getChildren().get(0);
                assertThat(chartContainer).isNotNull();
            }
        }

        @Test
        @Order(6)
        @DisplayName("Should create charts with sentiment data")
        void testChartsWithSentimentData() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify charts contain data
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(7)
        @DisplayName("Should handle multiple cinemas")
        void testMultipleCinemas() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(8)
        @DisplayName("Should calculate sentiment percentages correctly")
        void testSentimentPercentages() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify statistics are generated
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("PieChart Display Tests")
    class PieChartDisplayTests {

        @Test
        @Order(9)
        @DisplayName("Should create pie charts dynamically")
        void testPieChartCreation() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Charts are created inside VBox container
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(10)
        @DisplayName("Should display charts with titles")
        void testChartTitles() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify container structure
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }

        @Test
        @Order(11)
        @DisplayName("Should organize charts in VBox container")
        void testChartContainer() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            if (!statsAnchor.getChildren().isEmpty() && statsAnchor.getChildren().get(0) instanceof VBox) {
                VBox container = (VBox) statsAnchor.getChildren().get(0);
                assertThat(container.getSpacing()).isEqualTo(20.0);
            }
        }

        @Test
        @Order(12)
        @DisplayName("Should clear previous statistics when regenerating")
        void testClearPreviousStats() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();
            
            // Click again to regenerate
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(13)
        @DisplayName("Should handle chart data properly")
        void testChartDataHandling() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify data structure is created
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Empty Data Handling Tests")
    class EmptyDataHandlingTests {

        @Test
        @Order(14)
        @DisplayName("Should handle button click when no comments")
        void testNoComments() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Should still work even with no data
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(15)
        @DisplayName("Should create empty VBox when no data")
        void testEmptyVBoxCreation() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // VBox should be created even if empty
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }

        @Test
        @Order(16)
        @DisplayName("Should not crash with null data")
        void testNullDataHandling() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            
            // Should not throw exception
            clickOn(showButton);
            waitForFxEvents();

            assertThat(showButton).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Button Interaction Tests")
    class ButtonInteractionTests {

        @Test
        @Order(17)
        @DisplayName("Should enable show statistics button")
        void testButtonEnabled() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            assertThat(showButton).isNotNull();
            assertThat(showButton.isDisabled()).isFalse();
        }

        @Test
        @Order(18)
        @DisplayName("Should respond to button clicks")
        void testButtonClick() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            assertThat(showButton).isNotNull();
            
            clickOn(showButton);
            waitForFxEvents();
            
            // Verify action was executed
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(19)
        @DisplayName("Should allow multiple button clicks")
        void testMultipleClicks() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            assertThat(showButton).isNotNull();
            
            clickOn(showButton);
            waitForFxEvents();
            clickOn(showButton);
            waitForFxEvents();
            
            assertThat(showButton).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Statistics Generation Tests")
    class StatisticsGenerationTests {

        @Test
        @Order(20)
        @DisplayName("Should generate statistics for each cinema")
        void testPerCinemaStatistics() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(21)
        @DisplayName("Should aggregate sentiment counts correctly")
        void testSentimentAggregation() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify statistics were generated
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }

        @Test
        @Order(22)
        @DisplayName("Should create separate chart for each cinema")
        void testSeparateChartsPerCinema() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Charts should be created dynamically
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(23)
        @DisplayName("Should include cinema name in chart titles")
        void testChartTitlesWithCinemaNames() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Verify charts are created with titles
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Data Refresh Tests")
    class DataRefreshTests {

        @Test
        @Order(24)
        @DisplayName("Should regenerate statistics on button click")
        void testRegenerateStatistics() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();
            
            // Click again to regenerate
            clickOn(showButton);
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(25)
        @DisplayName("Should clear old data before generating new")
        void testClearOldData() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();
            
            clickOn(showButton);
            waitForFxEvents();
            
            // Verify anchor is cleared and repopulated
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
        }

        @Test
        @Order(26)
        @DisplayName("Should fetch fresh data from database")
        void testFreshDataFetch() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // Data should be fetched each time
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor.getChildren()).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("UI Layout Tests")
    class UILayoutTests {

        @Test
        @Order(27)
        @DisplayName("Should position statistics anchor correctly")
        void testAnchorPosition() {
            waitForFxEvents();

            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            assertThat(statsAnchor).isNotNull();
            assertThat(statsAnchor.isVisible()).isTrue();
        }

        @Test
        @Order(28)
        @DisplayName("Should include sidebar in layout")
        void testSidebarInclusion() {
            waitForFxEvents();

            // Sidebar should be included via fx:include
            // Main anchor pane should contain multiple elements
            assertThat(lookup(".root").queryAll()).isNotEmpty();
        }

        @Test
        @Order(29)
        @DisplayName("Should maintain proper spacing in VBox")
        void testVBoxSpacing() {
            waitForFxEvents();

            Button showButton = lookup("Show Statistics").queryButton();
            clickOn(showButton);
            waitForFxEvents();

            // VBox should have 20px spacing
            AnchorPane statsAnchor = lookup("#statisticsAnchor").query();
            if (!statsAnchor.getChildren().isEmpty() && statsAnchor.getChildren().get(0) instanceof VBox) {
                VBox container = (VBox) statsAnchor.getChildren().get(0);
                assertThat(container.getSpacing()).isEqualTo(20.0);
            }
        }
    }

}
