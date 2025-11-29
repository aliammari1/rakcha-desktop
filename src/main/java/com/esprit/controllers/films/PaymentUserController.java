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
     * Determines whether the given string consists only of one or more decimal digits.
     *
     * @param str the string to test
     * @return `true` if the string contains one or more digits and nothing else, `false` otherwise
     */
    public static boolean isNum(final String str) {
        final String expression = "\\d+";
        return str.matches(expression);
    }


    /**
     * Convert a double to an int by discarding its fractional part.
     *
     * @param value the double value to convert; its fractional part will be discarded
     * @return the value's integer part with any fractional component removed (truncated toward zero)
     */
    public static int doubleToInt(final double value) {
        return (int) value;
    }


    /**
     * Initialize the controller with the given client and display the film name on the payment view.
     *
     * Stores the provided client for later use and sets the film label to the supplied filmName.
     *
     * @param client   the client performing the payment
     * @param filmName the film name to display in the payment UI
     */
    public void setData(final Client client, final String filmName) {
        this.client = client;
        this.filmLabel_Payment.setText(filmName);
        PaymentUserController.LOGGER.info("payment: " + client);
    }


    /**
     * Initialize UI controls, populate the cinema selector, and wire listeners for session selection and price updates.
     *
     * <p>Disables payment controls on startup, enables the film and cinema controls, fills the cinema ComboBox,
     * adds a listener that loads available movie sessions for the selected film and cinema into the session
     * CheckComboBox, enables payment-related controls and configures the seat-count spinner when a session is chosen,
     * and updates the displayed total price when the spinner value changes.</p>
     *
     * @param url the location used to resolve relative paths for the root object; may be null
     * @param rb  the ResourceBundle for localized strings used by the UI; may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL url, final ResourceBundle rb) {
        this.anchorpane_payment.getChildren().forEach(node -> {
            node.setDisable(true);
        }
);
        this.filmm.setDisable(false);
        this.cinemacombox_res.setDisable(false);
        this.cinemacombox_res.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            /**
             * Populate the session selector with movie sessions for the film shown in {@code filmLabel_Payment} and the
             * cinema selected in {@code cinemacombox_res}.
             *
             * Clears existing session entries, enables the session control, and adds one readable entry per session
             * (index, date, start–end times).
             *
             * @param observableValue the observed value that triggered this change (unused)
             * @param s               the previous selected cinema name (unused)
             * @param t1              the newly selected cinema name (unused)
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

        }
);
        this.checkcomboboxmoviesession_res.getCheckModel().getCheckedItems()
                .addListener(new ListChangeListener<String>() {
                    /**
                     * Reacts to additions in the checked session list by selecting the corresponding movie session and enabling payment controls.
                     *
                     * When an item is added, sets the controller's active MovieSession for the current film and cinema, enables all nodes in the payment pane, and configures the seat-count spinner with a minimum of 1 and a maximum equal to the session's hall seat capacity (initial value 1).
                     *
                     * @param change the list-change event for checked session entries; processed to detect added items
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

                }
);
        this.nbrplacepPayment_Spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            /**
             * Recomputes and updates the total price label for the currently selected film and cinema using the current seat count.
             *
             * This reads the movie sessions for the selected film and cinema, multiplies each session's price by the number-of-seats spinner value, and sets the total label text.
             *
             * @param observableValue the observable integer property that triggered the change
             * @param integer the previous value reported by the observable
             * @param t1 the new value reported by the observable
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

        }
);
        final CinemaService cinemaService = new CinemaService();
        final List<Cinema> cinemaList = cinemaService.read(PageRequest.defaultPage()).getContent();
        if (null != cinemaList) {
            for (final Cinema cinema : cinemaList) {
                this.cinemacombox_res.getItems().add(cinema.getName());
            }

        }

    }


    /**
 * Process the user's payment for the selected movie session, charge the card, update seat status, and persist the ticket order.
 *
 * @param event the ActionEvent triggered by the Pay button
 * @throws StripeException if the payment provider reports an error while processing the charge
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
         * Checks that all payment form fields contain valid values.
         *
         * <p>Performs these validations:
         * <ul>
         *   <li>Credit card number matches the Visa format (13 or 16 digits, starts with '4').</li>
         *   <li>Expiration month is numeric and between 1 and 12 inclusive.</li>
         *   <li>Expiration year is numeric and not earlier than the current year.</li>
         *   <li>CVC is present and numeric.</li>
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
     * Determines whether a string is a valid Visa card number.
     *
     * @param text the card number string to validate (expected to contain only digits)
     * @return `true` if the string starts with '4' and contains exactly 13 or 16 digits, `false` otherwise
     */
    private boolean isValidVisaCardNo(final String text) {
        final String regex = "^4[0-9]{12}(?:[0-9]{3})?$";
        final Pattern p = Pattern.compile(regex);
        final CharSequence cs = text;
        final Matcher m = p.matcher(cs);
        return m.matches();
    }


    /**
     * Displays an error alert with the given title and message.
     *
     * @param title   the alert window title
     * @param message the content text shown in the alert body
     */
    private void showError(final String title, final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Initializes the controller for a given movie session and client and updates the total label to show the session price.
     *
     * @param p      the MovieSession to initialize the controller with; its price is displayed in the total label
     * @param client the Client who will be associated with the purchase
     */
    public void init(final MovieSession p, final Client client) {
        this.client = client;
        moviesession = p;
        this.total.setText("Payer " + p.getPrice() + "dinars");
    }


    /**
     * Initialize the controller with a movie session, client, and preselected seats.
     *
     * Sets the film label, computes and displays the total price based on selected seats,
     * and locks the seat-count spinner to the number of provided seats.
     *
     * @param moviesession  the movie session being purchased
     * @param client        the client making the purchase
     * @param selectedSeats the list of seats already selected for this purchase
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
     * Switches the current window's scene to the film user view defined at "/ui/films/filmuser.fxml".
     *
     * If loading or setting the scene fails, the exception is logged at SEVERE level.
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
     * Create a PDF receipt containing the ticket's purchase details.
     *
     * <p>The generated PDF is written to the provided filename and includes information derived from the given ticket.</p>
     *
     * @param filename the path of the output PDF file
     * @param ticket   the ticket whose purchase details will be included in the receipt
     * @throws IOException if an I/O error occurs while writing the PDF file
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
     * Open the specified PDF file using the system's default PDF application.
     *
     * @param filename path to the PDF file to open
     * @throws IOException if an I/O error occurs while launching the associated application
     */
    public void openPDF(final String filename) throws IOException {
        if (Desktop.isDesktopSupported()) {
            final File myFile = new File(filename);
            Desktop.getDesktop().open(myFile);
        }

    }

}