package com.esprit.controllers.cinemas;

import com.esprit.enums.CinemaStatus;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.PageRequest;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Configure the cinema TableView for the admin dashboard, including columns,
     * logo rendering, per-row action controls, and then populates the table.
     *
     * <p>
     * Sets up cell value factories, a logo cell renderer, and an action cell that
     * exposes Accept/Refuse or Show Movies controls depending on cinema status.
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
            final String logo = cinema.getLogoUrl();
            if (logo != null && !logo.isEmpty()) {
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Acceptance");
                    alert.setHeaderText("Accept Cinema");
                    alert.setContentText("Are you sure you want to accept this cinema?");
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        Cinema cinema = getTableView().getItems().get(getIndex());
                        cinema.setStatus(CinemaStatus.ACCEPTED.getStatus());
                        CinemaService cinemaService = new CinemaService();
                        cinemaService.update(cinema);
                        getTableView().refresh();
                    }
                });

                refuseButton.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Reject Cinema");
                    dialog.setHeaderText("Reject Cinema");
                    dialog.setContentText("Please enter the reason for rejection:");
                    dialog.showAndWait().ifPresent(reason -> {
                        if (!reason.trim().isEmpty()) {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            cinema.setStatus(CinemaStatus.REFUSED.getStatus());
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.update(cinema);
                            getTableView().refresh();
                        }
                    });
                });

                showMoviesButton.setOnAction(event -> {
                    Cinema cinema = getTableView().getItems().get(getIndex());
                    showFilmsInModal(cinema);
                });
            }

            /**
             * Update the cell's graphic to display action buttons based on the associated
             * cinema's status.
             *
             * If the cell is empty the graphic is cleared. Otherwise, the cell's row's
             * Cinema is inspected:
             * - when the cinema's status equals "Accepted", the "Show Movies" button is
             * shown;
             * - otherwise the "Accept" and "Refuse" buttons are shown.
             *
             * @param item  the cell item (unused for action cells)
             * @param empty true if the cell does not contain data and should be cleared
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    // Récupérer le cinéma associé à cette ligne
                    final Cinema cinema = this.getTableView().getItems().get(this.getIndex());
                    if (CinemaStatus.ACCEPTED.getStatus().equals(cinema.getStatus())) {
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
        // TODO: This logic is wrong
        FilmService filmCinemaService = new FilmService();
        List<Film> films = filmCinemaService.read(new PageRequest(0, 10)).getContent();

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
            Image image = new Image(film.getImageUrl());
            imageView.setImage(image);
        } catch (Exception e) {
            // Use default image if film image fails to load
            Image defaultImage = new Image(getClass().getResourceAsStream("/Logo.png"));
            imageView.setImage(defaultImage);
        }

        Label titleLabel = new Label(film.getTitle());
        titleLabel.getStyleClass().add("film-title");
        titleLabel.setLayoutX(10);
        titleLabel.setLayoutY(210);
        titleLabel.setMaxWidth(180);
        titleLabel.setWrapText(true);

        Label durationLabel = new Label("Duration: " + film.getDurationMin());
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
     * Load all cinemas using the default page request and display them in the
     * cinema table.
     * <p>
     * Fetches cinemas from CinemaService, wraps the result in an ObservableList,
     * and sets it as the items of listCinema.
     *
     * @since 1.0
     */
    private void loadCinemas() {
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemas = cinemaService.read(PageRequest.defaultPage()).getContent();
        final ObservableList<Cinema> cinemaObservableList = FXCollections.observableArrayList(cinemas);
        this.listCinema.setItems(cinemaObservableList);
    }

    /**
     * Retrieve all cinemas using the default page request.
     *
     * @return a list of Cinema objects retrieved from the data source
     */
    private List<Cinema> getAllCinemas() {
        final CinemaService cinemaService = new CinemaService();
        return cinemaService.read(PageRequest.defaultPage()).getContent();
    }

    /**
     * Perform controller initialization: ensure the cinema list is visible, attach
     * the search listener, load cinemas, and configure the table.
     * <p>
     * Initializes UI state after FXML load by making the cinemasList visible (if
     * present), registering a listener on the search field to filter cinemas as
     * text changes, loading cinema data, and setting up the table view.
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
     * Filter the cinemas displayed in the table by name.
     * <p>
     * If `searchText` is empty the full cinema list is restored; otherwise the
     * table
     * is updated to show only cinemas whose name contains `searchText`,
     * case-insensitively.
     *
     * @param searchText text to match against cinema names (case-insensitive); an
     *                   empty string restores the full list
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
     * Reveals the filter panel and populates it with checkboxes for available
     * cinema addresses and statuses.
     * <p>
     * The method dims the main cinema list, makes the filter container visible,
     * clears any previous checkbox state,
     * retrieves unique addresses and statuses, builds labeled VBox sections with
     * corresponding CheckBox controls,
     * adds those sections to the filter container, and shows the container.
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
     * Retrieves unique addresses of all cinemas.
     *
     * @return a list of unique cinema addresses (each address appears only once)
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        final List<Cinema> cinemas = this.getAllCinemas();
        // Extraire les adresses uniques des cinémas
        return cinemas.stream().map(Cinema::getAddress).distinct().collect(Collectors.toList());
    }

    /**
     * Provides predefined cinema statuses used by the dashboard.
     *
     * @return a list containing the statuses "Pending" and "Accepted".
     */
    public List<String> getCinemaStatuses() {
        // Créer une liste de statuts pré-définis
        final List<String> statuses = new ArrayList<>();
        statuses.add(CinemaStatus.PENDING.getStatus());
        statuses.add(CinemaStatus.ACCEPTED.getStatus());
        return statuses;
    }

    /**
     * Filters cinemas by the currently selected addresses and statuses and updates
     * the cinema table with the filtered list.
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
     * Collects the texts of address checkboxes that are currently selected.
     *
     * @return a List<String> containing the text of each selected address checkbox;
     * empty if none are selected.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return this.addressCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
            .collect(Collectors.toList());
    }

    /**
     * Get the labels of status checkboxes that are selected.
     *
     * @return a list containing the text of each selected status checkbox
     */
    private List<String> getSelectedStatuses() {
        // Récupérer les statuts sélectionnés dans l'AnchorPane de filtrage
        return this.statusCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
            .collect(Collectors.toList());
    }

    /**
     * Opens the Event administration window and closes the current window.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the FXML resource cannot be loaded
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
     * Open the film administration window and close the current window.
     * <p>
     * Loads the film administration UI, shows it in a new stage titled "Film
     * Management",
     * and closes the stage that originated the given event.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the FXML resource cannot be loaded
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
     * Open the series administration UI in a new window and close the current
     * window.
     *
     * @param event the action event whose source window will be closed after the
     *              new window is shown
     * @throws IOException if the FXML resource for the series view cannot be loaded
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
     * Open the Product administration UI in a new window and close the window that
     * produced the event.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the Product admin FXML resource cannot be loaded
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
