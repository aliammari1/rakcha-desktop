package com.esprit.controllers.evenements;

import com.esprit.models.evenements.Sponsor;
import com.esprit.services.evenements.SponsorService;
import com.esprit.utils.DataSource;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DesignSponsorAdminController {

    private File selectedFile; // pour stocke le fichier image selectionné

    @FXML
    private Button bAddS;

    @FXML
    private FontAwesomeIconView iconLogoS;

    @FXML
    private ImageView image;

    @FXML
    private TableColumn<Sponsor, ImageView> tcLogo;

    @FXML
    private TableColumn<Sponsor, String> tcNomS;

    @FXML
    private TextField tfNomS;

    @FXML
    private TableView<Sponsor> tvSponsor;

    @FXML
    private TableColumn<Sponsor, Void> tcDeleteS;

    @FXML

    public static List<Sponsor> rechercher(List<Sponsor> liste, String recherche) {
        List<Sponsor> resultats = new ArrayList<>();

        if (recherche.isEmpty()) {
            return resultats;
        }

        for (Sponsor element : liste) {
            if (element.getNomSociete() != null && element.getNomSociete().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }

    @FXML
    void initialize() {

        afficher_sponsor();
        initDeleteColumn();

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
    void selectImage(MouseEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            image.setImage(selectedImage);
        }

    }

    @FXML
    public void ajouter_sponsor(ActionEvent actionEvent) {

        // Vérifier si une image a été sélectionnée
        if (selectedFile != null) {
            // Récupérer les valeurs des champs de saisie
            String nomSponsor = tfNomS.getText().trim();

            // Vérifier si les champs sont vides
            if (nomSponsor.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Information");
                alert.setContentText("Please fill out the form.");
                alert.show();
            }

            // Vérifier si le nom ne contient que des alphabets et des chiffres
            if (!nomSponsor.matches("[a-zA-Z0-9]*")) {
                showAlert("Please enter a valid name with no special characters.");
                return; // Arrêter l'exécution de la méthode si le nom n'est pas valide
            }

            // Convertir le fichier en un objet Blob
            Connection connection = null;
            try {
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

                // Créer l'objet Produit avec l'image Blob
                SponsorService ss = new SponsorService();
                Sponsor nouveauSponsor = new Sponsor(nomSponsor, imageBlob);
                ss.add(nouveauSponsor);

                // Ajouter le nouveau produit à la liste existante
                tvSponsor.getItems().add(nouveauSponsor);

                // Rafraîchir la TableView
                tvSponsor.refresh();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sponsor Added");
                alert.setContentText("Sponsor Added !");
                alert.show();
            } catch (SQLException | IOException e) {
                showAlert("Error while adding sponsor : " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        showAlert("Error while closing connection : " + e.getMessage());
                    }
                }
            }

        } else {
            showAlert("Please select an image !");
        }
    }

    @FXML
    void modifier_sponsor(Sponsor sponsor) {

        // Récupérez les valeurs modifiées depuis la ligne
        String nouveauNom = sponsor.getNomSociete();
        Blob img = sponsor.getLogo();
        int id = sponsor.getId();

        // Enregistrez les modifications dans la base de données en utilisant un service approprié
        SponsorService ss = new SponsorService();
        ss.update(sponsor);
    }

    void afficher_sponsor() {

        // Créer un nouveau ComboBox
        ImageView imageView = new ImageView();

        tcNomS.setCellValueFactory(new PropertyValueFactory<Sponsor, String>("nom"));
        tcNomS.setCellFactory(TextFieldTableCell.forTableColumn());
        tcNomS.setOnEditCommit(event -> {
            Sponsor sponsor = event.getRowValue();
            sponsor.setNomSociete(event.getNewValue());
            modifier_sponsor(sponsor);
        });


        // Configurer la colonne Logo pour afficher et changer l'image
        tcLogo.setCellValueFactory(cellData -> {
            Sponsor s = cellData.getValue();

            imageView.setFitWidth(100); // Réglez la largeur de l'image selon vos préférences
            imageView.setFitHeight(50); // Réglez la hauteur de l'image selon vos préférences
            try {
                Blob blob = s.getLogo();
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

        // Configurer l'événement de clic pour changer l'image
        tcLogo.setCellFactory(col -> new TableCell<Sponsor, ImageView>() {

            private final ImageView imageView = new ImageView();
            private Sponsor sponsor;

            {
                setOnMouseClicked(event -> {
                    if (!isEmpty()) {
                        changerImage(sponsor);
                        afficher_sponsor();
                    }
                });
            }

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                    setText(null);
                }

                sponsor = getTableRow().getItem();
            }
        });


        // Activer l'édition en cliquant sur une ligne
        tvSponsor.setEditable(true);

        // Gérer la modification du texte dans une cellule et le valider en appuyant sur Enter
        tvSponsor.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                Sponsor selectedSponsor = tvSponsor.getSelectionModel().getSelectedItem();
                if (selectedSponsor != null) {
                    modifier_sponsor(selectedSponsor);
                }
            }
        });

        // Utiliser une ObservableList pour stocker les éléments
        ObservableList<Sponsor> list = FXCollections.observableArrayList();
        SponsorService ss = new SponsorService();
        list.addAll(ss.show());
        tvSponsor.setItems(list);

        // Activer la sélection de cellules
        tvSponsor.getSelectionModel().setCellSelectionEnabled(true);

    }

    private void initDeleteColumn() {
        Callback<TableColumn<Sponsor, Void>, TableCell<Sponsor, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Sponsor, Void> call(final TableColumn<Sponsor, Void> param) {
                final TableCell<Sponsor, Void> cell = new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.getStyleClass().add("delete-button");
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Sponsor sponsor = getTableView().getItems().get(getIndex());
                            SponsorService ss = new SponsorService();
                            ss.delete(sponsor);

                            // Mise à jour de la TableView après la suppression de la base de données
                            tvSponsor.getItems().remove(sponsor);
                            tvSponsor.refresh();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
                return cell;
            }
        };

        tcDeleteS.setCellFactory(cellFactory);
        tvSponsor.getColumns().add(tcDeleteS);
    }

    // Méthode pour changer l'image
    private void changerImage(Sponsor sponsor) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        selectedFile = fileChooser.showOpenDialog(null);

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
                sponsor.setLogo(imageBlob);
                modifier_sponsor(sponsor);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showAlert("Error while loading image : " + e.getMessage());
            }
        }
    }


}
