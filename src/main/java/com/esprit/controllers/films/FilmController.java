package com.esprit.controllers.films;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Time;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.controlsfx.control.CheckComboBox;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.*;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.*;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;
import net.synedra.validatorfx.Validator;

/**
 * Controller class responsible for managing film-related operations in the
 * RAKCHA application.
 * 
 * <p>
 * This controller handles the complete lifecycle of film entities, including
 * creation,
 * reading, updating, and deletion. It provides functionality for managing film
 * details,
 * assigning categories and actors to films, handling image uploads, and
 * implementing
 * advanced search and filtering capabilities.
 * </p>
 * 
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Film CRUD operations with validation</li>
 * <li>Category and actor association management</li>
 * <li>Image upload and management</li>
 * <li>Advanced search and filtering</li>
 * <li>Inline table editing with validation</li>
 * </ul>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class FilmController {
    private static final Logger LOGGER = Logger.getLogger(FilmController.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> yearsCheckBoxes = new ArrayList<>();
    Validator validator;
    private String cloudinaryImageUrl;
    @FXML
    private Button ajouterCinema_Button;
    @FXML
    private TableColumn<Film, Integer> annederalisationFilm_tableColumn;
    @FXML
    private TextArea annederealisationFilm_textArea;
    @FXML
    private TableColumn<Film, String> descriptionFilm_tableColumn;
    @FXML
    private TextArea descriptionFilm_textArea;
    @FXML
    private TableColumn<Film, Time> dureeFilm_tableColumn;
    @FXML
    private TextArea dureeFilm_textArea;
    @FXML
    private TableColumn<Film, Button> Delete_Column;
    @FXML
    private TableView<Film> filmCategory_tableView1;
    @FXML
    private AnchorPane filmCrudInterface;
    @FXML
    private TableColumn<Film, Long> idFilm_tableColumn;
    @FXML
    private TableColumn<Film, CheckComboBox<String>> idacteurFilm_tableColumn;
    @FXML
    private TableColumn<Film, CheckComboBox<String>> idcategoryFilm_tableColumn;
    @FXML
    private TableColumn<Film, CheckComboBox<String>> idcinemaFilm_tableColumn;
    @FXML
    private CheckComboBox<String> idcinemaFilm_comboBox;
    @FXML
    private ImageView imageFilm_ImageView;
    @FXML
    private TableColumn<Film, HBox> imageFilm_tableColumn;
    @FXML
    private TableColumn<Film, String> nomFilm_tableColumn;
    @FXML
    private TextArea nomFilm_textArea;
    @FXML
    private AnchorPane image_view;
    @FXML
    private Button AjouterFilm_Button;
    @FXML
    private CheckComboBox<String> Categorychecj_ComboBox;
    @FXML
    private CheckComboBox<String> Actorcheck_ComboBox1;
    @FXML
    private Button addButton;
    @FXML
    private HBox addHbox;
    private FilteredList<Film> filteredActors;
    @FXML
    private TextField recherche_textField;
    @FXML
    private AnchorPane Anchore_Pane_filtrage1;
    @FXML
    private Button bouttonAnchor_outfilltrer;
    @FXML
    private Button bouttonAnchor_outfilltrer1;

    private FilteredList<Film> filteredFilms;
    private SortedList<Film> sortedFilms;

    /**
     * Initialize UI controls: populate actor, cinema, and category selectors, install per-item tooltips,
     * bind the film table to a filtered view driven by the actor search field, and configure advanced search and sorting.
     *
     * Populates Actorcheck_ComboBox1, idcinemaFilm_comboBox, and Categorychecj_ComboBox with names from their services,
     * installs a tooltip for each combo-box item, replaces the film table items with a filtered list bound to recherche_textField,
     * and invokes setupAdvancedSearch() to configure table search and sorting.
     */
    @FXML
    void initialize() {
        this.readFilmTable();
        final CategoryService cs = new CategoryService();
        final FilmService fs = new FilmService();
        final CinemaService cinemaService = new CinemaService();
        final ActorService actorService = new ActorService();
        PageRequest actorPageRequest = new PageRequest(0, 10);
        final List<Actor> actors = actorService.read(actorPageRequest).getContent();
        final List<String> actorNames = actors.stream().map(Actor::getName).collect(Collectors.toList());
        this.Actorcheck_ComboBox1.getItems().addAll(actorNames);
        this.Actorcheck_ComboBox1.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(this.Actorcheck_ComboBox1, tooltip2); // Set the tooltip for the ComboBox
        }
);
        PageRequest cinemaPageRequest = new PageRequest(0, 10);
        final List<Cinema> cinemaList = cinemaService.read(cinemaPageRequest).getContent();
        final List<String> cinemaNames = cinemaList.stream().map(Cinema::getName).collect(Collectors.toList());
        this.idcinemaFilm_comboBox.getItems().addAll(cinemaNames);
        this.idcinemaFilm_comboBox.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(this.idcinemaFilm_comboBox, tooltip2); // Set the tooltip for the ComboBox
        }
);
        // Populate the CheckComboBox with category names
        final CategoryService categoryService = new CategoryService();
        PageRequest categoryPageRequest = new PageRequest(0, 10);
        final List<Category> categories = categoryService.read(categoryPageRequest).getContent();
        final List<String> categoryNames = categories.stream().map(Category::getName).collect(Collectors.toList());
        this.Categorychecj_ComboBox.getItems().addAll(categoryNames);
        this.filteredActors = new FilteredList<>(this.filmCategory_tableView1.getItems());
        // Réinitialiser la TableView avec la liste filtrée
        this.filmCategory_tableView1.setItems(this.filteredActors);
        // Appliquer le filtre lorsque le texte de recherche change
        this.recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.searchActor(newValue);
        }
);
        setupAdvancedSearch();
    }


    /**
     * Filters the filteredActors list to show only actors whose names contain the given text, case-insensitively.
     *
     * @param searchText the text used to filter actor names; when null or empty, all actors are shown
     */
    private void searchActor(final String searchText) {
        this.filteredActors.setPredicate(actor -> {
            // Si le champ de recherche est vide, afficher tous les acteurs
            if (null == searchText || searchText.isEmpty()) {
                return true;
            }

            // Vérifier si le nom de l'acteur contient le texte de recherche (en ignorant la
            // casse)
            final String lowerCaseFilter = searchText.toLowerCase();
            return actor.getName().toLowerCase().contains(lowerCaseFilter);
        }
);
    }


    /**
     * Sets up live filtering and sorting for the film table and binds the search field to update the filter predicate.
     *
     * Wraps the table's items in a filtered list, exposes that via a sorted list bound to the table comparator,
     * sets the table's items to the sorted list, and installs a listener on the search text field to update the
     * filtered list's predicate when the user changes the search text.
     */
    private void setupAdvancedSearch() {
        filteredFilms = new FilteredList<>(filmCategory_tableView1.getItems(), p -> true);
        sortedFilms = new SortedList<>(filteredFilms);
        sortedFilms.comparatorProperty().bind(filmCategory_tableView1.comparatorProperty());
        filmCategory_tableView1.setItems(sortedFilms);

        recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFilms.setPredicate(createSearchPredicate(newValue));
        }
);
    }


    /**
         * Create a predicate that checks whether a film matches the given search text across name,
         * description, release year, category names, and actor names.
         *
         * @param searchText the text to match; may be null or empty. Matching is case-insensitive.
         * @return `true` if the film contains `searchText` in any of the checked fields (case-insensitive), `false` otherwise
         */
    private Predicate<Film> createSearchPredicate(String searchText) {
        return film -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }


            String lowerCaseFilter = searchText.toLowerCase();

            // Search in multiple fields
            return film.getName().toLowerCase().contains(lowerCaseFilter)
                    || film.getDescription().toLowerCase().contains(lowerCaseFilter)
                    || String.valueOf(film.getReleaseYear()).contains(lowerCaseFilter)
                    || new FilmCategoryService().getCategoryNames(film.getId()).toLowerCase().contains(lowerCaseFilter)
                    || new ActorFilmService().getActorsNames(film.getId()).toLowerCase().contains(lowerCaseFilter);
        }
