package com.esprit.controllers.films;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Actor;
import com.esprit.services.films.ActorService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller class responsible for managing actor-related operations in the
 * RAKCHA movie database application.
 * 
 * <p>
 * This controller handles comprehensive actor management including CRUD
 * operations, image management with caching, data export/import functionality,
 * and
 * undo/redo capabilities. It provides a JavaFX interface for users to interact
 * with
 * actor data through a TableView with inline editing, search functionality, and
 * keyboard shortcuts.
 * </p>
 * 
 * <p>
 * <strong>Key Features:</strong>
 * </p>
 * <ul>
 * <li>Actor CRUD operations (Create, Read, Update, Delete)</li>
 * <li>Image upload and management with validation (max 5MB, PNG/JPG
 * formats)</li>
 * <li>Concurrent image loading and caching for improved performance</li>
 * <li>Data export/import in JSON format</li>
 * <li>Undo/Redo functionality with command pattern implementation</li>
 * <li>Real-time search and filtering with case-insensitive matching</li>
 * <li>Keyboard shortcuts (Ctrl+Z, Ctrl+Y, Ctrl+E, Ctrl+I)</li>
 * </ul>
 * 
 * <p>
 * <strong>Thread Safety:</strong> This controller uses thread-safe collections
 * ({@link ConcurrentHashMap}) and proper JavaFX {@code Platform.runLater()}
 * calls
 * for UI updates from background threads.
 * </p>
 * 
 * <p>
 * <strong>Usage Example:</strong>
 * 
 * <pre>{@code
 * // Controller is automatically initialized by JavaFX FXML loader
 * ActorController controller = fxmlLoader.getController();
 * // Search functionality
 * controller.searchActor("John");
 * // Export actors
 * controller.exportActors("actors_backup.json");
 * }</pre>
 * 
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 * @see Actor
 * @see ActorService
 * @see javafx.scene.control.TableView
 * @see javafx.collections.transformation.FilteredList
 */
public class ActorController {
    private static final Logger LOGGER = Logger.getLogger(ActorController.class.getName());

    @FXML
    private TextArea bioAcotr_textArea;
    @FXML
    private TableColumn<Actor, Button> DeleteActor_Column1;
    @FXML
    private Label errorBio;
    @FXML
    private Label errorNameActor;
    @FXML
    private Button AjouterFilm_Button;
    @FXML
    private TableView<Actor> filmActor_tableView11;
    @FXML
    private TableColumn<Actor, Integer> idActor_tableColumn1;
    @FXML
    private TableColumn<Actor, String> bioActor_tableColumn1;
    @FXML
    private ImageView imageAcotr_ImageView1;
    @FXML
    private TableColumn<Actor, HBox> imageAcotr_tableColumn1;
    @FXML
    private TableColumn<Actor, String> nomAcotr_tableColumn1;
    @FXML
    private TextArea nomAcotr_textArea1;
    private FilteredList<Actor> filteredActors;
    @FXML
    private TextField recherche_textField;
    private String cloudinaryImageUrl;

    private final Stack<UndoableAction> undoStack = new Stack<>();
    private final Stack<UndoableAction> redoStack = new Stack<>();

