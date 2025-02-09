package com.example.ewaste.repository;

import com.example.hellofx.DataBase.DbConnection;
import javafx.scene.control.Alert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class AuthRepository {
    private final Connection conn = DbConnection.getConnection();

    private static final int SALT_LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();

//    public static String hashPassword(String password) throws NoSuchAlgorithmException {
//
//        byte[] salt = new byte[SALT_LENGTH];
//        random.nextBytes(salt);
//
//        // Hacher le mot de passe avec le sel
//        String hashedPassword = hashWithSalt(password, salt);
//
//        // Concaténer le sel et le hachage du mot de passe pour le stockage
//        return Base64.getEncoder().encodeToString(salt) + ":" + hashedPassword;
//    }
//
//
//    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
//        // Concatène le sel avec le mot de passe
//        String saltedPassword = Base64.getEncoder().encodeToString(salt) + password;
//
//        // Hash le sel concaténé avec le mot de passe
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        byte[] hashedPassword = md.digest(saltedPassword.getBytes());
//
//        // Retourne le résultat haché
//        return Base64.getEncoder().encodeToString(hashedPassword);
//    }

//    public static boolean verifyPassword(String inputPassword, String storedHash) throws NoSuchAlgorithmException {
//        // Séparation de la chaîne stockée en sel et hachage du mot de passe
//        String[] parts = storedHash.split(":");
//        String storedSalt = parts[0];
//        String storedHashedPassword = parts[1];
//
//        // Décodage du sel depuis Base64
//        byte[] salt = Base64.getDecoder().decode(storedSalt);
//
//        // Calcul du hachage du mot de passe fourni avec le même sel
//        String hashedPassword = hashWithSalt(inputPassword, salt);
//
//        // Comparaison des hachages
//        return storedHashedPassword.equals(hashedPassword);
//    }
    public boolean emailExists(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Si resultSet.next() est vrai, cela signifie qu'un enregistrement avec cet e-mail existe
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public static String hashPassword2(String password) throws NoSuchAlgorithmException {
        // Hasher le mot de passe sans sel
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedPassword = md.digest(password.getBytes());

        // Retourner le hachage encodé en Base64
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashPassword2(password));
            System.out.println("testt from authenticate user function");
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // If a row is found, the user is authenticated
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

//    public boolean validerPassword(int id, String password) {
//        String query = "SELECT * FROM user WHERE password = ? AND id = ?";
//        try (PreparedStatement statement = conn.prepareStatement(query)) {
//            statement.setString(1, hashPassword2(password));
//            statement.setInt(2, id);
//            try (ResultSet resultSet = statement.executeQuery()) {
//                boolean isValid = resultSet.next();
//                if (!isValid) {
//                    // Show an alert if the password is invalid
//                    Alert alert = new Alert(Alert.AlertType.WARNING);
//                    alert.setTitle("Invalid Password");
//                    alert.setHeaderText(null);
//                    alert.setContentText("The password is incorrect.");
//                    alert.showAndWait();
//                }
//                return isValid;
//            }
//        } catch (SQLException ex) {
//            System.err.println(ex.getMessage());
//            // Show an alert for SQL exception
//            showErrorAlert("SQL Error", "An error occurred while validating the password.");
//            return false;
//        } catch (Exception ex) {
//            System.err.println(ex.getMessage());
//            // Show a generic alert for other exceptions
//            showErrorAlert("Error", "An unexpected error occurred.");
//            return false;
//        }
//    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void modifyPassword(int id, String password) {
        String query = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, hashPassword2(password));
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Modification jawha hafalet");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("fama mochkla ");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // Handle SQLException appropriately
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