;
    }


    /**
     * Display an information alert with the given message.
     *
     * @param message the message to show in the alert content
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


    /**
         * Opens a file chooser to pick an image file and sets the chosen image into imageFilm_ImageView.
         *
         * If the user cancels the dialog, the currently displayed image is left unchanged.
         */
    @FXML
    void importFilmImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            final Image selectedImage = new Image(selectedFile.toURI().toString());
            this.imageFilm_ImageView.setImage(selectedImage);
        }

    }


    /**
     * Delete the film with the given ID and refresh the film table view.
     *
     * Shows an information alert confirming the deletion.
     *
     * @param id the film's unique identifier
     */
    void deleteFilm(final Long id) {
        final FilmService fs = new FilmService();
        fs.delete(new Film(id));
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Film supprimée");
        alert.setContentText("Film supprimée !");
        alert.show();
        this.readFilmTable();
    }


    /**
     * Validate the film input fields and, if valid, create a new film and its associations
     * with selected categories, actors, and cinemas, then refresh the table and clear inputs.
     *
     * Displays an information alert on success and validation alerts on user input errors;
     * displays an error alert if insertion fails.
     *
     * @param event the ActionEvent that triggered the insertion (not used)
     */
    @FXML
    void insertFilm(final ActionEvent event) {
        try {
            // Validation des champs requis
            if (this.nomFilm_textArea.getText().isEmpty()) {
                this.showAlert("Le nom du film est requis.");
                return;
            }

            if (null == imageFilm_ImageView.getImage()) {
                this.showAlert("Une image pour le film est requise.");
                return;
            }

            if (this.dureeFilm_textArea.getText().isEmpty()
                    || !this.dureeFilm_textArea.getText().matches("\\d{2}:\\d{2}:\\d{2}")) {
                this.showAlert("La durée du film est requise au format (HH:MM:SS).");
                return;
            }

            if (this.descriptionFilm_textArea.getText().isEmpty()) {
                this.showAlert("La description du film est requise.");
                return;
            }

            if (this.annederealisationFilm_textArea.getText().isEmpty()
                    || !this.annederealisationFilm_textArea.getText().matches("\\d{4}")
                    || 1900 > Integer.parseInt(annederealisationFilm_textArea.getText())) {
                this.showAlert(
                        "L'année de réalisation du film est requise au format (YYYY) et doit être supérieure ou égale à 1900.");
                return;
            }

            if (this.Categorychecj_ComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins une catégorie doit être sélectionnée.");
                return;
            }

            if (this.Actorcheck_ComboBox1.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins un acteur doit être sélectionné.");
                return;
            }

            if (this.idcinemaFilm_comboBox.getCheckModel().getCheckedItems().isEmpty()) {
                this.showAlert("Au moins un cinéma doit être sélectionné.");
                return;
            }

            final String fullPath = this.imageFilm_ImageView.getImage().getUrl();
            final String requiredPath = fullPath.substring(fullPath.indexOf("/img/films/"));
            final URI uri = new URI(requiredPath);
            // Création et insertion des données après validation
            final FilmCategoryService fs = new FilmCategoryService();
            fs.createFilmCategoryAssociation(
                    new Film(this.nomFilm_textArea.getText(), uri.getPath(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText())),
                    this.Categorychecj_ComboBox.getCheckModel().getCheckedItems());

            final ActorFilmService actorfilmService = new ActorFilmService();
            actorfilmService.createFilmActorAssociation(
                    new Film(this.nomFilm_textArea.getText(), this.imageFilm_ImageView.getImage().getUrl(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText())),
                    this.Actorcheck_ComboBox1.getCheckModel().getCheckedItems());
            final FilmCinemaService filmcinemaService = new FilmCinemaService();
            filmcinemaService.createFilmCinemaAssociation(
                    new Film(this.nomFilm_textArea.getText(), this.imageFilm_ImageView.getImage().getUrl(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText())),
                    this.idcinemaFilm_comboBox.getCheckModel().getCheckedItems());
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film ajouté");
            alert.setContentText("Le film a été ajouté avec succès !");
            alert.show();
            this.readFilmTable();
            this.clear();
        } catch (final Exception e) {
            this.showAlert("Erreur lors de l'ajout du film : " + e.getMessage());
        }

    }


    // Méthode pour afficher les alertes

    /**
     * Shows the film creation form when the add-film button triggered the event.
     *
     * @param event the ActionEvent fired by a UI control; if its source is the add-film button, the film form is made visible
     */
    public void switchForm(final ActionEvent event) {
        if (event.getSource() == this.AjouterFilm_Button) {
            this.filmCrudInterface.setVisible(true);
        }

    }


    /**
     * Clears all film input fields in the form.
     *
     * Resets the name, duration, description, and release year text areas to empty strings.
     */
    void clear() {
        this.nomFilm_textArea.setText("");
        // image_view.setVisible(false);
        this.dureeFilm_textArea.setText("");
        this.descriptionFilm_textArea.setText("");
        this.annederealisationFilm_textArea.setText("");
    }


    /**
     * Listen for selection changes in an actor CheckComboBox and apply corresponding actor–film association updates.
     *
     * When actors are checked the method will add association records for those actors; when actors are unchecked it will remove their association records.
     */
    void updateActorlist() {
        final CheckComboBox<Actor> checkComboBox = new CheckComboBox<>(FXCollections.observableArrayList());
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Actor>() {
            /**
             * Applies an observed change in the actor list to the actor–film associations for the current film.
             *
             * For each actor added in the provided Change, creates a link between that actor and the film; for each actor removed, deletes the existing link.
             *
             * @param c the change describing actors that were added or removed
             */
            @Override
            /**
             * Performs onChanged operation.
             *
             * @return the result of the operation
             */
            public void onChanged(final Change<? extends Actor> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (final Actor actor : c.getAddedSubList()) {
                            final String sql = "INSERT INTO actorFilm (actor_id, film_id) VALUES (?, ?)";
                        }

                    }

                    if (c.wasRemoved()) {
                        for (final Actor actor : c.getRemoved()) {
                            // Remove actor from actorFilm table
                            final String sql = "DELETE FROM actorFilm WHERE actor_id = ?";
                        }

                    }

                }

            }

        }
);
    }


    /**
     * Loads the first page of films and displays them in the film table view.
     *
     * Configures the table for editing and the cell factories, then sets the table items
     * to the first 10 films retrieved from the data source.
     */
    void readFilmTable() {
        try {
            this.filmCategory_tableView1.setEditable(true);
            this.setupCellValueFactory();
            this.setupCellFactory();
            this.setupCellOnEditCommit();
            final FilmService filmService = new FilmService();
            PageRequest filmPageRequest = new PageRequest(0, 10);
            final ObservableList<Film> obF = FXCollections
                    .observableArrayList(filmService.read(filmPageRequest).getContent());
            this.filmCategory_tableView1.setItems(obF);
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Configure per-column cell factories to enable inline editing with validation and error tooltips.
     *
     * <p>Installs editable cell factories for film table columns (name, release year, duration, description, etc.),
     * attaches validators that enforce required formats and ranges, and displays validation feedback as tooltips
     * during cell editing. Also hides the id column.</p>
     */
    private void setupCellFactory() {
        this.idFilm_tableColumn.setVisible(false);
        this.nomFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
            /**
             * Create a TableCell for editable film string fields that validates user input and shows an inline error tooltip when invalid.
             *
             * @param param the TableColumn for which the cell is created
             * @return the TableCell configured to enforce that the input is not empty and begins with an uppercase letter; validation errors are shown in a tooltip positioned near the editor
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * Attach a Validator to the cell's inline TextField to enforce that the value is not empty and starts with an uppercase letter, and show or hide an adjacent error tooltip while the cell is being edited.
                     *
                     * When editing starts, this initializes a Validator (if needed), binds it to the TextField's text property, decorates the field with validation, and registers a listener that updates an on-screen Tooltip with the current validation message positioned near the TextField when errors are present; the Tooltip is hidden when the value becomes valid.
                     */
                    @Override
                    /**
                     * Performs startEdit operation.
                     *
                     * @return the result of the operation
                     */
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("nom", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("nom");
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                }
 else if (!Character.isUpperCase(input.charAt(0))) {
                                    c.error("Please start with an uppercase letter.");
                                }

                            }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Update the text field's tooltip to reflect current validation errors and show or hide it near the field.
                                 *
                                 * When validation errors are present, the tooltip is populated with the validator message and displayed near the
                                 * observed text field; when there are no errors any visible tooltip is hidden.
                                 *
                                 * @param observable the observable value being monitored for changes
                                 * @param oldValue the previous text value
                                 * @param newValue the new text value
                                 */
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                        }

                    }

                }
