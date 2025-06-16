package com.esprit.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public enum UserMail {
    ;
    private static final Logger LOGGER = Logger.getLogger(UserMail.class.getName());

    /**
     * @param Recipient
     * @param messageToSend
     */
    public static void send(final String Recipient, final String messageToSend) {
        // Sender's email ID needs to be mentioned
        final String from = "ammari.ali.0001@gmail.com";
        final String username = "ammariali0001";// change accordingly
        String password = System.getenv("JAVAMAIL_APP_PASSWORD");// change accordingly
        // Assuming you are sending email through relay.jangosmtp.net
        final String host = "smtp.gmail.com";
        final Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "25");
        props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        // Get the Session object.
        final Session session = Session.getInstance(props, new Authenticator() {
            /**
             * Retrieves the PasswordAuthentication value.
             *
             * @return the PasswordAuthentication value
             */
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            // Create a default MimeMessage object.
            final Message message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Recipient));
            // Set Subject: header field
            message.setSubject("Testing Subject");
            // Now set the actual message
            message.setText(messageToSend);
            // Send message
            Transport.send(message);
            UserMail.LOGGER.info("Sent message successfully....");
        } catch (final MessagingException e) {
            UserMail.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
