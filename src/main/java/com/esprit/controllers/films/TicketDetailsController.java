package com.esprit.controllers.films;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.films.Ticket;
import com.esprit.services.films.TicketService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for displaying detailed ticket information with QR code.
 * Supports ticket printing, downloading, and cancellation.
 */
public class TicketDetailsController {

    private static final Logger LOGGER = Logger.getLogger(TicketDetailsController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int QR_CODE_SIZE = 200;
    private final TicketService ticketService;
    @FXML
    private VBox ticketContainer;
    @FXML
    private ImageView qrCodeImage;
    @FXML
    private ImageView moviePosterImage;
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label cinemaNameLabel;
    @FXML
    private Label cinemaAddressLabel;
    @FXML
    private Label hallNameLabel;
    @FXML
    private Label screenTypeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label countdownLabel;
    @FXML
    private Label seatRowLabel;
    @FXML
    private Label seatNumberLabel;
    @FXML
    private Label seatTypeLabel;
    @FXML
    private Label ticketIdLabel;
    @FXML
    private Label bookingDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label basePriceLabel;
    @FXML
    private Label premiumFeeLabel;
    @FXML
    private Label serviceFeeLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button printButton;
    @FXML
    private Button shareButton;
    @FXML
    private VBox cancellationPolicyBox;
    @FXML
    private Label refundAmountLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    private Ticket currentTicket;
    private Image qrCodeImageData;

    public TicketDetailsController() {
        this.ticketService = new TicketService();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing TicketDetailsController");
        startCountdownTimer();
    }

    /**
     * Sets the ticket to display and loads its details.
     *
     * @param ticket The ticket to display
     */
    public void setTicket(Ticket ticket) {
        this.currentTicket = ticket;
        loadTicketDetails();
    }

    /**
     * Loads ticket by ID (for navigation from other screens).
     *
     * @param ticketId The ticket ID to load
     */
    public void loadTicketById(int ticketId) {
        showLoading(true);

        new Thread(() -> {
            try {
                Ticket ticket = ticketService.getTicketById((long) ticketId);

                Platform.runLater(() -> {
                    if (ticket != null) {
                        this.currentTicket = ticket;
                        loadTicketDetails();
                    } else {
                        showError("Ticket not found.");
                        handleBack();
                    }
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading ticket", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load ticket details.");
                });
            }
        }).start();
    }

