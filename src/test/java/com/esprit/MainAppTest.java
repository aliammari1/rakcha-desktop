package com.esprit;

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

import com.esprit.utils.TestFXBase;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for MainApp.
 * Tests application startup, FXML loading, and initial UI state.
 * 
 * Test Categories:
 * - Application Launch
 * - Scene Initialization
 * - Login UI Components
 * - Window Properties
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Timeout(value = 15, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainAppTest extends TestFXBase {

    private Stage primaryStage;
    private MainApp app;

    @Start
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        app = new MainApp();
        app.start(stage);
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Application Launch Tests")
    class ApplicationLaunchTests {

        @Test
        @Order(1)
        @DisplayName("Should launch application successfully")
        void testApplicationLaunch() {
            waitForFxEvents();
            assertThat(primaryStage).isNotNull();
            assertThat(primaryStage.isShowing()).isTrue();
        }


        @Test
        @Order(2)
        @DisplayName("Should load login scene")
        void testLoginSceneLoaded() {
            waitForFxEvents();
            Scene scene = primaryStage.getScene();
            assertThat(scene).isNotNull();
            assertThat(scene.getRoot()).isNotNull();
        }


        @Test
        @Order(3)
        @DisplayName("Should set scene on primary stage")
        void testSceneSetOnStage() {
            waitForFxEvents();
            assertThat(primaryStage.getScene()).isNotNull();
        }


        @Test
        @Order(4)
        @DisplayName("Should display primary stage")
        void testStageVisible() {
            waitForFxEvents();
            assertThat(primaryStage.isShowing()).isTrue();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Login UI Components Tests")
    class LoginUIComponentsTests {

        @Test
        @Order(5)
        @DisplayName("Should display email field")
        void testEmailFieldPresent() {
            waitForFxEvents();
            TextField emailField = lookup("#tfEmail").query();
            assertThat(emailField).isNotNull();
            assertThat(emailField.isVisible()).isTrue();
        }


        @Test
        @Order(6)
        @DisplayName("Should display password field")
        void testPasswordFieldPresent() {
            waitForFxEvents();
            PasswordField passwordField = lookup("#tfPassword").query();
            assertThat(passwordField).isNotNull();
            assertThat(passwordField.isVisible()).isTrue();
        }


        @Test
        @Order(7)
        @DisplayName("Should display login button")
        void testLoginButtonPresent() {
            waitForFxEvents();
            Button loginButton = lookup("#btnSignIn").query();
            assertThat(loginButton).isNotNull();
            assertThat(loginButton.isVisible()).isTrue();
        }


        @Test
        @Order(8)
        @DisplayName("Should have empty fields initially")
        void testInitialFieldsEmpty() {
            waitForFxEvents();
            TextField emailField = lookup("#tfEmail").query();
            PasswordField passwordField = lookup("#tfPassword").query();
            
            assertThat(emailField.getText()).isEmpty();
            assertThat(passwordField.getText()).isEmpty();
        }


        @Test
        @Order(9)
        @DisplayName("Should enable login button initially")
        void testLoginButtonEnabled() {
            waitForFxEvents();
            Button loginButton = lookup("#btnSignIn").query();
            assertThat(loginButton.isDisabled()).isFalse();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("FXML Loading Tests")
    class FXMLLoadingTests {

        @Test
        @Order(10)
        @DisplayName("Should load Login.fxml without errors")
        void testFXMLLoading() {
            waitForFxEvents();
            assertThat(primaryStage.getScene()).isNotNull();
            assertThat(primaryStage.getScene().getRoot()).isNotNull();
        }


        @Test
        @Order(11)
        @DisplayName("Should have non-null scene root")
        void testSceneRootNotNull() {
            waitForFxEvents();
            assertThat(primaryStage.getScene().getRoot()).isNotNull();
        }


        @Test
        @Order(12)
        @DisplayName("Should load all UI components from FXML")
        void testAllComponentsLoaded() {
            waitForFxEvents();
            
            // Verify key components are loaded
            assertThat(lookup("#tfEmail").query()).isNotNull();
            assertThat(lookup("#tfPassword").query()).isNotNull();
            assertThat(lookup("#btnSignIn").query()).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Window Properties Tests")
    class WindowPropertiesTests {

        @Test
        @Order(13)
        @DisplayName("Should have valid scene dimensions")
        void testSceneDimensions() {
            waitForFxEvents();
            Scene scene = primaryStage.getScene();
            assertThat(scene.getWidth()).isGreaterThan(0);
            assertThat(scene.getHeight()).isGreaterThan(0);
        }


        @Test
        @Order(14)
        @DisplayName("Should show stage after start")
        void testStageShown() {
            waitForFxEvents();
            assertThat(primaryStage.isShowing()).isTrue();
        }


        @Test
        @Order(15)
        @DisplayName("Should have scene attached to stage")
        void testSceneAttached() {
            waitForFxEvents();
            assertThat(primaryStage.getScene()).isNotNull();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Scene Navigation Tests")
    class SceneNavigationTests {

        @Test
        @Order(16)
        @DisplayName("Should display signup link")
        void testSignupLinkPresent() {
            waitForFxEvents();
            // Signup link should be present for user registration
            assertThat(lookup(".hyperlink").queryAll()).isNotEmpty();
        }


        @Test
        @Order(17)
        @DisplayName("Should have proper scene hierarchy")
        void testSceneHierarchy() {
            waitForFxEvents();
            Scene scene = primaryStage.getScene();
            assertThat(scene.getRoot()).isNotNull();
            assertThat(scene.getRoot().getChildrenUnmodifiable()).isNotEmpty();
        }

    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Application State Tests")
    class ApplicationStateTests {

        @Test
        @Order(18)
        @DisplayName("Should initialize with default state")
        void testDefaultState() {
            waitForFxEvents();
            
            TextField emailField = lookup("#tfEmail").query();
            PasswordField passwordField = lookup("#tfPassword").query();
            
            assertThat(emailField.getText()).isEmpty();
            assertThat(passwordField.getText()).isEmpty();
        }


        @Test
        @Order(19)
        @DisplayName("Should have all required UI elements")
        void testRequiredElements() {
            waitForFxEvents();
            
            // Verify all essential login components exist
            assertThat(lookup("#tfEmail").query()).isNotNull();
            assertThat(lookup("#tfPassword").query()).isNotNull();
            assertThat(lookup("#btnSignIn").query()).isNotNull();
        }


        @Test
        @Order(20)
        @DisplayName("Should start with clean state")
        void testCleanStartState() {
            waitForFxEvents();
            
            TextField emailField = lookup("#tfEmail").query();
            PasswordField passwordField = lookup("#tfPassword").query();
            
            assertThat(emailField.getText()).isEmpty();
            assertThat(passwordField.getText()).isEmpty();
            assertThat(emailField.isDisabled()).isFalse();
            assertThat(passwordField.isDisabled()).isFalse();
        }

    }

}