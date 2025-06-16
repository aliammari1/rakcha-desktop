package com.esprit.controllers.films;

import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.CheckComboBox;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.utils.PaymentProcessor;
import com.stripe.exception.StripeException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

/**
 * Is responsible for handling payment processing and redirection to either a
 * success or failure page after a successful payment. The class includes
 * several methods that validate credit card numbers, email addresses, and
 * expiration dates, and checks if the client's information is valid before
 * redirecting to the appropriate page. Additionally, it provides an alert
 * mechanism for displaying informational messages during the payment process.
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
     * Sets up three SpinnerValueFactories, `MM`, `YY`, and `cvc`, for displaying
     * dates in the format `mm/yyyy/ccvc`. It assigns the value factories to the
     * respective Spinners.
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
     * Sets the `MovieSession` object's fields and updates spinner values for the
     * year, month, and cvc based on the `MovieSession` object's `prix` field and
     * sets the text of a text field with the total amount.
     *
     * @param s
     *            MovieSession object passed into the function, which is used to set
     *            the values of various fields within the `MovieSession` object.
     *            <p>
     *            - `moviesession`: represents an object of the MovieSession class,
     *            containing information about a moviesession. - `prix`: a float
     *            representing the price of the moviesession. - `terrain_id`: an
     *            integer representing the terrain ID for display purposes. -
     *            `monthValue` and `year`: integers representing the current month
     *            and year respectively. - `cvc`: an object of the CVC class, used
     *            to display a spinner for the number of seats available in the
     *            moviesession.
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
        });
    }

    /**
     * Validates user input and processes a payment using a third-party payment
     * processor. If the payment is successful, it displays an information alert;
     * otherwise, it displays an error alert.
     *
     * @param event
     *            payment action that triggered the function execution, and it is
     *            used to identify the specific payment method being processed.
     *            <p>
     *            - `LOGGER.info(cvc.getValue());`: This line prints the value of
     *            the `CVC` field.
     *            <p>
     *            `event` is an instance of the `ActionEvent` class, which
     *            represents a user event related to a button press or other action
     *            in the JavaFX application. It provides information about the
     *            event, such as the source of the event (e.g., a button), the type
     *            of event (e.g., "click"), and any additional data related to the
     *            event.
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
     * Verifies if a given integer value can be represented as a three-digit credit
     * card number (CVC) by checking its length. If the length is equal to 3, the
     * function returns `true`, otherwise it returns `false`.
     *
     * @param value
     *            3-digit credit card number to be checked for length.
     * @returns a boolean value indicating whether the input string has a length of
     *          3.
     */
    private boolean check_cvc(final int value) {
        final String cvc_txt = String.valueOf(value);
        return 3 == cvc_txt.length();
    }

    /**
     * Takes two parameters `value_y` and `value_mm`, checks if the date represented
     * by those parameters is after the current date, and returns `true` if it is,
     * or `false` otherwise.
     *
     * @param value_y
     *            4-digit year value in the expiration date.
     * @param value_mm
     *            month of the date to be checked, which is used to determine if the
     *            date is valid.
     * @returns a boolean value indicating whether the given date is valid.
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
     * Checks whether a given credit card number follows a specific format by
     * matching it against a regular expression pattern. It returns `true` if the
     * pattern matches and `false` otherwise.
     *
     * @param cardNumber
     *            13-19 digit credit card number to be checked against the regular
     *            expression pattern for validation.
     * @returns a boolean value indicating whether the provided credit card number
     *          matches the specified pattern.
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
     * Checks whether a given email address is valid by matching it against a
     * regular expression pattern that matches most standard email addresses.
     *
     * @param email
     *            email address to be checked for validity.
     * @returns a boolean value indicating whether the provided email address is
     *          valid or not.
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
     * Redirects the user to a success page with data loaded from an external
     * resource.
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
     * Redirects the user to a "Fail Page" by loading an FXML file, updating the
     * controller with data from the reservation object, and displaying the scene on
     * the stage.
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
     * Redirects the user to a new scene containing a reservation view client. It
     * loads the reservation view client fxml file, sets the controller data with
     * the client ID, and displays the stage in a new window.
     *
     * @param event
     *            triggering of an action, specifically the click on the "Back"
     *            button, which calls the `redirectToListReservation()` method.
     *            <p>
     *            - `event` is an instance of the `ActionEvent` class.
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
