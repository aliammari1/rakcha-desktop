package com.esprit.controllers.evenements;

import com.esprit.models.evenements.Participation;
import com.esprit.models.users.Client;
import com.esprit.services.evenements.ParticipationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Date;

public class EventHistoryController {

    ObservableList<Participation> masterData = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Participation, String> tcHistoryName;
    @FXML
    private TableColumn<Participation, Date> tcHistorydd;

    @FXML
    private TableColumn<Participation, Date> tcHistorydf;

    @FXML
    private TableView<Participation> tvHistory;

    @FXML
    void initialize() {

        generateHistory();


    }

    @FXML
    void generateHistory() {
        ParticipationService ps = new ParticipationService();

        tcHistoryName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Participation, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Participation, String> param) {
                // Assuming the Evenement class has a getName() method
                return new SimpleStringProperty(param.getValue().getEvent().getNom());
            }
        });

        tcHistorydd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Participation, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(TableColumn.CellDataFeatures<Participation, Date> param) {
                // Assuming the Evenement class has a getName() method
                return new SimpleObjectProperty<Date>(param.getValue().getEvent().getDateDebut());
            }
        });
        tcHistorydf.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Participation, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(TableColumn.CellDataFeatures<Participation, Date> param) {
                // Assuming the Evenement class has a getName() method
                return new SimpleObjectProperty<Date>(param.getValue().getEvent().getDateFin());
            }
        });

        Stage stage = (Stage) tvHistory.getScene().getWindow();
        masterData.addAll(ps.generateEventHistory(((Client) stage.getUserData()).getId()));

        tvHistory.setItems(masterData);

        // Activer la sélection de cellules
        tvHistory.getSelectionModel().setCellSelectionEnabled(true);

    }

}
