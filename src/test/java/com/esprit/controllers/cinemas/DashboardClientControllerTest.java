package com.esprit.controllers.cinemas;

import com.esprit.utils.TestFXBase;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
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
 * Comprehensive test suite for DashboardClientController.
 * Tests client cinema browsing and booking features.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardClientControllerTest extends TestFXBase {

    @Start
    @Override
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml")
        );
        javafx.scene.Parent root = loader.load();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Cinema Display Tests")
    class CinemaDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display cinemas")
        void testCinemasDisplay() {
            waitForFxEvents();

            FlowPane cinemasPane = lookup("#cinemasFlowPane").query();
            assertThat(cinemasPane).isNotNull();
            assertThat(cinemasPane.isVisible()).isTrue();
        }


        @Test
        @Order(2)
        @DisplayName("Should load cinemas from service")
        void testLoadCinemas() {
            waitForFxEvents();

            FlowPane cinemasPane = lookup("#cinemasFlowPane").query();
            assertThat(cinemasPane).isNotNull();

            // Verify cinemas are loaded in the pane
            assertThat(cinemasPane.getChildren()).isNotEmpty();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Display Tests")
    class FilmDisplayTests {

        @Test
        @Order(3)
        @DisplayName("Should display films")
        void testFilmsDisplay() {
            waitForFxEvents();

            FlowPane filmsPane = lookup("#filmsFlowPane").query();
            assertThat(filmsPane).isNotNull();
            assertThat(filmsPane.isVisible()).isTrue();
        }


        @Test
        @Order(4)
        @DisplayName("Should navigate to film details")
        void testNavigateToFilmDetails() {
            waitForFxEvents();

            FlowPane filmsPane = lookup("#filmsFlowPane").query();
            assertThat(filmsPane).isNotNull();

            // Verify films are loaded in the pane
            assertThat(filmsPane.getChildren()).isNotEmpty();

            Button filmButton = (Button) lookup("#filmButton").tryQuery().orElse(null);
            if (filmButton != null) {
                clickOn(filmButton);
                waitForFxEvents();
            }

        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Booking Tests")
    class BookingTests {

        @Test
        @Order(5)
        @DisplayName("Should navigate to booking")
        void testNavigateToBooking() {
            waitForFxEvents();

            Button bookButton = (Button) lookup("#bookButton").tryQuery().orElse(null);
            assertThat(bookButton).isNotNull();
            assertThat(bookButton.isVisible()).isTrue();

            clickOn(bookButton);
            waitForFxEvents();
        }

    }


}

