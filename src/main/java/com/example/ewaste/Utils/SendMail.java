package com.example.ewaste.Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class SendMail {

    public static boolean send(String to, String subject, String text) {
        String from = "khairibouzid95@gmail.com";
        String password = "ohdv puqy vwqg hoou"; // Replace with your real password
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            System.out.println("Sent message successfully....");
            return true; // Email sent successfully
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false; // Failed to send email
        }
    }
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

//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.util.Properties;
//
//public class SendMail implements Runnable{
//    private String msg;
//    private String to;
//    private String subject;
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public void outMail() throws MessagingException {
//        String from = "nasseffadhlaoui@gmail.com"; //sender's email address
//        String host = "localhost";
//
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", 587);
//
//
//
//
//        Session session = Session.getDefaultInstance(properties, new Authenticator() {
//            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("nasseffadhlaoui@gmail.com", "zeig wkkt tgrc vejf");  // have to change some settings in SMTP
//            }
//        });
//
//
//        MimeMessage mimeMessage = new MimeMessage(session);
//        mimeMessage.setFrom(new InternetAddress(from));
//        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//        mimeMessage.setSubject(this.subject);
//        mimeMessage.setText(this.msg);
//        Transport.send(mimeMessage);
//
//        System.out.println("sent");
//    }
//
//    @Override
//    public void run() {
//        if (msg != null) {
//            try {
//                outMail();
//            } catch (MessagingException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            System.out.println("not sent. empty msg!");
//        }
//    }
//}