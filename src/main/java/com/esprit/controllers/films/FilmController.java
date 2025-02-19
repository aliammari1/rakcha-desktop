package com.esprit.controllers.films;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.*;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.films.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import org.controlsfx.control.CheckComboBox;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Predicate;
import javafx.collections.transformation.SortedList;

public class FilmController {
    private static final Logger LOGGER = Logger.getLogger(FilmController.class.getName());
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> yearsCheckBoxes = new ArrayList<>();
    Validator validator;
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
    private TableColumn<Film, Integer> idFilm_tableColumn;
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
     * Populates a ComboBox with actor names, another with cinema names, and a third
     * with
     * category names. It also sets tooltips for each item and filters the actors
     * based
     * on user input in a text field.
     */
    @FXML
    void initialize() {
        this.readFilmTable();
        final CategoryService cs = new CategoryService();
        final FilmService fs = new FilmService();
        final CinemaService cinemaService = new CinemaService();
        final ActorService actorService = new ActorService();
        final List<Actor> actors = actorService.read();
        final List<String> actorNames = actors.stream().map(Actor::getNom).collect(Collectors.toList());
        this.Actorcheck_ComboBox1.getItems().addAll(actorNames);
        this.Actorcheck_ComboBox1.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(this.Actorcheck_ComboBox1, tooltip2); // Set the tooltip for the ComboBox
        });
        final List<Cinema> cinemaList = cinemaService.read();
        final List<String> cinemaNames = cinemaList.stream().map(Cinema::getNom).collect(Collectors.toList());
        this.idcinemaFilm_comboBox.getItems().addAll(cinemaNames);
        this.idcinemaFilm_comboBox.getItems().forEach(item -> {
            final Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(this.idcinemaFilm_comboBox, tooltip2); // Set the tooltip for the ComboBox
        });
        // Populate the CheckComboBox with category names
        final CategoryService categoryService = new CategoryService();
        final List<Category> categories = categoryService.read();
        final List<String> categoryNames = categories.stream().map(Category::getNom).collect(Collectors.toList());
        this.Categorychecj_ComboBox.getItems().addAll(categoryNames);
        this.filteredActors = new FilteredList<>(this.filmCategory_tableView1.getItems());
        // Réinitialiser la TableView avec la liste filtrée
        this.filmCategory_tableView1.setItems(this.filteredActors);
        // Appliquer le filtre lorsque le texte de recherche change
        this.recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.searchActor(newValue);
        });
        setupAdvancedSearch();
    }

    /**
     * /**
     * Sets a predicate for the `filteredActors` list to search for an actor based
     * on the
     * given search text. If the search text is empty or null, it returns all actors
     * in
     * the list. Otherwise, it checks if the actor's name contains the lowercase
     * version
     * of the search text, ignoring case.
     *
     * @param searchText searched text, which is used to filter the `filteredActors`
     *                   list
     *                   by checking if an actor's name contains the search text in
     *                   lower case.
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
            return actor.getNom().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void setupAdvancedSearch() {
        filteredFilms = new FilteredList<>(filmCategory_tableView1.getItems(), p -> true);
        sortedFilms = new SortedList<>(filteredFilms);
        sortedFilms.comparatorProperty().bind(filmCategory_tableView1.comparatorProperty());
        filmCategory_tableView1.setItems(sortedFilms);

        recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFilms.setPredicate(createSearchPredicate(newValue));
        });
    }

    private Predicate<Film> createSearchPredicate(String searchText) {
        return film -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            // Search in multiple fields
            return film.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    film.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                    String.valueOf(film.getAnnederalisation()).contains(lowerCaseFilter) ||
                    new FilmcategoryService().getCategoryNames(film.getId()).toLowerCase().contains(lowerCaseFilter) ||
                    new ActorfilmService().getActorsNames(film.getId()).toLowerCase().contains(lowerCaseFilter);
        };
    }

    /**
     * /**
     * Generates an information alert displaying a provided message.
     *
     * @param message information to be displayed as the content of the alert.
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
     * Displays a file chooser dialog to select an image file, reads the selected
     * file,
     * and sets the image as the Image component's source.
     *
     * @param event ActionEvent that triggered the execution of the
     *              `importFilmImage()`
     *              method.
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
     * Deletes a film with the specified ID from the database and displays an
     * information
     * alert message.
     *
     * @param id unique identifier of the film to be deleted.
     */
    void deleteFilm(final int id) {
        final FilmService fs = new FilmService();
        fs.delete(new Film(id));
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Film supprimée");
        alert.setContentText("Film supprimée !");
        alert.show();
        this.readFilmTable();
    }

    /**
     * Takes user inputted film data and validates it against predetermined criteria
     * before inserting the film into a database.
     *
     * @param event action event that triggered the method to be executed, and it is
     *              not
     *              used in this case.
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
            final FilmcategoryService fs = new FilmcategoryService();
            fs.create(new Filmcategory(
                    new Category(this.Categorychecj_ComboBox.getCheckModel().getCheckedItems().stream()
                            .collect(Collectors.joining(", ")), ""),
                    new Film(this.nomFilm_textArea.getText(), uri.getPath(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText()))));
            final ActorfilmService actorfilmService = new ActorfilmService();
            actorfilmService.create(new Actorfilm(
                    new Actor(this.Actorcheck_ComboBox1.getCheckModel().getCheckedItems().stream()
                            .collect(Collectors.joining(", ")), "", ""),
                    new Film(this.nomFilm_textArea.getText(), this.imageFilm_ImageView.getImage().getUrl(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText()))));
            final FilmcinemaService filmcinemaService = new FilmcinemaService();
            filmcinemaService.create(new Filmcinema(
                    new Film(this.nomFilm_textArea.getText(), this.imageFilm_ImageView.getImage().getUrl(),
                            Time.valueOf(this.dureeFilm_textArea.getText()), this.descriptionFilm_textArea.getText(),
                            Integer.parseInt(this.annederealisationFilm_textArea.getText())),
                    new Cinema(this.idcinemaFilm_comboBox.getCheckModel().getCheckedItems().stream()
                            .collect(Collectors.joining(", ")), "", null, "", "")));
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
     * Determines the source of an event and sets the visibility of a component
     * based on
     * that source.
     *
     * @param event event that occurred and triggered the execution of the
     *              `switchForm()`
     *              function.
     */
    public void switchForm(final ActionEvent event) {
        if (event.getSource() == this.AjouterFilm_Button) {
            this.filmCrudInterface.setVisible(true);
        }
    }

    /**
     * Removes text and image contents from four text areas: `nomFilm`, `dureeFilm`,
     * `descriptionFilm`, and `annederealisationFilm`.
     */
    void clear() {
        this.nomFilm_textArea.setText("");
        // image_view.setVisible(false);
        this.dureeFilm_textArea.setText("");
        this.descriptionFilm_textArea.setText("");
        this.annederealisationFilm_textArea.setText("");
    }

    /**
     * Monitors changes to a combo box containing a list of actors and updates the
     * actor
     * film table accordingly, by inserting or removing actors based on the changed
     * selection.
     */
    void updateActorlist() {
        final CheckComboBox<Actor> checkComboBox = new CheckComboBox<>(FXCollections.observableArrayList());
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Actor>() {
            /**
             * Is called whenever the change to the given actors occurs. It processes the
             * changes
             * by inserting or removing actors from the actorFilm table based on the added
             * and
             * removed actors respectively.
             *
             * @param c Change object that contains a list of Actor objects, which are used
             *          to
             *          perform operations such as inserting or removing actors from the
             *          actorFilm table
             *          in the database.
             */
            @Override
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
        });
    }

    /**
     * Populates an ObservableList of Films based on data read from the FilmService
     * API
     * and displays them in a tableView.
     */
    void readFilmTable() {
        try {
            this.filmCategory_tableView1.setEditable(true);
            this.setupCellValueFactory();
            this.setupCellFactory();
            this.setupCellOnEditCommit();
            final FilmService filmService = new FilmService();
            final ObservableList<Film> obF = FXCollections.observableArrayList(filmService.read());
            this.filmCategory_tableView1.setItems(obF);
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Sets up cell factories for the table columns in a `TableView`. It creates
     * `TextFieldTableCell` instances with validators for each column to check input
     * validity during editing. Tooltips are also set up for each cell to display
     * validation
     * errors.
     */
    private void setupCellFactory() {
        this.idFilm_tableColumn.setVisible(false);
        this.nomFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
            /**
             * Generates a `TextFieldTableCell` with an embedded validator that checks for
             * input
             * validity when editing begins. The validator displays error messages in a
             * tooltip
             * near the input field when it detects errors.
             *
             * @param param TableColumn<Film, String> object that defines the cell's
             *              properties
             *              and behavior.
             *
             * @returns a `TextFieldTableCell` instance with a built-in validator to check
             *          for
             *          empty or invalid input.
             */
            @Override
            public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    /**
                     * 1) calls super's `startEdit`, 2) retrieves and initializes a validator
                     * object, 3)
                     * creates a check constraint for the input field based on its text value, 4)
                     * decorates
                     * the input field with the validator, and 5) sets the immediate execution of
                     * the validation.
                     */
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck()
                                    .dependsOn("nom", textField.textProperty())
                                    .withMethod(c -> {
                                        final String input = c.get("nom");
                                        if (null == input || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!Character.isUpperCase(input.charAt(0))) {
                                            c.error("Please start with an uppercase letter.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Is called when an observable value changes. It checks if there are any errors
                                 * in
                                 * the validator and displays a tooltip with the error message if present.
                                 *
                                 * @param observable ObservableValue of the form <T extends String> that emits
                                 *                   changes
                                 *                   to its value, and it is being passed into the function as a
                                 *                   reference to track
                                 *                   changes to its value.
                                 *
                                 * @param oldValue   previous value of the `observable` before the change
                                 *                   occurred.
                                 *
                                 * @param newValue   newly entered value by the user and is used to determine if
                                 *                   any
                                 *                   validation errors are present, and if so, to display the
                                 *                   corresponding error message
                                 *                   in the tooltip.
                                 */
                                @Override
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue,
                                        final String newValue) {
                                    FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        this.annederalisationFilm_tableColumn
                .setCellFactory(new Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>>() {
                    /**
                     * Generates a `TableCell` instance for a Film entity, with an editable
                     * `TextField`
                     * that validates the user input based on the `annederalisation` property.
                     *
                     * @param param TableColumn<Film, Integer> object that is used to display the
                     *              edited
                     *              value in the table cell.
                     *
                     * @returns a `TextFieldTableCell` instance that provides editing capabilities
                     *          for
                     *          the Film data property.
                     */
                    @Override
                    public TableCell<Film, Integer> call(final TableColumn<Film, Integer> param) {
                        return new TextFieldTableCell<Film, Integer>(new IntegerStringConverter()) {
                            private Validator validator;

                            /**
                             * Creates a validator for an text field and sets its tooltip to display errors.
                             * It
                             * also adds a listener to update the tooltip with the latest error message
                             * whenever
                             * the field's text changes.
                             */
                            @Override
                            public void startEdit() {
                                super.startEdit();
                                final TextField textField = (TextField) this.getGraphic();
                                if (null != textField && null == validator) {
                                    this.validator = new Validator();
                                    this.validator.createCheck()
                                            .dependsOn("annederalisation", textField.textProperty())
                                            .withMethod(c -> {
                                                final String input = c.get("annederalisation");
                                                if (null == input || input.trim().isEmpty()) {
                                                    c.error("Input cannot be empty.");
                                                } else {
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
                                            })
                                            .decorates(textField)
                                            .immediate();
                                    final Window window = getScene().getWindow();
                                    final Tooltip tooltip = new Tooltip();
                                    final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    textField.textProperty().addListener(new ChangeListener<String>() {
                                        /**
                                         * Is called when an observable value changes. It sets a tooltip text based on
                                         * validator
                                         * errors and shows or hides the tooltip depending on the presence of errors.
                                         *
                                         * @param observable ObservableValue of the text field's value, which is being
                                         *                   updated
                                         *                   and processed in the function.
                                         *
                                         * @param oldValue   previous value of the observable variable before the change
                                         *                   occurred.
                                         *
                                         * @param newValue   String value of the observable variable being monitored,
                                         *                   which is
                                         *                   used to update the tooltip text displayed next to the
                                         *                   corresponding text field
                                         *                   when the function is called.
                                         */
                                        @Override
                                        public void changed(final ObservableValue<? extends String> observable,
                                                final String oldValue, final String newValue) {
                                            if (validator.containsErrors()) {
                                                tooltip.setText(validator.createStringBinding().getValue());
                                                tooltip.setStyle("-fx-background-color: #f00;");
                                                textField.setTooltip(tooltip);
                                                textField.getTooltip().show(window, bounds.getMinX(),
                                                        bounds.getMinY() - 30);
                                            } else {
                                                if (null != textField.getTooltip()) {
                                                    textField.getTooltip().hide();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                    }
                });
        // i
        // dcategoryFilm_tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        this.dureeFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, Time>, TableCell<Film, Time>>() {
            /**
             * Generates a `TextFieldTableCell` that displays the time in the format
             * `HH:MM:SS`,
             * validates the input using a custom validator, and provides a tooltip with
             * error
             * messages if the input is invalid.
             *
             * @param filmcategoryTimeTableColumn TableColumn object that provides the table
             *                                    cell
             *                                    with the necessary properties and methods
             *                                    to display and validate the time value.
             *
             * @returns a `TextFieldTableCell` instance with a validator that checks for a
             *          valid
             *          time format.
             */
            @Override
            public TableCell<Film, Time> call(final TableColumn<Film, Time> filmcategoryTimeTableColumn) {
                return new TextFieldTableCell<Film, Time>(new StringConverter<Time>() {
                    /**
                     * Returns the specified `Time` object in a string representation.
                     *
                     * @param time time value that is to be converted into a string.
                     *
                     * @returns a string representation of the input `time` parameter.
                     */
                    @Override
                    public String toString(final Time time) {
                        return time.toString();
                    }

                    /**
                     * Parses a time string and returns a `Time` object.
                     *
                     * @param s 10-character string to be converted into a `Time` object through the
                     *          `ValueOf()` method.
                     *
                     * @returns a `Time` object representing the specified time string.
                     */
                    @Override
                    public Time fromString(final String s) {
                        return Time.valueOf(s);
                    }
                }) {
                    private Validator validator;

                    /**
                     * Is a custom implementation of `StartEditable` that validates user input
                     * against a
                     * regular expression, displaying an error message in a tooltip if the input is
                     * invalid.
                     */
                    @Override
                    public void startEdit() {
                        super.startEdit();
                        final TextField textField = (TextField) this.getGraphic();
                        if (null != textField && null == validator) {
                            this.validator = new Validator();
                            this.validator.createCheck()
                                    .dependsOn("duree", textField.textProperty())
                                    .withMethod(c -> {
                                        final String input = c.get("duree");
                                        final String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
                                        if (null == input || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!input.matches(timeRegex)) {
                                            c.error("Invalid time format. Please enter the time in the format HH:MM:SS (hours:minutes:seconds).");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            final Window window = getScene().getWindow();
                            final Tooltip tooltip = new Tooltip();
                            final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                /**
                                 * Monitors an observable value and updates a tooltip with validation errors if
                                 * present.
                                 *
                                 * @param observable ObservableValue of the text field's value, which is being
                                 *                   observed
                                 *                   and triggered the method execution when its value changes.
                                 *
                                 * @param oldValue   previous value of the observable variable before the change
                                 *                   was made.
                                 *
                                 * @param newValue   new value of the observable variable passed to the
                                 *                   function, which
                                 *                   is used to determine whether an error message should be
                                 *                   displayed and what that
                                 *                   message should be.
                                 */
                                @Override
                                public void changed(final ObservableValue<? extends String> observable,
                                        final String oldValue,
                                        final String newValue) {
                                    FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (null != textField.getTooltip()) {
                                            textField.getTooltip().hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        // nomFilm_tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.descriptionFilm_tableColumn
                .setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
                    /**
                     * Generates a `TextFieldTableCell` instance with a built-in validator that
                     * checks
                     * if the input string starts with an uppercase letter, and displays an error
                     * message
                     * if it doesn't meet the criteria.
                     *
                     * @param param TableColumn<Film, String> that contains the data to be edited.
                     *
                     * @returns a `TextFieldTableCell` instance that provides text input validation.
                     */
                    @Override
                    public TableCell<Film, String> call(final TableColumn<Film, String> param) {
                        return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                            private Validator validator;

                            /**
                             * Sets up a validator that checks if the input is empty or not an uppercase
                             * letter,
                             * and displays an error tooltip when errors are found.
                             */
                            @Override
                            public void startEdit() {
                                super.startEdit();
                                final TextField textField = (TextField) this.getGraphic();
                                if (null != textField && null == validator) {
                                    this.validator = new Validator();
                                    this.validator.createCheck()
                                            .dependsOn("description", textField.textProperty())
                                            .withMethod(c -> {
                                                final String input = c.get("description");
                                                if (null == input || input.trim().isEmpty()) {
                                                    c.error("Input cannot be empty.");
                                                } else if (!Character.isUpperCase(input.charAt(0))) {
                                                    c.error("Please start with an uppercase letter.");
                                                }
                                            })
                                            .decorates(textField)
                                            .immediate();
                                    final Window window = getScene().getWindow();
                                    final Tooltip tooltip = new Tooltip();
                                    final Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    textField.textProperty().addListener(new ChangeListener<String>() {
                                        /**
                                         * Is called whenever the value of an observable changes. It checks if there are
                                         * any
                                         * validation errors and displays a tooltip with the error message if present,
                                         * otherwise
                                         * it hides the tooltip.
                                         *
                                         * @param observable ObservableValue<? extends String> that is being monitored
                                         *                   and
                                         *                   updated in the function, which captures changes to the
                                         *                   value of the property it
                                         *                   is observing.
                                         *
                                         * @param oldValue   previous value of the observable value being observed,
                                         *                   which is
                                         *                   passed as an argument to the `changed()` method for
                                         *                   informational purposes only.
                                         *
                                         * @param newValue   string value that is being updated or replaced in the
                                         *                   `textField`.
                                         */
                                        @Override
                                        public void changed(final ObservableValue<? extends String> observable,
                                                final String oldValue,
                                                final String newValue) {
                                            FilmController.LOGGER.info(String.valueOf(validator.containsErrors()));
                                            if (validator.containsErrors()) {
                                                tooltip.setText(validator.createStringBinding().getValue());
                                                tooltip.setStyle("-fx-background-color: #f00;");
                                                textField.setTooltip(tooltip);
                                                textField.getTooltip().show(window, bounds.getMinX(),
                                                        bounds.getMinY() - 30);
                                            } else {
                                                if (null != textField.getTooltip()) {
                                                    textField.getTooltip().hide();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                    }
                });
    }

    /**
     * Sets up cell value factories for the `id`, `nomFilm`, `idacteurFilm`,
     * `idcinemaFilm`
     * columns of a table. It creates and returns observable values for each column
     * based
     * on their respective data types and uses ListChangeListener to handle changes
     * in
     * the checked items in the `idacteurFilm` and `idcinemaFilm` columns.
     */
    private void setupCellValueFactory() {
        this.Delete_Column.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Button>, ObservableValue<Button>>() {
                    /**
                     * Generates an `ObservableValue` of type `Button` that represents a delete
                     * button
                     * for each film in a `TableColumn`. The button has a sale class and an
                     * on-action
                     * handler that calls the `deleteFilm` function when pressed.
                     *
                     * @param filmcategoryButtonCellDataFeatures `FilmCategoryButtonCellDataFeatures`
                     *                                           class instance, which contains the
                     *                                           film category and button data for
                     *                                           the given
                     *                                           film ID.
                     *
                     * @returns a `SimpleObjectProperty` of a `Button` object with the text "delete"
                     *          and
                     *          the style class "sale".
                     */
                    @Override
                    public ObservableValue<Button> call(
                            final TableColumn.CellDataFeatures<Film, Button> filmcategoryButtonCellDataFeatures) {
                        final Button button = new Button("delete");
                        button.getStyleClass().add("sale");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            /**
                             * Deletes a film from the database when the `ActionEvent` is triggered by
                             * selecting
                             * a film category button cell.
                             *
                             * @param event selection of a film category button.
                             */
                            @Override
                            public void handle(final ActionEvent event) {
                                FilmController.this.deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                            }
                        });
                        return new SimpleObjectProperty<Button>(button);
                    }
                });
        this.annederalisationFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    /**
                     * Takes a `TableColumn.CellDataFeatures` object as input and returns an
                     * `ObservableValue<Integer>` representing the integer value associated with the
                     * input
                     * film category.
                     *
                     * @param filmcategoryIntegerCellDataFeatures integer value of the Film category
                     *                                            column in the given TableColumn.
                     *
                     * @returns an `ObservableValue` of type `Integer`.
                     */
                    @Override
                    public ObservableValue<Integer> call(
                            final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                        return new SimpleIntegerProperty(
                                filmcategoryIntegerCellDataFeatures.getValue().getAnnederalisation()).asObject();
                    }
                });
        this.dureeFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
                    /**
                     * Generates a time value based on the `films` argument, returning a
                     * `SimpleObjectProperty`
                     * instance with the duration.
                     *
                     * @param filmcategoryTimeCellDataFeatures cell data features of a table column,
                     *                                         specifically the `Duree` property of
                     *                                         the Film object.
                     *
                     * @returns a `SimpleObjectProperty` of type `Time` representing the duration of
                     *          the
                     *          film.
                     */
                    @Override
                    public ObservableValue<Time> call(
                            final TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                        return new SimpleObjectProperty<Time>(filmcategoryTimeCellDataFeatures.getValue().getDuree());
                    }
                });
        this.imageFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, HBox>, ObservableValue<HBox>>() {
                    /**
                     * Generates high-quality documentation for code given to it, by creating an
                     * `HBox`
                     * container with an image view displaying the image associated with the `Film`
                     * value.
                     * The image view is resized to 100x50 pixels and adds a click event handler
                     * that
                     * opens a file chooser to select a new image, which is then set as the film's
                     * image.
                     *
                     * @param param Film object that contains the image data to be displayed in the
                     *              HBox.
                     *
                     * @returns an `ObservableValue` of type `HBox` containing a `ImageView` element
                     *          with
                     *          a changed image.
                     */
                    @Override
                    public ObservableValue<HBox> call(final TableColumn.CellDataFeatures<Film, HBox> param) {
                        final HBox hBox = new HBox();
                        try {
                            final ImageView imageView = new ImageView(
                                    new Image(param.getValue().getImage()));
                            hBox.getChildren().add(imageView);
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(50);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                /**
                                 * Allows the user to select an image file from a list of available formats,
                                 * displays
                                 * it in an `ImageView`, and associates it with a `Film` object using its
                                 * `setImage()`
                                 * method.
                                 *
                                 * @param event mouse event that triggered the function, providing the necessary
                                 *              information to handle the corresponding action.
                                 */
                                @Override
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
                                            FilmController.this.updateFilm(film);
                                        }
                                    } catch (final Exception e) {
                                        FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                        return new SimpleObjectProperty<HBox>(hBox);
                    }
                });
        this.dureeFilm_tableColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
                    /**
                     * Generates an `ObservableValue` representing the duration of a film category.
                     *
                     * @param filmcategoryTimeCellDataFeatures cell data features of a Film entity,
                     *                                         specifically its duration or duree.
                     *
                     * @returns a `SimpleObjectProperty` of type `Time` containing the duration
                     *          value of
                     *          the Film.
                     */
                    @Override
                    public ObservableValue<Time> call(
                            final TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                        return new SimpleObjectProperty<Time>(filmcategoryTimeCellDataFeatures.getValue().getDuree());
                    }
                });
        this.descriptionFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                    /**
                     * Generates an ObservableValue of a string by returning the description of the
                     * given
                     * Film category.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features of a table
                     *                                           column
                     *                                           that contains a description of the
                     *                                           film category.
                     *
                     * @returns a simple string property containing the description of the film
                     *          category.
                     */
                    @Override
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getDescription());
                    }
                });
        this.annederalisationFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    /**
                     * Generates an `ObservableValue<Integer>` by transforming the value of
                     * `filmcategoryIntegerCellDataFeatures.getValue()` using a lambda expression
                     * and
                     * returning it as an `Object`.
                     *
                     * @param filmcategoryIntegerCellDataFeatures integer value of the category of a
                     *                                            film.
                     *
                     * @returns an `ObservableValue<Integer>` of a simple integer property.
                     */
                    @Override
                    public ObservableValue<Integer> call(
                            final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                        return new SimpleIntegerProperty(
                                filmcategoryIntegerCellDataFeatures.getValue().getAnnederalisation()).asObject();
                    }
                });
        this.idcategoryFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Generates high-quality documentation for given code by reading categories
                     * from a
                     * service, splitting them into a list, and then displaying them in a check box.
                     *
                     * @param p Film object for which the function is being called, providing the
                     *          necessary
                     *          data for the function to populate the combobox with categories
                     *          associated with
                     *          that film.
                     *
                     * @returns a `SimpleObjectProperty` of a `CheckComboBox` instance, which
                     *          displays
                     *          and updates the categories of a film based on its ID.
                     */
                    @Override
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> p) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        final CategoryService cs = new CategoryService();
                        for (final Category c : cs.read())
                            l.add(c.getNom());
                        checkComboBox.getItems().addAll(l);
                        final FilmcategoryService fcs = new FilmcategoryService();
                        final List<String> ls = Stream.of(fcs.getCategoryNames(p.getValue().getId()).split(", "))
                                .toList();
                        for (final String checkedString : ls) {
                            FilmController.LOGGER.info(checkedString);
                            checkComboBox.getCheckModel().check(checkedString);
                        }
                        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                            /**
                             * Updates Filmcategory Service with the selected category from the ComboBox and
                             * its
                             * checked items.
                             *
                             * @param change Change event that has occurred on the `checkComboBox`,
                             *               providing the
                             *               current state of the selected items.
                             */
                            @Override
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final FilmcategoryService fcs = new FilmcategoryService();
                                fcs.updateCategories(p.getValue(), checkComboBox.getCheckModel().getCheckedItems());
                            }
                        });
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }
                });
        this.idFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
                    /**
                     * Takes a `TableColumn.CellDataFeatures` object as input, returns an
                     * `ObservableValue`
                     * object representing the integer value associated with the cell, and creates a
                     * new
                     * property with that value.
                     *
                     * @param filmcategoryIntegerCellDataFeatures FilmCategory data feature of the
                     *                                            cell
                     *                                            being processed, providing its
                     *                                            integer value.
                     *
                     * @returns an `ObservableValue<Integer>` object representing the id of the film
                     *          category.
                     */
                    @Override
                    public ObservableValue<Integer> call(
                            final TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                        return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getId())
                                .asObject();
                    }
                });
        this.nomFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
                    /**
                     * Generates an observable value representing a string property containing the
                     * film
                     * category, based on the input cell data features.
                     *
                     * @param filmcategoryStringCellDataFeatures `nom` value of a `Film` object,
                     *                                           which
                     *                                           is passed as an argument to the
                     *                                           `call()` method.
                     *
                     * @returns a string representation of the `nom` value of the provided `Film`
                     *          object.
                     */
                    @Override
                    public ObservableValue<String> call(
                            final TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                        return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getNom());
                    }
                });
        this.idacteurFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Generates high-quality documentation for code by populating a CheckComboBox
                     * with
                     * actors' names from two separate services, and setting up a listener to update
                     * the
                     * selection based on changes to the list of checked items.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features of a table
                     *                                           column,
                     *                                           providing the necessary information
                     *                                           to perform the necessary operations
                     *                                           on the
                     *                                           actors' names for the given film
                     *                                           category.
                     *
                     * @returns a `SimpleObjectProperty` of a `CheckComboBox` with pre-populated
                     *          items.
                     */
                    @Override
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        final ActorService cs = new ActorService();
                        for (final Actor a : cs.read())
                            l.add(a.getNom());
                        checkComboBox.getItems().addAll(l);
                        final ActorfilmService afs = new ActorfilmService();
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
                             * Prints out the currently selected actors and updates them in the Actorfilm
                             * Service
                             * using the `updateActors` method.
                             *
                             * @param change Change<? extends String> event that occurred, providing the
                             *               information
                             *               about the changed objects.
                             */
                            @Override
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final ActorfilmService afs = new ActorfilmService();
                                afs.updateActors(filmcategoryStringCellDataFeatures.getValue(),
                                        checkComboBox.getCheckModel().getCheckedItems());
                            }
                        });
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }
                });
        this.idcinemaFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                    /**
                     * Creates a CheckComboBox with cinema names retrieved from the Cinema and
                     * Filmcinema
                     * services, and listens for changes in the selected items. When an item is
                     * checked
                     * or unchecked, it updates the corresponding cinema names in the Filmcinema
                     * service,
                     * which will reflect the change in the UI.
                     *
                     * @param filmcategoryStringCellDataFeatures cell data features of a table
                     *                                           column
                     *                                           that contains the cinema category
                     *                                           string values, which are used to
                     *                                           populate the
                     *                                           CheckComboBox and update the
                     *                                           cinemas in the database.
                     *
                     * @returns a `SimpleObjectProperty` of a `CheckComboBox` object containing the
                     *          selected cinema names.
                     */
                    @Override
                    public ObservableValue<CheckComboBox<String>> call(
                            final TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                        final CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                        final List<String> l = new ArrayList<>();
                        final CinemaService cs = new CinemaService();
                        for (final Cinema c : cs.read())
                            l.add(c.getNom());
                        checkComboBox.getItems().addAll(l);
                        final FilmcinemaService cfs = new FilmcinemaService();
                        final List<String> ls = Stream.of(
                                cfs.getcinemaNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", "))
                                .toList();
                        for (final String checkedString : ls)
                            checkComboBox.getCheckModel().check(checkedString);
                        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                            /**
                             * Is triggered when the state of a combo box changes, and it prints the
                             * currently
                             * selected items from the combo box to the console, and then calls the
                             * `updatecinemas`
                             * method of the `FilmcinemaService` class with the modified cinema data.
                             *
                             * @param change Change<? extends String> object that contains information about
                             *               a
                             *               change to the value of the CheckBox control.
                             */
                            @Override
                            public void onChanged(final Change<? extends String> change) {
                                FilmController.LOGGER.info(checkComboBox.getCheckModel().getCheckedItems().toString());
                                final FilmcinemaService afs = new FilmcinemaService();
                                afs.updatecinemas(filmcategoryStringCellDataFeatures.getValue(),
                                        checkComboBox.getCheckModel().getCheckedItems());
                            }
                        });
                        return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                    }
                });
    }

    /**
     * Sets event handlers for cell editing events on columns related to film
     * details,
     * namely "Annenderalisation", "Nom", "Description", and "Duree". These event
     * handlers
     * call the `handle` method when an edit commit occurs, updating the
     * corresponding
     * film detail field with the new value from the event.
     */
    private void setupCellOnEditCommit() {
        this.annederalisationFilm_tableColumn
                .setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Integer>>() {
                    /**
                     * Is called when a cell editing event occurs in a table displaying films, and
                     * it
                     * updates the film's annederalisation based on the new value entered by the
                     * user.
                     *
                     * @param event CellEditEvent object that contains information about the cell
                     *              being
                     *              edited, including the table position and the new value being
                     *              entered by the user.
                     */
                    @Override
                    public void handle(final TableColumn.CellEditEvent<Film, Integer> event) {
                        try {
                            event.getTableView().getItems().get(
                                    event.getTablePosition().getRow()).setAnnederalisation(event.getNewValue());
                            FilmController.this.updateFilm(event.getTableView().getItems().get(
                                    event.getTablePosition().getRow()));
                        } catch (final Exception e) {
                            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                });
        this.nomFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            /**
             * Handles cell editing events for a table displaying films. It updates the film
             * object's fields with new values and triggers an event to update the film data
             * source.
             *
             * @param event `TableColumn.CellEditEvent` that occurred in the table,
             *              providing
             *              access to information such as the edited cell's position and new
             *              value.
             */
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setNom(event.getNewValue());
                    FilmController.this.updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.descriptionFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            /**
             * Modifies a film's description based on a user edit event in a table view.
             *
             * @param event `TableColumn.CellEditEvent<Film, String>` event generated by the
             *              user's edit action on the cell in the table displaying the Film
             *              objects and their
             *              descriptions.
             */
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDescription(event.getNewValue());
                    FilmController.this.updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        this.dureeFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Time>>() {
            /**
             * Processes a `CellEditEvent` notification by updating the duration of a film
             * item
             * based on the user's input and updating the corresponding film object in the
             * table
             * view.
             *
             * @param event CellEditEvent object that contains information about the editing
             *              event
             *              on a cell in a table, including the row and column of the edited
             *              cell, the original
             *              value and the new value entered by the user.
             */
            @Override
            public void handle(final TableColumn.CellEditEvent<Film, Time> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDuree(event.getNewValue());
                    FilmController.this.updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (final Exception e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Updates a film in the database using the `FilmService`, and displays an alert
     * message with the title "Film modifiée" and content text "Film modifié !". If
     * an
     * error occurs, it shows an alert with the error message. Finally, it reads the
     * film
     * table again to reflect the update.
     *
     * @param film Film object that will be updated.
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
     * Imports an image file selected by the user through a file chooser and saves
     * it to
     * two different directories based on user preference. It then sets the imported
     * image
     * as the image displayable in the `imageFilm_ImageView`.
     *
     * @param event event object that triggered the `importImage()` method to be
     *              called,
     *              providing the source of the action that led to the file
     *              selection and import process.
     */
    @FXML
    void importImage(final ActionEvent event) {
        // Add try-with-resources to ensure proper cleanup
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.setTitle("Sélectionner une image");
            final File selectedFile = fileChooser.showOpenDialog(null);
            if (null != selectedFile) {
                try {
                    final String destinationDirectory1 = "./src/main/resources/img/films/";
                    final String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\films\\";
                    final Path destinationPath1 = Paths.get(destinationDirectory1);
                    final Path destinationPath2 = Paths.get(destinationDirectory2);
                    final String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    final Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                    final Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                    Files.copy(selectedFile.toPath(), destinationFilePath1);
                    Files.copy(selectedFile.toPath(), destinationFilePath2);
                    final Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                    this.imageFilm_ImageView.setImage(selectedImage);
                } catch (final IOException e) {
                    FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } catch (final Exception e) {
            FilmController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Loads an FXML file and displays it as a stage with a specific size.
     *
     * @param event triggered action event that caused the method to be called,
     *              providing
     *              the necessary context for the code inside the method to execute
     *              properly.
     */
    @FXML
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
