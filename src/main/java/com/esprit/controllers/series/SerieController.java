package com.esprit.controllers.series;


import com.esprit.services.series.DTO.SerieDto;
import com.esprit.models.series.Categorie;
import com.esprit.models.series.Serie;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.services.series.IServiceSerieImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class SerieController {
    ///
    @FXML
    private Label categoriecheck;
    @FXML
    private Label directeurcheck;
    @FXML
    private Label imagechek;
    @FXML
    private Label nomcheck;
    @FXML
    private Label payscheck;
    @FXML
    private Label resumecheck;
    @FXML
    private TextField nomF;
    @FXML
    private TextField resumeF;
    @FXML
    private TextField directeurF;
    @FXML
    private TextField paysF;
    @FXML
    private ComboBox<String> categorieF;
    private String imgpath;
    private List<Categorie> categorieList;
    @FXML
    private TableView<SerieDto> tableView;
    

    private void ref(){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        categorieF.getItems().clear();
        nomF.setText("");
        resumeF.setText("");
        directeurF.setText("");
        paysF.setText("");
        imgpath="";
        IServiceCategorieImpl categorieserv=new  IServiceCategorieImpl();
        IServiceSerieImpl iServiceSerie=new IServiceSerieImpl();
        try {
            categorieList=categorieserv.recuperer();
            for (Categorie c  : categorieList
            ) {
                categorieF.getItems().add(c.getNom());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ///// affichage du tableau
        IServiceSerieImpl serviceSerie = new IServiceSerieImpl();
       // TableColumn<SerieDto, Integer> idCol = new TableColumn<>("ID");
        //idCol.setCellValueFactory(new PropertyValueFactory<>("idserie"));

        TableColumn<SerieDto, String> nomCol = new TableColumn<>("Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<SerieDto, String> resumeCol = new TableColumn<>("Summary");
        resumeCol.setCellValueFactory(new PropertyValueFactory<>("resume"));

        TableColumn<SerieDto, String> directeurCol = new TableColumn<>("Director");
        directeurCol.setCellValueFactory(new PropertyValueFactory<>("directeur"));

        TableColumn<SerieDto, String> paysCol = new TableColumn<>("Country");
        paysCol.setCellValueFactory(new PropertyValueFactory<>("pays"));

        TableColumn<SerieDto, String> categorieCol = new TableColumn<>("Category");
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("nomCategories"));
        TableColumn<SerieDto, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {

            private final Button button = new Button("delete");

            {
                button.setOnAction(event -> {
                    SerieDto serieDto = getTableView().getItems().get(getIndex());
                    try {
                        iServiceSerie.supprimer(serieDto.getIdserie());
                        tableView.getItems().remove(serieDto);
                        tableView.refresh();
                        showAlert("Succes","Deleted Successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Succes","Deleted Successfully!");
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
        TableColumn<SerieDto, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount =0;
            {

                button.setOnAction(event -> {
                    clickCount++;
                    if (clickCount==2){
                    SerieDto serie = getTableView().getItems().get(getIndex());
                    modifierSerie(serie);
                        clickCount =0;
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
        //tableView.getColumns().addAll(idCol,nomCol, resumeCol,directeurCol,paysCol,categorieCol,supprimerCol,modifierCol);
        tableView.getColumns().addAll(nomCol, resumeCol,directeurCol,paysCol,categorieCol,supprimerCol,modifierCol);

        // Récupérer les catégories et les ajouter à la TableView
        try {
            tableView.getItems().addAll(serviceSerie.recuperer());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void modifierSerie(SerieDto serieDto) {
        IServiceSerieImpl iServiceSerie=new IServiceSerieImpl();
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Serie ");

        TextField nomFild = new TextField(serieDto.getNom());
        TextField resumeFild = new TextField(serieDto.getResume());
        TextField directeurFild = new TextField(serieDto.getDirecteur());
        TextField paysFild = new TextField(serieDto.getPays());
        ComboBox<String> categorieComboBox = new ComboBox<>();
        for (Categorie c  : categorieList
        ) {
            categorieComboBox.getItems().add(c.getNom());
        }
        categorieComboBox.setValue(serieDto.getNomCategories());
        Button Ajouterimage= new Button("ADD");

        {
            Ajouterimage.setOnAction(event -> {
                addimg(event);
            });
        }



        dialog.getDialogPane().setContent(new VBox(8, new Label("Name:"), nomFild, new Label("Summary:"),resumeFild,new Label("Director :"),directeurFild,new Label("Country :"),paysFild,new Label("Add picture :") ,Ajouterimage,categorieComboBox));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nomFild.getText(), resumeFild.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
            Serie serie=new Serie();
        result.ifPresent(pair -> {
            serie.setIdserie(serieDto.getIdserie());
            serie.setNom(nomFild.getText());
            serie.setResume(resumeFild.getText());
            serie.setDirecteur(directeurFild.getText());
            serie.setPays(paysFild.getText());
            serie.setImage(imgpath);
            for (Categorie c: categorieList
            ) {
                if(c.getNom()==categorieComboBox.getValue()){
                    serie.setIdcategorie(c.getIdcategorie());
                }}
            try {
                iServiceSerie.modifier(serie);
                showAlert("Succes","Successfully modified!");
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

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void addimg(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        // Set file extension filter to only allow image files
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null && isImageFile(selectedFile)) {
            imgpath = selectedFile.getAbsolutePath().replace("\\", "/");
            System.out.println("File path stored: " + imgpath);

            Image image = new Image(selectedFile.toURI().toString());
        } else {
            System.out.println("Please select a valid image file.");
        }

    }

    // Method to retrieve the stored file path
    public String getFilePath() {
        return imgpath;
    }

    // Method to check if the selected file is an image file
    private boolean isImageFile(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            return image.isError() ? false : true;
        } catch (Exception e) {
            return false;
        }
    }
////////////
boolean nomcheck( ){
    if(nomF.getText()!=""){

        return true;
    }else{
        nomcheck.setText("Please enter a valid Name");
        return false;
    }}
    boolean categoriecheck( ) {
        if (categorieF.getValue()!=null) {

            return true;
        } else {
            categoriecheck.setText("Please select a Category");
            return false;
        }
    }
    boolean directeurcheck( ) {
        if (directeurF.getText()!="") {

            return true;
        } else {
            directeurcheck.setText("Please enter a valid Director");
            return false;
        }
    }
    boolean payscheck( ) {
        if (paysF.getText()!="") {

            return true;
        } else {
            payscheck.setText("Please enter a valid Country");
            return false;
        }
    }
    boolean resumecheck( ) {
        if (resumeF.getText()!="") {

            return true;
        } else {
            resumecheck.setText("Please enter a valid Summary");
            return false;
        }
    }
    boolean imagechek( ) {
        if (imgpath!="") {

            return true;
        } else {
            imagechek.setText("Please select a Picture");
            return false;
        }
    }

//////////////////////

    public void sendEmail(String recipientEmail, String subject, String message) {
        try {

            Email email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587); // Port SMTP
            email.setStartTLSEnabled(true); // Utiliser STARTTLS
            email.setAuthenticator(new DefaultAuthenticator("nourhene.ftaymia@esprit.tn", "211JFT2451"));
            //email.setSSLOnConnect(true); // Utiliser SSL
            email.setFrom("nourhene.ftaymia@esprit.tn");
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(recipientEmail);
            email.send();
            System.out.println("Email sent successfully.");
        } catch (EmailException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void ajouterSerie(ActionEvent event) {
        IServiceSerieImpl serieserv = new IServiceSerieImpl();
        Serie serie= new Serie();
        nomcheck();categoriecheck( );directeurcheck( );payscheck( );resumecheck( );imagechek( );
        if(nomcheck() && categoriecheck( ) && directeurcheck( ) && payscheck( ) && resumecheck( ) && imagechek( ) ) {
            try {
                serie.setNom(nomF.getText());
                serie.setResume(resumeF.getText());
                serie.setDirecteur(directeurF.getText());
                serie.setPays(paysF.getText());
                serie.setImage(imgpath);

                for (Categorie c : categorieList
                ) {
                    if (c.getNom() == categorieF.getValue()) {
                        serie.setIdcategorie(c.getIdcategorie());
                    }
                }
                serieserv.ajouter(serie);
                showAlert("successfully", "The serie has been successfully saved");
                nomcheck.setText("");categoriecheck.setText("");directeurcheck.setText("");payscheck.setText("");resumecheck.setText("");imagechek.setText("");
                // Envoyer un e-mail de notification
                String recipientEmail = "nourhene.ftaymia@esprit.tn"; // Remplacez par l'adresse e-mail réelle du destinataire
                String subject = "Exciting News! New Series Alert 🚀";
                String message = "Dear Viewer,\n\nWe are thrilled to announce a new series on our platform!\n\n" +
                        "Title: " + serie.getNom() + "\n" +
                        "Description: " + serie.getResume() + "\n\n" +
                        "Get ready for an incredible viewing experience. Enjoy the show!\n\n" +
                        "Best regards.\n";
                String newSerieTitle = serie.getNom();
                String newSerieDescription = serie.getResume(); // Vous pouvez personnaliser cela
                sendEmail(recipientEmail, newSerieTitle, message);
                ref();


            } catch (Exception e) {
                showAlert("Error", "An error occurred while saving the serie: " + e.getMessage());
                System.out.println(e.getMessage());

            }
        }
///
        /*
        private void showStatistics() {
            IServiceSerieImpl serviceSerie = new IServiceSerieImpl();

            try {
                Map<String, Long> statistics = serviceSerie.getSeriesStatisticsByCategory();
                // Handle or display the statistics as needed
                System.out.println(statistics);
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception
            }
        }
        */
       ///
        }

    //Gestion du menu
    @FXML
    void Ocategories(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Categorie-view.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    void Oseries(ActionEvent event) throws IOException {
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

    }
   /*
    @FXML
    public void showStatistics(ActionEvent actionEvent) {
        statstiqueController.handleShowPieChart();

    }
    */






