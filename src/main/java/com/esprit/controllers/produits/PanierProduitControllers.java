package com.esprit.controllers.produits;
import com.esprit.models.produits.*;
import com.esprit.models.users.Client;
import com.esprit.services.produits.CommandeItemService;
import com.esprit.services.produits.PanierService;
import com.esprit.services.produits.ProduitService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
public class PanierProduitControllers implements Initializable {
    @FXML
    private FlowPane cartFlowPane;
    @FXML
    private FlowPane prixtotaleFlowPane;
    Commande commande = new Commande();
    // Déclarez une structure de données pour stocker les VBox associés aux produits
    private Map<Integer, VBox> produitVBoxMap = new HashMap<>();
    // Mettez à jour le prix total dans SharedData
    //SharedData sharedData = new SharedData();
    PanierService panierService = new PanierService();
    Panier panier = new Panier();
    private double prixTotal = 0.0;
    @FXML
    private FontIcon retour;
    /** 
    /**
     * Triggers a task to be executed on the EDT (Event Dispatch Thread) by calling
     * `Platform.runLater`. The task is an anonymous inner class that calls the method `loadAcceptedPanier`.
     * 
     * @param url URL of the application that requires initialization, and is passed to
     * the `runLater()` method for further processing.
     * 
     * @param resourceBundle resource bundle that provides localized data for the platform,
     * and is used to load the accepted panier in the run method.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(new Runnable() {
            /**
             * Loads accepted panier.
             */
            @Override
            public void run() {
                loadAcceptedPanier();
            }
        });
    }
    /**
     * Retrieves a list of products from a service, verifies if each product has already
     * been added to the cart, and adds it to the cart if not. It also marks the product
     * as added in a map for future reference.
     */
    private void loadAcceptedPanier() {
        // Récupérer toutes les produits depuis le service
        PanierService panierService = new PanierService();
        List<Panier> items = null;
        try {
            Client client = (Client) cartFlowPane.getScene().getWindow().getUserData();
            items = panierService.readUserPanier(client.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Panier panier1 : items) {
            // Vérifier si le produit a déjà été ajouté
            if (!produitVBoxMap.containsKey(panier1.getProduit().getId_produit())) {
                VBox produitVBox = createProduitVBox(panier1);
                cartFlowPane.getChildren().add(produitVBox);
                // Ajouter le produit à la map pour le marquer comme déjà ajouté
                produitVBoxMap.put(panier1.getProduit().getId_produit(), produitVBox);
            }
        }
    }
    /** 
    /**
     * Creates a new Label element with the total price displayed as a double value, using
     * the specified font size, position, and styling options.
     * 
     * @param prixTotal total price of the product, which is used to create and set the
     * label's text value.
     * 
     * @returns a label with the price total value displayed in bold font.
     */
    private Label createPrixTotalLabel(double prixTotal) {
        // Créez le Label du prix total ici
        Label prixTotalLabel = new Label(prixTotal + " DT");
        prixTotalLabel.setFont(Font.font("Verdana", 20));
        prixTotalLabel.setLayoutX(30);
        prixTotalLabel.setLayoutY(30);
        prixTotalLabel.setStyle("-fx-text-fill: #d72222;");
        return prixTotalLabel;
    }
    /**
     * Generates a `VBox` container for each product in the shopping cart, with buttons
     * to decrease or increase the quantity and a label to display the total price. It
     * also provides delete confirmation pop-up for removing items from the cart.
     * 
     * @param Panier panier object that contains the details of the products, quantities,
     * and total price, which are used to populate the UI elements in the `generateProduitCard()`
     * function.
     * 
     * @returns a VBox container that displays a product's details and allows users to
     * select it for their order.
     */
    private VBox createProduitVBox(Panier Panier) {
        VBox produitVBox = new VBox();
        produitVBox.setStyle("-fx-padding: 20px 0 0  10px;"); // Ajout de remplissage à gauche pour le décalage
        AnchorPane card = new AnchorPane();
        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(3);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        try {
            String produitImage = Panier.getProduit().getImage();
            if (produitImage != null) {
                Image image = new Image(produitImage);
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
        Label nameLabel = new Label(Panier.getProduit().getNom());
        nameLabel.setLayoutX(175);
        nameLabel.setLayoutY(10);
        nameLabel.setFont(Font.font("Arial", 20));
        nameLabel.setStyle("-fx-text-fill: black;");
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // Prix unitaire du Produit
        Label priceLabel = new Label(" " + Panier.getProduit().getPrix() + " DT");
        priceLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(180);
        priceLabel.setLayoutY(140);
        // la quantité
        TextField quantityTextField = new TextField("1");
        quantityTextField.setPromptText("Quantité");
        quantityTextField.setLayoutX(375);
        quantityTextField.setLayoutY(80);
        quantityTextField.setMaxWidth(50);
        quantityTextField.setEditable(false); // Empêcher l'édition manuelle
        int totalQuantiteProduit = Integer.parseInt(quantityTextField.getText());
        Label sommeTotaleLabel = new Label(
                "Somme totale : " + prixProduit(Panier.getProduit().getId_produit(), totalQuantiteProduit) + " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sommeTotaleLabel.setLayoutX(550);
        sommeTotaleLabel.setLayoutY(60);
        sommeTotaleLabel.setMaxWidth(150); // Ajuster la largeur max double sommeTotaleProduit =
        // PanierService.calculerSommeTotaleProduit(Panier.getProduit().getId_produit());
        sommeTotaleLabel.setWrapText(true); // Activer le retour à la ligne automatique
        // CheckBox pour sélectionner le produit
        CheckBox checkBox = new CheckBox("Select");
        checkBox.setLayoutX(650);
        checkBox.setLayoutY(150);
        checkBox.setSelected(false); // Vous pouvez définir l'état initial selon vos besoins
        CommandeItem commandeItem = new CommandeItem();
        commandeItem.setProduit(Panier.getProduit());
        commandeItem.setQuantity(Integer.parseInt(quantityTextField.getText()));
        // Ajout d'un gestionnaire d'événements pour réagir aux changements d'état de la
        // CheckBox
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Réagir au changement d'état de la CheckBox (sélectionné ou non)
            if (newValue) {
                commande.getCommandeItem().add(commandeItem);
                System.out.println("Produit sélectionné");
                updatePrixTotal();
            } else {
                commande.getCommandeItem().remove(commandeItem);
                System.out.println("Produit non sélectionné");
                updatePrixTotal();
            }
        });
        // Icône de diminution de quantité
        FontIcon decreaseIcon = new FontIcon("fa-minus");
        decreaseIcon.setIconSize(15);
        decreaseIcon.setLayoutX(350);
        decreaseIcon.setLayoutY(95);
        decreaseIcon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                decreaseQuantity(quantityTextField, Panier);
                sommeTotaleLabel.setText("Somme totale : " + prixProduit(Panier.getProduit().getId_produit(),
                        Integer.parseInt(quantityTextField.getText())) + " DT");
                commande.getCommandeItem().get(commande.getCommandeItem().indexOf(commandeItem))
                        .setQuantity(Integer.parseInt(quantityTextField.getText()));
                updatePrixTotal();
            }
        });
        // Icône d'augmentation de quantité
        FontIcon increaseIcon = new FontIcon("fa-plus");
        increaseIcon.setIconSize(15);
        increaseIcon.setLayoutX(435);
        increaseIcon.setLayoutY(95);
        increaseIcon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                increaseQuantity(quantityTextField, Panier);
                sommeTotaleLabel.setText("Somme totale : " + prixProduit(Panier.getProduit().getId_produit(),
                        Integer.parseInt(quantityTextField.getText())) + " DT");
                commande.getCommandeItem().get(commande.getCommandeItem().indexOf(commandeItem))
                        .setQuantity(Integer.parseInt(quantityTextField.getText()));
                updatePrixTotal();
            }
        });
        FontIcon deleteIcon = new FontIcon("fa-trash");
        deleteIcon.setIconSize(25);
        deleteIcon.setLayoutX(700); // Définissez la position X selon vos besoins
        deleteIcon.setLayoutY(30); // Définissez la position Y selon vos besoins
        deleteIcon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Voulez-vous vraiment supprimer cet élément du panier?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // L'utilisateur a confirmé la suppression
                    panierService.delete(Panier);
                    // Mettez à jour votre interface utilisateur ici
                    refreshUI();
                }
            }
        });
        card.setStyle("-fx-background-color:#F6F2F2;\n"
                + " -fx-text-fill: #FFFFFF;\n"
                + "    -fx-font-size: 12px;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-padding: 10px;\n"
                + "    -fx-border-color:  #ae2d3c;/* Couleur de la bordure */\n"
                + "    -fx-border-width: 2px; /* Largeur de la bordure */\n"
                + "    -fx-border-radius: 5px; /* Rayon de la bordure pour arrondir les coins */");
        // Ajouter tous les éléments au VBox
        card.getChildren().addAll(imageView, nameLabel, priceLabel, decreaseIcon, quantityTextField, increaseIcon,
                sommeTotaleLabel, checkBox, deleteIcon);
        produitVBoxMap.put(Panier.getProduit().getId_produit(), produitVBox);
        produitVBox.getChildren().add(card);
        return produitVBox;
    }
    /**
     * Clears the current elements from the shopping cart, reloads the accepted items,
     * and updates the total price.
     */
    private void refreshUI() {
        // Effacer les éléments actuels du panier
        cartFlowPane.getChildren().clear();
        produitVBoxMap.clear();
        // Charger à nouveau les produits du panier
        loadAcceptedPanier();
        // Mettre à jour le prix total
        updatePrixTotal();
    }
    /**
     * Decreases the quantity of items in a shopping cart by one unit when the user types
     * a negative value into a text field. The updated quantity is then saved in the
     * panier object and reflected in the cart's total quantity.
     * 
     * @param quantityTextField quantity to be decreased, which is obtained from the text
     * field of the same name.
     * 
     * @param panier Panier object whose quantity is being updated by the function.
     */
    private void decreaseQuantity(TextField quantityTextField, Panier panier) {
        // Diminuer la quantité
        int quantity = Integer.parseInt(quantityTextField.getText());
        if (quantity > 1) {
            quantityTextField.setText(String.valueOf(quantity - 1));
            panier.setQuantity(panier.getQuantity() - 1);
        }
    }
    /**
     * Compares the requested quantity with the available quantity of stock for a given
     * product and returns true if there is enough stock, otherwise false.
     * 
     * @param produit product for which the availability of stock is being checked.
     * 
     * @param quantity amount of units of the product that are required or desired by the
     * user, which is compared with the available stock quantity to determine if the
     * product is available for purchase.
     * 
     * @returns a boolean value indicating whether the requested quantity of stock is
     * available or not.
     */
    private boolean isStockAvailable(Produit produit, int quantity) {
        // Comparer la quantité demandée avec la quantité disponible en stock
        return produit.getQuantiteP() >= quantity;
    }
    /**
     * Increases the quantity of an item in a shopping cart by 1, checking if the stock
     * is available and displaying an alert if it's not.
     * 
     * @param quantityTextField quantity of the product to be updated in the panier, as
     * indicated by its name.
     * 
     * @param panier containing the products that the user wishes to increase the quantity
     * of.
     */
    private void increaseQuantity(TextField quantityTextField, Panier panier) {
        int currentQuantity = Integer.parseInt(quantityTextField.getText());
        // Vérifier si le stock est disponible
        if (isStockAvailable(panier.getProduit(), currentQuantity + 1)) {
            quantityTextField.setText(String.valueOf(currentQuantity + 1));
            panier.setQuantity(panier.getQuantity() + 1);
        } else {
            // Afficher une alerte en cas de stock insuffisant
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock Insuffisant");
            alert.setHeaderText("La quantité demandée dépasse la quantité en stock.");
            alert.setContentText("Veuillez réduire la quantité demandée.");
            alert.showAndWait();
        }
    }
    /**
     * Updates the total price label based on the items in a given order by multiplying
     * the product prices by their quantities and storing the result in a shared data
     * instance, then adding it to the flow pane with a created label.
     */
    private void updatePrixTotal() {
        prixTotal = 0.0;
        for (CommandeItem commandeItem : commande.getCommandeItem()) {
            prixTotal += prixProduit(commandeItem.getProduit().getId_produit(), commandeItem.getQuantity());
        }
        // Mettez à jour le prix total dans SharedData
        SharedData.getInstance().setTotalPrix(prixTotal);
        Label prixTotalLabel = createPrixTotalLabel(prixTotal);
        prixtotaleFlowPane.getChildren().clear();
        prixtotaleFlowPane.getChildren().add(prixTotalLabel);
    }
    /**
     * Calculates the total price of a product based on its ID and quantity by multiplying
     * the unitaire price fetched from the `ProduitService`.
     * 
     * @param idProduit ID of the product for which the price is being calculated.
     * 
     * @param quantity number of units of the product to be priced, which is multiplied
     * by the unit price returned by the `ProduitService` to compute the total price.
     * 
     * @returns the total price of a product in units of quantity.
     */
    private double prixProduit(int idProduit, int quantity) {
        ProduitService produitService = new ProduitService();
        double prixUnitaire = produitService.getPrixProduit(idProduit);
        return quantity * prixUnitaire;
    }
    /**
     * Loads a FXML file named `/CommandeClient.fxml` into a Stage, initializes a
     * `CommandeClientController`, and displays the scene on the Stage.
     * 
     * @param event order action event that triggered the function, providing the necessary
     * context for the code to operate properly.
     */
    @FXML
    void order(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CommandeClient.fxml"));
        Stage stage = (Stage) cartFlowPane.getScene().getWindow();
        try {
            Parent root = fxmlLoader.load();
            CommandeClientController controller = fxmlLoader.getController();
            controller.initialize(commande);
            Scene scene = new Scene(root);
            stage.setTitle("order");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Is a handling function for mouse events. It does not perform any specific action
     * or have any distinctive features beyond processing mouse input.
     * 
     * @param event mouse event that triggered the execution of the `Paiment()` function.
     */
    public void Paiment(MouseEvent event) {
    }
    /**
     * Loads a new user interface, creates a new stage and attaches it to the existing
     * stage, replacing the original interface, and finally closes the original stage.
     * 
     * @param event ActionEvent object that triggered the `cinemaclient` method, providing
     * the necessary information to update the FXML layout of the stage.
     */
    @FXML
    void cinemaclient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommentaireProduit.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Loads a new UI fragment (`AffichageEvenementClient.fxml`) and replaces the current
     * scene with it, creating a new stage and closing the original one.
     * 
     * @param event event object that triggered the function, providing information about
     * the event, such as its source and details, which can be used to handle the event
     * appropriately.
     */
    @FXML
    void eventClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Loads a new FXML interface, creates a new scene and stage, and replaces the current
     * stage with the new one, closing the old stage upon execution.
     * 
     * @param event ActionEvent that triggers the function and provides access to information
     * about the action that was performed, such as the source of the event and the stage
     * where the action occurred.
     */
    @FXML
    void produitClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Is expected to perform some actions or calculations upon receiving an event call.
     * 
     * @param event triggered event that initiated the call to the `profilclient` function.
     */
    @FXML
    void profilclient(ActionEvent event) {
    }
    /**
     * Loads a new FXML interface using `FXMLLoader`, creates a new scene and stage, and
     * replaces the current stage with the new one, closing the previous stage.
     * 
     * @param event ActionEvent object that triggered the function execution, providing
     * access to information about the event such as its source and target.
     */
    @FXML
    void MovieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Charges a new FXML file, creates a new scene, and attaches it to a new stage. It
     * also closes the current stage and shows the new stage.
     * 
     * @param event ActionEvent that triggers the `SerieClient()` method and provides
     * information about the source of the event.
     */
    @FXML
    void SerieClient(ActionEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Series-view.fxml"));
            Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("chat ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }
    }
    /**
     * Loads an FXML file and displays it in a new stage, blocking the current stage and
     * setting the new stage as the owner.
     * 
     * @param mouseEvent mouse event that triggered the execution of the `afficherProduit()`
     * method.
     */
    public void afficherProduit(MouseEvent mouseEvent) {
        // Obtenir la fenêtre précédente
        Window previousWindow = retour.getScene().getWindow();
        // Charger le fichier FXML de la page "AfficherProduit.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        try {
            Parent rootNode = fxmlLoader.load();
            Scene scene = new Scene(rootNode);
            // Créer une nouvelle fenêtre pour la page "AfficherProduit.fxml"
            Stage previousStage = new Stage();
            // Configurer la fenêtre précédente avec les propriétés nécessaires
            previousStage.setScene(scene);
            previousStage.setTitle(" Afficher Produit");
            // Afficher la fenêtre précédente de manière bloquante
            previousStage.initModality(Modality.APPLICATION_MODAL);
            previousStage.initOwner(previousWindow);
            previousStage.showAndWait();
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) retour.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace(); // Gérer l'exception selon vos besoins
        }
    }
}
