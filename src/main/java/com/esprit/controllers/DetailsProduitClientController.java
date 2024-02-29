package com.esprit.controllers;

import com.esprit.models.Produit;
import com.esprit.services.ProduitService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.util.Optional;
import java.util.ResourceBundle;



public class DetailsProduitClientController implements Initializable {

@FXML
    public FlowPane detailFlowPane;
  @FXML
    public TextField SearchBar;

    @FXML
    private FontAwesomeIconView retour;

    private int produitId;



    // Méthode pour initialiser l'ID du produit
    public void setProduitId(int produitId) throws IOException {
        this.produitId = produitId;
        // Initialiser les détails du produit après avoir défini l'ID
        initDetailsProduit();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


       // Attacher un gestionnaire d'événements au clic de la souris sur l'icône de retour
        retour.setOnMouseClicked(event -> afficherProduit());
    }

    private void initDetailsProduit() {
        // Récupérer le produit depuis le service en utilisant l'ID
        ProduitService produitService = new ProduitService();
        Optional<Produit> produitOptional = Optional.ofNullable(produitService.getProduitById(produitId));

        // Vérifier si le produit a été trouvé
        produitOptional.ifPresentOrElse(
                produit -> {
                    // Effacer les anciennes cartes avant d'ajouter la nouvelle carte
                    detailFlowPane.getChildren().clear();

                    // Créer la carte pour le produit trouvé et l'ajouter à la FlowPane
                    HBox cardContainer = createProduitCard(produit);
                    detailFlowPane.getChildren().add(cardContainer);
                },
                () -> System.err.println("Produit non trouvé avec l'ID : " + produitId)
        );
    }


    private HBox createProduitCard (Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 30px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(10);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);


        try {
            Blob blob = produit.getImage();
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                Image image = new Image(new ByteArrayInputStream(bytes));
                imageView.setImage(image);
            } else {
                // Utiliser une image par défaut si le Blob est null
                Image defaultImage = new Image(getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Nom du Produit
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(450);
        nameLabel.setLayoutY(30);

        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setLayoutX(450);
        priceLabel.setLayoutY(200);

       // Description du produit
        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setStyle("-fx-text-fill: #666666;");
        descriptionLabel.setLayoutX(470);
        descriptionLabel.setLayoutY(70);

        // Champ de texte pour la quantité
        TextField quantityTextField = new TextField();
        quantityTextField.setPromptText("Quantité");
        quantityTextField.setLayoutX(450);
        quantityTextField.setLayoutY(250);

        // Icône de diminution de quantité
        FontAwesomeIconView decreaseIcon = new FontAwesomeIconView();
        decreaseIcon.setGlyphName("MINUS");
        decreaseIcon.setSize("15");
        decreaseIcon.setLayoutX(425);
        decreaseIcon.setLayoutY(270);
        decreaseIcon.setOnMouseClicked(event -> decreaseQuantity(quantityTextField));

        // Icône d'augmentation de quantité
        FontAwesomeIconView increaseIcon = new FontAwesomeIconView();
        increaseIcon.setGlyphName("PLUS");
        increaseIcon.setSize("15");
        increaseIcon.setLayoutX(609);
        increaseIcon.setLayoutY(270);
        increaseIcon.setOnMouseClicked(event -> increaseQuantity(quantityTextField));



        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Ajouter au panier");
        addToCartButton.setLayoutX(450);
        addToCartButton.setLayoutY(300);
        addToCartButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: White;"); // Style du bouton
        addToCartButton.setOnAction(event -> {
            // Récupérer la quantité sélectionnée
            int quantity = Integer.parseInt(quantityTextField.getText());

            // Vérifier si la quantité demandée est disponible en stock
            if (isStockAvailable(produit, quantity)) {
                // Mettre à jour le stock dans la base de données
                decrementStock(produit, quantity);

                // Créer un objet représentant l'élément du panier
        //        PanierProduitControllers commandeItem = new PanierProduitControllers(produit, quantity);

                // Ajouter l'élément à la commande
       //        Commande.getInstance().ajouterAuPanier(commandeItem);

                // Afficher un message de confirmation
                System.out.println("Produit ajouté à la commande!");
            } else {
                // Afficher un message d'erreur si la quantité demandée n'est pas disponible en stock
                System.out.println("Quantité non disponible en stock!");
            }
        });

        card.getChildren().addAll(imageView,nameLabel,descriptionLabel,priceLabel,increaseIcon,quantityTextField,decreaseIcon,addToCartButton);

        cardContainer.getChildren().add(card);

        return cardContainer;
    }

    private boolean isStockAvailable(Produit produit, int quantity) {
        // Comparer la quantité demandée avec la quantité disponible en stock
        return produit.getQuantiteP() >= quantity;
    }

    private void decrementStock(Produit produit, int quantity) {
        // Décrémenter le stock dans la base de données
        produit.setQuantiteP(produit.getQuantiteP() - quantity);
        ProduitService produitService = new ProduitService();
        produitService.update(produit); // Assurez-vous que votre service de produit dispose d'une méthode de mise à jour
    }

    private void decreaseQuantity(TextField quantityTextField) {
        // Diminuer la quantité
        int quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity > 1) {
            quantityTextField.setText(String.valueOf(quantity - 1));
        }
    }

    private void increaseQuantity(TextField quantityTextField) {
        // Augmenter la quantité
        int quantity = Integer.parseInt(quantityTextField.getText());
        quantityTextField.setText(String.valueOf(quantity + 1));
    }




    @FXML
    public void afficherProduit() {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) retour.getScene().getWindow();
        stage.close();

    }


}

