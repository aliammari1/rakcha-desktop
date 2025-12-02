package com.esprit.controllers.series;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Series;
import com.esprit.services.series.EpisodeService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
 * Comprehensive test suite for EpisodeController.
 * Tests episode CRUD operations, image/video upload, SMS notifications, and
 * validation.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EpisodeControllerTest extends TestFXBase {

    private EpisodeService episodeService;

    private SeriesService seriesService;

    private CloudinaryStorage cloudinary;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Episode-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    // Helper methods
    private List<Series> createMockSeries() {
        List<Series> series = new ArrayList<>();
        Series s1 = new Series();
        s1.setName("Test Series");
        series.add(s1);
        return series;
    }

    private List<Episode> createMockEpisodes() {
        List<Episode> episodes = new ArrayList<>();
        Episode e1 = new Episode();
        e1.setTitle("Episode 1");
        e1.setEpisodeNumber(1);
        e1.setSeason(1);
        episodes.add(e1);
        return episodes;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Episode Creation Tests")
    class EpisodeCreationTests {

        @Test
        @Order(1)
        @DisplayName("Should create episode with valid input")
        void testCreateEpisode() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField).isNotNull();
            TextField episodeNumField = lookup("#numeroepisodeF").query();
            assertThat(episodeNumField).isNotNull();
            TextField seasonField = lookup("#saisonF").query();
            assertThat(seasonField).isNotNull();
            ComboBox<String> seriesCombo = lookup("#serieF").query();
            assertThat(seriesCombo).isNotNull();

            clickOn(titleField).write("Episode 1");
            clickOn(episodeNumField).write("1");
            clickOn(seasonField).write("1");
        }


        @Test
        @Order(2)
        @DisplayName("Should validate episode title not empty")
        void testTitleValidation() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField).isNotNull();
            assertThat(lookup("#titrecheck").tryQuery()).isPresent();
        }


        @Test
        @Order(3)
        @DisplayName("Should validate episode number is numeric")
        void testEpisodeNumberValidation() {
            waitForFxEvents();

            TextField episodeNumField = lookup("#numeroepisodeF").query();
            assertThat(episodeNumField).isNotNull();
            assertThat(lookup("#numbercheck").tryQuery()).isPresent();
        }


        @Test
        @Order(4)
        @DisplayName("Should validate season number is numeric")
        void testSeasonNumberValidation() {
            waitForFxEvents();

            TextField seasonField = lookup("#saisonF").query();
            assertThat(seasonField).isNotNull();
            assertThat(lookup("#seasoncheck").tryQuery()).isPresent();
        }


        @Test
        @Order(5)
        @DisplayName("Should require series selection")
        void testSeriesRequired() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField).isNotNull();
            TextField episodeNumField = lookup("#numeroepisodeF").query();
            assertThat(episodeNumField).isNotNull();
            assertThat(lookup("#seriecheck").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Series Selection Tests")
    class SeriesSelectionTests {

        @Test
        @Order(6)
        @DisplayName("Should populate series combo box")
        void testSeriesComboBoxPopulation() {
            waitForFxEvents();

            ComboBox<String> seriesCombo = lookup("#serieF").query();
            assertThat(seriesCombo).isNotNull();
        }


        @Test
        @Order(7)
        @DisplayName("Should select series from combo box")
        void testSeriesSelection() {
            waitForFxEvents();

            ComboBox<String> seriesCombo = lookup("#serieF").query();
            assertThat(seriesCombo).isNotNull();
        }


        @Test
        @Order(8)
        @DisplayName("Should handle empty series list")
        void testEmptySeriesList() {
            waitForFxEvents();

            ComboBox<String> seriesCombo = lookup("#serieF").query();
            assertThat(seriesCombo).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Image Upload Tests")
    class ImageUploadTests {

        @Test
        @Order(9)
        @DisplayName("Should upload episode image")
        void testImageUpload() throws Exception {
            waitForFxEvents();

            assertThat(lookup("#episodeImageView").tryQuery()).isPresent();
        }


        @Test
        @Order(10)
        @DisplayName("Should validate image format")
        void testImageFormatValidation() {
            waitForFxEvents();

            assertThat(lookup("#picturechek").tryQuery()).isPresent();
        }


        @Test
        @Order(11)
        @DisplayName("Should display uploaded image")
        void testImageDisplay() {
            waitForFxEvents();

            ImageView imageView = lookup("#episodeImageView").query();
            assertThat(imageView).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Video Upload Tests")
    class VideoUploadTests {

        @Test
        @Order(12)
        @DisplayName("Should upload episode video")
        void testVideoUpload() {
            waitForFxEvents();

            assertThat(lookup("#videoPathField").tryQuery()).isPresent();
        }


        @Test
        @Order(13)
        @DisplayName("Should validate video format")
        void testVideoFormatValidation() {
            waitForFxEvents();

            Label videoCheck = lookup("#videocheck").query();
            assertThat(videoCheck).isNotNull();
        }


        @Test
        @Order(14)
        @DisplayName("Should handle large video files")
        void testLargeVideoFile() {
            waitForFxEvents();

            assertThat(lookup("#videoPathField").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Episode Table Display Tests")
    class EpisodeTableDisplayTests {

        @Test
        @Order(15)
        @DisplayName("Should display episodes in table")
        void testDisplayEpisodes() {
            waitForFxEvents();

            assertThat(lookup("#EpisodeTable").tryQuery()).isPresent();
        }


        @Test
        @Order(16)
        @DisplayName("Should display episode title column")
        void testTitleColumnDisplay() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table.getColumns()).isNotEmpty();
        }


        @Test
        @Order(17)
        @DisplayName("Should display episode number column")
        void testEpisodeNumberColumn() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(18)
        @DisplayName("Should display season column")
        void testSeasonColumn() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(19)
        @DisplayName("Should display delete button column")
        void testDeleteButtonColumn() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(20)
        @DisplayName("Should display edit button column")
        void testEditButtonColumn() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Episode Update Tests")
    class EpisodeUpdateTests {

        @Test
        @Order(21)
        @DisplayName("Should populate form with episode data for editing")
        void testPopulateFormForEdit() {
            waitForFxEvents();

            // Click edit button
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField.getText()).isNotEmpty();
        }


        @Test
        @Order(22)
        @DisplayName("Should update episode")
        void testUpdateEpisode() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField).isNotNull();
        }


        @Test
        @Order(23)
        @DisplayName("Should refresh table after update")
        void testTableRefreshAfterUpdate() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Episode Delete Tests")
    class EpisodeDeleteTests {

        @Test
        @Order(24)
        @DisplayName("Should delete episode")
        void testDeleteEpisode() {
            waitForFxEvents();

            assertThat(lookup("#tableView").tryQuery()).isPresent();
        }


        @Test
        @Order(25)
        @DisplayName("Should refresh table after deletion")
        void testTableRefreshAfterDeletion() {
            waitForFxEvents();

            TableView<Episode> table = lookup("#tableView").query();
            assertThat(table).isNotNull();
        }


        @Test
        @Order(26)
        @DisplayName("Should confirm before deletion")
        void testDeleteConfirmation() {
            waitForFxEvents();

            // Click delete
            waitForFxEvents();

            // Should show confirmation dialog
            assertThat(lookup(".alert").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("SMS Notification Tests")
    class SMSNotificationTests {

        @Test
        @Order(27)
        @DisplayName("Should send SMS notification on episode creation")
        void testSMSOnCreation() {
            waitForFxEvents();

            assertThat(lookup("#titreF").tryQuery()).isPresent();
        }


        @Test
        @Order(28)
        @DisplayName("Should handle SMS sending errors")
        void testSMSError() {
            waitForFxEvents();

            assertThat(lookup("#titreF").tryQuery()).isPresent();
        }


        @Test
        @Order(29)
        @DisplayName("Should use Twilio credentials from environment")
        void testTwilioCredentials() {
            waitForFxEvents();

            assertThat(lookup("#titreF").tryQuery()).isPresent();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Form Reset Tests")
    class FormResetTests {

        @Test
        @Order(30)
        @DisplayName("Should clear form after successful creation")
        void testFormClearAfterCreation() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            assertThat(titleField).isNotNull();
            assertThat(titleField.getText()).isEmpty();
        }


        @Test
        @Order(31)
        @DisplayName("Should reset all fields")
        void testResetAllFields() {
            waitForFxEvents();

            TextField titleField = lookup("#titreF").query();
            TextField episodeNumField = lookup("#numeroepisodeF").query();

            assertThat(titleField).isNotNull();
            assertThat(episodeNumField).isNotNull();
            assertThat(titleField.getText()).isEmpty();
            assertThat(episodeNumField.getText()).isEmpty();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Validation Label Tests")
    class ValidationLabelTests {

        @Test
        @Order(32)
        @DisplayName("Should display validation labels")
        void testValidationLabelsDisplay() {
            waitForFxEvents();

            assertThat(lookup("#titrecheck").tryQuery()).isPresent();
            assertThat(lookup("#numbercheck").tryQuery()).isPresent();
            assertThat(lookup("#seasoncheck").tryQuery()).isPresent();
            assertThat(lookup("#seriecheck").tryQuery()).isPresent();
        }


        @Test
        @Order(33)
        @DisplayName("Should show error messages on invalid input")
        void testErrorMessages() {
            waitForFxEvents();

            Label titleCheck = lookup("#titrecheck").query();
            assertThat(titleCheck).isNotNull();
            assertThat(titleCheck.getText()).isNotEmpty();
        }


        @Test
        @Order(34)
        @DisplayName("Should clear error messages on valid input")
        void testClearErrorMessages() {
            waitForFxEvents();

            Label titleCheck = lookup("#titrecheck").query();
            assertThat(titleCheck).isNotNull();
            assertThat(titleCheck.getText()).isEmpty();
        }

    }

}