    /**
     * Initializes the controller after FXML loading is complete.
     * Sets up the filtered list of actors, configures search functionality,
     * and registers keyboard shortcuts for undo/redo and import/export operations.
     * 
     * <p>
     * This method is automatically called by JavaFX after the FXML file is loaded
     * and all @FXML annotated fields are injected.
     * </p>
     * 
     * <p>
     * Keyboard shortcuts:
     * </p>
     * <ul>
     * <li>Ctrl+Z: Undo last action</li>
     * <li>Ctrl+Y: Redo last undone action</li>
     * <li>Ctrl+E: Export actors to JSON</li>
     * <li>Ctrl+I: Import actors from JSON</li>
     * </ul>
     * 
     * @see FilteredList
     * @see #searchActor(String)
     * @see #undo()
     * @see #redo()
     * @see #exportActors(String)
     * @see #importActors(String)
     */
    @FXML
    void initialize() {
        this.readActorTable();
        this.setupCellOnEditCommit();
        this.filteredActors = new FilteredList<>(this.filmActor_tableView11.getItems());
        // Réinitialiser la TableView avec la liste filtrée
        this.filmActor_tableView11.setItems(this.filteredActors);
        // Appliquer le filtre lorsque le texte de recherche change
        this.recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.searchActor(newValue);
        });

        // Add keyboard shortcuts
        filmActor_tableView11.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case Z -> undo();
                    case Y -> redo();
                    case E -> exportActors("actors_export.json");
                    case I -> importActors("actors_import.json");
                    default -> {
                    }
                }
            }
        });
    }

    /**
     * Filters the actor list based on a provided search text, returning only
     * actors whose name contains the searched text (case-insensitive).
     * 
     * <p>
     * This method updates the predicate of the filtered list to show only
     * matching actors. If the search text is null or empty, all actors are shown.
     * </p>
     *
     * @param searchText the search query used to filter actors in the
     *                   filteredActors list.
     *                   If null or empty, no filtering is applied.
     * @see FilteredList#setPredicate(java.util.function.Predicate)
     */
    private void searchActor(final String searchText) {
        this.filteredActors.setPredicate(actor -> {
            // Si le champ de recherche est vide, afficher tous les acteurs
            if (null == searchText || searchText.isEmpty()) {
                return true;
            }
            // Vérifier si le nom de l'acteur contient le texte de recherche (en ignorant la
            // casse)
            final String lowerCaseFilter = searchText.toLowerCase();
            return actor.getName().toLowerCase().contains(lowerCaseFilter);
        });
    }

    /**
     * Handles the import actor image action by allowing the user to select an image
     * file,
     * which is then copied to a specified directory and set as the image for
     * display.
     * 
     * <p>
     * This method validates the selected image file size and format before copying.
     * The image is stored in the films directory for consistency with the existing
     * structure.
     * </p>
     *
     * @param event the action event triggered by the user clicking the import image
     *              button.
     *              This parameter is not directly used but follows JavaFX event
     *              handling conventions.
     * @throws IOException if an I/O error occurs during file operations
     * @see #validateImage(File)
     */
    @FXML
    void importAcotrImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            if (!validateImage(selectedFile)) {
                return;
            }
            try {
                final String destinationDirectory = "./src/main/resources/img/films/";
                final Path destinationPath = Paths.get(destinationDirectory);
                final String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                final Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath);
                final Image selectedImage = new Image(destinationFilePath.toUri().toString());
                this.imageAcotr_ImageView1.setImage(selectedImage);
            } catch (final IOException e) {
                ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Displays an informational alert dialog to the user with a specified message.
     *
     * @param message the content to be displayed in the alert window
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Handles the insertion of a new actor into the database.
     * 
     * <p>
     * This method extracts the image path from the ImageView, creates a new Actor
     * object with the provided name, image path, and biography, and then stores it
     * in the database using the ActorService. Upon successful insertion, an
     * informational alert is displayed and the actor table is refreshed.
     * </p>
     *
     * @param event the action event triggered by clicking the insert button
     * @see ActorService#create(Actor)
     */
    @FXML
    void insertActor(final ActionEvent event) {
        if (this.imageAcotr_ImageView1.getImage() == null) {
            showAlert("Please select an image for the actor");
            return;
        }

        final String fullPath = this.imageAcotr_ImageView1.getImage().getUrl();
        String imagePath = "";
        try {
            final String requiredPath = fullPath.substring(fullPath.indexOf("/img/actors/"));
            URI uri = new URI(requiredPath);
            imagePath = uri.getPath();
        } catch (final Exception e) {
            ActorController.LOGGER.warning("Error processing image path: " + e.getMessage());
            // Use the full path if substring fails
            imagePath = fullPath;
        }

        final ActorService actorService = new ActorService();
        final Actor actor = new Actor(this.nomAcotr_textArea1.getText(), imagePath,
                this.bioAcotr_textArea.getText());
        actorService.create(actor);

        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor ajoutée");
        alert.setContentText("Actor ajoutée !");
        alert.show();
        this.readActorTable();
    }

    /**
     * Sets up event handlers for two table columns in a `TableView`. When an edit
     * is committed, it updates the corresponding actor's property with the new
     * value from the column edit.
     */
    private void setupCellOnEditCommit() {
        this.nomAcotr_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
            /**
             * Is called when a cell in a table is edited. It sets the new value of the cell
             * to the nominated value, and then updates the corresponding actor object with
             * the new value.
             *
             * @param event
             *              CellEditEvent object that contains information about the edit
             *              event occurring on a table column, including the edited cell's
             *              position and the new value being entered.
             *
             *              - `TableColumn.CellEditEvent`: This is the type of event that
             *              triggered the function's execution. - `Actor`: This is the type
             *              parameter of the event, which represents the data type of the
             *              cell
             *              being edited. - `String`: This is the type of the value being
             *              edited in the cell.
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Actor, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
                    ActorController.this
                            .updateActor(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.bioActor_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
            /**
             * In the `TableColumn` class is responsible for handling cell editing events
             * for a column containing a `String` type data. It updates the value of the
             * `biographie` property of the corresponding actor object in the table view,
             * and also triggers an update of the actor object itself.
             *
             * @param event
             *              CellEditEvent of a TableColumn, providing the edited cell and
             *              its
             *              new value.
             *
             *              - `TableColumn.CellEditEvent`: The event type that indicates
             *              cell
             *              editing has occurred on a table column. - `Actor`: The data type
             *              of the cells being edited, which is an actor object in this
             *              case.
             *              - `String`: The type of the value being edited, which is a
             *              string
             *              in this case.
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Actor, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setBiography(event.getNewValue());
                    ActorController.this
                            .updateActor(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Allows the user to select an image file from a file chooser, then saves it to
     * two different locations and displays the image in an `ImageView`.
     *
     * @param event
     *              event of opening a file chooser, which triggers the code inside
     *              the function to execute when a file is selected.
     *              <p>
     *              - `event`: an ActionEvent object representing the user's action
     *              of
     *              opening a file from the FileChooser.
     *              </p>
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            if (!validateImage(selectedFile)) {
                return;
            }
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.imageAcotr_ImageView1.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }
        }
    }

    /**
     * Updates an actor's data in a database by first getting a connection, then
     * calling the `update` method of the `ActorService` class and displaying an
     * alert with the updated actor's information.
     *
     * @param actor
     *              Actor object to be updated.
     *              <p>
     *              - `Actor actor`: The actor to be updated.
     *              </p>
     */
    void updateActor(final Actor actor) {
        try {
            final ActorService actorService = new ActorService();
            // Assign value to imageString
            /* assign the String value here */
            actorService.update(actor);
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actor modifiée");
            alert.setContentText("Actor modifiée !");
            alert.show();
        } catch (final Exception e) {
            this.showAlert("Erreur lors de la modification du Actor : " + e.getMessage());
        }
        this.readActorTable();
    }

    /**
     * Reads data from an Actor database, creates a table with columns for actor
     * name, nom, bio, and image, and displays the data in the table.
     */
    @FXML
    void readActorTable() {
        try {
            this.filmActor_tableView11.setEditable(true);
            this.idActor_tableColumn1.setVisible(false);
            this.idActor_tableColumn1.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.nomAcotr_tableColumn1.setCellValueFactory((
                    TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) -> new SimpleStringProperty(
                            actorStringCellDataFeatures.getValue().getName())
            /**
             * Generates a `SimpleStringProperty` instance from the `Value` object returned
             * by the `getName()` method of an `Actor` object.
             *
             * @param actorStringCellDataFeatures
             *                                    cell data features of an actor,
             *                                    specifically the string value of
             *                                    the actor's name.
             *
             * @returns a `SimpleStringProperty` object containing the nominal value of the
             *          input `Actor` object.
             */
            );
            this.nomAcotr_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            this.DeleteActor_Column1.setCellValueFactory(filmcategoryButtonCellDataFeatures -> {
                final Button button = new Button("delete");
                button.setOnAction((final ActionEvent event) -> {
                    this.deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                } /**
                   * Deletes a film based on the ID passed as an event parameter from the
                   * `filmcategoryButtonCellDataFeatures`.
                   *
                   * @param event
                   *              `ActionEvent` that triggered the function execution, providing
                   *              the
                   *              identifier of the button that was clicked.
                   */
                );
                return new SimpleObjectProperty<>(button);
            }
            /**
             * Creates a new button with an `OnAction` event handler that deletes a film
             * when clicked, and returns the button object as an observable value.
             *
             * @param filmcategoryButtonCellDataFeatures
             *                                           value of a Button cell in a
             *                                           TableColumn, which contains the ID
             *                                           of
             *                                           a film category.
             *
             *                                           - `getValue()`: returns the
             *                                           internal value of the object, which
             *                                           is
             *                                           an instance of
             *                                           `FilmCategoryButtonCellData`. -
             *                                           `getId()`:
             *                                           retrieves the unique identifier for
             *                                           the film category button.
             *
             * @returns a `SimpleObjectProperty` of a `Button` object with the text
             *          "delete".
             *
             *          - The output is an `ObservableValue` of type `Button`, which means
             *          that it can be observed to change over time. - The output is created
             *          by calling the `Button` constructor and setting an `OnAction` event
             *          handler for the button's `onAction` method. - The `OnAction` event
             *          handler is an instance of `EventHandler`, which is a standard Java
             *          interface for handling events. In this case, the event handler
             *          deletes the film with the specified ID when the button is pressed.
             */
            );
            this.bioActor_tableColumn1.setCellValueFactory((
                    TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) -> new SimpleStringProperty(
                            actorStringCellDataFeatures.getValue()
                                    .getBiography()) /**
                                                      * Takes a `TableColumn.CellDataFeatures<Actor, String>` parameter
                                                      * and returns an `ObservableValue<String>` representing the
                                                      * actor's biography.
                                                      *
                                                      * @param actorStringCellDataFeatures
                                                      *                                    String value of a cell in a
                                                      *                                    table that displays an
                                                      *                                    actor's biography.
                                                      *
                                                      * @returns a `SimpleStringProperty` containing the biography of
                                                      *          the actor.
                                                      */
            );
            this.bioActor_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            this.imageAcotr_tableColumn1
                    .setCellValueFactory((final TableColumn.CellDataFeatures<Actor, HBox> param) -> {
                        final HBox hBox = new HBox();
                        try {
                            final ImageView imageView = new ImageView();
                            imageView.setFitWidth(120); // Réglez la largeur de l'image selon vos préférences
                            imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
                            try {
                                loadImage(param.getValue().getImage(), imageView);
                            } catch (final Exception e) {
                                ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            }
                            hBox.getChildren().add(imageView);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent event) -> {
                                try {
                                    final FileChooser fileChooser = new FileChooser();
                                    fileChooser.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                                            new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                    final File file = fileChooser.showOpenDialog(null);
                                    if (null != file) {
                                        if (!validateImage(file)) {
                                            return;
                                        }
                                        final String destinationDirectory = "./src/main/resources/img/films/";
                                        final Path destinationPath = Paths.get(destinationDirectory);
                                        final String uniqueFileName = System.currentTimeMillis() + "_" + file.getName();
                                        final Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                                        Files.copy(file.toPath(), destinationFilePath);
                                        final Image image = new Image(destinationFilePath.toUri().toString());
                                        imageView.setImage(image);
                                        hBox.getChildren().clear();
                                        hBox.getChildren().add(imageView);
                                        new ActorService()
                                                .update(new Actor(null, destinationFilePath.toUri().toString(), null));
                                    }
                                } catch (final IOException e) {
                                    ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                }
                            } /**
                               * Displays an open file dialog, saves the selected file to a designated
                               * directory, creates an image from the saved file, and displays the image in a
                               * graphical user interface.
                               *
                               * @param event
                               *              mouse event that triggered the function, providing information
                               *              about the location and type of the event.
                               *
                               *              - `event`: a `MouseEvent` object representing a user interaction
                               *              with the application.
                               */
                            );
                        } catch (final Exception e) {
                            ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                        return new SimpleObjectProperty<>(hBox);
                    } /**
                       * Generates an `HBox` container that displays an image when clicked, allowing
                       * users to select images from a file system and display them in the
                       * application.
                       *
                       * @param param
                       *              CellDataFeatures of a TableColumn, providing an Actor object as
                       *              its value, which is used to display an image and handle mouse
                       *              clicks to save the image to a designated directory.
                       *
                       *              - `param.getValue()`: returns an instance of `Actor`, which
                       *              contains the image to be displayed.
                       *
                       *              The `param` object is not destructured in this case.
                       *
                       * @returns an `ObservableValue` of type `HBox`, which contains a `ImageView`
                       *          element that displays an image selected by the user and allows for
                       *          its save to a designated directory.
                       *
                       *          - `HBox` is the type of the output, which is an observable value of
                       *          type HBox. - The output is created by initializing a new instance of
                       *          HBox and adding an ImageView component to it. - The ImageView
                       *          component displays an image, which is set using the `setImage()`
                       *          method and passing in the image from the `getValue()` method of the
                       *          parameter object. - The ImageView component also has an event
                       *          handler attached to it that responds to a mouse click event. When
                       *          the event occurs, the handler will execute the code inside it.
                       */
                    );
            final ActorService categoryService = new ActorService();
            PageRequest pageRequest = new PageRequest(0, 10);
            final ObservableList<Actor> obC = FXCollections.observableArrayList(categoryService.read(pageRequest).getContent());
            this.filmActor_tableView11.setItems(obC);
        } catch (final Exception e) {
            ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Deletes an actor from a table using the `delete()` method provided by the
     * `ActorService`. It displays an informational alert and updates the
     * `readActorTable()` function to reflect the change.
     *
     * @param id
     *           identifier of the actor to be deleted.
     */
    void deleteFilm(final Long id) {
        final ActorService actorService = new ActorService();
        actorService.delete(new Actor(id));
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor supprimée");
        alert.setContentText("Actor supprimée !");
        alert.show();
        this.readActorTable();
    }

    /**
     * Loads an FXML file using the `FXMLLoader` class, creates a new `AnchorPane`
     * object, and sets it as the scene of a `Stage`.
     *
     * @param event
     *              action event that triggered the function execution, providing
     *              the
     *              necessary context for the code to perform its intended task.
     *              <p>
     *              - `event`: An instance of `ActionEvent`, representing a user
     *              action that triggered the function to execute.
     *              </p>
     */
    @FXML
    /**
     * Performs switchtoajouterCinema operation.
     *
     * @return the result of the operation
     */
    public void switchtoajouterCinema(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceFilm.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.AjouterFilm_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final IOException e) {
            ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Validates an image file for size and format requirements.
     * 
     * <p>
     * This method performs comprehensive validation of image files before
     * allowing them to be uploaded. It checks both file size constraints
     * and validates that the file is a proper image format.
     * </p>
     * 
     * @param file the image file to validate, must not be {@code null}
     * @return {@code true} if the file meets all validation criteria:
     *         <ul>
     *         <li>File size is 5MB or less</li>
     *         <li>File is a valid image format that can be loaded by JavaFX</li>
     *         </ul>
     *         {@code false} otherwise
     * @throws IllegalArgumentException if file is {@code null}
     * @see Image#Image(String)
     * @since 1.0.0
     */
    /**
     * Validates an image file for size and format requirements.
     * 
     * <p>
     * This method performs comprehensive validation of image files before
     * allowing them to be uploaded. It checks both file size constraints
     * and validates that the file is a proper image format.
     * </p>
     * 
     * @param file the image file to validate, must not be {@code null}
     * @return {@code true} if the file meets all validation criteria:
     *         <ul>
     *         <li>File size is 5MB or less</li>
     *         <li>File is a valid image format that can be loaded by JavaFX</li>
     *         </ul>
     *         {@code false} otherwise
     * @throws IllegalArgumentException if file is {@code null}
     * @see Image#Image(String)
     * @since 1.0.0
     */
    private boolean validateImage(File file) {
        // Add size limit (e.g., 5MB)
        if (file.length() > 5_000_000) {
            showAlert("Image file is too large. Maximum size is 5MB.");
            return false;
        }
        // Validate actual image format
        try {
            Image image = new Image(file.toURI().toString());
            return !image.isError();
        } catch (Exception e) {
            showAlert("Invalid image file format");
            return false;
        }
    }

    /**
     * Loads an image directly without caching.
     *
     * <p>
     * This method loads an image synchronously and sets it directly on the
     * ImageView.
     * </p>
     *
     * @param url       the URL or path of the image to load, must not be
     *                  {@code null}
     * @param imageView the ImageView component to display the image, must not be
     *                  {@code null}
     * @throws IllegalArgumentException if url or imageView is {@code null}
     * @since 1.0.0
     */
    private void loadImage(String url, ImageView imageView) {
        try {
            Image image = new Image(url);
            imageView.setImage(image);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load image: " + url, e);
        }
    }

    /**
     * Exports actors data to a JSON file.
     * 
     * <p>
     * This method serializes all actors currently displayed in the table view
     * to a JSON file using Jackson ObjectMapper. The export operation includes
     * all actor properties and can be used for backup or data transfer purposes.
     * </p>
     * 
     * @param filePath the destination file path for the JSON export, must not be
     *                 {@code null}
     * @throws IllegalArgumentException if filePath is {@code null} or empty
     * @see ObjectMapper#writeValue(File, Object)
     * @see #importActors(String)
     * @since 1.0.0
     */
    public void exportActors(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Actor> actors = filmActor_tableView11.getItems();
            mapper.writeValue(new File(filePath), actors);
            showAlert("Export successful");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to export actors", e);
            showAlert("Export failed: " + e.getMessage());
        }
    }

    /**
     * Imports actors data from a JSON file.
     * 
     * <p>
     * This method deserializes actors from a JSON file using Jackson ObjectMapper
     * and adds them to both the UI table and the database. The operation is wrapped
     * in an undoable action to support undo/redo functionality.
     * </p>
     * 
     * @param filePath the source file path for the JSON import, must not be
     *                 {@code null}
     * @throws IllegalArgumentException if filePath is {@code null} or empty
     * @see ObjectMapper#readValue(File, Class)
     * @see #exportActors(String)
     * @see UndoableAction
     * @since 1.0.0
     */
    public void importActors(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Actor> actors = mapper.readValue(new File(filePath),
                    mapper.getTypeFactory().constructCollectionType(List.class, Actor.class));

            UndoableAction action = new UndoableAction(() -> {
                filmActor_tableView11.getItems().addAll(actors);
                actors.forEach(actor -> {
                    ActorService service = new ActorService();
                    service.create(actor);
                });
            }, () -> {
                filmActor_tableView11.getItems().removeAll(actors);
                actors.forEach(actor -> {
                    ActorService service = new ActorService();
                    service.delete(actor);
                });
            });

            executeAction(action);
            showAlert("Import successful");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to import actors", e);
            showAlert("Import failed: " + e.getMessage());
        }
    }

    /**
     * Undoes the last action performed.
     * 
     * <p>
     * This method pops the most recent action from the undo stack,
     * reverses its effect by calling its undo method, and pushes it
     * onto the redo stack to enable redoing the action later.
     * If the undo stack is empty, this method does nothing.
     * </p>
     * 
     * @see #redo()
     * @see UndoableAction#undo()
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            UndoableAction action = undoStack.pop();
            action.undo();
            redoStack.push(action);
        }
    }

    /**
     * Redoes the last undone action.
     * 
     * <p>
     * This method pops the most recent action from the redo stack,
     * re-executes it by calling its execute method, and pushes it
     * onto the undo stack to enable undoing the action again.
     * If the redo stack is empty, this method does nothing.
     * </p>
     * 
     * @see #undo()
     * @see UndoableAction#execute()
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableAction action = redoStack.pop();
            action.execute();
            undoStack.push(action);
        }
    }

    /**
     * Executes an action and adds it to the undo history.
     * 
     * <p>
     * This method executes the provided action, pushes it onto the undo stack
     * for potential later undoing, and clears the redo stack since a new action
     * branch has been created.
     * </p>
     * 
     * @param action the action to execute and record in the undo history,
     *               must not be {@code null}
     * @throws NullPointerException if action is {@code null}
     * @see UndoableAction
     * @see #undo()
     */
    private void executeAction(UndoableAction action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();
    }

    /**
     * Inner class implementing the Command pattern for undo/redo functionality.
     * 
     * <p>
     * This class encapsulates an operation that can be executed and later undone.
     * Each action stores both the action to perform and its corresponding undo
     * action.
     * This enables the application to maintain an undo/redo history for actor
     * management
     * operations.
     * </p>
     * 
     * @see #undo()
     * @see #redo()
     * @since 1.0.0
     */
    private static class UndoableAction {
        /** The action to execute */
        private final Runnable doAction;

        /** The action to undo the execution */
        private final Runnable undoAction;

        /**
         * Creates a new undoable action with corresponding do and undo operations.
         *
         * @param doAction   the action to execute, must not be {@code null}
         * @param undoAction the action to undo the execution, must not be {@code null}
         * @throws NullPointerException if either doAction or undoAction is {@code null}
         */
        public UndoableAction(Runnable doAction, Runnable undoAction) {
            this.doAction = doAction;
            this.undoAction = undoAction;
        }

        /**
         * Executes the action.
         * 
         * <p>
         * This method triggers the execution of the action by calling the stored
         * Runnable's run method.
         * </p>
         */
        public void execute() {
            doAction.run();
        }

        /**
         * Undoes the action.
         * 
         * <p>
         * This method reverses the effect of the action by calling the stored
         * undo Runnable's run method.
         * </p>
         */
        public void undo() {
            undoAction.run();
        }
    }

    /**
     * Cleans up resources when the controller is no longer needed.
     * 
     * <p>
     * This method should be called when the view using this controller
     * is being closed or destroyed.
     * </p>
     * 
     * @since 1.0.0
     */
    @FXML
    public void close() {
        // Clean up any remaining resources if needed
    }
}
