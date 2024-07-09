package com.esprit.controllers.users;
import com.esprit.models.users.Client;
import com.esprit.models.users.Responsable_de_cinema;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
public class SignUpController {
    @FXML
    AnchorPane anchorPane;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField num_telephoneTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TextField prenomTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    void initialize() {
        Tooltip tooltip = new Tooltip();
        UserService userService = new UserService();
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!emailTextField.getText().matches(emailRegex)) {
                emailTextField.getStyleClass().removeAll("checked");
                // emailErrorLabel.setText("this mail format is wrong");
                emailTextField.getStyleClass().add("notChecked");
            } else if (userService.checkEmailFound(newValue)) {
                emailTextField.getStyleClass().removeAll("checked");
                // emailErrorLabel.setText("this mail address is used");
                emailTextField.getStyleClass().add("notChecked");
            } else {
                // emailErrorLabel.setText("");
                emailTextField.getStyleClass().removeAll("notChecked");
                emailTextField.getStyleClass().add("checked");
            }
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (passwordTextField.getLength() < 8) {
                passwordTextField.getStyleClass().removeAll("checked");
                // passwordErrorLabel.setText("the password must contain at least 8
                // characters");
                emailTextField.getStyleClass().add("notChecked");
            } else {
                // passwordErrorLabel.setText("");
                passwordTextField.getStyleClass().removeAll("notChecked");
                passwordTextField.getStyleClass().add("checked");
            }
        });
        nomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", nomTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(nomTextField)
                        .immediate();
                Window window = nomTextField.getScene().getWindow();
                Bounds bounds = nomTextField.localToScreen(nomTextField.getBoundsInLocal());
                nomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            nomTextField.setTooltip(tooltip);
                            nomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (nomTextField.getTooltip() != null) {
                                nomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                nomTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        prenomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", prenomTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(prenomTextField)
                        .immediate();
                Window window = prenomTextField.getScene().getWindow();
                Bounds bounds = prenomTextField.localToScreen(prenomTextField.getBoundsInLocal());
                prenomTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            prenomTextField.setTooltip(tooltip);
                            prenomTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (prenomTextField.getTooltip() != null) {
                                prenomTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                prenomTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        adresseTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", adresseTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(adresseTextField)
                        .immediate();
                Window window = adresseTextField.getScene().getWindow();
                Bounds bounds = adresseTextField.localToScreen(adresseTextField.getBoundsInLocal());
                adresseTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            adresseTextField.setTooltip(tooltip);
                            adresseTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (adresseTextField.getTooltip() != null) {
                                adresseTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                adresseTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        emailTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", emailTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null) {
                                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                                if (!userName.matches(emailRegex)) {
                                    c.error("Invalid email format.");
                                }
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(emailTextField)
                        .immediate();
                Window window = emailTextField.getScene().getWindow();
                Bounds bounds = emailTextField.localToScreen(emailTextField.getBoundsInLocal());
                emailTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            emailTextField.setTooltip(tooltip);
                            emailTextField.getTooltip().show(window, bounds.getMinX() - 10, bounds.getMinY() + 30);
                        } else {
                            if (emailTextField.getTooltip() != null) {
                                emailTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                emailTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        passwordTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", passwordTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null && !userName.toLowerCase().equals(userName)) {
                                c.error("Please use only lowercase letters.");
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(passwordTextField)
                        .immediate();
                Window window = passwordTextField.getScene().getWindow();
                Bounds bounds = passwordTextField.localToScreen(passwordTextField.getBoundsInLocal());
                passwordTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            passwordTextField.setTooltip(tooltip);
                            passwordTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (passwordTextField.getTooltip() != null) {
                                passwordTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                passwordTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Validator validator = new Validator();
                validator.createCheck()
                        .dependsOn("firstName", num_telephoneTextField.textProperty())
                        .withMethod(c -> {
                            String userName = c.get("firstName");
                            if (userName != null) {
                                String numberRegex = "\\d*";
                                if (!userName.matches(numberRegex)) {
                                    c.error("Please use only numbers.");
                                }
                            } else if (userName.isEmpty()) {
                                c.error("the string is empty");
                            }
                        })
                        .decorates(num_telephoneTextField)
                        .immediate();
                Window window = num_telephoneTextField.getScene().getWindow();
                Bounds bounds = num_telephoneTextField.localToScreen(num_telephoneTextField.getBoundsInLocal());
                num_telephoneTextField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (validator.containsErrors()) {
                            tooltip.setText(validator.createStringBinding().getValue());
                            tooltip.setStyle("-fx-background-color: #f00;");
                            num_telephoneTextField.setTooltip(tooltip);
                            num_telephoneTextField.getTooltip().show(window, bounds.getMinX() - 10,
                                    bounds.getMinY() + 30);
                        } else {
                            if (num_telephoneTextField.getTooltip() != null) {
                                num_telephoneTextField.getTooltip().hide();
                            }
                        }
                    }
                });
                num_telephoneTextField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode().equals(KeyCode.ENTER)) {
                            if (validator.containsErrors()) {
                                event.consume();
                            }
                        }
                    }
                });
            }
        });
        List<String> roleList = Arrays.asList("client", "responsable de cinema");
        for (String role : roleList) {
            roleComboBox.getItems().add(role);
        }
    }
    /** 
     * @param event
     */
    @FXML
    void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
        fileChooser.setTitle("Sélectionner une image");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationDirectory1 = "./src/main/resources/img/users/";
                String destinationDirectory2 = "C:\\xampp\\htdocs\\Rakcha\\rakcha-web\\public\\img\\users\\";
                Path destinationPath1 = Paths.get(destinationDirectory1);
                Path destinationPath2 = Paths.get(destinationDirectory2);
                String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destinationFilePath1 = destinationPath1.resolve(uniqueFileName);
                Path destinationFilePath2 = destinationPath2.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), destinationFilePath1);
                Files.copy(selectedFile.toPath(), destinationFilePath2);
                Image selectedImage = new Image(destinationFilePath1.toUri().toString());
                photoDeProfilImageView.setImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /** 
     * @param event
     * @throws IOException
     */
    @FXML
    void signup(ActionEvent event) throws IOException {
        String role = roleComboBox.getValue();
        User user = null;
        URI uri = null;
        String nom = nomTextField.getText();
        String prenom = prenomTextField.getText();
        String num_telephone = num_telephoneTextField.getText();
        String password = passwordTextField.getText();
        String email = emailTextField.getText();
        LocalDate dateDeNaissance = dateDeNaissanceDatePicker.getValue();
        if (nom.isEmpty() || prenom.isEmpty() || num_telephone.isEmpty() || password.isEmpty()
                || role.isEmpty() || email.isEmpty() || dateDeNaissance == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all the required fields", ButtonType.CLOSE);
            alert.show();
            return;
        }
        if (!num_telephone.matches("\\d+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid phone number format", ButtonType.CLOSE);
            alert.show();
            return;
        }
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid email format", ButtonType.CLOSE);
            alert.show();
            return;
        }
        if (role.equals("responsable de cinema")) {
            user = new Responsable_de_cinema(nomTextField.getText(), prenomTextField.getText(),
                    Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(),
                    roleComboBox.getValue(), emailTextField.getText(),
                    Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), "");
        } else if (role.equals("client")) {
            user = new Client(nomTextField.getText(), prenomTextField.getText(),
                    Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(),
                    roleComboBox.getValue(), emailTextField.getText(),
                    Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), "");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "the given role is not available", ButtonType.CLOSE);
            alert.show();
            return;
        }
        UserService userService = new UserService();
        userService.create(user);
        Stage stage = (Stage) nomTextField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
        Parent root = loader.load();
        ProfileController profileController = loader.getController();
        profileController.setData(user);
        stage.setScene(new Scene(root));
    }
}
