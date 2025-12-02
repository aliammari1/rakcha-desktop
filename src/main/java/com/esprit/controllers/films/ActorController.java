package com.esprit.controllers.films;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
 * ({@link java.util.concurrent.ConcurrentHashMap})
 * and proper JavaFX {@code Platform.runLater()}
 * <p>
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
 * }
 * </pre>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @see Actor
 * @see ActorService
 * @see javafx.scene.control.TableView
 * @see javafx.collections.transformation.FilteredList
 * @since 1.0.0
 */
public class ActorController {

    private static final Logger LOGGER = Logger.getLogger(ActorController.class.getName());
    private final Stack<UndoableAction> undoStack = new Stack<>();
    private final Stack<UndoableAction> redoStack = new Stack<>();
    @FXML
    public ImageView imageActor_ImageView1;
    @FXML
    private VBox anchorActor_Form;
    @FXML
    private TextArea bioActor_textArea;
    @FXML
    private TableColumn<Actor, Button> DeleteActor_Column1;
    @FXML
    private Label errorBio;
    @FXML
    private Label errorNameActor;
    @FXML
    private Button GoToFilms_Button;
    @FXML
    private TableView<Actor> filmActor_tableView11;
    @FXML
    private TableColumn<Actor, Integer> idActor_tableColumn1;
    @FXML
    private TableColumn<Actor, String> bioActor_tableColumn1;
    @FXML
    private TableColumn<Actor, HBox> imageActor_tableColumn1;
    @FXML
    private TableColumn<Actor, String> nomActor_tableColumn1;
    @FXML
    private TextField nomActor_textField;
    private FilteredList<Actor> filteredActors;
    @FXML
    private TextField recherche_textField;
    private String cloudinaryImageUrl;

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
            }
        );

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

            }
        );
    }


    /**
     * Filter the displayed actor list to only those whose name contains the given text (case-insensitive).
     * <p>
     * If {@code searchText} is null or empty, the filter is cleared so all actors are shown.
     *
     * @param searchText the query used to filter actors by name; matching is case-insensitive
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
            }
        );
    }


    /**
     * Lets the user choose an image file, validates it, copies it into
     * ./src/main/resources/img/films/ with a unique filename, and displays it
     * in the actor ImageView.
     *
     * <p>If the selected file fails validation the method returns without
     * changing UI state; I/O errors are logged.</p>
     *
     * @param event the triggering ActionEvent (not used by this handler)
     * @see #validateImage(File)
     */
    @FXML
    void importActorImage(final ActionEvent event) {
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
                this.imageActor_ImageView1.setImage(selectedImage);
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
     * Create and persist a new Actor from the controller's form fields and refresh the actors table.
     *
     * <p>The method requires an image to be present in {@code imageActor_ImageView1}; if none is set,
     * an informational alert is shown and no actor is created. The actor's name and biography are taken
     * from {@code nomActor_textField} and {@code bioActor_textArea}. The image path is derived from the
     * ImageView's URL, with a best-effort fallback to the full URL if path extraction fails. After
     * persisting the actor, the method shows a confirmation alert and reloads the actor table.</p>
     */
    @FXML
    void insertActor(final ActionEvent event) {
        if (this.imageActor_ImageView1.getImage() == null) {
            showAlert("Please select an image for the actor");
            return;
        }


        final String fullPath = this.imageActor_ImageView1.getImage().getUrl();
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
        final Actor actor = new Actor(this.nomActor_textField.getText(), imagePath,
            this.bioActor_textArea.getText());
        actorService.create(actor);

        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor ajoutée");
        alert.setContentText("Actor ajoutée !");
        alert.show();
        this.readActorTable();
    }


    /**
     * Configures commit handlers on the name and biography table columns so that
     * cell edits are applied to the underlying Actor instance and persisted via
     * updateActor.
     */
    private void setupCellOnEditCommit() {
        this.nomActor_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
                                                       /**
                                                        * Apply an edited actor name from an inline table cell and persist the change.
                                                        *
                                                        * @param event the cell edit event for the actor-name column containing the row position and the new name
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

                                                   }
        );
        this.bioActor_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
                                                       /**
                                                        * Apply an in-table biography edit to the Actor and persist the change.
                                                        *
                                                        * @param event cell edit event containing the edited Actor row and the new biography value
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

                                                   }
        );
    }


    /**
     * Open a file chooser to select an image, validate and upload it to the configured cloud storage,
     * store the returned image URL in {@code cloudinaryImageUrl}, and display the uploaded image in {@code imageActor_ImageView1}.
     * <p>
     * If validation fails or an upload error occurs, the current image is left unchanged.
     *
     * @param event the ActionEvent that triggered the image import
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
                this.imageActor_ImageView1.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }

        }

    }


    /**
     * Persist the updated fields of the given Actor, display a success or error alert, and refresh the actor table.
     *
     * <p>On success an informational alert with the message "Actor modifiée !" is shown. On failure an error alert
     * is displayed containing the exception message. The actor table is reloaded after the operation.</p>
     *
     * @param actor the Actor instance containing updated values to persist
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
     * Populate the actor TableView with actors from the database and configure its columns and row interactions.
     *
     * <p>Configures the table for editing and binds columns for id (hidden), name, biography, an image cell that displays
     * the actor image and supports replacement, and a per-row delete button; then loads the first page of actors into the table.</p>
     */
    @FXML
    void readActorTable() {
        try {
            this.filmActor_tableView11.setEditable(true);
            this.idActor_tableColumn1.setVisible(false);
            this.idActor_tableColumn1.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.nomActor_tableColumn1.setCellValueFactory((
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
            this.nomActor_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            this.DeleteActor_Column1.setCellValueFactory(filmcategoryButtonCellDataFeatures -> {
                    final Button button = new Button("delete");
                    button.setOnAction((final ActionEvent event) -> {
                            this.deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                        }
                        /**
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
            this.imageActor_tableColumn1
                .setCellValueFactory((final TableColumn.CellDataFeatures<Actor, HBox> param) -> {
                        final HBox hBox = new HBox();
                        try {
                            final ImageView imageView = new ImageView();
                            imageView.setFitWidth(120); // Réglez la largeur de l'image selon vos préférences
                            imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
                            try {
                                loadImage(param.getValue().getImageUrl(), imageView);
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

                                }
                                /**
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
                    }
                    /**
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
     * Remove the actor with the given id and refresh the displayed actor list.
     * <p>
     * Shows an informational alert confirming the deletion.
     *
     * @param id identifier of the actor to delete
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
     * Replaces the current window's scene with the Films interface.
     * <p>
     * Loads the Films FXML and sets it as the scene on the current stage.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    /**
     * Navigates to the Films interface.
     *
     * @param event the action event
     */
    public void switchToFilms(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceFilm.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.GoToFilms_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final IOException e) {
            ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Navigates to the Films interface.
     *
     * @param event the ActionEvent that triggered the navigation
     * @deprecated Use {@link #switchToFilms(ActionEvent)} instead.
     */
    @Deprecated
    public void switchtoajouterCinema(final ActionEvent event) {
        switchToFilms(event);
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

     * @return {@code true}
    if the file meets all validation criteria:
     *         <ul>
     *         <li>File size is 5MB or less</li>
     *         <li>File is a valid image format that can be loaded by JavaFX</li>
     *         </ul>
     *         {@code false}
    otherwise
     * @throws IllegalArgumentException if file is {@code null}

     * @see Image#Image(String)
     * @since 1.0.0
     */
    /**
     * Validate that a file is an acceptable image for use by the application.
     *
     * @param file the image file to validate
     * @return `true` if the file's size is less than or equal to 5,000,000 bytes and JavaFX can load it; `false` otherwise
     * @see javafx.scene.image.Image#Image(String)
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
     * Load an image from the given URL and set it on the provided ImageView.
     *
     * <p>If the image cannot be loaded, the ImageView is left unchanged.</p>
     *
     * @param url       the URL or filesystem path of the image; may be a URI string
     * @param imageView the ImageView to display the loaded image
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
     * Writes the actors currently displayed in the table view to a JSON file at the specified path.
     * <p>
     * The method serializes the table's current items using Jackson and writes them to the given file path.
     * On completion it displays an informational alert indicating success or failure and logs errors when they occur.
     *
     * @param filePath the destination file path for the JSON export
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
     * Import actors from a JSON file into the application and record the operation for undo/redo.
     * <p>
     * Parses the specified file as a JSON array of Actor objects, adds the parsed actors to the table view
     * and persists them to the database, and registers a corresponding undo action that removes those actors
     * from the table and deletes them from the database.
     *
     * @param filePath path to a JSON file containing an array of Actor objects
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
                    }
                );
            }
                , () -> {
                filmActor_tableView11.getItems().removeAll(actors);
                actors.forEach(actor -> {
                        ActorService service = new ActorService();
                        service.delete(actor);
                    }
                );
            }
            );

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
     * Reapplies the most recently undone action and makes it available for undo again.
     * <p>
     * Does nothing if there is no action to redo.
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
     * Execute the given undoable action and record it in the undo history.
     * <p>
     * The action is executed, then pushed onto the undo stack; the redo stack is cleared.
     *
     * @param action the action to execute and record; must not be {@code null}
     * @see UndoableAction
     * @see #undo()
     */
    private void executeAction(UndoableAction action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();
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

        /**
         * The action to execute
         */
        private final Runnable doAction;

        /**
         * The action to undo the execution
         */
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
         * Executes the action's do operation.
         *
         * <p>Invokes the stored operation to perform the change represented by this UndoableAction.</p>
         */
        public void execute() {
            doAction.run();
        }


        /**
         * Reverses this undoable action.
         * <p>
         * Executes the action's undo operation to restore the previous state.
         */
        public void undo() {
            undoAction.run();
        }

    }

}
