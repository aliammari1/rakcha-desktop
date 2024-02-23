package com.esprit.controllers;

import com.esprit.models.Salle;
import com.esprit.services.SalleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListSalleResponsableController {

    private int cinemaId;

    @FXML
    private ListView<String> salleListView;

    public void initData(int cinemaId) {
        this.cinemaId = cinemaId;
        loadsalles();
    }

    @FXML
    void addSalle(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSalle.fxml"));
        Parent root = loader.load();

        AjouterSalleController controller = loader.getController();
        controller.initData(cinemaId);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private void loadsalles() {
        SalleService salleService = new SalleService();
        List<Salle> salles = salleService.read();

        List<Salle> salles_cinema = salles.stream()
                .filter(salle -> salle.getId_cinema() == cinemaId)
                .collect(Collectors.toList());

        if (salles_cinema.isEmpty()) {
            showAlert("Aucune salle n'est disponible");
            return;
        }

        ObservableList<String> salleInfos = FXCollections.observableArrayList();
        for (Salle salle : salles_cinema) {
            String info = salle.getNom_salle() + " - " + salle.getNb_places() + " places";
            salleInfos.add(info);
        }

        salleListView.setItems(salleInfos);
    }

    @FXML
    void modifiersalle(ActionEvent event) {

    }

    @FXML
    void supprimersalle(ActionEvent event) {
        int selectedIndex = salleListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            Salle salleToDelete = getSalleFromListViewIndex(selectedIndex);

            SalleService salleService = new SalleService();
            salleService.delete(salleToDelete);

            loadsalles();
        } else {
            showAlert("Veuillez sélectionner une salle à supprimer.");
        }
    }

    private Salle getSalleFromListViewIndex(int index) {
        SalleService salleService = new SalleService();
        List<Salle> salles = salleService.read();

        List<Salle> salles_cinema = salles.stream()
                .filter(salle -> salle.getId_cinema() == cinemaId)
                .collect(Collectors.toList());

        return salles_cinema.get(index);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
