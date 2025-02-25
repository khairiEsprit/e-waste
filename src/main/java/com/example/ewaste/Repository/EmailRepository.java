package com.example.ewaste.Repository;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailRepository {

    public static void sendEmail(String recipientEmail, String subject, String messageContent) {
        final String username = "nasseffadhlaoui@gmail.com";
        final String password = "xnye onep frrg swzp";

        // Setup mail server properties
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Authenticate and create session
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Prepare the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject(subject);
            message.setText(messageContent);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
