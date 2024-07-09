package com.esprit.controllers.films;
import com.esprit.models.films.Actor;
import com.esprit.services.films.ActorService;
import com.esprit.utils.DataSource;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
/**
 * Is responsible for handling user interactions related to actors in a movie database
 * application. It provides functions to read and update actor data, as well as delete
 * actors from the database. Additionally, it provides an event handler for switching
 * to a new stage with a different FXML layout when the "Ajouter Cinéma" button is clicked.
 */
public class ActorController {
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
    /**
     * Sets up a filtered list of actors based on a searchable text field and updates a
     * tableView with the filtered list.
     */
    @FXML
    void initialize() {
        readActorTable();
        setupCellOnEditCommit();
        filteredActors = new FilteredList<>(filmActor_tableView11.getItems());
        // Réinitialiser la TableView avec la liste filtrée
        filmActor_tableView11.setItems(filteredActors);
        // Appliquer le filtre lorsque le texte de recherche change
        recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchActor(newValue);
        });
    }
    /**
     * Filters an `Actors` list based on a provided search text, returning only actors
     * whose name contains the searched text (ignoring case).
     * 
     * @param searchText search query that filters the actors in the `filteredActors` list.
     */
    private void searchActor(String searchText) {
        filteredActors.setPredicate(actor -> {
            // Si le champ de recherche est vide, afficher tous les acteurs
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            // Vérifier si le nom de l'acteur contient le texte de recherche (en ignorant la
            // casse)
            String lowerCaseFilter = searchText.toLowerCase();
            return actor.getNom().toLowerCase().contains(lowerCaseFilter);
        });
    }
    /**
     * Allows the user to select an image file, which is then copied to a specified
     * directory and set as the image for a widget named `imageAcoter_ImageView1`.
     * 
     * @param event open file selection event that triggered the function to execute.
     * 
     * 	- Type: `ActionEvent` (represents an action event triggered by a user)
     * 	- Target: null (indicates that the event is not associated with any specific component)
     */
    @FXML
    void importAcotrImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory = "./src/main/resources/img/films/";
                Path destinationPath = Paths.get(destinationDirectory);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath);
                Image selectedImage = new Image(destinationFilePath.toUri().toString());
                imageAcotr_ImageView1.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Creates an `Alert` object and displays a message to the user.
     * 
     * @param message content to be displayed in an alert window when the `showAlert()`
     * method is called.
     */
    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    /**
     * Takes an image URL, extracts the actor's path from it, creates a new actor object
     * with the actor's name, URI, and bio, and adds it to the ActorService using the
     * create method. It then displays an information alert with the title "Actor ajoutée"
     * and content text "Actor ajouté!" before refreshing the actor table.
     * 
     * @param event user's action of clicking on the "Insert Actor" button, which triggers
     * the function to execute and perform the actor insertion logic.
     * 
     * 	- `event`: An `ActionEvent` object representing the user's action that triggered
     * the function.
     */
    @FXML
    void insertActor(ActionEvent event) {
        String fullPath = imageAcotr_ImageView1.getImage().getUrl();
        String requiredPath = fullPath.substring(fullPath.indexOf("/img/actors/"));
        URI uri = null;
        try {
            uri = new URI(requiredPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ActorService actorService = new ActorService();
        Actor actor = new Actor(nomAcotr_textArea1.getText(), uri.getPath(),
                bioAcotr_textArea.getText());
        actorService.create(actor);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor ajoutée");
        alert.setContentText("Actor ajoutée !");
        alert.show();
        readActorTable();
    }
    /**
     * Sets up event handlers for two table columns in a `TableView`. When an edit is
     * committed, it updates the corresponding actor's property with the new value from
     * the column edit.
     */
    private void setupCellOnEditCommit() {
        nomAcotr_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
            /**
             * Is called when a cell in a table is edited. It sets the new value of the cell to
             * the nominated value, and then updates the corresponding actor object with the new
             * value.
             * 
             * @param event CellEditEvent object that contains information about the edit event
             * occurring on a table column, including the edited cell's position and the new value
             * being entered.
             * 
             * 	- `TableColumn.CellEditEvent`: This is the type of event that triggered the
             * function's execution.
             * 	- `Actor`: This is the type parameter of the event, which represents the data
             * type of the cell being edited.
             * 	- `String`: This is the type of the value being edited in the cell.
             */
            @Override
            public void handle(TableColumn.CellEditEvent<Actor, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setNom(event.getNewValue());
                    updateActor(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        bioActor_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
            /**
             * In the `TableColumn` class is responsible for handling cell editing events for a
             * column containing a `String` type data. It updates the value of the `biographie`
             * property of the corresponding actor object in the table view, and also triggers
             * an update of the actor object itself.
             * 
             * @param event CellEditEvent of a TableColumn, providing the edited cell and its new
             * value.
             * 
             * 	- `TableColumn.CellEditEvent`: The event type that indicates cell editing has
             * occurred on a table column.
             * 	- `Actor`: The data type of the cells being edited, which is an actor object in
             * this case.
             * 	- `String`: The type of the value being edited, which is a string in this case.
             */
            @Override
            public void handle(TableColumn.CellEditEvent<Actor, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setBiographie(event.getNewValue());
                    updateActor(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Allows the user to select an image file from a file chooser, then saves it to two
     * different locations and displays the image in an `ImageView`.
     * 
     * @param event event of opening a file chooser, which triggers the code inside the
     * function to execute when a file is selected.
     * 
     * 	- `event`: an ActionEvent object representing the user's action of opening a file
     * from the FileChooser.
     */
    @FXML
    void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/actors/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\actors\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                imageAcotr_ImageView1.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Updates an actor's data in a database by first getting a connection, then calling
     * the `update` method of the `ActorService` class and displaying an alert with the
     * updated actor's information.
     * 
     * @param actor Actor object to be updated.
     * 
     * 	- `Actor actor`: The actor to be updated.
     */
    void updateActor(Actor actor) {
        Connection connection = null;
        try {
            connection = DataSource.getInstance().getConnection();
            ActorService actorService = new ActorService();
            // Assign value to imageString
            /* assign the String value here */
            actorService.update(actor);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actor modifiée");
            alert.setContentText("Actor modifiée !");
            alert.show();
        } catch (Exception e) {
            showAlert("Erreur lors de la modification du Actor : " + e.getMessage());
        }
        readActorTable();
    }
    /**
     * Reads data from an Actor database, creates a table with columns for actor name,
     * nom, bio, and image, and displays the data in the table.
     */
    @FXML
    void readActorTable() {
        try {
            filmActor_tableView11.setEditable(true);
            idActor_tableColumn1.setVisible(false);
            idActor_tableColumn1.setCellValueFactory(new PropertyValueFactory<Actor, Integer>("id"));
            nomAcotr_tableColumn1.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Actor, String>, ObservableValue<String>>() {
                        /**
                         * Generates a `SimpleStringProperty` instance from the `Value` object returned by
                         * the `getNom()` method of an `Actor` object.
                         * 
                         * @param actorStringCellDataFeatures cell data features of an actor, specifically
                         * the string value of the actor's name.
                         * 
                         * @returns a `SimpleStringProperty` object containing the nominal value of the input
                         * `Actor` object.
                         */
                        @Override
                        public ObservableValue<String> call(
                                TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) {
                            return new SimpleStringProperty(actorStringCellDataFeatures.getValue().getNom());
                        }
                    });
            nomAcotr_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            DeleteActor_Column1.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Actor, Button>, ObservableValue<Button>>() {
                        /**
                         * Creates a new button with an `OnAction` event handler that deletes a film when
                         * clicked, and returns the button object as an observable value.
                         * 
                         * @param filmcategoryButtonCellDataFeatures value of a Button cell in a TableColumn,
                         * which contains the ID of a film category.
                         * 
                         * 	- `getValue()`: returns the internal value of the object, which is an instance
                         * of `FilmCategoryButtonCellData`.
                         * 	- `getId()`: retrieves the unique identifier for the film category button.
                         * 
                         * @returns a `SimpleObjectProperty` of a `Button` object with the text "delete".
                         * 
                         * 	- The output is an `ObservableValue` of type `Button`, which means that it can
                         * be observed to change over time.
                         * 	- The output is created by calling the `Button` constructor and setting an
                         * `OnAction` event handler for the button's `onAction` method.
                         * 	- The `OnAction` event handler is an instance of `EventHandler`, which is a
                         * standard Java interface for handling events. In this case, the event handler deletes
                         * the film with the specified ID when the button is pressed.
                         */
                        @Override
                        public ObservableValue<Button> call(
                                TableColumn.CellDataFeatures<Actor, Button> filmcategoryButtonCellDataFeatures) {
                            Button button = new Button("delete");
                            button.setOnAction(new EventHandler<ActionEvent>() {
                                /**
                                 * Deletes a film based on the ID passed as an event parameter from the `filmcategoryButtonCellDataFeatures`.
                                 * 
                                 * @param event `ActionEvent` that triggered the function execution, providing the
                                 * identifier of the button that was clicked.
                                 */
                                @Override
                                public void handle(ActionEvent event) {
                                    deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                                }
                            });
                            return new SimpleObjectProperty<Button>(button);
                        }
                    });
            bioActor_tableColumn1.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Actor, String>, ObservableValue<String>>() {
                        /**
                         * Takes a `TableColumn.CellDataFeatures<Actor, String>` parameter and returns an
                         * `ObservableValue<String>` representing the actor's biography.
                         * 
                         * @param actorStringCellDataFeatures String value of a cell in a table that displays
                         * an actor's biography.
                         * 
                         * @returns a `SimpleStringProperty` containing the biography of the actor.
                         */
                        @Override
                        public ObservableValue<String> call(
                                TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) {
                            return new SimpleStringProperty(actorStringCellDataFeatures.getValue().getBiographie());
                        }
                    });
            bioActor_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            imageAcotr_tableColumn1.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<Actor, HBox>, ObservableValue<HBox>>() {
                        /**
                         * Generates an `HBox` container that displays an image when clicked, allowing users
                         * to select images from a file system and display them in the application.
                         * 
                         * @param param CellDataFeatures of a TableColumn, providing an Actor object as its
                         * value, which is used to display an image and handle mouse clicks to save the image
                         * to a designated directory.
                         * 
                         * 	- `param.getValue()`: returns an instance of `Actor`, which contains the image
                         * to be displayed.
                         * 
                         * The `param` object is not destructured in this case.
                         * 
                         * @returns an `ObservableValue` of type `HBox`, which contains a `ImageView` element
                         * that displays an image selected by the user and allows for its save to a designated
                         * directory.
                         * 
                         * 	- `HBox` is the type of the output, which is an observable value of type HBox.
                         * 	- The output is created by initializing a new instance of HBox and adding an
                         * ImageView component to it.
                         * 	- The ImageView component displays an image, which is set using the `setImage()`
                         * method and passing in the image from the `getValue()` method of the parameter object.
                         * 	- The ImageView component also has an event handler attached to it that responds
                         * to a mouse click event. When the event occurs, the handler will execute the code
                         * inside it.
                         */
                        @Override
                        public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Actor, HBox> param) {
                            HBox hBox = new HBox();
                            try {
                                ImageView imageView = new ImageView();
                                imageView.setFitWidth(120); // Réglez la largeur de l'image selon vos préférences
                                imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
                                try {
                                    imageView.setImage(new Image(param.getValue().getImage()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                hBox.getChildren().add(imageView);
                                hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                    /**
                                     * Displays an open file dialog, saves the selected file to a designated directory,
                                     * creates an image from the saved file, and displays the image in a graphical user
                                     * interface.
                                     * 
                                     * @param event mouse event that triggered the function, providing information about
                                     * the location and type of the event.
                                     * 
                                     * 	- `event`: a `MouseEvent` object representing a user interaction with the application.
                                     */
                                    @Override
                                    public void handle(MouseEvent event) {
                                        try {
                                            FileChooser fileChooser = new FileChooser();
                                            fileChooser.getExtensionFilters().addAll(
                                                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                                                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                            File file = fileChooser.showOpenDialog(null);
                                            if (file != null) {
                                                String destinationDirectory = "./src/main/resources/img/films/";
                                                Path destinationPath = Paths.get(destinationDirectory);
                                                String uniqueFileName = System.currentTimeMillis() + "_"
                                                        + file.getName();
                                                Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                                                Files.copy(file.toPath(), destinationFilePath);
                                                Image image = new Image(destinationFilePath.toUri().toString());
                                                imageView.setImage(image);
                                                hBox.getChildren().clear();
                                                hBox.getChildren().add(imageView);
                                                new ActorService().update(
                                                        new Actor(null, destinationFilePath.toUri().toString(), null));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return new SimpleObjectProperty<HBox>(hBox);
                        }
                    });
            ActorService categoryService = new ActorService();
            ObservableList<Actor> obC = FXCollections.observableArrayList(categoryService.read());
            filmActor_tableView11.setItems(obC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Deletes an actor from a table using the `delete()` method provided by the
     * `ActorService`. It displays an informational alert and updates the `readActorTable()`
     * function to reflect the change.
     * 
     * @param id identifier of the actor to be deleted.
     */
    void deleteFilm(int id) {
        ActorService actorService = new ActorService();
        actorService.delete(new Actor(id));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor supprimée");
        alert.setContentText("Actor supprimée !");
        alert.show();
        readActorTable();
    }
    /**
     * Loads an FXML file using the `FXMLLoader` class, creates a new `AnchorPane` object,
     * and sets it as the scene of a `Stage`.
     * 
     * @param event action event that triggered the function execution, providing the
     * necessary context for the code to perform its intended task.
     * 
     * 	- `event`: An instance of `ActionEvent`, representing a user action that triggered
     * the function to execute.
     */
    @FXML
    public void switchtoajouterCinema(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) AjouterFilm_Button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
