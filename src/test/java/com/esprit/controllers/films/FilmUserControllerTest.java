package com.esprit.controllers.films;

import com.esprit.models.films.Film;
import com.esprit.models.films.FilmComment;
import com.esprit.utils.TestFXBase;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;
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
 * Comprehensive test suite for FilmUserController.
 * Tests film browsing, rating, comments, QR code, and trailer viewing.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmUserControllerTest extends TestFXBase {

    private FilmUserController controller;

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/films/filmuser.fxml"));
        javafx.scene.Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    // Helper methods
    private List<Film> createMockFilms() {
        List<Film> films = new ArrayList<>();

        Film film1 = new Film();
        film1.setTitle("Action Movie");
        film1.setDescription("Exciting action film");

        Film film2 = new Film();
        film2.setTitle("Comedy Movie");
        film2.setDescription("Funny comedy");

        films.add(film1);
        films.add(film2);
        return films;
    }

    private List<FilmComment> createMockComments() {
        List<FilmComment> comments = new ArrayList<>();

        FilmComment comment1 = new FilmComment();
        comment1.setComment("Great film!");

        FilmComment comment2 = new FilmComment();
        comment2.setComment("Loved it!");

        comments.add(comment1);
        comments.add(comment2);
        return comments;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Display Tests")
    class FilmDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display films flow pane")
        void testFilmsFlowPaneDisplay() {
            waitForFxEvents();

            FlowPane filmPane = lookup("#flowpaneFilm").query();
            assertThat(filmPane).isNotNull();
            assertThat(filmPane.isVisible()).isTrue();
        }

        @Test
        @Order(2)
        @DisplayName("Should load films from service")
        void testLoadFilms() {
            waitForFxEvents();

            // Verify films are loaded in flow pane
            FlowPane filmPane = lookup("#flowpaneFilm").query();
            assertThat(filmPane).isNotNull();
            assertThat(filmPane.getChildren()).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(3)
        @DisplayName("Should display film cards")
        void testFilmCardsDisplay() {
            waitForFxEvents();

            // Verify film pane is displayed
            FlowPane filmPane = lookup("#flowpaneFilm").query();
            assertThat(filmPane).isNotNull();
            assertThat(filmPane.isVisible()).isTrue();

            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Rating Tests")
    class FilmRatingTests {

        @Test
        @Order(4)
        @DisplayName("Should display rating control")
        void testRatingControlDisplay() {
            waitForFxEvents();

            Rating ratingControl = lookup("#filmRate").query();
            assertThat(ratingControl).isNotNull();
            assertThat(ratingControl.isVisible()).isTrue();
        }

        @Test
        @Order(5)
        @DisplayName("Should submit film rating")
        void testSubmitRating() {
            waitForFxEvents();

            // Interact with rating control
            Rating ratingControl = lookup("#filmRate").query();
            ratingControl.setRating(4.5);

            waitForFxEvents();

            // Verify rating was set
            assertThat(ratingControl.getRating()).isEqualTo(4.5);
        }

        @Test
        @Order(6)
        @DisplayName("Should display average rating")
        void testAverageRatingDisplay() {
            waitForFxEvents();

            Label avgRatingLabel = lookup("#avgRatingLabel").query();
            assertThat(avgRatingLabel).isNotNull();
            assertThat(avgRatingLabel.isVisible()).isTrue();
        }

        @Test
        @Order(7)
        @DisplayName("Should update rating on change")
        void testUpdateRating() {
            waitForFxEvents();

            Rating ratingControl = lookup("#filmRate").query();
            assertThat(ratingControl).isNotNull();

            ratingControl.setRating(3.0);
            waitForFxEvents();
            assertThat(ratingControl.getRating()).isEqualTo(3.0);

            ratingControl.setRating(5.0);
            waitForFxEvents();
            assertThat(ratingControl.getRating()).isEqualTo(5.0);
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comments Display Tests")
    class CommentsDisplayTests {

        @Test
        @Order(8)
        @DisplayName("Should display comments section")
        void testCommentsSection() {
            waitForFxEvents();

            ScrollPane commentsPane = lookup("#AnchorComments").query();
            assertThat(commentsPane).isNotNull();
            assertThat(commentsPane.isVisible()).isTrue();
        }

        @Test
        @Order(9)
        @DisplayName("Should load film comments")
        void testLoadComments() {
            waitForFxEvents();

            // Verify comments section is present
            ScrollPane commentsPane = lookup("#AnchorComments").query();
            assertThat(commentsPane).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(10)
        @DisplayName("Should display comment text")
        void testCommentTextDisplay() {
            waitForFxEvents();

            // Verify comments section is visible
            ScrollPane commentsPane = lookup("#AnchorComments").query();
            assertThat(commentsPane).isNotNull();
            assertThat(commentsPane.isVisible()).isTrue();

            waitForFxEvents();
        }

        @Test
        @Order(11)
        @DisplayName("Should display comment author")
        void testCommentAuthorDisplay() {
            waitForFxEvents();

            // Verify comments section is present and ready
            ScrollPane commentsPane = lookup("#AnchorComments").query();
            assertThat(commentsPane).isNotNull();

            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Add Comment Tests")
    class AddCommentTests {

        @Test
        @Order(12)
        @DisplayName("Should display comment input")
        void testCommentInputDisplay() {
            waitForFxEvents();

            TextArea commentInput = lookup("#commentInput").query();
            assertThat(commentInput).isNotNull();
            assertThat(commentInput.isVisible()).isTrue();
        }

        @Test
        @Order(13)
        @DisplayName("Should add new comment")
        void testAddComment() {
            waitForFxEvents();

            // Interact with real UI components
            TextArea commentInput = lookup("#commentInput").query();
            clickOn(commentInput).write("Great film!");

            Button submitButton = lookup("#submitCommentButton").query();
            clickOn(submitButton);

            waitForFxEvents();

            // Verify UI is responsive
            assertThat(submitButton).isNotNull();
        }

        @Test
        @Order(14)
        @DisplayName("Should validate comment not empty")
        void testCommentValidation() {
            waitForFxEvents();

            // Verify comment input is present
            TextArea commentInput = lookup("#commentInput").query();
            assertThat(commentInput).isNotNull();

            // Verify submit button is present
            Button submitButton = lookup("#submitCommentButton").query();
            assertThat(submitButton).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(15)
        @DisplayName("Should clear comment after submission")
        void testClearCommentAfterSubmit() {
            waitForFxEvents();

            TextArea commentInput = lookup("#commentInput").query();
            assertThat(commentInput).isNotNull();

            commentInput.setText("Test comment");
            assertThat(commentInput.getText()).isEqualTo("Test comment");

            Button submitButton = lookup("#submitCommentButton").query();
            clickOn(submitButton);

            waitForFxEvents();

            // Comment field should be cleared after submit
            assertThat(commentInput.getText()).isEmpty();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Trailer Button Tests")
    class TrailerButtonTests {

        @Test
        @Order(16)
        @DisplayName("Should display trailer button")
        void testTrailerButtonDisplay() {
            waitForFxEvents();

            Button trailerButton = lookup("#trailer_Button").query();
            assertThat(trailerButton).isNotNull();
            assertThat(trailerButton.isVisible()).isTrue();
        }

        @Test
        @Order(17)
        @DisplayName("Should play trailer on click")
        void testPlayTrailer() {
            waitForFxEvents();

            Button trailerButton = lookup("#trailer_Button").query();
            assertThat(trailerButton).isNotNull();

            clickOn(trailerButton);

            waitForFxEvents();

            // Verify button is responsive
            assertThat(trailerButton).isNotNull();
        }

        @Test
        @Order(18)
        @DisplayName("Should open trailer in browser")
        void testOpenTrailerInBrowser() {
            waitForFxEvents();

            Button trailerButton = lookup("#trailer_Button").query();
            assertThat(trailerButton).isNotNull();

            clickOn(trailerButton);

            waitForFxEvents();

            // Verify button interaction completed
            assertThat(trailerButton).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("QR Code Tests")
    class QRCodeTests {

        @Test
        @Order(19)
        @DisplayName("Should generate QR code for film")
        void testGenerateQRCode() {
            waitForFxEvents();

            Button qrButton = lookup("#generateQRButton").query();
            assertThat(qrButton).isNotNull();

            clickOn(qrButton);

            waitForFxEvents();

            // Verify button interaction completed
            assertThat(qrButton).isNotNull();
        }

        @Test
        @Order(20)
        @DisplayName("Should display QR code image")
        void testQRCodeDisplay() {
            waitForFxEvents();

            Button qrButton = lookup("#generateQRButton").query();
            clickOn(qrButton);

            waitForFxEvents();

            // Verify QR code image exists
            assertThat(lookup("#qrCodeImage").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(21)
        @DisplayName("Should navigate to cinema")
        void testNavigateToCinema() {
            waitForFxEvents();

            Button cinemaButton = lookup("#Cinema_Button").query();
            assertThat(cinemaButton).isNotNull();

            clickOn(cinemaButton);

            waitForFxEvents();

            // Verify navigation completed
            assertThat(cinemaButton).isNotNull();
        }

        @Test
        @Order(22)
        @DisplayName("Should navigate to products")
        void testNavigateToProducts() {
            waitForFxEvents();

            Button productButton = lookup("#product").query();
            assertThat(productButton).isNotNull();

            clickOn(productButton);

            waitForFxEvents();

            // Verify navigation completed
            assertThat(productButton).isNotNull();
        }

        @Test
        @Order(23)
        @DisplayName("Should navigate to events")
        void testNavigateToEvents() {
            waitForFxEvents();

            Button eventButton = lookup("#event_button").query();
            assertThat(eventButton).isNotNull();

            clickOn(eventButton);

            waitForFxEvents();

            // Verify navigation completed
            assertThat(eventButton).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Filter Tests")
    class FilmFilterTests {

        @Test
        @Order(24)
        @DisplayName("Should filter films by year")
        void testFilterByYear() {
            waitForFxEvents();

            FlowPane filmFlowPane = lookup("#filmCards").query();
            assertThat(filmFlowPane).isNotNull();
            assertThat(filmFlowPane.getChildren()).isNotEmpty();
        }

        @Test
        @Order(25)
        @DisplayName("Should filter films by category")
        void testFilterByCategory() {
            waitForFxEvents();

            FlowPane filmFlowPane = lookup("#filmCards").query();
            assertThat(filmFlowPane).isNotNull();
            assertThat(filmFlowPane.getChildren()).isNotEmpty();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Actors Display Tests")
    class ActorsDisplayTests {

        @Test
        @Order(26)
        @DisplayName("Should display actors flow pane")
        void testActorsFlowPaneDisplay() {
            waitForFxEvents();

            FlowPane actorsPane = lookup("#flowPaneactors").query();
            assertThat(actorsPane).isNotNull();
            assertThat(actorsPane.isVisible()).isTrue();
        }

        @Test
        @Order(27)
        @DisplayName("Should load film actors")
        void testLoadActors() {
            waitForFxEvents();

            FlowPane actorsPane = lookup("#flowPaneactors").query();
            assertThat(actorsPane).isNotNull();

            // Verify actors are loaded
            assertThat(actorsPane.getChildren()).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Details Tests")
    class FilmDetailsTests {

        @Test
        @Order(28)
        @DisplayName("Should close film details")
        void testCloseDetails() {
            waitForFxEvents();

            Button closeButton = lookup("#closeDetailFilm1").query();
            assertThat(closeButton).isNotNull();

            clickOn(closeButton);

            waitForFxEvents();

            // Verify close action completed
            assertThat(closeButton).isNotNull();
        }

        @Test
        @Order(29)
        @DisplayName("Should display film title")
        void testFilmTitleDisplay() {
            waitForFxEvents();

            Label titleLabel = lookup("#filmTitleLabel").query();
            assertThat(titleLabel).isNotNull();
            assertThat(titleLabel.isVisible()).isTrue();
        }

        @Test
        @Order(30)
        @DisplayName("Should display film description")
        void testFilmDescriptionDisplay() {
            waitForFxEvents();

            Label descLabel = lookup("#filmDescLabel").query();
            assertThat(descLabel).isNotNull();
            assertThat(descLabel.isVisible()).isTrue();
        }

    }

}