;
            }

        }
);
        this.annederalisationFilm_tableColumn
                .setCellFactory(new Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>>() {
                    /**
                     * Create a table cell that edits a film's release year and provides inline validation with tooltip feedback.
                     *
                     * <p>The cell uses a text editor that enforces the value is a non-empty integer between 1800 and the current year.
                     * When validation fails, a red tooltip containing the validation message is shown adjacent to the editor.</p>
                     *
                     * @param param the table column associated with the created cell
                     * @return the TableCell configured to edit a film's release year; it validates the year (1800..current year)
                     *         and displays validation errors in a tooltip near the editor
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public TableCell<Film, Integer> call(final TableColumn<Film, Integer> param) {
                        return new TextFieldTableCell<Film, Integer>(new IntegerStringConverter()) {
                            private Validator validator;

                            /**
                             * Prepares the cell for inline editing by attaching a year validator and displaying a live tooltip with validation errors.
                             *
                             * The validator requires a non-empty value that parses to an integer between 1800 and the current year. When validation errors are present a red tooltip containing the error message is shown near the editor; the tooltip is hidden when the value becomes valid.
                             */
                            @Override
                            /**
                             * Performs startEdit operation.
                             *
                             * @return the result of the operation
                             */
                            public void startEdit() {
                                super.startEdit();
                                final TextField textField = (TextField) this.getGraphic();
                                if (null != textField && null == validator) {
                                    this.validator = new Validator();
                                    this.validator.createCheck().dependsOn("annederalisation", textField.textProperty())
                                            .withMethod(c -> {
                                                final String input = c.get("annederalisation");
                                                if (null == input || input.trim().isEmpty()) {
                                                    c.error("Input cannot be empty.");
                                                }
 else {
                                                    try {
                                                        final int year = Integer.parseInt(input);
                                                        if (1800 > year || year > Year.now().getValue()) {
                                                            c.error("Please enter a year between 1800 and "
                                                                    + Year.now().getValue());
                                                        }

                                                    } catch (final NumberFormatException e) {
                                                        c.error("Please enter a valid year.");
                                                    }

                                                }

                                            }
).decorates(textField).immediate();
                                    final Window window = getScene().getWindow();
                                    final Tooltip tooltip = new Tooltip();
                                    final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    textField.textProperty().addListener(new ChangeListener<String>() {
                                        /**
                                         * Update the TextField's tooltip to reflect current validator messages and show or hide it.
                                         *
                                         * If the validator reports errors, the tooltip text and style are updated, the tooltip is attached
                                         * to the TextField and shown near the field; if there are no errors, any visible tooltip is hidden.
                                         *
                                         * @param observable the observable value being listened to (the TextField's text)
                                         * @param oldValue   the previous text value
                                         * @param newValue   the new text value that triggered validation and tooltip update
                                         */
                                        @Override
                                        /**
                                         * Performs changed operation.
                                         *
                                         * @return the result of the operation
                                         */
                                        public void changed(final ObservableValue<? extends String> observable,
                                                final String oldValue, final String newValue) {
                                            if (validator.containsErrors()) {
                                                tooltip.setText(validator.createStringBinding().getValue());
                                                tooltip.setStyle("-fx-background-color: #f00;");
                                                textField.setTooltip(tooltip);
                                                textField.getTooltip().show(window, bounds.getMinX(),
                                                        bounds.getMinY() - 30);
                                            }
 else {
                                                if (null != textField.getTooltip()) {
                                                    textField.getTooltip().hide();
                                                }

                                            }

                                        }

                                    }
);
                                }

                            }

                        }
;
                    }

                }
);
        // i
        // dcategoryFilm_tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        this.dureeFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, Time>, TableCell<Film, Time>>() {
            /**
             * Creates a table cell for editing a film's time duration that displays values as HH:MM:SS and validates user input.
             *
             * The cell formats Time values using the `HH:MM:SS` representation, validates edited text against the pattern
             * `([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]`, and shows a tooltip with validation errors when the input is invalid.
             *
             * @param filmcategoryTimeTableColumn the table column that will host the created time-editing cell
             * @return a TableCell configured as a TextFieldTableCell for `Time` with builtin format validation and tooltip error display
             */
            @Override
            /**
             * Performs call operation.
             *
             * @return the result of the operation
             */
            public TableCell<Film, Time> call(final TableColumn<Film, Time> filmcategoryTimeTableColumn) {
                return new TextFieldTableCell<Film, Time>(new StringConverter<Time>() {
                    /**
                     * Get the string representation of the given Time.
                     *
                     * @param time the Time to convert to a string
                     * @return the time formatted as a string (for example "HH:MM:SS")
                     */
                    @Override
                    /**
                     * Performs toString operation.
                     *
                     * @return the result of the operation
                     */
                    public String toString(final Time time) {
                        return time.toString();
                    }


                    /**
                     * Parse a time string in "HH:MM:SS" format.
                     *
                     * @param s the time string in "HH:MM:SS" format (hours:minutes:seconds)
                     * @return a Time representing the parsed time
                     */
                    @Override
                    /**
                     * Performs fromString operation.
                     *
                     * @return the result of the operation
                     */
                    public Time fromString(final String s) {
                        return Time.valueOf(s);
                    }

                }
) {
                    private Validator validator;

                    /**
                     * Begins editing the cell and installs live validation for a duration in HH:MM:SS format.
                     *
                     * While editing, validates the cell's text field against the pattern `HH:MM:SS` and, when the value is empty or
                     * incorrectly formatted, shows a tooltip with the validation message positioned above the field; the tooltip is
                     * hidden when the value becomes valid.
                     */
                    @Override
                    /**
                     * Performs startEdit operation.
                     *
                     * @return the result of the operation
                     */
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck().dependsOn("duree", textField.textProperty()).withMethod(c -> {
                                final String input = c.get("duree");
                                final String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
                                if (null == input || input.trim().isEmpty()) {
                                    c.error("Input cannot be empty.");
                                }
 else if (!input.matches(timeRegex)) {
                                    c.error("Invalid time format. Please enter the time in the format HH:MM:SS (hours:minutes:seconds).");
                                }

                            }
).decorates(textField).immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Update the text field's tooltip to reflect current validation errors and show or hide it near the field.
                                 *
                                 * When validation errors are present, the tooltip is populated with the validator message and displayed near the
                                 * observed text field; when there are no errors any visible tooltip is hidden.
                                 *
                                 * @param observable the observable value being monitored for changes
                                 * @param oldValue the previous text value
                                 * @param newValue the new text value
                                 */
                                @Override
                                /**
                                 * Performs changed operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue, final String newValue) {
                                    FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    }
 else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }

                                    }

                                }

                            }
);
                        }

                    }

                }
;
            }

        }
);
        // nomFilm_tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.descriptionFilm_tableColumn
                .setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
                    /**
                     * Create a table cell for editing film string fields that enforces the edited text to begin with an uppercase letter and displays an inline error tooltip when validation fails.
                     *
                     * @param param the table column for which the cell is created
                     * @return a TableCell that enforces an uppercase-start validation on edited text and shows an inline error tooltip when the value is invalid
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                        return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                            private Validator validator;

                            /**
                             * Begin inline editing of the cell and attach a validator that requires the edited text to be non-empty and to start with an uppercase letter.
                             *
                             * The validator is bound to the cell's TextField and will show a styled tooltip positioned near the field when validation errors are present and hide it when the value becomes valid.
                             */
                            @Override
                            /**
                             * Performs startEdit operation.
                             *
                             * @return the result of the operation
                             */
                            public void startEdit() {
                                super.startEdit();
                                final TextField textField = (TextField) this.getGraphic();
                                if (null != textField && null == validator) {
                                    this.validator = new Validator();
                                    this.validator.createCheck().dependsOn("description", textField.textProperty())
                                            .withMethod(c -> {
                                                final String input = c.get("description");
                                                if (null == input || input.trim().isEmpty()) {
                                                    c.error("Input cannot be empty.");
                                                }
 else if (!Character.isUpperCase(input.charAt(0))) {
                                                    c.error("Please start with an uppercase letter.");
                                                }

                                            }
).decorates(textField).immediate();
                                    final Window window = getScene().getWindow();
                                    final Tooltip tooltip = new Tooltip();
                                    final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    textField.textProperty().addListener(new ChangeListener<String>() {
                                        /**
                                         * Show or hide a validation tooltip for the associated text field based on the validator's current state.
                                         *
                                         * Displays the validator's message near the text field when validation errors exist; hides any shown tooltip when no errors remain.
                                         *
                                         * @param observable the observed String property that triggered this change
                                         * @param oldValue the previous value of the observed property
                                         * @param newValue the new value of the observed property
                                         */
                                        @Override
                                        /**
                                         * Performs changed operation.
                                         *
                                         * @return the result of the operation
                                         */
                                        public void changed(final ObservableValue<? extends String> observable,
                                                final String oldValue, final String newValue) {
                                            FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                            if (validator.containsErrors()) {
                                                tooltip.setText(validator.createStringBinding().getValue());
                                                tooltip.setStyle("-fx-background-color: #f00;");
                                                textField.setTooltip(tooltip);
                                                textField.getTooltip().show(window, bounds.getMinX(),
                                                        bounds.getMinY() - 30);
                                            }
 else {
                                                if (null != textField.getTooltip()) {
                                                    textField.getTooltip().hide();
                                                }

                                            }

                                        }

                                    }
);
                                }

                            }

                        }
;
                    }

                }
);
    }


    /**
     * Configure cell value factories for film table columns to supply observable cell values
     * and interactive controls bound to film data.
     *
     * The method sets up factories for columns including delete button, id, name, image HBox,
     * duration, release year, description, and CheckComboBox editors for categories, actors,
     * and cinemas. Factories produce the appropriate ObservableValue for each cell and attach
     * listeners or handlers that perform the following observable side effects:
     * - invoke deleteFilm when a row's delete button is pressed,
     * - open a file chooser and persist an updated image when the image cell is clicked,
     * - update associated services when category, actor, or cinema selections change.
     */
    private void setupCellValueFactory() {
        this.Delete_Column.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Button>, ObservableValue<Button>>() {
                    /**
                     * Create an observable holding a delete Button for the given film row.
                     *
                     * @param filmcategoryButtonCellDataFeatures the table cell context for the film row; used to obtain the film's id when the button is activated
                     * @return an ObservableValue containing a Button labeled "delete" that removes the associated film when clicked
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Button> call(
                            final TableColumn.CellDataFeatures<Film, Button> filmcategoryButtonCellDataFeatures) {
                        final Button button = new Button("delete");
                        button.getStyleClass().add("sale");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Delete the film associated with the invoked table-cell action.
                             *
                             * @param event the ActionEvent triggered by clicking the table-cell button
                             */
                            @Override
                            /**
                             * Performs handle operation.
                             *
                             * @return the result of the operation
                             */
                            public void handle(final ActionEvent event) {
                                deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                            }

                        }
);
                        return new SimpleObjectProperty<Button>(button);
                    }

                }
);
        this.annederalisationFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    /**
                     * Provides an observable integer representing the film's release year for the given table row.
                     *
                     * @param filmcategoryIntegerCellDataFeatures the cell data features for the row's Film
                     * @return the film's release year as an ObservableValue<Integer>
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Integer> call(
                            final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                        return new SimpleIntegerProperty(
                                filmcategoryIntegerCellDataFeatures.getValue().getReleaseYear()).asObject();
                    }

                }
);
        this.dureeFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
                    /**
                     * Provide an observable holding the film's duration for a table cell.
                     *
                     * @param filmcategoryTimeCellDataFeatures table cell data features whose associated Film supplies the duration
                     * @return an ObservableValue containing the film's duration as a `Time`
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Time> call(
                            final TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                        return new SimpleObjectProperty<Time>(
                                filmcategoryTimeCellDataFeatures.getValue().getDuration());
                    }

                }
);
        this.imageFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, HBox>, ObservableValue<HBox>>() {
                    /**
                     * Create an HBox containing the film's image with a clickable ImageView that lets the user select and persist a replacement image.
                     *
                     * The ImageView is sized for table display. Clicking it opens a file chooser for PNG/JPG files; if the user selects an image, the view is updated, the Film's image URI is set, and the change is persisted via updateFilm(...).
                     *
                     * @param param the table cell data features for the row; provides the Film whose image is displayed and editable
                     * @return an ObservableValue containing an HBox whose ImageView displays the film image and supports selecting and saving a new image
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<HBox> call(final TableColumn.CellDataFeatures<Film, HBox> param) {
                        final HBox hBox = new HBox();
                        try {
                            final ImageView imageView = new ImageView(new Image(param.getValue().getImage()));
                            hBox.getChildren().add(imageView);
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(50);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                /**
                                 * Open a file chooser for PNG/JPG selection, display the chosen image in the controller's ImageView, set the film's image URI to the chosen file, and persist the updated film.
                                 *
                                 * @param event the MouseEvent that triggered the image selection
                                 */
                                @Override
                                /**
                                 * Performs handle operation.
                                 *
                                 * @return the result of the operation
                                 */
                                public void handle(final MouseEvent event) {
                                    try {
                                        final FileChooser fileChooser = new FileChooser();
                                        fileChooser.getExtensionFilters().addAll(
                                                new FileChooser.ExtensionFilter("PNG", "*.png"),
                                                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                                        final File file = fileChooser.showOpenDialog(new Stage());
                                        if (null != file) {
                                            final Image image = new Image(file.toURI().toURL().toString());
                                            imageView.setImage(image);
                                            hBox.getChildren().clear();
                                            hBox.getChildren().add(imageView);
                                            final Film film = param.getValue();
                                            film.setImage(file.toURI().toURL().toString());
                                            updateFilm(film);
                                        }

                                    } catch (final Exception e) {
                                        FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                    }

                                }

                            }
);
                        } catch (final Exception e) {
                            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }

                        return new SimpleObjectProperty<HBox>(hBox);
                    }

                }
);
        this.dureeFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
                    /**
                     * Provide an observable holding the film's duration for a table cell.
                     *
                     * @param filmcategoryTimeCellDataFeatures table cell data features whose associated Film supplies the duration
                     * @return an ObservableValue containing the film's duration as a `Time`
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Time> call(
                            final TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                        return new SimpleObjectProperty<Time>(
                                filmcategoryTimeCellDataFeatures.getValue().getDuration());
                    }

                }
);
        this.descriptionFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                    /**
                     * Exposes the film's description for a table cell as an observable string property.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features whose row's Film provides the description
                     * @return the film's description as an ObservableValue<String>
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getDescription());
                    }

                }
);
        this.annederalisationFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    /**
                     * Provides an observable integer representing the film's release year for the given table row.
                     *
                     * @param filmcategoryIntegerCellDataFeatures the cell data features for the row's Film
                     * @return the film's release year as an ObservableValue<Integer>
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Integer> call(
                            final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                        return new SimpleIntegerProperty(
                                filmcategoryIntegerCellDataFeatures.getValue().getReleaseYear()).asObject();
                    }

                }
);
        this.idcategoryFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Create a CheckComboBox of category names that reflects and updates a film's category associations.
                     *
                     * The CheckComboBox is pre-checked for the categories currently associated with the film provided in the cell data features;
                     * when the user changes selections the film's associations are updated via the FilmCategoryService.
                     *
                     * @param p table cell data features containing the film whose categories should be displayed and edited
                     * @return a SimpleObjectProperty containing the configured CheckComboBox of category names
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> p) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        PageRequest categoryPageRequest = new PageRequest(0, 10);
                        final CategoryService cs = new CategoryService();
                        for (final Category c : cs.read(categoryPageRequest).getContent())
                            l.add(c.getName());
                        checkComboBox.getItems().addAll(l);
                        final FilmCategoryService fcs = new FilmCategoryService();
                        final List<String> ls = Stream.of(fcs.getCategoryNames(p.getValue().getId()).split(", "))
                                .toList();
                        for (final String checkedString : ls) {
                            FilmController.LOGGER.info(checkedString);
                            checkComboBox.getCheckModel().check(checkedString);
                        }

                        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                            /**
                             * Synchronizes a film's category associations with the current checked items in the CheckComboBox.
                             *
                             * @param change the change event for the CheckComboBox's checked items
                             */
                            @Override
                            /**
                             * Performs onChanged operation.
                             *
                             * @return the result of the operation
                             */
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final FilmCategoryService fcs = new FilmCategoryService();
                                fcs.updateCategories(p.getValue(), checkComboBox.getCheckModel().getCheckedItems());
                            }

                        }
);
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }

                }
);
        this.idFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Long>, ObservableValue<Long>>() {
                    /**
                     * Provide an observable value representing the film's id for the current table row.
                     *
                     * @param filmcategoryLongCellDataFeatures cell data features for the current Film row, used to obtain the Film instance
                     * @return an ObservableValue<Long> containing the film's id
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<Long> call(
                            final TableColumn.CellDataFeatures<Film, Long> filmcategoryLongCellDataFeatures) {
                        return new SimpleLongProperty(filmcategoryLongCellDataFeatures.getValue().getId()).asObject();
                    }

                }
);
        this.nomFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                    /**
                     * Provide an observable property representing the film's name for the table cell.
                     *
                     * @param filmcategoryStringCellDataFeatures table cell data features supplying the Film instance for this row
                     * @return an ObservableValue<String> containing the film's name
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getName());
                    }

                }
);
        this.idacteurFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Create a CheckComboBox of actor names for the film represented by the given cell and keep the control synchronized
                     * with that film's actor selections.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features that provide the Film whose actor selection is shown
                     *                                           and updated
                     * @return an ObservableValue containing a CheckComboBox<String> whose checked items reflect and update the film's
                     *         associated actors
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        PageRequest actorPageRequest = new PageRequest(0, 10);
                        final ActorService cs = new ActorService();
                        for (final Actor a : cs.read(actorPageRequest).getContent())
                            l.add(a.getName());
                        checkComboBox.getItems().addAll(l);
                        final ActorFilmService afs = new ActorFilmService();
                        FilmController.LOGGER.info(filmcategoryStringCellDataFeatures.getValue().getId() + " "
                                + afs.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getId()));
                        final List<String> ls = Stream.of(
                                afs.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", "))
                                .toList();
                        FilmController.LOGGER.info("pass: " + filmcategoryStringCellDataFeatures.getValue().getId());
                        for (final String checkedString : ls)
                            checkComboBox.getCheckModel().check(checkedString);
                        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                            /**
                             * Handle updates to a film's actor associations when the CheckComboBox checked items change.
                             *
                             * @param change the Change describing modifications to the checked actor items
                             */
                            @Override
                            /**
                             * Performs onChanged operation.
                             *
                             * @return the result of the operation
                             */
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final ActorFilmService afs = new ActorFilmService();
                                afs.updateActors(filmcategoryStringCellDataFeatures.getValue(),
                                        checkComboBox.getCheckModel().getCheckedItems());
                            }

                        }
);
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }

                }
);
        this.idcinemaFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Create a CheckComboBox populated with all cinema names for a film and persist selection changes to that film's cinema associations.
                     *
                     * The control pre-selects cinemas already linked to the film and updates persistence via FilmCinemaService when the checked items change.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features whose film determines which cinemas are pre-selected and which film will be updated
                     * @return a SimpleObjectProperty containing the configured CheckComboBox; its checked items reflect and update the film's cinema associations
                     */
                    @Override
                    /**
                     * Performs call operation.
                     *
                     * @return the result of the operation
                     */
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        PageRequest pageRequest = new PageRequest(0, 10);
                        final CinemaService cs = new CinemaService();
                        for (final Cinema c : cs.read(pageRequest).getContent())
                            l.add(c.getName());
                        checkComboBox.getItems().addAll(l);
                        final FilmCinemaService cfs = new FilmCinemaService();
                        final List<String> ls = Stream.of(
                                cfs.getCinemaNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", "))
                                .toList();
                        for (final String checkedString : ls)
                            checkComboBox.getCheckModel().check(checkedString);
                        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                            /**
                             * Reacts to changes in the CheckComboBox selection: logs the currently checked items and persists the updated cinema associations for the film.
                             *
                             * @param change the change event describing the updated checked values
                             */
                            @Override
                            /**
                             * Performs onChanged operation.
                             *
                             * @return the result of the operation
                             */
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final FilmCinemaService afs = new FilmCinemaService();
                                afs.updateCinemas(filmcategoryStringCellDataFeatures.getValue(),
                                        checkComboBox.getCheckModel().getCheckedItems());
                            }

                        }
);
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }

                }
);
    }


    /**
     * Attach edit-commit handlers to film table columns so edits to release year,
     * name, description, and duration are applied to the underlying Film and persisted.
     *
     * Each handler updates the edited Film object's corresponding field with the
     * new value and calls updateFilm(...) to persist the change. Exceptions raised
     * during persistence are logged.
     */
    private void setupCellOnEditCommit() {
        this.annederalisationFilm_tableColumn
                .setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Integer>>() {
                    /**
                     * Persist the edited release year for the film represented by the edited table row.
                     *
                     * @param event the cell edit event whose row identifies the Film and whose new value is the updated release year
                     */
                    @Override
                    /**
                     * Performs handle operation.
                     *
                     * @return the result of the operation
                     */
                    public void handle(final TableColumn.CellEditEvent<Film, Integer> event) {
                        try {
                            event.getTableView().getItems().get(event.getTablePosition().getRow())
                                    .setReleaseYear(event.getNewValue());
                            FilmController.this
                                    .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                        } catch (final Exception e) {
                            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }

                    }

                }
);
        this.nomFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            /**
             * Apply an edited name from a table cell to the corresponding Film and persist the change.
             *
             * <p>Sets the Film's name using the cell edit event's new value, then calls the controller's
             * update method to save the modified Film. Any exception during update is logged.</p>
             *
             * @param event the cell edit event containing the edited row position and the new name value
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
                    FilmController.this
                            .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.descriptionFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            /**
             * Persist the edited description to the Film associated with the edited table row.
             *
             * Updates the Film object's description with the cell's new value and invokes persistence.
             *
             * @param event the table cell edit event containing the new description and the edited row position
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setDescription(event.getNewValue());
                    FilmController.this
                            .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
        this.dureeFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Time>>() {
            /**
             * Set the edited duration on the corresponding film row and persist the change.
             *
             * @param event the cell edit event carrying the new duration value and the row position of the edited film
             */
            @Override
            /**
             * Performs handle operation.
             *
             * @return the result of the operation
             */
            public void handle(final TableColumn.CellEditEvent<Film, Time> event) {
                try {
                    event.getTableView().getItems().get(event.getTablePosition().getRow())
                            .setDuration(event.getNewValue());
                    FilmController.this
                            .updateFilm(event.getTableView().getItems().get(event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

            }

        }
);
    }


    /**
     * Persist the given film's changes, display a success or error alert, and refresh the film table.
     *
     * @param film the Film whose updated state should be saved
     */
    void updateFilm(final Film film) {
        // if (imageString != null) { // Vérifier si une image a été sélectionnée
        final Connection connection = null;
        try {
            final FilmService fs = new FilmService();
            fs.update(film);
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film modifiée");
            alert.setContentText("Film modifiée !");
            alert.show();
            this.readFilmTable();
        } catch (final Exception e) {
            this.showAlert("Erreur lors de la modification du Film : " + e.getMessage());
        }

        // }

        this.readFilmTable();
    }


    /**
     * Opens a file chooser to select a PNG or JPG, uploads the chosen file to the configured image storage, and sets imageFilm_ImageView to the uploaded image.
     *
     * <p>On success updates {@code cloudinaryImageUrl} and the ImageView; upload or chooser errors are logged.</p>
     */
    @FXML
    void importImage(final ActionEvent event) {
        // Add try-with-resources to ensure proper cleanup
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.setTitle("Sélectionner une image");
            final File selectedFile = fileChooser.showOpenDialog(null);
            if (null != selectedFile) {
                try {
                    // Use the CloudinaryStorage service to upload the image
                    CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                    cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                    // Display the image in the ImageView
                    final Image selectedImage = new Image(cloudinaryImageUrl);
                    this.imageFilm_ImageView.setImage(selectedImage);

                    LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
                } catch (final IOException e) {
                    LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
                }

            }

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error in file chooser", e);
        }

    }


    /**
     * Switches the current scene to the cinema dashboard view.
     *
     * Loads "/ui/cinemas/DashboardResponsableCinema.fxml" and replaces the window's scene
     * with the loaded view using a scene size of 1280×700.
     *
     * @param event the ActionEvent that triggered the scene switch
     */
    @FXML
    /**
     * Performs switchtoajouterCinema operation.
     *
     * @return the result of the operation
     */
    public void switchtoajouterCinema(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.ajouterCinema_Button.getScene().getWindow();
            final Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

}