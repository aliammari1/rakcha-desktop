package com.esprit.controllers;

import com.esprit.models.*;
import com.esprit.services.*;
import com.esprit.utils.DataSource;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Time;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmController {
    Blob imageBlob;
    Validator validator;
    private File selectedFile;
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
    private ComboBox<String> idacteurFilm_comboBox;
    @FXML
    private TableColumn<Film, CheckComboBox<String>> idcategoryFilm_tableColumn;
    @FXML
    private TableColumn<Film, Integer> idcinemaFilm_tableColumn;
    @FXML
    private ComboBox<String> idcinemaFilm_comboBox;
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

    @FXML
    void initialize() {
        validator = new Validator();
        TooltipWrapper<Button> signUpWrapper = new TooltipWrapper<>(
                addButton,
                validator.containsErrorsProperty(),
                Bindings.concat("Cannot add Film:\n", validator.createStringBinding())
        );
        addHbox.getChildren().add(signUpWrapper);
        Tooltip tooltip = new Tooltip();
        validator.createCheck()
                .dependsOn("nom", nomFilm_textArea.textProperty())
                .withMethod(c -> {
                    String userName = c.get("nom");
                    if (userName != null && userName.isEmpty())
                        c.error("the string is empty");
                    else if (userName != null && !userName.toLowerCase().equals(userName)) {
                        c.error("nom Please use only lowercase letters.");
                    }
                })
                .decorates(nomFilm_textArea)
                .immediate();

        nomFilm_textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Window window = nomFilm_textArea.getScene().getWindow();
                Bounds bounds = nomFilm_textArea.localToScreen(nomFilm_textArea.getBoundsInLocal());
                if (validator.containsErrors()) {
                    Tooltip tooltip1 = new Tooltip();
                    tooltip1.setText(validator.createStringBinding().getValue());
                    tooltip1.setStyle("-fx-background-color: #f00;");
                    if (nomFilm_textArea.getTooltip() == null)
                        nomFilm_textArea.setTooltip(tooltip1);
                    else
                        nomFilm_textArea.getTooltip().setText(validator.createStringBinding().getValue());
                    nomFilm_textArea.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                } else {
                    if (nomFilm_textArea.getTooltip() != null) {
                        nomFilm_textArea.getTooltip().hide();
                    }
                }
            }
        });

        nomFilm_textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (validator.containsErrors())
                    event.consume();
            }
        });
        validator.createCheck()
                .dependsOn("description", descriptionFilm_textArea.textProperty())
                .withMethod(c -> {
                    String userName = c.get("description");
                    if (userName != null && !userName.toLowerCase().equals(userName)
                    ) {
                        c.error("Please use only lowercase letters.");
                    } else if (userName.isEmpty())
                        c.error("the string is empty");

                })
                .decorates(descriptionFilm_textArea)
                .immediate();

        descriptionFilm_textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Window window = descriptionFilm_textArea.getScene().getWindow();
                Bounds bounds = descriptionFilm_textArea.localToScreen(descriptionFilm_textArea.getBoundsInLocal());
                if (validator.containsErrors()) {
                    Tooltip tooltip1 = new Tooltip();
                    tooltip1.setText(validator.createStringBinding().getValue());
                    tooltip1.setStyle("-fx-background-color: #f00;");
                    if (descriptionFilm_textArea.getTooltip() == null)
                        descriptionFilm_textArea.setTooltip(tooltip1);
                    else
                        descriptionFilm_textArea.getTooltip().setText(validator.createStringBinding().getValue());
                    descriptionFilm_textArea.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                } else {
                    if (descriptionFilm_textArea.getTooltip() != null) {
                        descriptionFilm_textArea.getTooltip().hide();
                    }
                }
            }
        });

        descriptionFilm_textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (validator.containsErrors())
                    event.consume();
            }
        });
        //-------------------------------
        validator.createCheck()
                .dependsOn("duree", dureeFilm_textArea.textProperty())
                .withMethod(c -> {
                    if (!dureeFilm_textArea.getText().isEmpty()) {
                        String input = c.get("duree");
                        String timeRegex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
                        if (input == null || input.trim().isEmpty()) {
                            c.error("Input cannot be empty.");
                        } else if (!input.matches(timeRegex)) {
                            c.error("Invalid time format. Please enter the time in the format HH:MM:SS (hours:minutes:seconds).");
                        }
                    }
                })
                .decorates(dureeFilm_textArea)
                .immediate();

        dureeFilm_textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Window window = dureeFilm_textArea.getScene().getWindow();
                Bounds bounds = dureeFilm_textArea.localToScreen(dureeFilm_textArea.getBoundsInLocal());
                if (validator.containsErrors()) {
                    Tooltip tooltip1 = new Tooltip();
                    tooltip1.setText(validator.createStringBinding().getValue());
                    tooltip1.setStyle("-fx-background-color: #f00;");
                    if (dureeFilm_textArea.getTooltip() == null)
                        dureeFilm_textArea.setTooltip(tooltip1);
                    else
                        dureeFilm_textArea.getTooltip().setText(validator.createStringBinding().getValue());
                    dureeFilm_textArea.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                } else {
                    if (dureeFilm_textArea.getTooltip() != null) {
                        dureeFilm_textArea.getTooltip().hide();
                    }
                }
            }
        });

        dureeFilm_textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (validator.containsErrors())
                    event.consume();
            }
        });

        Categorychecj_ComboBox.checkModelProperty().addListener(new ChangeListener<IndexedCheckModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends IndexedCheckModel<String>> observableValue, IndexedCheckModel<String> stringIndexedCheckModel, IndexedCheckModel<String> t1) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("description", Categorychecj_ComboBox.checkModelProperty())
                        .withMethod(c -> {
                            String userName = c.get("description");
                            if (Categorychecj_ComboBox.getCheckModel().isEmpty()
                            ) {
                                c.error("choose one");
                            } else if (userName.isEmpty())
                                c.error("the string is empty");

                        })
                        .decorates(Categorychecj_ComboBox)
                        .immediate();

            }
        });
        validator.createCheck()
                .dependsOn("annederalisation", annederealisationFilm_textArea.textProperty())
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
                .decorates(dureeFilm_textArea)
                .immediate();

        annederealisationFilm_textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Window window = annederealisationFilm_textArea.getScene().getWindow();
                Bounds bounds = annederealisationFilm_textArea.localToScreen(annederealisationFilm_textArea.getBoundsInLocal());
                if (validator.containsErrors()) {
                    Tooltip tooltip1 = new Tooltip();
                    tooltip1.setText(validator.createStringBinding().getValue());
                    tooltip1.setStyle("-fx-background-color: #f00;");
                    if (annederealisationFilm_textArea.getTooltip() == null)
                        annederealisationFilm_textArea.setTooltip(tooltip1);
                    else
                        annederealisationFilm_textArea.getTooltip().setText(validator.createStringBinding().getValue());
                    annederealisationFilm_textArea.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                } else {
                    if (annederealisationFilm_textArea.getTooltip() != null) {
                        annederealisationFilm_textArea.getTooltip().hide();
                    }
                }
            }
        });

        annederealisationFilm_textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (validator.containsErrors())
                    event.consume();
            }
        });
        //----------------------

        readFilmTable();
        CategoryService cs = new CategoryService();
        FilmService fs = new FilmService();

        ActorService actorService = new ActorService();
        List<Actor> actors = actorService.read();
        List<String> actorNames = actors.stream().map(Actor::getNom).collect(Collectors.toList());
        Actorcheck_ComboBox1.getItems().addAll(actorNames);
        Actorcheck_ComboBox1.getItems().forEach(item -> {
            Tooltip tooltip2 = new Tooltip("Tooltip text for " + item); // Create a tooltip for each item
            Tooltip.install(Actorcheck_ComboBox1, tooltip2); // Set the tooltip for the ComboBox
        });
        for (Film f : fs.read())
            idcinemaFilm_comboBox.getItems().add(String.valueOf(f.getIdcinema()));
        // Populate the CheckComboBox with category names
        CategoryService categoryService = new CategoryService();
        List<Category> categories = categoryService.read();
        List<String> categoryNames = categories.stream().map(Category::getNom).collect(Collectors.toList());
        Categorychecj_ComboBox.getItems().addAll(categoryNames);
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
        selectedFile = fileChooser.showOpenDialog(null);
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
        if (validator.containsErrors())
            System.out.println();
        else {
            if (selectedFile != null) { // Vérifier si une image a été sélectionnée
                Connection connection = null;
                try {
                    // Convertir le fichier en un objet Blob
                    FileInputStream fis = new FileInputStream(selectedFile);
                    connection = DataSource.getInstance().getConnection();
                    Blob imageBlob = connection.createBlob();

                    // Définir le flux d'entrée de l'image dans l'objet Blob
                    try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    // Créer l'objet Cinema avec l'image Blob
                    FilmcategoryService fs = new FilmcategoryService();
                    fs.create(new Filmcategory(new Category(Categorychecj_ComboBox.getCheckModel().getCheckedItems().stream().collect(Collectors.joining(", ")), ""), new Film(nomFilm_textArea.getText(), imageBlob, Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()), Integer.parseInt(idcinemaFilm_comboBox.getValue()))));
                    ActorfilmService actorfilmService = new ActorfilmService();
                    actorfilmService.create(new Actorfilm(new Actor(Actorcheck_ComboBox1.getCheckModel().getCheckedItems().stream().collect(Collectors.joining(", ")), "", ""), new Film(nomFilm_textArea.getText(), imageBlob, Time.valueOf(dureeFilm_textArea.getText()), descriptionFilm_textArea.getText(), Integer.parseInt(annederealisationFilm_textArea.getText()), Integer.parseInt(idcinemaFilm_comboBox.getValue()))));
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Film ajoutée");
                    alert.setContentText("Film ajoutée !");
                    alert.show();
                    readFilmTable();
                    clear();
                } catch (Exception e) {
                    showAlert("Erreur lors de l'ajout du Film : " + e.getMessage());
                }
            } else {
                showAlert("Veuillez sélectionner une image d'abord !");
            }
        }
    }

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
        idcinemaFilm_comboBox.setValue("");
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
            System.out.println(e.getMessage());
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
                                    new Image(new ByteArrayInputStream(param.getValue().getImage().getBinaryStream().readAllBytes())));
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

                                            FileInputStream fis = new FileInputStream(file);
                                            Blob imageBlob = DataSource.getInstance().getConnection().createBlob();
                                            // Définir le flux d'entrée de l'image dans l'objet Blob
                                            try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                                                byte[] buffer = new byte[4096];
                                                int bytesRead;
                                                while ((bytesRead = fis.read(buffer)) != -1) {
                                                    outputStream.write(buffer, 0, bytesRead);
                                                }
                                            }
                                            Film film = param.getValue();
                                            film.setImage(imageBlob);
                                            updateFilm(film);
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
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
                List<String> ls = Stream.of(afs.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getId()).split(", ")).toList();
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
        idcinemaFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Film, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Film, Integer> filmcategoryIntegerCellDataFeatures) {
                return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getIdcinema()).asObject();
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
                    System.out.println(e.getMessage());
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
                    System.out.println(e.getMessage());
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
                    System.out.println(e.getMessage());
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
                    System.out.println(e.getMessage());
                }
            }
        });
        imageFilm_tableColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Film, HBox>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Film, HBox> event) {
                        try {
                            event.getTableView().getItems().get(
                                    event.getTablePosition().getRow()).setImage(
                                    (Blob) event.getNewValue().getChildren().get(0));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });


    }

    void updateFilm(Film film) {
//        if (imageBlob != null) { // Vérifier si une image a été sélectionnée
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
}
