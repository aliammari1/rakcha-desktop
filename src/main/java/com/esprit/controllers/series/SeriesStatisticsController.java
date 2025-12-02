package com.esprit.controllers.series;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SeriesStatisticsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SeriesStatisticsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("SeriesStatisticsController initialized");
    }
}
