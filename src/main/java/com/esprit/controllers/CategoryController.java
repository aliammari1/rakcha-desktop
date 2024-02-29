package com.esprit.controllers;

import com.esprit.models.Category;
import com.esprit.services.CategoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private TextArea idCategory_textArea;

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
    private AnchorPane categoryCrudInterface;

    @FXML
    private StackPane stackPane;
    @FXML
    private Button AjouterCategory_Button;

    @FXML
    void initialize() {
        readCategoryTable(new ActionEvent());
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
        readCategoryTable(event);
    }

    @FXML
    void readCategoryTable(ActionEvent event) {
        try {
            idCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, Integer>("id"));
            nomCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, String>("nom"));
            descrptionCategory_tableColumn.setCellValueFactory(new PropertyValueFactory<Category, String>("description"));
            CategoryService categoryService = new CategoryService();
            ObservableList<Category> obC = FXCollections.observableArrayList(categoryService.read());
            filmCategory_tableView.setItems(obC);
            filmCategory_tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, category, t1) -> {
                Category c = filmCategory_tableView.getSelectionModel().getSelectedItem();
                if (c != null) {
                    idCategory_textArea.setText(String.valueOf(c.getId()));
                    nomCategory_textArea.setText(c.getNom());
                    descriptionCategory_textArea.setText(c.getDescription());
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == AjouterCategory_Button) {
            categoryCrudInterface.setVisible(true);
        }
    }

    @FXML
    void deleteCategory(ActionEvent event) {
        try {
            CategoryService categoryService = new CategoryService();
            Category category = new Category(Integer.parseInt(idCategory_textArea.getText()), nomCategory_textArea.getText(), descriptionCategory_textArea.getText());
            categoryService.delete(category);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("la suppression est terminer");
            alert.setHeaderText("categorie");
            alert.setHeaderText("categorie");
            alert.show();
            readCategoryTable(event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void updateCategory(ActionEvent event) {
        CategoryService categoryService = new CategoryService();
        Category category = new Category(Integer.parseInt(idCategory_textArea.getText()), nomCategory_textArea.getText(), descriptionCategory_textArea.getText());
        categoryService.update(category);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("le mis à jour est terminer");
        alert.setHeaderText("categorie");
        alert.setHeaderText("categorie");
        alert.show();
        readCategoryTable(event);
    }

    private void setupCellFactory() {
        idCategory_tableColumn.setVisible(false);

        nomCategory_tableColumn.setCellFactory(new Callback<TableColumn<Category, String>, TableCell<Category, String>>() {
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

    }

    private void setupCellValueFactory() {

        nomCategory_tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> filmcategoryStringCellDataFeatures) {

                return new SimpleStringProperty(filmcategoryStringCellDataFeatures.getValue().getNom());
            }
        });
    }
    
}
