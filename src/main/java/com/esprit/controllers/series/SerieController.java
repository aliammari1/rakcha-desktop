package com.esprit.controllers.series;


import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.series.Categorie;
import com.esprit.models.series.Feedback;
import com.esprit.models.series.Serie;
import com.esprit.models.users.Client;
import com.esprit.services.series.DTO.SerieDto;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.services.series.IServiceFeedbackImpl;
import com.esprit.services.series.IServiceSerieImpl;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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


    private void ref() {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        categorieF.getItems().clear();
        nomF.setText("");
        resumeF.setText("");
        directeurF.setText("");
        paysF.setText("");
        imgpath = "";
        IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        try {
            categorieList = categorieserv.recuperer();
            for (Categorie c : categorieList
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
                        showAlert("Succes", "Deleted Successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Succes", "Deleted Successfully!");
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
            private int clickCount = 0;

            {

                button.setOnAction(event -> {
                    clickCount++;
                    if (clickCount == 2) {
                        SerieDto serie = getTableView().getItems().get(getIndex());
                        modifierSerie(serie);
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


        //tableView.getColumns().addAll(idCol,nomCol, resumeCol,directeurCol,paysCol,categorieCol,supprimerCol,modifierCol);
        tableView.getColumns().addAll(nomCol, resumeCol, directeurCol, paysCol, categorieCol, supprimerCol, modifierCol);

        // Récupérer les catégories et les ajouter à la TableView
        try {
            tableView.getItems().addAll(serviceSerie.recuperer());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void exportPdf(ActionEvent event) {
        // Afficher la boîte de dialogue de sélection de fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            IServiceFeedbackImpl sf = new IServiceFeedbackImpl();
            List<Feedback> feedbackList = sf.Show();
            try {
                // Créer le document PDF
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
                document.open();

                // Créer une police personnalisée pour la date
                Font fontDate = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                BaseColor color = new BaseColor(114, 0, 0); // Rouge: 114, Vert: 0, Bleu: 0
                fontDate.setColor(color); // Définir la couleur de la police

                // Créer un paragraphe avec le lieu
                Paragraph tunis = new Paragraph("Ariana", fontDate);
                tunis.setIndentationLeft(455); // Définir la position horizontale
                tunis.setSpacingBefore(-30); // Définir la position verticale
                // Ajouter le paragraphe au document
                document.add(tunis);

                // Obtenir la date d'aujourd'hui
                LocalDate today = LocalDate.now();

                // Créer un paragraphe avec la date
                Paragraph date = new Paragraph(today.toString(), fontDate);

                date.setIndentationLeft(437); // Définir la position horizontale
                date.setSpacingBefore(1); // Définir la position verticale
                // Ajouter le paragraphe au document
                document.add(date);

                // Créer une police personnalisée
                Font font = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLD);
                BaseColor titleColor = new BaseColor(114, 0, 0); //
                font.setColor(titleColor);

                // Ajouter le contenu au document
                Paragraph title = new Paragraph("FeedBack List", font);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingBefore(50); // Ajouter une marge avant le titre pour l'éloigner de l'image
                title.setSpacingAfter(20);
                document.add(title);


                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setSpacingBefore(30f);
                table.setSpacingAfter(30f);

                // Ajouter les en-têtes de colonnes
                Font hrFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                BaseColor hrColor = new BaseColor(255, 255, 255); //
                hrFont.setColor(hrColor);

                PdfPCell cell1 = new PdfPCell(new Paragraph("Description", hrFont));
                BaseColor bgColor = new BaseColor(114, 0, 0);
                cell1.setBackgroundColor(bgColor);
                cell1.setBorderColor(titleColor);
                cell1.setPaddingTop(20);
                cell1.setPaddingBottom(20);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell cell2 = new PdfPCell(new Paragraph("Date", hrFont));
                cell2.setBackgroundColor(bgColor);
                cell2.setBorderColor(titleColor);
                cell2.setPaddingTop(20);
                cell2.setPaddingBottom(20);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell cell3 = new PdfPCell(new Paragraph("Episode", hrFont));
                cell3.setBackgroundColor(bgColor);
                cell3.setBorderColor(titleColor);
                cell3.setPaddingTop(20);
                cell3.setPaddingBottom(20);
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);

                Font hdFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
                BaseColor hdColor = new BaseColor(255, 255, 255); //
                hrFont.setColor(hdColor);
                for (Feedback fee : feedbackList) {
                    PdfPCell cellR1 = new PdfPCell(new Paragraph(String.valueOf(fee.getDescription()), hdFont));
                    cellR1.setBorderColor(titleColor);
                    cellR1.setPaddingTop(10);
                    cellR1.setPaddingBottom(10);
                    cellR1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR1);

                    PdfPCell cellR2 = new PdfPCell(new Paragraph(String.valueOf(fee.getDate()), hdFont));
                    cellR2.setBorderColor(titleColor);
                    cellR2.setPaddingTop(10);
                    cellR2.setPaddingBottom(10);
                    cellR2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR2);

                    PdfPCell cellR3 = new PdfPCell(new Paragraph(String.valueOf(fee.getId_episode()), hdFont));
                    cellR3.setBorderColor(titleColor);
                    cellR3.setPaddingTop(10);
                    cellR3.setPaddingBottom(10);
                    cellR3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR3);
                }


                table.setSpacingBefore(20);
                document.add(table);
                document.close();

                System.out.println("Le Poster a été généré avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void modifierSerie(SerieDto serieDto) {
        IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Serie ");

        TextField nomFild = new TextField(serieDto.getNom());
        TextField resumeFild = new TextField(serieDto.getResume());
        TextField directeurFild = new TextField(serieDto.getDirecteur());
        TextField paysFild = new TextField(serieDto.getPays());
        ComboBox<String> categorieComboBox = new ComboBox<>();
        for (Categorie c : categorieList
        ) {
            categorieComboBox.getItems().add(c.getNom());
        }
        categorieComboBox.setValue(serieDto.getNomCategories());
        Button Ajouterimage = new Button("ADD");

        {
            Ajouterimage.setOnAction(event -> {
                addimg(event);
            });
        }


        dialog.getDialogPane().setContent(new VBox(8, new Label("Name:"), nomFild, new Label("Summary:"), resumeFild, new Label("Director :"), directeurFild, new Label("Country :"), paysFild, new Label("Add picture :"), Ajouterimage, categorieComboBox));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nomFild.getText(), resumeFild.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        Serie serie = new Serie();
        result.ifPresent(pair -> {
            serie.setIdserie(serieDto.getIdserie());
            serie.setNom(nomFild.getText());
            serie.setResume(resumeFild.getText());
            serie.setDirecteur(directeurFild.getText());
            serie.setPays(paysFild.getText());
            serie.setImage(imgpath);
            for (Categorie c : categorieList
            ) {
                if (c.getNom() == categorieComboBox.getValue()) {
                    serie.setIdcategorie(c.getIdcategorie());
                }
            }
            try {
                iServiceSerie.modifier(serie);
                showAlert("Succes", "Successfully modified!");
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
    void addimg(ActionEvent event) {
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
            return !image.isError();
        } catch (Exception e) {
            return false;
        }
    }

    ////////////
    boolean nomcheck() {
        if (nomF.getText() != "") {

            return true;
        } else {
            nomcheck.setText("Please enter a valid Name");
            return false;
        }
    }

    boolean categoriecheck() {
        if (categorieF.getValue() != null) {

            return true;
        } else {
            categoriecheck.setText("Please select a Category");
            return false;
        }
    }

    boolean directeurcheck() {
        if (directeurF.getText() != "") {

            return true;
        } else {
            directeurcheck.setText("Please enter a valid Director");
            return false;
        }
    }

    boolean payscheck() {
        if (paysF.getText() != "") {

            return true;
        } else {
            payscheck.setText("Please enter a valid Country");
            return false;
        }
    }

    boolean resumecheck() {
        if (resumeF.getText() != "") {

            return true;
        } else {
            resumecheck.setText("Please enter a valid Summary");
            return false;
        }
    }

    boolean imagechek() {
        if (imgpath != "") {

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
        Serie serie = new Serie();
        nomcheck();
        categoriecheck();
        directeurcheck();
        payscheck();
        resumecheck();
        imagechek();
        if (nomcheck() && categoriecheck() && directeurcheck() && payscheck() && resumecheck() && imagechek()) {
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
                nomcheck.setText("");
                categoriecheck.setText("");
                directeurcheck.setText("");
                payscheck.setText("");
                resumecheck.setText("");
                imagechek.setText("");
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
                e.printStackTrace();

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
    void Oepisodes(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Episode-view.fxml")));
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
   /*
    @FXML
    public void showStatistics(ActionEvent actionEvent) {
        statstiqueController.handleShowPieChart();

    }
    */






