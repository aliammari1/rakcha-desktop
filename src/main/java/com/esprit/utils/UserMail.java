package com.esprit.utils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class UserMail {
    /** 
     * @param Recipient
     * @param messageToSend
     */
    public static void send(String Recipient, String messageToSend) {
        // Sender's email ID needs to be mentioned
        String from = "ammari.ali.0001@gmail.com";
        final String username = "ammariali0001";// change accordingly
        final String password = System.getenv("JAVAMAIL_APP_PASSWORD");// change accordingly
        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(Recipient));
            // Set Subject: header field
            message.setSubject("Testing Subject");
            // Now set the actual message
            message.setText(messageToSend);
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
