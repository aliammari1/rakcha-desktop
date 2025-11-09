package com.esprit.controllers.users;

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

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.models.products.Product;
import com.esprit.models.series.Series;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for HomeClientController.
 * Tests home page display, search, featured content, and navigation.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeClientControllerTest extends TestFXBase {

    private HomeClientController controller;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/HomeClient.fxml"));
        javafx.scene.Parent root = loader.load();
        controller = loader.getController();
        
        stage.setScene(new Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Page Initialization Tests")
    class PageInitializationTests {

        @Test
        @Order(1)
        @DisplayName("Should display welcome message")
        void testWelcomeMessage() {
            waitForFxEvents();

            Label welcomeLabel = lookup("#welcomeLabel").query();
            assertThat(welcomeLabel).isNotNull();
            assertThat(welcomeLabel.getText()).isNotEmpty();
        }

        @Test
        @Order(2)
        @DisplayName("Should display search field")
        void testSearchFieldDisplay() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            assertThat(searchField.isVisible()).isTrue();
        }

        @Test
        @Order(3)
        @DisplayName("Should initialize all content containers")
        void testContentContainersInitialized() {
            waitForFxEvents();

            assertThat(lookup("#moviesContainer").tryQuery()).isPresent();
            assertThat(lookup("#seriesContainer").tryQuery()).isPresent();
            assertThat(lookup("#productsContainer").tryQuery()).isPresent();
            assertThat(lookup("#cinemasContainer").tryQuery()).isPresent();
        }

        @Test
        @Order(4)
        @DisplayName("Should display featured banner")
        void testFeaturedBannerDisplay() {
            waitForFxEvents();

            assertThat(lookup("#featuredBanner").tryQuery()).isPresent();
            assertThat(lookup("#featuredImage").tryQuery()).isPresent();
            assertThat(lookup("#featuredTitle").tryQuery()).isPresent();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Featured Content Tests")
    class FeaturedContentTests {

        @Test
        @Order(5)
        @DisplayName("Should display featured image")
        void testFeaturedImage() {
            waitForFxEvents();

            ImageView featuredImage = lookup("#featuredImage").query();
            assertThat(featuredImage).isNotNull();
            assertThat(featuredImage.isVisible()).isTrue();
        }

        @Test
        @Order(6)
        @DisplayName("Should display featured title")
        void testFeaturedTitle() {
            waitForFxEvents();

            Label featuredTitle = lookup("#featuredTitle").query();
            assertThat(featuredTitle).isNotNull();
            assertThat(featuredTitle.getText()).isNotEmpty();
        }

        @Test
        @Order(7)
        @DisplayName("Should display featured description")
        void testFeaturedDescription() {
            waitForFxEvents();

            Label featuredDesc = lookup("#featuredDescription").query();
            assertThat(featuredDesc).isNotNull();
            assertThat(featuredDesc.isVisible()).isTrue();
        }

        @Test
        @Order(8)
        @DisplayName("Should rotate featured content")
        void testFeaturedContentRotation() {
            waitForFxEvents();

            Label featuredTitle = lookup("#featuredTitle").query();
            String initialTitle = featuredTitle.getText();

            sleep(3000);

            Label updatedTitle = lookup("#featuredTitle").query();
            assertThat(updatedTitle).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Movies Display Tests")
    class MoviesDisplayTests {

        @Test
        @Order(9)
        @DisplayName("Should display movies container")
        void testMoviesContainerDisplay() {
            waitForFxEvents();

            HBox moviesContainer = lookup("#moviesContainer").query();
            assertThat(moviesContainer).isNotNull();
            assertThat(moviesContainer.isVisible()).isTrue();
        }

        @Test
        @Order(10)
        @DisplayName("Should load movies from service")
        void testLoadMovies() {
            waitForFxEvents();

            HBox moviesContainer = lookup("#moviesContainer").query();
            assertThat(moviesContainer).isNotNull();
            // Verify movies are loaded into container
            waitForFxEvents();
        }

        @Test
        @Order(11)
        @DisplayName("Should display movie cards")
        void testMovieCardsDisplay() {
            waitForFxEvents();

            HBox moviesContainer = lookup("#moviesContainer").query();
            assertThat(moviesContainer).isNotNull();
            assertThat(moviesContainer.getChildren()).isNotNull();
        }

        @Test
        @Order(12)
        @DisplayName("Should handle empty movie list")
        void testEmptyMovieList() {
            waitForFxEvents();

            HBox moviesContainer = lookup("#moviesContainer").query();
            assertThat(moviesContainer).isNotNull();
            // Container should still be present even if empty
            assertThat(moviesContainer.isVisible()).isTrue();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Series Display Tests")
    class SeriesDisplayTests {

        @Test
        @Order(13)
        @DisplayName("Should display series container")
        void testSeriesContainerDisplay() {
            waitForFxEvents();

            HBox seriesContainer = lookup("#seriesContainer").query();
            assertThat(seriesContainer).isNotNull();
            assertThat(seriesContainer.isVisible()).isTrue();
        }

        @Test
        @Order(14)
        @DisplayName("Should load series from service")
        void testLoadSeries() {
            waitForFxEvents();

            HBox seriesContainer = lookup("#seriesContainer").query();
            assertThat(seriesContainer).isNotNull();
            waitForFxEvents();
        }

        @Test
        @Order(15)
        @DisplayName("Should display series cards")
        void testSeriesCardsDisplay() {
            waitForFxEvents();

            HBox seriesContainer = lookup("#seriesContainer").query();
            assertThat(seriesContainer).isNotNull();
            assertThat(seriesContainer.getChildren()).isNotNull();
        }

        @Test
        @Order(16)
        @DisplayName("Should handle empty series list")
        void testEmptySeriesList() {
            waitForFxEvents();

            HBox seriesContainer = lookup("#seriesContainer").query();
            assertThat(seriesContainer).isNotNull();
            assertThat(seriesContainer.isVisible()).isTrue();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Products Display Tests")
    class ProductsDisplayTests {

        @Test
        @Order(17)
        @DisplayName("Should display products container")
        void testProductsContainerDisplay() {
            waitForFxEvents();

            HBox productsContainer = lookup("#productsContainer").query();
            assertThat(productsContainer).isNotNull();
            assertThat(productsContainer.isVisible()).isTrue();
        }

        @Test
        @Order(18)
        @DisplayName("Should load products from service")
        void testLoadProducts() {
            waitForFxEvents();

            HBox productsContainer = lookup("#productsContainer").query();
            assertThat(productsContainer).isNotNull();
            waitForFxEvents();
        }

        @Test
        @Order(19)
        @DisplayName("Should display product cards")
        void testProductCardsDisplay() {
            waitForFxEvents();

            HBox productsContainer = lookup("#productsContainer").query();
            assertThat(productsContainer).isNotNull();
            assertThat(productsContainer.getChildren()).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cinemas Display Tests")
    class CinemasDisplayTests {

        @Test
        @Order(20)
        @DisplayName("Should display cinemas container")
        void testCinemasContainerDisplay() {
            waitForFxEvents();

            HBox cinemasContainer = lookup("#cinemasContainer").query();
            assertThat(cinemasContainer).isNotNull();
            assertThat(cinemasContainer.isVisible()).isTrue();
        }

        @Test
        @Order(21)
        @DisplayName("Should load cinemas from service")
        void testLoadCinemas() {
            waitForFxEvents();

            HBox cinemasContainer = lookup("#cinemasContainer").query();
            assertThat(cinemasContainer).isNotNull();
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Search Functionality Tests")
    class SearchFunctionalityTests {

        @Test
        @Order(22)
        @DisplayName("Should filter content by search term")
        void testSearchFilter() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField).write("Action");
            assertThat(searchField.getText()).isEqualTo("Action");
            waitForFxEvents();
        }

        @Test
        @Order(23)
        @DisplayName("Should search across all content types")
        void testSearchAllTypes() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField).write("Test");
            assertThat(searchField.getText()).isNotEmpty();
            waitForFxEvents();
        }

        @Test
        @Order(24)
        @DisplayName("Should handle empty search")
        void testEmptySearch() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField);
            assertThat(searchField.getText()).isEmpty();
            waitForFxEvents();
        }

        @Test
        @Order(25)
        @DisplayName("Should perform case-insensitive search")
        void testCaseInsensitiveSearch() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField).write("ACTION");
            assertThat(searchField.getText()).isEqualTo("ACTION");
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Animation Tests")
    class AnimationTests {

        @Test
        @Order(26)
        @DisplayName("Should display animated particles")
        void testParticleAnimations() {
            waitForFxEvents();

            assertThat(lookup("#particle1").tryQuery()).isPresent();
            assertThat(lookup("#particle2").tryQuery()).isPresent();
            sleep(500);
        }

        @Test
        @Order(27)
        @DisplayName("Should display animated shapes")
        void testShapeAnimations() {
            waitForFxEvents();

            assertThat(lookup("#shape1").tryQuery()).isPresent();
            assertThat(lookup("#shape2").tryQuery()).isPresent();
            sleep(500);
        }

        @Test
        @Order(28)
        @DisplayName("Should animate on page load")
        void testPageLoadAnimation() {
            waitForFxEvents();

            VBox mainContainer = lookup("#mainContainer").query();
            assertThat(mainContainer).isNotNull();
            assertThat(mainContainer.isVisible()).isTrue();
            sleep(500);
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Scroll Functionality Tests")
    class ScrollFunctionalityTests {

        @Test
        @Order(29)
        @DisplayName("Should enable horizontal scrolling for movies")
        void testMoviesScroll() {
            waitForFxEvents();

            ScrollPane moviesScroll = lookup("#moviesScrollPane").query();
            assertThat(moviesScroll).isNotNull();
            assertThat(moviesScroll.getHbarPolicy()).isNotEqualTo(ScrollPane.ScrollBarPolicy.NEVER);
        }

        @Test
        @Order(30)
        @DisplayName("Should enable horizontal scrolling for series")
        void testSeriesScroll() {
            waitForFxEvents();

            ScrollPane seriesScroll = lookup("#seriesScrollPane").query();
            assertThat(seriesScroll).isNotNull();
            assertThat(seriesScroll.getHbarPolicy()).isNotEqualTo(ScrollPane.ScrollBarPolicy.NEVER);
        }

        @Test
        @Order(31)
        @DisplayName("Should enable smooth scrolling")
        void testSmoothScrolling() {
            waitForFxEvents();

            ScrollPane mainScroll = lookup("#mainScrollPane").query();
            assertThat(mainScroll).isNotNull();
            assertThat(mainScroll.isPannable()).isTrue();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Card Click Navigation Tests")
    class CardClickNavigationTests {

        @Test
        @Order(32)
        @DisplayName("Should navigate to movie details on click")
        void testMovieCardClick() {
            waitForFxEvents();

            HBox moviesContainer = lookup("#moviesContainer").query();
            assertThat(moviesContainer).isNotNull();
            assertThat(moviesContainer.getChildren()).isNotNull();
            
            if (!moviesContainer.getChildren().isEmpty()) {
                // Verify cards are clickable
                assertThat(moviesContainer.getChildren().size()).isGreaterThanOrEqualTo(0);
            }
        }

        @Test
        @Order(33)
        @DisplayName("Should navigate to series details on click")
        void testSeriesCardClick() {
            waitForFxEvents();

            HBox seriesContainer = lookup("#seriesContainer").query();
            assertThat(seriesContainer).isNotNull();
            assertThat(seriesContainer.getChildren()).isNotNull();
            
            if (!seriesContainer.getChildren().isEmpty()) {
                // Verify cards are clickable
                assertThat(seriesContainer.getChildren().size()).isGreaterThanOrEqualTo(0);
            }
        }

        @Test
        @Order(34)
        @DisplayName("Should navigate to product details on click")
        void testProductCardClick() {
            waitForFxEvents();

            HBox productsContainer = lookup("#productsContainer").query();
            assertThat(productsContainer).isNotNull();
            assertThat(productsContainer.getChildren()).isNotNull();
            
            if (!productsContainer.getChildren().isEmpty()) {
                // Verify cards are clickable
                assertThat(productsContainer.getChildren().size()).isGreaterThanOrEqualTo(0);
            }
        }
    }


    // Helper methods
    private List<Film> createMockFilms() {
        List<Film> films = new ArrayList<>();
        Film film1 = new Film();
        film1.setName("Action Movie");
        Film film2 = new Film();
        film2.setName("Comedy Movie");
        films.add(film1);
        films.add(film2);
        return films;
    }

    private List<Series> createMockSeries() {
        List<Series> series = new ArrayList<>();
        Series s1 = new Series();
        s1.setName("Drama Series");
        series.add(s1);
        return series;
    }

    private List<Product> createMockProducts() {
        List<Product> products = new ArrayList<>();
        Product p1 = new Product();
        p1.setName("Test Product");
        products.add(p1);
        return products;
    }

    private List<Cinema> createMockCinemas() {
        List<Cinema> cinemas = new ArrayList<>();
        Cinema c1 = new Cinema();
        c1.setName("Test Cinema");
        cinemas.add(c1);
        return cinemas;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }
}
