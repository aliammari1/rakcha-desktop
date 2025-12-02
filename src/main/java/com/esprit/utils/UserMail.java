package com.esprit.utils;

import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for sending professional HTML and plain text emails via Gmail SMTP.
 * Supports both plain text and rich HTML formatted messages with cinematic styling.
 *
 * @author RAKCHA Team
 * @version 2.0.0
 * @since 1.0.0
 */
public enum UserMail {
    ;
    private static final Logger LOGGER = Logger.getLogger(UserMail.class.getName());

    /**
     * Sends a plain text email to the specified recipient.
     *
     * @param recipient     The email address of the recipient
     * @param subject       The subject line of the email
     * @param messageToSend The plain text message content
     * @throws RuntimeException if email configuration is missing or sending fails
     */
    public static void send(final String recipient, final String subject, final String messageToSend) {
        sendInternal(recipient, subject, messageToSend, false);
    }

    /**
     * Sends an HTML formatted email to the specified recipient.
     *
     * @param recipient   The email address of the recipient
     * @param subject     The subject line of the email
     * @param htmlContent The HTML formatted message content
     * @throws RuntimeException if email configuration is missing or sending fails
     */
    public static void sendHtml(final String recipient, final String subject, final String htmlContent) {
        sendInternal(recipient, subject, htmlContent, true);
    }

    /**
     * Legacy method for backward compatibility - sends plain text email.
     *
     * @param recipient     The email address of the recipient
     * @param messageToSend The plain text message content
     * @deprecated Use {@link #send(String, String, String)} instead
     */
    @Deprecated
    public static void send(final String recipient, final String messageToSend) {
        send(recipient, "RAKCHA Notification", messageToSend);
    }

    /**
     * Sends a verification code email using the professional template.
     *
     * @param recipient        The email address of the recipient
     * @param recipientName    The recipient's name
     * @param verificationCode The verification code
     * @param expiryMinutes    The code expiry time in minutes
     */
    public static void sendVerificationCode(final String recipient, final String recipientName,
                                            final String verificationCode, final int expiryMinutes) {
        String htmlContent = EmailTemplates.verificationCodeTemplate(recipientName, verificationCode, expiryMinutes);
        sendHtml(recipient, "RAKCHA - Verification Code", htmlContent);
    }

    /**
     * Sends a welcome email using the professional template.
     *
     * @param recipient     The email address of the recipient
     * @param recipientName The recipient's name
     * @param message       The welcome message
     */
    public static void sendWelcome(final String recipient, final String recipientName, final String message) {
        String htmlContent = EmailTemplates.welcomeTemplate("Welcome to RAKCHA", recipientName, message);
        sendHtml(recipient, "Welcome to RAKCHA", htmlContent);
    }

    /**
     * Sends a notification email using the professional template.
     *
     * @param recipient  The email address of the recipient
     * @param title      The notification title
     * @param content    The notification content
     * @param actionUrl  Optional action URL
     * @param actionText Optional action button text
     */
    public static void sendNotification(final String recipient, final String title, final String content,
                                        final String actionUrl, final String actionText) {
        String htmlContent = EmailTemplates.notificationTemplate(title, title, content, actionUrl, actionText);
        sendHtml(recipient, title, htmlContent);
    }

    /**
     * Sends a password reset email using the professional template.
     *
     * @param recipient     The email address of the recipient
     * @param recipientName The recipient's name
     * @param resetLink     The password reset link
     * @param expiryHours   The link expiry time in hours
     */
    public static void sendPasswordReset(final String recipient, final String recipientName,
                                         final String resetLink, final int expiryHours) {
        String htmlContent = EmailTemplates.passwordResetTemplate(recipientName, resetLink, expiryHours);
        sendHtml(recipient, "RAKCHA - Reset Your Password", htmlContent);
    }

    /**
     * Sends a success/confirmation email using the professional template.
     *
     * @param recipient The email address of the recipient
     * @param title     The success title
     * @param message   The success message
     * @param details   Optional additional details
     */
    public static void sendSuccess(final String recipient, final String title, final String message, final String details) {
        String htmlContent = EmailTemplates.successTemplate(title, message, details);
        sendHtml(recipient, title, htmlContent);
    }

    /**
     * Internal method to handle email sending for both plain text and HTML content.
     *
     * @param recipient The email address of the recipient
     * @param subject   The subject line of the email
     * @param content   The message content (plain text or HTML)
     * @param isHtml    Whether the content is HTML formatted
     */
    private static void sendInternal(final String recipient, final String subject, final String content, final boolean isHtml) {
        // Load environment variables from .env file
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Sender's email ID needs to be mentioned
        final String from = dotenv.get("EMAIL_FROM", "ammari.ali.0001@gmail.com");
        // Gmail requires the full email address as username
        final String username = dotenv.get("EMAIL_USERNAME", "ammari.ali.0001@gmail.com");
        String password = dotenv.get("JAVAMAIL_APP_PASSWORD");

        // Validate password is set
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Email password not configured. Please set the JAVAMAIL_APP_PASSWORD environment variable with your Gmail app password.");
        }

        // Gmail SMTP configuration
        final String host = "smtp.gmail.com";
        final Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.required", "true");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        // Get the Session object
        final Session session = Session.getInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage object
            final Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            // Set Subject: header field
            message.setSubject(subject);

            // Set message content
            if (isHtml) {
                message.setContent(content, "text/html; charset=utf-8");
            } else {
                message.setText(content);
            }

            // Send message
            Transport.send(message);
            UserMail.LOGGER.info("Email sent successfully to: " + recipient);
        } catch (final MessagingException e) {
            UserMail.LOGGER.log(Level.SEVERE, "Failed to send email: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

}
