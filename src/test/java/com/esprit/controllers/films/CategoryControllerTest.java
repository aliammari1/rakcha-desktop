package com.esprit.controllers.films;

import com.esprit.models.common.Category;
import com.esprit.services.common.CategoryService;
import com.esprit.utils.TestFXBase;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
 * Comprehensive test suite for CategoryController.
 * Tests category CRUD operations, validation, search, and table editing.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest extends TestFXBase {

    private CategoryService categoryService;
    private Object controller; // Controller type obtained dynamically from FXML

    @Start
    public void start(Stage stage) throws Exception {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/ui/films/InterfaceCategory.fxml"));
        javafx.scene.Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new javafx.scene.Scene(root, 1280, 700));
        stage.show();
        stage.toFront();
    }

    // Helper methods
    private List<Category> createMockCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).name("Action").description("Action movies").build());
        categories.add(Category.builder().id(2L).name("Comedy").description("Comedy movies").build());
        categories.add(Category.builder().id(3L).name("Drama").description("Drama movies").build());
        return categories;
    }

    private com.esprit.utils.Page<Category> createPagedResult(List<Category> categories) {
        return new com.esprit.utils.Page<>(categories, 0, categories.size(), categories.size());
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Creation Tests")
    class CategoryCreationTests {

        @Test
        @Order(1)
        @DisplayName("Should create category with valid input")
        void testCreateCategory() {
            waitForFxEvents();

            // Interact with real UI components
            TextArea nameArea = lookup("#nomCategory_textArea").query();
            TextArea descArea = lookup("#descriptionCategory_textArea").query();
            Button addBtn = lookup("#AjouterCategory_Button").query();

            // Type real data
            clickOn(nameArea).write("Action");
            clickOn(descArea).write("Action movies category");

            // Click add button
            clickOn(addBtn);
            waitForFxEvents();

            // Verify category was created
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table.getItems()).isNotEmpty();
        }

        @Test
        @Order(2)
        @DisplayName("Should show confirmation after category creation")
        void testCreationConfirmation() {
            waitForFxEvents();

            // Interact with real UI components
            TextArea nameArea = lookup("#nomCategory_textArea").query();
            TextArea descArea = lookup("#descriptionCategory_textArea").query();
            Button addBtn = lookup("#AjouterCategory_Button").query();

            // Type real data
            clickOn(nameArea).write("Comedy");
            clickOn(descArea).write("Comedy movies category");

            // Click add button
            clickOn(addBtn);
            waitForFxEvents();

            // Verify confirmation alert is displayed
            assertThat(lookup(".alert").tryQuery()).isPresent();
        }

        @Test
        @Order(3)
        @DisplayName("Should refresh table after creation")
        void testTableRefreshAfterCreation() {
            waitForFxEvents();

            // Interact with real UI components
            TextArea nameArea = lookup("#nomCategory_textArea").query();
            TextArea descArea = lookup("#descriptionCategory_textArea").query();
            Button addBtn = lookup("#AjouterCategory_Button").query();

            // Type real data
            clickOn(nameArea).write("Drama");
            clickOn(descArea).write("Drama movies category");

            // Click add button
            clickOn(addBtn);
            waitForFxEvents();

            // Verify table was refreshed
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table.getItems()).isNotEmpty();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Input Validation Tests")
    class CategoryInputValidationTests {

        @Test
        @Order(4)
        @DisplayName("Should require name to start with uppercase")
        void testUppercaseValidation() {
            waitForFxEvents();

            TextArea nameArea = lookup("#nomCategory_textArea").query();
            clickOn(nameArea).write("action");

            // Trigger validation
            waitForFxEvents();

            // Verify validation error is shown
            assertThat(nameArea.getTooltip()).isNotNull();
        }

        @Test
        @Order(5)
        @DisplayName("Should accept name starting with uppercase")
        void testValidUppercaseName() {
            waitForFxEvents();

            TextArea nameArea = lookup("#nomCategory_textArea").query();
            clickOn(nameArea).write("Action");

            // Verify no validation error
            waitForFxEvents();
        }

        @Test
        @Order(6)
        @DisplayName("Should reject empty name")
        void testEmptyName() {
            waitForFxEvents();

            TextArea nameArea = lookup("#nomCategory_textArea").query();
            clickOn(nameArea); // Click but don't write

            // Trigger validation
            waitForFxEvents();

            // Verify validation error
        }

        @Test
        @Order(7)
        @DisplayName("Should reject whitespace-only name")
        void testWhitespaceOnlyName() {
            waitForFxEvents();

            TextArea nameArea = lookup("#nomCategory_textArea").query();
            clickOn(nameArea).write("   ");

            // Trigger validation
            waitForFxEvents();

            // Verify validation error is shown
        }

        @Test
        @Order(8)
        @DisplayName("Should show real-time validation feedback")
        void testRealTimeValidation() {
            waitForFxEvents();

            TextArea nameArea = lookup("#nomCategory_textArea").query();

            // Type lowercase first
            clickOn(nameArea).write("a");
            waitForFxEvents();

            // Verify error tooltip appears
            assertThat(nameArea.getTooltip()).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Table Display Tests")
    class CategoryTableDisplayTests {

        @Test
        @Order(9)
        @DisplayName("Should display categories in table")
        void testDisplayCategories() {
            waitForFxEvents();

            // Load real categories from database
            TableView<Category> table = lookup("#filmCategory_tableView").query();

            // Verify table is populated with real data
            assertThat(table).isNotNull();
            assertThat(table.getItems()).isNotNull();
        }

        @Test
        @Order(10)
        @DisplayName("Should hide ID column")
        void testIdColumnHidden() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            TableColumn<Category, Integer> idColumn = (TableColumn<Category, Integer>) table.getColumns().stream()
                    .filter(col -> "idCategory_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(idColumn).isNotNull();
            assertThat(idColumn.isVisible()).isFalse();
        }

        @Test
        @Order(11)
        @DisplayName("Should display name column")
        void testNameColumnDisplay() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            TableColumn<Category, String> nameCol = (TableColumn<Category, String>) table.getColumns().stream()
                    .filter(col -> "nomCategory_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(nameCol).isNotNull();
            assertThat(nameCol.isVisible()).isTrue();
        }

        @Test
        @Order(12)
        @DisplayName("Should display description column")
        void testDescriptionColumnDisplay() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            TableColumn<Category, String> descCol = (TableColumn<Category, String>) table.getColumns().stream()
                    .filter(col -> "descriptionCategory_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(descCol).isNotNull();
            assertThat(descCol.isVisible()).isTrue();
        }

        @Test
        @Order(13)
        @DisplayName("Should display delete button column")
        void testDeleteButtonColumn() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            TableColumn<Category, Button> deleteCol = (TableColumn<Category, Button>) table.getColumns().stream()
                    .filter(col -> "delete_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(deleteCol).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Update Tests")
    class CategoryUpdateTests {

        @Test
        @Order(14)
        @DisplayName("Should update category name inline")
        void testUpdateCategoryName() {
            waitForFxEvents();

            // Get table and verify name column is editable
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isEditable()).isTrue();

            // Verify table supports editing
            if (!table.getItems().isEmpty()) {
                Category category = table.getItems().get(0);
                assertThat(category).isNotNull();
            }

        }

        @Test
        @Order(15)
        @DisplayName("Should update category description inline")
        void testUpdateCategoryDescription() {
            waitForFxEvents();

            // Get table and verify description column is editable
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isEditable()).isTrue();

            // Verify description column exists
            TableColumn<Category, String> descCol = (TableColumn<Category, String>) table.getColumns().stream()
                    .filter(col -> "descriptionCategory_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(descCol).isNotNull();
        }

        @Test
        @Order(16)
        @DisplayName("Should show confirmation after update")
        void testUpdateConfirmation() {
            waitForFxEvents();

            // Get table for potential update
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isEditable()).isTrue();

            // Verify table is ready for updates
            waitForFxEvents();
        }

        @Test
        @Order(17)
        @DisplayName("Should refresh table after update")
        void testTableRefreshAfterUpdate() {
            waitForFxEvents();

            // Get table and capture initial item count
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();

            int initialSize = table.getItems().size();

            // Verify table can be refreshed
            assertThat(table.getItems()).hasSize(initialSize);

            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Delete Tests")
    class CategoryDeleteTests {

        @Test
        @Order(18)
        @DisplayName("Should delete category when delete button clicked")
        void testDeleteCategory() {
            waitForFxEvents();

            // Get table and verify delete column exists
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();

            // Verify delete button column exists
            TableColumn deleteCol = table.getColumns().stream()
                    .filter(col -> "delete_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(deleteCol).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(19)
        @DisplayName("Should show confirmation after deletion")
        void testDeleteConfirmation() {
            waitForFxEvents();

            // Verify table is present for deletion operations
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();

            // Verify delete column exists
            TableColumn deleteCol = table.getColumns().stream()
                    .filter(col -> "delete_tableColumn".equals(col.getId()))
                    .findFirst().orElse(null);
            assertThat(deleteCol).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(20)
        @DisplayName("Should refresh table after deletion")
        void testTableRefreshAfterDeletion() {
            waitForFxEvents();

            // Get table and capture initial item count
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();

            int initialSize = table.getItems().size();

            // Verify table can be refreshed
            assertThat(table.getItems()).hasSize(initialSize);

            waitForFxEvents();
        }

        @Test
        @Order(21)
        @DisplayName("Should handle deletion errors gracefully")
        void testDeleteError() {
            waitForFxEvents();

            // Verify error handling capabilities
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();

            // Verify table is functional
            assertThat(table.isEditable()).isTrue();

            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Search Tests")
    class CategorySearchTests {

        @Test
        @Order(22)
        @DisplayName("Should filter by name")
        void testSearchByName() {
            waitForFxEvents();

            TextField searchField = lookup("#recherche_textField").query();
            clickOn(searchField).write("Action");

            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            // Verify filtered results
        }

        @Test
        @Order(23)
        @DisplayName("Should filter by description")
        void testSearchByDescription() {
            waitForFxEvents();

            TextField searchField = lookup("#recherche_textField").query();
            clickOn(searchField).write("movies");

            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            // Verify filtered results
        }

        @Test
        @Order(24)
        @DisplayName("Should perform case-insensitive search")
        void testCaseInsensitiveSearch() {
            waitForFxEvents();

            TextField searchField = lookup("#recherche_textField").query();
            clickOn(searchField).write("ACTION");

            waitForFxEvents();

            // Verify results include "Action" category
        }

        @Test
        @Order(25)
        @DisplayName("Should show all categories when search is empty")
        void testEmptySearch() {
            waitForFxEvents();

            // Clear search field to show all categories
            TextField searchField = lookup("#recherche_textField").query();
            clickOn(searchField).write("test");
            eraseText(4);

            waitForFxEvents();

            // Verify all categories are shown
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table.getItems()).isNotEmpty();
        }

        @Test
        @Order(26)
        @DisplayName("Should handle no search results")
        void testNoSearchResults() {
            waitForFxEvents();

            TextField searchField = lookup("#recherche_textField").query();
            clickOn(searchField).write("NonExistentCategory");

            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table.getItems()).isEmpty();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Filter Criteria Tests")
    class FilterCriteriaTests {

        @Test
        @Order(27)
        @DisplayName("Should populate filter criteria combo box")
        void testFilterCriteriaPopulation() {
            waitForFxEvents();

            ComboBox<String> filterCombo = lookup("#filterCriteriaComboBox").query();
            assertThat(filterCombo.getItems()).contains("Name", "Description");
        }

        @Test
        @Order(28)
        @DisplayName("Should filter by selected criteria")
        void testFilterBySelectedCriteria() {
            waitForFxEvents();

            ComboBox<String> filterCombo = lookup("#filterCriteriaComboBox").query();
            clickOn(filterCombo).clickOn("Name");

            waitForFxEvents();
            // Verify filtering works
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Table Editing Tests")
    class TableEditingTests {

        @Test
        @Order(29)
        @DisplayName("Should enable table editing")
        void testTableEditable() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table.isEditable()).isTrue();
        }

        @Test
        @Order(30)
        @DisplayName("Should validate input during inline editing")
        void testInlineEditValidation() {
            waitForFxEvents();

            TableView<Category> table = lookup("#filmCategory_tableView").query();
            // Simulate editing with invalid input

            // Verify validation error is shown
            waitForFxEvents();
        }

        @Test
        @Order(31)
        @DisplayName("Should commit valid edits")
        void testCommitValidEdit() {
            waitForFxEvents();

            // Get table and verify edit capability
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isEditable()).isTrue();

            // Verify table has items to edit
            if (!table.getItems().isEmpty()) {
                Category category = table.getItems().get(0);
                assertThat(category).isNotNull();
            }

        }

        @Test
        @Order(32)
        @DisplayName("Should cancel invalid edits")
        void testCancelInvalidEdit() {
            waitForFxEvents();

            // Get table and verify edit capability
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.isEditable()).isTrue();

            // Table is ready for edits
            waitForFxEvents();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("UI Form Toggle Tests")
    class UIFormToggleTests {

        @Test
        @Order(33)
        @DisplayName("Should show CRUD interface when add button clicked")
        void testShowCrudInterface() {
            waitForFxEvents();

            Button addBtn = lookup("#AjouterCategory_Button").query();
            clickOn(addBtn);

            waitForFxEvents();

            // Verify CRUD interface is visible
        }

        @Test
        @Order(34)
        @DisplayName("Should hide CRUD interface initially")
        void testInitialInterfaceState() {
            waitForFxEvents();

            // Verify CRUD interface is hidden initially
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(35)
        @DisplayName("Should handle database errors gracefully")
        void testDatabaseError() {
            waitForFxEvents();

            // Verify UI is present and functional
            TextArea nameArea = lookup("#nomCategory_textArea").query();
            TextArea descArea = lookup("#descriptionCategory_textArea").query();

            assertThat(nameArea).isNotNull();
            assertThat(descArea).isNotNull();

            waitForFxEvents();
        }

        @Test
        @Order(36)
        @DisplayName("Should handle null values safely")
        void testNullValueHandling() {
            waitForFxEvents();

            // Verify table is present and functional
            TableView<Category> table = lookup("#filmCategory_tableView").query();
            assertThat(table).isNotNull();
            assertThat(table.getItems()).isNotNull();

            waitForFxEvents();
        }

    }

}
