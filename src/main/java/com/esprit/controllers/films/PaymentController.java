package com.esprit.controllers.films;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.utils.PaymentProcessor;
import com.stripe.exception.StripeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller class for handling payment processing in the RAKCHA application.
 *
 * <p>
 * This controller is responsible for validating and processing payments for
 * movie tickets.
 * It handles credit card information validation, payment processing, and user
 * feedback.
 * </p>
 *
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Credit card number validation using regex patterns</li>
 * <li>Expiration date validation</li>
 * <li>CVC validation</li>
 * <li>Email format validation</li>
 * <li>Integration with Stripe payment processing</li>
 * </ul>
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PaymentController {

    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());

    @FXML
    private Label filmLabel_Payment;
    @FXML
    private Label total;
    @FXML
    private Button pay_btn;
    @FXML
    private TextField email;
    @FXML
    private TextField num_card;
    @FXML
    private Spinner<Integer> MM;
    @FXML
    private Spinner<Integer> YY;
    @FXML
    private Spinner<Integer> cvc;
    @FXML
    private Spinner<Integer> nbrplacepPayment_Spinner;
    private double total_pay;
    private MovieSession moviesession;
    @FXML
    private TextField client_name;
    @FXML
    private Button back_btn;
    @FXML
    private TextField spinnerTextField;
    @FXML
    private Pane anchorpane_payment;
    @FXML
    private CheckComboBox<String> checkcomboboxmoviesession_res;
    @FXML
    private ComboBox<String> cinemacombox_res;

    /**
     * Initializes the payment form's month, year, and CVC spinners.
     * <p>
     * Month spinner accepts values 1–12, year spinner accepts a large positive range, and CVC spinner accepts values 1–999.
     */
    @FXML
    void initialize() {
        final SpinnerValueFactory<Integer> valueFactory_month = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
            12, 1, 1);// (min,max,startvalue,incrementValue)
        final SpinnerValueFactory<Integer> valueFactory_year = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
            9999999, 1, 1);// (min,max,startvalue,incrementValue)
        final SpinnerValueFactory<Integer> valueFactory_cvc = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999,
            1, 1);// (min,max,startvalue,incrementValue)
        this.MM.setValueFactory(valueFactory_month);
        this.YY.setValueFactory(valueFactory_year);
        this.cvc.setValueFactory(valueFactory_cvc);
    }


    /**
     * Binds the given MovieSession to the controller, sets the total payment amount, and initializes the month, year, and CVC spinners.
     *
     * <p>Also attaches a listener to the CVC spinner's editor to update the spinner value when the user edits the text field.</p>
     *
     * @param s the MovieSession to display and use for initializing payment fields
     */
    public void setData(final MovieSession s) {
        moviesession = s;
        final MovieSessionService sc = new MovieSessionService();
        // Terrain t = ts.diplay(r.getTerrain_id());
        this.total_pay = s.getPrice();
        final int mm = LocalDate.now().getMonthValue();
        final int yy = LocalDate.now().getYear();
        final SpinnerValueFactory<Integer> valueFactory_month = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
            12, mm, 1);// (min,max,startvalue,incrementValue)
        final SpinnerValueFactory<Integer> valueFactory_year = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
            9999999, yy, 1);// (min,max,startvalue,incrementValue)
        final SpinnerValueFactory<Integer> valueFactory_cvc = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999,
            1, 1);// (min,max,startvalue,incrementValue)
        this.MM.setValueFactory(valueFactory_month);
        this.YY.setValueFactory(valueFactory_year);
        this.cvc.setValueFactory(valueFactory_cvc);
        // String total_txt = "Total : " + +" Dt.";
        // total.setText(total_txt);
        this.spinnerTextField = this.cvc.getEditor();
        this.spinnerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    this.cvc.getValueFactory().setValue(Integer.parseInt(newValue));
                } catch (final NumberFormatException e) {
                    // Handle invalid input
                }

            }
        );
    }


    /**
     * Validate payment inputs and attempt to process the payment.
     *
     * <p>
     * Validates client name, email, card number, CVC, and expiration date; if validation succeeds,
     * submits the payment via PaymentProcessor. The method presents error alerts and highlights
     * invalid fields when validation fails, and presents a success or failure alert after attempting payment.
     * </p>
     *
     * @param event the action event that triggered this handler
     * @throws StripeException if an error occurs during Stripe payment processing
     */
    @FXML
    private void payment(final ActionEvent event) throws StripeException {
        PaymentController.LOGGER.info(String.valueOf(this.cvc.getValue()));
        if (this.client_name.getText().isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Name");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.client_name.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(client_name).play();
        } else if (this.email.getText().isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Email");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.email.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(email).play();
        } else if (!this.isValidEmail(this.email.getText())) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid Email address.");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.email.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(email).play();
        } else if (this.num_card.getText().isEmpty()) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Card Number");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.num_card.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(num_card).play();
        } else if (!this.check_cvc(this.cvc.getValue())) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The CVC number should contain three digits");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.cvc.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(cvc).play();
        } else if (!this.check_expDate(this.YY.getValue(), this.MM.getValue())) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid expiration date");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            this.MM.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            this.YY.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            // new animatefx.animation.Shake(MM).play();
            // new animatefx.animation.Shake(YY).play();
        } else {
            this.client_name.setStyle(null);
            this.email.setStyle(null);
            this.num_card.setStyle(null);
            this.cvc.setStyle(null);
            this.MM.setStyle(null);
            this.YY.setStyle(null);
            final boolean isValid = this.check_card_num(this.num_card.getText());
            if (!isValid) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please enter a valid Card number");
                alert.setTitle("Problem");
                alert.setHeaderText(null);
                alert.showAndWait();
                this.num_card.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                // new animatefx.animation.Shake(num_card).play();
            } else {
                this.num_card.setStyle(null);
                final String name = this.client_name.getText();
                final String email_txt = this.email.getText();
                final String num = this.num_card.getText();
                final int yy = this.YY.getValue();
                final int mm = this.MM.getValue();
                final String cvc_num = String.valueOf(this.cvc.getValue());
                final boolean payment_result = PaymentProcessor.processPayment(name, email_txt,
                    Integer.parseInt(this.total.getText().replaceAll("\\D+", "")), num, mm, yy, cvc_num);
                PaymentController.LOGGER.info("the payment is done: " + payment_result);
                if (payment_result) {
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("Successful Payment.");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    // this.reservation.setResStatus(true);
                    // ReservationService rs = new ReservationService();
                    // rs.updateEntity(this.reservation);
                    // redirect_to_successPage();
                } else {
                    final Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Payment Failed.");
                    alert.setTitle("Problem");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    // redirect_to_FailPage();
                }

            }

        }

    }


    /**
     * Checks whether the given integer represents a three-digit credit card CVC.
     *
     * @param value the CVC value to check
     * @return `true` if the value represents a three-digit number, `false` otherwise
     */
    private boolean check_cvc(final int value) {
        final String cvc_txt = String.valueOf(value);
        return 3 == cvc_txt.length();
    }


    /**
     * Determines whether the given expiration year and month are the same as or later than the current year and month.
     *
     * @param value_y  the expiration year
     * @param value_mm the expiration month (1-12)
     * @return `true` if `value_y` is greater than or equal to the current year and `value_mm` is greater than or equal to the current month, `false` otherwise
     */
    private boolean check_expDate(final int value_y, final int value_mm) {
        boolean valid = false;
        final LocalDate date = LocalDate.now();
        if ((value_y >= date.getYear()) && (value_mm >= date.getMonthValue())) {
            valid = true;
        }

        return valid;
    }


    /**
     * Determine whether a string is a valid credit card number format.
     *
     * <p>Checks that the input, after trimming, has length between 13 and 19 and
     * matches common issuer patterns such as Visa, MasterCard, American Express,
     * Discover, Diners Club, and JCB.</p>
     *
     * @param cardNumber the credit card number string to validate (whitespace will be trimmed)
     * @return true if the input has 13–19 characters and matches a common card issuer pattern, false otherwise
     */
    private boolean check_card_num(String cardNumber) {
        // Trim the input string to remove any leading or trailing whitespace
        cardNumber = cardNumber.trim();
        // Step 1: Check length
        final int length = cardNumber.length();
        if (13 > length || 19 < length) {
            return false;
        }

        final String regex = "^(?:(?:4[0-9]{12}(?:[0-9]{3})?)|(?:5[1-5][0-9]{14})|(?:6(?:011|5[0-9][0-9])[0-9]{12})|(?:3[47][0-9]{13})|(?:3(?:0[0-5]|[68][0-9])[0-9]{11})|(?:((?:2131|1800|35[0-9]{3})[0-9]{11})))$";
        // Create a Pattern object with the regular expression
        final Pattern pattern = Pattern.compile(regex);
        // Match the pattern against the credit card number
        final Matcher matcher = pattern.matcher(cardNumber);
        // Return true if the pattern matches, false otherwise
        return matcher.matches();
    }


    /**
     * Determines whether the given string is a valid email address.
     *
     * @param email the email address to validate; leading and trailing whitespace are ignored
     * @return true if the email contains a valid local part, an '@' separator, and a domain with a top-level domain, false otherwise
     */
    public boolean isValidEmail(String email) {
        // Trim the input string to remove any leading or trailing whitespace
        email = email.trim();
        // Regular expression pattern to match an email address
        final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        // Compile the pattern
        final Pattern pattern = Pattern.compile(regex);
        // Match the pattern against the email address
        final Matcher matcher = pattern.matcher(email);
        // Return true if the pattern matches, false otherwise
        return matcher.matches();
    }


    /**
     * Navigate to the payment success view and display it on the current stage.
     */
