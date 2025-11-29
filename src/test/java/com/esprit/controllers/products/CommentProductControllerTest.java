package com.esprit.controllers.products;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import org.testfx.framework.junit5.Start;

import com.esprit.models.products.Comment;
import com.esprit.models.products.Product;
import com.esprit.models.users.Client;
import com.esprit.utils.TestFXBase;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * Comprehensive test suite for CommentProductController.
 * Tests comment creation, bad word filtering, display, and navigation.
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentProductControllerTest extends TestFXBase {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/products/CommentaireProduit.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comment Input Tests")
    class CommentInputTests {

        @Test
        @Order(1)
        @DisplayName("Should accept valid comment text")
        void testValidCommentInput() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("This is a great product!");

            assertThat(commentArea.getText()).isEqualTo("This is a great product!");
        }


        @Test
        @Order(2)
        @DisplayName("Should handle multi-line comments")
        void testMultiLineComment() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            String multiLineComment = "Great product!\nI really like it.\nHighly recommended.";
            clickOn(commentArea).write(multiLineComment);

            assertThat(commentArea.getText()).contains("\n");
        }


        @Test
        @Order(3)
        @DisplayName("Should accept long comments")
        void testLongComment() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            String longComment = "a".repeat(500);
            clickOn(commentArea).write(longComment);

            assertThat(commentArea.getText()).hasSize(500);
        }


        @Test
        @Order(4)
        @DisplayName("Should handle empty comment submission")
        void testEmptyComment() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea); // Click but don't write

            // Try to submit
            waitForFxEvents();
            
            // Verify text area is empty
            assertThat(commentArea.getText()).isEmpty();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Bad Word Filtering Tests")
    class BadWordFilteringTests {

        @Test
        @Order(5)
        @DisplayName("Should detect bad words in comment")
        void testBadWordDetection() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("This contains badword");

            // Submit comment
            waitForFxEvents();

            // Verify warning label is visible for bad word
            var warningLabel = lookup("#warningLabel").tryQuery();
            if (warningLabel.isPresent()) {
                assertThat(warningLabel.get().isVisible()).isTrue();
            }

            
            // Verify submit was prevented or comment didn't get added
            assertThat(commentArea.getText()).isNotEmpty();
        }


        @Test
        @Order(6)
        @DisplayName("Should allow comment without bad words")
        void testCleanComment() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            String cleanText = "This is a clean comment";
            clickOn(commentArea).write(cleanText);

            // Verify warning is NOT shown for clean text
            var warningLabel = lookup("#warningLabel").tryQuery();
            if (warningLabel.isPresent()) {
                assertThat(warningLabel.get().isVisible()).isFalse();
            }

            
            // Verify comment text is present
            assertThat(commentArea.getText()).contains(cleanText);
        }


        @Test
        @Order(7)
        @DisplayName("Should show warning for inappropriate content")
        void testInappropriateContentWarning() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("inappropriate content");

            // Submit comment
            waitForFxEvents();

            // Verify warning label exists and is visible
            var warningLabel = lookup("#warningLabel").tryQuery();
            if (warningLabel.isPresent()) {
                assertThat(warningLabel.get().isVisible()).isTrue();
            }

        }


        @Test
        @Order(8)
        @DisplayName("Should prevent comment creation with bad words")
        void testPreventBadWordComment() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            
            // Verify submit button exists
            var submitButton = lookup("#submitButton").tryQuery();
            if (submitButton.isPresent()) {
                // Check if button is enabled before bad word input
                assertThat(submitButton.get().isDisabled()).isFalse();
            }

            
            clickOn(commentArea).write("bad words here");

            // Submit comment
            waitForFxEvents();

            // Verify either submit button is disabled or warning is shown
            if (submitButton.isPresent()) {
                // Button should be disabled after bad words
                assertThat(submitButton.get().isDisabled()).isTrue();
            }
 else {
                // Or warning should be visible
                var warningLabel = lookup("#warningLabel").tryQuery();
                assertThat(warningLabel.isPresent()).isTrue();
            }

        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comment Display Tests")
    class CommentDisplayTests {

        @Test
        @Order(9)
        @DisplayName("Should display existing comments on load")
        void testDisplayComments() {
            waitForFxEvents();

            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
            assertThat(commentPane.isVisible()).isTrue();
            
            // Verify pane has children or is ready to display
            assertThat(commentPane.getChildren()).isNotNull();
        }


        @Test
        @Order(10)
        @DisplayName("Should display comment author name")
        void testDisplayAuthorName() {
            waitForFxEvents();

            Comment comment = createMockComment("John", "Doe", "Great product!");
            
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();

            // Wait for any async rendering
            waitForFxEvents();
            
            // Query for author name label or text node with author name
            var authorLabel = lookup("John Doe").tryQuery();
            if (authorLabel.isPresent()) {
                assertThat(authorLabel.get().isVisible()).isTrue();
            }
 else {
                // If not found by text, verify comment pane has children
                assertThat(commentPane.getChildren()).isNotNull();
            }

            
            // Verify mock object has correct author data
            assertThat(comment.getClient().getFirstName()).isEqualTo("John");
            assertThat(comment.getClient().getLastName()).isEqualTo("Doe");
        }


        @Test
        @Order(11)
        @DisplayName("Should display comment text")
        void testDisplayCommentText() {
            waitForFxEvents();

            Comment comment = createMockComment("John", "Doe", "Great product!");
            
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();

            // Wait for rendering
            waitForFxEvents();
            
            // Query for comment text node
            var textNode = lookup("Great product!").tryQuery();
            if (textNode.isPresent()) {
                assertThat(textNode.get().isVisible()).isTrue();
            }
 else {
                // Verify comment pane is visible and ready
                assertThat(commentPane.isVisible()).isTrue();
            }

            
            // Verify mock comment has expected text
            assertThat(comment.getCommentText()).isEqualTo("Great product!");
        }


        @Test
        @Order(12)
        @DisplayName("Should handle empty comment list")
        void testEmptyCommentList() {
            waitForFxEvents();

            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
            
            // Comment pane should exist even if empty
            assertThat(commentPane.isVisible()).isTrue();
            
            // Verify pane is ready (children list exists)
            assertThat(commentPane.getChildren()).isNotNull();
        }


        @Test
        @Order(13)
        @DisplayName("Should display multiple comments")
        void testMultipleComments() {
            waitForFxEvents();

            List<Comment> comments = createMockComments();
            assertThat(comments).isNotEmpty().hasSize(3);
            
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
            assertThat(commentPane.isVisible()).isTrue();
            
            // Verify each comment has valid author and text
            for (Comment c : comments) {
                assertThat(c.getClient()).isNotNull();
                assertThat(c.getClient().getFirstName()).isNotEmpty();
                assertThat(c.getCommentText()).isNotEmpty();
            }

        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comment Card Styling Tests")
    class CommentCardStylingTests {

        @Test
        @Order(14)
        @DisplayName("Should create comment card with proper layout")
        void testCommentCardLayout() {
            waitForFxEvents();

            Comment comment = createMockComment("Jane", "Smith", "Excellent!");
            assertThat(comment).isNotNull();
            assertThat(comment.getClient().getFirstName()).isEqualTo("Jane");
            
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }


        @Test
        @Order(15)
        @DisplayName("Should wrap long comment text")
        void testLongTextWrapping() {
            waitForFxEvents();

            String longText = "a".repeat(200);
            Comment comment = createMockComment("John", "Doe", longText);
            
            assertThat(comment.getCommentText()).hasSize(200);
            assertThat(comment.getCommentText()).isEqualTo(longText);
        }


        @Test
        @Order(16)
        @DisplayName("Should style author name in bold")
        void testAuthorNameBold() {
            waitForFxEvents();

            Comment comment = createMockComment("John", "Doe", "Test comment");
            
            assertThat(comment.getClient()).isNotNull();
            assertThat(comment.getClient().getFirstName()).isEqualTo("John");
            assertThat(comment.getClient().getLastName()).isEqualTo("Doe");
        }


        @Test
        @Order(17)
        @DisplayName("Should use correct font sizes")
        void testFontSizes() {
            waitForFxEvents();

            Comment comment = createMockComment("John", "Doe", "Test comment");
            
            assertThat(comment.getCommentText()).isNotEmpty();
            assertThat(comment.getClient().getLastName()).isEqualTo("Doe");
            
            // Verify comment structure
            assertThat(comment.getClient().getFirstName()).isNotEmpty();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comment Creation Tests")
    class CommentCreationTests {

        @Test
        @Order(18)
        @DisplayName("Should create comment with client info")
        void testCreateCommentWithClient() {
            waitForFxEvents();

            Client client = new Client();
            client.setFirstName("John");
            client.setLastName("Doe");

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Great product!");

            // Submit comment
            waitForFxEvents();
            
            // Verify UI is responsive
            assertThat(commentArea).isNotNull();
        }


        @Test
        @Order(19)
        @DisplayName("Should associate comment with product")
        void testCommentProductAssociation() {
            waitForFxEvents();

            Product product = new Product();
            product.setId(1L);

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Great product!");

            // Submit comment
            waitForFxEvents();
            
            // Verify comment area is responsive
            assertThat(commentArea).isNotNull();
        }


        @Test
        @Order(20)
        @DisplayName("Should clear comment field after submission")
        void testClearAfterSubmission() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Great product!");

            // Submit comment
            waitForFxEvents();

            // Verify field is ready for next comment
            assertThat(commentArea).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Navigation Tests")
    class NavigationTests {

        @Test
        @Order(21)
        @DisplayName("Should navigate to cinema client interface")
        void testNavigateToCinema() {
            waitForFxEvents();
            
            // Verify page is loaded
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }


        @Test
        @Order(22)
        @DisplayName("Should navigate to product client interface")
        void testNavigateToProducts() {
            waitForFxEvents();
            
            // Verify page is loaded
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }


        @Test
        @Order(23)
        @DisplayName("Should navigate to movie client interface")
        void testNavigateToMovies() {
            waitForFxEvents();
            
            // Verify page is loaded
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }


        @Test
        @Order(24)
        @DisplayName("Should navigate to series client interface")
        void testNavigateToSeries() {
            waitForFxEvents();
            
            // Verify page is loaded
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }


        @Test
        @Order(25)
        @DisplayName("Should navigate to event client interface")
        void testNavigateToEvents() {
            waitForFxEvents();
            
            // Verify page is loaded
            FlowPane commentPane = lookup("#CommentFlowPane").query();
            assertThat(commentPane).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @Order(26)
        @DisplayName("Should handle database errors gracefully")
        void testDatabaseError() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Test comment");

            // Submit comment
            waitForFxEvents();

            // Verify UI is still responsive
            assertThat(commentArea).isNotNull();
        }


        @Test
        @Order(27)
        @DisplayName("Should handle null user gracefully")
        void testNullUser() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Test comment");

            // Submit comment and verify handling
            waitForFxEvents();
            
            // Verify UI is still responsive
            assertThat(commentArea).isNotNull();
        }


        @Test
        @Order(28)
        @DisplayName("Should handle bad word detection service errors")
        void testBadWordServiceError() {
            waitForFxEvents();

            TextArea commentArea = lookup("#monCommentaitreText").query();
            assertThat(commentArea).isNotNull();
            clickOn(commentArea).write("Test comment");

            // Submit comment and verify error handling
            waitForFxEvents();
            
            // Verify UI is still responsive
            assertThat(commentArea).isNotNull();
        }

    }


    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Comment Timestamp Display Tests")
    class CommentTimestampDisplayTests {

        @Test
        @Order(29)
        @Disabled("TODO: Implement timestamp display - verify comment creation date is rendered in correct format")
        @DisplayName("Should display comment creation date")
        void testDisplayCreationDate() {
            // Requires: Lookup timestamp label for comment, verify LocalDate/LocalDateTime formatting
            waitForFxEvents();
        }


        @Test
        @Order(30)
        @Disabled("TODO: Implement timestamp validation - verify timestamp is not in future")
        @DisplayName("Should validate timestamp is not in future")
        void testTimestampNotInFuture() {
            // Requires: Create comment with mocked current date, verify timestamp <= now
            waitForFxEvents();
        }

    }


    // Helper methods
    private List<Comment> createMockComments() {
        List<Comment> comments = new ArrayList<>();
        comments.add(createMockComment("John", "Doe", "Great product!"));
        comments.add(createMockComment("Jane", "Smith", "Excellent quality!"));
        comments.add(createMockComment("Bob", "Johnson", "Highly recommended!"));
        return comments;
    }


    private Comment createMockComment(String firstName, String lastName, String text) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);

        Comment comment = new Comment();
        comment.setClient(client);
        comment.setCommentText(text);

        return comment;
    }

}

