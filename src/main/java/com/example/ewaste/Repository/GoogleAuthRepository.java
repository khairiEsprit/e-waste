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
        String sql = "INSERT INTO user (first_name, last_name, email, profile_image, active, roles, " +
                "created_at, has_seen_guide, is_face_recognition_enabled, freeze) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE first_name=?, last_name=?, profile_image=?, active=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String givenName = userInfo.get("given_name").toString();
            String familyName = userInfo.get("family_name") != null ? userInfo.get("family_name").toString() : "";
            String email = userInfo.get("email").toString();
            String picture = userInfo.get("picture") != null ? userInfo.get("picture").toString() : null;

            // Current timestamp for created_at
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Set values for INSERT
            stmt.setString(1, givenName);
            stmt.setString(2, familyName);
            stmt.setString(3, email);
            stmt.setString(4, picture);
            stmt.setBoolean(5, true); // active
            stmt.setString(6, "ROLE_CITOYEN"); // roles
            stmt.setTimestamp(7, now); // created_at
            stmt.setBoolean(8, false); // has_seen_guide
            stmt.setBoolean(9, false); // is_face_recognition_enabled
            stmt.setBoolean(10, false); // freeze

            // Set values for ON DUPLICATE KEY UPDATE
            stmt.setString(11, givenName);
            stmt.setString(12, familyName);
            stmt.setString(13, picture);
            stmt.setBoolean(14, true); // active

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        User user = new User();
                        user.setId(id);
                        user.setFirst_name(givenName);
                        user.setLast_name(familyName);
                        user.setEmail(email);
                        user.setProfile_image(picture);
                        user.setActive(true);
                        user.setFreeze(false);
                        user.setRoles("ROLE_CITOYEN");
                        user.setCreated_at(now);
                        user.setHas_seen_guide(false);
                        user.setIs_face_recognition_enabled(false);
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
        // Delegate to UserRepository to avoid code duplication
        return new UserRepository().getUserById(id);
    }

    // Method to map Google user info to GoogleUser object
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
                picture,
                email,
                (Boolean) userInfo.get("email_verified")
        );
    }


}