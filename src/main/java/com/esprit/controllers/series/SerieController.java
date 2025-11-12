package com.esprit.controllers.series;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.esprit.models.series.Category;
import com.esprit.models.series.Feedback;
import com.esprit.models.series.Series;
import com.esprit.services.series.IServiceCategorieImpl;
import com.esprit.services.series.IServiceFeedbackImpl;
import com.esprit.services.series.IServiceSeriesImpl;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * JavaFX controller class for the RAKCHA application. Handles UI interactions
 * and manages view logic using FXML.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SerieController {
    private static final Logger LOGGER = Logger.getLogger(SerieController.class.getName());

    @FXML
    public ImageView serieImageView;
    String imgpath;
    private String cloudinaryImageUrl;
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
    private List<Category> categorieList;
    @FXML
    private TableView<Series> tableView;

    /**
     * Refreshes the controller's UI state by clearing form inputs and the table, reloading categories into the category selector,
     * configuring the series table columns (including Edit and Delete action columns), and repopulating the table with current series.
     *
     * <p>This method updates the visual state of the view and table contents; it does not return a value.</p>
     */
    private void ref() {
        this.tableView.getItems().clear();
        this.tableView.getColumns().clear();
        this.categorieF.getItems().clear();
        this.nomF.setText("");
        this.resumeF.setText("");
        this.directeurF.setText("");
        this.paysF.setText("");
        this.imgpath = "";
        final IServiceCategorieImpl categorieserv = new IServiceCategorieImpl();
        final IServiceSeriesImpl iServiceSerie = new IServiceSeriesImpl();
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            this.categorieList = categorieserv.read(pageRequest).getContent();
            for (final Category c : this.categorieList) {
                this.categorieF.getItems().add(c.getName());
            }

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        ///// affichage du tableau
        final IServiceSeriesImpl serviceSerie = new IServiceSeriesImpl();
        // TableColumn<Series, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idserie"));
        final TableColumn<Series, String> nomCol = new TableColumn<>("Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        final TableColumn<Series, String> resumeCol = new TableColumn<>("Summary");
        resumeCol.setCellValueFactory(new PropertyValueFactory<>("resume"));
        final TableColumn<Series, String> directeurCol = new TableColumn<>("Director");
        directeurCol.setCellValueFactory(new PropertyValueFactory<>("directeur"));
        final TableColumn<Series, String> paysCol = new TableColumn<>("Country");
        paysCol.setCellValueFactory(new PropertyValueFactory<>("pays"));
        final TableColumn<Series, String> categorieCol = new TableColumn<>("Category");
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("nomCategories"));
        final TableColumn<Series, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("delete");

            {
                this.button.setOnAction(event -> {
                    final Series serieDto = this.getTableView().getItems().get(this.getIndex());
                    try {
                        iServiceSerie.delete(serieDto);
                        tableView.getItems().remove(serieDto);
                        tableView.refresh();
                        showAlert("Succes", "Deleted Successfully!");
                    } catch (final Exception e) {
                        SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        showAlert("Succes", "Deleted Successfully!");
                    }

                }
);
            }


            /**
             * Sets the cell's graphic to the configured button when the cell is not empty; clears it when the cell is empty.
             *
             * @param item  the cell's item (unused; declared as `Void`)
             * @param empty `true` if the cell is empty and the graphic should be cleared, `false` if the button graphic should be displayed
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        final TableColumn<Series, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount;

            {
                this.button.setOnAction(event -> {
                    this.clickCount++;
                    if (2 == clickCount) {
                        final Series serie = this.getTableView().getItems().get(this.getIndex());
                        modifierSerie(serie);
                        this.clickCount = 0;
                    }

                }
);
            }


            /**
             * Sets the cell's graphic to the configured button when the cell is not empty; clears it when the cell is empty.
             *
             * @param item  the cell's item (unused; declared as `Void`)
             * @param empty `true` if the cell is empty and the graphic should be cleared, `false` if the button graphic should be displayed
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                }
 else {
                    this.setGraphic(this.button);
                }

            }

        }
);
        // tableView.getColumns().addAll(idCol,nomCol,
        // resumeCol,directeurCol,paysCol,categorieCol,supprimerCol,modifierCol);
        this.tableView.getColumns().addAll(nomCol, resumeCol, directeurCol, paysCol, categorieCol, supprimerCol,
                modifierCol);
        // Récupérer les catégories et les ajouter à la TableView
        try {
            PageRequest pageRequest = new PageRequest(0, 10);
            this.tableView.getItems().addAll(serviceSerie.read(pageRequest).getContent());
        } catch (final Exception e) {
            SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Export feedback entries to a PDF file selected by the user.
     *
     * The generated PDF contains a header with location and date, a centered title,
     * and a three-column table with columns "Description", "Date", and "Episode".
     *
     * @param event the ActionEvent that triggered the export; its window is used as the owner for the save dialog
     */
    @FXML
    private void exportPdf(final ActionEvent event) {
        // Afficher la boîte de dialogue de sélection de fichier
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        final File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (null != selectedFile) {
            final IServiceFeedbackImpl sf = new IServiceFeedbackImpl();
            PageRequest pageRequest = new PageRequest(0, 10);
            final List<Feedback> feedbackList = sf.read(pageRequest).getContent();
            try {
                // Créer le document PDF
                final Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
                document.open();
                // Créer une police personnalisée pour la date
                final Font fontDate = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                final BaseColor color = new BaseColor(114, 0, 0); // Rouge: 114, Vert: 0, Bleu: 0
                fontDate.setColor(color); // Définir la couleur de la police
                // Créer un paragraphe avec le lieu
                final Paragraph tunis = new Paragraph("Ariana", fontDate);
                tunis.setIndentationLeft(455); // Définir la position horizontale
                tunis.setSpacingBefore(-30); // Définir la position verticale
                // Ajouter le paragraphe au document
                document.add(tunis);
                // Obtenir la date d'aujourd'hui
                final LocalDate today = LocalDate.now();
                // Créer un paragraphe avec la date
                final Paragraph date = new Paragraph(today.toString(), fontDate);
                date.setIndentationLeft(437); // Définir la position horizontale
                date.setSpacingBefore(1); // Définir la position verticale
                // Ajouter le paragraphe au document
                document.add(date);
                // Créer une police personnalisée
                final Font font = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLD);
                final BaseColor titleColor = new BaseColor(114, 0, 0); //
                font.setColor(titleColor);
                // Ajouter le contenu au document
                final Paragraph title = new Paragraph("FeedBack List", font);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingBefore(50); // Ajouter une marge avant le titre pour l'éloigner de l'image
                title.setSpacingAfter(20);
                document.add(title);
                final PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setSpacingBefore(30.0f);
                table.setSpacingAfter(30.0f);
                // Ajouter les en-têtes de colonnes
                final Font hrFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                final BaseColor hrColor = new BaseColor(255, 255, 255); //
                hrFont.setColor(hrColor);
                final PdfPCell cell1 = new PdfPCell(new Paragraph("Description", hrFont));
                final BaseColor bgColor = new BaseColor(114, 0, 0);
                cell1.setBackgroundColor(bgColor);
                cell1.setBorderColor(titleColor);
                cell1.setPaddingTop(20);
                cell1.setPaddingBottom(20);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                final PdfPCell cell2 = new PdfPCell(new Paragraph("Date", hrFont));
                cell2.setBackgroundColor(bgColor);
                cell2.setBorderColor(titleColor);
                cell2.setPaddingTop(20);
                cell2.setPaddingBottom(20);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                final PdfPCell cell3 = new PdfPCell(new Paragraph("Episode", hrFont));
                cell3.setBackgroundColor(bgColor);
                cell3.setBorderColor(titleColor);
                cell3.setPaddingTop(20);
                cell3.setPaddingBottom(20);
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                final Font hdFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
                final BaseColor hdColor = new BaseColor(255, 255, 255); //
                hrFont.setColor(hdColor);
                for (final Feedback fee : feedbackList) {
                    final PdfPCell cellR1 = new PdfPCell(new Paragraph(String.valueOf(fee.getDescription()), hdFont));
                    cellR1.setBorderColor(titleColor);
                    cellR1.setPaddingTop(10);
                    cellR1.setPaddingBottom(10);
                    cellR1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR1);
                    final PdfPCell cellR2 = new PdfPCell(new Paragraph(String.valueOf(fee.getDate()), hdFont));
                    cellR2.setBorderColor(titleColor);
                    cellR2.setPaddingTop(10);
                    cellR2.setPaddingBottom(10);
                    cellR2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR2);
                    final PdfPCell cellR3 = new PdfPCell(new Paragraph(String.valueOf(fee.getEpisodeId()), hdFont));
                    cellR3.setBorderColor(titleColor);
                    cellR3.setPaddingTop(10);
                    cellR3.setPaddingBottom(10);
                    cellR3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR3);
                }

                table.setSpacingBefore(20);
                document.add(table);
                document.close();
                SerieController.LOGGER.info("Le Poster a été généré avec succès.");
            } catch (final Exception e) {
                SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

    }


    /**
     * Opens a dialog to edit the provided Series, applies the user's changes,
     * persists the updated Series while preserving its original id, refreshes the view,
     * and shows a confirmation alert.
     *
     * @param serieDto the Series to edit; its id is preserved and used to identify which Series to update
     * @throws RuntimeException if persisting the updated Series fails
     */
    private void modifierSerie(final Series serieDto) {
        final IServiceSeriesImpl iServiceSerie = new IServiceSeriesImpl();
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Serie ");
        final TextField nomFild = new TextField(serieDto.getName());
        final TextField resumeFild = new TextField(serieDto.getSummary());
        final TextField directeurFild = new TextField(serieDto.getDirector());
        final TextField paysFild = new TextField(serieDto.getCountry());
        final ComboBox<String> categorieComboBox = new ComboBox<>();
        for (final Category c : this.categorieList) {
            categorieComboBox.getItems().add(c.getName());
        }

        categorieComboBox.setValue(serieDto.getCategories().stream().findFirst().map(Category::getName).orElse(""));
        final Button Ajouterimage = new Button("ADD");
        {
            Ajouterimage.setOnAction(event -> {
                this.addimg(event);
            }
);
        }

        dialog.getDialogPane()
                .setContent(new VBox(8, new Label("Name:"), nomFild, new Label("Summary:"), resumeFild,
                        new Label("Director :"), directeurFild, new Label("Country :"), paysFild,
                        new Label("Add picture :"), Ajouterimage, categorieComboBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nomFild.getText(), resumeFild.getText());
            }

            return null;
        }
);
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        final Series serie = new Series();
        result.ifPresent(pair -> {
            serie.setId(serieDto.getId());
            serie.setName(nomFild.getText());
            serie.setSummary(resumeFild.getText());
            serie.setDirector(directeurFild.getText());
            serie.setCountry(paysFild.getText());
            serie.setImage(this.imgpath);
            for (final Category c : this.categorieList) {
                if (Objects.equals(c.getName(), categorieComboBox.getValue())) {
                    serie.setId(c.getId());
                }

            }

            try {
                iServiceSerie.update(serie);
                this.showAlert("Succes", "Successfully modified!");
                this.ref();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
);
    }


    /**
     * Configure the controller's UI and load initial data after FXML injection.
     *
     * Invoked by the FXMLLoader once fields are injected; resets form state and populates category and series data in the view.
     */
    @FXML
    private void initialize() {
        this.ref();
    }


    /**
     * Show an informational alert dialog with the given title and message.
     *
     * @param title   dialog title shown in the window's title bar
     * @param message dialog content text displayed to the user
     */
    @FXML
    private void showAlert(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Opens a file chooser for selecting a local image, validates the selection, and stores the normalized file path in {@code imgpath}.
     *
     * If the selected file is not a valid image or no file is chosen, an informational message is logged and {@code imgpath} is not modified.
     */
    @FXML
    void addimg(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        // Set file extension filter to only allow image files
        final FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",
                "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        final File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (null != selectedFile && this.isImageFile(selectedFile)) {
            this.imgpath = selectedFile.getAbsolutePath().replace("\\", "/");
            SerieController.LOGGER.info("File path stored: " + this.imgpath);
            final Image image = new Image(selectedFile.toURI().toString());
        }
 else {
            SerieController.LOGGER.info("Please select a valid image file.");
        }

    }


    /**
     * Opens a file chooser for PNG/JPG images, uploads the selected file to Cloudinary, and displays the uploaded image in the `serieImageView`.
     *
     * @param event the action event that triggered the image import
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(cloudinaryImageUrl);
                this.serieImageView.setImage(selectedImage);
                this.imgpath = cloudinaryImageUrl;

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
            }

        }

    }


    // Method to retrieve the stored file path

    /**
     * Gets the stored image file path.
     *
     * @return the stored image path, or null if no image has been selected.
     */
    public String getFilePath() {
        return this.imgpath;
    }


    // Method to check if the selected file is an image file

    /**
     * Determines whether a file can be loaded as an image.
     *
     * <p>Null, unreadable, or unsupported files are treated as not images.</p>
     *
     * @param file the file to test
     * @return `true` if the file can be loaded as a JavaFX Image, `false` otherwise
     */
    private boolean isImageFile(final File file) {
        try {
            final Image image = new Image(file.toURI().toString());
            return !image.isError();
        } catch (final Exception e) {
            return false;
        }

    }


    ////////////

    /**
     * Checks whether the name text field contains at least one character.
     *
     * @return true if the name field contains at least one character, false otherwise.
     */
    boolean nomcheck() {
        if (!Objects.equals(this.nomF.getText(), "")) {
            return true;
        }
 else {
            this.nomcheck.setText("Please enter a valid Name");
            return false;
        }

    }


    /**
     * Validate that a category is selected; show an error message when none is chosen.
     *
     * If no category is selected, sets the `categoriecheck` label text to "Please select a Category".
     *
     * @return `true` if a category is selected, `false` otherwise
     */
    boolean categoriecheck() {
        if (null != categorieF.getValue()) {
            return true;
        }
 else {
            this.categoriecheck.setText("Please select a Category");
            return false;
        }

    }


    /**
     * Checks whether the director text field contains a non-empty value.
     *
     * @return `true` if the director field contains text, `false` otherwise.
     */
    boolean directeurcheck() {
        if ("" != directeurF.getText()) {
            return true;
        }
 else {
            this.directeurcheck.setText("Please enter a valid Director");
            return false;
        }

    }


    /**
     * Checks that the country input field contains text.
     *
     * If the field is empty, sets the `payscheck` label to "Please enter a valid Country".
     *
     * @return `true` if the country field contains text, `false` otherwise.
     */
    boolean payscheck() {
        if ("" != paysF.getText()) {
            return true;
        }
 else {
            this.payscheck.setText("Please enter a valid Country");
            return false;
        }

    }


    /**
     * Validate that the summary text field is not empty.
     *
     * If the field is empty, sets the controller's `resumecheck` label to "Please enter a valid Summary".
     *
     * @return true if the summary field contains text, false otherwise.
     */
    boolean resumecheck() {
        if ("" != resumeF.getText()) {
            return true;
        }
 else {
            this.resumecheck.setText("Please enter a valid Summary");
            return false;
        }

    }


    /**
     * Check whether an image path has been set for the current form.
     *
     * If no image path is set, sets the `imagechek` label text to "Please select a Picture".
     *
     * @return true if an image path has been set, false otherwise
     */
    boolean imagechek() {
        if (!Objects.equals(this.imgpath, "")) {
            return true;
        }
 else {
            this.imagechek.setText("Please select a Picture");
            return false;
        }

    }


    //////////////////////

    /**
     * Send an HTML email to the specified recipient.
     *
     * @param recipientEmail the recipient's email address
     * @param subject        the email subject line
     * @param message        the email body as an HTML-formatted string
     */
    public void sendEmail(final String recipientEmail, final String subject, final String message) {
        try {
            final Email email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587); // Port SMTP
            email.setStartTLSEnabled(true); // Utiliser STARTTLS
            email.setAuthenticator(new DefaultAuthenticator("nourhene.ftaymia@esprit.tn", "211JFT2451"));
            // email.setSSLOnConnect(true); // Utiliser SSL
            email.setFrom("nourhene.ftaymia@esprit.tn");
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(recipientEmail);
            email.send();
            SerieController.LOGGER.info("Email sent successfully.");
        } catch (final EmailException e) {
            SerieController.LOGGER.info("Error sending email: " + e.getMessage());
            SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    /**
     * Create a new Series from the current form inputs, persist it, send a notification email, and refresh the view.
     *
     * Validates required form fields, constructs a Series (including image path and selected category), saves it
     * via the series service, clears validation messages and shows a success alert on success or an error alert on failure.
     *
     * @param event the ActionEvent that triggered this handler
     */
    @FXML
    void ajouterSerie(final ActionEvent event) {
        final IServiceSeriesImpl serieserv = new IServiceSeriesImpl();
        final Series serie = new Series();
        this.nomcheck();
        this.categoriecheck();
        this.directeurcheck();
        this.payscheck();
        this.resumecheck();
        if (this.nomcheck() && this.categoriecheck() && this.directeurcheck() && this.payscheck()
                && this.resumecheck()) {
            try {
                final String fullPath = this.serieImageView.getImage().getUrl();
                final String requiredPath = fullPath.substring(fullPath.indexOf("/img/series/"));
                final URI uri = new URI(requiredPath);
                serie.setName(this.nomF.getText());
                serie.setSummary(this.resumeF.getText());
                serie.setDirector(this.directeurF.getText());
                serie.setCountry(this.paysF.getText());
                serie.setImage(uri.getPath());
                for (final Category c : this.categorieList) {
                    if (Objects.equals(c.getName(), this.categorieF.getValue())) {
                        serie.setId(c.getId());
                    }

                }

                serieserv.create(serie);
                this.showAlert("successfully", "The serie has been successfully saved");
                this.nomcheck.setText("");
                this.categoriecheck.setText("");
                this.directeurcheck.setText("");
                this.payscheck.setText("");
                this.resumecheck.setText("");
                this.imagechek.setText("");
                // Envoyer un e-mail de notification
                final String recipientEmail = "nourhene.ftaymia@esprit.tn"; // Remplacez par l'adresse e-mail réelle du
                // destinataire
                final String subject = "Exciting News! New Series Alert 🚀";
                final String message = "Dear Viewer,\n\nWe are thrilled to announce a new series on our platform!\n\n"
                        + "Title: " + serie.getName() + "\n" + "Description: " + serie.getSummary() + "\n\n"
                        + "Get ready for an incredible viewing experience. Enjoy the show!\n\n" + "Best regards.\n";
                final String newSerieTitle = serie.getName();
                final String newSerieDescription = serie.getSummary(); // Vous pouvez personnaliser cela
                this.sendEmail(recipientEmail, newSerieTitle, message);
                this.ref();
            } catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the serie: " + e.getMessage());
                SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

        }

        ///
        /*
         * private void showStatistics() { IServiceSerieImpl serviceSerie = new
         * IServiceSerieImpl();
         *
         * try { Map<String, Long> statistics =
         * serviceSerie.getSeriesStatisticsByCategory(); // Handle or display the
         * statistics as needed LOGGER.info(statistics); } catch (SQLException e) {
         * LOGGER.log(Level.SEVERE, e.getMessage(), e); // Handle the exception }
 }

         */
        ///
    }


    // Gestion du menu

    /**
     * Opens the Episode view by loading its FXML and replacing the current window's scene.
     *
     * @param event the action event whose source Node is used to obtain the current Stage
     * @throws IOException if the FXML resource cannot be loaded or the scene cannot be created
     */
    @FXML
    void Oepisodes(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Episode-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Opens the series view FXML ("/ui/series/Serie-view.fxml") and replaces the current stage's scene with it.
     *
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Serie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Switches the current window to the Episode view by loading its FXML and setting it on the stage.
     *
     * @param event the ActionEvent that triggered the navigation
     * @throws IOException if the Episode FXML resource cannot be loaded
     */
    @FXML
    void Oepisode(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader
                .load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Episode-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Shows the movies view.
     *
     * @param actionEvent the ActionEvent that triggered this handler
     */
    public void showmovies(final ActionEvent actionEvent) {
    }


    /**
     * Handles the user action that switches the UI to the products view.
     *
     * @param actionEvent the triggering ActionEvent
     */
    public void showproducts(final ActionEvent actionEvent) {
    }


    /**
     * Handles the user action to display the cinema view.
     *
     * @param actionEvent the event that triggered this action
     */
    public void showcinema(final ActionEvent actionEvent) {
    }


    /**
     * Placeholder handler invoked when the events view action is triggered.
     *
     * @param actionEvent the JavaFX ActionEvent that triggered this handler
     */
    public void showevent(final ActionEvent actionEvent) {
    }


    /**
     * Trigger display of the series view in the UI.
     *
     * @param actionEvent the event that initiated this action
     */
    public void showseries(final ActionEvent actionEvent) {
    }

}

/*
 * @FXML public void showStatistics(ActionEvent actionEvent) {
 * statstiqueController.handleShowPieChart();
 *
 * }

 */