    private void loadTicketDetails() {
        if (currentTicket == null) {
            LOGGER.warning("No ticket to display");
            return;
        }

        MovieSession session = currentTicket.getMovieSession();
        Seat seat = currentTicket.getSeat();

        // Generate QR Code
        generateQRCode();

        // Movie Information
        if (session != null && session.getFilm() != null) {
            movieTitleLabel.setText(session.getFilm().getNom());
            genreLabel.setText(session.getFilm().getCategories() != null && !session.getFilm().getCategories().isEmpty() ?
                session.getFilm().getGenre() : "N/A");
            durationLabel.setText(session.getFilm().getDuree() + " min");
            // Load movie poster if available
            loadMoviePoster(session.getFilm().getImage());
        } else {
            movieTitleLabel.setText("Unknown Movie");
            genreLabel.setText("N/A");
            durationLabel.setText("N/A");
        }

        // Cinema Information
        if (session != null && session.getCinemaHall() != null) {
            if (session.getCinemaHall().getCinema() != null) {
                cinemaNameLabel.setText(session.getCinemaHall().getCinema().getNom());
                cinemaAddressLabel.setText(session.getCinemaHall().getCinema().getAdresse());
            }
            hallNameLabel.setText(session.getCinemaHall().getHallName());
            screenTypeLabel.setText(session.getCinemaHall().getScreenType());
        } else {
            cinemaNameLabel.setText("Unknown Cinema");
            cinemaAddressLabel.setText("");
            hallNameLabel.setText("N/A");
            screenTypeLabel.setText("N/A");
        }

        // Session Time
        if (session != null && session.getStartTime() != null) {
            dateLabel.setText(session.getStartTime().format(DATE_FORMAT));
            timeLabel.setText(session.getStartTime().format(TIME_FORMAT));
            updateCountdown(session.getStartTime());
        } else {
            dateLabel.setText("N/A");
            timeLabel.setText("N/A");
            countdownLabel.setText("N/A");
        }

        // Seat Information
        if (seat != null) {
            seatRowLabel.setText(seat.getRowLabel());
            seatNumberLabel.setText(String.valueOf(seat.getSeatNumber()));
            seatTypeLabel.setText(seat.getSeatType() != null ? seat.getSeatType() : "Standard");
        } else {
            seatRowLabel.setText("N/A");
            seatNumberLabel.setText("N/A");
            seatTypeLabel.setText("N/A");
        }

        // Booking Information
        ticketIdLabel.setText("#" + currentTicket.getId());
        bookingDateLabel.setText(currentTicket.getCreatedAt() != null ?
            currentTicket.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A");

        // Status
        statusLabel.setText(currentTicket.getStatus().toString());
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("ticket-status-badge",
            "status-" + currentTicket.getStatus().toString().toLowerCase());

        // Pricing
        double basePrice = currentTicket.getPrice() * 0.85; // Estimate base price
        double premiumFee = seat != null && "Premium".equalsIgnoreCase(seat.getSeatType()) ?
            currentTicket.getPrice() * 0.10 : 0;
        double serviceFee = currentTicket.getPrice() * 0.05;

        basePriceLabel.setText(String.format("$%.2f", basePrice));
        premiumFeeLabel.setText(String.format("$%.2f", premiumFee));
        serviceFeeLabel.setText(String.format("$%.2f", serviceFee));
        totalPriceLabel.setText(String.format("$%.2f", currentTicket.getPrice()));

        // Update button states based on ticket status
        updateButtonStates();

        // Calculate refund policy
        calculateRefundPolicy();
    }

    private void generateQRCode() {
        try {
            String qrContent = String.format("TICKET:%d|SESSION:%d|SEAT:%s|USER:%d",
                currentTicket.getId(),
                currentTicket.getMovieSession() != null ? currentTicket.getMovieSession().getId() : 0,
                currentTicket.getSeat() != null ? currentTicket.getSeat().getSeatNumber() : "N/A",
                currentTicket.getUser() != null ? currentTicket.getUser().getId() : 0
            );

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            qrCodeImageData = SwingFXUtils.toFXImage(bufferedImage, null);
            qrCodeImage.setImage(qrCodeImageData);

            LOGGER.info("QR Code generated successfully");
        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Error generating QR code", e);
        }
    }

    private void loadMoviePoster(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl, true);
                moviePosterImage.setImage(image);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load movie poster", e);
            }
        }
    }

    private void startCountdownTimer() {
        Thread countdownThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(60000); // Update every minute
                    if (currentTicket != null && currentTicket.getMovieSession() != null) {
                        LocalDateTime sessionTime = currentTicket.getMovieSession().getStartTime();
                        Platform.runLater(() -> updateCountdown(sessionTime));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        countdownThread.setDaemon(true);
        countdownThread.start();
    }

    private void updateCountdown(LocalDateTime sessionTime) {
        if (sessionTime == null) {
            countdownLabel.setText("N/A");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (sessionTime.isBefore(now)) {
            countdownLabel.setText("Session has ended");
            countdownLabel.getStyleClass().add("countdown-ended");
        } else {
            long days = ChronoUnit.DAYS.between(now, sessionTime);
            long hours = ChronoUnit.HOURS.between(now, sessionTime) % 24;
            long minutes = ChronoUnit.MINUTES.between(now, sessionTime) % 60;

            if (days > 0) {
                countdownLabel.setText(String.format("%dd %dh %dm", days, hours, minutes));
            } else if (hours > 0) {
                countdownLabel.setText(String.format("%dh %dm", hours, minutes));
            } else {
                countdownLabel.setText(String.format("%d minutes", minutes));
                countdownLabel.getStyleClass().add("countdown-soon");
            }
        }
    }

    private void updateButtonStates() {
        String status = currentTicket.getStatus().toString().toLowerCase();
        boolean isPast = currentTicket.getMovieSession() != null &&
            currentTicket.getMovieSession().getStartTime() != null &&
            currentTicket.getMovieSession().getStartTime().isBefore(LocalDateTime.now());

        // Cancel button - only available for confirmed/pending tickets that haven't started
        cancelButton.setDisable(isPast ||
            "cancelled".equals(status) ||
            "used".equals(status) ||
            "expired".equals(status));

        // Download and print always available
        downloadButton.setDisable(false);
        printButton.setDisable(false);
    }

    private void calculateRefundPolicy() {
        if (currentTicket.getMovieSession() == null ||
            currentTicket.getMovieSession().getStartTime() == null) {
            refundAmountLabel.setText("N/A");
            return;
        }

        LocalDateTime sessionTime = currentTicket.getMovieSession().getStartTime();
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilSession = ChronoUnit.HOURS.between(now, sessionTime);

        double refundPercentage;
        String policyText;

        if (hoursUntilSession >= 48) {
            refundPercentage = 1.0;
            policyText = "Full refund available (48+ hours before)";
        } else if (hoursUntilSession >= 24) {
            refundPercentage = 0.75;
            policyText = "75% refund (24-48 hours before)";
        } else if (hoursUntilSession >= 6) {
            refundPercentage = 0.50;
            policyText = "50% refund (6-24 hours before)";
        } else if (hoursUntilSession > 0) {
            refundPercentage = 0.25;
            policyText = "25% refund (less than 6 hours)";
        } else {
            refundPercentage = 0;
            policyText = "No refund available (session started)";
        }

        double refundAmount = currentTicket.getPrice() * refundPercentage;
        refundAmountLabel.setText(String.format("$%.2f (%s)", refundAmount, policyText));
    }

    @FXML
    private void handleDownloadTicket() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Ticket");
        fileChooser.setInitialFileName("ticket_" + currentTicket.getId() + ".png");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG Image", "*.png")
        );

        File file = fileChooser.showSaveDialog(ticketContainer.getScene().getWindow());

        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(qrCodeImageData, null);
                ImageIO.write(bufferedImage, "png", file);
                showSuccess("Ticket QR code saved successfully!");
                LOGGER.info("Ticket downloaded to: " + file.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error saving ticket", e);
                showError("Failed to save ticket. Please try again.");
            }
        }
    }

    @FXML
    private void handlePrintTicket() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(ticketContainer.getScene().getWindow())) {
            boolean success = printerJob.printPage(ticketContainer);

            if (success) {
                printerJob.endJob();
                showSuccess("Ticket sent to printer!");
                LOGGER.info("Ticket printed successfully");
            } else {
                showError("Failed to print ticket.");
            }
        }
    }

    @FXML
    private void handleShareTicket() {
        // Create shareable text
        String shareText = String.format(
            "🎬 Movie Ticket\n" +
                "Movie: %s\n" +
                "Cinema: %s\n" +
                "Date: %s at %s\n" +
                "Seat: Row %s, Seat %s\n" +
                "Ticket #%d",
            movieTitleLabel.getText(),
            cinemaNameLabel.getText(),
            dateLabel.getText(),
            timeLabel.getText(),
            seatRowLabel.getText(),
            seatNumberLabel.getText(),
            currentTicket.getId()
        );

        // Copy to clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(shareText);
        clipboard.setContent(content);

        showSuccess("Ticket details copied to clipboard!");
    }

    @FXML
    private void handleCancelTicket() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Ticket");
        confirmDialog.setHeaderText("Are you sure you want to cancel this ticket?");
        confirmDialog.setContentText("Refund amount: " + refundAmountLabel.getText());

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);

            new Thread(() -> {
                try {
                    boolean success = ticketService.cancelTicket(currentTicket.getId(), "User Requested Cancellation");

                    Platform.runLater(() -> {
                        showLoading(false);

                        if (success) {
                            currentTicket.setStatus(com.esprit.enums.TicketStatus.CANCELLED);
                            loadTicketDetails();
                            showSuccess("Ticket cancelled successfully. Refund will be processed.");
                        } else {
                            showError("Failed to cancel ticket. Please try again.");
                        }
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error cancelling ticket", e);
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("An error occurred while cancelling the ticket.");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleAddToCalendar() {
        if (currentTicket.getMovieSession() == null ||
            currentTicket.getMovieSession().getStartTime() == null) {
            showError("Cannot add to calendar - session time not available.");
            return;
        }

        // Create ICS file content
        LocalDateTime sessionTime = currentTicket.getMovieSession().getStartTime();
        int duration = currentTicket.getMovieSession().getFilm() != null ?
            currentTicket.getMovieSession().getFilm().getDuree() : 120;

        String icsContent = String.format(
            "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:%s\n" +
                "DTEND:%s\n" +
                "SUMMARY:%s\n" +
                "LOCATION:%s\n" +
                "DESCRIPTION:Seat: Row %s, Seat %s\\nTicket #%d\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR",
            sessionTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")),
            sessionTime.plusMinutes(duration).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")),
            movieTitleLabel.getText(),
            cinemaNameLabel.getText(),
            seatRowLabel.getText(),
            seatNumberLabel.getText(),
            currentTicket.getId()
        );

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Calendar Event");
        fileChooser.setInitialFileName("movie_" + currentTicket.getId() + ".ics");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Calendar File", "*.ics")
        );

        File file = fileChooser.showSaveDialog(ticketContainer.getScene().getWindow());

        if (file != null) {
            try {
                java.nio.file.Files.writeString(file.toPath(), icsContent);
                showSuccess("Calendar event saved! Double-click to add to your calendar.");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error saving calendar event", e);
                showError("Failed to save calendar event.");
            }
        }
    }

    @FXML
    private void handleGetDirections() {
        if (currentTicket.getMovieSession() != null &&
            currentTicket.getMovieSession().getCinemaHall() != null &&
            currentTicket.getMovieSession().getCinemaHall().getCinema() != null) {

            String address = currentTicket.getMovieSession().getCinemaHall().getCinema().getAdresse();
            if (address != null && !address.isEmpty()) {
                try {
                    String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
                    java.awt.Desktop.getDesktop().browse(
                        new java.net.URI("https://www.google.com/maps/search/?api=1&query=" + encodedAddress)
                    );
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error opening maps", e);
                    showError("Could not open maps.");
                }
            } else {
                showError("Cinema address not available.");
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/views/tickets/TicketHistory.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ticketContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/tickets.css").toExternalForm());
            stage.setScene(scene);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addToWallet(ActionEvent actionEvent) {

    }

    public void downloadAsPDF(ActionEvent actionEvent) {

    }

    public void shareTicket(ActionEvent actionEvent) {

    }

    public void transferTicket(ActionEvent actionEvent) {

    }

    public void requestRefund(ActionEvent actionEvent) {

    }

    public void goBack(ActionEvent actionEvent) {

    }
}
