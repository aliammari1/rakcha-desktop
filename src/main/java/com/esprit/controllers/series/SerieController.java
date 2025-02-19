package com.esprit.controllers.series;

import com.esprit.models.series.Categorie;
import com.esprit.models.series.Feedback;
import com.esprit.models.series.Serie;
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
import javafx.scene.image.ImageView;
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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerieController {
    private static final Logger LOGGER = Logger.getLogger(SerieController.class.getName());

    @FXML
    public ImageView serieImageView;
    String imgpath;
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
    private List<Categorie> categorieList;
    @FXML
    private TableView<SerieDto> tableView;

    /**
     * 1/ Clears the content of `tableView`, `categorieF`, and other fields.
     * 2/ Recovers categories and series from a database using
     * `IServiceCategorieImpl`
     * and `IServiceSerieImpl`.
     * 3/ Adds recovered categories to `tableView` and sets their cells.
     * 4/ Creates new columns for editing and deleting series.
     * 5/ Initializes buttons for editing and deleting series.
     * 6/ Updates the graphic of each cell based on its state (empty or not).
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
        final IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        try {
            this.categorieList = categorieserv.recuperer();
            for (final Categorie c : this.categorieList) {
                this.categorieF.getItems().add(c.getNom());
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        ///// affichage du tableau
        final IServiceSerieImpl serviceSerie = new IServiceSerieImpl();
        // TableColumn<SerieDto, Integer> idCol = new TableColumn<>("ID");
        // idCol.setCellValueFactory(new PropertyValueFactory<>("idserie"));
        final TableColumn<SerieDto, String> nomCol = new TableColumn<>("Name");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        final TableColumn<SerieDto, String> resumeCol = new TableColumn<>("Summary");
        resumeCol.setCellValueFactory(new PropertyValueFactory<>("resume"));
        final TableColumn<SerieDto, String> directeurCol = new TableColumn<>("Director");
        directeurCol.setCellValueFactory(new PropertyValueFactory<>("directeur"));
        final TableColumn<SerieDto, String> paysCol = new TableColumn<>("Country");
        paysCol.setCellValueFactory(new PropertyValueFactory<>("pays"));
        final TableColumn<SerieDto, String> categorieCol = new TableColumn<>("Category");
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("nomCategories"));
        final TableColumn<SerieDto, Void> supprimerCol = new TableColumn<>("Delete");
        supprimerCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("delete");

            {
                this.button.setOnAction(event -> {
                    final SerieDto serieDto = this.getTableView().getItems().get(this.getIndex());
                    try {
                        iServiceSerie.supprimer(serieDto.getIdserie());
                        SerieController.this.tableView.getItems().remove(serieDto);
                        SerieController.this.tableView.refresh();
                        SerieController.this.showAlert("Succes", "Deleted Successfully!");
                    } catch (final SQLException e) {
                        SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        SerieController.this.showAlert("Succes", "Deleted Successfully!");
                    }
                });
            }

            /**
             * Updates a graphical item's state based on a boolean input, setting its
             * graphics
             * component to either null or an provided button component upon empty/non-empty
             * status.
             *
             * @param item  widget being updated, and it is passed to the super method
             *              `updateItem()`
             *              along with the `empty` parameter for further processing.
             *
             * @param empty ether the item being updated is empty or not, and accordingly
             *              sets
             *              the graphic of the button to null or the button itself when it
             *              is not empty.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
        final TableColumn<SerieDto, Void> modifierCol = new TableColumn<>("Edit");
        modifierCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");
            private int clickCount;

            {
                this.button.setOnAction(event -> {
                    this.clickCount++;
                    if (2 == clickCount) {
                        final SerieDto serie = this.getTableView().getItems().get(this.getIndex());
                        SerieController.this.modifierSerie(serie);
                        this.clickCount = 0;
                    }
                });
            }

            /**
             * Updates a widget's graphics based on an item's `empty` status, setting the
             * graphic
             * to `null` if the item is empty and `button` otherwise.
             *
             * @param item  element being updated, which can be null or the `button` object
             *              depending
             *              on whether it is being updated or not.
             *
             * @param empty status of the item being updated, and its value determines
             *              whether
             *              or not to set the graphic of the button to null or the specified
             *              button graphics.
             */
            @Override
            protected void updateItem(final Void item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    this.setGraphic(null);
                } else {
                    this.setGraphic(this.button);
                }
            }
        });
        // tableView.getColumns().addAll(idCol,nomCol,
        // resumeCol,directeurCol,paysCol,categorieCol,supprimerCol,modifierCol);
        this.tableView.getColumns().addAll(nomCol, resumeCol, directeurCol, paysCol, categorieCol, supprimerCol,
                modifierCol);
        // Récupérer les catégories et les ajouter à la TableView
        try {
            this.tableView.getItems().addAll(serviceSerie.recuperer());
        } catch (final SQLException e) {
            SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * /**
     * Generates a PDF document containing a table with columns for description,
     * date,
     * and episode number, based on feedback data.
     *
     * @param event An action event that triggers the function execution, providing
     *              the
     *              user's feedback selection.
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
            final List<Feedback> feedbackList = sf.Show();
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
                    final PdfPCell cellR3 = new PdfPCell(new Paragraph(String.valueOf(fee.getId_episode()), hdFont));
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
     * /**
     * Modifies a serie's information by displaying a dialog box to enter and
     * validate
     * the values of name, summary, director, country, add image, and categories,
     * and
     * then updating the serie with the new information.
     *
     * @param serieDto data for a serie that is being modified, which includes the
     *                 serie's
     *                 ID, name, summary, director, country, and image, as well as
     *                 its category(ies).
     */
    private void modifierSerie(final SerieDto serieDto) {
        final IServiceSerieImpl iServiceSerie = new IServiceSerieImpl();
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Serie ");
        final TextField nomFild = new TextField(serieDto.getNom());
        final TextField resumeFild = new TextField(serieDto.getResume());
        final TextField directeurFild = new TextField(serieDto.getDirecteur());
        final TextField paysFild = new TextField(serieDto.getPays());
        final ComboBox<String> categorieComboBox = new ComboBox<>();
        for (final Categorie c : this.categorieList) {
            categorieComboBox.getItems().add(c.getNom());
        }
        categorieComboBox.setValue(serieDto.getNomCategories());
        final Button Ajouterimage = new Button("ADD");
        {
            Ajouterimage.setOnAction(event -> {
                this.addimg(event);
            });
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
        });
        final Optional<Pair<String, String>> result = dialog.showAndWait();
        final Serie serie = new Serie();
        result.ifPresent(pair -> {
            serie.setIdserie(serieDto.getIdserie());
            serie.setNom(nomFild.getText());
            serie.setResume(resumeFild.getText());
            serie.setDirecteur(directeurFild.getText());
            serie.setPays(paysFild.getText());
            serie.setImage(this.imgpath);
            for (final Categorie c : this.categorieList) {
                if (Objects.equals(c.getNom(), categorieComboBox.getValue())) {
                    serie.setIdcategorie(c.getIdcategorie());
                }
            }
            try {
                iServiceSerie.modifier(serie);
                this.showAlert("Succes", "Successfully modified!");
                this.ref();
            } catch (final SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * References a code resource denoted by `ref()`.
     */
    @FXML
    private void initialize() {
        this.ref();
    }

    /**
     * Creates an alert box with a title and message and displays it using the
     * `showAndWait()`
     * method.
     *
     * @param title   title of an alert message shown by the `showAlert` method,
     *                which is
     *                displayed in a title bar at the top of the window.
     * @param message message to be displayed in the Alert dialog box.
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
     * Enables users to choose an image from their local computer and stores its
     * path in
     * a variable called `imgpath`. If an invalid file is selected, an error message
     * is
     * displayed.
     *
     * @param event selection event triggered by the user selecting an image file
     *              using
     *              the FileChooser, and it provides the path of the selected file
     *              to the `addimg`
     *              method for processing.
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
        } else {
            SerieController.LOGGER.info("Please select a valid image file.");
        }
    }

    /**
     * Allows the user to select an image file, then saves it in two different
     * locations
     * and sets the image as the `serieImageView` field.
     *
     * @param event open file dialog event that triggers the function to execute.
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            try {
                final String destinationDirectory1 = "./src/main/resources/img/series/";
                final String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\series\\";
                final Path destinationPath1 = Paths.get(destinationDirectory1);
                final Path destinationPath2 = Paths.get(destinationDirectory2);
                final String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                final Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                final Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                final Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                this.serieImageView.setImage(selectedImage);
            } catch (final IOException e) {
                SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    // Method to retrieve the stored file path

    /**
     * Retrieves the image path.
     *
     * @returns a string representing the path to an image file.
     */
    public String getFilePath() {
        return this.imgpath;
    }

    // Method to check if the selected file is an image file

    /**
     * Takes a `File` object as input and returns a `Boolean` value indicating
     * whether
     * the file is an image file or not. It does so by attempting to create an
     * `Image`
     * object from the file's URI string, and returning `true` if the creation was
     * successful and `false` otherwise.
     *
     * @param file File to be tested for being an image file.
     * @returns a boolean value indicating whether the provided file is an image
     *          file or
     *          not.
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
     * Checks if the user's entered name is empty, and returns `true` otherwise it
     * sets
     * the text to "Please enter a valid Name" and returns `false`.
     *
     * @returns a boolean value indicating whether the input name is valid or not.
     */
    boolean nomcheck() {
        if (!Objects.equals(this.nomF.getText(), "")) {
            return true;
        } else {
            this.nomcheck.setText("Please enter a valid Name");
            return false;
        }
    }

    /**
     * Verifies if a category has been selected and returns `true` if it has,
     * otherwise
     * it displays an error message and returns `false`.
     *
     * @returns `true` if a value is provided for `categorieF.getValue()`, otherwise
     *          it
     *          returns `false` and sets the `categoriecheck` text to "Please select
     *          a Category".
     */
    boolean categoriecheck() {
        if (null != categorieF.getValue()) {
            return true;
        } else {
            this.categoriecheck.setText("Please select a Category");
            return false;
        }
    }

    /**
     * Verifies if a director's name is provided and returns `true` if it is valid,
     * else
     * it sets an error message and returns `false`.
     *
     * @returns a boolean value indicating whether a valid director has been
     *          entered.
     */
    boolean directeurcheck() {
        if ("" != directeurF.getText()) {
            return true;
        } else {
            this.directeurcheck.setText("Please enter a valid Director");
            return false;
        }
    }

    /**
     * Checks if the user has entered a valid country by comparing the inputted
     * string
     * to an empty string. If it is not empty, the function returns true, otherwise
     * it
     * displays an error message and returns false.
     *
     * @returns a boolean value indicating whether a valid country was entered or
     *          not.
     */
    boolean payscheck() {
        if ("" != paysF.getText()) {
            return true;
        } else {
            this.payscheck.setText("Please enter a valid Country");
            return false;
        }
    }

    /**
     * Verifies if the user has entered a non-empty string in the `resumeF` field.
     * If the
     * field is not empty, it returns `true`. Otherwise, it sets the text of the
     * `resumecheck`
     * label to "Please enter a valid Summary" and returns `false`.
     *
     * @returns a boolean value indicating whether a summary is provided.
     */
    boolean resumecheck() {
        if ("" != resumeF.getText()) {
            return true;
        } else {
            this.resumecheck.setText("Please enter a valid Summary");
            return false;
        }
    }

    /**
     * Checks if an input image path is provided, returning `true` if valid and
     * "Please
     * select a Picture" otherwise.
     *
     * @returns "Please select a Picture".
     */
    boolean imagechek() {
        if (!Objects.equals(this.imgpath, "")) {
            return true;
        } else {
            this.imagechek.setText("Please select a Picture");
            return false;
        }
    }

    //////////////////////

    /**
     * Sends an HTML-formatted email to a recipient via Gmail's SMTP service, using
     * authentication and STARTTLS protocol for encryption.
     *
     * @param recipientEmail email address of the intended recipient of the email
     *                       message
     *                       being sent.
     * @param subject        subject of the email to be sent, which is used as the
     *                       email's title
     *                       in the recipient's inbox.
     * @param message        message that will be sent through the email, and it is
     *                       passed as a
     *                       string to the `setMsg()` method of the `Email` class.
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
     * Allows users to create a new serie by inputting necessary information such as
     * name,
     * category, director, pay, and resume. The function then checks if all fields
     * are
     * filled in correctly, and if so, adds the series to a list of saved series and
     * sends
     * an email notification to a predefined recipient with details about the newly
     * added
     * serie.
     *
     * @param event ClickEvent that triggers the execution of the `ajouterSerie()`
     *              method
     *              and provides information about the event, such as the button or
     *              component that was
     *              clicked.
     */
    @FXML
    void ajouterSerie(final ActionEvent event) {
        final IServiceSerieImpl serieserv = new IServiceSerieImpl();
        final Serie serie = new Serie();
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
                serie.setNom(this.nomF.getText());
                serie.setResume(this.resumeF.getText());
                serie.setDirecteur(this.directeurF.getText());
                serie.setPays(this.paysF.getText());
                serie.setImage(uri.getPath());
                for (final Categorie c : this.categorieList) {
                    if (Objects.equals(c.getNom(), this.categorieF.getValue())) {
                        serie.setIdcategorie(c.getIdcategorie());
                    }
                }
                serieserv.ajouter(serie);
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
                        + "Title: " + serie.getNom() + "\n"
                        + "Description: " + serie.getResume() + "\n\n"
                        + "Get ready for an incredible viewing experience. Enjoy the show!\n\n"
                        + "Best regards.\n";
                final String newSerieTitle = serie.getNom();
                final String newSerieDescription = serie.getResume(); // Vous pouvez personnaliser cela
                this.sendEmail(recipientEmail, newSerieTitle, message);
                this.ref();
            } catch (final Exception e) {
                this.showAlert("Error", "An error occurred while saving the serie: " + e.getMessage());
                SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        ///
        /*
         * private void showStatistics() {
         * IServiceSerieImpl serviceSerie = new IServiceSerieImpl();
         *
         * try {
         * Map<String, Long> statistics = serviceSerie.getSeriesStatisticsByCategory();
         * // Handle or display the statistics as needed
         * LOGGER.info(statistics);
         * } catch (SQLException e) {
         * LOGGER.log(Level.SEVERE, e.getMessage(), e);
         * // Handle the exception
         * }
         * }
         */
        ///
    }

    // Gestion du menu

    /**
     * Loads an FXML file, creates a scene, and displays it on a Stage.
     *
     * @param event action event that triggered the execution of the `Oepisodes()`
     *              method,
     *              providing the source of the event as an object that can be
     *              referenced and used
     *              within the method.
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
     * Loads a FXML file named `"/ui/series/Serie-view.fxml"` and displays it on a Stage,
     * creating
     * a new Scene and setting it as the scene of the Stage.
     *
     * @param event An action event object that triggers the `Oseries` method and
     *              provides
     *              information about the event, such as the source of the event and
     *              the state of the
     *              stage.
     */
    @FXML
    void Oseries(final ActionEvent event) throws IOException {
        final Parent root = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/ui/series/Serie-view.fxml")));
        final Scene scene = new Scene(root);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads an FXML file, creates a scene from it, and displays the scene on the
     * primary
     * Stage.
     *
     * @param event ActionEvent object that triggers the function, providing
     *              information
     *              about the source of the event and any related data.
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
     * Displays a list of movies to the user.
     *
     * @param actionEvent occurrence of an event that triggered the function call.
     */
    public void showmovies(final ActionEvent actionEvent) {
    }

    /**
     * Likely displays a list or inventory of products.
     *
     * @param actionEvent occurrence of an event that triggers the execution of the
     *                    `showProducts` method.
     */
    public void showproducts(final ActionEvent actionEvent) {
    }

    /**
     * Is called when the `ActionEvent` occurs, and it does not provide any
     * information
     * about what it does beyond the fact that it exists.
     *
     * @param actionEvent event that triggered the execution of the `show cinema`
     *                    function.
     */
    public void showcinema(final ActionEvent actionEvent) {
    }

    /**
     * Handles an `ActionEvent`.
     *
     * @param actionEvent event that triggered the function call.
     */
    public void showevent(final ActionEvent actionEvent) {
    }

    /**
     * Likely displays a series of data or elements in a graphical interface.
     *
     * @param actionEvent event that triggered the call to the `showSeries`
     *                    function.
     */
    public void showseries(final ActionEvent actionEvent) {
    }
}
/*
 * @FXML
 * public void showStatistics(ActionEvent actionEvent) {
 * statstiqueController.handleShowPieChart();
 *
 * }
 */
