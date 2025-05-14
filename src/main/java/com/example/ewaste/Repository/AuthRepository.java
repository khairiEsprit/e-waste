package com.example.ewaste.Repository;

import com.example.ewaste.Utils.DataBase;
import javafx.scene.control.Alert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;

public class AuthRepository {
    private final Connection conn = DataBase.getConnection();






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
                return resultSet.next(); // If resultSet.next() is true, it means a record with this email exists
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
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashPassword2(password));
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

    public boolean validerPassword(int id, String password) {
        String query = "SELECT * FROM user WHERE password = ? AND id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, hashPassword2(password));
            statement.setInt(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                boolean isValid = resultSet.next();
                if (!isValid) {
                    // Show an alert if the password is invalid
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Password");
                    alert.setHeaderText(null);
                    alert.setContentText("The password is incorrect.");
                    alert.showAndWait();
                }
                return isValid;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            // Show an alert for SQL exception
            showErrorAlert("SQL Error", "An error occurred while validating the password.");
            return false;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            // Show a generic alert for other exceptions
            showErrorAlert("Error", "An unexpected error occurred.");
            return false;
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void modifyPassword(int id, String password) {
        String query = "UPDATE user SET password = ?, password_requested_at = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, hashPassword2(password));
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(3, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password updated successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error updating password");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // Handle SQLException appropriately
            showErrorAlert("SQL Error", "An error occurred while updating the password.");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
            showErrorAlert("Error", "An error occurred while hashing the password.");
            throw new RuntimeException(e);
        }
    }

}
