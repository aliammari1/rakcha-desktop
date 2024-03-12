package com.esprit.controllers.produits;

import com.esprit.models.produits.Avis;
import com.esprit.models.produits.Panier;
import com.esprit.models.produits.Produit;
import com.esprit.models.users.Client;
import com.esprit.services.produits.AvisService;
import com.esprit.services.produits.PanierService;
import com.esprit.services.produits.ProduitService;
import com.esprit.services.users.UserService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.Rating;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



public class DetailsProduitClientController implements Initializable {

    @FXML

    public FlowPane detailFlowPane;


    @FXML
    private AnchorPane anchordetail;

    @FXML
    private AnchorPane panierAnchorPane;

    @FXML

    public FlowPane panierFlowPane;


    @FXML
    public TextField SearchBar;

    @FXML
    private FontAwesomeIconView retour;

    private int produitId;

    private Button addToCartButton;
    @FXML
    private AnchorPane top3anchorpane;

    @FXML
    private FlowPane topthreeVbox;

    private int quantiteSelectionnee = 1; // Initialiser à 1 par défaut


    PanierService panierService = new PanierService();
    Panier panier = new Panier();








    // Méthode pour initialiser l'ID du produit
    public void setProduitId(int produitId) throws IOException {
        this.produitId = produitId;

        // Initialiser les détails du produit après avoir défini l'ID
        initDetailsProduit();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAcceptedTop3();
        // Attacher un gestionnaire d'événements au clic de la souris sur l'icône de retour
        retour.setOnMouseClicked(event -> afficherProduit());



        }

