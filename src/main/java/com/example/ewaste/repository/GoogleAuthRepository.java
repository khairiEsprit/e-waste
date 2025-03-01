package com.example.ewaste.Repository;

import com.example.ewaste.Config.GoogleConfig;
import com.example.ewaste.Entities.GoogleUser;
import com.example.ewaste.Entities.User;
import com.example.ewaste.Utils.DataBase;
import com.example.ewaste.Utils.HttpUtil;
import com.example.ewaste.Utils.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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



    public void createUser(String name, String givenName, String familyName, String picture, String email, String emailVerified, String role) {
        String sql = "INSERT INTO utilisateur (nom, prenom, family_name, photo, email, isEmailVerified, role) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nom=?, prenom=?, family_name=?, photo=?, isEmailVerified=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, givenName);
            stmt.setString(3, familyName);
            stmt.setString(4, picture);
            stmt.setString(5, email);
            stmt.setBoolean(6, Boolean.parseBoolean(emailVerified)); // Convert String back to Boolean
            stmt.setString(7, role);

            stmt.setString(8, name);
            stmt.setString(9, givenName);
            stmt.setString(10, familyName);
            stmt.setString(11, picture);
            stmt.setBoolean(12, Boolean.parseBoolean(emailVerified));

            stmt.executeUpdate();
            System.out.println("User saved/updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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