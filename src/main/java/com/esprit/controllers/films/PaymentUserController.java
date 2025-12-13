package com.esprit.controllers.films;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.films.Ticket;
import com.esprit.enums.TicketStatus;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.cinemas.SeatService;

import com.esprit.services.films.FilmService;
import com.esprit.services.films.TicketService;
import com.esprit.utils.PageRequest;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.controlsfx.control.CheckComboBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.stream.Collectors;

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
    private Label selectedSeatsDisplay;
    @FXML
    private Label sessionTimeDisplay;
    @FXML
    private VBox orderSummary;
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
     * Determines whether the given string consists only of one or more decimal
     * digits.
     *
     * @param str the string to test
     * @return `true` if the string contains one or more digits and nothing else,
     * `false` otherwise
     */
    public static boolean isNum(final String str) {
        final String expression = "\\d+";
        return str.matches(expression);
    }

    /**
     * Convert a double to an int by discarding its fractional part.
     *
     * @param value the double value to convert; its fractional part will be
     *              discarded
     * @return the value's integer part with any fractional component removed
     * (truncated toward zero)
     */
    public static int doubleToInt(final double value) {
        return (int) value;
    }

    /**
     * Initialize the controller with the given client and display the film name on
     * the payment view.
     * <p>
     * Stores the provided client for later use and sets the film label to the
     * supplied filmName.
     * session selection and price updates.
     *
     * <p>
     * Disables payment controls on startup, enables the film and cinema controls,
     * fills the cinema ComboBox,
     * adds a listener that loads available movie sessions for the selected film and
     * cinema into the session
     * CheckComboBox, enables payment-related controls and configures the seat-count
     * spinner when a session is chosen,
     * and updates the displayed total price when the spinner value changes.
     * </p>
     *
     * @param url the location used to resolve relative paths for the root object;
     *            may be null
     * @param rb  the ResourceBundle for localized strings used by the UI; may be
     *            null
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
             * Populate the session selector with movie sessions for the film shown in
             * {@code filmLabel_Payment} and the
             * cinema selected in {@code cinemacombox_res}.
             *
             * Clears existing session entries, enables the session control, and adds one
             * readable entry per session
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
                        .add("MovieSession " + (i + 1) + " " + moviesessions.get(i).getStartTime().toLocalDate() + " "
                            + moviesessions.get(i).getStartTime() + "-" + moviesessions.get(i).getEndTime());
                }

            }

        });
        this.checkcomboboxmoviesession_res.getCheckModel().getCheckedItems()
            .addListener(new ListChangeListener<String>() {
                /**
                 * Reacts to additions in the checked session list by selecting the
                 * corresponding movie session and enabling payment controls.
                 *
                 * When an item is added, sets the controller's active MovieSession for the
                 * current film and cinema, enables all nodes in the payment pane, and
                 * configures the seat-count spinner with a minimum of 1 and a maximum equal to
                 * the session's hall seat capacity (initial value 1).
                 *
                 * @param change the list-change event for checked session entries; processed to
                 *               detect added items
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
             * Recomputes and updates the total price label for the currently selected film
             * and cinema using the current seat count.
             *
             * This reads the movie sessions for the selected film and cinema, multiplies
             * each session's price by the number-of-seats spinner value, and sets the total
             * label text.
             *
             * @param observableValue the observable integer property that triggered the
             *                        change
             * @param integer         the previous value reported by the observable
             * @param t1              the new value reported by the observable
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
     * Process the user's payment for the selected movie session, charge the card,
     * update seat status, and persist the ticket orders.
     * Enhanced version with proper seat reservation and multiple ticket creation.
     *
     * @param event the ActionEvent triggered by the Pay button
     */
    @FXML
    private void Pay(final ActionEvent event) {
        final TicketService ticketService = new TicketService();

        if (!this.isValidInput()) {
            return;
        }

        if (this.moviesession == null) {
            showError("Session Error", "No movie session selected.");
            return;
        }

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            showError("Seat Error", "No seats selected for reservation.");
            return;
        }

        try {
            // Calculate total amount with seat multipliers
            double totalAmount = selectedSeats.stream()
                .mapToDouble(seat -> {
                    double basePrice = moviesession.getPrice() != null ? moviesession.getPrice() : 15.0;
                    double multiplier = seat.getPriceMultiplier() != null ? seat.getPriceMultiplier() : 1.0;
                    return basePrice * multiplier;
                })
                .sum();

            // Prepare payment details
            String name = client.getFirstName() + " " + client.getLastName();
            String email = client.getEmail();
            String cardNumber = carte.getText();
            int expMonth = Integer.parseInt(moisExp.getText());
            int expYear = Integer.parseInt(anneeExp.getText());
            String cvcCode = cvc.getText();

            // Process payment
            boolean paymentSuccess = com.esprit.utils.PaymentProcessor.processPayment(
                name, email, (float) totalAmount, cardNumber, expMonth, expYear, cvcCode);

            if (paymentSuccess) {
                // First, ensure seats are saved to database and have IDs
                prepareSeatForDatabase(selectedSeats);
                
                // Create tickets for each selected seat
                for (Seat seat : selectedSeats) {
                    // Calculate individual seat price
                    double basePrice = moviesession.getPrice() != null ? moviesession.getPrice() : 15.0;
                    double multiplier = seat.getPriceMultiplier() != null ? seat.getPriceMultiplier() : 1.0;
                    double seatPrice = basePrice * multiplier;

                    // Create ticket for this seat
                    Ticket ticket = new Ticket();
                    ticket.setPricePaid((float) seatPrice);
                    ticket.setClient(this.client);
                    ticket.setMovieSession(this.moviesession);
                    ticket.setSeat(seat);
                    
                    // Set required fields that were missing
                    ticket.setStatus(TicketStatus.CONFIRMED);
                    ticket.setPurchaseTime(java.time.LocalDateTime.now());
                    ticket.setQrCode(generateQRCode(ticket));
                    
                    // Save ticket to database
                    ticketService.create(ticket);
                    
                    LOGGER.info(String.format("✅ TICKET CREATED: User ID: %d, Movie: %s, Seat: %s%s, Price: %.2f TND, Status: %s, QR: %s", 
                        client.getId(),
                        moviesession.getFilm().getTitle(),
                        getRowLetter(seat), 
                        seat.getSeatNumber(), 
                        seatPrice,
                        ticket.getStatus(),
                        ticket.getQrCode()));
                }

                // Verify tickets were created
                int ticketCount = verifyTicketsCreated();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Payment Successful");
                successAlert.setHeaderText("Booking Confirmed!");
                successAlert.setContentText(String.format(
                    "Payment of %.2f TND processed successfully.\n" +
                    "Your tickets have been reserved for %s.\n" +
                    "Seats: %s\n" +
                    "Session: %s\n" +
                    "Tickets Created: %d\n\n" +
                    "You can view your tickets in the 'My Tickets' section.",
                    totalAmount,
                    moviesession.getFilm().getTitle(),
                    selectedSeats.stream()
                        .map(seat -> getRowLetter(seat) + seat.getSeatNumber())
                        .collect(Collectors.joining(", ")),
                    moviesession.getStartTime().toLocalTime() + " - " + moviesession.getEndTime().toLocalTime(),
                    ticketCount
                ));
                successAlert.showAndWait();

                // Navigate back to cinema dashboard
                navigateBackToDashboard();

            } else {
                showError("Payment Failed", "The payment could not be processed. Please check your card details and try again.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment", e);
            showError("Payment Error", "An error occurred while processing your payment: " + e.getMessage());
        }
    }
    
    /**
     * Get row letter for display
     */
    private String getRowLetter(Seat seat) {
        try {
            int rowNum = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "1");
            return String.valueOf((char) ('A' + rowNum - 1));
        } catch (NumberFormatException e) {
            return seat.getRowLabel() != null ? seat.getRowLabel() : "?";
        }
    }
    
    /**
     * Prepare seats for database storage by ensuring they have proper IDs
     * This method handles seats that were created dynamically in the UI
     */
    private void prepareSeatForDatabase(List<Seat> seats) {
        SeatService seatService = new SeatService();
        
        for (Seat seat : seats) {
            if (seat.getId() == null) {
                // Set cinema hall reference if missing
                if (seat.getCinemaHall() == null && moviesession != null && moviesession.getCinemaHall() != null) {
                    seat.setCinemaHall(moviesession.getCinemaHall());
                }
                
                // Try to find existing seat in database first
                Seat existingSeat = findExistingSeat(seatService, seat);
                if (existingSeat != null) {
                    // Use existing seat data
                    seat.setId(existingSeat.getId());
                    LOGGER.info(String.format("Found existing seat ID %d for %s%s", 
                        existingSeat.getId(), getRowLetter(seat), seat.getSeatNumber()));
                } else {
                    // Create new seat in database
                    try {
                        seatService.create(seat);
                        // Since create doesn't return ID, we need to find it again
                        Seat createdSeat = findExistingSeat(seatService, seat);
                        if (createdSeat != null) {
                            seat.setId(createdSeat.getId());
                            LOGGER.info(String.format("Created new seat ID %d for %s%s", 
                                createdSeat.getId(), getRowLetter(seat), seat.getSeatNumber()));
                        } else {
                            // Fallback: use position-based ID
                            seat.setId(generateTempSeatId(seat));
                            LOGGER.warning(String.format("Using fallback ID %d for seat %s%s", 
                                seat.getId(), getRowLetter(seat), seat.getSeatNumber()));
                        }
                    } catch (Exception e) {
                        // If creation fails, use fallback ID
                        seat.setId(generateTempSeatId(seat));
                        LOGGER.warning(String.format("Seat creation failed, using fallback ID %d for %s%s: %s", 
                            seat.getId(), getRowLetter(seat), seat.getSeatNumber(), e.getMessage()));
                    }
                }
            }
        }
    }
    
    /**
     * Find existing seat in database by hall, row, and seat number
     */
    private Seat findExistingSeat(SeatService seatService, Seat seat) {
        if (seat.getCinemaHall() == null || seat.getCinemaHall().getId() == null) {
            return null;
        }
        
        try {
            List<Seat> hallSeats = seatService.getSeatsByCinemaHallId(seat.getCinemaHall().getId());
            return hallSeats.stream()
                .filter(s -> seat.getRowLabel().equals(s.getRowLabel()) && 
                           seat.getSeatNumber().equals(s.getSeatNumber()))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            LOGGER.warning("Error finding existing seat: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate a temporary seat ID based on seat position
     * This is a workaround for dynamically created seats
     */
    private long generateTempSeatId(Seat seat) {
        // Create a unique ID based on hall, row, and seat number
        long hallId = moviesession.getCinemaHall() != null ? moviesession.getCinemaHall().getId() : 1L;
        int row = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "1");
        int seatNum = Integer.parseInt(seat.getSeatNumber() != null ? seat.getSeatNumber() : "1");
        
        // Generate unique ID: hallId * 10000 + row * 100 + seatNum
        return hallId * 10000 + row * 100 + seatNum;
    }
    
    /**
     * Generate a QR code string for the ticket
     */
    private String generateQRCode(Ticket ticket) {
        // Create a unique QR code based on ticket information
        String movieTitle = ticket.getMovieSession().getFilm().getTitle();
        String seatInfo = getRowLetter(ticket.getSeat()) + ticket.getSeat().getSeatNumber();
        String sessionTime = ticket.getMovieSession().getStartTime().toString();
        String clientName = ticket.getClient().getFirstName() + " " + ticket.getClient().getLastName();
        
        // Format: RAKCHA-MOVIE_TITLE-SEAT-SESSION_TIME-CLIENT_HASH
        String qrData = String.format("RAKCHA-%s-%s-%s-%d", 
            movieTitle.replaceAll("\\s+", "").toUpperCase(),
            seatInfo,
            sessionTime.substring(0, 16).replace(":", "").replace("-", ""),
            Math.abs(clientName.hashCode())
        );
        
        LOGGER.info("Generated QR code: " + qrData);
        return qrData;
    }
    
    /**
     * Verify that tickets were successfully created in the database
     */
    private int verifyTicketsCreated() {
        try {
            TicketService verifyService = new TicketService();
            List<Ticket> userTickets = verifyService.getTicketsByUser(client.getId());
            
            LOGGER.info(String.format("📊 TICKET VERIFICATION: User %d has %d total tickets in database", 
                client.getId(), userTickets.size()));
                
            // Count tickets for this specific session
            long sessionTickets = userTickets.stream()
                .filter(t -> t.getMovieSession() != null && 
                           t.getMovieSession().getId().equals(moviesession.getId()))
                .count();
                
            LOGGER.info(String.format("🎬 SESSION TICKETS: %d tickets found for session %d (%s)", 
                sessionTickets, moviesession.getId(), moviesession.getFilm().getTitle()));
                
            return userTickets.size();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error verifying tickets: " + e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Navigate back to the cinema dashboard
     */
    private void navigateBackToDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/cinemas/DashboardClientCinema.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) Pay.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cinema Dashboard - Booking Confirmed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating back to dashboard", e);
            // Fallback: try to navigate to film user view
            try {
                FXMLLoader fallbackLoader = new FXMLLoader(getClass().getResource("/ui/films/filmuser.fxml"));
                AnchorPane fallbackRoot = fallbackLoader.load();
                Stage stage = (Stage) Pay.getScene().getWindow();
                Scene scene = new Scene(fallbackRoot);
                stage.setScene(scene);
                stage.setTitle("Films");
            } catch (Exception fallbackError) {
                LOGGER.log(Level.SEVERE, "Fallback navigation also failed", fallbackError);
                showError("Navigation Error", "Unable to navigate back to dashboard. Please restart the application.");
            }
        }
    }

    /**
     * Checks that all payment form fields contain valid values.
     * Enhanced with test card support and better validation.
     *
     * @return true if all input fields are valid, false otherwise
     */
    private boolean isValidInput() {
        // Check if card number is valid (more flexible for test cards)
        if (!this.isValidCardNumber(this.carte.getText())) {
            this.showTestCardInfo();
            return false;
        }

        if (this.moisExp.getText().isEmpty() || !PaymentUserController.isNum(this.moisExp.getText())
            || 1 > Integer.parseInt(moisExp.getText()) || 12 < Integer.parseInt(moisExp.getText())) {
            this.showError("Invalid Expiry Month", "Please enter a valid expiry month (1-12).");
            return false;
        }

        if (this.anneeExp.getText().isEmpty() || !PaymentUserController.isNum(this.anneeExp.getText())) {
            this.showError("Invalid Expiry Year", "Please enter a valid expiry year.");
            return false;
        }

        // More lenient year validation for test mode
        int currentYear = LocalDate.now().getYear();
        int enteredYear = Integer.parseInt(this.anneeExp.getText());
        if (enteredYear < currentYear - 10 || enteredYear > currentYear + 20) {
            this.showError("Invalid Expiry Year", "Please enter a reasonable expiry year.");
            return false;
        }

        if (this.cvc.getText().isEmpty() || !PaymentUserController.isNum(this.cvc.getText())) {
            this.showError("Invalid CVC", "Please enter a valid numeric CVC code.");
            return false;
        }

        return true;
    }
    
    /**
     * Enhanced card validation that supports test cards
     */
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        // Remove spaces and check length
        String cleanCard = cardNumber.replaceAll("\\s+", "");
        
        // Must be between 13-19 digits
        if (!cleanCard.matches("\\d{13,19}")) {
            return false;
        }
        
        // Check for common test cards (always valid in development)
        if (isTestCard(cleanCard)) {
            return true;
        }
        
        // For production, use Luhn algorithm
        return isValidLuhn(cleanCard);
    }
    
    /**
     * Check if this is a known test card
     */
    private boolean isTestCard(String cardNumber) {
        return cardNumber.equals("4242424242424242") ||
               cardNumber.equals("4000000000000002") ||
               cardNumber.equals("5555555555554444") ||
               cardNumber.equals("378282246310005") ||
               cardNumber.equals("4000000000009995") ||
               cardNumber.equals("4111111111111111");
    }
    
    /**
     * Validate card number using Luhn algorithm
     */
    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
    
    /**
     * Show test card information to help users
     */
    private void showTestCardInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Card Information");
        alert.setHeaderText("Use Test Cards for Development");
        alert.setContentText(
            "For testing, please use one of these test cards:\n\n" +
            "• 4242 4242 4242 4242 (Visa - Success)\n" +
            "• 4000 0000 0000 0002 (Visa Debit - Success)\n" +
            "• 5555 5555 5555 4444 (Mastercard - Success)\n" +
            "• 4000 0000 0000 9995 (Visa - Declined)\n\n" +
            "Use any future expiry date (e.g., 12/2025) and any 3-digit CVC (e.g., 123)."
        );
        alert.showAndWait();
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
     * Initializes the controller for a given movie session and client and updates
     * the total label to show the session price.
     *
     * @param p      the MovieSession to initialize the controller with; its price
     *               is displayed in the total label
     * @param client the Client who will be associated with the purchase
     */
    public void init(final MovieSession p, final Client client) {
        this.client = client;
        moviesession = p;
        this.total.setText("Payer " + p.getPrice() + "dinars");
    }

    /**
     * Initialize the controller with a movie session, client, and preselected seats.
     * Enhanced version with proper UI updates and seat price calculation.
     *
     * @param moviesession  the movie session being purchased
     * @param client        the client making the purchase
     * @param selectedSeats the list of seats already selected for this purchase
     */
    public void initWithSeats(MovieSession moviesession, Client client, List<Seat> selectedSeats) {
        this.moviesession = moviesession;
        this.client = client;
        this.selectedSeats = selectedSeats;
        
        // Update UI elements
        if (filmLabel_Payment != null) {
            filmLabel_Payment.setText(moviesession.getFilm().getTitle());
        }
        
        // Calculate total price considering seat multipliers
        double totalPrice = selectedSeats.stream()
            .mapToDouble(seat -> {
                double basePrice = moviesession.getPrice() != null ? moviesession.getPrice() : 15.0;
                double multiplier = seat.getPriceMultiplier() != null ? seat.getPriceMultiplier() : 1.0;
                return basePrice * multiplier;
            })
            .sum();
            
        if (total != null) {
            total.setText(String.format("%.2f TND", totalPrice));
        }
        
        // Update seat display
        if (selectedSeatsDisplay != null) {
            String seatLabels = selectedSeats.stream()
                .map(seat -> {
                    try {
                        int rowNum = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "1");
                        String row = String.valueOf((char) ('A' + rowNum - 1));
                        String seatNum = seat.getSeatNumber() != null ? seat.getSeatNumber() : "?";
                        String type = "VIP".equals(seat.getType()) ? " 👑" : "";
                        return row + seatNum + type;
                    } catch (NumberFormatException e) {
                        return seat.getRowLabel() + seat.getSeatNumber();
                    }
                })
                .collect(Collectors.joining(", "));
            selectedSeatsDisplay.setText(seatLabels);
        }
        
        // Update session time display
        if (sessionTimeDisplay != null && moviesession.getStartTime() != null && moviesession.getEndTime() != null) {
            String timeDisplay = String.format("%s - %s", 
                moviesession.getStartTime().toLocalTime().toString(),
                moviesession.getEndTime().toLocalTime().toString());
            sessionTimeDisplay.setText(timeDisplay);
        }

        // Disable seat selection controls since seats are already selected
        if (nbrplacepPayment_Spinner != null) {
            nbrplacepPayment_Spinner.setDisable(true);
            if (nbrplacepPayment_Spinner.getValueFactory() != null) {
                nbrplacepPayment_Spinner.getValueFactory().setValue(selectedSeats.size());
            }
        }
        
        // Disable cinema and session selection
        if (cinemacombox_res != null) {
            cinemacombox_res.setDisable(true);
        }
    }

    /**
     * Switches the current window's scene to the film user view defined at
     * "/ui/films/filmuser.fxml".
     * <p>
     * If loading or setting the scene fails, the exception is logged at SEVERE
     * level.
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
     * <p>
     * The generated PDF is written to the provided filename and includes
     * information derived from the given ticket.
     * </p>
     *
     * @param filename the path of the output PDF file
     * @param ticket   the ticket whose purchase details will be included in the
     *                 receipt
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
     * @throws IOException if an I/O error occurs while launching the associated
     *                     application
     */
    public void openPDF(final String filename) throws IOException {
        if (Desktop.isDesktopSupported()) {
            final File myFile = new File(filename);
            Desktop.getDesktop().open(myFile);
        }

    }

}
