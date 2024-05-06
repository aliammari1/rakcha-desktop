package com.esprit.controllers.films;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.films.Actor;
import com.esprit.models.users.Client;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

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

    private void searchActor(String searchText) {
        filteredActors.setPredicate(actor -> {
            // Si le champ de recherche est vide, afficher tous les acteurs
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Vérifier si le nom de l'acteur contient le texte de recherche (en ignorant la casse)
            String lowerCaseFilter = searchText.toLowerCase();
            return actor.getNom().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    void importAcotrImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory = "./src/main/resources/pictures/films/";
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

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

    }

    @FXML
    void insertActor(ActionEvent event) {
        ActorService actorService = new ActorService();
        Actor actor = new Actor(nomAcotr_textArea1.getText(), imageAcotr_ImageView1.getImage().getUrl(), bioAcotr_textArea.getText());
        actorService.create(actor);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor ajoutée");
        alert.setContentText("Actor ajoutée !");
        alert.show();
        readActorTable();
    }

    private void setupCellOnEditCommit() {
        nomAcotr_tableColumn1.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Actor, String>>() {
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

    @FXML
    void readActorTable() {
        try {

            filmActor_tableView11.setEditable(true);
            idActor_tableColumn1.setVisible(false);
            idActor_tableColumn1.setCellValueFactory(new PropertyValueFactory<Actor, Integer>("id"));
            nomAcotr_tableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Actor, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) {
                    return new SimpleStringProperty(actorStringCellDataFeatures.getValue().getNom());
                }
            });
            nomAcotr_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            DeleteActor_Column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Actor, Button>, ObservableValue<Button>>() {
                @Override
                public ObservableValue<Button> call(TableColumn.CellDataFeatures<Actor, Button> filmcategoryButtonCellDataFeatures) {
                    Button button = new Button("delete");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                        }
                    });
                    return new SimpleObjectProperty<Button>(button);
                }
            });
            bioActor_tableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Actor, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) {
                    return new SimpleStringProperty(actorStringCellDataFeatures.getValue().getBiographie());
                }
            });
            bioActor_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
            imageAcotr_tableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Actor, HBox>, ObservableValue<HBox>>() {
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
                            @Override
                            public void handle(MouseEvent event) {
                                try {
                                    FileChooser fileChooser = new FileChooser();
                                    fileChooser.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                                            new FileChooser.ExtensionFilter("JPG", "*.jpg")
                                    );
                                    File file = fileChooser.showOpenDialog(null);
                                    if (file != null) {
                                        String destinationDirectory = "./src/main/resources/pictures/films/";
                                        Path destinationPath = Paths.get(destinationDirectory);
                                        String uniqueFileName = System.currentTimeMillis() + "_" + file.getName();
                                        Path destinationFilePath = destinationPath.resolve(uniqueFileName);
                                        Files.copy(file.toPath(), destinationFilePath);
                                        Image image = new Image(destinationFilePath.toUri().toString());
                                        imageView.setImage(image);
                                        hBox.getChildren().clear();
                                        hBox.getChildren().add(imageView);
                                        new ActorService().update(new Actor(null, destinationFilePath.toUri().toString(), null));
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


    void deleteFilm(int id) {
        ActorService actorService = new ActorService();
        actorService.delete(new Actor(id));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actor supprimée");
        alert.setContentText("Actor supprimée !");
        alert.show();
        readActorTable();

    }

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
