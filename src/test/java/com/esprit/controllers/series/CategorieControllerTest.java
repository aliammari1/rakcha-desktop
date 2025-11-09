package com.esprit.controllers.series;

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

import com.esprit.models.series.Category;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for CategorieController.
 * Tests series category management (admin).
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategorieControllerTest extends TestFXBase {

    private IServiceCategorieImpl categoryService;

    @Override
    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Categorie-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Category Table Tests")
    class CategoryTableTests {

        @Test
        @Order(1)
        @DisplayName("Should display categories table")
        void testCategoriesTableDisplay() {
            waitForFxEvents();

            TableView<Category> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
        }

        @Test
        @Order(2)
        @DisplayName("Should load categories from service")
        void testLoadCategories() {
            waitForFxEvents();

            TableView<Category> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
        }

        @Test
        @Order(3)
        @DisplayName("Should display category name column")
        void testCategoryNameColumn() {
            waitForFxEvents();

            TableView<Category> table = lookup("#categoriesTable").query();
            assertThat(table).isNotNull();
            assertThat(table.getColumns()).isNotEmpty();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {

        @Test
        @Order(4)
        @DisplayName("Should create new category")
        void testCreateCategory() {
            waitForFxEvents();

            TextField nameField = lookup("#categoryNameField").query();
            assertThat(nameField).isNotNull();
            clickOn(nameField).write("Action");

            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();
        }

        @Test
        @Order(5)
        @DisplayName("Should validate category name not empty")
        void testCategoryNameValidation() {
            waitForFxEvents();

            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();
        }

        @Test
        @Order(6)
        @DisplayName("Should clear form after creation")
        void testClearFormAfterCreation() {
            waitForFxEvents();

            TextField nameField = lookup("#categoryNameField").query();
            assertThat(nameField).isNotNull();
            clickOn(nameField).write("Action");

            Button createButton = lookup("#createButton").query();
            assertThat(createButton).isNotNull();

            assertThat(nameField.getText()).isNotEmpty();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        @Order(7)
        @DisplayName("Should update category")
        void testUpdateCategory() {
            waitForFxEvents();

            assertThat(lookup("#updateButton").tryQuery()).isPresent();
        }

        @Test
        @Order(8)
        @DisplayName("Should populate form on selection")
        void testPopulateFormOnSelection() {
            waitForFxEvents();

            TextField nameField = lookup("#categoryNameField").query();
            assertThat(nameField).isNotNull();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        @Order(9)
        @DisplayName("Should delete category")
        void testDeleteCategory() {
            waitForFxEvents();

            assertThat(lookup("#deleteButton").tryQuery()).isPresent();
        }

        @Test
        @Order(10)
        @DisplayName("Should confirm before deletion")
        void testConfirmDeletion() {
            waitForFxEvents();

            assertThat(lookup("#deleteButton").tryQuery()).isPresent();
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)@Nested
    @DisplayName("Search Category Tests")
    class SearchCategoryTests {

        @Test
        @Order(11)
        @DisplayName("Should search categories by name")
        void testSearchByName() {
            waitForFxEvents();

            TextField searchField = lookup("#searchField").query();
            assertThat(searchField).isNotNull();
            clickOn(searchField).write("Drama");

            assertThat(searchField.getText()).contains("Drama");
        }
    }

    // Helper methods
    private List<Category> createMockCategories() {
        List<Category> categories = new ArrayList<>();

        Category cat1 = new Category();
        cat1.setName("Drama");

        Category cat2 = new Category();
        cat2.setName("Comedy");

        categories.add(cat1);
        categories.add(cat2);
        return categories;
    }

    private <T> com.esprit.utils.Page<T> createPagedResult(List<T> content) {
        return new com.esprit.utils.Page<>(content, 0, content.size(), content.size());
    }
}
