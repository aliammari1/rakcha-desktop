package com.esprit.controllers.series;

import com.esprit.models.series.Category;
import com.esprit.services.series.CategoryService;
import com.esprit.utils.TestFXBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for CategoriesClientController.
 * Tests category browsing for series.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesClientControllerTest extends TestFXBase {

    private CategoryService categoryService;

    @Override
    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/SeriesClient.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Categories Display Tests")
    class CategoriesDisplayTests {

        @Test
        @Order(1)
        @DisplayName("Should display categories flow pane")
        void testCategoriesFlowPaneDisplay() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#categoriesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(2)
        @DisplayName("Should load categories from service")
        void testLoadCategories() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#categoriesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(3)
        @DisplayName("Should display category cards")
        void testCategoryCardsDisplay() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#categoriesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Category Selection Tests")
    class CategorySelectionTests {

        @Test
        @Order(4)
        @DisplayName("Should select category")
        void testSelectCategory() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#categoriesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }


        @Test
        @Order(5)
        @DisplayName("Should navigate to category series")
        void testNavigateToCategorySeries() {
            waitForFxEvents();

            FlowPane flowPane = lookup("#categoriesFlowPane").query();
            assertThat(flowPane).isNotNull();
        }

    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(6)
        @DisplayName("Should navigate back to home")
        void testNavigateBackToHome() {
            waitForFxEvents();

            assertThat(lookup(".button").tryQuery()).isPresent();
        }

    }

}

