package com.esprit.controllers.series;

import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.series.Categorie;
import com.esprit.models.users.Client;
import com.esprit.services.series.IServiceCategorieImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class CategorieController {

    @FXML
    private Label checkdescreption;
    @FXML
    private Label checkname;
    @FXML
    private TextField nomF;
    @FXML
    private TextField descreptionF;
    @FXML
    private TableView<Categorie> tableView;


    private void ref() {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        nomF.setText("");
        descreptionF.setText("");
        //affichege tableau
        IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        //TableColumn<Categorie, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idcategorie"));

        TableColumn<Categorie, String> nomCol = new TableColumn<>("Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Categorie, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Categorie, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Delete");

            {
                button.setOnAction(event -> {

                    Categorie categorie = getTableView().getItems().get(getIndex());
                    try {
                        categorieserv.supprimer(categorie.getIdcategorie());
                        tableView.getItems().remove(categorie);
                        showAlert("Succes", "Deleted successfully !");
                        ref();
                        tableView.refresh();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Error", e.getSQLState());
                    }
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
        TableColumn<Categorie, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount = 0;

            {
                button.setOnAction(event -> {
                    clickCount++;
                    if (clickCount == 2) {
                        Categorie categorie = getTableView().getItems().get(getIndex());
                        modifierCategorie(categorie);
                        clickCount = 0;
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        // tableView.getColumns().addAll(idCol,nomCol, descriptionCol,modifierCol, supprimerCol);
        tableView.getColumns().addAll(nomCol, descriptionCol, modifierCol, supprimerCol);

        try {
            tableView.getItems().addAll(categorieserv.recuperer());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void modifierCategorie(Categorie categorie) {
        IServiceCategorieImpl iServiceCategorie = new IServiceCategorieImpl();
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");

        TextField nomFild = new TextField(categorie.getNom());
        TextField desqFild = new TextField(String.valueOf(categorie.getDescription()));


        dialog.getDialogPane().setContent(new VBox(8, new Label("Name:"), nomFild, new Label("Description:"), desqFild));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nomFild.getText(), desqFild.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            categorie.setNom(nomFild.getText());
            categorie.setDescription(desqFild.getText());
            try {
                iServiceCategorie.modifier(categorie);
                showAlert("successfully",
                        "Modified successfully!");
                ref();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @FXML
    private void initialize() {
        ref();
    }

    boolean checkname() {
        if (nomF.getText() != "") {

            return true;
        } else {
            checkname.setText("Please enter a valid Name");
            return false;
        }
    }

    boolean checkdescreption() {
        if (descreptionF.getText() != "") {

            return true;
        } else {
            checkdescreption.setText("Please enter a valid Description");
            return false;
        }
    }

    @FXML
    void ajouteroeuvre(ActionEvent event) {
        IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        Categorie categorie = new Categorie();

        checkname();
        checkdescreption();
        if (checkname() && checkdescreption()) {
            try {

                categorie.setNom(nomF.getText());
                categorie.setDescription(descreptionF.getText());
                categorieserv.ajouter(categorie);
                showAlert("Succes", "The category has been saved successfully");
                checkname.setText("");
                checkdescreption.setText("");
                ref();
            } catch (Exception e) {
                showAlert("Error", "An error occurred while saving the category : " + e.getMessage());
            }
        }
    }

    ///Gestion du menu
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Categorie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void Oseriess(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Serie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void Oepisode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Episode-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void showStatistics(ActionEvent actionEvent) {
        if (actionEvent != null) {


            // Logique pour basculer vers la vue des statistiques
            // Vous pouvez utiliser un FXMLLoader pour charger la vue des statistiques
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatistiquesView.fxml"));
                Parent root = loader.load();
                StatistiqueController statistiqueController = loader.getController();
                // Initialisez le contrôleur statistique si nécessaire
                statistiqueController.initialize();

                // Créez une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenez la scène actuelle à partir de l'événement
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                // Remplacez la scène actuelle par la nouvelle scène
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer l'exception
            }
        } else {
            // Gérer le cas où actionEvent est nul
            System.out.println("actionEvent is null");
        }


    }


    public void showmovies(ActionEvent actionEvent) {
    }

    public void showproducts(ActionEvent actionEvent) {
    }

    public void showcinema(ActionEvent actionEvent) {
    }

    public void showevent(ActionEvent actionEvent) {
    }

    public void showseries(ActionEvent actionEvent) {
    }
}
