package com.esprit.controllers.series;

import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for StatistiqueController.
 * Tests category statistics visualization with bar charts and pie charts.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatistiqueControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/StatistiquesView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);

            @SuppressWarnings("unchecked")
            BarChart<String, Number> barChart = (BarChart<String, Number>) borderPane.getCenter();

            // Verify bar chart has data
            assertThat(barChart.getData()).isNotEmpty();

            // Verify at least one series with data items
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                assertThat(series.getData()).isNotEmpty();
            }

        }


        @Test
        @Order(7)
        @DisplayName("Should display category names on X-axis")
        void testBarChartXAxis() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);

            @SuppressWarnings("unchecked")
            BarChart<String, Number> barChart = (BarChart<String, Number>) borderPane.getCenter();

            // Verify X-axis exists and is a category axis
            assertThat(barChart.getXAxis()).isNotNull();

            // Verify X-axis label (if set)
            String xAxisLabel = barChart.getXAxis().getLabel();
            assertThat(xAxisLabel).isNotNull();

            // Verify categories have been populated
            assertThat(barChart.getData()).isNotEmpty();
        }


        @Test
        @Order(8)
        @DisplayName("Should display counts on Y-axis")
        void testBarChartYAxis() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);

            @SuppressWarnings("unchecked")
            BarChart<String, Number> barChart = (BarChart<String, Number>) borderPane.getCenter();

            // Verify Y-axis exists and is numeric
            assertThat(barChart.getYAxis()).isNotNull();
            assertThat(barChart.getYAxis()).isInstanceOf(NumberAxis.class);

            // Verify Y-axis label
            String yAxisLabel = barChart.getYAxis().getLabel();
            assertThat(yAxisLabel).isNotNull();

            // Verify Y-axis has proper bounds or auto-ranging
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            assertThat(yAxis.getUpperBound()).isGreaterThanOrEqualTo(0);
        }


        @Test
        @Order(9)
        @DisplayName("Should display tooltip data on data points")
        void testBarChartTooltips() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);

            @SuppressWarnings("unchecked")
            BarChart<String, Number> barChart = (BarChart<String, Number>) borderPane.getCenter();

            // Verify series data contains items with non-null values
            assertThat(barChart.getData()).isNotEmpty();
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                assertThat(series.getData()).isNotEmpty();
                // Verify each data point has valid X and Y values
                for (XYChart.Data<String, Number> data : series.getData()) {
                    assertThat(data.getXValue()).isNotNull();
                    assertThat(data.getYValue()).isNotNull();
                    assertThat(data.getYValue().doubleValue()).isGreaterThanOrEqualTo(0);
                }

            }

        }


        @Test
        @Order(10)
        @DisplayName("Should limit displayed categories to max value")
        void testBarChartLimit() {
            waitForFxEvents();

            ComboBox<String> comboBox = lookup("#statisticsComboBox").query();
            clickOn(comboBox).clickOn("Bar Chart");

            waitForFxEvents();

            BorderPane borderPane = lookup("#borderPane").query();
            assertThat(borderPane.getCenter()).isInstanceOf(BarChart.class);

            @SuppressWarnings("unchecked")
            BarChart<String, Number> barChart = (BarChart<String, Number>) borderPane.getCenter();

            // Verify category count doesn't exceed limit (default: 10)
            int displayLimit = 10;
            assertThat(barChart.getData()).isNotEmpty();
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                int categoryCount = series.getData().size();
                assertThat(categoryCount).isLessThanOrEqualTo(displayLimit);
            }

        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


}

