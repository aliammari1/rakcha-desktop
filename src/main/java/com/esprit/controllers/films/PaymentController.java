package com.esprit.controllers.films;
import com.esprit.controllers.ClientSideBarController;
import com.esprit.models.cinemas.Seance;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.utils.PaymentProcessor;
import com.stripe.exception.StripeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Is responsible for handling payment processing and redirection to either a success
 * or failure page after a successful payment. The class includes several methods
 * that validate credit card numbers, email addresses, and expiration dates, and
 * checks if the client's information is valid before redirecting to the appropriate
 * page. Additionally, it provides an alert mechanism for displaying informational
 * messages during the payment process.
 */
public class PaymentController {
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
    private float total_pay;
    private Seance seance;
    @FXML
    private TextField client_name;
    @FXML
    private Button back_btn;
    @FXML
    private TextField spinnerTextField;
    @FXML
    private Pane anchorpane_payment;
    @FXML
    private CheckComboBox<String> checkcomboboxseance_res;
    @FXML
    private ComboBox<String> cinemacombox_res;
    /**
     * Sets up three SpinnerValueFactories, `MM`, `YY`, and `cvc`, for displaying dates
     * in the format `mm/yyyy/ccvc`. It assigns the value factories to the respective Spinners.
     */
    @FXML
    void initialize() {
        SpinnerValueFactory<Integer> valueFactory_month = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1, 1);// (min,max,startvalue,incrementValue)
        SpinnerValueFactory<Integer> valueFactory_year = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999999, 1, 1);// (min,max,startvalue,incrementValue)
        SpinnerValueFactory<Integer> valueFactory_cvc = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1, 1);// (min,max,startvalue,incrementValue)
        MM.setValueFactory(valueFactory_month);
        YY.setValueFactory(valueFactory_year);
        cvc.setValueFactory(valueFactory_cvc);
    }
    /**
     * Sets the `Seance` object's fields and updates spinner values for the year, month,
     * and cvc based on the `Seance` object's `prix` field and sets the text of a text
     * field with the total amount.
     * 
     * @param s Seance object passed into the function, which is used to set the values
     * of various fields within the `Seance` object.
     * 
     * 	- `seance`: represents an object of the Seance class, containing information about
     * a seance.
     * 	- `prix`: a float representing the price of the seance.
     * 	- `terrain_id`: an integer representing the terrain ID for display purposes.
     * 	- `monthValue` and `year`: integers representing the current month and year respectively.
     * 	- `cvc`: an object of the CVC class, used to display a spinner for the number of
     * seats available in the seance.
     */
    public void setData(Seance s) {
        this.seance = s;
        SeanceService sc = new SeanceService();
//        Terrain t = ts.diplay(r.getTerrain_id());
        total_pay = (float) s.getPrix();
        int mm = LocalDate.now().getMonthValue();
        int yy = LocalDate.now().getYear();
        SpinnerValueFactory<Integer> valueFactory_month = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, mm, 1);// (min,max,startvalue,incrementValue)
        SpinnerValueFactory<Integer> valueFactory_year = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999999, yy, 1);// (min,max,startvalue,incrementValue)
        SpinnerValueFactory<Integer> valueFactory_cvc = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1, 1);// (min,max,startvalue,incrementValue)
        MM.setValueFactory(valueFactory_month);
        YY.setValueFactory(valueFactory_year);
        cvc.setValueFactory(valueFactory_cvc);
        // String total_txt = "Total : " + +" Dt.";
        // total.setText(total_txt);
        spinnerTextField = cvc.getEditor();
        spinnerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                cvc.getValueFactory().setValue(Integer.parseInt(newValue));
            } catch (NumberFormatException e) {
                // Handle invalid input
            }
        });
    }
    /**
     * Validates user input and processes a payment using a third-party payment processor.
     * If the payment is successful, it displays an information alert; otherwise, it
     * displays an error alert.
     * 
     * @param event payment action that triggered the function execution, and it is used
     * to identify the specific payment method being processed.
     * 
     * 	- `System.out.println(cvc.getValue());`: This line prints the value of the `CVC`
     * field.
     * 
     * `event` is an instance of the `ActionEvent` class, which represents a user event
     * related to a button press or other action in the JavaFX application. It provides
     * information about the event, such as the source of the event (e.g., a button), the
     * type of event (e.g., "click"), and any additional data related to the event.
     */
    @FXML
    private void payment(ActionEvent event) throws StripeException {
        System.out.println(cvc.getValue());
        if (client_name.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Name");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            client_name.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
//            new animatefx.animation.Shake(client_name).play();
        } else if (email.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Email");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            email.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
//            new animatefx.animation.Shake(email).play();
        } else if (!isValidEmail(email.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid Email address.");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            email.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
//            new animatefx.animation.Shake(email).play();
        } else if (num_card.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to input your Card Number");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            num_card.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
//            new animatefx.animation.Shake(num_card).play();
        } else if (!check_cvc(cvc.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The CVC number should contain three digits");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            cvc.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            //new animatefx.animation.Shake(cvc).play();
        } else if (!check_expDate(YY.getValue(), MM.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid expiration date");
            alert.setTitle("Problem");
            alert.setHeaderText(null);
            alert.showAndWait();
            MM.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            YY.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            //new animatefx.animation.Shake(MM).play();
            //new animatefx.animation.Shake(YY).play();
        } else {
            client_name.setStyle(null);
            email.setStyle(null);
            num_card.setStyle(null);
            cvc.setStyle(null);
            MM.setStyle(null);
            YY.setStyle(null);
            boolean isValid = check_card_num(num_card.getText());
            if (!isValid) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please enter a valid Card number");
                alert.setTitle("Problem");
                alert.setHeaderText(null);
                alert.showAndWait();
                num_card.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
//                new animatefx.animation.Shake(num_card).play();
            } else {
                num_card.setStyle(null);
                String name = client_name.getText();
                String email_txt = email.getText();
                String num = num_card.getText();
                int yy = YY.getValue();
                int mm = MM.getValue();
                String cvc_num = String.valueOf(cvc.getValue());
                boolean payment_result = PaymentProcessor.processPayment(name, email_txt, Integer.parseInt(total.getText().replaceAll("\\D+", "")), num, mm, yy, cvc_num);
                System.out.println("the payment is done: " + payment_result);
                if (payment_result) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("Successful Payment.");
                    alert.setHeaderText(null);
                    alert.showAndWait();
//                    this.reservation.setResStatus(true);
//                    ReservationService rs = new ReservationService();
//                    rs.updateEntity(this.reservation);
//                    redirect_to_successPage();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Payment Failed.");
                    alert.setTitle("Problem");
                    alert.setHeaderText(null);
                    alert.showAndWait();
//                    redirect_to_FailPage();
                }
            }
        }
    }
    /**
     * Verifies if a given integer value can be represented as a three-digit credit card
     * number (CVC) by checking its length. If the length is equal to 3, the function
     * returns `true`, otherwise it returns `false`.
     * 
     * @param value 3-digit credit card number to be checked for length.
     * 
     * @returns a boolean value indicating whether the input string has a length of 3.
     */
    private boolean check_cvc(int value) {
        String cvc_txt = String.valueOf(value);
        boolean valid = cvc_txt.length() == 3;
        return valid;
    }
    /**
     * Takes two parameters `value_y` and `value_mm`, checks if the date represented by
     * those parameters is after the current date, and returns `true` if it is, or `false`
     * otherwise.
     * 
     * @param value_y 4-digit year value in the expiration date.
     * 
     * @param value_mm month of the date to be checked, which is used to determine if the
     * date is valid.
     * 
     * @returns a boolean value indicating whether the given date is valid.
     */
    private boolean check_expDate(int value_y, int value_mm) {
        boolean valid = false;
        LocalDate date = LocalDate.now();
        if ((value_y >= date.getYear()) && (value_mm >= date.getMonthValue())) {
            valid = true;
        }
        return valid;
    }
    /**
     * Checks whether a given credit card number follows a specific format by matching
     * it against a regular expression pattern. It returns `true` if the pattern matches
     * and `false` otherwise.
     * 
     * @param cardNumber 13-19 digit credit card number to be checked against the regular
     * expression pattern for validation.
     * 
     * @returns a boolean value indicating whether the provided credit card number matches
     * the specified pattern.
     */
    private boolean check_card_num(String cardNumber) {
        // Trim the input string to remove any leading or trailing whitespace
        cardNumber = cardNumber.trim();
        // Step 1: Check length
        int length = cardNumber.length();
        if (length < 13 || length > 19) {
            return false;
        }
        String regex = "^(?:(?:4[0-9]{12}(?:[0-9]{3})?)|(?:5[1-5][0-9]{14})|(?:6(?:011|5[0-9][0-9])[0-9]{12})|(?:3[47][0-9]{13})|(?:3(?:0[0-5]|[68][0-9])[0-9]{11})|(?:((?:2131|1800|35[0-9]{3})[0-9]{11})))$";
        // Create a Pattern object with the regular expression
        Pattern pattern = Pattern.compile(regex);
        // Match the pattern against the credit card number
        Matcher matcher = pattern.matcher(cardNumber);
        // Return true if the pattern matches, false otherwise
        return matcher.matches();
    }
    /**
     * Checks whether a given email address is valid by matching it against a regular
     * expression pattern that matches most standard email addresses.
     * 
     * @param email email address to be checked for validity.
     * 
     * @returns a boolean value indicating whether the provided email address is valid
     * or not.
     */
    public boolean isValidEmail(String email) {
        // Trim the input string to remove any leading or trailing whitespace
        email = email.trim();
        // Regular expression pattern to match an email address
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);
        // Match the pattern against the email address
        Matcher matcher = pattern.matcher(email);
        // Return true if the pattern matches, false otherwise
        return matcher.matches();
    }
    /**
     * Redirects the user to a success page with data loaded from an external resource.
     */
    private void redirect_to_successPage() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/reservation/Success_page.fxml"));
