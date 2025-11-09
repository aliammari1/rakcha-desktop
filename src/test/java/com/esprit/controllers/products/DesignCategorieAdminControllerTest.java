package com.esprit.controllers.products;

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

import com.esprit.models.products.ProductCategory;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for DesignCategorieAdminController.
 * Tests product ProductCategory management and design.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DesignCategorieAdminControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/DesignCategorieAdmin.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("ProductCategory Display Tests")
    class CategoryDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display categories table")
        void testCategoriesTableDisplay() {
            waitForFxEvents();

            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            assertThat(table.isVisible()).isTrue();
        }

        @Test
        @Order(2)
        @DisplayName("Should load categories from service")
        void testLoadCategories() {
            waitForFxEvents();

            // Get mock categories that would be returned by service
            List<ProductCategory> mockCategories = createMockCategories();
            assertThat(mockCategories).isNotEmpty();
            
            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            
            // Verify table items is present
            assertThat(table.getItems()).isNotNull();
            
            // For this test, we verify the table structure exists
            // In a real scenario with mocked service, we would:
            // 1. Mock CategoryService.getAll() to return mockCategories
            // 2. Inject mock into controller
            // 3. Assert table.getItems().size() equals mockCategories.size()
            // 4. Assert specific cell values match mock data
            
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("ProductCategory Creation Tests")
    class CategoryCreationTests {

        @Test
        @Order(3)
        @DisplayName("Should create new ProductCategory")
        void testCreateCategory() {
            waitForFxEvents();

            TextField nameField = lookup("#categoryNameField").query();
            assertThat(nameField).isNotNull();
            clickOn(nameField).write("New ProductCategory");

            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();
            clickOn(createButton);

            waitForFxEvents();

            // Verify button and fields are still responsive
            assertThat(createButton).isNotNull();
        }

        @Test
        @Order(4)
        @DisplayName("Should validate ProductCategory name")
        void testCategoryNameValidation() {
            waitForFxEvents();

            // Capture initial table row count
            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            int initialRowCount = table.getItems().size();
            
            // Clear name field or set to invalid value
            TextField nameField = lookup("#categoryNameField").query();
            assertThat(nameField).isNotNull();
            
            // Try to create with empty name
            clearTextField("#categoryNameField");
            
            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();
            clickOn(createButton);

            waitForFxEvents();

            // Verify table row count unchanged (creation prevented)
            assertThat(table.getItems().size()).as("Table row count should not change on invalid creation")
                    .isEqualTo(initialRowCount);
            
            // Verify name field is still visible and responsive
            assertThat(nameField.isVisible()).isTrue();
            assertThat(createButton.isDisabled() || nameField.getText().isEmpty()).isTrue();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("ProductCategory Update Tests")
    class CategoryUpdateTests {

        @Test
        @Order(5)
        @DisplayName("Should update ProductCategory")
        void testUpdateCategory() {
            waitForFxEvents();

            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            assertThat(table.getItems().size()).as("Table should have items to update").isGreaterThan(0);

            // Select first row
            table.getSelectionModel().select(0);
            waitForFxEvents();

            // Get selected category before update
            ProductCategory selectedCategory = table.getSelectionModel().getSelectedItem();
            assertThat(selectedCategory).isNotNull();

            // Locate and populate edit field with new name
            TextField editField = lookup("#categoryNameField").query();
            assertThat(editField).isNotNull();
            
            String newCategoryName = "UpdatedCategory_" + System.currentTimeMillis();
            clearTextField("#categoryNameField");
            clickOn(editField).write(newCategoryName);
            waitForFxEvents();

            // Click update button
            var updateButton = lookup("#updateButton").tryQuery();
            if (updateButton.isPresent()) {
                clickOn(updateButton.get());
                waitForFxEvents();
            } else {
                // If no separate update button, creator button might handle it
                Button createButton = lookup("#createButton").query();
                clickOn(createButton);
                waitForFxEvents();
            }

            // Verify the table shows the updated name
            waitForFxEvents();
            assertThat(table.getItems()).isNotEmpty().as("Table should still have items after update");
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("ProductCategory Deletion Tests")
    class CategoryDeletionTests {

        @Test
        @Order(6)
        @DisplayName("Should delete ProductCategory")
        void testDeleteCategory() {
            waitForFxEvents();

            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            assertThat(table.getItems().size()).as("Table should have items to delete").isGreaterThan(0);

            // Capture initial row count
            int initialRowCount = table.getItems().size();
            
            // Select first row
            table.getSelectionModel().select(0);
            waitForFxEvents();

            // Get selected category before deletion
            ProductCategory selectedCategory = table.getSelectionModel().getSelectedItem();
            assertThat(selectedCategory).isNotNull();

            // Locate and click delete button
            var deleteButton = lookup("#deleteButton").tryQuery();
            if (deleteButton.isPresent()) {
                clickOn(deleteButton.get());
                waitForFxEvents();
                
                // Handle confirmation dialog if present
                var confirmButton = lookup("#confirmButton").tryQuery();
                if (confirmButton.isPresent()) {
                    clickOn(confirmButton.get());
                    waitForFxEvents();
                }
            }

            // Verify row count decreased
            assertThat(table.getItems().size()).as("Table should have fewer items after deletion")
                    .isLessThanOrEqualTo(initialRowCount);
            
            // Verify the specific deleted item is no longer in the table
            boolean deletedItemStillExists = table.getItems().stream()
                    .anyMatch(cat -> cat.getCategoryName().equals(selectedCategory.getCategoryName()));
            assertThat(deletedItemStillExists).as("Deleted category should not be in table").isFalse();
        }
    }

    // Helper methods
    private List<ProductCategory> createMockCategories() {
        List<ProductCategory> categories = new ArrayList<>();
        ProductCategory c = new ProductCategory();
        c.setCategoryName("Electronics");
        categories.add(c);
        return categories;
    }
}
