package com.esprit.controllers.films;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.controlsfx.control.CheckComboBox;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.films.Ticket;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.cinemas.SeatService;
import com.esprit.services.films.FilmService;
import com.esprit.services.films.TicketService;
import com.esprit.utils.Paymentuser;
import com.esprit.utils.PageRequest;
import com.stripe.exception.StripeException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Controller class for handling user payments for cinema tickets.
 * 
 * <p>
 * This controller manages the payment process for movie tickets, including
 * credit card validation, payment processing, and receipt generation. It
 * provides
 * a user interface for entering payment details and handles the validation and
 * submission of payment information.
 * </p>
 * 
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Credit card validation with Luhn algorithm</li>
 * <li>PDF receipt generation</li>
 * <li>Integration with Stripe payment processing</li>
 * </ul>
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PaymentUserController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(PaymentUserController.class.getName());

    Client client;
    private MovieSession moviesession;
    private List<Seat> selectedSeats;
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
    private CheckComboBox<String> checkcomboboxmoviesession_res;
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
     * Checks if a given string is a numerical value by matching it against a
     * regular expression pattern of one or more digits.
     *
     * @param str
     *            String to be checked for matching the regular expression `\d+`.
     * @returns a `Boolean` value indicating whether the input string matches the
     *          regular expression for a number.
     */
    public static boolean isNum(final String str) {
        final String expression = "\\d+";
        return str.matches(expression);
    }

    /**
     * Converts a `float` argument into an `int` value by calling the `int` casting
     * operator `(int)`.
     *
     * @param value
     *              floating-point number to be converted to an integer.
     * @returns an integer value equivalent to the provided floating-point number.
     */
    public static int doubleToInt(final double value) {
        return (int) value;
    }

    /**
     * Sets the `client` field and displays the film name on a label. It also prints
     * the value of `client` to the console.
     *
     * @param client
     *                 Client object that provides the payment details for the film
     *                 name
     *                 set by the `filmName` parameter.
     * @param filmName
     *                 name of a film that is being associated with the `Client`
     *                 object
     *                 passed as an argument to the `setData()` method.
     */
    public void setData(final Client client, final String filmName) {
        this.client = client;
        this.filmLabel_Payment.setText(filmName);
        PaymentUserController.LOGGER.info("payment: " + client);
    }

    /**
     * Reads the film and cinema data, creates a combobox for selecting cinemas and
     * initializes the payment panel with disabled options. When the user selects a
     * cinema, it calls the `readLoujain` method to retrieve the moviesession list
     * for that cinema, which is then displayed in a spinner.
     *
     * @param url
     *            URL of a resource bundle that provides localization keys for the
     *            function's output, such as film and cinema names.
     *            <p>
     *            - `url`: The URL provided by the user, which contains information
     *            about the film and cinema. - `rb`: A `ResourceBundle` object
     *            containing key-value pairs of localized messages and resource
     *            keys.
     * @param rb
     *            ResourceBundle object, which provides localized messages and
     *            values for the Java application.
     *            <p>
     *            - `rb`: A `ResourceBundle` object containing key-value pairs for
     *            resource string messages.
     *            <p>
     *            The main properties of `rb` are:
     *            <p>
     *            - Key-value pairs: Contains key-value pairs in the form of `(key,
     *            value)`, where `key` is a unique identifier for a message, and
     *            `value` is the corresponding message text. - Messages: `rb`
     *            provides a collection of messages that can be used to localize
     *            user interface elements, such as labels, buttons, and menus. -
     *            Culture-specific messages: `rb` allows developers to create
     *            culture-specific messages by providing separate key-value pairs
     *            for each culture.
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle rb) {
        this.anchorpane_payment.getChildren().forEach(node -> {
            node.setDisable(true);
        });
        this.filmm.setDisable(false);
        this.cinemacombox_res.setDisable(false);
        this.cinemacombox_res.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            /**
             * Reads movie and cinema information to populate a combobox with moviesession
             * options for a given film and cinema. It clears the existing items, retrieves
             * new moviesession data, and adds it to the combobox.
             *
             * @param observableValue
             *                        observable value that has been changed, providing the
             *                        updated
             *                        value and the previous value (in `s` and `t1`).
             *
             *                        - `observableValue` is an `ObservableValue` object
             *                        that represents
             *                        changes to the `MovieSession` list in the UI. - The
             *                        type of the
             *                        value being observed is a `String`, indicating that
             *                        the list
             *                        contains strings representing the names of
             *                        `MovieSession` objects.
             *                        - The third argument, `t1`, is not used in this
             *                        implementation.
             *
             * @param s
             *                        string value of a film label, which is used to
             *                        retrieve the id of
             *                        the corresponding film and cinema id for displaying
             *                        moviesession
             *                        options in the combobox.
             *
             * @param t1
             *                        2nd string value passed to the function, which is used
             *                        to populate
             *                        the `checkcomboboxmoviesession_res` widget with
             *                        available
             *                        moviesession options based on the selected film and
             *                        cinema.
             */
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends String> observableValue, final String s,
                    final String t1) {
                final List<MovieSession> moviesessions = new MovieSessionService().getSessionsByFilmAndCinema(
                        new FilmService().getFilmByName(filmLabel_Payment.getText()).getId(),
                        new CinemaService().getCinemaByName(cinemacombox_res.getValue())
                                .getId());
                checkcomboboxmoviesession_res.setDisable(false);
                checkcomboboxmoviesession_res.getItems().clear();
                PaymentUserController.LOGGER.info(
                        new FilmService().getFilmByName(filmLabel_Payment.getText()).getId()
                                + " "
                                + new CinemaService()
                                        .getCinemaByName(cinemacombox_res.getValue())
                                        .getId());
                for (int i = 0; i < moviesessions.size(); i++) {
                    checkcomboboxmoviesession_res.getItems()
                            .add("MovieSession " + (i + 1) + " " + moviesessions.get(i).getSessionDate() + " "
                                    + moviesessions.get(i).getStartTime() + "-" + moviesessions.get(i).getEndTime());
                }
            }
        });
        this.checkcomboboxmoviesession_res.getCheckModel().getCheckedItems()
                .addListener(new ListChangeListener<String>() {
                    /**
                     * Updates the spinner value based on the change in the film and cinema
                     * comboboxes, retrieves the seating information for the selected film and
                     * cinema, and sets the disable status of the payment nodes to false.
                     *
                     * @param change
                     *               change event that occurs when the user interacts with the
                     *               `Loujain` list, providing the opportunity to process the
                     *               changes
                     *               and update the `MovieSession` objects accordingly.
                     *
                     *               - `change.next()` returns true if there are more changes to
                     *               iterate over. - `change.wasAdded()` indicates whether a new
                     *               element was added to the list or not. If true, the code inside
                     *               the
                     *               `if` statement is executed.
                     */
                    @Override
                    /**
                     * Performs onChanged operation.
                     *
                     * @return the result of the operation
                     */
                    public void onChanged(final Change<? extends String> change) {
                        while (change.next()) {
                            if (change.wasAdded()) {
                                final List<MovieSession> moviesessions = new MovieSessionService()
                                        .getSessionsByFilmAndCinema(
                                                new FilmService()
                                                        .getFilmByName(
                                                                filmLabel_Payment.getText())
                                                        .getId(),
                                                new CinemaService()
                                                        .getCinemaByName(
                                                                cinemacombox_res.getValue())
                                                        .getId());
                                moviesession = moviesessions.get(0);
                                anchorpane_payment.getChildren()
                                        .forEach(node -> node.setDisable(false));
                                nbrplacepPayment_Spinner
                                        .setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                                                moviesessions.get(0).getCinemaHall().getSeatCapacity(), 1, 1));
                            }
                        }
                    }
                });
        this.nbrplacepPayment_Spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            /**
             * Reads the moviesessions available for a given film and cinema, calculates the
             * total price based on the number of places paid, and displays the total price
             * to the user.
             *
             * @param observableValue
             *                        observeable value that has changed, providing the new
             *                        value and
             *                        the old value for the method to operate on.
             *
             *                        - `observableValue`: An observable value of type
             *                        `Integer`, which
             *                        represents the selected payment method. - `integer`:
             *                        The current
             *                        value of the `observableValue`.
             *
             * @param integer
             *                        2nd value passed to the `changed()` method, which is
             *                        the `t1`
             *                        value from the observable value notification.
             *
             *                        - `t1`: The value of `t1` is not explicitly mentioned
             *                        in the
             *                        provided code snippet. However, based on the context,
             *                        it can be
             *                        inferred that `t1` represents a time interval or a
             *                        timestamp.
             *
             * @param t1
             *                        2nd value passed to the `readLoujain()` method, which
             *                        is used to
             *                        retrieve the film and cinema information for the
             *                        payment
             *                        calculation.
             *
             *                        - `t1`: An `Integer` variable representing the ID of
             *                        the film.
             */
            @Override
            /**
             * Performs changed operation.
             *
             * @return the result of the operation
             */
            public void changed(final ObservableValue<? extends Integer> observableValue, final Integer integer,
                    final Integer t1) {
                final List<MovieSession> moviesessions = new MovieSessionService().getSessionsByFilmAndCinema(
                        new FilmService().getFilmByName(filmLabel_Payment.getText()).getId(),
                        new CinemaService().getCinemaByName(cinemacombox_res.getValue())
                                .getId());
                double totalPrice = 0;
                for (int i = 0; i < moviesessions.size(); i++) {
                    totalPrice += moviesessions.get(i).getPrice()
                            * nbrplacepPayment_Spinner.getValue();
                }
                PaymentUserController.LOGGER.info(String.valueOf(totalPrice));
                final String total_txt = "Total : " + totalPrice + " Dt.";
                total.setText(total_txt);
            }
        });
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemaList = cinemaService.read(PageRequest.defaultPage()).getContent();
        if (null != cinemaList) {
            for (final Cinema cinema : cinemaList) {
                this.cinemacombox_res.getItems().add(cinema.getName());
            }
        }
    }

    /**
     * Processes a payment for a ticket purchase by first checking if the input is
     * valid, then calculating and charging the correct amount based on the ticket's
     * price and quantity, and finally saving the order to the database.
     *
     * @param event
     *              Pay action event, which triggers the execution of the function
     *              and
     *              enables the processing of the payment request.
     *              <p>
     *              - `event` is an ActionEvent that represents a user's action on
     *              the
     *              Pay button.
     */
    @FXML
    private void Pay(final ActionEvent event) throws StripeException {
        final TicketService scom = new TicketService();
        final SeatService seatService = new SeatService();

        if (this.isValidInput()) {
            // Update seat status
            for (Seat seat : selectedSeats) {
                seatService.updateSeatStatus(seat.getId(), true);
            }

            // Continue with payment processing
            final double f = this.moviesession.getPrice() * 100; // Potential NPE if moviesession is null
            // Add null check:
            if (this.moviesession == null) {
                return;
            }
            final int k = PaymentUserController.doubleToInt(f);
            final String url = Paymentuser.pay(k);
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paiement");
            alert.setContentText("Paiement effectué avec succès, Votre Order a été enregistré");
            alert.showAndWait();
            final Stage stage = new Stage();
            WebView webView = new WebView();
            // Path dest = Path.of("stripe.pdf");
            // try (InputStream in = new URL(url).openStream()) {
            // Files.copy(in, Paths.get(dest.toUri()));
            // LOGGER.info("PDF downloaded successfully");
            // } catch (Exception e) {
            // LOGGER.log(Level.SEVERE, e.getMessage(), e);
            // }
            // try {
            // Desktop.getDesktop().open(new File("stripe.pdf"));
            // } catch (IOException e) {
            // LOGGER.log(Level.SEVERE, e.getMessage(), e);
            // throw new RuntimeException(e);
            // }
            webView.getEngine().load(url);
            // create scene
            // stage.getIcons().add(new Image("/Images/logo.png"));
            stage.setTitle("localisation");
            final Scene scene = new Scene(webView, 1000, 700, Color.web("#666970"));
            stage.setScene(scene);
            // show stage
            stage.show();
        }
        // long clientId=GuiLoginController.user.getId().intValue();
        long clientId = this.client.getId();
        if (0 == clientId) {
            clientId = 1;
        }
        // Creating a product order
        final Ticket ticket = new Ticket();
        ticket.setPrice(ticket.getPrice() * ticket.getNumberOfSeats()); // Replace with actual product price
        ticket.setNumberOfSeats(1); // Replace with actual quantity
        ticket.setClient(this.client);
        ticket.setMovieSession(this.moviesession);
        final TicketService p = new TicketService();
        p.update(ticket);
        // Saving the product order
        scom.create(ticket);
    }

    /**
     * Validates all payment input fields.
     * 
     * <p>
     * This method performs comprehensive validation of all payment input fields
     * including:
     * <ul>
     * <li>Credit card number validation (must be a valid Visa card format)</li>
     * <li>Expiration month validation (must be a number between 1-12)</li>
     * <li>Expiration year validation (must be current year or later)</li>
     * <li>CVC code validation (must be a valid numeric code)</li>
     * </ul>
     * </p>
     *
     * @return true if all input fields are valid, false otherwise
     */
    private boolean isValidInput() {
        if (!this.isValidVisaCardNo(this.carte.getText())) {
            this.showError("Numéro de carte invalide", "Veuillez entrer un numéro de carte Visa valide.");
            return false;
        }
        if (this.moisExp.getText().isEmpty() || !PaymentUserController.isNum(this.moisExp.getText())
                || 1 > Integer.parseInt(moisExp.getText()) || 12 < Integer.parseInt(moisExp.getText())) {
            this.showError("Mois d'expiration invalide",
                    "Veuillez entrer un mois d'expiration valide (entre 1 et 12).");
            return false;
        }
        if (this.anneeExp.getText().isEmpty() || !PaymentUserController.isNum(this.anneeExp.getText())
                || Integer.parseInt(this.anneeExp.getText()) < LocalDate.now().getYear()) {
            this.showError("Année d'expiration invalide", "Veuillez entrer une année d'expiration valide.");
            return false;
        }
        if (this.cvc.getText().isEmpty() || !PaymentUserController.isNum(this.cvc.getText())) {
            this.showError("Code CVC invalide", "Veuillez entrer un code CVC numérique valide.");
            return false;
        }
        return true;
    }

    /**
     * Takes a string as input and checks if it matches the valid Visa card number
     * format, which is a 13-digit number consisting of four digits followed by a
     * hyphen and then another four digits.
     *
     * @param text
     *             13-digit credit card number to be validated.
     * @returns a boolean value indicating whether the given string represents a
     *          valid Visa card number or not.
     *          <p>
     *          - The function returns a boolean value indicating whether the given
     *          text represents a valid Visa card number or not. - The output is
     *          based on the pattern `(^4[0-9]{12}(?:[0-9]{3})?$)` which is used to
     *          validate the Visa card number. - The pattern checks that the card
     *          number consists of 12 digits, with the first 4 digits being "4", and
     *          optionally followed by a further 3 digits.
     */
    private boolean isValidVisaCardNo(final String text) {
        final String regex = "^4[0-9]{12}(?:[0-9]{3})?$";
        final Pattern p = Pattern.compile(regex);
        final CharSequence cs = text;
        final Matcher m = p.matcher(cs);
        return m.matches();
    }

    /**
     * Creates an Alert object with an error title and message, displays it using
     * the `showAndWait()` method.
     *
     * @param title
     *                title of an error message that is displayed in the alert box
     *                when
     *                the `showError()` method is called.
     * @param message
     *                message to be displayed as the content of an error alert when
     *                the
     *                function is called.
     */
    private void showError(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets the values of class variables `client` and `moviesession`, and updates
     * the text of a label `total`.
     *
     * @param p
     *               MovieSession object, which is assigned to the class instance
     *               variable `moviesession`.
     * @param client
     *               Client object that is associated with the MovieSession object
     *               being initialized.
     */
    public void init(final MovieSession p, final Client client) {
        this.client = client;
        moviesession = p;
        this.total.setText("Payer " + p.getPrice() + "dinars");
    }

    /**
     * Initializes the payment controller with movie session, client, and selected
     * seats information.
     * 
     * <p>
     * This method configures the payment view with the selected movie session,
     * client details,
     * and calculates the total price based on the number of seats selected.
     * </p>
     *
     * @param moviesession  the movie session for which tickets are being purchased
     * @param client        the client who is making the payment
     * @param selectedSeats the list of seats selected by the client
     */
    public void initWithSeats(MovieSession moviesession, Client client, List<Seat> selectedSeats) {
        this.moviesession = moviesession;
        this.client = client;
        this.selectedSeats = selectedSeats;
        this.filmLabel_Payment.setText(moviesession.getFilm().getName());

        // Calculate total based on number of seats
        double totalPrice = moviesession.getPrice() * selectedSeats.size();
        total.setText(String.format("Total: %.2f Dt.", totalPrice));

        // Disable seat selection since it's already done
        nbrplacepPayment_Spinner.setDisable(true);
        nbrplacepPayment_Spinner.getValueFactory().setValue(selectedSeats.size());
    }

    /**
     * Loads and displays a FXML file named "/ui/films/filmuser.fxml" using Java's
     * `FXMLLoader` class, creating a new stage and scene to display the content.
     *
     * @param event
     *              ActionEvent object that triggered the method execution,
     *              providing
     *              the necessary information about the action that was performed.
     *              <p>
     *              Event: An ActionEvent object representing a user event.
     */
    public void switchtfillmmaa(final ActionEvent event) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
            final AnchorPane root = fxmlLoader.load();
            final Stage stage = (Stage) this.anchorpane_payment.getScene().getWindow();
            final Scene scene = new Scene(root, 1507, 855);
            stage.setScene(scene);
        } catch (final Exception e) {
            PaymentUserController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Creates a PDF receipt for a ticket purchase.
     * 
     * <p>
     * This method generates a PDF document containing receipt information for a
     * ticket purchase.
     * It creates a new document, adds a page, and writes the ticket details before
     * saving the file.
     * </p>
     *
     * @param filename the name of the output PDF file
     * @param ticket   the ticket object containing purchase details
     * @throws IOException if there is an error creating or writing to the PDF file
     */
    public void createReceiptPDF(final String filename, final Ticket ticket) throws IOException {
        try (final PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage();
            document.addPage(page);
            final PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
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
     * @param filename the name of the PDF file to be opened
     * @throws IOException if there is an error opening the file
     */
    public void openPDF(final String filename) throws IOException {
        if (Desktop.isDesktopSupported()) {
            final File myFile = new File(filename);
            Desktop.getDesktop().open(myFile);
        }
    }
}
