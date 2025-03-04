package com.example.ewaste.Repository;

import com.example.ewaste.Config.GoogleConfig;
import com.example.ewaste.Entities.GoogleUser;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Entities.UserRole;
import com.example.ewaste.Utils.DataBase;
import com.example.ewaste.Utils.HttpUtil;
import com.example.ewaste.Utils.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

public class GoogleAuthRepository {

    private final Connection conn = DataBase.getInstance().getConnection();

    public String buildAuthUrl() {
        return GoogleConfig.getAuthUri() + "?" +
                "client_id=" + GoogleConfig.getClientId() +
                "&redirect_uri=" + GoogleConfig.getRedirectUri() +
                "&response_type=code" +
                "&scope=openid%20profile%20email" +
                "&prompt=select_account";
    }

    public String getAccessToken(String code) throws IOException {
        String requestBody = "code=" + code +
                "&client_id=" + GoogleConfig.getClientId() +
                "&client_secret=" + GoogleConfig.getClientSecret() +
                "&redirect_uri=" + GoogleConfig.getRedirectUri() +
                "&grant_type=authorization_code";

        String response = HttpUtil.post(GoogleConfig.getTokenUri(), requestBody);

        // Add error handling
        Map<String, Object> responseMap = JsonUtil.parseJson(response);
        if (responseMap.containsKey("error")) {
            throw new IOException("Token error: " + responseMap.get("error_description"));
        }

        return (String) responseMap.get("access_token");
    }

    public Map<String, Object> getUserInfo(String accessToken) throws IOException {
        URL url = new URL(GoogleConfig.getUserInfoUri());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken); // Add Bearer token

        if (conn.getResponseCode() != 200) {
            throw new IOException("Failed to get user info: " + conn.getResponseMessage());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return JsonUtil.parseJson(br.lines().collect(Collectors.joining()));
        }
    }



    public User createUserFromGoogle(Map<String, Object> userInfo) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, photo, isEmailVerified, role) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nom=?, prenom=?, photo=?, isEmailVerified=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String name = userInfo.get("name").toString();
            String givenName = userInfo.get("given_name").toString();
            String email = userInfo.get("email").toString();
            String picture = userInfo.get("picture") != null ? userInfo.get("picture").toString() : null;
            boolean emailVerified = Boolean.parseBoolean(userInfo.get("email_verified").toString());

            stmt.setString(1, name);
            stmt.setString(2, givenName);
            stmt.setString(3, email);
            stmt.setString(4, picture);
            stmt.setBoolean(5, emailVerified);
            stmt.setString(6, "CITOYEN");

            stmt.setString(7, name);
            stmt.setString(8, givenName);
            stmt.setString(9, picture);
            stmt.setBoolean(10, emailVerified);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        User user = new User();
                        user.setId(id);
                        user.setNom(name);
                        user.setPrenom(givenName);
                        user.setEmail(email);
                        user.setPhotoUrl(picture);
                        user.setRole(UserRole.CITOYEN);
                        // Other fields like status, createdAt, etc., could be set by DB defaults
                        return user;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user from Google data", e);
        }
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRole.valueOf(resultSet.getString("role")));
                    user.setTelephone(resultSet.getInt("telephone"));
                    user.setDateNss(resultSet.getDate("DateNss"));
                    user.setPhotoUrl(resultSet.getString("photo"));
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    // Method to map Google user info to User object
    public GoogleUser mapToGoogleUser(Map<String, Object> userInfo) {
        System.out.println("Raw User Info: " + userInfo); // Print full response

        String email = (String) userInfo.get("email");
        String picture = (String) userInfo.get("picture");

        System.out.println("Extracted Email: " + email);
        System.out.println("Extracted Picture: " + picture);

        return new GoogleUser(
                (String) userInfo.get("sub"),
                (String) userInfo.get("name"),
                (String) userInfo.get("given_name"),
                (String) userInfo.get("family_name"),
                email,  // Ensure correct mapping
                picture,  // Ensure correct mapping
                (Boolean) userInfo.get("email_verified")
        );
    }


}