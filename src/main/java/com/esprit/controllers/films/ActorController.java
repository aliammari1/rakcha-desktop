package com.esprit.controllers.films;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.models.films.Actor;
import com.esprit.services.films.ActorService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
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
 * Is responsible for handling user interactions related to actors in a movie
 * database application. It provides functions to read and update actor data, as
 * well as delete actors from the database. Additionally, it provides an event
 * handler for switching to a new stage with a different FXML layout when the
 * "Ajouter Cinéma" button is clicked.
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

    private final ConcurrentHashMap<String, Image> imageCache = new ConcurrentHashMap<>();
    private final ExecutorService imageLoaderService = Executors.newFixedThreadPool(3);
    private final Stack<UndoableAction> undoStack = new Stack<>();
    private final Stack<UndoableAction> redoStack = new Stack<>();

    /**
     * Sets up a filtered list of actors based on a searchable text field and
     * updates a tableView with the filtered list.
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
     * Filters an `Actors` list based on a provided search text, returning only
     * actors whose name contains the searched text (ignoring case).
     *
     * @param searchText
     *            search query that filters the actors in the `filteredActors` list.
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
     * Allows the user to select an image file, which is then copied to a specified
     * directory and set as the image for a widget named `imageAcoter_ImageView1`.
     *
     * @param event
     *            open file selection event that triggered the function to execute.
     *            <p>
     *            - Type: `ActionEvent` (represents an action event triggered by a
     *            user) - Target: null (indicates that the event is not associated
     *            with any specific component)
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
     * Creates an `Alert` object and displays a message to the user.
     *
     * @param message
     *            content to be displayed in an alert window when the `showAlert()`
     *            method is called.
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
     * Takes an image URL, extracts the actor's path from it, creates a new actor
     * object with the actor's name, URI, and bio, and adds it to the ActorService
     * using the create method. It then displays an information alert with the title
     * "Actor ajoutée" and content text "Actor ajouté!" before refreshing the actor
     * table.
     *
     * @param event
     *            user's action of clicking on the "Insert Actor" button, which
     *            triggers the function to execute and perform the actor insertion
     *            logic.
     *            <p>
     *            - `event`: An `ActionEvent` object representing the user's action
     *            that triggered the function.
     */
    @FXML
    void insertActor(final ActionEvent event) {
        final String fullPath = this.imageAcotr_ImageView1.getImage().getUrl();
        final String requiredPath = fullPath.substring(fullPath.indexOf("/img/actors/"));
        URI uri = null;
        try {
            uri = new URI(requiredPath);
        } catch (final Exception e) {
            ActorController.LOGGER.info(e.getMessage());
        }
        final ActorService actorService = new ActorService();
        final Actor actor = new Actor(this.nomAcotr_textArea1.getText(), uri.getPath(),
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
             *            CellEditEvent object that contains information about the edit
             *            event occurring on a table column, including the edited cell's
             *            position and the new value being entered.
             *
             *            - `TableColumn.CellEditEvent`: This is the type of event that
             *            triggered the function's execution. - `Actor`: This is the type
             *            parameter of the event, which represents the data type of the cell
             *            being edited. - `String`: This is the type of the value being
             *            edited in the cell.
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
             *            CellEditEvent of a TableColumn, providing the edited cell and its
             *            new value.
             *
             *            - `TableColumn.CellEditEvent`: The event type that indicates cell
             *            editing has occurred on a table column. - `Actor`: The data type
             *            of the cells being edited, which is an actor object in this case.
             *            - `String`: The type of the value being edited, which is a string
             *            in this case.
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
     *            event of opening a file chooser, which triggers the code inside
     *            the function to execute when a file is selected.
     *            <p>
     *            - `event`: an ActionEvent object representing the user's action of
     *            opening a file from the FileChooser.
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
                final String destinationDirectory1 = "./src/main/resources/img/actors/";
                final String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\actors\\";
                final Path destinationPath1 = Paths.get(destinationDirectory1);
                final Path destinationPath2 = Paths.get(destinationDirectory2);
                final String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                final Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                final Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                final Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                this.imageAcotr_ImageView1.setImage(selectedImage);
            } catch (final IOException e) {
                ActorController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Updates an actor's data in a database by first getting a connection, then
     * calling the `update` method of the `ActorService` class and displaying an
     * alert with the updated actor's information.
     *
     * @param actor
     *            Actor object to be updated.
     *            <p>
     *            - `Actor actor`: The actor to be updated.
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
             *            cell data features of an actor, specifically the string value of
             *            the actor's name.
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
                   *            `ActionEvent` that triggered the function execution, providing the
                   *            identifier of the button that was clicked.
                   */
                );
                return new SimpleObjectProperty<>(button);
            }
            /**
             * Creates a new button with an `OnAction` event handler that deletes a film
             * when clicked, and returns the button object as an observable value.
             *
             * @param filmcategoryButtonCellDataFeatures
             *            value of a Button cell in a TableColumn, which contains the ID of
             *            a film category.
             *
             *            - `getValue()`: returns the internal value of the object, which is
             *            an instance of `FilmCategoryButtonCellData`. - `getId()`:
             *            retrieves the unique identifier for the film category button.
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
                                                      *            String value of a cell in a table that displays an
                                                      *            actor's biography.
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
                                loadImageWithCache(param.getValue().getImage(), imageView);
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
                               *            mouse event that triggered the function, providing information
                               *            about the location and type of the event.
                               *
                               *            - `event`: a `MouseEvent` object representing a user interaction
                               *            with the application.
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
                       *            CellDataFeatures of a TableColumn, providing an Actor object as
                       *            its value, which is used to display an image and handle mouse
                       *            clicks to save the image to a designated directory.
                       *
                       *            - `param.getValue()`: returns an instance of `Actor`, which
                       *            contains the image to be displayed.
                       *
                       *            The `param` object is not destructured in this case.
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
            final ObservableList<Actor> obC = FXCollections.observableArrayList(categoryService.read());
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
     *            identifier of the actor to be deleted.
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
     *            action event that triggered the function execution, providing the
     *            necessary context for the code to perform its intended task.
     *            <p>
     *            - `event`: An instance of `ActionEvent`, representing a user
     *            action that triggered the function to execute.
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

    private void loadImageWithCache(String url, ImageView imageView) {
        if (imageCache.containsKey(url)) {
            imageView.setImage(imageCache.get(url));
            return;
        }

        imageLoaderService.submit(() -> {
            try {
                Image image = new Image(url);
                imageCache.put(url, image);
                Platform.runLater(() -> imageView.setImage(image));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load image: " + url, e);
            }
        });
    }

    /**
     * Performs exportActors operation.
     *
     * @return the result of the operation
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
     * Performs importActors operation.
     *
     * @return the result of the operation
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
     * Performs undo operation.
     *
     * @return the result of the operation
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            UndoableAction action = undoStack.pop();
            action.undo();
            redoStack.push(action);
        }
    }

    /**
     * Performs redo operation.
     *
     * @return the result of the operation
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableAction action = redoStack.pop();
            action.execute();
            undoStack.push(action);
        }
    }

    private void executeAction(UndoableAction action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();
    }

    // Add inner class for undo/redo support
    private static class UndoableAction {
        private final Runnable doAction;
        private final Runnable undoAction;

        /**
         * Performs UndoableAction operation.
         *
         * @return the result of the operation
         */
        public UndoableAction(Runnable doAction, Runnable undoAction) {
            this.doAction = doAction;
            this.undoAction = undoAction;
        }

        /**
         * Performs execute operation.
         *
         * @return the result of the operation
         */
        public void execute() {
            doAction.run();
        }

        /**
         * Performs undo operation.
         *
         * @return the result of the operation
         */
        public void undo() {
            undoAction.run();
        }
    }

    // Override close method to clean up resources
    @FXML
    /**
     * Performs close operation.
     *
     * @return the result of the operation
     */
    public void close() {
        imageLoaderService.shutdown();
        imageCache.clear();
    }
}
