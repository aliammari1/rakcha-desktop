package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
//        UserSMSAPI.sendSMS(28500013, "Rakcha", "hello, this message is from rakcha");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Paymentuser.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
