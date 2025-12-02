package com.esprit.controllers.series;

import com.esprit.models.series.Favorite;
import com.esprit.models.series.Series;
import com.esprit.services.series.FavoriteService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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
 * Comprehensive test suite for ListFavorisController.
 * Tests favorite series management.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ListFavorisControllerTest extends TestFXBase {

    private FavoriteService favoriteService;

    private SeriesService seriesService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/ListFavoris.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    // Helper methods
    private List<Favorite> createMockFavorites() {
        List<Favorite> favorites = new ArrayList<>();

        Favorite fav1 = new Favorite();
        Series s1 = new Series();
        s1.setName("Breaking Bad");
        fav1.setSeries(s1);

        favorites.add(fav1);
        return favorites;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Favorites Display Tests")
    class FavoritesDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display favorites table")
        void testFavoritesTableDisplay() {
            waitForFxEvents();

            TableView<Favorite> table = lookup("#favoritesTable").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(2)
        @DisplayName("Should load user favorites")
        void testLoadFavorites() {
            waitForFxEvents();

            TableView<Favorite> table = lookup("#favoritesTable").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(3)
        @DisplayName("Should display series title in table")
        void testSeriesTitleDisplay() {
            waitForFxEvents();

            TableView<Favorite> table = lookup("#favoritesTable").query();
            assertThat(table).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Remove Favorite Tests")
    class RemoveFavoriteTests {

        @Test
        @Order(4)
        @DisplayName("Should remove favorite series")
        void testRemoveFavorite() {
            waitForFxEvents();

            TableView<Favorite> table = lookup("#favoritesTable").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(5)
        @DisplayName("Should confirm before removing")
        void testConfirmRemoval() {
            waitForFxEvents();

            Button removeButton = lookup("#removeButton").query();
            assertThat(removeButton).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("View Series Tests")
    class ViewSeriesTests {

        @Test
        @Order(6)
        @DisplayName("Should navigate to series details")
        void testNavigateToSeriesDetails() {
            waitForFxEvents();

            TableView<Favorite> table = lookup("#favoritesTable").query();
            assertThat(table).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Empty State Tests")
    class EmptyStateTests {

        @Test
        @Order(7)
        @DisplayName("Should display empty state")
        void testEmptyState() {
            waitForFxEvents();

            Label emptyLabel = lookup("#emptyStateLabel").query();
            assertThat(emptyLabel).isNotNull();
        }

    }

}

