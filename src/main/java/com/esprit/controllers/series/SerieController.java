package com.esprit.controllers.series;

import com.esprit.models.common.Category;
import com.esprit.models.common.Review;
import com.esprit.models.series.Series;
import com.esprit.services.common.CategoryService;
import com.esprit.services.common.ReviewService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.PageRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * 1/ Clears the content of `tableView`, `categorieF`, and other fields. 2/
     * Recovers categories and series from a database using `IServiceCategorieImpl`
     * and `IServiceSerieImpl`. 3/ Adds recovered categories to `tableView` and sets
     * their cells. 4/ Creates new columns for editing and deleting series. 5/
     * Initializes buttons for editing and deleting series. 6/ Updates the graphic
     * of each cell based on its state (empty or not).
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
        final CategoryService categorieserv = new CategoryService();
        final SeriesService iServiceSerie = new SeriesService();
        try {
            PageRequest pageRequest = PageRequest.defaultPage();
            this.categorieList = categorieserv.read(pageRequest).getContent();
            for (final Category c : this.categorieList) {
                this.categorieF.getItems().add(c.getName());
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        ///// affichage du tableau
        final SeriesService serviceSerie = new SeriesService();
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
                });
            }

            /**
             * Updates a graphical item's state based on a boolean input, setting its
             * graphics component to either null or an provided button component upon
             * empty/non-empty status.
             *
             * @param item
             *              widget being updated, and it is passed to the super method
             *              `updateItem()` along with the `empty` parameter for further
             *              processing.
             *
             * @param empty
             *              ether the item being updated is empty or not, and accordingly
             *              sets
             *              the graphic of the button to null or the button itself when it
             *              is
             *              not empty.
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
                });
            }

            /**
             * Updates a widget's graphics based on an item's `empty` status, setting the
             * graphic to `null` if the item is empty and `button` otherwise.
             *
             * @param item
             *              element being updated, which can be null or the `button` object
             *              depending on whether it is being updated or not.
             *
             * @param empty
             *              status of the item being updated, and its value determines
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
            PageRequest pageRequest = PageRequest.defaultPage();
            this.tableView.getItems().addAll(serviceSerie.read(pageRequest).getContent());
        } catch (final Exception e) {
            SerieController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * /** Generates a PDF document containing a table with columns for description,
     * date, and episode number, based on feedback data.
     *
     * @param event An action event that triggers the function execution, providing
     *              the user's feedback selection.
     */
    @FXML
    private void exportPdf(final ActionEvent event) {
        // Afficher la boîte de dialogue de sélection de fichier
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        final File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (null != selectedFile) {
            final ReviewService sf = new ReviewService();
            PageRequest pageRequest = PageRequest.defaultPage();
            final List<Review> feedbackList = sf.read(pageRequest).getContent();
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
                for (final Review fee : feedbackList) {
                    final PdfPCell cellR1 = new PdfPCell(new Paragraph(String.valueOf(fee.getComment()), hdFont));
                    cellR1.setBorderColor(titleColor);
                    cellR1.setPaddingTop(10);
                    cellR1.setPaddingBottom(10);
                    cellR1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR1);
                    final PdfPCell cellR2 = new PdfPCell(new Paragraph(String.valueOf(fee.getCreatedAt()), hdFont));
                    cellR2.setBorderColor(titleColor);
                    cellR2.setPaddingTop(10);
                    cellR2.setPaddingBottom(10);
                    cellR2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cellR2);
                    final PdfPCell cellR3 = new PdfPCell(new Paragraph(String.valueOf(fee.getSeries().getName()), hdFont));
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
     * /** Modifies a serie's information by displaying a dialog box to enter and
     * validate the values of name, summary, director, country, add image, and
     * categories, and then updating the serie with the new information.
     *
     * @param serieDto data for a serie that is being modified, which includes the
     *                 serie's ID, name, summary, director, country, and image, as
     *                 well
     *                 as its category(ies).
     */
    private void modifierSerie(final Series serieDto) {
        final SeriesService iServiceSerie = new SeriesService();
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
        final Series serie = new Series();
        result.ifPresent(pair -> {
            serie.setId(serieDto.getId());
            serie.setName(nomFild.getText());
            serie.setSummary(resumeFild.getText());
            serie.setDirector(directeurFild.getText());
            serie.setCountry(paysFild.getText());
            serie.setImageUrl(this.imgpath);
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
     * `showAndWait()` method.
     *
     * @param title   title of an alert message shown by the `showAlert` method,
     *                which
     *                is displayed in a title bar at the top of the window.
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
     * path in a variable called `imgpath`. If an invalid file is selected, an error
     * message is displayed.
     *
     * @param event selection event triggered by the user selecting an image file
     *              using the FileChooser, and it provides the path of the selected
     *              file to the `addimg` method for processing.
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
     * locations and sets the image as the `serieImageView` field.
     *
     * @param event open file dialog event that triggers the function to execute.
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
     * whether the file is an image file or not. It does so by attempting to create
     * an `Image` object from the file's URI string, and returning `true` if the
     * creation was successful and `false` otherwise.
     *
     * @param file File to be tested for being an image file.
     * @returns a boolean value indicating whether the provided file is an image
     * file or not.
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
     * sets the text to "Please enter a valid Name" and returns `false`.
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
     * otherwise it displays an error message and returns `false`.
     *
     * @returns `true` if a value is provided for `categorieF.getValue()`, otherwise
     * it returns `false` and sets the `categoriecheck` text to "Please
     * select a Category".
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
     * else it sets an error message and returns `false`.
     *
     * @returns a boolean value indicating whether a valid director has been
     * entered.
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
     * string to an empty string. If it is not empty, the function returns true,
     * otherwise it displays an error message and returns false.
     *
     * @returns a boolean value indicating whether a valid country was entered or
     * not.
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
     * If the field is not empty, it returns `true`. Otherwise, it sets the text of
     * the `resumecheck` label to "Please enter a valid Summary" and returns
     * `false`.
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
     * "Please select a Picture" otherwise.
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
     *                       message being
     *                       sent.
     * @param subject        subject of the email to be sent, which is used as the
     *                       email's
     *                       title in the recipient's inbox.
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
     * name, category, director, pay, and resume. The function then checks if all
     * fields are filled in correctly, and if so, adds the series to a list of saved
     * series and sends an email notification to a predefined recipient with details
     * about the newly added serie.
     *
     * @param event ClickEvent that triggers the execution of the `ajouterSerie()`
     *              method and provides information about the event, such as the
     *              button or component that was clicked.
     */
    @FXML
    void ajouterSerie(final ActionEvent event) {
        final SeriesService serieserv = new SeriesService();
        final Series serie = new Series();
        this.nomcheck();
        this.categoriecheck();
        this.directeurcheck();
        this.payscheck();
        this.resumecheck();
        if (this.nomcheck() && this.categoriecheck() && this.directeurcheck() && this.payscheck()
            && this.resumecheck()) {
            try {
                // Issue #3: Add null check before accessing image
                if (this.serieImageView.getImage() == null) {
                    this.showAlert("Error", "Please select an image for the series.");
                    return;
                }

                final String fullPath = this.serieImageView.getImage().getUrl();

                // Issue #11: Fix Windows path compatibility - use platform-independent path handling
                String requiredPath = fullPath;
                if (fullPath.contains("/img/series/")) {
                    requiredPath = fullPath.substring(fullPath.indexOf("/img/series/"));
                } else if (fullPath.contains("\\img\\series\\")) {
                    // Handle Windows-style paths
                    requiredPath = fullPath.substring(fullPath.indexOf("\\img\\series\\")).replace("\\", "/");
                } else {
                    SerieController.LOGGER.warning("Image path does not contain expected '/img/series/' directory");
                }

                final URI uri = new URI(requiredPath);
                serie.setName(this.nomF.getText());
                serie.setSummary(this.resumeF.getText());
                serie.setDirector(this.directeurF.getText());
                serie.setCountry(this.paysF.getText());
                serie.setImageUrl(uri.getPath());

                // Issue #6: Validate category exists in database before using it
                // Issue #8: CRITICAL FIX - Removed serie.setId(c.getId()) which was corrupting the database
                // The series ID should be auto-generated by the database, not set from category ID
                boolean categoryFound = false;
                for (final Category c : this.categorieList) {
                    if (Objects.equals(c.getName(), this.categorieF.getValue())) {
                        categoryFound = true;
                        // Note: Category association should be handled properly in the service layer
                        // Do NOT set serie.setId(c.getId()) as it corrupts the series record
                        break;
                    }
                }

                if (!categoryFound) {
                    this.showAlert("Error", "Selected category does not exist. Please refresh and try again.");
                    return;
                }

                serieserv.create(serie);
                this.showAlert("successfully", "The serie has been successfully saved");
                this.nomcheck.setText("");
                this.categoriecheck.setText("");
                this.directeurcheck.setText("");
                this.payscheck.setText("");
                this.resumecheck.setText("");
                this.imagechek.setText("");

                // Issue #15: TODO - Replace hardcoded email with series owner or dynamic recipient
                // Get email from user config or series subscribers instead of hardcoding
                final String recipientEmail = "nourhene.ftaymia@esprit.tn"; // TODO: Replace with dynamic email lookup
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
         * LOGGER.log(Level.SEVERE, e.getMessage(), e); // Handle the exception } }
         */
        ///
    }

    // Gestion du menu

    /**
     * Loads an FXML file, creates a scene, and displays it on a Stage.
     *
     * @param event action event that triggered the execution of the `Oepisodes()`
     *              method, providing the source of the event as an object that can
     *              be
     *              referenced and used within the method.
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
     * Loads a FXML file named `"/ui/series/Serie-view.fxml"` and displays it on a
     * Stage, creating a new Scene and setting it as the scene of the Stage.
     *
     * @param event An action event object that triggers the `Oseries` method and
     *              provides information about the event, such as the source of the
     *              event and the state of the stage.
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
     * Loads an FXML file, creates a scene from it, and displays the scene on the
     * primary Stage.
     *
     * @param event ActionEvent object that triggers the function, providing
     *              information about the source of the event and any related data.
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
     * information about what it does beyond the fact that it exists.
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
 * @FXML public void showStatistics(ActionEvent actionEvent) {
 * statstiqueController.handleShowPieChart();
 *
 * }
 */
