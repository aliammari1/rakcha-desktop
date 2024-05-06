package com.esprit.controllers.films;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.films.Category;
import com.esprit.models.users.Client;
import com.esprit.services.films.CategoryService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import net.synedra.validatorfx.Validator;

public class CategoryController {

    @FXML
    private TextArea descriptionCategory_textArea;
    @FXML
    private TextArea nomCategory_textArea;
    @FXML
    private TableView<Category> filmCategory_tableView;
    @FXML
    private TableColumn<Category, String> nomCategory_tableColumn;
    @FXML
    private TableColumn<Category, String> descrptionCategory_tableColumn;
    @FXML
    private TableColumn<Category, Integer> idCategory_tableColumn;
    @FXML
    private TableColumn<Category, Button> delete_tableColumn;
    @FXML
    private AnchorPane categoryCrudInterface;
    @FXML
    private ComboBox<String> filterCriteriaComboBox;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button AjouterCategory_Button;
    @FXML
    private TextField recherche_textField;


    @FXML
    void initialize() {
        delete_tableColumn = new TableColumn<>("delete");
        filmCategory_tableView.getColumns().add(delete_tableColumn);
        filmCategory_tableView.setEditable(true);
        setupCellFactory();
        setupCellValueFactory();
        setupCellOnEditCommit();
        readCategoryTable();
        filterCriteriaComboBox.setItems(FXCollections.observableArrayList("Name", "Description"));


    }

    private void search(String keyword) {
        CategoryService categoryService = new CategoryService();
        ObservableList<Category> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(categoryService.read());
        } else {
            for (Category category : categoryService.read()) {
                if (category.getNom().toLowerCase().contains(keyword.toLowerCase()) ||
                        category.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(category);
                }
            }
        }
        filmCategory_tableView.setItems(filteredList);
    }

    @FXML
    void insertCategory(ActionEvent event) {
        CategoryService categoryService = new CategoryService();
        Category category = new Category(nomCategory_textArea.getText(), descriptionCategory_textArea.getText());
        categoryService.create(category);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("l'insertion est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        readCategoryTable();

    }


    void readCategoryTable() {
        try {
            CategoryService categoryService = new CategoryService();
            ObservableList<Category> obC = FXCollections.observableArrayList(categoryService.read());
            filmCategory_tableView.setItems(obC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == AjouterCategory_Button) {
            categoryCrudInterface.setVisible(true);
        }
    }

    void deleteCategory(int id) {
        try {
            CategoryService categoryService = new CategoryService();
            categoryService.delete(categoryService.getCategory(id));
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("la suppression est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            readCategoryTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateCategory(Category category) {
        CategoryService categoryService = new CategoryService();
        categoryService.update(category);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("le mis à jour est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        readCategoryTable();
    }

    private void setupCellFactory() {
        idCategory_tableColumn.setVisible(false);
        Callback<TableColumn<Category, String>, TableCell<Category, String>> stringCellFactory = new Callback<TableColumn<Category, String>, TableCell<Category, String>>() {
            @Override
            public TableCell<Category, String> call(TableColumn<Category, String> param) {
                return new TextFieldTableCell<Category, String>(new DefaultStringConverter()) {
                    private Validator validator;


                    @Override
                    public void startEdit() {
                        super.startEdit();
                        TextField textField = (TextField) getGraphic();
                        if (textField != null && validator == null) {
                            validator = new Validator();
                            validator.createCheck()
                                    .dependsOn("text", textField.textProperty())
                                    .withMethod(c -> {
                                        String input = c.get("text");
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
        };

        nomCategory_tableColumn.setCellFactory(stringCellFactory);
        descrptionCategory_tableColumn.setCellFactory(stringCellFactory);

    }

    private void setupCellValueFactory() {
        idCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));

        descrptionCategory_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> param) {
                return new SimpleStringProperty(param.getValue().getDescription());
            }
        });

        nomCategory_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> filmcategoryStringCellDataFeatures) {

                return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getNom());
            }
        });
        delete_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, Button>, ObservableValue<Button>>() {
            @Override
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<Category, Button> param) {
                Button button = new Button("delete");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        deleteCategory(param.getValue().getId());
                    }
                });
                return new SimpleObjectProperty<Button>(button);
            }
        });
    }

    private void setupCellOnEditCommit() {
        nomCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setNom(event.getNewValue());
                    updateCategory(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        descrptionCategory_tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Category, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Category, String> event) {
                try {
                    event.getTableView().getItems().get(
                            event.getTablePosition().getRow()).setDescription(event.getNewValue());
                    updateCategory(event.getTableView().getItems().get(
                            event.getTablePosition().getRow()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}


