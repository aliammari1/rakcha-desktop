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
import java.sql.Connection;
import java.sql.Time;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmController {
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


    @FXML
    void initialize() {
        readFilmTable();
        CategoryService cs = new CategoryService();
        FilmService fs = new FilmService();
        CinemaService cinemaService = new CinemaService();
        ActorService actorService = new ActorService();
        List<Actor> actors = actorService.read();
        List<String> actorNames = actors.stream().map(Actor::getNom).collect(Collectors.toList());
        Actorcheck_ComboBox1.getItems().addAll(actorNames);
        Actorcheck_ComboBox1.getItems().forEach(item -> {
            Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(Actorcheck_ComboBox1, tooltip2); // Set the tooltip for the ComboBox
        });
        List<Cinema> cinemaList = cinemaService.read();
        List<String> cinemaNames = cinemaList.stream().map(Cinema::getNom).collect(Collectors.toList());
        idcinemaFilm_comboBox.getItems().addAll(cinemaNames);
        idcinemaFilm_comboBox.getItems().forEach(item -> {
            Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(idcinemaFilm_comboBox, tooltip2); // Set the tooltip for the ComboBox
        });
        // Populate the CheckComboBox with category names
        CategoryService categoryService = new CategoryService();
        List<Category> categories = categoryService.read();
        List<String> categoryNames = categories.stream().map(Category::getNom).collect(Collectors.toList());
        Categorychecj_ComboBox.getItems().addAll(categoryNames);
        filteredActors = new FilteredList<>(filmCategory_tableView1.getItems());

        // Réinitialiser la TableView avec la liste filtrée
        filmCategory_tableView1.setItems(filteredActors);

        // Appliquer le filtre lorsque le texte de recherche change
        recherche_textField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchActor(newValue);
        });
    }


    private void searchActor(String searchText) {
        filteredActors.setPredicate(actor -> {
            // Si le champ de recherche est vide, afficher tous les acteurs
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Vérifier si le nom de l'acteur contient le texte de recherche (en ignorant la casse)
            String lowerCaseFilter = searchText.toLowerCase();
            return actor.getNom().toLowerCase().contains(lowerCaseFilter);
        });
    }


    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void importFilmImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            imageFilm_ImageView.setImage(selectedImage);
        }
    }

    void deleteFilm(int id) {
        FilmService fs = new FilmService();
        fs.delete(new Film(id));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Film supprimée");
        alert.setContentText("Film supprimée !");
        alert.show();
        readFilmTable();
    }

    @FXML
    void insertFilm(ActionEvent event) {
        try {
            // Validation des champs requis
            if (nomFilm_textArea.getText().isEmpty()) {
                showAlert("Le nom du film est requis.");
                return;
            }
            if (imageFilm_ImageView.getImage() == null) {
                showAlert("Une image pour le film est requise.");
                return;
            }
            if (dureeFilm_textArea.getText().isEmpty() || !dureeFilm_textArea.getText().matches("\\d{2}:\\d{2}:\\d{2}")) {
                showAlert("La durée du film est requise au format (HH:MM:SS).");
                return;
            }
            if (descriptionFilm_textArea.getText().isEmpty()) {
                showAlert("La description du film est requise.");
                return;
            }
            if (annederealisationFilm_textArea.getText().isEmpty() || !annederealisationFilm_textArea.getText().matches("\\d{4}")) {
                showAlert("L'année de réalisation du film est requise au format (YYYY).");
                return;
            }
            if (Categorychecj_ComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                showAlert("Au moins une catégorie doit être sélectionnée.");
                return;
            }
            if (Actorcheck_ComboBox1.getCheckModel().getCheckedItems().isEmpty()) {
                showAlert("Au moins un acteur doit être sélectionné.");
                return;
            }
            if (idcinemaFilm_comboBox.getCheckModel().getCheckedItems().isEmpty()) {
                showAlert("Au moins un cinéma doit être sélectionné.");
                return;
            }

            // Création et insertion des données après validation
            FilmcategoryService fs = new FilmcategoryService();
            fs.create(new Filmcategory(new Category(Categorychecj_ComboBox.getCheckModel().getCheckedItems().stream().collect(Collectors.joining(", ")), ""), new Film(nomFilm_textArea.getText(), imageFilm_ImageView.getImage().getUrl(), Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()))));

            ActorfilmService actorfilmService = new ActorfilmService();
            actorfilmService.create(new Actorfilm(new Actor(Actorcheck_ComboBox1.getCheckModel().getCheckedItems().stream().collect(Collectors.joining(", ")), "", ""), new Film(nomFilm_textArea.getText(), imageFilm_ImageView.getImage().getUrl(), Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()))));

            FilmcinemaService filmcinemaService = new FilmcinemaService();
            filmcinemaService.create(new Filmcinema(new Film(nomFilm_textArea.getText(), imageFilm_ImageView.getImage().getUrl(), Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText())), new Cinema(idcinemaFilm_comboBox.getCheckModel().getCheckedItems().stream().collect(Collectors.joining(", ")), "", null, "", "")));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film ajouté");
            alert.setContentText("Le film a été ajouté avec succès !");
            alert.show();

            readFilmTable();
            clear();
        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout du film : " + e.getMessage());
        }
    }

    // Méthode pour afficher les alertes


    public void switchForm(ActionEvent event) {
        if (event.getSource() == AjouterFilm_Button) {
            filmCrudInterface.setVisible(true);
        }
    }

    void clear() {
        nomFilm_textArea.setText("");
        // image_view.setVisible(false);
        dureeFilm_textArea.setText("");
        descriptionFilm_textArea.setText("");
        annederealisationFilm_textArea.setText("");
    }

    void updateActorlist() {
        CheckComboBox<Actor> checkComboBox = new CheckComboBox<>(FXCollections.observableArrayList());
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Actor>() {
            @Override
            public void onChanged(Change<? extends Actor> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Actor actor : c.getAddedSubList()) {
                            String sql = "INSERT INTO actorFilm (actor_id, film_id) VALUES (?, ?)";
                        }
                    }
                    if (c.wasRemoved()) {
                        for (Actor actor : c.getRemoved()) {
                            // Remove actor from actorFilm table
                            String sql = "DELETE FROM actorFilm WHERE actor_id = ?";
                        }
                    }
                }
            }
        });
    }

    void readFilmTable() {
        try {
            filmCategory_tableView1.setEditable(true);
            setupCellValueFactory();
            setupCellFactory();
            setupCellOnEditCommit();
            FilmService filmService = new FilmService();
            ObservableList<Film> obF = FXCollections.observableArrayList(filmService.read());
            filmCategory_tableView1.setItems(obF);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCellFactory() {
        idFilm_tableColumn.setVisible(false);

        nomFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
            @Override
            public TableCell<Film, String> call(TableColumn<Film, String> param) {
                return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("nom", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("nom");
                                        if (input == null || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!Character.isUpperCase(input.charAt(0))) {
                                            c.error("Please start with an uppercase letter.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    System.out.println(validator.containsErrors());
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (textField.getTooltip() != null)
                                            textField.getTooltip().hide();
                                    }
                                }
                            });

                        }
                    }
                };
            }
        });
        annederalisationFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>>() {
            @Override
            public TableCell<Film, Integer> call(TableColumn<Film, Integer> param) {
                return new TextFieldTableCell<Film, Integer>(new IntegerStringConverter()) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("annederalisation", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("annederalisation");
                                        if (input == null || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else {
                                            try {
                                                int year = Integer.parseInt(input);
                                                if (year < 1800 || year > Year.now().getValue()) {
                                                    c.error("Please enter a year between 1800 and " + Year.now().getValue());
                                                }
                                            } catch (NumberFormatException e) {
                                                c.error("Please enter a valid year.");
                                            }
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (textField.getTooltip() != null) {
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
        //i dcategoryFilm_tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn());
        dureeFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, Time>, TableCell<Film, Time>>() {
            @Override
            public TableCell<Film, Time> call(TableColumn<Film, Time> filmcategoryTimeTableColumn) {
                return new TextFieldTableCell<Film, Time>(new StringConverter<Time>() {
                    @Override
                    public String toString(Time time) {
                        return time.toString();
                    }

                    @Override
                    public Time fromString(String s) {
                        return Time.valueOf(s);
                    }
                }) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("duree", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("duree");
                                        String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
                                        if (input == null || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!input.matches(timeRegex)) {
                                            c.error("Invalid time format. Please enter the time in the format HH:MM:SS (hours:minutes:seconds).");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    System.out.println(validator.containsErrors());
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (textField.getTooltip() != null)
                                            textField.getTooltip().hide();
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        //  nomFilm_tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionFilm_tableColumn.setCellFactory(new Callback<TableColumn<Film, String>, TableCell<Film, String>>() {
            @Override
            public TableCell<Film, String> call(TableColumn<Film, String> param) {
                return new TextFieldTableCell<Film, String>(new DefaultStringConverter()) {
                    private Validator validator;

                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("description", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("description");
                                        if (input == null || input.trim().isEmpty()) {
                                            c.error("Input cannot be empty.");
                                        } else if (!Character.isUpperCase(input.charAt(0))) {
                                            c.error("Please start with an uppercase letter.");
                                        }
                                    })
                                    .decorates(textField)
                                    .immediate();
                            Window window = this.getScene().getWindow();
                            Tooltip tooltip = new Tooltip();
                            Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                            textField.textProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    System.out.println(validator.containsErrors());
                                    if (validator.containsErrors()) {
                                        tooltip.setText(validator.createStringBinding().getValue());
                                        tooltip.setStyle("-fx-background-color: #f00;");
                                        textField.setTooltip(tooltip);
                                        textField.getTooltip().show(window, bounds.getMinX(), bounds.getMinY() - 30);
                                    } else {
                                        if (textField.getTooltip() != null)
                                            textField.getTooltip().hide();
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
    }

    private void setupCellValueFactory() {
        Delete_Column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Film, Button> filmcategoryButtonCellDataFeatures) {
                Button button = new Button("delete");
                button.getStyleClass().add("sale");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        deleteFilm(filmcategoryButtonCellDataFeatures.getValue().getId());
                    }
                });
                return new SimpleObjectProperty<Button>(button);
            }
        });
        annederalisationFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getAnnederalisation()).asObject();
            }
        });
        dureeFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
            @Override
            public ObservableValue<Time> call(TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                return new SimpleObjectProperty<Time>(filmcategoryTimeCellDataFeatures.getValue().getDuree());
            }
        });
        imageFilm_tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Film, HBox>, ObservableValue<HBox>>() {
                    @Override
                    public ObservableValue<HBox> call(TableColumn.CellDataFeatures<Film, HBox> param) {
                        HBox hBox = new HBox();
                        try {
                            ImageView imageView = new ImageView(
                                    new Image(param.getValue().getImage()));
                            hBox.getChildren().add(imageView);
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(50);
                            hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    try {
                                        FileChooser fileChooser = new FileChooser();
                                        fileChooser.getExtensionFilters().addAll(
                                                new FileChooser.ExtensionFilter("PNG", "*.png"),
                                                new FileChooser.ExtensionFilter("JPG", "*.jpg"));

                                        File file = fileChooser.showOpenDialog(new Stage());
                                        if (file != null) {
                                            Image image = new Image(file.toURI().toURL().toString());
                                            imageView.setImage(image);
                                            hBox.getChildren().clear();
                                            hBox.getChildren().add(imageView);
                                            Film film = param.getValue();
                                            film.setImage(file.toURI().toURL().toString());
                                            updateFilm(film);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new SimpleObjectProperty<HBox>(hBox);
                    }
                });
        dureeFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Time>, ObservableValue<Time>>() {
            @Override
            public ObservableValue<Time> call(TableColumn.CellDataFeatures<Film, Time> filmcategoryTimeCellDataFeatures) {
                return new SimpleObjectProperty<Time>(filmcategoryTimeCellDataFeatures.getValue().getDuree());
            }
        });
        descriptionFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {
                return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getDescription());
            }
        });
        annederalisationFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getAnnederalisation()).asObject();
            }
        });
        idcategoryFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
            @Override
            public ObservableValue<CheckComboBox<String>> call(TableColumn.CellDataFeatures<Film, CheckComboBox<String>> p) {
                CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                List<String> l = new ArrayList<>();
                CategoryService cs = new CategoryService();
                for (Category c : cs.read())
                    l.add(c.getNom());
                checkComboBox.getItems().addAll(l);
                FilmcategoryService fcs = new FilmcategoryService();
                List<String> ls = Stream.of(fcs.getCategoryNames(p.getValue().getId()).split(", ")).toList();
                for (String checkedString : ls) {
                    System.out.println(checkedString);
                    checkComboBox.getCheckModel().check(checkedString);
                }
                checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> change) {
                        System.out.println(checkComboBox.getCheckModel().getCheckedItems());
                        FilmcategoryService fcs = new FilmcategoryService();
                        fcs.updateCategories(p.getValue(), checkComboBox.getCheckModel().getCheckedItems());
                    }
                });

                return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
            }
        });
        idFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getId()).asObject();
            }
        });
        nomFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Film, String> filmcategoryStringCellDataFeatures) {

                return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getNom());
            }
        });
        idacteurFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
            @Override
            public ObservableValue<CheckComboBox<String>> call(TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                List<String> l = new ArrayList<>();
                ActorService cs = new ActorService();
                for (Actor a : cs.read())
                    l.add(a.getNom());
                checkComboBox.getItems().addAll(l);
                ActorfilmService afs = new ActorfilmService();
                System.out.println(filmcategoryStringCellDataFeatures.getValue().getId() + " " + afs.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getId()));
                List<String> ls = Stream.of(afs.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", ")).toList();
                System.out.println("pass: " + filmcategoryStringCellDataFeatures.getValue().getId());
                for (String checkedString : ls)
                    checkComboBox.getCheckModel().check(checkedString);
                checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> change) {
                        System.out.println(checkComboBox.getCheckModel().getCheckedItems());
                        ActorfilmService afs = new ActorfilmService();
                        afs.updateActors(filmcategoryStringCellDataFeatures.getValue(), checkComboBox.getCheckModel().getCheckedItems());
                    }
                });
                return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
            }
        });
        idcinemaFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
            @Override
            public ObservableValue<CheckComboBox<String>> call(TableColumn.CellDataFeatures<Film, CheckComboBox<String>> filmcategoryStringCellDataFeatures) {
                CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                List<String> l = new ArrayList<>();
                CinemaService cs = new CinemaService();
                for (Cinema c : cs.read())
                    l.add(c.getNom());
                checkComboBox.getItems().addAll(l);
                FilmcinemaService cfs = new FilmcinemaService();
                List<String> ls = Stream.of(cfs.getcinemaNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", ")).toList();
                for (String checkedString : ls)
                    checkComboBox.getCheckModel().check(checkedString);
                checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> change) {
                        System.out.println(checkComboBox.getCheckModel().getCheckedItems());
                        FilmcinemaService afs = new FilmcinemaService();
                        afs.updatecinemas(filmcategoryStringCellDataFeatures.getValue(), checkComboBox.getCheckModel().getCheckedItems());
                    }
                });
                return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
            }
        });
    }

    private void setupCellOnEditCommit() {
        annederalisationFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Film, Integer> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setAnnederalisation(event.getNewValue());
                    updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        nomFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setNom(event.getNewValue());
                    updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        descriptionFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Film, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDescription(event.getNewValue());
                    updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dureeFilm_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Film, Time>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Film, Time> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDuree(event.getNewValue());
                    updateFilm(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    void updateFilm(Film film) {
//        if (imageString != null) { // Vérifier si une image a été sélectionnée
        Connection connection = null;
        try {

            FilmService fs = new FilmService();
            fs.update(film);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Film modifiée");
            alert.setContentText("Film modifiée !");
            alert.show();
            readFilmTable();
        } catch (Exception e) {
            showAlert("Erreur lors de la modification du Film : " + e.getMessage());
        }
//        }
        readFilmTable();

    }

    @FXML
    public void switchtoajouterCinema(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DashboardResponsableCinema.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) ajouterCinema_Button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
