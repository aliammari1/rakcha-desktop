package com.esprit.controllers.films;

import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import com.esprit.models.films.Filmcoment;
import com.esprit.models.films.RatingFilm;
import com.esprit.models.users.Client;
import com.esprit.services.films.*;
import com.esprit.services.users.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilmUserController extends Application {

    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> yearsCheckBoxes = new ArrayList<>();

    FlowPane flowpaneFilm;

    FlowPane flowPaneactors;
    @FXML
    private Button closeDetailFilm1;

    @FXML
    private AnchorPane AnchorComments;
    private List<Film> l1 = new ArrayList<>();
    @FXML
    private Rating filmRate;
    @FXML
    private Button Cinema_Button;
    @FXML
    private Button event_button;
    @FXML
    private Button product;
    @FXML
    private Button trailer_Button;
    @FXML
    private AnchorPane anchorPane_Trailer;
    @FXML
    private Label labelavregeRate;
    @FXML
    private AnchorPane anchorPaneFilm;
    @FXML
    private ScrollPane ScrollPaneComments;

    @FXML
    private TextField txtAreaComments;
    @FXML
    private AnchorPane Anchore_Pane_filtrage;
    @FXML
    private AnchorPane detalAnchorPane;
    @FXML
    private Button closeDetailFilm;
    @FXML
    private TextArea descriptionDETAILfilm;
    @FXML
    private ImageView imagefilmDetail;
    @FXML
    private Label filmNomDetail;
    @FXML
    private VBox topthreeVbox;
    @FXML
    private ScrollPane filmScrollPane;
    private ScrollPane actorScrollPane;

    @FXML
    private TextField serach_film_user;
    @FXML
    private Button reserver_Film;
    @FXML
    private Button SerieButton;
    @FXML
    private ImageView qrcode;
    @FXML
    private ComboBox<String> top3combobox;
    @FXML
    private VBox topthreeVbox1;

    @FXML
    private ComboBox<String> tricomboBox;

    private int filmId;

    @FXML
    public static List<Film> rechercher(List<Film> liste, String recherche) {
        List<Film> resultats = new ArrayList<>();

        for (Film element : liste) {
            if (element.getNom() != null && element.getNom().contains(recherche)) {
                resultats.add(element);
            }
        }

        return resultats;
    }

    public void switchtopayment(String nom) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Paymentuser.fxml"));
        AnchorPane root = fxmlLoader.load();
        Stage stage = (Stage) reserver_Film.getScene().getWindow();
        PaymentuserController paymentuserController = fxmlLoader.getController();
        Client client = (Client) stage.getUserData();
        paymentuserController.setData(client, nom);
        Scene scene = new Scene(root, 1507, 855);
        stage.setScene(scene);
    }

    private void createfilmCards(List<Film> Films) {
        for (Film film : Films) {
            AnchorPane cardContainer = createFilmCard(film);
            flowpaneFilm.getChildren().add(cardContainer);


        }

    }

    public void initialize() {
        top3combobox.getItems().addAll("Top 3 Films", "Top 3 Actors");
        top3combobox.setValue("Top 3 Films");
        top3combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Top 3 Films")) {

                topthreeVbox1.setVisible(false);
                topthreeVbox.setVisible(true);
            } else if (newValue.equals("Top 3 Actors")) {

                ObservableList<Node> topthreevboxactorsChildren = topthreeVbox1.getChildren();
                topthreevboxactorsChildren.clear();
                topthreeVbox.setVisible(false);
                topthreeVbox1.setVisible(true);
                for (int i = 1; i < flowpaneFilm.getChildren().size() && i < 4; i++) {
                    topthreevboxactorsChildren.add(createActorDetails(i));
                }
                topthreeVbox1.setSpacing(10);

            }
        });
        tricomboBox.getItems().addAll("nom", "annederalisation");
        tricomboBox.setValue("");
        tricomboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            flowpaneFilm.getChildren().clear();
            List<Film> filmList = new FilmService().sort(t1);
            for (Film film : filmList) {
                flowpaneFilm.getChildren().add(createFilmCard(film));
            }

        });
        FilmService filmService1 = new FilmService();
        l1 = filmService1.read();

        serach_film_user.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Film> produitsRecherches = rechercher(l1, newValue);
            // Effacer la FlowPane actuelle pour afficher les nouveaux résultats
            flowpaneFilm.getChildren().clear();
            createfilmCards(produitsRecherches);
        });
        flowpaneFilm = new FlowPane();
        filmScrollPane.setContent(flowpaneFilm);
        filmScrollPane.setFitToWidth(true);
        filmScrollPane.setFitToHeight(true);
        topthreeVbox.setSpacing(10);
        FilmService filmService = new FilmService();
        String trailerURL = filmService.getTrailerFilm("garfield");
        flowpaneFilm.setHgap(10);
        flowpaneFilm.setVgap(10);


        closeDetailFilm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(false);
                anchorPaneFilm.setOpacity(1);
                anchorPaneFilm.setDisable(false);
            }
        });
        // Set the padding
        flowpaneFilm.setPadding(new Insets(10, 10, 10, 10));
        List<Film> filmList = new FilmService().read();
        for (Film film : filmList) {
            flowpaneFilm.getChildren().add(createFilmCard(film));
        }


        ObservableList<Node> topthreevboxChildren = topthreeVbox.getChildren();
        for (int i = 0; i < flowpaneFilm.getChildren().size() && i < 3; i++) {
            topthreevboxChildren.add(createtopthree(i));
        }

    }

    private void filterByName(String keyword) {
        for (Node node : flowpaneFilm.getChildren()) {
            AnchorPane filmCard = (AnchorPane) node;
            Label nomFilm = (Label) filmCard.lookup(".nomFilm"); // Supposons que le nom du film soit représenté par une classe CSS ".nomFilm"
            if (nomFilm != null) {
                boolean isVisible = nomFilm.getText().toLowerCase().contains(keyword); // Vérifie si le nom du film contient le mot-clé de recherche
                filmCard.setVisible(isVisible); // Définit la visibilité de la carte en fonction du résultat du filtrage
                filmCard.setManaged(isVisible); // Définit la gestion de la carte en fonction du résultat du filtrage
            }
        }
    }

    private AnchorPane createFilmCard(Film film) {
        AnchorPane copyOfAnchorPane = new AnchorPane();

        copyOfAnchorPane.setLayoutX(0);
        copyOfAnchorPane.setLayoutY(0);
        copyOfAnchorPane.setPrefSize(213, 334);
        copyOfAnchorPane.getStyleClass().add("anchorfilm");
        ImageView imageView = new ImageView();
        try {
            if (!film.getImage().isEmpty())
                imageView.setImage(new Image(film.getImage()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        imageView.setLayoutX(45);
        imageView.setLayoutY(7);
        imageView.setFitHeight(193);
        imageView.setFitWidth(132);
        imageView.getStyleClass().addAll("bg-white");

        Label nomFilm = new Label(film.getNom());
        nomFilm.setLayoutX(23);
        nomFilm.setLayoutY(200);
        nomFilm.setPrefSize(176, 32);
        nomFilm.setFont(new Font(18)); // Copy the font size
        nomFilm.getStyleClass().addAll("labeltext");

        Label ratefilm = new Label(film.getNom());
        ratefilm.setLayoutX(15);
        ratefilm.setLayoutY(222);
        ratefilm.setPrefSize(176, 32);
        ratefilm.setFont(new Font(18)); // Copy the font size
        ratefilm.getStyleClass().addAll("labeltext");
        double rate = new RatingFilmService().getavergerating(film.getId());
        ratefilm.setText(rate + "/5");

        RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film.getId(),/*(Client) stage.getUserData()*/2);
        filmRate.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);

        FontAwesomeIconView etoile = new FontAwesomeIconView();
        etoile.setGlyphName("STAR");
        etoile.setLayoutX(128);
        etoile.setLayoutY(247);
        etoile.setSize("25");
        etoile.setFill(Color.web("#f2b604"));

        Button button = new Button("reserve");
        button.setLayoutX(22);
        button.setLayoutY(278);
        button.setPrefSize(172, 42);
        button.getStyleClass().addAll("sale");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    switchtopayment(nomFilm.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Hyperlink hyperlink = new Hyperlink("Details");
        hyperlink.setLayoutX(89);
        hyperlink.setLayoutY(251);

        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                detalAnchorPane.setVisible(true);

                anchorPaneFilm.setOpacity(0.26);
                anchorPaneFilm.setDisable(true);
                Film film1 = new Film(film);
                filmId = film.getId();
                filmNomDetail.setText(film1.getNom());
                descriptionDETAILfilm.setText(film1.getDescription() + "\nTime:" + film1.getDuree() + "\nYear:" + film1.getAnnederalisation() + "\nCategories: " + new FilmcategoryService().getCategoryNames(film1.getId()) + "\nActors: " + new ActorfilmService().getActorsNames(film1.getId()));
                imagefilmDetail.setImage(new Image(film1.getImage()));
                double rate = new RatingFilmService().getavergerating(film1.getId());
                labelavregeRate.setText(rate + "/5");
                RatingFilm ratingFilm = new RatingFilmService().ratingExiste(film1.getId(),/*(Client) stage.getUserData()*/2);
                Rating rateFilm = new Rating();
                rateFilm.setLayoutX(103);
                rateFilm.setLayoutY(494);
                rateFilm.setPrefSize(199, 35);
                rateFilm.setRating(ratingFilm != null ? ratingFilm.getRate() : 0);


                //Stage stage = (Stage) hyperlink.getScene().getWindow();
                final String text = film1.getNom();// Créer un objet QRCodeWriter pour générer le QR code
                final String url = FilmService.getIMDBUrlbyNom(text);

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix;
                try {
                    bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                // Convertir la matrice de bits en image BufferedImage
                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                qrcode.setOnMouseClicked(e -> {
                    HostServices hostServices = getHostServices();

                    // Open the URL in the default system browser
                    hostServices.showDocument(url);
                });                //HBox qrCodeImgModel = (HBox) ((Node) event.getSource()).getScene().lookup("#qrCodeImgModel");
                qrcode.setVisible(true);
                rateFilm.ratingProperty().addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                        RatingFilmService ratingFilmService = new RatingFilmService();
                        RatingFilm ratingFilm = ratingFilmService.ratingExiste(film1.getId(), 2/*(Client) stage.getUserData()*/);
                        System.out.println("---------   " + film1.getId());
                        if (ratingFilm != null)
                            ratingFilmService.delete(ratingFilm);
                        ratingFilmService.create(new RatingFilm(film1, /*(Client) stage.getUserData()*/(Client) new UserService().getUserById(2), t1.intValue()));
                        double rate = new RatingFilmService().getavergerating(film1.getId());
                        labelavregeRate.setText(rate + "/5");
                        ratefilm.setText(rate + "/5");
                        topthreeVbox.getChildren().clear();
                        for (int i = 0; i < 3; i++) {
                            topthreeVbox.getChildren().add(createtopthree(i));
                        }
                    }

                });

                trailer_Button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        anchorPane_Trailer.getChildren().forEach(node -> {
                            node.setDisable(false);
                        });
                        WebView webView = new WebView();
                        webView.getEngine().load(new FilmService().getTrailerFilm(film1.getNom()));
                        anchorPane_Trailer.setVisible(true);
                        anchorPane_Trailer.getChildren().add(webView);
                        anchorPane_Trailer.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                    anchorPane_Trailer.getChildren().forEach(node -> {
                                        node.setDisable(true);
                                    });
                                    anchorPane_Trailer.setVisible(false);
                                }
                            }
                        });
                    }
                });
                detalAnchorPane.getChildren().add(rateFilm);
            }
        });
        // Copy CSS classes
        copyOfAnchorPane.getChildren().addAll(imageView, nomFilm, button, hyperlink, ratefilm, etoile);
        return copyOfAnchorPane;
    }

    public AnchorPane createActorDetails(int actorPlacement) {
        ActorService as = new ActorService();
        Actor actor = as.getActorByPlacement(actorPlacement);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefSize(244, 226);
        anchorPane.getStyleClass().add("meilleurfilm");

        if (actor != null) {
            ImageView imageView = new ImageView();
            try {
                if (!actor.getImage().isEmpty()) {
                    imageView.setImage(new Image(actor.getImage()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            imageView.setLayoutX(21);
            imageView.setLayoutY(21);
            imageView.setFitHeight(167);
            imageView.setFitWidth(122);
            imageView.getStyleClass().addAll("bg-white");

            // Combine actor name and number of appearances in one label
            String actorDetailsText = actor.getNom().trim() + ": " + actor.getNumberOfAppearances() + " Films";
            System.out.println(actorDetailsText);

            Label actorDetails = new Label(actorDetailsText);
            actorDetails.setLayoutX(153);
            actorDetails.setLayoutY(8); // Adjusted to top, similar to imageView
            actorDetails.setPrefSize(500, 70);
            actorDetails.setFont(new Font(22));
            actorDetails.setTextFill(Color.WHITE);


            // Actor biography
            TextArea actorBio = new TextArea(actor.getBiographie());
            actorBio.setLayoutX(153);
            actorBio.setLayoutY(75); // Positioned directly under actorDetails label
            actorBio.setPrefSize(400, 100); // You can adjust this size as needed
            actorBio.setWrapText(true);
            actorBio.setEditable(false);
            // Set the background of the TextArea to transparent and text color to white
            actorBio.setStyle("-fx-control-inner-background:#de3030 ; -fx-text-fill: WHITE; -fx-opacity: 1;");

            anchorPane.getChildren().addAll(imageView, actorDetails, actorBio);
        }
        return anchorPane;
    }


    public AnchorPane createtopthree(int filmRank) {
        List<RatingFilm> ratingFilmList = new RatingFilmService().getavergeratingSorted();
        AnchorPane anchorPane = new AnchorPane();
        if (ratingFilmList.size() > filmRank) {
            anchorPane.setLayoutX(0);
            anchorPane.setLayoutY(0);
            anchorPane.setPrefSize(544, 226);
            anchorPane.getStyleClass().add("meilleurfilm");
            RatingFilm ratingFilm = ratingFilmList.get(filmRank);
            ImageView imageView = new ImageView();
            try {
                if (!ratingFilm.getId_film().getImage().isEmpty())
                    imageView.setImage(new Image(ratingFilm.getId_film().getImage()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            imageView.setLayoutX(21);
            imageView.setLayoutY(21);
            imageView.setFitHeight(167);
            imageView.setFitWidth(122);
            imageView.getStyleClass().addAll("bg-white");

            Label nomFilm = new Label(ratingFilm.getId_film().getNom());
            nomFilm.setLayoutX(153);
            nomFilm.setLayoutY(87);
            nomFilm.setPrefSize(205, 35);
            nomFilm.setFont(new Font(22));
            nomFilm.setTextFill(Color.WHITE);// Copy the font size

            Button button = new Button("reserve");
            button.setLayoutX(346);
            button.setLayoutY(154);
            button.setPrefSize(172, 42);
            button.getStyleClass().addAll("sale");

            Rating rating = new Rating();
            rating.setLayoutX(344);
            rating.setLayoutY(38);
            rating.setPrefSize(176, 32);
            rating.setPartialRating(true);
            double rate = new RatingFilmService().getavergerating(ratingFilm.getId_film().getId());
            rating.setRating(rate);
            rating.setDisable(true);

            anchorPane.getChildren().addAll(nomFilm, button, rating, imageView);
        }
        return anchorPane;
    }//////////////////////////////////////////////////////////////

    private List<Integer> getCinemaYears() {
        FilmService cinemaService = new FilmService();
        List<Film> cinemas = cinemaService.read();
        // Extraire les années de réalisation uniques des films
        return cinemas.stream()
                .map(Film::getAnnederalisation)
                .distinct()
                .collect(Collectors.toList());
    }

    @FXML
    void filtrer(ActionEvent event) {
        flowpaneFilm.setOpacity(0.5);
        Anchore_Pane_filtrage.setVisible(true);
        // Nettoyer la liste des cases à cocher
        yearsCheckBoxes.clear();
        // Récupérer les années de réalisation uniques depuis la base de données
        List<Integer> years = getCinemaYears();

        // Créer des VBox pour les années de réalisation
        VBox yearsCheckBoxesVBox = new VBox();
        Label yearLabel = new Label("Années de réalisation");
        yearLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        yearsCheckBoxesVBox.getChildren().add(yearLabel);
        for (Integer year : years) {
            CheckBox checkBox = new CheckBox(String.valueOf(year));
            yearsCheckBoxesVBox.getChildren().add(checkBox);
            yearsCheckBoxes.add(checkBox);
        }
        yearsCheckBoxesVBox.setLayoutX(25);
        yearsCheckBoxesVBox.setLayoutY(120);

        // Ajouter les VBox dans le FilterAnchor
        Anchore_Pane_filtrage.getChildren().addAll(yearsCheckBoxesVBox);
        Anchore_Pane_filtrage.setVisible(true);
    }


    @FXML
    void closercommets(ActionEvent event) {
        detalAnchorPane.setOpacity(1);
        AnchorComments.setVisible(false);
        detalAnchorPane.setVisible(true);
    }

    @FXML
    void filtrercinema(ActionEvent event) {
        flowpaneFilm.setOpacity(1);
        Anchore_Pane_filtrage.setVisible(false);

        // Récupérer les années de réalisation sélectionnées
        List<Integer> selectedYears = getSelectedYears();

        // Filtrer les films en fonction des années de réalisation sélectionnées
        List<Film> filteredCinemas = l1.stream()
                .filter(cinema -> selectedYears.isEmpty() || selectedYears.contains(cinema.getAnnederalisation()))
                .collect(Collectors.toList());

        // Afficher les films filtrés
        flowpaneFilm.getChildren().clear();
        createfilmCards(filteredCinemas);
    }

    private List<Integer> getSelectedYears() {
        // Récupérer les années de réalisation sélectionnées dans l'AnchorPane de filtrage
        return yearsCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(checkBox -> Integer.parseInt(checkBox.getText()))
                .collect(Collectors.toList());
    }

    public void switchtoajouterCinema(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) product.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchtevent(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) event_button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchtcinemaaa(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DashboardClientCinema.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) Cinema_Button.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchtoajouterproduct(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) product.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchtoSerie(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SeriesClient.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) SerieButton.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 700);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addCommentaire() {
        String message = txtAreaComments.getText();
        if (message.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Commentaire vide");
            alert.setContentText("Add Comment");
            alert.showAndWait();
        } else {
            Filmcoment commentaire = new Filmcoment(message, (Client) new UserService().getUserById(4), new FilmService().getFilm(filmId));
            System.out.println(commentaire + " " + new UserService().getUserById(4));
            FilmcomentService commentaireCinemaService = new FilmcomentService();
            commentaireCinemaService.create(commentaire);
            txtAreaComments.clear();
        }
    }

    @FXML
    void AddComment(MouseEvent event) {
        addCommentaire();
        displayAllComments(filmId);
    }

    @FXML
    void afficherAnchorComment(MouseEvent event) {
        AnchorComments.setVisible(true);
        displayAllComments(filmId);

    }

    private HBox addCommentToView(Filmcoment commentaire) {
        // Création du cercle pour l'image de l'utilisateur
        Circle imageCircle = new Circle(20);
        imageCircle.setFill(Color.TRANSPARENT);
        imageCircle.setStroke(Color.BLACK);
        imageCircle.setStrokeWidth(2);

        // Image de l'utilisateur
        String imageUrl = commentaire.getUser_id().getPhoto_de_profil();
        Image userImage;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            userImage = new Image(imageUrl);
        } else {
            // Chargement de l'image par défaut
            userImage = new Image(getClass().getResourceAsStream("/Logo.png"));
        }

        ImageView imageView = new ImageView(userImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Positionner l'image au centre du cercle
        imageView.setTranslateX(2 - imageView.getFitWidth() / 2);
        imageView.setTranslateY(2 - imageView.getFitHeight() / 2);

        // Ajouter l'image au cercle
        Group imageGroup = new Group();
        imageGroup.getChildren().addAll(imageCircle, imageView);

        // Création de la boîte pour l'image et la bordure du cercle
        HBox imageBox = new HBox();
        imageBox.getChildren().add(imageGroup);

        // Création du conteneur pour la carte du commentaire
        HBox cardContainer = new HBox();
        cardContainer.setStyle("-fx-background-color: white; -fx-padding: 5px ; -fx-border-radius: 8px; -fx-border-color: #000; -fx-background-radius: 8px; ");

        // Nom de l'utilisateur
        Text userName = new Text(commentaire.getUser_id().getFirstName() + " " + commentaire.getUser_id().getLastName());
        userName.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-style: bold;");

        // Commentaire
        Text commentText = new Text(commentaire.getComment());
        commentText.setStyle("-fx-font-family: 'Arial'; -fx-max-width: 300 ;");
        commentText.setWrappingWidth(300); // Définir une largeur maximale pour le retour à la ligne automatique


        // Création de la boîte pour le texte du commentaire
        VBox textBox = new VBox();


        // Ajouter le nom d'utilisateur et le commentaire à la boîte de texte
        textBox.getChildren().addAll(userName, commentText);

        // Ajouter la boîte d'image et la boîte de texte à la carte du commentaire
        cardContainer.getChildren().addAll(textBox);

        // Création du conteneur pour l'image, le cercle et la carte du commentaire
        HBox contentContainer = new HBox();
        contentContainer.setPrefHeight(50);
        contentContainer.setStyle("-fx-background-color: transparent; -fx-padding: 10px"); // Arrière-plan transparent
        contentContainer.getChildren().addAll(imageBox, cardContainer);

        // Ajouter le conteneur principal au ScrollPane
        ScrollPaneComments.setContent(contentContainer);
        return contentContainer;
    }

    private List<Filmcoment> getAllComment(int filmId) {
        FilmcomentService commentaireCinemaService = new FilmcomentService();
        List<Filmcoment> allComments = commentaireCinemaService.read(); // Récupérer tous les commentaires
        List<Filmcoment> cinemaComments = new ArrayList<>();

        // Filtrer les commentaires pour ne conserver que ceux du cinéma correspondant
        for (Filmcoment comment : allComments) {
            if (comment.getFilm_id().getId() == filmId) {
                cinemaComments.add(comment);
            }
        }

        return cinemaComments;
    }

    private void displayAllComments(int filmId) {
        List<Filmcoment> comments = getAllComment(filmId);
        VBox allCommentsContainer = new VBox();

        for (Filmcoment comment : comments) {
            HBox commentView = addCommentToView(comment);
            allCommentsContainer.getChildren().add(commentView);
        }

        ScrollPaneComments.setContent(allCommentsContainer);
    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}