/**
 * Navigate to the payment failure view and display it on the current stage.
 */
    /**
     * Placeholder for navigating to the payment success page.
     *
     * <p>Currently a no-op; navigation implementation is commented out and this method does not change application state or UI.</p>
     */
    private void redirect_to_successPage() {
        // try {
        // FXMLLoader loader = new
        // FXMLLoader(getClass().getResource("/ui//ui/Success_page.fxml"));
        // Parent root = loader.load();
        // //UPDATE The Controller with Data :
        // Success_pageController controller = loader.getController();
        // controller.setData(this.reservation);
        // Scene scene = new Scene(root);
        // Stage stage = (Stage) pay_btn.getScene().getWindow();
        // stage.setScene(scene);
        // } catch (IOException ex) {
        // LOGGER.info(ex.getMessage());
        // }

    }


    //

    /**
     * Placeholder that would navigate the UI to the payment failure view but currently performs no action.
     *
     * <p>The implementation is commented out; its intended behavior is to load the failure FXML, update the
     * failure controller with reservation data, and replace the current scene.</p>
     */
    private void redirect_to_FailPage() {
        // try {
        // FXMLLoader loader = new
        // FXMLLoader(getClass().getResource("/ui//ui/Fail_page.fxml"));
        // Parent root = loader.load();
        // //UPDATE The Controller with Data :
        // Fail_pageController controller = loader.getController();
        // controller.setData(this.reservation);
        // Scene scene = new Scene(root);
        // Stage stage = (Stage) pay_btn.getScene().getWindow();
        // stage.setScene(scene);
        // } catch (IOException ex) {
        // LOGGER.info(ex.getMessage());
        // }

    }


    //

    /**
     * Navigate to the client reservation list view for the current reservation's client.
     * <p>
     * Loads the reservation list FXML, initializes its controller with the current
     * reservation's client ID, and replaces the current scene to display that view.
     *
     * @param event the ActionEvent that triggered the navigation (e.g., Back button click)
     */
    @FXML
    private void redirectToListReservation(final ActionEvent event) {
        // try {
        // FXMLLoader loader = new
        // FXMLLoader(getClass().getResource("/ui//ui/Reservation_view_client.fxml"));
        // Parent root = loader.load();
        // //UPDATE The Controller with Data :
        // Reservation_view_client controller = loader.getController();
        // controller.setData(this.reservation.getClient_id());
        // Scene scene = new Scene(root);
        // Stage stage = (Stage) back_btn.getScene().getWindow();
        // stage.setScene(scene);
        // } catch (IOException ex) {
        // LOGGER.info(ex.getMessage());
        // }

    }

}
