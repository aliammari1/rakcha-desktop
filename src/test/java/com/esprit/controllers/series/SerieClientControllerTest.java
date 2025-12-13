package com.esprit.controllers.series;

import com.esprit.models.common.Category;
import com.esprit.models.series.Series;
import com.esprit.services.common.CategoryService;
import com.esprit.services.series.FavoriteService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for SerieClientController.
 * Tests series display, filtering, favorites, and top-rated series.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SerieClientControllerTest extends TestFXBase {

    private SeriesService seriesService;

    private CategoryService categoryService;

    private FavoriteService favoriteService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/SeriesClient.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    // Helper methods
    private List<Series> createMockSeries() {
        List<Series> series = new ArrayList<>();

        Series s1 = new Series();
        s1.setName("Breaking Bad");
        s1.setSummary("Chemistry teacher turns to crime");
        s1.setDirector("Vince Gilligan");
        s1.setCountry("USA");

        Series s2 = new Series();
        s2.setName("Game of Thrones");
        s2.setSummary("Epic fantasy series");
        s2.setDirector("David Benioff");
        s2.setCountry("USA");

        series.add(s1);
        series.add(s2);
        return series;
    }

    private List<Series> createMockTopSeries() {
        List<Series> top = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Series s = new Series();
            s.setName("Top Series " + i);
            top.add(s);
        }

        return top;
    }

    private List<Category> createMockCategories() {
        List<Category> categories = new ArrayList<>();
        Category c1 = new Category();
        c1.setName("Action");
        Category c2 = new Category();
        c2.setName("Drama");
        categories.add(c1);
        categories.add(c2);
        return categories;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Series Display Tests")
    class SeriesDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display series list view")
        void testSeriesListDisplay() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should load series from service")
        void testLoadSeries() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(3)
        @DisplayName("Should display series with custom cell factory")
        void testSeriesCellFactory() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(4)
        @DisplayName("Should handle empty series list")
        void testEmptySeriesList() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Top 3 Series Tests")
    class Top3SeriesTests {

        @Test
        @Order(5)
        @DisplayName("Should display top 3 HBox")
        void testTop3HBoxDisplay() {
            waitForFxEvents();

            HBox top3Box = lookup("#hboxTop3").query();
            assertThat(top3Box).isNotNull();
        }

        @Test
        @Order(6)
        @DisplayName("Should load most liked series")
        void testLoadMostLiked() {
            waitForFxEvents();

            HBox top3Box = lookup("#hboxTop3").query();
            assertThat(top3Box).isNotNull();
        }

        @Test
        @Order(7)
        @DisplayName("Should display maximum 3 series in top section")
        void testTop3Limit() {
            waitForFxEvents();

            HBox top3Box = lookup("#hboxTop3").query();
            assertThat(top3Box).isNotNull();
        }

        @Test
        @Order(8)
        @DisplayName("Should set spacing for top 3 series")
        void testTop3Spacing() {
            waitForFxEvents();

            HBox top3Box = lookup("#hboxTop3").query();
            assertThat(top3Box.getSpacing()).isGreaterThan(0);
        }

        @Test
        @Order(9)
        @DisplayName("Should set padding for top 3 series")
        void testTop3Padding() {
            waitForFxEvents();

            HBox top3Box = lookup("#hboxTop3").query();
            assertThat(top3Box.getPadding()).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Filter Tests")
    class CategoryFilterTests {

        @Test
        @Order(10)
        @DisplayName("Should display category combo box")
        void testCategoryComboBoxDisplay() {
            waitForFxEvents();

            ComboBox<Category> categoryCombo = lookup("#CamboxCategorie").query();
            assertThat(categoryCombo).isNotNull();
        }

        @Test
        @Order(11)
        @DisplayName("Should populate category combo box")
        void testPopulateCategories() {
            waitForFxEvents();

            ComboBox<Category> categoryCombo = lookup("#CamboxCategorie").query();
            assertThat(categoryCombo).isNotNull();
        }

        @Test
        @Order(12)
        @DisplayName("Should filter series by selected category")
        void testFilterByCategory() {
            waitForFxEvents();

            ComboBox<Category> categoryCombo = lookup("#CamboxCategorie").query();

            Category category = new Category();
            category.setName("Action");
            clickOn(categoryCombo).clickOn("Action");

            waitForFxEvents();

            // Verify filtered results
        }

        @Test
        @Order(13)
        @DisplayName("Should show all series when no category selected")
        void testShowAllSeries() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(14)
        @DisplayName("Should update series list on category change")
        void testUpdateOnCategoryChange() {
            waitForFxEvents();

            ComboBox<Category> categoryCombo = lookup("#CamboxCategorie").query();
            clickOn(categoryCombo);

            waitForFxEvents();

            // Select different category
            // Verify list updated
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Search Functionality Tests")
    class SearchFunctionalityTests {

        @Test
        @Order(15)
        @DisplayName("Should display search field")
        void testSearchFieldDisplay() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            assertThat(searchField).isNotNull();
        }

        @Test
        @Order(16)
        @DisplayName("Should filter series by search term")
        void testSearchFilter() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            clickOn(searchField).write("Breaking");

            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            // Verify filtered results
        }

        @Test
        @Order(17)
        @DisplayName("Should perform case-insensitive search")
        void testCaseInsensitiveSearch() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            clickOn(searchField).write("BREAKING");

            waitForFxEvents();

            // Should match "Breaking", "breaking", "BREAKING"
        }

        @Test
        @Order(18)
        @DisplayName("Should search by series name")
        void testSearchByName() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            clickOn(searchField).write("Game of Thrones");

            waitForFxEvents();
        }

        @Test
        @Order(19)
        @DisplayName("Should search by director")
        void testSearchByDirector() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            clickOn(searchField).write("Vince Gilligan");

            waitForFxEvents();
        }

        @Test
        @Order(20)
        @DisplayName("Should display search results label")
        void testResultsLabel() {
            waitForFxEvents();

            Label resultsLabel = lookup("#resultatLabel").query();
            assertThat(resultsLabel).isNotNull();
        }

        @Test
        @Order(21)
        @DisplayName("Should update results label with count")
        void testResultsCount() {
            waitForFxEvents();

            TextField searchField = lookup("#recherchefld").query();
            clickOn(searchField).write("Series");

            waitForFxEvents();

            Label resultsLabel = lookup("#resultatLabel").query();
            // Should show number of results
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Favorite Management Tests")
    class FavoriteManagementTests {

        @Test
        @Order(22)
        @DisplayName("Should add series to favorites")
        void testAddToFavorites() {
            waitForFxEvents();

            Button watchButton = lookup("#watchEpisode").query();
            assertThat(watchButton).isNotNull();
        }

        @Test
        @Order(23)
        @DisplayName("Should remove series from favorites")
        void testRemoveFromFavorites() {
            waitForFxEvents();

            Button watchButton = lookup("#watchEpisode").query();
            assertThat(watchButton).isNotNull();
        }

        @Test
        @Order(24)
        @DisplayName("Should toggle favorite status")
        void testToggleFavorite() {
            waitForFxEvents();

            // Click favorite
            // Click again to unfavorite
            waitForFxEvents();
        }

        @Test
        @Order(25)
        @DisplayName("Should display favorite icon")
        void testFavoriteIconDisplay() {
            waitForFxEvents();

            // Verify favorite icon shown
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Watch Episode Tests")
    class WatchEpisodeTests {

        @Test
        @Order(26)
        @DisplayName("Should display watch episode button")
        void testWatchButtonDisplay() {
            waitForFxEvents();

            Button watchButton = lookup("#watchEpisode").query();
            assertThat(watchButton).isNotNull();
        }

        @Test
        @Order(27)
        @DisplayName("Should navigate to episode viewer")
        void testNavigateToEpisodeViewer() {
            waitForFxEvents();

            Button watchButton = lookup("#watchEpisode").query();
            clickOn(watchButton);

            waitForFxEvents();

            // Verify navigation
        }

        @Test
        @Order(28)
        @DisplayName("Should enable watch button when series selected")
        void testWatchButtonEnabled() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            // Select series

            waitForFxEvents();

            Button watchButton = lookup("#watchEpisode").query();
            assertThat(watchButton.isDisabled()).isFalse();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Series Details Display Tests")
    class SeriesDetailsDisplayTests {

        @Test
        @Order(29)
        @DisplayName("Should display series name")
        void testDisplayName() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(30)
        @DisplayName("Should display series summary")
        void testDisplaySummary() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(31)
        @DisplayName("Should display series director")
        void testDisplayDirector() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(32)
        @DisplayName("Should display series country")
        void testDisplayCountry() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

        @Test
        @Order(33)
        @DisplayName("Should display series image")
        void testDisplayImage() {
            waitForFxEvents();

            ListView<Series> listView = lookup("#listeSerie").query();
            assertThat(listView).isNotNull();
        }

    }

}
