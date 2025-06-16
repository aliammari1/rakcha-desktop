package com.esprit;

import java.sql.Connection;

import com.esprit.utils.DataSource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApp class for the RAKCHA JavaFX desktop application.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
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
    /**
     * Performs start operation.
     *
     * @return the result of the operation
     */
    public void start(final Stage primaryStage) throws Exception {
        final Connection connection = DataSource.getInstance().getConnection();
        if (null == connection) {
            System.exit(1);
        }
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
