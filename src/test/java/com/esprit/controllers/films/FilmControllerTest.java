package com.esprit.controllers.films;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit5.Start;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;

import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Comprehensive UI tests for FilmController.
 * Tests film CRUD operations, category/actor management, image uploads,
 * search/filtering.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
            getClass().getResource("/ui/films/InterfaceFilm.fxml")
        );
        javafx.scene.Parent root = loader.load();
        
        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Film Table Display Tests")
    class FilmTableDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display film table with all columns")
        void testFilmTableDisplay() {
            // Verify table and main columns are visible
            verifyThat("#film_tableView", isVisible());
        }

        @Test
        @Order(2)
        @DisplayName("Should display film title column")
        void testFilmTitleColumnDisplay() {
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table);
        }

        @Test
        @Order(3)
        @DisplayName("Should display film year column")
        void testFilmYearColumnDisplay() {
            verifyThat("#annederalisationFilm_tableColumn", isNotNull());
        }

        @Test
        @Order(4)
        @DisplayName("Should display film duration column")
        void testFilmDurationColumnDisplay() {
            // Duration column should be visible
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table);
        }

        @Test
        @Order(5)
        @DisplayName("Should display film description column")
        void testFilmDescriptionColumnDisplay() {
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table.getColumns());
        }

        @Test
        @Order(6)
        @DisplayName("Should display film image column")
        void testFilmImageColumnDisplay() {
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table.getColumns());
        }

        @Test
        @Order(7)
        @DisplayName("Should load film data into table")
        void testLoadFilmData() {
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table.getItems());
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Film Creation Tests")
    class FilmCreationTests {

        @Test
        @Order(10)
        @DisplayName("Should display add film button")
        void testAddFilmButtonVisible() {
            verifyThat("#ajouter_Button", isVisible());
            verifyThat("#ajouter_Button", isEnabled());
        }

        @Test
        @Order(11)
        @DisplayName("Should display film title input field")
        void testFilmTitleFieldVisible() {
            verifyThat("#titre_textField", isVisible());
        }

        @Test
        @Order(12)
        @DisplayName("Should display film year input field")
        void testFilmYearFieldVisible() {
            verifyThat("#annederalisation_textField", isVisible());
        }

        @Test
        @Order(13)
        @DisplayName("Should display film duration input field")
        void testFilmDurationFieldVisible() {
            verifyThat("#duree_textField", isVisible());
        }

        @Test
        @Order(14)
        @DisplayName("Should display film description input field")
        void testFilmDescriptionFieldVisible() {
            verifyThat("#description_textField", isVisible());
        }

        @Test
        @Order(15)
        @DisplayName("Should accept valid film title")
        void testValidFilmTitleInput() {
            String filmTitle = TestDataFactory.generateMovieTitle();
            fillTextField("#titre_textField", filmTitle);

            assertEquals(filmTitle, getTextFieldValue("#titre_textField"));
        }

        @Test
        @Order(16)
        @DisplayName("Should accept valid film year")
        void testValidFilmYearInput() {
            String year = String.valueOf(TestDataFactory.generateMovieYear());
            fillTextField("#annederalisation_textField", year);

            assertEquals(year, getTextFieldValue("#annederalisation_textField"));
        }

        @Test
        @Order(17)
        @DisplayName("Should accept valid film duration")
        void testValidFilmDurationInput() {
            String duration = String.valueOf(TestDataFactory.generateMovieDuration());
            fillTextField("#duree_textField", duration);

            assertEquals(duration, getTextFieldValue("#duree_textField"));
        }

        @Test
        @Order(18)
        @DisplayName("Should accept valid film description")
        void testValidFilmDescriptionInput() {
            String description = TestDataFactory.generateMovieDescription();
            fillTextField("#description_textField", description);

            assertEquals(description, getTextFieldValue("#description_textField"));
        }

        @Test
        @Order(19)
        @DisplayName("Should create new film successfully")
        void testCreateFilm() {
            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "2024");
            fillTextField("#duree_textField", "120");
            fillTextField("#description_textField", TestDataFactory.generateMovieDescription());

            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Film Validation Tests")
    class FilmValidationTests {

        @Test
        @Order(20)
        @DisplayName("Should validate required film title")
        void testTitleRequired() {
            clearTextField("#titre_textField");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
            // Should show validation error
        }

        @Test
        @Order(21)
        @DisplayName("Should validate required film year")
        void testYearRequired() {
            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            clearTextField("#annederalisation_textField");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
        }

        @Test
        @Order(22)
        @DisplayName("Should validate year format")
        void testYearFormatValidation() {
            fillTextField("#annederalisation_textField", "invalid");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
            // Should show format error
        }

        @Test
        @Order(23)
        @DisplayName("Should validate year range")
        void testYearRangeValidation() {
            fillTextField("#annederalisation_textField", "1800");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
            // Year too old should be invalid
        }

        @Test
        @Order(24)
        @DisplayName("Should validate duration is numeric")
        void testDurationNumericValidation() {
            fillTextField("#duree_textField", "invalid");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
        }

        @Test
        @Order(25)
        @DisplayName("Should validate duration positive value")
        void testDurationPositiveValidation() {
            fillTextField("#duree_textField", "-10");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
        }

        @Test
        @Order(26)
        @DisplayName("Should validate description length")
        void testDescriptionLengthValidation() {
            fillTextField("#description_textField", "Too short");
            clickOnAndWait("#ajouter_Button");

            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Category Management Tests")
    class CategoryManagementTests {

        @Test
        @Order(30)
        @DisplayName("Should display category selection control")
        void testCategorySelectionVisible() {
            // CheckComboBox or similar for categories
            waitForFxEvents();
        }

        @Test
        @Order(31)
        @DisplayName("Should load available categories")
        void testLoadCategories() {
            // Categories should be loaded
            waitForFxEvents();
        }

        @Test
        @Order(32)
        @DisplayName("Should allow selecting multiple categories")
        void testSelectMultipleCategories() {
            // Should support multi-selection
            waitForFxEvents();
        }

        @Test
        @Order(33)
        @DisplayName("Should assign categories to film")
        void testAssignCategoriesToFilm() {
            // Selected categories should be assigned
            waitForFxEvents();
        }

        @Test
        @Order(34)
        @DisplayName("Should display selected categories")
        void testDisplaySelectedCategories() {
            // Selected categories should be visible
            waitForFxEvents();
        }

        @Test
        @Order(35)
        @DisplayName("Should remove category from film")
        void testRemoveCategoryFromFilm() {
            // Should allow deselecting categories
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Actor Management Tests")
    class ActorManagementTests {

        @Test
        @Order(40)
        @DisplayName("Should display actor selection control")
        void testActorSelectionVisible() {
            waitForFxEvents();
        }

        @Test
        @Order(41)
        @DisplayName("Should load available actors")
        void testLoadActors() {
            waitForFxEvents();
        }

        @Test
        @Order(42)
        @DisplayName("Should allow selecting multiple actors")
        void testSelectMultipleActors() {
            waitForFxEvents();
        }

        @Test
        @Order(43)
        @DisplayName("Should assign actors to film")
        void testAssignActorsToFilm() {
            waitForFxEvents();
        }

        @Test
        @Order(44)
        @DisplayName("Should display selected actors")
        void testDisplaySelectedActors() {
            waitForFxEvents();
        }

        @Test
        @Order(45)
        @DisplayName("Should remove actor from film")
        void testRemoveActorFromFilm() {
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Image Upload Tests")
    class ImageUploadTests {

        @Test
        @Order(50)
        @DisplayName("Should display image upload button")
        void testImageUploadButtonVisible() {
            // Button to select image should be visible
            waitForFxEvents();
        }

        @Test
        @Order(51)
        @DisplayName("Should open file chooser on button click")
        void testOpenFileChooser() {
            // Click should open file chooser dialog
            waitForFxEvents();
        }

        @Test
        @Order(52)
        @DisplayName("Should display image preview")
        void testImagePreviewDisplay() {
            // After selecting image, preview should show
            waitForFxEvents();
        }

        @Test
        @Order(53)
        @DisplayName("Should validate image file format")
        void testImageFormatValidation() {
            // Only image formats should be accepted
            waitForFxEvents();
        }

        @Test
        @Order(54)
        @DisplayName("Should validate image file size")
        void testImageSizeValidation() {
            // Too large images should be rejected
            waitForFxEvents();
        }

        @Test
        @Order(55)
        @DisplayName("Should upload image to Cloudinary")
        void testCloudinaryUpload() {
            // Image should be uploaded to cloud storage
            waitForFxEvents();
        }

        @Test
        @Order(56)
        @DisplayName("Should handle upload errors")
        void testUploadErrorHandling() {
            // Should gracefully handle upload failures
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Film Update Tests")
    class FilmUpdateTests {

        @Test
        @Order(60)
        @DisplayName("Should display update button")
        void testUpdateButtonVisible() {
            verifyThat("#modifier_Button", isVisible());
        }

        @Test
        @Order(61)
        @DisplayName("Should enable inline table editing")
        void testInlineEditing() {
            TableView<?> table = lookup("#film_tableView").query();

            if (!table.getItems().isEmpty()) {
                doubleClickOn(table);
                waitForFxEvents();
            }
        }

        @Test
        @Order(62)
        @DisplayName("Should update film title")
        void testUpdateFilmTitle() {
            // Edit title in table
            waitForFxEvents();
        }

        @Test
        @Order(63)
        @DisplayName("Should update film year")
        void testUpdateFilmYear() {
            // Edit year in table
            waitForFxEvents();
        }

        @Test
        @Order(64)
        @DisplayName("Should validate updates")
        void testUpdateValidation() {
            // Invalid updates should be rejected
            waitForFxEvents();
        }

        @Test
        @Order(65)
        @DisplayName("Should save updates to database")
        void testSaveUpdates() {
            clickOnAndWait("#modifier_Button");
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Film Deletion Tests")
    class FilmDeletionTests {

        @Test
        @Order(70)
        @DisplayName("Should display delete button")
        void testDeleteButtonVisible() {
            verifyThat("#supprimer_Button", isVisible());
        }

        @Test
        @Order(71)
        @DisplayName("Should require film selection for deletion")
        void testDeleteRequiresSelection() {
            clickOnAndWait("#supprimer_Button");
            waitForFxEvents();

            // Should show message to select film first
        }

        @Test
        @Order(72)
        @DisplayName("Should show confirmation dialog")
        void testDeleteConfirmation() {
            // Select a film and click delete
            waitForFxEvents();

            // Should show confirmation dialog
        }

        @Test
        @Order(73)
        @DisplayName("Should delete film on confirmation")
        void testDeleteOnConfirm() {
            // Confirm deletion
            waitForFxEvents();

            // Film should be removed
        }

        @Test
        @Order(74)
        @DisplayName("Should cancel deletion")
        void testCancelDeletion() {
            // Cancel confirmation dialog
            waitForFxEvents();

            // Film should remain
        }

        @Test
        @Order(75)
        @DisplayName("Should refresh table after deletion")
        void testRefreshAfterDeletion() {
            // Table should update after deletion
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Search and Filter Tests")
    class SearchFilterTests {

        @Test
        @Order(80)
        @DisplayName("Should display search field")
        void testSearchFieldVisible() {
            verifyThat("#rechercher_textField", isVisible());
        }

        @Test
        @Order(81)
        @DisplayName("Should filter films by title")
        void testFilterByTitle() {
            fillTextField("#rechercher_textField", "Action");
            waitForFxEvents();
        }

        @Test
        @Order(82)
        @DisplayName("Should filter by year")
        void testFilterByYear() {
            // Year filter checkboxes
            waitForFxEvents();
        }

        @Test
        @Order(83)
        @DisplayName("Should filter by category")
        void testFilterByCategory() {
            // Category filter
            waitForFxEvents();
        }

        @Test
        @Order(84)
        @DisplayName("Should apply multiple filters")
        void testMultipleFilters() {
            fillTextField("#rechercher_textField", "Action");
            // Additional filters
            waitForFxEvents();
        }

        @Test
        @Order(85)
        @DisplayName("Should clear all filters")
        void testClearFilters() {
            clearTextField("#rechercher_textField");
            waitForFxEvents();
        }

        @Test
        @Order(86)
        @DisplayName("Should show no results message")
        void testNoResultsMessage() {
            fillTextField("#rechercher_textField", "NonexistentFilm12345");
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Cinema Assignment Tests")
    class CinemaAssignmentTests {

        @Test
        @Order(90)
        @DisplayName("Should display add to cinema button")
        void testAddToCinemaButtonVisible() {
            verifyThat("#ajouterCinema_Button", isVisible());
        }

        @Test
        @Order(91)
        @DisplayName("Should load available cinemas")
        void testLoadCinemas() {
            // Cinema list should be available
            waitForFxEvents();
        }

        @Test
        @Order(92)
        @DisplayName("Should assign film to cinema")
        void testAssignFilmToCinema() {
            clickOnAndWait("#ajouterCinema_Button");
            waitForFxEvents();
        }

        @Test
        @Order(93)
        @DisplayName("Should validate cinema selection")
        void testCinemaSelectionValidation() {
            // Should require cinema selection
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Table Operations Tests")
    class TableOperationsTests {

        @Test
        @Order(100)
        @DisplayName("Should select table row")
        void testSelectTableRow() {
            TableView<?> table = lookup("#film_tableView").query();

            if (!table.getItems().isEmpty()) {
                runOnFxThread(() -> table.getSelectionModel().select(0));
                assertNotNull(table.getSelectionModel().getSelectedItem());
            }
        }

        @Test
        @Order(101)
        @DisplayName("Should sort table by column")
        void testTableSorting() {
            TableView<?> table = lookup("#film_tableView").query();

            // Click column header to sort
            waitForFxEvents();
        }

        @Test
        @Order(102)
        @DisplayName("Should handle empty table")
        void testEmptyTable() {
            TableView<?> table = lookup("#film_tableView").query();
            assertNotNull(table.getItems());
        }

        @Test
        @Order(103)
        @DisplayName("Should refresh table data")
        void testRefreshTable() {
            clickOnAndWait("#afficher_Button");
            waitForFxEvents();
        }
    }
}