    public int getQuantiteSelectionnee() {
        return quantiteSelectionnee;
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
                    HBox panierContainer = createPanierCard(produit);
                    detailFlowPane.getChildren().add(cardContainer);
                    panierFlowPane.getChildren().add(panierContainer);
                },
                () -> System.err.println("Produit non trouvé avec l'ID : " + produitId)
        );


    }


    private HBox createProduitCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-padding: 10px 0 0  50px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();

        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(10);
        imageView.setLayoutY(3);
        imageView.setFitWidth(370);
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
        nameLabel.setLayoutX(400);
        nameLabel.setLayoutY(30);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique




        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setLayoutX(410);
        priceLabel.setLayoutY(200);


        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.web("#392c2c")); // Définir la même couleur de texte que descriptionText
        descriptionLabel.setLayoutX(410);
        descriptionLabel.setLayoutY(95);
        descriptionLabel.setMaxWidth(250); // Ajuster la largeur maximale selon vos besoins
        descriptionLabel.setWrapText(true); // Activer le retour à la ligne automatique




        // Bouton Ajouter au Panier
        Button addToCartButton = new Button("Add to Cart", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        addToCartButton.setLayoutX(435);
        addToCartButton.setLayoutY(300);
        //addToCartButton.getStyleClass().add("sale"); // Style du bouton
        addToCartButton.setStyle("-fx-background-color: #dd4f4d;\n" +
                "    -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;");
        addToCartButton.setOnAction(


                      event -> {



                        int produitId = produit.getId_produit();
                        int quantity = 1; // Vous pouvez ajuster la quantité en fonction de vos besoins
                        ajouterAuPanier(produitId, quantity);

                          panierAnchorPane.setVisible(true);
                          detailFlowPane.setVisible(true);
                          detailFlowPane.setOpacity(0.2);
                          top3anchorpane.setVisible(false);

                    });


        Avis avis = new Avis();

        double rate = new AvisService().getavergerating(produit.getId_produit());
        System.out.println(BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR));

        // Champ de notation (Rating)
        Rating rating = new Rating();
        rating.setLayoutX(410);
        rating.setLayoutY(390);
        rating.setMax(5);
        rating.setRating(avis.getNote()); // Vous pouvez ajuster en fonction de la valeur du produit



        String format = String.format("%.1f/5", BigDecimal.valueOf(rate).setScale(1, RoundingMode.FLOOR).doubleValue());
        Label etoilelabel = new Label(format);
        etoilelabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        etoilelabel.setStyle("-fx-text-fill: #333333;");
        etoilelabel.setLayoutX(410);
        etoilelabel.setLayoutY (230);

        FontAwesomeIconView iconeetoile = new FontAwesomeIconView(FontAwesomeIcon.STAR);
        iconeetoile.setGlyphName("STAR");
        iconeetoile.setFill(Color.YELLOW);
        iconeetoile.setSize("20");
        iconeetoile.setLayoutX(465);
        iconeetoile.setLayoutY(250);

        Avis avi = new AvisService().ratingExiste(produit.getId_produit(),/*(Client) stage.getUserData()*/4);
        rating.setRating(avi != null ? avi.getNote() : 0);
        //Stage stage = (Stage) hyperlink.getScene().getWindow();
        rating.ratingProperty().addListener((observableValue, number, t1) -> {
            AvisService avisService = new AvisService();
            Avis avi1 = avisService.ratingExiste(produit.getId_produit(), 4 /*(Client) stage.getUserData()*/);
            if (avi != null)
                avisService.delete(avi1);
            avisService.create(new Avis(/*(Client) stage.getUserData()*/(Client) new UserService().getUserById(4),t1.intValue(),produit));
            double rate1 = new AvisService().getavergerating(produit.getId_produit());
            // Formater le texte avec une seule valeur après la virgule
            String formattedRate = String.format("%.1f/5", BigDecimal.valueOf(rate1).setScale(1, RoundingMode.FLOOR).doubleValue());

            System.out.println(formattedRate);
            etoilelabel.setText(formattedRate);

        });

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, priceLabel,rating,  etoilelabel,  addToCartButton,iconeetoile);

                    cardContainer.getChildren().add(card);

        return cardContainer;
    }



    private void ajouterAuPanier(int produitId, int quantity) {
        ProduitService produitService = new ProduitService();
        PanierService panierService=new PanierService();
        UserService usersService=new UserService();
        // Vérifier le stock disponible avant d'ajouter au panier
        if (produitService.verifierStockDisponible(produitId, quantity)) {
            Produit produit = produitService.getProduitById(produitId);
            Panier panier=new Panier();
            panier.setProduit(produit);
            panier.setQuantity(quantity);
            panier.setUser(usersService.getUserById(4));
            panierService.create(panier);
            afficherpanier(); // Utilisez le produit ajouté pour afficher dans le panier
        } else {
            // Afficher un message d'avertissement sur le stock insuffisant
            System.out.println("Stock insuffisant pour le produit avec l'ID : " + produitId);
        }
    }




    @FXML
    public void afficherProduit() {


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



    private void afficherpanier (){

        // Initialiser la visibilité des AnchorPane

        panierFlowPane.setVisible(true);
        detailFlowPane.setVisible(true);
        detailFlowPane.setOpacity(0.2);
        //top3anchorpane.setVisible(false);


    }


    private HBox createPanierCard(Produit produit) {
        // Créer une carte pour le produit avec ses informations

        HBox panierContainer = new HBox();
        panierContainer.setStyle("-fx-padding: 0px 0 0 20px;"); // Ajout de remplissage à gauche pour le décalage

        AnchorPane card = new AnchorPane();


        // Ajouter un label "Card"
        Label cartLabel = new Label("My Cart");
        cartLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 25));
        cartLabel.setTextFill(Color.web("#B40C0C")); // Définir la couleur du texte
        cartLabel.setPrefWidth(230);
        cartLabel.setLayoutY(30);

        // Centrer le texte dans le label
        cartLabel.setAlignment(Pos.CENTER);



        // Image du Produit
        ImageView imageView = new ImageView();
        imageView.setLayoutX(50);
        imageView.setLayoutY(75);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);


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
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(10);
        nameLabel.setLayoutY (235);
        nameLabel.setMaxWidth(200); // Ajuster la largeur maximale selon vos besoins
        nameLabel.setWrapText(true); // Activer le retour à la ligne automatique

        // Prix du Produit
        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        priceLabel.setLayoutX(10);
        priceLabel.setLayoutY(270);



       // Champ de texte pour la quantité
        Label quantiteLabel = new Label("Quantité : 1 " );
        quantiteLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        quantiteLabel.setLayoutX(30);
        quantiteLabel.setLayoutY(290);




        Label sommeTotaleLabel = new Label("Somme totale : " + produit.getPrix()+ " DT");
        sommeTotaleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        sommeTotaleLabel.setLayoutX(30);
        sommeTotaleLabel.setLayoutY(320);




        // Bouton Ajouter au Panier
        Button commandebutton = new Button("Order", new FontAwesomeIconView(FontAwesomeIcon.CART_PLUS));
        commandebutton.setLayoutX(50);
        commandebutton.setLayoutY(350);
        commandebutton.setPrefWidth(120);
        commandebutton.setPrefHeight(35);
        commandebutton.setStyle("-fx-background-color: #624970;\n" +
                " -fx-text-fill: #FCE19A;" +
                "   -fx-font-size: 12px;\n" +
                "     -fx-font-weight: bold;\n" +
                " -fx-background-color: #6f7b94"); // Style du bouton
        commandebutton.setOnAction(
                event -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DesignProduitAdmin.fxml"));

                    try {

                        Parent root = null;
                        root = fxmlLoader.load();
                        //Parent rootNode = fxmlLoader.load();
                        //Scene scene = new Scene(rootNode);

                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Stage newStage = new Stage();
                        newStage.setScene(new Scene(root,1280,700));
                        newStage.setTitle("my cart");
                        newStage.show();

                        // Fermer la fenêtre actuelle
                        currentStage.close();
                    } catch (IOException e) {
                        e.printStackTrace(); // Affiche l'erreur dans la console (vous pourriez le remplacer par une boîte de dialogue)
                        System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
                    }


                });


        // Bouton Ajouter au Panier
        Button achatbutton = new Button("continue shopping");
        achatbutton.setLayoutX(50);
        achatbutton.setLayoutY(400);
        achatbutton.setPrefHeight(30);
        achatbutton.setStyle(" -fx-background-color: #466288;\n" +
                "    -fx-text-fill: #FCE19A;\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-padding: 10px 10px;");
        achatbutton.setOnAction(
                event -> {
                    fermerPanierCard(panierContainer);



                });


        // Icône de fermeture (close)
        FontAwesomeIconView closeIcon = new FontAwesomeIconView();
        closeIcon.setGlyphName("TIMES_CIRCLE");
        closeIcon.setSize("20");
        closeIcon.setLayoutX(220);
        closeIcon.setLayoutY(20);
        closeIcon.getStyleClass().add("close"); // Style de l'icône





        // Attachez un gestionnaire d'événements pour fermer la carte du panier
        closeIcon.setOnMouseClicked(event -> fermerPanierCard(panierContainer));



        card.getChildren().addAll(cartLabel,imageView, nameLabel, priceLabel,quantiteLabel,sommeTotaleLabel,achatbutton,commandebutton,closeIcon);
        panierContainer.getChildren().add(card);

        return panierContainer;
    }

    private double prixProduit(int idProduit, int quantity) {
        ProduitService produitService = new ProduitService();
        double prixUnitaire = produitService.getPrixProduit(idProduit);
        return quantity * prixUnitaire;
    }




    private void fermerPanierCard(HBox panierContainer) {
        // Rendre la carte du panier invisible
        // Rendre panierFlowPane invisible
        panierFlowPane.setVisible(false);

        // Rendre detailFlowPane visible et ajuster l'opacité à 1
        detailFlowPane.setVisible(true);
        detailFlowPane.setOpacity(1);

        anchordetail.setVisible(true);
        anchordetail.setOpacity(1);

        top3anchorpane.setVisible(true);
        top3anchorpane.setOpacity(1);

    }



    @FXML
    void panier(MouseEvent event) {
        try {
            // Charger la nouvelle interface PanierProduit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierProduit.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la nouvelle interface
            Scene scene = new Scene(root);

            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panier des produits");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }


    }



    @FXML
    void commentaire(MouseEvent event) {


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
            stage.setTitle("Commentaire des produits");
            stage.show();

            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer l'exception d'entrée/sortie
        }

    }
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

    @FXML
    void profilclient(ActionEvent event) {



    }


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



    public void loadAcceptedTop3() {

        ProduitService produitService = new ProduitService();

        try {
            List<Produit> produits = produitService.getProduitsOrderByQuantityAndStatus();

            if (produits.size() < 3) {
                System.out.println("Pas assez de produits disponibles");
                return;
            }


            List<Produit> top3Produits = produits.subList(0, 3);
            int j =0;
            for (Produit produit : top3Produits) {
                System.out.println(produit.getId_produit());
                VBox cardContainer = createtopthree(produit);
                System.out.println("------------------"+j+(cardContainer.getChildren()));
                topthreeVbox.getChildren().add(cardContainer);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Une erreur est survenue lors du chargement des produits");
        }
    }




    public VBox createtopthree(Produit produit) {
        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-padding: 20px 0 0 30px;"); // Add left padding

        AnchorPane card = new AnchorPane();
        card.setLayoutX(0);
        card.setLayoutY(0);
        cardContainer.setPrefWidth(255);


        card.getStyleClass().add("meilleurproduit");
        cardContainer.setSpacing(10);

        // Image of the product
        ImageView imageView = new ImageView();
        imageView.setLayoutX(5);
        imageView.setLayoutY(21);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        try {
            Blob blob = produit.getImage();
            if (blob != null) {
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                Image image = new Image(new ByteArrayInputStream(bytes));
                imageView.setImage(image);
            } else {
                // Use a default image if Blob is null
                Image defaultImage = new Image(getClass().getResourceAsStream("defaultImage.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions during image loading
            System.out.println("Une erreur est survenue lors du chargement de l'image");
        }

        imageView.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;

                System.out.println("Clique sur le nom du produit. ID du produit : " + produit.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(produit.getId_produit());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Produit");
                stage.show();
                currentStage.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });


        // Product name
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        nameLabel.setLayoutX(60); // Adjust X position
        nameLabel.setLayoutY(25);
        nameLabel.setMaxWidth(200); // Adjust max width
        nameLabel.setWrapText(true);
        nameLabel.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduitClient.fxml"));

                Parent root = null;

                System.out.println("Clique sur le nom du produit. ID du produit : " + produit.getId_produit());
                root = loader.load();
                // Récupérez le contrôleur et passez l'id du produit lors de l'initialisation
                DetailsProduitClientController controller = loader.getController();
                controller.setProduitId(produit.getId_produit());

                // Afficher la nouvelle interface
                Stage stage = new Stage();
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1280, 700));
                stage.setTitle("Détails du Produit");
                stage.show();
                currentStage.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });




        Label priceLabel = new Label(" " + produit.getPrix() + " DT");
        priceLabel.setLayoutX(60);
        priceLabel.setLayoutY(55);
        priceLabel.setFont(Font.font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: black;");


        card.getChildren().addAll(nameLabel, priceLabel, imageView);
        cardContainer.getChildren().addAll(card); // Add vertical space

        return cardContainer;
    }








}

