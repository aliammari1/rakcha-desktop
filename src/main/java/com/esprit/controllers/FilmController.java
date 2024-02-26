package com.esprit.controllers;

import com.esprit.models.*;
import com.esprit.services.*;
import com.esprit.utils.DataSource;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilmController {
    Blob imageBlob;
    private File selectedFile;
    @FXML
    private TableColumn<Filmcategory, Integer> annederalisationFilm_tableColumn;
    @FXML
    private TextArea annederealisationFilm_textArea;
    @FXML
    private TableColumn<Filmcategory, String> descriptionFilm_tableColumn;
    @FXML
    private TextArea descriptionFilm_textArea;
    @FXML
    private TableColumn<Filmcategory, Time> dureeFilm_tableColumn;
    @FXML
    private TextArea dureeFilm_textArea;
    @FXML
    private TableColumn<Filmcategory, Button> Delete_Column;

    @FXML
    private TableView<Filmcategory> filmCategory_tableView1;
    @FXML
    private AnchorPane filmCrudInterface;
    @FXML
    private TableColumn<Filmcategory, Integer> idFilm_tableColumn;
    @FXML
    private TextArea idFilm_textArea;
    @FXML
    private TableColumn<Filmcategory, String> idacteurFilm_tableColumn;
    @FXML
    private ComboBox<String> idacteurFilm_comboBox;
    @FXML
    private TableColumn<Filmcategory, CheckComboBox<String>> idcategoryFilm_tableColumn;
    @FXML
    private TableColumn<Filmcategory, Integer> idcinemaFilm_tableColumn;
    @FXML
    private ComboBox<String> idcinemaFilm_comboBox;
    @FXML
    private ImageView imageFilm_ImageView;
    @FXML
    private TableColumn<Filmcategory, ImageView> imageFilm_tableColumn;
    @FXML
    private TableColumn<Filmcategory, String> nomFilm_tableColumn;
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
    void initialize() {
        readFilmTable();
        CategoryService cs = new CategoryService();
        FilmService fs = new FilmService();

        ActorService actorService = new ActorService();
        List<Actor> actors = actorService.read();
        List<String> actorNames = actors.stream().map(Actor::getNom).collect(Collectors.toList());
        Actorcheck_ComboBox1.getItems().addAll(actorNames);


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
        alert.setTitle("Produit supprimée");
        alert.setContentText("Produit supprimée !");
        alert.show();
        readFilmTable();
        clear();
    }

    @FXML
    void insertFilm(ActionEvent event) {
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

    public void switchForm(ActionEvent event) {
        if (event.getSource() == AjouterFilm_Button) {
            filmCrudInterface.setVisible(true);
        }
    }

    void clear() {
        idFilm_textArea.setText("");
        nomFilm_textArea.setText("");
        // image_view.setVisible(false);
        dureeFilm_textArea.setText("");
        descriptionFilm_textArea.setText("");
        annederealisationFilm_textArea.setText("");
        idacteurFilm_comboBox.setValue("");
        idcinemaFilm_comboBox.setValue("");

    }

    void readFilmTable() {
        try {
            filmCategory_tableView1.setEditable(true);
            idFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Filmcategory, Integer> filmcategoryIntegerCellDataFeatures) {
                    return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getFilmId().getId()).asObject();
                }
            });
            nomFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Filmcategory, String> filmcategoryStringCellDataFeatures) {
                    return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getFilmId().getNom());
                }
            });
            nomFilm_tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            imageFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Filmcategory, ImageView>("image"));
            dureeFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, Time>, ObservableValue<Time>>() {
                @Override
                public ObservableValue<Time> call(TableColumn.CellDataFeatures<Filmcategory, Time> filmcategoryTimeCellDataFeatures) {
                    return new SimpleObjectProperty<Time>(filmcategoryTimeCellDataFeatures.getValue().getFilmId().getDuree());
                }


            });
            descriptionFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Filmcategory, String> filmcategoryStringCellDataFeatures) {
                    return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getFilmId().getDescription());
                }
            });
            annederalisationFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Filmcategory, Integer> filmcategoryIntegerCellDataFeatures) {
                    return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getFilmId().getAnnederalisation()).asObject();
                }
            });
            //idcategoryFilm_tableColumn.setCellValueFactory(new PropertyValueFactory<Film, String>("categoryNom"));
            idcategoryFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, CheckComboBox<String>>, ObservableValue<CheckComboBox<String>>>() {
                @Override
                public ObservableValue<CheckComboBox<String>> call(TableColumn.CellDataFeatures<Filmcategory, CheckComboBox<String>> p) {
                    CheckComboBox<String> checkComboBox = new CheckComboBox<>();
                    List<String> l = new ArrayList<>();
                    CategoryService cs = new CategoryService();
                    for (Category c : cs.read())
                        l.add(c.getNom());
                    checkComboBox.getItems().addAll(l);
                    List<String> ls = Stream.of(p.getValue().getCategoryId().getNom().split(", ")).toList();
                    for (String checkedString : ls)
                        checkComboBox.getCheckModel().check(checkedString);
                    return new SimpleObjectProperty<CheckComboBox<String>>(checkComboBox);
                }
            });
            idacteurFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Filmcategory, String> filmcategoryStringCellDataFeatures) {
                    ActorfilmService actorfilmService = new ActorfilmService();
                    String actorsNames = actorfilmService.getActorsNames(filmcategoryStringCellDataFeatures.getValue().getFilmId().getId());
                    return new SimpleStringProperty(actorsNames);
                }
            });
            idcinemaFilm_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, Integer>, ObservableValue<Integer>>() {
                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Filmcategory, Integer> filmcategoryIntegerCellDataFeatures) {
                    return new SimpleIntegerProperty(filmcategoryIntegerCellDataFeatures.getValue().getFilmId().getIdcinema()).asObject();
                }
            });
            Delete_Column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Filmcategory, Button>, ObservableValue<Button>>() {
                public ObservableValue<Button> call(TableColumn.CellDataFeatures<Filmcategory, Button> p) {
                    // p.getValue() returns the Person instance for a particular TableView row
                    Button button = new Button("add");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            deleteFilm(p.getValue().getFilmId().getId());
                        }
                    });
                    return new SimpleObjectProperty<Button>(button);
                }
            });
            // Configurer la cellule de la colonne Logo pour afficher l'image
            imageFilm_tableColumn.setCellValueFactory(cellData -> {
                Film film = cellData.getValue().getFilmId();
                ImageView imageView = new ImageView();
                imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
                imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
                try {
                    Blob blob = film.getImage();
                    if (blob != null) {
                        Image image = new Image(blob.getBinaryStream());
                        imageView.setImage(image);
                    } else {
                        // Afficher une image par défaut si le logo est null
                        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("default_image.png")));
                        imageView.setImage(defaultImage);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
            });
            FilmcategoryService filmcategoryService = new FilmcategoryService();
            CategoryService categoryService = new CategoryService();

            //           for (Film ff : lf)
