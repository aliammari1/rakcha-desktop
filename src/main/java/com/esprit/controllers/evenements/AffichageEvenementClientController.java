package com.esprit.controllers.evenements;

import com.esprit.models.evenements.Evenement;
import com.esprit.models.evenements.Feedback;
import com.esprit.models.evenements.Participation;
import com.esprit.models.users.Client;
import com.esprit.services.evenements.EvenementService;
import com.esprit.services.evenements.FeedbackEvenementService;
import com.esprit.services.evenements.ParticipationService;
import com.esprit.services.evenements.SmsService;
import com.esprit.services.users.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AffichageEvenementClientController {

    private final ObservableList<Evenement> masterData = FXCollections.observableArrayList();
    private final ObservableList<Evenement> masterData2 = FXCollections.observableArrayList();
    private final List<Evenement> l1 = new ArrayList<>();
    private final List<CheckBox> addressCheckBoxes = new ArrayList<>();
    private final List<CheckBox> statusCheckBoxes = new ArrayList<>();
    @FXML
    private AnchorPane FilterAnchor;
    @FXML
    private ListView<Evenement> listeEvents;
    @FXML
    private AnchorPane anchplanning;
    @FXML
    private FlowPane planningFlowPane;
    @FXML
    private Button bHistory;
    @FXML
    private Button bSend;
    @FXML
    private ComboBox<String> cbeventname;
    @FXML
    private TextArea taComment;
    @FXML
    private TableColumn<Evenement, String> tcCategorieE;
    @FXML
    private TableColumn<Evenement, Date> tcDDE;
    @FXML
    private TableColumn<Evenement, Date> tcDFE;
    @FXML
    private TableColumn<Evenement, String> tcDescriptionE;
    @FXML
    private TableColumn<Evenement, String> tcEtatE;
    @FXML
    private TableColumn<Evenement, String> tcLieuE;
    @FXML
    private TableColumn<Evenement, String> tcNomE;
    @FXML
    private TextField tfRechercheEc;
    @FXML
    private FlowPane eventFlowPane;

    @FXML
    private Button bWeather;
    private TilePane tilePane;

    @FXML
    void initialize() {

        EvenementService es = new EvenementService();
        for (Evenement e : es.read()) {
            cbeventname.getItems().add(e.getNom());
        }
        tfRechercheEc.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
            //filterCategorieEvenements(newValue.trim());
        });

        afficherliste();
        // setupSearchFilter();
        listeEvents.setVisible(true);
        anchplanning.setVisible(false);


    }

    @FXML
    private void search(String keyword) {
        EvenementService es = new EvenementService();
        ObservableList<Evenement> filteredList = FXCollections.observableArrayList();
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredList.addAll(es.read());
        } else {
            for (Evenement evenement : es.read()) {
                if (evenement.getNom().toLowerCase().contains(keyword.toLowerCase()) || evenement.getLieu().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getEtat().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        evenement.getNom_categorieEvenement().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(evenement);
                }
            }
        }
        listeEvents.setItems(filteredList);
    }


    public void afficherliste() {
        EvenementService es = new EvenementService();
        listeEvents.getItems().clear();

        listeEvents.setCellFactory(param -> {
            return new ListCell<Evenement>() {
                @Override
                protected void updateItem(Evenement item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        // Créez un AnchorPane pour chaque evenement
                        AnchorPane anchorPane = new AnchorPane();
                        anchorPane.setPrefSize(400, 200); // Définissez la taille souhaitée

                        // Ajoutez une ImageView pour afficher l'image
                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(150);
                        imageView.setFitHeight(150);
                        imageView.setPreserveRatio(true);

                        Blob blob = item.getAffiche_event();
                        InputStream inputStream = null;
                        try {
                            inputStream = blob.getBinaryStream();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        FileOutputStream outputStream = null; // Provide appropriate file name and extension
                        try {
                            outputStream = new FileOutputStream("image.png");
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;

                        while (true) {
                            try {
                                if ((bytesRead = inputStream.read(buffer)) == -1) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                outputStream.write(buffer, 0, bytesRead);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        File file = new File("image.png");

                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);

                        // Ajoutez des composants à l'AnchorPane (toutes les informations de la série)
                        Label nameLabel = new Label("Name : " + item.getNom());
                        nameLabel.setStyle(
                                "-fx-text-fill: #000000;" +
                                        "-fx-font-size: 40px;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label ddLabel = new Label("Start Date : " + item.getDateDebut());
                        ddLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label dfLabel = new Label("End Date : " + item.getDateFin());
                        dfLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;\n" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label lieuLabel = new Label("Location : " + item.getLieu());
                        lieuLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;\n" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label categorieLabel = new Label("Category : " + item.getNom_categorieEvenement());
                        categorieLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;\n" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label etatLabel = new Label("Status : " + item.getEtat());
                        etatLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;\n" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Label descriptionLabel = new Label("Description : " + item.getDescription());
                        descriptionLabel.setStyle(
                                " -fx-text-fill: #000000;" +
                                        "   -fx-font-size: 15px;\n" +
                                        "     -fx-font-weight: bold;\n" +
                                        "-fx-font-family: 'Bebas Neue';"

                        );
                        Button joinButton = new Button("Join Event");


                        joinButton.setStyle("-fx-background-color: #800000;\n" +
                                " -fx-text-fill: #FCE19A;" +
                                "   -fx-font-size: 17.5px;\n" +
                                "     -fx-font-weight: bold;\n" +
                                " -fx-background-color: #6f7b94");
                        joinButton.setOnAction(
                                event -> {

                                    joinButton.setStyle("-fx-background-color: #800000;\n" +
                                            " -fx-text-fill: #FCE18B;" +
                                            "   -fx-font-size: 17.5px;\n" +
                                            "     -fx-font-weight: bold;\n" +
                                            " -fx-background-color: #6f7c84");

                                    Label personsLabel = new Label("Number of persons: ");
                                    TextField personstf = new TextField();
                                    Button confirmButton = new Button("Confirm");
                                    confirmButton.setStyle("-fx-background-color: #624970;\n" +
                                            " -fx-text-fill: #FCE19A;" +
                                            "   -fx-font-size: 12px;\n" +
                                            "     -fx-font-weight: bold;\n" +
                                            " -fx-background-color: #6f7b94");
                                    confirmButton.setOnAction(
                                            event2 -> {
                                                ParticipationService ps = new ParticipationService();
                                                UserService us = new UserService();
                                                Stage stage = (Stage) confirmButton.getScene().getWindow();
                                                ps.create(new Participation(item, (Client) stage.getUserData(), Integer.parseInt(personstf.getText())));
                                                SmsService.sendSms("+21622757828", String.format("   Thank you for your reservation : your '%s' tickets for the event : '%s' have been confirmed !", personstf.getText(), item.getNom()));

                                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                alert.setTitle("Reservation Complete !");
                                                alert.setContentText("Reservation Complete !");
                                                alert.show();


                                            });
                                    AnchorPane.setTopAnchor(personsLabel, 119.0);
                                    AnchorPane.setLeftAnchor(personsLabel, 570.0);
                                    AnchorPane.setTopAnchor(personstf, 120.0);
                                    AnchorPane.setLeftAnchor(personstf, 725.0);
                                    AnchorPane.setTopAnchor(confirmButton, 160.0);
                                    AnchorPane.setLeftAnchor(confirmButton, 700.0);
                                    anchorPane.getChildren().addAll(personsLabel, personstf, confirmButton);
                                    setGraphic(anchorPane);


                                    //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DesignEvenementAdmin.fxml"));

                                    // try {

                                    //   Parent root = null;
                                    //   root = fxmlLoader.load();
                                    //  //Parent rootNode = fxmlLoader.load();
                                    //Scene scene = new Scene(rootNode);

                                    //   Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    //   Stage newStage = new Stage();
                                    //   newStage.setScene(new Scene(root, 1280, 700));
                                    //   newStage.setTitle("event");
                                    //   newStage.show();

                                    //    // Fermer la fenêtre actuelle
                                    //    currentStage.close();
                                    //  } catch (IOException e) {
                                    //     e.printStackTrace(); // Affiche l'erreur dans la console (vous pourriez le remplacer par une boîte de dialogue)
                                    //    System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
                                    // }


                                });


                        // Ajoutez d'autres composants selon vos besoins

                        // Positionnez les composants dans l'AnchorPane
                        AnchorPane.setTopAnchor(imageView, 10.0);
                        AnchorPane.setLeftAnchor(imageView, 30.0);
                        AnchorPane.setTopAnchor(nameLabel, 10.0);
                        AnchorPane.setLeftAnchor(nameLabel, 220.0);
                        AnchorPane.setTopAnchor(ddLabel, 40.0);
                        AnchorPane.setLeftAnchor(ddLabel, 220.0);
                        AnchorPane.setTopAnchor(dfLabel, 70.0);
                        AnchorPane.setLeftAnchor(dfLabel, 220.0);
                        AnchorPane.setTopAnchor(lieuLabel, 100.0);
                        AnchorPane.setLeftAnchor(lieuLabel, 220.0);
                        AnchorPane.setTopAnchor(categorieLabel, 130.0);
                        AnchorPane.setLeftAnchor(categorieLabel, 220.0);
                        AnchorPane.setTopAnchor(etatLabel, 160.0);
                        AnchorPane.setLeftAnchor(etatLabel, 220.0);
                        AnchorPane.setTopAnchor(descriptionLabel, 190.0);
                        AnchorPane.setLeftAnchor(descriptionLabel, 220.0);
                        AnchorPane.setTopAnchor(joinButton, 10.0);
                        AnchorPane.setLeftAnchor(joinButton, 800.0);

                        // Positionnez d'autres composants selon vos besoins

                        // Ajoutez les composants à l'AnchorPane
                        anchorPane.getChildren().addAll(imageView, nameLabel, ddLabel, dfLabel, lieuLabel, categorieLabel, etatLabel, descriptionLabel, joinButton);
                        // Ajoutez d'autres composants selon vos besoins


                        // Définissez l'AnchorPane en tant que graphique pour la cellule
                        setGraphic(anchorPane);


                    }
                }
            };
        });
        masterData2.addAll(es.read());
        listeEvents.getItems().addAll(masterData2);
    }







  /*  private void afficher_evenements() {
        // Initialiser les cellules de la TableView
        tcNomE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nom"));
        //tcCategorieE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("nomCategorie"));

        tcCategorieE.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Evenement, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Evenement, String> evenementStringCellDataFeatures) {
                return new SimpleStringProperty(evenementStringCellDataFeatures.getValue().getNom_categorieEvenement());
            }
        });

        tcDDE.setCellValueFactory(new PropertyValueFactory<Evenement, Date>("dateDebut"));
        tcDFE.setCellValueFactory(new PropertyValueFactory<Evenement, Date>("dateFin"));
        tcLieuE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("lieu"));
        tcEtatE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("etat"));
        tcDescriptionE.setCellValueFactory(new PropertyValueFactory<Evenement, String>("description"));


        // Utiliser une ObservableList pour stocker les éléments
        EvenementService es = new EvenementService();
        masterData.addAll(es.read());
        tvEvenement.setItems(masterData);


    }*/

    private void setupSearchFilter() {
        EvenementService es = new EvenementService();
        masterData.addAll(es.read());
        FilteredList<Evenement> filteredData = new FilteredList<>(masterData, p -> true);

        tfRechercheEc.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(evenement -> {
                // If filter text is empty, display all events.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare event name, category, and description of every event with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (evenement.getNom().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches event name.
                } else if (evenement.getNom_categorieEvenement().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches category.
                } else if (evenement.getLieu().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches description.
                } else // Filter matches description.
                    if (evenement.getEtat().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches description.
                    } else return evenement.getDescription().toLowerCase().contains(lowerCaseFilter);// Does not match.
            });
        });


        // Wrap the FilteredList in a SortedList.
        SortedList<Evenement> sortedData = new SortedList<>(filteredData);

        // Add sorted (and filtered) data to the table.
        listeEvents.setItems(sortedData);


    }

    @FXML
    void ajouterFeedback(ActionEvent event) {

        // Récupérer les valeurs des champs de saisie
        String nomEvenement = cbeventname.getValue();
        String comment = taComment.getText().trim();

        // Vérifier si les champs sont vides
        if (nomEvenement.isEmpty() || comment.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Typing Error !");
            alert.setContentText("Please fill out the form");
            alert.show();
        }


        // Créer l'objet Feedback
        FeedbackEvenementService fs = new FeedbackEvenementService();
        EvenementService es = new EvenementService();
        UserService us = new UserService();
        Stage stage = (Stage) taComment.getScene().getWindow();
        Feedback nouveauFeedback = new Feedback(es.getEvenementByNom(nomEvenement), (Client) stage.getUserData(), comment);
        fs.create(nouveauFeedback);
        SmsService.sendSms("+21622757828", "Thank You ! We appreciate your feedback, may this be the starting point for a better RAKCHA :) ");


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback Sent");
        alert.setContentText("Feedback Sent !");
        alert.show();

        taComment.clear();

    }

   /* public void showMonthlyCalendar(User user) {
        // Hide other elements and show the monthly calendar pane
        listeEvents.setVisible(false);
        anchplanning.setVisible(true);

        // Create a VBox to contain the monthly calendar
        VBox monthlyCalendarContent = new VBox();
        monthlyCalendarContent.setSpacing(10);

        // Create a GridPane to display the days of the month
        GridPane monthGrid = new GridPane();
        monthGrid.setHgap(10);
        monthGrid.setVgap(10);

        // Get the current date and first day of the month
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

        // Add each day of the month to the GridPane
        int row = 0;
        int col = 0;
        while (startDateOfMonth.getMonth().equals(currentDate.getMonth())) {
            Label dayLabel = new Label(String.valueOf(startDateOfMonth.getDayOfMonth()));
            dayLabel.setStyle("-fx-background-color: #ae2d3c; -fx-padding: 10px; -fx-text-fill: white;"); // Red background with white text
            dayLabel.setOnMouseClicked(event -> displayEventsForDate(startDateOfMonth, user));
            dayLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14)); // Arial, bold, size 14
            monthGrid.add(dayLabel, col, row);
            startDateOfMonth = startDateOfMonth.plusDays(1);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        VBox.setMargin(monthGrid, new Insets(0, 0, 0, 50));
        monthlyCalendarContent.getChildren().add(monthGrid);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(200);
        monthlyCalendarContent.getChildren().add(separator);

        // Clear any existing content in the monthly calendar pane
        anchplanning.getChildren().clear();

        // Add the monthly calendar content to the pane
        anchplanning.getChildren().add(monthlyCalendarContent);
        AnchorPane.setTopAnchor(monthlyCalendarContent, 50.0);
    }

    private void displayEventsForDate(LocalDate date, User user) {
        // Load events for the specified date and client
        List<Evenement> eventsForDate = loadEventsForDate(date, user+);

        // Check if there are any events for the date
        if (!eventsForDate.isEmpty()) {
            // Create a VBox to contain the event cards for the date
            VBox eventsForDateVBox = new VBox();
            eventsForDateVBox.setSpacing(10);

            // Add event cards to the VBox
            for (Evenement event : eventsForDate) {
                StackPane eventCard = createEventCard(event);
                eventsForDateVBox.getChildren().add(eventCard);
            }

            // Clear existing content in the monthly calendar pane if it's a VBox
            if (monthlyCalendarPane.getChildren().size() > 1 && monthlyCalendarPane.getChildren().get(1) instanceof VBox) {
                VBox existingEventsVBox = (VBox) monthlyCalendarPane.getChildren().get(1);
                existingEventsVBox.getChildren().clear();
            }

            // Add the event VBox to the monthly calendar pane
            monthlyCalendarPane.getChildren().add(eventsForDateVBox);
            AnchorPane.setTopAnchor(eventsForDateVBox, 30.0);
        } else {
            System.out.println("No events available for the specified date.");
        }
    }

    private List<Evenement> loadEventsForDate(LocalDate date, User user) {
        // Implement this method to load events for the specified date and client from the database or any other data source
        // Return a list of events for the date
        // Example:
        // EventService eventService = new EventService();
        // return eventService.getEventsForDateAndClient(date, client);
        return Collections.emptyList(); // Placeholder implementation
    }

    private StackPane createEventCard(Evenement event) {
        // Implement this method to create a UI card for displaying an event
        // Example:
        // StackPane eventCard = new StackPane();
        // eventCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px;"); // White background with padding
        // Label eventNameLabel = new Label(event.getName());
        // eventCard.getChildren().add(eventNameLabel);
        // return eventCard;
        return null; // Placeholder implementation
    }
*/

    @FXML
    void showHistory(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventHistory.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("My Events");
        stage.show();

    }


    @FXML
    void showFilms(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Movies List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showProduits(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitClient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Products List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showSeries(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seriesclient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Series List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showCinemas(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Seriesclient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Series List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showEvents(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvenementClient.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Events List");
        stage.show();

        // Fermer la fenêtre actuelle
        currentStage.close();
    }

    @FXML
    void showWeather(ActionEvent event) throws IOException {


        // Charger la nouvelle interface ListevenementAdmin.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MeteoEvent.fxml"));
        Parent root = loader.load();

        // Créer une nouvelle scène avec la nouvelle interface
        Scene scene = new Scene(root);

        // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Today's Weather");
        stage.show();

    }

    private void filterCategorieEvenements(String searchText) {
        // Vérifier si le champ de recherche n'est pas vide
        if (!searchText.isEmpty()) {
            // Filtrer la liste des cinémas pour ne garder que ceux dont le nom contient le texte saisi
            ObservableList<Evenement> filteredList = FXCollections.observableArrayList();
            for (Evenement categorie : listeEvents.getItems()) {
                if (categorie.getCategorie().getNom_categorie().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(categorie);
                }
            }
            // Mettre à jour la TableView avec la liste filtrée
            listeEvents.setItems(filteredList);
        } else {
            // Si le champ de recherche est vide, afficher tous les cinémas
            afficherliste();
        }
    }


    private List<Evenement> getAllCategories() {
        EvenementService evenementService = new EvenementService();
        List<Evenement> categorie = evenementService.read();
        return categorie;
    }

    @FXML
    void filtrer(MouseEvent event) {

        listeEvents.setOpacity(0.5);
        FilterAnchor.setVisible(true);

        // Nettoyer les listes des cases à cocher
        addressCheckBoxes.clear();
        statusCheckBoxes.clear();
        // Récupérer les adresses uniques depuis la base de données
        List<String> categorie = getCategorie_Evenement();


        // Créer des VBox pour les adresses
        VBox addressCheckBoxesVBox = new VBox();
        Label addressLabel = new Label("Category");
        addressLabel.setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 14px;");
        addressCheckBoxesVBox.getChildren().add(addressLabel);
        for (String address : categorie) {
            CheckBox checkBox = new CheckBox(address);
            addressCheckBoxesVBox.getChildren().add(checkBox);
            addressCheckBoxes.add(checkBox);
        }
        addressCheckBoxesVBox.setLayoutX(25);
        addressCheckBoxesVBox.setLayoutY(70);


        // Ajouter les VBox dans le FilterAnchor
        FilterAnchor.getChildren().addAll(addressCheckBoxesVBox);
        FilterAnchor.setVisible(true);
    }


    public List<String> getCategorie_Evenement() {
        // Récupérer tous les cinémas depuis la base de données
        List<Evenement> categories = getAllCategories();

        // Extraire les adresses uniques des cinémas
        List<String> categorie = categories.stream()
                .map(c -> c.getCategorie().getNom_categorie())
                .distinct()
                .collect(Collectors.toList());

        return categorie;
    }

    @FXML
    public void filtercinema(ActionEvent event) {

        listeEvents.setOpacity(1);


        FilterAnchor.setVisible(false);

        listeEvents.setVisible(true);

        // Récupérer les adresses sélectionnées
        List<String> selectedCategories = getSelectedCategories();
        // Récupérer les statuts sélectionnés

        Evenement evenement = new Evenement();
        // Filtrer les cinémas en fonction des adresses et/ou des statuts sélectionnés
        List<Evenement> filteredCategories = getAllCategories().stream()
                .filter(c -> selectedCategories.contains(c.getCategorie().getNom_categorie()))
                .collect(Collectors.toList());

        // Mettre à jour le TableView avec les cinémas filtrés
        ObservableList<Evenement> filteredList = FXCollections.observableArrayList(filteredCategories);
        listeEvents.setItems(filteredList);


    }

    private List<String> getSelectedCategories() {
        // Récupérer les adresses sélectionnées dans l'AnchorPane de filtrage
        return addressCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }


}






