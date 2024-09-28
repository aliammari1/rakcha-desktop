package com.esprit.tests;

import com.esprit.utils.DataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApp extends Application {
    /**
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }

    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Connection connection = DataSource.getInstance().getConnection();
        if (null == connection) {
            System.exit(1);
        }
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Login.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
