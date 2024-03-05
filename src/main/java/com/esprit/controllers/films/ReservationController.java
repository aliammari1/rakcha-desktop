package com.esprit.controllers.films;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.SeanceService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

public class ReservationController {

    @FXML
    private Spinner<Integer> MM;

    @FXML
    private Spinner<Integer> YY;

    @FXML
    private Pane anchorpane_payment;

    @FXML
    private Button back_btn;

    @FXML
    private CheckComboBox<String> checkcomboboxseance_res;

    @FXML
    private ComboBox<String> cinemacombox_res;

    @FXML
    private TextField client_name;

    @FXML
    private Spinner<Integer> cvc;

    @FXML
    private TextField email;

    @FXML
    private TextField num_card;

    @FXML
    private Button pay_btn;

    @FXML
    private Label total;

    @FXML
    void initialize() {
        anchorpane_payment.getChildren().forEach(node -> {
            node.setDisable(true);
        });
        cinemacombox_res.setDisable(false);
        cinemacombox_res.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                checkcomboboxseance_res.setDisable(false);
                List<Seance> seances = new SeanceService().readLoujain(0);
                for (int i = 0; i < seances.size(); i++)
                    checkcomboboxseance_res.getItems().add("Seance " + (i + 1) + " " + seances.get(i).getDate() + " " + seances.get(i).getHD() + "-" + seances.get(i).getHF());
            }
        });
        CinemaService cinemaService = new CinemaService();
        for (Cinema cinema : cinemaService.read()) {
            cinemacombox_res.getItems().add(cinema.getNom());

        }

    }

    @FXML
    void payment(ActionEvent event) {

    }

    @FXML
    void redirectToListReservation(ActionEvent event) {

    }

}
