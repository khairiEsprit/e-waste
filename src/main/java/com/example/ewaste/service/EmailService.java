package com.example.ewaste.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final String password;
    private final Properties properties;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
        this.properties =setupMailProperties();
    }

    private Properties setupMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");  // Use your SMTP server
        props.put("mail.smtp.port", "587");  // Port for TLS
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return props;
    }
    public boolean sendEmail(String recipient, String subject, String body) {
        try {
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
