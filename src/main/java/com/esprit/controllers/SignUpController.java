package com.esprit.controllers;

import com.esprit.models.Client;
import com.esprit.models.Responsable_de_cinema;
import com.esprit.models.User;
import com.esprit.services.UserService;
import com.esprit.utils.DataSource;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.synedra.validatorfx.Validator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SignUpController {

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
                            } else if (userName.isEmpty())
                                c.error("the string is empty");

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
                            if (validator.containsErrors())
                                event.consume();
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
                            } else if (userName.isEmpty())
                                c.error("the string is empty");

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
                            if (validator.containsErrors())
                                event.consume();
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
                            } else if (userName.isEmpty())
                                c.error("the string is empty");

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
                            if (validator.containsErrors())
                                event.consume();
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
                            if (validator.containsErrors())
                                event.consume();
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
                            } else if (userName.isEmpty())
                                c.error("the string is empty");

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
                            if (validator.containsErrors())
                                event.consume();
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
                            if (validator.containsErrors())
                                event.consume();
                        }
                    }
                });
            }
        });

        List<String> roleList = new ArrayList<>() {
            {
                add("responsable de cinema");
                add("client");
            }
        };
        for (String role : roleList)
            roleComboBox.getItems().add(role);
    }

    @FXML
    void importImage(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                Image image = new Image(file.toURI().toURL().toString());
                photoDeProfilImageView.setImage(image);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void signup(ActionEvent event) throws IOException {
        String role = roleComboBox.getValue();
        User user = null;
        URI uri = null;
        Blob imageBlob = null;
        try {
            byte[] imageBytes = Files.readAllBytes(Path.of(new URI(photoDeProfilImageView.getImage().getUrl())));
            imageBlob = DataSource.getInstance().getConnection().createBlob();
            imageBlob.setBytes(1, imageBytes);
        } catch (SQLException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }

        if (role.equals("responsable de cinema")) {
            user = new Responsable_de_cinema(nomTextField.getText(), prenomTextField.getText(),
                    Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(),
                    roleComboBox.getValue(), emailTextField.getText(),
                    Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), imageBlob);
        } else if (role.equals("client")) {
            user = new Client(nomTextField.getText(), prenomTextField.getText(),
                    Integer.parseInt(num_telephoneTextField.getText()), passwordTextField.getText(),
                    roleComboBox.getValue(), emailTextField.getText(),
                    Date.valueOf(dateDeNaissanceDatePicker.getValue()), emailTextField.getText(), imageBlob);
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
        stage.setUserData(user);
        stage.setScene(new Scene(root));
    }
}
