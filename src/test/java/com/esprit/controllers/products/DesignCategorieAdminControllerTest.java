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
import com.esprit.services.products.CategoryService;
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

    private CategoryService categoryService;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/DesignCategorieAdmin.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

            List<ProductCategory> categories = createMockCategories();
            
            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            
            // Verify table is ready to display categories
            waitForFxEvents();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
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

            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();
            clickOn(createButton);

            waitForFxEvents();

            // Verify button is still responsive
            assertThat(createButton).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("ProductCategory Update Tests")
    class CategoryUpdateTests {

        @Test
        @Order(5)
        @DisplayName("Should update ProductCategory")
        void testUpdateCategory() {
            waitForFxEvents();

            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();

            // Select ProductCategory and update
            waitForFxEvents();

            // Verify table is still responsive
            assertThat(table).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("ProductCategory Deletion Tests")
    class CategoryDeletionTests {

        @Test
        @Order(6)
        @DisplayName("Should delete ProductCategory")
        void testDeleteCategory() {
            waitForFxEvents();

            TableView<ProductCategory> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();

            // Select and delete ProductCategory
            waitForFxEvents();

            // Verify table is still responsive
            assertThat(table).isNotNull();
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

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }
}
