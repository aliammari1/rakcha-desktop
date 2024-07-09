package com.esprit.controllers.cinemas;
import com.esprit.models.cinemas.Cinema;
import com.esprit.services.cinemas.CinemaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Is responsible for handling user interactions related to admin dashboards for
 * various applications. It provides functionality to filter and display data from
 * the Cinema, Addresses, Statuses, Events, Movies, and Series modules based on user
 * selections. The controller also handles button clicks to display different views
 * for each module.
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
    private AnchorPane FilterAnchor;
    /**
     * Configures a table to display cinemas, including their name, address, and responsible
     * person. It also sets up buttons for accepting or refusing cinemas, and a button
     * to show movies.
     */
    @FXML
    void afficherCinemas() {
        cinemasList.setVisible(true);
        // Appelée lorsque le fichier FXML est chargé
        // Configurer les cellules des colonnes pour afficher les données
        colCinema.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        // Configurer la cellule de la colonne Logo pour afficher l'image
        colLogo.setCellValueFactory(cellData -> {
            Cinema cinema = cellData.getValue();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(50); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            String logo = cinema.getLogo();
            if (!logo.isEmpty()) {
                Image image = new Image(logo);
                imageView.setImage(image);
            } else {
                // Afficher une image par défaut si le logo est null
                Image defaultImage = new Image(getClass().getResourceAsStream("/Logo.png"));
                imageView.setImage(defaultImage);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });
        colStatut.setCellValueFactory(new PropertyValueFactory<>("Statut"));
        // Configurer la cellule de la colonne Action
        colAction.setCellFactory(new Callback<TableColumn<Cinema, Void>, TableCell<Cinema, Void>>() {
            /**
             * Generates a `TableCell` that displays buttons for accepting or refusing a movie.
             * When a button is pressed, it updates the cinema's status and refreshes the table
             * view to reflect the change.
             * 
             * @param param `TableColumn<Cinema, Void>` object that triggers the function, providing
             * the necessary information for the cell to render properly.
             * 
             * 	- `param`: A `TableColumn` object that represents the column being edited.
             * 	- `getIndex()`: Returns the row index of the item being edited in the table.
             * 
             * @returns a `TableCell` object that displays buttons for accepting or refusing
             * movies based on the cinema's status.
             * 
             * 	- The returned output is an instance of `TableCell`, which represents a cell in
             * a table.
             * 	- The cell contains three buttons: "Accepter", "Refuser", and "Show Movies".
             * 	- The buttons are created using the `Button` class and are added to the cell's
             * graphic using the `setGraphic` method.
             * 	- The `acceptButton`, `refuseButton`, and `showMoviesButton` are private fields
             * in the `TableCell` class that correspond to the buttons added to the cell.
             */
            @Override
            public TableCell<Cinema, Void> call(TableColumn<Cinema, Void> param) {
                return new TableCell<Cinema, Void>() {
                    private final Button acceptButton = new Button("Accepter");
                    private final Button refuseButton = new Button("Refuser");
                    private final Button showMoviesButton = new Button("Show Movies");
                    {
                        acceptButton.getStyleClass().add("delete-btn");
                        acceptButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            // Mettre à jour le statut du cinéma en "Accepted"
                            cinema.setStatut("Accepted");
                            // Mettre à jour le statut dans la base de données
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.update(cinema);
                            // Rafraîchir la TableView pour refléter les changements
                            getTableView().refresh();
                        });
                        refuseButton.getStyleClass().add("delete-btn");
                        refuseButton.setOnAction(event -> {
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            CinemaService cinemaService = new CinemaService();
                            cinemaService.delete(cinema);
                            getTableView().getItems().remove(cinema);
                        });
                        showMoviesButton.getStyleClass().add("delete-btn");
                        showMoviesButton.setOnAction(event -> {
                        });
                    }
                    /**
                     * Updates the graphic displayed by an item in a table based on its empty status and
                     * the status of the associated cinema.
                     * 
                     * @param item item being updated in the `TableView`, which is passed to the superclass's
                     * `updateItem` method for further processing before displaying the appropriate button
                     * or buttons.
                     * 
                     * 	- `item`: A Void object representing an item to be updated.
                     * 	- `empty`: A boolean indicating whether the item is empty or not.
                     * 
                     * @param empty whether the line is empty or not, and controls the display of buttons
                     * for accepting or refusing the movie.
                     */
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Récupérer le cinéma associé à cette ligne
                            Cinema cinema = getTableView().getItems().get(getIndex());
                            if (cinema.getStatut().equals("Accepted")) {
                                // Afficher le bouton "Show Movies" si le statut est "Accepted"
                                setGraphic(showMoviesButton);
                            } else {
                                // Afficher les boutons "Accepter" et "Refuser" si le statut est "En attente"
                                setGraphic(new HBox(acceptButton, refuseButton));
                            }
                        }
                    }
                };
            }
        });
        loadCinemas();
    }
    /**
     * Creates an observable list of cinemas by reading them from a service and setting
     * it as the items of a `ListBox`.
     */
    private void loadCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        ObservableList<Cinema> cinemaObservableList = FXCollections.observableArrayList(cinemas);
        listCinema.setItems(cinemaObservableList);
    }
    /**
     * Retrieves a list of cinemas through the use of the `CinemaService`. The list is
     * then returned.
     * 
     * @returns a list of Cinema objects retrieved from the Cinema Service.
     */
    private List<Cinema> getAllCinemas() {
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemas = cinemaService.read();
        return cinemas;
    }
    /**
     * Adds a listener to the `tfSearch` text field to filter and update the list of
     * cinemas when the user types in it, loads all cinemas initially, and displays them.
     */
    @FXML
    public void initialize() {
        // Ajouter un écouteur de changement pour le champ de recherche
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filtrer la liste des cinémas en fonction du nouveau texte saisi dans le champ de recherche
            filterCinemas(newValue.trim());
        });
        // Charger tous les cinémas initialement
        loadCinemas();
        afficherCinemas();
    }
    /**
     * Filters a list of cinemas based on a search query, updating the displayed list in
     * a TableView.
     * 
     * @param searchText search term used to filter the list of cinemas displayed on the
     * screen.
     */
    private void filterCinemas(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Cinema> filteredList = FXCollections.observableArrayList();
            for (Cinema cinema : listCinema.getItems()) {
                if (cinema.getNom().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(cinema);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            listCinema.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            loadCinemas();
        }
    }
    /**
     * Updates the opacity of a container and makes a filter anchor visible, then clears
     * the lists of check boxes for addresses and statuses, retrieves unique addresses
     * and statuses from the database, creates VBoxes for each address and status, adds
     * them to the filter anchor, and sets the filter anchor's visibility to true.
     * 
     * @param event ActionEvent that triggered the filtrer method, providing the necessary
     * information to update the UI components accordingly.
     * 
     * 	- `event`: an ActionEvent object representing the user's action that triggered
     * the function execution.
     */
    @FXML
    void filtrer(ActionEvent event) {
        cinemasList.setOpacity(0.5);
        FilterAnchor.setVisible(true);
        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> addresses = getCinemaAddresses();
        // Récupérer les statuts uniques depuis la base de données
        List<String> statuses = getCinemaStatuses();
        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Adresse");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : addresses) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(60);
        // Créer des VBox pour les statuts
        VBox statusCheckBoxesVBox = new VBox();
        Label statusLabel = new Label("Statut");
        statusLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        statusCheckBoxesVBox.getChildren().add(statusLabel);
        for (String status : statuses) {
            CheckBox checkBox = new CheckBox(status);
            statusCheckBoxesVBox.getChildren().add(checkBox);
            statusCheckBoxes.add(checkBox);
        }
        statusCheckBoxesVBox.setLayoutX(25);
        statusCheckBoxesVBox.setLayoutY(120);
        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox, statusCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }
    /**
     * Retrieves a list of cinema addresses from a database and extracts unique addresses
     * from the list of cinemas using Stream API.
     * 
     * @returns a list of unique cinema addresses obtained from the database.
     * 
     * 	- The output is a list of strings representing the unique addresses of cinemas.
     * 	- The list is generated by streaming the `cinemas` collection using `map()` and
     * `distinct()` methods to extract the addresses.
     * 	- The `collect()` method is used to collect the distinct addresses into a list.
     */
    public List<String> getCinemaAddresses() {
        // Récupérer tous les cinémas depuis la base de données
        List<Cinema> cinemas = getAllCinemas();
        // Extraire les adresses uniques des cinémas
        List<String> addresses = cinemas.stream()
                .map(Cinema::getAdresse)
                .distinct()
                .collect(Collectors.toList());
        return addresses;
    }
    /**
     * Creates a list of predefined cinema statuses, including "Pending" and "Accepted",
     * and returns it.
     * 
     * @returns a list of predefined cinema statuses: "Pending" and "Accepted".
     * 
     * 	- The list contains 2 pre-defined statuses: "Pending" and "Accepted".
     * 	- Each status is a unique string in the list.
     */
    public List<String> getCinemaStatuses() {
        // Créer une liste de statuts pré-définis
        List<String> statuses = new ArrayList<>();
        statuses.add("Pending");
        statuses.add("Accepted");
        return statuses;
    }
    /**
     * Filters a list of cinemas based on selected addresses and/or statuses, and updates
     * the TableView with the filtered list.
     * 
     * @param event occurrence of an action event, such as a button press or key stroke,
     * that triggers the execution of the `filtrercinema` method.
     * 
     * 	- `Event`: This represents an event object that triggered the function to be executed.
     * 	- `ActionEvent`: This is a specific type of event object that indicates that a
     * button or other control was pressed.
     */
    @FXML
    void filtrercinema(ActionEvent event) {
        cinemasList.setOpacity(1);
        FilterAnchor.setVisible(false);
        // Récupérer les adresses sélectionnées
        List<String> selectedAddresses = getSelectedAddresses();
        // Récupérer les statuts sélectionnés
        List<String> selectedStatuses = getSelectedStatuses();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Cinema> filteredCinemas = getAllCinemas().stream()
                .filter(cinema -> selectedAddresses.isEmpty() || selectedAddresses.contains(cinema.getAdresse()))
                .filter(cinema -> selectedStatuses.isEmpty() || selectedStatuses.contains(cinema.getStatut()))
                .collect(Collectors.toList());
        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Cinema> filteredList = FXCollections.observableArrayList(filteredCinemas);
        listCinema.setItems(filteredList);
    }
    /**
     * Streamlines the selected addresses from an `AnchorPane` of filtering, applies a
     * filter to only include selected checkboxes, and collects the results into a list
     * of strings.
     * 
     * @returns a list of selected addresses represented as strings.
     * 
     * 1/ The output is a list of strings (`List<String>`), indicating that each selected
     * address is represented as a string.
     * 2/ The list is generated using the `stream()`, `filter()`, and `map()` methods of
     * the `Optional` class, which suggests that the function returns a stream of filtered
     * and transformed elements.
     * 3/ The `collect()` method is used to collect the filtered and transformed elements
     * into a list, which is then returned as the output.
     */
    private List<String> getSelectedAddresses() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves the selected statuses from an `AnchorPane` of filtering by streaming the
     * checked checkboxes, filtering the non-checked ones, and collecting the selected
     * statuses as a list.
     * 
     * @returns a list of selected statuses represented as strings.
     * 
     * 	- The list contains only selected statuses as determined by the `isSelected`
     * method of the `CheckBox` class.
     * 	- Each element in the list is a string representing the text of the selected status.
     */
    private List<String> getSelectedStatuses() {
        // Récupérer les statuts sélectionnés dans l'AnchorPane de filtrage
        return statusCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }
    /**
     * Loads an fxml file and displays a stage with a scene, changing the current stage
     * to the new one.
     * 
     * @param event event that triggered the function and provides access to its related
     * data, allowing the code inside the function to interact with it.
     * 
     * 	- `event`: An `ActionEvent` object representing an action performed on the application.
     */
    @FXML
    void afficherEventsAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Event Manegement");
        stage.show();
        currentStage.close();
    }
    /**
     * Loads an FXML file, creates a stage and window for film management, and replaces
     * the current stage with the new one.
     * 
     * @param event event that triggered the function execution, specifically an `ActionEvent`
     * related to the loading of the FXML file.
     * 
     * Event: An event object that represents a user-initiated action or event, such as
     * a button click or a key press.
     */
    @FXML
    void afficherMovieAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterfaceFilm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Film Manegement");
        stage.show();
        currentStage.close();
    }
    /**
     * Loads a FXML file, creates a stage and window for a series management interface,
     * and replaces the current stage with the new one.
     * 
     * @param event event that triggered the execution of the `afficherserieAdmin()`
     * function, which is an action event generated by a user's click on a button or other
     * element in the FXML file.
     * 
     * 	- `event` is an `ActionEvent`, indicating that the method was called as a result
     * of user action.
     */
    @FXML
    void afficherserieAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Serie-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Serie Manegement");
        stage.show();
        currentStage.close();
    }
    /**
     * Loads an fxml file, creates a scene and stage, and replaces the current stage with
     * the new one.
     * 
     * @param event ActionEvent object that triggered the execution of the `AfficherProduitAdmin()`
     * method.
     * 
     * 	- `event`: An `ActionEvent` object representing a user action that triggered the
     * function to execute.
     */
    @FXML
    void AfficherProduitAdmin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Product Manegement");
        stage.show();
        currentStage.close();
    }
}