//            Parent root = loader.load();
//            //UPDATE The Controller with Data :
//            Success_pageController controller = loader.getController();
//            controller.setData(this.reservation);
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) pay_btn.getScene().getWindow();
//            stage.setScene(scene);
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
    }
    //
    /**
     * Redirects the user to a "Fail Page" by loading an FXML file, updating the controller
     * with data from the reservation object, and displaying the scene on the stage.
     */
    private void redirect_to_FailPage() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/reservation/Fail_page.fxml"));
//            Parent root = loader.load();
//            //UPDATE The Controller with Data :
//            Fail_pageController controller = loader.getController();
//            controller.setData(this.reservation);
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) pay_btn.getScene().getWindow();
//            stage.setScene(scene);
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
    }
    //
    /**
     * Redirects the user to a new scene containing a reservation view client. It loads
     * the reservation view client fxml file, sets the controller data with the client
     * ID, and displays the stage in a new window.
     * 
     * @param event triggering of an action, specifically the click on the "Back" button,
     * which calls the `redirectToListReservation()` method.
     * 
     * 	- `event` is an instance of the `ActionEvent` class.
     */
    @FXML
    private void redirectToListReservation(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/reservation/Reservation_view_client.fxml"));
//            Parent root = loader.load();
//            //UPDATE The Controller with Data :
//            Reservation_view_client controller = loader.getController();
//            controller.setData(this.reservation.getClient_id());
//            Scene scene = new Scene(root);
//            Stage stage = (Stage) back_btn.getScene().getWindow();
//            stage.setScene(scene);
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
    }
}
