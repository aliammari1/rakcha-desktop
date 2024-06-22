package com.esprit.controllers.films;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.films.Ticket;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.TicketService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Paymentuser;
import com.stripe.exception.StripeException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.controlsfx.control.CheckComboBox;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Is responsible for handling payments for a Visa card. It has several methods for
 * validating user input, creating a receipt PDF, and opening the PDF file. The class
 * also initializes an Alert to display error messages if necessary.
 */
public class PaymentuserController implements Initializable {
    Client client;
    private Seance seance;
    @FXML
    private Label total;
    @FXML
    private Label filmLabel_Payment;
    @FXML
    private Button Pay;
    @FXML
    private AnchorPane anchorpane_payment;
    @FXML
    private TextField anneeExp;
    @FXML
    private AnchorPane bord;
    @FXML
    private TextField carte;
    @FXML
    private CheckComboBox<String> checkcomboboxseance_res;
    @FXML
    private ComboBox<String> cinemacombox_res;
    @FXML
    private TextField cvc;
    @FXML
    private Button filmm;
    @FXML
    private TextField moisExp;
    @FXML
    private Spinner<Integer> nbrplacepPayment_Spinner;
    @FXML
    private Label nomPrenom;
    @FXML
    private Button viewPDF;
    /**
     * Checks if a given string is a numerical value by matching it against a regular
     * expression pattern of one or more digits.
     * 
     * @param str String to be checked for matching the regular expression `\d+`.
     * 
     * @returns a `Boolean` value indicating whether the input string matches the regular
     * expression for a number.
     */
    public static boolean isNum(String str) {
        String expression = "\\d+";
        return str.matches(expression);
    }
    /**
     * Converts a `float` argument into an `int` value by calling the `int` casting
     * operator `(int)`.
     * 
     * @param value floating-point number to be converted to an integer.
     * 
     * @returns an integer value equivalent to the provided floating-point number.
     */
    public static int floatToInt(float value) {
        return (int) value;
    }
    /**
     * Sets the `client` field and displays the film name on a label. It also prints the
     * value of `client` to the console.
     * 
     * @param client Client object that provides the payment details for the film name
     * set by the `filmName` parameter.
     * 
     * @param filmName name of a film that is being associated with the `Client` object
     * passed as an argument to the `setData()` method.
     */
    public void setData(Client client, String filmName) {
        this.client = client;
        filmLabel_Payment.setText(filmName);
        System.out.println("payment: " + client);
    }
    /**
     * Reads the film and cinema data, creates a combobox for selecting cinemas and
     * initializes the payment panel with disabled options. When the user selects a cinema,
     * it calls the `readLoujain` method to retrieve the seance list for that cinema,
     * which is then displayed in a spinner.
     * 
     * @param url URL of a resource bundle that provides localization keys for the
     * function's output, such as film and cinema names.
     * 
     * 	- `url`: The URL provided by the user, which contains information about the film
     * and cinema.
     * 	- `rb`: A `ResourceBundle` object containing key-value pairs of localized messages
     * and resource keys.
     * 
     * @param rb ResourceBundle object, which provides localized messages and values for
     * the Java application.
     * 
     * 	- `rb`: A `ResourceBundle` object containing key-value pairs for resource string
     * messages.
     * 
     * The main properties of `rb` are:
     * 
     * 	- Key-value pairs: Contains key-value pairs in the form of `(key, value)`, where
     * `key` is a unique identifier for a message, and `value` is the corresponding message
     * text.
     * 	- Messages: `rb` provides a collection of messages that can be used to localize
     * user interface elements, such as labels, buttons, and menus.
     * 	- Culture-specific messages: `rb` allows developers to create culture-specific
     * messages by providing separate key-value pairs for each culture.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorpane_payment.getChildren().forEach(node -> {
            node.setDisable(true);
        });
        filmm.setDisable(false);
        cinemacombox_res.setDisable(false);
        cinemacombox_res.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            /**
             * Reads movie and cinema information to populate a combobox with seance options for
             * a given film and cinema. It clears the existing items, retrieves new seance data,
             * and adds it to the combobox.
             * 
             * @param observableValue observable value that has been changed, providing the updated
             * value and the previous value (in `s` and `t1`).
             * 
             * 	- `observableValue` is an `ObservableValue` object that represents changes to the
             * `Seance` list in the UI.
             * 	- The type of the value being observed is a `String`, indicating that the list
             * contains strings representing the names of `Seance` objects.
             * 	- The third argument, `t1`, is not used in this implementation.
             * 
             * @param s string value of a film label, which is used to retrieve the id of the
             * corresponding film and cinema id for displaying seance options in the combobox.
             * 
             * @param t1 2nd string value passed to the function, which is used to populate the
             * `checkcomboboxseance_res` widget with available seance options based on the selected
             * film and cinema.
             */
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                java.util.List<Seance> seances = new SeanceService().readLoujain(
                        new FilmService().getFilmByName(filmLabel_Payment.getText()).getId(),
                        new CinemaService().getCinemaByName(cinemacombox_res.getValue()).getId_cinema());
                checkcomboboxseance_res.setDisable(false);
                checkcomboboxseance_res.getItems().clear();
                System.out.println(new FilmService().getFilmByName(filmLabel_Payment.getText()).getId() + " "
                        + new CinemaService().getCinemaByName(cinemacombox_res.getValue()).getId_cinema());
                for (int i = 0; i < seances.size(); i++)
                    checkcomboboxseance_res.getItems().add("Seance " + (i + 1) + " " + seances.get(i).getDate() + " "
                            + seances.get(i).getHD() + "-" + seances.get(i).getHF());
            }
        });
        checkcomboboxseance_res.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            /**
             * Updates the spinner value based on the change in the film and cinema comboboxes,
             * retrieves the seating information for the selected film and cinema, and sets the
             * disable status of the payment nodes to false.
             * 
             * @param change change event that occurs when the user interacts with the `Loujain`
             * list, providing the opportunity to process the changes and update the `Seance`
             * objects accordingly.
             * 
             * 	- `change.next()` returns true if there are more changes to iterate over.
             * 	- `change.wasAdded()` indicates whether a new element was added to the list or
             * not. If true, the code inside the `if` statement is executed.
             */
            @Override
            public void onChanged(Change<? extends String> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        java.util.List<Seance> seances = new SeanceService().readLoujain(
                                new FilmService().getFilmByName(filmLabel_Payment.getText()).getId(),
                                new CinemaService().getCinemaByName(cinemacombox_res.getValue()).getId_cinema());
                        seance = seances.get(0);
                        anchorpane_payment.getChildren().forEach(node -> node.setDisable(false));
                        nbrplacepPayment_Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                                seances.get(0).getId_salle().getNb_places(), 1, 1));
                    }
                }
            }
        });
        nbrplacepPayment_Spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            /**
             * Reads the seances available for a given film and cinema, calculates the total price
             * based on the number of places paid, and displays the total price to the user.
             * 
             * @param observableValue observeable value that has changed, providing the new value
             * and the old value for the method to operate on.
             * 
             * 	- `observableValue`: An observable value of type `Integer`, which represents the
             * selected payment method.
             * 	- `integer`: The current value of the `observableValue`.
             * 
             * @param integer 2nd value passed to the `changed()` method, which is the `t1` value
             * from the observable value notification.
             * 
             * 	- `t1`: The value of `t1` is not explicitly mentioned in the provided code snippet.
             * However, based on the context, it can be inferred that `t1` represents a time
             * interval or a timestamp.
             * 
             * @param t1 2nd value passed to the `readLoujain()` method, which is used to retrieve
             * the film and cinema information for the payment calculation.
             * 
             * 	- `t1`: An `Integer` variable representing the ID of the film.
             */
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                List<Seance> seances = new SeanceService().readLoujain(
                        new FilmService().getFilmByName(filmLabel_Payment.getText()).getId(),
                        new CinemaService().getCinemaByName(cinemacombox_res.getValue()).getId_cinema());
                double totalPrice = 0;
                for (int i = 0; i < seances.size(); i++) {
                    totalPrice += seances.get(i).getPrix() * nbrplacepPayment_Spinner.getValue();
                }
                System.out.println(totalPrice);
                String total_txt = "Total : " + totalPrice + " Dt.";
                total.setText(total_txt);
            }
        });
        CinemaService cinemaService = new CinemaService();
        List<Cinema> cinemaList = cinemaService.read();
        if (cinemaList != null)
            for (Cinema cinema : cinemaList) {
                cinemacombox_res.getItems().add(cinema.getNom());
            }
    }
    /**
     * Processes a payment for a ticket purchase by first checking if the input is valid,
     * then calculating and charging the correct amount based on the ticket's price and
     * quantity, and finally saving the order to the database.
     * 
     * @param event Pay action event, which triggers the execution of the function and
     * enables the processing of the payment request.
     * 
     * 	- `event` is an ActionEvent that represents a user's action on the Pay button.
     */
    @FXML
    private void Pay(ActionEvent event) throws StripeException {
        TicketService scom = new TicketService();
        // TODO replace the next line with reservation
        // Seance seance;
        UserService sc = new UserService();
        if (isValidInput()) {
            float f = (float) seance.getPrix() * 100;
            int k = floatToInt(f);
            String url = Paymentuser.pay(k);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paiement");
            alert.setContentText("Paiement effectué avec succès, Votre Commande a été enregistré");
            alert.showAndWait();
            Stage stage = new Stage();
            final WebView webView = new WebView();
            final WebEngine webEngine = webView.getEngine();
            // Path dest = Path.of("stripe.pdf");
            // try (InputStream in = new URL(url).openStream()) {
            // Files.copy(in, Paths.get(dest.toUri()));
            // System.out.println("PDF downloaded successfully");
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // try {
            // Desktop.getDesktop().open(new File("stripe.pdf"));
            // } catch (IOException e) {
            // e.printStackTrace();
            // throw new RuntimeException(e);
            // }
            webView.getEngine().load(url);
            // create scene
            // stage.getIcons().add(new Image("/Images/logo.png"));
            stage.setTitle("localisation");
            Scene scene = new Scene(webView, 1000, 700, Color.web("#666970"));
            stage.setScene(scene);
            // show stage
            stage.show();
        }
        // int clientId=GuiLoginController.user.getId();
        int clientId = client.getId();
        if (clientId == 0)
            clientId = 1;
        // Creating a product order
        Ticket ticket = new Ticket();
        ticket.setPrix(ticket.getPrix() * ticket.getNbrdeplace()); // Replace with actual product price
        ticket.setNbrdeplace(1); // Replace with actual quantity
        ticket.setId_user(client);
        ticket.setId_seance(seance);
        TicketService p = new TicketService();
        p.update(ticket);
        // Saving the product order
        scom.create(ticket);
    }
    /**
     * Validates input fields for a Visa card number, month and year of expiration, and
     * CVC code. It returns `true` if all inputs are valid, otherwise it displays an error
     * message and returns `false`.
     * 
     * @returns a boolean value indicating whether the input is valid or not.
     */
    private boolean isValidInput() {
        if (!isValidVisaCardNo(carte.getText())) {
            showError("Numéro de carte invalide", "Veuillez entrer un numéro de carte Visa valide.");
            return false;
        }
        if (moisExp.getText().isEmpty() || !isNum(moisExp.getText()) || Integer.parseInt(moisExp.getText()) < 1
                || Integer.parseInt(moisExp.getText()) > 12) {
            showError("Mois d'expiration invalide", "Veuillez entrer un mois d'expiration valide (entre 1 et 12).");
            return false;
        }
        if (anneeExp.getText().isEmpty() || !isNum(anneeExp.getText())
                || Integer.parseInt(anneeExp.getText()) < LocalDate.now().getYear()) {
            showError("Année d'expiration invalide", "Veuillez entrer une année d'expiration valide.");
            return false;
        }
        if (cvc.getText().isEmpty() || !isNum(cvc.getText())) {
            showError("Code CVC invalide", "Veuillez entrer un code CVC numérique valide.");
            return false;
        }
        return true;
    }
    /**
     * Takes a string as input and checks if it matches the valid Visa card number format,
     * which is a 13-digit number consisting of four digits followed by a hyphen and then
     * another four digits.
     * 
     * @param text 13-digit credit card number to be validated.
     * 
     * @returns a boolean value indicating whether the given string represents a valid
     * Visa card number or not.
     * 
     * 	- The function returns a boolean value indicating whether the given text represents
     * a valid Visa card number or not.
     * 	- The output is based on the pattern `(^4[0-9]{12}(?:[0-9]{3})?$)` which is used
     * to validate the Visa card number.
     * 	- The pattern checks that the card number consists of 12 digits, with the first
     * 4 digits being "4", and optionally followed by a further 3 digits.
     */
    private boolean isValidVisaCardNo(String text) {
        String regex = "^4[0-9]{12}(?:[0-9]{3})?$";
        Pattern p = Pattern.compile(regex);
        CharSequence cs = text;
        Matcher m = p.matcher(cs);
        return m.matches();
    }
    /**
     * Creates an Alert object with an error title and message, displays it using the
     * `showAndWait()` method.
     * 
     * @param title title of an error message that is displayed in the alert box when the
     * `showError()` method is called.
     * 
     * @param message message to be displayed as the content of an error alert when the
     * function is called.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Sets the values of class variables `client` and `seance`, and updates the text of
     * a label `total`.
     * 
     * @param p Seance object, which is assigned to the class instance variable `seance`.
     * 
     * @param client Client object that is associated with the Seance object being initialized.
     */
    public void init(Seance p, Client client) {
        this.client = client;
        this.seance = p;
        total.setText("Payer " + p.getPrix() + "dinars");
    }
    /**
     * Loads and displays a FXML file named "filmuser.fxml" using Java's `FXMLLoader`
     * class, creating a new stage and scene to display the content.
     * 
     * @param event ActionEvent object that triggered the method execution, providing the
     * necessary information about the action that was performed.
     * 
     * Event: An ActionEvent object representing a user event.
     */
    public void switchtfillmmaa(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) anchorpane_payment.getScene().getWindow();
            Scene scene = new Scene(root, 1507, 855);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Creates a PDF receipt for a ticket purchase by adding text and graphics to a blank
     * page using a PDPageContentStream, then saving the document with a specified filename.
     * 
     * @param filename name of the output PDF file that the `createReceiptPDF` method
     * will save.
     * 
     * @param ticket ticket for which a receipt is to be generated, and its contents are
     * used to populate the receipt document.
     * 
     * 	- Ticket is an instance of the `Ticket` class.
     * 	- It contains information about the ticket purchase, such as the date and time
     * of purchase, the amount paid, and any other relevant details.
     */
    public void createReceiptPDF(String filename, Ticket ticket) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Receipt for Ticket Purchase");
            contentStream.endText();
            // Add more details as needed
            contentStream.close();
            document.save(filename);
        }
    }
    /**
     * Opens a PDF file using the desktop application.
     * 
     * @param filename name of the PDF file to be opened.
     */
    public void openPDF(String filename) throws IOException {
        if (Desktop.isDesktopSupported()) {
            File myFile = new File(filename);
            Desktop.getDesktop().open(myFile);
        }
    }
}
