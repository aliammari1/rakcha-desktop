package com.esprit.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esprit.controllers.cinemas.CinemaStatisticsController;
import com.esprit.controllers.films.ActorController;
import com.esprit.controllers.films.FilmController;
import com.esprit.controllers.series.CategorieController;
import com.esprit.models.users.CinemaManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Is responsible for managing the navigation between different views in a
 * cinema management application. It provides buttons for switching between
 * different categories of films, moviesessions, and statistics, as well as a
 * logout button. The controller also sets data for the current user and handles
 * actions such as switching to profiles or films.
 */
public class CinemaManagerSideBarController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CinemaManagerSideBarController.class.getName());
    CinemaManager resp;
    @FXML
    private Button actorButton;
    @FXML
    private Button cinemaButton;
    @FXML
    private Button filmCategorieButton;
    @FXML
    private Button movieButton;
    @FXML
    private Button moviesessionButton;
    @FXML
    private Button statestique_button;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;

    /**
     * Loads and displays an actor's interface using the `FXMLLoader` class, passing
     * in a reference to the actor's data as an argument.
     *
     * @param event
     *              user's action of clicking on the button that triggers the
     *              function.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user
     *              interaction
     *              with the stage button.
     */
    @FXML
    void switchToActor(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceActor.fxml"));
            final ActorController moviesessionController = loader.getController();
            // moviesessionController.setData(resp);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.actorButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file named "/ui/cinemas/statistiques.fxml" using the
     * `FXMLLoader` class, creates a new controller instance from the loaded file,
     * and sets the scene of a stage to display the loaded content.
     *
     * @param event
     *              event of a button click that triggered the function execution.
     *              <p>
     *              - Event is an ActionEvent object, representing a user action
     *              related to the stage or window where the function is called.
     */
    @FXML
    void switchstatestique(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/statistiques.fxml"));
            final CinemaStatisticsController moviesessionController = loader.getController();
            // moviesessionController.setData(resp);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.statestique_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads and displays a FXML file named
     * "/ui/cinemas/DashboardResponsableCinema.fxml" in a JavaFX stage, replacing
     * the existing content.
     *
     * @param event
     *              click event on the cinema button that triggered the function
     *              execution.
     *              <p>
     *              Event: An ActionEvent object representing a button press.
     */
    @FXML
    void switchToCinema(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
        final Parent root = loader.load();
        final Stage stage = (Stage) this.cinemaButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    /**
     * Loads an FXML file and displays its contents on a stage, replacing the
     * current scene.
     *
     * @param event
     *              clicked button that triggered the function call.
     *              <p>
     *              Event: An action event object representing the user's
     *              interaction
     *              with the button.
     */
    @FXML
    void switchToFilmCategorie(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceCategory.fxml"));
            final CategorieController moviesessionController = loader.getController();
            // moviesessionController.setData(resp);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.filmCategorieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file and creates a new scene in a Stage using the FXMLLoader
     * class.
     *
     * @param event
     *              click event that triggered the `switchToMovies()` method to be
     *              executed.
     *              <p>
     *              - Event type: `ActionEvent` indicating a user action on the
     *              movie
     *              button - Target object: `movieButton` providing the context of
     *              the
     *              event
     */
    @FXML
    void switchToMovies(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceFilm.fxml"));
            final FilmController moviesessionController = loader.getController();
            // moviesessionController.setData(resp);
            final Parent root = loader.load();
            final Stage stage = (Stage) this.movieButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads and displays a new FXML document when the "MovieSession" button is
     * clicked, using the `FXMLLoader` class and the `Stage` class to switch between
     * scenes.
     *
     * @param event
     *              occurrence of a button press event that triggers the function to
     *              switch to the `DashboardResponsableCinema.fxml` scene.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user's
     *              action
     *              that triggered the function.
     */
    @FXML
    void switchToMovieSessions(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.moviesessionButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Sets the value of the `resp` field to a provided `CinemaManager` object.
     *
     * @param resp
     *             CinemaManager object that will store the data.
     */
    public void setData(final CinemaManager resp) {
        this.resp = resp;
    }

    /**
     * Is called when the application starts and initializes its resources by taking
     * a URL and resource bundle as arguments.
     *
     * @param location
     *                  URL of the initial resource to be processed by the function.
     * @param resources
     *                  resource bundle for the application, providing localized
     *                  strings
     *                  and formatting data to the initialize method.
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
    }

    /**
     * Loads a new FXML file `/Login.fxml` when the logout button is clicked,
     * replacing the current scene with a new one containing the loaded stage.
     *
     * @param event
     *              event of button click, which triggers the function to switch to
     *              the login screen.
     *              <p>
     *              - `event`: an `ActionEvent` object representing a user action on
     *              the logout button.
     */
    @FXML
    void switchToLogout(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.logoutButton.getScene().getWindow();
            stage.setUserData(null);
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads a new FXML file, replacing the current scene with the new one.
     *
     * @param event
     *              occurrence of a button press event that triggers the execution
     *              of
     *              the `switchToProfile()` method.
     *              <p>
     *              - `event`: An `ActionEvent` object representing the user's
     *              action
     *              of pressing the profile button.
     */
    @FXML
    void switchToProfile(final ActionEvent event) {
        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (final Exception e) {
            CinemaManagerSideBarController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
