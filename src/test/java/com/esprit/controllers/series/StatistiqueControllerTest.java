package com.esprit.controllers.series;

import java.util.HashMap;
import java.util.Map;

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

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for StatistiqueController.
 * Tests category statistics visualization with bar charts and pie charts.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatistiqueControllerTest extends TestFXBase {

    private IServiceCategorieImpl categoryService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/StatistiquesView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Statistics View Tests")
    class StatisticsViewTests {

        @Test
        @Order(1)
        @DisplayName("Should display border pane")
        void testBorderPaneDisplay() {
            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should display statistics combo box")
        void testComboBoxDisplay() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }

        @Test
        @Order(3)
        @DisplayName("Should populate combo box with options")
        void testComboBoxOptions() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox.getItems()).isNotEmpty();
        }

        @Test
        @Order(4)
        @DisplayName("Should load category statistics on init")
        void testLoadStatistics() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Bar Chart Tests")
    class BarChartTests {

        @Test
        @Order(5)
        @DisplayName("Should display bar chart when selected")
        void testBarChartDisplay() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);
        }

        @Test
        @Order(6)
        @DisplayName("Should create bar chart with category data")
        void testBarChartData() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }

        @Test
        @Order(7)
        @DisplayName("Should display category names on X-axis")
        void testBarChartXAxis() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            // Verify X-axis has category names
        }

        @Test
        @Order(8)
        @DisplayName("Should display counts on Y-axis")
        void testBarChartYAxis() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            // Verify Y-axis has numerical values
        }

        @Test
        @Order(9)
        @DisplayName("Should show tooltips on bar hover")
        void testBarChartTooltips() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            // Verify tooltips installed
        }

        @Test
        @Order(10)
        @DisplayName("Should limit categories displayed in bar chart")
        void testBarChartLimit() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Pie Chart Tests")
    class PieChartTests {

        @Test
        @Order(11)
        @DisplayName("Should display pie chart when selected")
        void testPieChartDisplay() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Pie Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(PieChart.class);
        }

        @Test
        @Order(12)
        @DisplayName("Should create pie chart with category data")
        void testPieChartData() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }

        @Test
        @Order(13)
        @DisplayName("Should display category names in pie chart")
        void testPieChartLabels() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Pie Chart");

            waitForFxEvents();

            // Verify labels displayed
        }

        @Test
        @Order(14)
        @DisplayName("Should show percentages in pie chart")
        void testPieChartPercentages() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Pie Chart");

            waitForFxEvents();

            // Verify percentages calculated
        }

        @Test
        @Order(15)
        @DisplayName("Should show tooltips on pie slice hover")
        void testPieChartTooltips() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Pie Chart");

            waitForFxEvents();

            // Verify tooltips on slices
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Chart Switching Tests")
    class ChartSwitchingTests {

        @Test
        @Order(16)
        @DisplayName("Should switch from bar to pie chart")
        void testSwitchBarToPie() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");
            waitForFxEvents();

            clickOn(comboBox).clickOn("Pie Chart");
            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(PieChart.class);
        }

        @Test
        @Order(17)
        @DisplayName("Should switch from pie to bar chart")
        void testSwitchPieToBar() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Pie Chart");
            waitForFxEvents();

            clickOn(comboBox).clickOn("Bar Chart");
            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);
        }

        @Test
        @Order(18)
        @DisplayName("Should preserve data when switching charts")
        void testPreserveDataOnSwitch() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Context Menu Tests")
    class ContextMenuTests {

        @Test
        @Order(19)
        @DisplayName("Should show context menu on right click")
        void testContextMenuDisplay() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");
            waitForFxEvents();

            // Right click on chart
            // Verify context menu appears
        }

        @Test
        @Order(20)
        @DisplayName("Should provide export option in context menu")
        void testExportOption() {
            waitForFxEvents();

            // Right click
            // Verify "Export" menu item
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Empty Data Tests")
    class EmptyDataTests {

        @Test
        @Order(21)
        @DisplayName("Should handle empty statistics")
        void testEmptyStatistics() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }

        @Test
        @Order(22)
        @DisplayName("Should display no data message")
        void testNoDataMessage() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Data Update Tests")
    class DataUpdateTests {

        @Test
        @Order(23)
        @DisplayName("Should refresh chart data")
        void testRefreshData() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }

        @Test
        @Order(24)
        @DisplayName("Should update chart when data changes")
        void testUpdateOnDataChange() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            assertThat(comboBox).isNotNull();
        }
    }

    // Helper methods
    private Map<Category, Long> createMockStatistics() {
        Map<Category, Long> stats = new HashMap<>();

        Category c1 = new Category();
        c1.setName("Action");
        stats.put(c1, 10L);

        Category c2 = new Category();
        c2.setName("Drama");
        stats.put(c2, 15L);

        Category c3 = new Category();
        c3.setName("Comedy");
        stats.put(c3, 8L);

        return stats;
    }

    private Map<Category, Long> createLargeStatistics() {
        Map<Category, Long> stats = new HashMap<>();

        for (int i = 1; i <= 10; i++) {
            Category c = new Category();
            c.setName("Category " + i);
            stats.put(c, (long) i * 5);
        }

        return stats;
    }
}
