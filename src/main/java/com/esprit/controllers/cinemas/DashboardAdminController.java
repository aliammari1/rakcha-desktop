package com.esprit.controllers.cinemas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.FilmCinemaService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller responsible for handling admin dashboard operations for cinema
 * management.
 * 
 * <p>
 * This controller provides functionality to filter and display data from the
 * Cinema module,
 * including cinema approval/rejection, movie display, and various filtering
 * options.
 * It handles user interactions related to admin dashboards for cinema,
 * addresses, statuses,
 * events, movies, and series modules based on user selections.
 * </p>
 * 
 * @author Esprit Team
 * @version 1.0
 * @since 1.0
 */
public class DashboardAdminController {
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    @FXML
    private AnchorPane cinemasList;
    @FXML
    private TableColumn<Cinema, Void> colAction;
    @FXML
    private TableColumn<Cinema, String> colAdresse;
    @FXML
    private TableColumn<Cinema, String> colCinema;
    @FXML
    private TableColumn<Cinema, ImageView> colLogo;
    @FXML
    private TableColumn<Cinema, String> colResponsable;
    @FXML
    private TableColumn<Cinema, String> colStatut;
    @FXML
    private TableView<Cinema> listCinema;
    @FXML
    private TextField tfSearch;
    @FXML
    private AnchorPane filterAnchor;

