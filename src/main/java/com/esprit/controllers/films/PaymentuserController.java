package com.esprit.controllers.films;


import com.esprit.models.cinemas.Seance;
import com.esprit.models.films.Ticket;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.SeanceService;
import com.esprit.services.films.TicketService;
import com.esprit.services.users.UserService;
import com.esprit.utils.Paymentuser;
import com.stripe.exception.StripeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentuserController implements Initializable {

    @FXML
    private TextField anneeExp;

    @FXML
    private Button filmm;

    @FXML
    private TextField carte;

    @FXML
    private TextField cvc;

    @FXML
    private TextField moisExp;

    @FXML
    private Button pay;

    private Seance seance;
    @FXML
    private Button viewPDF;

    public static boolean isNum(String str) {
        String expression = "\\d+";
        return str.matches(expression);
    }

    public static int floatToInt(float value) {
        return (int) value;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            seance = new SeanceService().read().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void Pay(ActionEvent event) throws StripeException {
        TicketService scom = new TicketService();
        // TODO replace the next line with reservation
        //Seance seance;
        UserService sc = new UserService();
        Client client = (Client) sc.getUserById(2);
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
            webView.getEngine().load(url);

            // create scene
            //   stage.getIcons().add(new Image("/Images/logo.png"));
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

    private boolean isValidInput() {
        if (!isValidVisaCardNo(carte.getText())) {
            showError("Numéro de carte invalide", "Veuillez entrer un numéro de carte Visa valide.");
            return false;
        }

        if (moisExp.getText().isEmpty() || !isNum(moisExp.getText()) || Integer.parseInt(moisExp.getText()) < 1 || Integer.parseInt(moisExp.getText()) > 12) {
            showError("Mois d'expiration invalide", "Veuillez entrer un mois d'expiration valide (entre 1 et 12).");
            return false;
        }

        if (anneeExp.getText().isEmpty() || !isNum(anneeExp.getText()) || Integer.parseInt(anneeExp.getText()) < LocalDate.now().getYear()) {
            showError("Année d'expiration invalide", "Veuillez entrer une année d'expiration valide.");
            return false;
        }

        if (cvc.getText().isEmpty() || !isNum(cvc.getText())) {
            showError("Code CVC invalide", "Veuillez entrer un code CVC numérique valide.");
            return false;
        }

        return true;
    }

    private boolean isValidVisaCardNo(String text) {
        String regex = "^4[0-9]{12}(?:[0-9]{3})?$";
        Pattern p = Pattern.compile(regex);
        CharSequence cs = text;
        Matcher m = p.matcher(cs);
        return m.matches();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void init(Seance p) {
        this.seance = p;
        this.pay.setText("Payer " + p.getPrix() + "dinars");
    }

    public void switchtfillmmaa(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/filmuser.fxml"));
            AnchorPane root = fxmlLoader.load();
            Stage stage = (Stage) filmm.getScene().getWindow();
            Scene scene = new Scene(root, 1507, 855);
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


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

    public void openPDF(String filename) throws IOException {
        if (Desktop.isDesktopSupported()) {
            File myFile = new File(filename);
            Desktop.getDesktop().open(myFile);
        }
    }


}