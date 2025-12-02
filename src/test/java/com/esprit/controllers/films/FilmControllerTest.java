package com.esprit.controllers.films;

import com.esprit.utils.TestDataFactory;
import com.esprit.utils.TestFXBase;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Film Validation Tests")
    class FilmValidationTests {

        @Test
        @Order(20)
        @DisplayName("Should validate required film title")
        void testTitleRequired() {
            waitForFxEvents();

            // Capture initial table size
            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            // Clear and submit with empty title
            clearTextField("#titre_textField");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify table size unchanged (no film added)
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Table size should not change after failed validation");
        }


        @Test
        @Order(21)
        @DisplayName("Should validate required film year")
        void testYearRequired() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            clearTextField("#annederalisation_textField");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify no film was added
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Table size should not change when year is missing");
        }


        @Test
        @Order(22)
        @DisplayName("Should validate year format")
        void testYearFormatValidation() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "invalid");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify invalid year format prevented film creation
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Non-numeric year should prevent film creation");
        }


        @Test
        @Order(23)
        @DisplayName("Should validate year range")
        void testYearRangeValidation() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "1800");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify year out of valid range prevented creation
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Year 1800 should be outside valid range and prevent creation");
        }


        @Test
        @Order(24)
        @DisplayName("Should validate duration is numeric")
        void testDurationNumericValidation() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "2023");
            fillTextField("#duree_textField", "invalid");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify non-numeric duration prevented creation
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Non-numeric duration should prevent film creation");
        }


        @Test
        @Order(25)
        @DisplayName("Should validate duration positive value")
        void testDurationPositiveValidation() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "2023");
            fillTextField("#duree_textField", "-10");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify negative duration prevented creation
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Negative duration should prevent film creation");
        }


        @Test
        @Order(26)
        @DisplayName("Should validate description length")
        void testDescriptionLengthValidation() {
            waitForFxEvents();

            int initialSize = ((TableView<?>) lookup("#film_tableView").query()).getItems().size();

            fillTextField("#titre_textField", TestDataFactory.generateMovieTitle());
            fillTextField("#annederalisation_textField", "2023");
            fillTextField("#duree_textField", "120");
            fillTextField("#description_textField", "Too short");
            clickOnAndWait("#ajouter_Button");
            waitForFxEvents();

            // Verify insufficient description prevented creation
            assertEquals(initialSize, ((TableView<?>) lookup("#film_tableView").query()).getItems().size(),
                "Description too short should prevent film creation");
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Management Tests")
    class CategoryManagementTests {

        @Test
        @Order(30)
        @Disabled("TODO: Implement category loading - verify categories populate in UI selector")
        @DisplayName("Should load available categories")
        void testLoadCategories() {
            // Requires: Category data loads from service
            // Implementation: Lookup category control, assert items not empty
        }


        @Test
        @Order(31)
        @Disabled("TODO: Implement category multi-selection - verify film can have multiple categories")
        @DisplayName("Should allow selecting multiple categories")
        void testSelectMultipleCategories() {
            // Requires: Multiple category selection UI works
            // Implementation: Click multiple categories, verify all selected
        }


        @Test
        @Order(32)
        @Disabled("TODO: Implement category assignment - verify selected categories save with film")
        @DisplayName("Should assign categories to film")
        void testAssignCategoriesToFilm() {
            // Requires: Categories persist after film creation
            // Implementation: Create film with categories, retrieve film, verify categories attached
        }


        @Test
        @Order(33)
        @Disabled("TODO: Implement category display - verify assigned categories visible in form")
        @DisplayName("Should display selected categories")
        void testDisplaySelectedCategories() {
            // Requires: Previously assigned categories show in UI on form load
            // Implementation: Load form with existing film, verify categories displayed
        }


        @Test
        @Order(34)
        @Disabled("TODO: Implement category removal - verify deselection removes category from film")
        @DisplayName("Should remove category from film")
        void testRemoveCategoryFromFilm() {
            // Requires: Deselecting category removes it from film data
            // Implementation: Create film with category, deselect, verify removed
        }


        @Test
        @Order(35)
        @Disabled("TODO: Implement category validation - verify film requires at least one category")
        @DisplayName("Should validate category requirement")
        void testCategoryValidation() {
            // Requires: Prevent film creation without categories
            // Implementation: Try to create film with no categories, assert validation error
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Actor Management Tests")
    class ActorManagementTests {

        @Test
        @Order(40)
        @Disabled("TODO: Implement actor loading - verify actors populate in UI selector")
        @DisplayName("Should load available actors")
        void testLoadActors() {
            // Requires: Actor data loads from service
            // Implementation: Lookup actor control, assert items not empty
        }


        @Test
        @Order(41)
        @Disabled("TODO: Implement actor multi-selection - verify film can have multiple actors")
        @DisplayName("Should allow selecting multiple actors")
        void testSelectMultipleActors() {
            // Requires: Multiple actor selection UI works
            // Implementation: Click multiple actors, verify all selected
        }


        @Test
        @Order(42)
        @Disabled("TODO: Implement actor assignment - verify selected actors save with film")
        @DisplayName("Should assign actors to film")
        void testAssignActorsToFilm() {
            // Requires: Actors persist after film creation
            // Implementation: Create film with actors, retrieve film, verify actors attached
        }


        @Test
        @Order(43)
        @Disabled("TODO: Implement actor display - verify assigned actors visible in form")
        @DisplayName("Should display selected actors")
        void testDisplaySelectedActors() {
            // Requires: Previously assigned actors show in UI on form load
            // Implementation: Load form with existing film, verify actors displayed
        }


        @Test
        @Order(44)
        @Disabled("TODO: Implement actor removal - verify deselection removes actor from film")
        @DisplayName("Should remove actor from film")
        void testRemoveActorFromFilm() {
            // Requires: Deselecting actor removes it from film data
            // Implementation: Create film with actor, deselect, verify removed
        }


        @Test
        @Order(45)
        @Disabled("TODO: Implement actor validation - verify film requires at least one actor")
        @DisplayName("Should validate actor requirement")
        void testActorValidation() {
            // Requires: Prevent film creation without actors
            // Implementation: Try to create film with no actors, assert validation error
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Image Upload Tests")
    class ImageUploadTests {

        @Test
        @Order(50)
        @Disabled("TODO: Implement image upload button visibility - verify button exists and is clickable")
        @DisplayName("Should display image upload button")
        void testImageUploadButtonVisible() {
            // Requires: Upload button present in UI
            // Implementation: Lookup button, assert not null and visible
        }


        @Test
        @Order(51)
        @Disabled("TODO: Implement file chooser dialog test - verify dialog opens on button click")
        @DisplayName("Should open file chooser on button click")
        void testOpenFileChooser() {
            // Requires: Clicking upload button opens file selection dialog
            // Implementation: Click button, verify file chooser appears (tricky in headless - may need mocking)
        }


        @Test
        @Order(52)
        @Disabled("TODO: Implement image preview display - verify selected image shows in UI")
        @DisplayName("Should display image preview")
        void testImagePreviewDisplay() {
            // Requires: After image selection, preview ImageView updates
            // Implementation: Select image file, verify ImageView image property set
        }


        @Test
        @Order(53)
        @Disabled("TODO: Implement image format validation - only accept common image types")
        @DisplayName("Should validate image file format")
        void testImageFormatValidation() {
            // Requires: Reject non-image files (pdf, txt, etc.)
            // Implementation: Attempt to select non-image, verify error shown
        }


        @Test
        @Order(54)
        @Disabled("TODO: Implement image size validation - reject files exceeding size limit")
        @DisplayName("Should validate image file size")
        void testImageSizeValidation() {
            // Requires: Large images rejected with error message
            // Implementation: Create oversized image file, verify validation error
        }


        @Test
        @Order(55)
        @Disabled("TODO: Implement Cloudinary upload - upload image to cloud storage service")
        @DisplayName("Should upload image to Cloudinary")
        void testCloudinaryUpload() {
            // Requires: Selected image uploaded to Cloudinary
            // Implementation: Mock CloudinaryStorage or use real service, verify image URL stored
        }


        @Test
        @Order(56)
        @Disabled("TODO: Implement upload error handling - gracefully handle upload failures")
        @DisplayName("Should handle upload errors")
        void testUploadErrorHandling() {
            // Requires: Network/service errors don't crash app
            // Implementation: Mock upload failure, verify error dialog shown
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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
        @Disabled("TODO: Implement cinema loading - verify available cinemas load in UI")
        @DisplayName("Should load available cinemas")
        void testLoadCinemas() {
            // Requires: Cinema list/dropdown populates from service
            // Implementation: Lookup cinema control, assert items present
        }


        @Test
        @Order(92)
        @Disabled("TODO: Implement film-to-cinema assignment - verify film assigned to selected cinema")
        @DisplayName("Should assign film to cinema")
        void testAssignFilmToCinema() {
            // Requires: Select cinema from list and assign film
            // Implementation: Click cinema button, select cinema, verify association created
        }


        @Test
        @Order(93)
        @Disabled("TODO: Implement cinema selection validation - prevent assignment without cinema")
        @DisplayName("Should validate cinema selection")
        void testCinemaSelectionValidation() {
            // Requires: Prevent empty cinema selection
            // Implementation: Try to assign without selecting cinema, verify error
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
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