    /**
     * Configures and displays the cinema table with all necessary columns and
     * action buttons.
     * 
     * <p>
     * This method sets up a table to display cinemas, including their name,
     * address, and
     * responsible person. It configures action buttons for accepting or refusing
     * cinemas, and a button to show movies. The method also handles the visibility
     * of action buttons based on cinema status.
     * </p>
     * 
     * @since 1.0
     */
    @FXML
    void afficherCinemas() {
        if (cinemasList != null) {
            cinemasList.setVisible(true);
        }
        // Appelée lorsque le fichier FXML est chargé
        // Configurer les cellules des colonnes pour afficher les données
        this.colCinema.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.colAdresse.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.colResponsable.setCellValueFactory(new PropertyValueFactory<>("manager"));
        // Configurer la cellule de la colonne Logo pour afficher l'image
        this.colLogo.setCellValueFactory(cellData -> {
            final Cinema cinema = cellData.getValue();
            final ImageView imageView = new ImageView();
            imageView.setFitWidth(50); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            final String logo = cinema.getLogoPath();
            if (!logo.isEmpty()) {
                final Image image = new Image(logo);
                imageView.setImage(image);
            } else {
                // Afficher une image par défaut si le logo est null
                final Image defaultImage = new Image(this.getClass().getResourceAsStream("/Logo.png"));
                imageView.setImage(defaultImage);
            }
            return new SimpleObjectProperty<>(imageView);
        });
        this.colStatut.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Configurer la cellule de la colonne Action
        this.colAction.setCellFactory((TableColumn<Cinema, Void> param) -> new TableCell<Cinema, Void>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button refuseButton = new Button("Refuser");
            private final Button showMoviesButton = new Button("Show Movies");

            {
                acceptButton.getStyleClass().add("delete-btn");
                refuseButton.getStyleClass().add("delete-btn");
                showMoviesButton.getStyleClass().add("movies-btn");

                acceptButton.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    cinema.setStatus("Accepted");
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    getTableView().refresh();
                });

                refuseButton.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    cinema.setStatus("Refused");
                    CinemaService cinemaService = new CinemaService();
                    cinemaService.update(cinema);
                    getTableView().refresh();
                });

                showMoviesButton.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    showFilmsInModal(cinema);
                });
            }

            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    // Récupérer le cinéma associé à cette ligne
                    final Cinema cinema = this.getTableView().getItems().get(this.getIndex());
                    if ("Accepted".equals(cinema.getStatus())) {
                        // Afficher le bouton "Show Movies" si le statut est "Accepted"
                        this.setGraphic(this.showMoviesButton);
                    } else {
                        // Afficher les boutons "Accepter" et "Refuser" si le statut est "En attente"
                        this.setGraphic(new HBox(10, this.acceptButton, this.refuseButton));
                    }
                }
            }
        });
        this.loadCinemas();
    }

    /**
     * Shows movies for a specific cinema in a modal dialog.
     * 
     * <p>
     * This method creates a new modal dialog window displaying all movies available
     * at the specified cinema. Movies are shown in a flow pane layout with cards
     * containing movie information and images.
     * </p>
     * 
     * @param cinema the cinema for which to display movies
     * @since 1.0
     */
    private void showFilmsInModal(Cinema cinema) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Movies at " + cinema.getName());

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(500);

        FilmCinemaService filmCinemaService = new FilmCinemaService();
        List<Film> films = filmCinemaService.readMoviesForCinema(cinema.getId());

        for (Film film : films) {
            AnchorPane card = createFilmCard(film);
            flowPane.getChildren().add(card);
        }

        Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getResource("/ui/cinemas/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Creates a film card UI component for a given film.
     * 
     * <p>
     * This method generates an AnchorPane containing film information including
     * title, duration, and release year. It also displays the film's poster image
     * if available, or a default image if not.
     * </p>
     * 
     * @param film the film object containing information to display
     * @return an AnchorPane containing the formatted film card
     * @since 1.0
     */
    private AnchorPane createFilmCard(Film film) {
        AnchorPane card = new AnchorPane();
        card.setPrefSize(200, 300);
        card.getStyleClass().add("film-card");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        try {
            Image image = new Image(film.getImage());
            imageView.setImage(image);
        } catch (Exception e) {
            // Use default image if film image fails to load
            Image defaultImage = new Image(getClass().getResourceAsStream("/Logo.png"));
            imageView.setImage(defaultImage);
        }

        Label titleLabel = new Label(film.getName());
        titleLabel.getStyleClass().add("film-title");
        titleLabel.setLayoutX(10);
        titleLabel.setLayoutY(210);
        titleLabel.setMaxWidth(180);
        titleLabel.setWrapText(true);

        Label durationLabel = new Label("Duration: " + film.getDuration());
        durationLabel.getStyleClass().add("film-info");
        durationLabel.setLayoutX(10);
        durationLabel.setLayoutY(240);

        Label yearLabel = new Label("Year: " + film.getReleaseYear());
        yearLabel.getStyleClass().add("film-info");
        yearLabel.setLayoutX(10);
        yearLabel.setLayoutY(260);

        card.getChildren().addAll(imageView, titleLabel, durationLabel, yearLabel);

        return card;
    }

    /**
     * Creates an observable list of cinemas by reading them from a service and
     * setting it as the items of a table view.
     * 
     * @since 1.0
     */
    private void loadCinemas() {
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemas = cinemaService.read();
        final ObservableList<Cinema> cinemaObservableList = FXCollections.observableArrayList(cinemas);
        this.listCinema.setItems(cinemaObservableList);
    }

    /**
     * Retrieves a list of cinemas through the use of the CinemaService.
     *
     * @return a list of Cinema objects retrieved from the Cinema Service
     * @since 1.0
     */
    private List<Cinema> getAllCinemas() {
        final CinemaService cinemaService = new CinemaService();
        return cinemaService.read();
    }

    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     * 
     * <p>
     * This method sets up the search functionality and loads initial cinema data.
     * </p>
     * 
     * @since 1.0
     */
    @FXML
    public void initialize() {
        if (cinemasList != null) {
            cinemasList.setVisible(true);
        }
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filterCinemas(newValue.trim());
            });
        }
        loadCinemas();
        afficherCinemas();
    }

    /**
     * Filters a list of cinemas based on a search query, updating the displayed
     * list in a TableView.
     *
     * @param searchText
     *                   search term used to filter the list of cinemas displayed on
     *                   the
     *                   screen.
     */
    private void filterCinemas(final String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le
            // texte saisi
            final ObservableList<Cinema> filteredList = FXCollections.observableArrayList();
            for (final Cinema cinema : this.listCinema.getItems()) {
                if (cinema.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(cinema);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            this.listCinema.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            this.loadCinemas();
        }
    }

    /**
     * Updates the opacity of a container and makes a filter anchor visible, then
     * clears the lists of check boxes for addresses and statuses, retrieves unique
     * addresses and statuses from the database, creates VBoxes for each address and
     * status, adds them to the filter anchor, and sets the filter anchor's
     * visibility to true.
     *
     * @param event
     *              ActionEvent that triggered the filtrer method, providing the
     *              necessary information to update the UI components accordingly.
     *              <p>
     *              - `event`: an ActionEvent object representing the user's action
     *              that triggered the function execution.
     */
    @FXML
    void filtrer(final ActionEvent event) {
        this.cinemasList.setOpacity(0.5);
        this.filterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        this.addressCheckBoxes.clear();
        this.statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        final List<String> addresses = this.getCinemaAddresses();
        // Récupérer les statuts uniques depuis la base de données
        final List<String> statuses = this.getCinemaStatuses();
        // Créer des VBox pour les adresses
        final VBox addressCheckBoxesVBox = new VBox();
        final Label addressLabel = new Label("Adresse");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (final String address : addresses) {
            final CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            this.addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);
        // Créer des VBox pour les statuts
        final VBox statusCheckBoxesVBox = new VBox();
        final Label statusLabel = new Label("Statut");
        statusLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        statusCheckBoxesVBox.getChildren().add(statusLabel);
        for (final String status : statuses) {
            final CheckBox checkBox = new CheckBox(status);
            statusCheckBoxesVBox.getChildren().add(checkBox);
            this.statusCheckBoxes.add(checkBox);
        }
        statusCheckBoxesVBox.setLayoutX(25);
        statusCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le filterAnchor
        this.filterAnchor.getChildren().addAll(addressCheckBoxesVBox, statusCheckBoxesVBox);
        this.filterAnchor.setVisible(true);
    }

    /**
     * Retrieves a list of cinema addresses from a database and extracts unique
     * addresses from the list of cinemas using Stream API.
     *
     * @returns a list of unique cinema addresses obtained from the database.
     *          <p>
     *          - The output is a list of strings representing the unique addresses
     *          of cinemas. - The list is generated by streaming the `cinemas`
     *          collection using `map()` and `distinct()` methods to extract the
     *          addresses. - The `collect()` method is used to collect the distinct
     *          addresses into a list.
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les adresses uniques des cinémas
        return cinemas.stream().map(Cinema::getAddress).distinct().collect(Collectors.toList());
    }

    /**
     * Creates a list of predefined cinema statuses, including "Pending" and
     * "Accepted", and returns it.
     *
     * @returns a list of predefined cinema statuses: "Pending" and "Accepted".
     *          <p>
     *          - The list contains 2 pre-defined statuses: "Pending" and
     *          "Accepted". - Each status is a unique string in the list.
     */
    public List<String> getCinemaStatuses() {
        // Créer une liste de statuts pré-définis
        final List<String> statuses = new ArrayList<>();
        statuses.add("Pending");
        statuses.add("Accepted");
        return statuses;
    }

    /**
     * Filters a list of cinemas based on selected addresses and/or statuses, and
     * updates the TableView with the filtered list.
     *
     * @param event
     *              occurrence of an action event, such as a button press or key
     *              stroke, that triggers the execution of the `filtrercinema`
     *              method.
     *              <p>
     *              - `Event`: This represents an event object that triggered the
     *              function to be executed. - `ActionEvent`: This is a specific
     *              type
     *              of event object that indicates that a button or other control
     *              was
     *              pressed.
     */
    @FXML
    void filtrercinema(final ActionEvent event) {
        this.cinemasList.setOpacity(1);
        this.filterAnchor.setVisible(false);
        // Récupérer les adresses sélectionnées
        final List<String> selectedAddresses = this.getSelectedAddresses();
        // Récupérer les statuts sélectionnés
        final List<String> selectedStatuses = this.getSelectedStatuses();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        final List<Cinema> filteredCinemas = this.getAllCinemas().stream()
                .filter(cinema -> selectedAddresses.isEmpty() || selectedAddresses.contains(cinema.getAddress()))
                .filter(cinema -> selectedStatuses.isEmpty() || selectedStatuses.contains(cinema.getStatus()))
                .collect(Collectors.toList());
        // Mettre à jour le TableView avec les cinémas filtrés
        final ObservableList<Cinema> filteredList = FXCollections.observableArrayList(filteredCinemas);
        this.listCinema.setItems(filteredList);
    }

    /**
     * Streamlines the selected addresses from an `AnchorPane` of filtering, applies
     * a filter to only include selected checkboxes, and collects the results into a
     * list of strings.
     *
     * @returns a list of selected addresses represented as strings.
     *          <p>
     *          1/ The output is a list of strings (`List<String>`), indicating that
     *          each selected address is represented as a string. 2/ The list is
     *          generated using the `stream()`, `filter()`, and `map()` methods of
     *          the `Optional` class, which suggests that the function returns a
     *          stream of filtered and transformed elements. 3/ The `collect()`
     *          method is used to collect the filtered and transformed elements into
     *          a list, which is then returned as the output.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the selected statuses from an `AnchorPane` of filtering by
     * streaming the checked checkboxes, filtering the non-checked ones, and
     * collecting the selected statuses as a list.
     *
     * @returns a list of selected statuses represented as strings.
     *          <p>
     *          - The list contains only selected statuses as determined by the
     *          `isSelected` method of the `CheckBox` class. - Each element in the
     *          list is a string representing the text of the selected status.
     */
    private List<String> getSelectedStatuses() {
        // Récupérer les statuts sélectionnés dans l'AnchorPane de filtrage
        return this.statusCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    /**
     * Loads an fxml file and displays a stage with a scene, changing the current
     * stage to the new one.
     *
     * @param event
     *              event that triggered the function and provides access to its
     *              related data, allowing the code inside the function to interact
     *              with it.
     *              <p>
     *              - `event`: An `ActionEvent` object representing an action
     *              performed on the application.
     */
    @FXML
    void afficherEventsAdmin(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/DesignEvenementAdmin.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Event Manegement");
        stage.show();
        currentStage.close();
    }

    /**
     * Loads an FXML file, creates a stage and window for film management, and
     * replaces the current stage with the new one.
     *
     * @param event
     *              event that triggered the function execution, specifically an
     *              `ActionEvent` related to the loading of the FXML file.
     *              <p>
     *              Event: An event object that represents a user-initiated action
     *              or
     *              event, such as a button click or a key press.
     */
    @FXML
    void afficherMovieAdmin(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/InterfaceFilm.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Film Manegement");
        stage.show();
        currentStage.close();
    }

    /**
     * Loads a FXML file, creates a stage and window for a series management
     * interface, and replaces the current stage with the new one.
     *
     * @param event
     *              event that triggered the execution of the `afficherserieAdmin()`
     *              function, which is an action event generated by a user's click
     *              on
     *              a button or other element in the FXML file.
     *              <p>
     *              - `event` is an `ActionEvent`, indicating that the method was
     *              called as a result of user action.
     */
    @FXML
    void afficherserieAdmin(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/series/Serie-view.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Serie Manegement");
        stage.show();
        currentStage.close();
    }

    /**
     * Loads an fxml file, creates a scene and stage, and replaces the current stage
     * with the new one.
     *
     * @param event
     *              ActionEvent object that triggered the execution of the
     *              `AfficherProductAdmin()` method.
     *              <p>
     *              - `event`: An `ActionEvent` object representing a user action
     *              that
     *              triggered the function to execute.
     */
    @FXML
    void AfficherProductAdmin(final ActionEvent event) throws IOException {
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui//ui/DesignProductAdmin.fxml.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product Manegement");
        stage.show();
        currentStage.close();
    }
}
