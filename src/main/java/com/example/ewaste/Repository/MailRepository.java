package com.example.ewaste.Repository;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.util.Properties;

public class MailRepository {

    public static void sendEmailWithoutAttachment(String recipientEmail, String subject, String messageContent) {
        final String username = "nasseffadhlaoui@gmail.com"; // Votre adresse e-mail
        final String password = "xnye onep frrg swzp\n"; // Votre mot de passe d'application

        // Configuration du serveur SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Création de la session
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Création du corps du message
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(messageContent);

            // Création du multipart (sans pièce jointe)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Ajout du contenu au message
            message.setContent(multipart);

            // Envoi de l'e-mail
            Transport.send(message);
            System.out.println("✅ E-mail envoyé avec succès à " + recipientEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'envoi de l'e-mail.");
        }
    }

    public static void sendEmail(String recipientEmail, String subject, String messageContent, String attachmentPath) {
        final String username = "nasseffadhlaoui@gmail.com"; // Votre adresse e-mail
        final String password = "xnye onep frrg swzp\n"; // Votre mot de passe d'application

        // Configuration du serveur SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        // Création de la session
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Création du corps du message
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(messageContent);

            // Création de la pièce jointe
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            File file = new File(attachmentPath);
            attachmentBodyPart.attachFile(file);

            // Combinaison du corps du message et de la pièce jointe
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            // Ajout du contenu au message
            message.setContent(multipart);

            // Envoi de l'e-mail
            Transport.send(message);
            System.out.println("✅ E-mail envoyé avec succès à " + recipientEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'envoi de l'e-mail.");
        }
    }
}