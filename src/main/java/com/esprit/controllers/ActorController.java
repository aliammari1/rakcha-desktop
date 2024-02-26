package com.esprit.controllers;

import com.esprit.models.Actor;
import com.esprit.services.ActorService;
import com.esprit.utils.DataSource;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import net.synedra.validatorfx.Validator;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

public class ActorController {
    Blob imageBlob;
    private File selectedFile;
    @FXML
    private TextArea bioAcotr_textArea;
    @FXML
    private AnchorPane ActorCrudInterface;

    @FXML
    private Button AjouterFilm_Button;

    @FXML
    private AnchorPane anchorActor_Form;
    @FXML
    private TableColumn<Actor, Button> DeleteActor_Column1;

    @FXML
    private TableView<Actor> filmActor_tableView11;

    @FXML
    private TableColumn<Actor, Integer> idActor_tableColumn1;

    @FXML
    private ComboBox<String> idfilmActor_comboBox1;
    @FXML
    private TableColumn<Actor, String> bioActor_tableColumn1;

    @FXML
    private ImageView imageAcotr_ImageView1;

    @FXML
    private TableColumn<Actor, HBox> imageAcotr_tableColumn1;

    @FXML
    private AnchorPane image_view2;

    @FXML
    private TableColumn<Actor, String> nomAcotr_tableColumn1;

    @FXML
    private TextArea nomAcotr_textArea1;

    @FXML
    void initialize() {
        readActorTable();
        TextField userTextField = new TextField();
        for (Node n : anchorActor_Form.getChildren())
            System.out.println(n);
        Validator validator = new Validator();

        validator.createCheck()
                .dependsOn("nom", nomAcotr_textArea1.textProperty())
                .withMethod(c -> {
                    String nom = c.get("nom");
                    if (!nom.toLowerCase().equals(nom)) {
                        c.error("Please use only lowercase letters.");
                    }
                })
                .decorates(nomAcotr_textArea1)
                .immediate();
    }

    @FXML
    void importAcotrImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            imageAcotr_ImageView1.setImage(selectedImage);
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
        if (selectedFile != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                // Convertir le fichier en un objet Blob
                FileInputStream fis = new FileInputStream(selectedFile);
                connection = DataSource.getInstance().getConnection();
                Blob imageBlob = connection.createBlob();

                // Définir le flux d'entrée de l'image dans l'objet Blob
                try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Créer l'objet Cinema avec l'image Blob

                ActorService actorService = new ActorService();
                Actor actor = new Actor(nomAcotr_textArea1.getText(), imageBlob, bioAcotr_textArea.getText());
                actorService.create(actor);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Film ajoutée");
                alert.setContentText("Film ajoutée !");
                alert.show();
            } catch (Exception e) {
                showAlert("Erreur lors de l'ajout du Film : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner une image d'abord !");
        }

    }

    @FXML
    void updateActor(ActionEvent event) {
        if (imageBlob != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                if (selectedFile != null)
                // Convertir le fichier en un objet Blob
                {
                    FileInputStream fis = new FileInputStream(selectedFile);

                    connection = DataSource.getInstance().getConnection();

                    // Définir le flux d'entrée de l'image dans l'objet Blob
                    try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                // modifier un produit

                ActorService actorService = new ActorService();
// Assign value to imageBlob
                /* assign the blob value here */
                actorService.update(new Actor(
                        nomAcotr_textArea1.getText(),
                        imageBlob,
                        bioAcotr_textArea.getText()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit modifiée");
                alert.setContentText("Produit modifiée !");
                alert.show();

            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de la modification du produit : " + e.getMessage());
            }
        }
        readActorTable();


    }

    @FXML
    void readActorTable() {
        try {
            filmActor_tableView11.setEditable(true);
            idActor_tableColumn1.setCellValueFactory(new PropertyValueFactory<Actor, Integer>("id"));
            nomAcotr_tableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Actor, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Actor, String> actorStringCellDataFeatures) {
                    return new SimpleStringProperty(actorStringCellDataFeatures.getValue().getNom());
                }
            });
            nomAcotr_tableColumn1.setCellFactory(TextFieldTableCell.forTableColumn());
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
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(120); // Réglez la largeur de l'image selon vos préférences
                    imageView.setFitHeight(100); // Réglez la hauteur de l'image selon vos préférences
                    try {
                        imageView.setImage(new Image(new ByteArrayInputStream(param.getValue().getImage().getBinaryStream().readAllBytes())));
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
                                File file = fileChooser.showOpenDialog(new Stage());
                                if (file != null) {
                                    Image image = new Image(file.toURI().toURL().toString());
                                    imageView.setImage(image);
                                    hBox.getChildren().clear();
                                    System.out.println(image);
                                    hBox.getChildren().add(imageView);
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    });
                    return new SimpleObjectProperty<HBox>(hBox);
                }
            });
//            imageAcotr_tableColumn1.setCellValueFactory(cellData -> {
//                Actor actor = cellData.getValue();
//                ImageView imageView = new ImageView();
//                imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
//                imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
//                try {
//                    Blob blob = actor.getImage();
//                    if (blob != null) {
//                        Image image = new Image(blob.getBinaryStream());
//                        imageView.setImage(image);
//                    } else {
//                        // Afficher une image par défaut si le logo est null
//                        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("default_image.png")));
//                        imageView.setImage(defaultImage);
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
//            });
            ActorService categoryService = new ActorService();
            ObservableList<Actor> obC = FXCollections.observableArrayList(categoryService.read());
            filmActor_tableView11.setItems(obC);
            filmActor_tableView11.getSelectionModel().selectedItemProperty().addListener((observableValue, actor, t1) -> {
                Actor a = filmActor_tableView11.getSelectionModel().getSelectedItem();
                if (a != null) {
                    nomAcotr_textArea1.setText(a.getNom());
                    nomAcotr_textArea1.setText(String.valueOf(a.getId()));
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
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


}
