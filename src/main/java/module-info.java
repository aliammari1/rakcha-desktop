module com.example.rakcha1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires twilio;
    requires commons.email;
    requires java.desktop;

    opens com.example.rakcha1 to javafx.fxml;
    opens com.example.rakcha1.modeles.series to javafx.base;
    opens com.example.rakcha1.service.series.DTO to javafx.base;
    exports com.example.rakcha1;
    opens com.example.rakcha1.controllers.series to javafx.fxml;
}