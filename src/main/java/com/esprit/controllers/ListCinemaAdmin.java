package com.esprit.controllers;

import com.esprit.models.Cinema;
import com.esprit.services.CinemaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;


import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

public class ListCinemaAdmin {

    @FXML
    private TableView<Cinema> listCinema;

    @FXML
    private TableColumn<Cinema, String> colCinema;

    @FXML
    private TableColumn<Cinema, String> colAdresse;

    @FXML
    private TableColumn<Cinema, String> colResponsable;

    @FXML
    private TableColumn<Cinema, ImageView> colLogo;

    @FXML
    private TableColumn<Cinema, String> colStatut;

    @FXML
    private TableColumn<Cinema, Void> colAction;


    @FXML
    private void initialize() {
        // Appelée lorsque le fichier FXML est chargé
        // Configurer les cellules des colonnes pour afficher les données
        colCinema.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));

        // Configurer la cellule de la colonne Logo pour afficher l'image
        colLogo.setCellValueFactory(cellData -> {
            Cinema cinema = cellData.getValue();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(50); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            try {
                Blob blob = cinema.getLogo();
                if (blob != null) {
                    Image image = new Image(blob.getBinaryStream());
                    imageView.setImage(image);
                } else {
                    // Afficher une image par défaut si le logo est null
                    Image defaultImage = new Image(getClass().getResourceAsStream("default_logo.png"));
                    imageView.setImage(defaultImage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        colStatut.setCellValueFactory(new PropertyValueFactory<>("Statut"));

        // Configurer la cellule de la colonne Action
        colAction.setCellFactory(new Callback<TableColumn<Cinema, Void>, TableCell<Cinema, Void>>() {
            @Override
            public TableCell<Cinema, Void> call(TableColumn<Cinema, Void> param) {
                return new TableCell<Cinema, Void>() {
                    private final Button acceptButton = new Button("Accepter");
                    private final Button refuseButton = new Button("Refuser");

                    {
                        acceptButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            // Mettre à jour le statut du cinéma en "Acceptée"
                            cinema.setStatut("Acceptée");
                            // Mettre à jour le statut dans la base de données
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.update(cinema);
                            // Rafraîchir la TableView pour refléter les changements
                            getTableView().refresh();
                        });

                        refuseButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.delete(cinema);
                            getTableView().getItems().remove(cinema);
                        });

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Afficher les boutons dans la cellule de la colonne Action
                            setGraphic(new HBox(acceptButton, refuseButton));
                        }
                    }
                };
            }
        });

        loadCinemas();
    }

    private void loadCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();

        ObservableList<Cinema> cinemaObservableList = FXCollections.observableArrayList(cinemas);

        listCinema.setItems(cinemaObservableList);
    }
}