//                System.out.println(ff);
            ObservableList<Filmcategory> obF = FXCollections.observableArrayList(filmcategoryService.read());
            filmCategory_tableView1.setItems(obF);
//            filmCategory_tableView1.getSelectionModel().selectedItemProperty().addListener((observableValue, category, t1) -> {
//                Filmcategory f = filmCategory_tableView1.getSelectionModel().getSelectedItem();
//                if (f != null) {
//                    idFilm_textArea.setText(String.valueOf(f.getId()));
//                    nomFilm_textArea.setText(f.getNom());
//                    imageFilm_ImageView.toString();
//                    dureeFilm_textArea.setText(f.getDuree().toString());
//                    descriptionFilm_textArea.setText(f.getDescription());
//                    annederealisationFilm_textArea.setText(String.valueOf(f.getAnnederalisation()));
//                    idacteurFilm_comboBox.setValue(String.valueOf(f.getIdacteur()));
//                    idcinemaFilm_comboBox.setValue(String.valueOf(f.getIdcinema()));
//                    imageBlob = f.getImage();
//                    Blob imageBlob1 = f.getImage();
//                    try (InputStream inputStream = imageBlob1.getBinaryStream()) {
//                        Image image1 = new Image(inputStream);
//                        imageFilm_ImageView.setImage(image1);
//                    } catch (SQLException | IOException e) {
//                        e.printStackTrace();
//                        showAlert("Erreur lors de la récupération de l'image : " + e.getMessage());
//                    }
//                }
//            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateFilm(ActionEvent event) {
        if (imageBlob != null) { // Vérifier si une image a été sélectionnée
            Connection connection = null;
            try {
                if (selectedFile != null)
                // Convertir le fichier en un objet Blob
                {
                    FileInputStream fis = new FileInputStream(selectedFile);

                    connection = DataSource.getInstance().getConnection();

                    // Définir le flux d'entrée de l'image dans l'objet Blob
                    try (OutputStream outputStream = imageBlob.setBinaryStream(1)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                // modifier un produit

                FilmService fs = new FilmService();
// Assign value to imageBlob
                /* assign the blob value here */
                fs.update(new Film(
                        Integer.parseInt(idFilm_textArea.getText()),
                        nomFilm_textArea.getText(),
                        imageBlob,
                        Time.valueOf(dureeFilm_textArea.getText()),
                        descriptionFilm_textArea.getText(),
                        Integer.parseInt(annederealisationFilm_textArea.getText()), // Comma added here
                        Integer.parseInt(idcinemaFilm_comboBox.getValue())));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Produit modifiée");
                alert.setContentText("Produit modifiée !");
                alert.show();
                readFilmTable();
                clear();

            } catch (SQLException | IOException e) {
                showAlert("Erreur lors de la modification du produit : " + e.getMessage());
            }
        }
        readFilmTable();

    }

}
