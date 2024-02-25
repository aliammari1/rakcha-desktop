package com.esprit.controllers;

import com.esprit.models.Category;
import com.esprit.services.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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

}
