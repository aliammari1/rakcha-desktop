package com.esprit.tests;
import java.sql.Connection;

import com.esprit.utils.DataSource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainApp extends Application {
    /** 
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
    /** 
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Connection connection = DataSource.getInstance().getConnection();
        if (connection == null) {
            System.exit(1);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
