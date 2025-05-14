package com.example.ewaste.Config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class GoogleConfig {
    private final static Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();

    public static String getClientId() {
        return dotenv.get("GOOGLE_CLIENT_ID");
    }

    public static String getClientSecret() {
        return dotenv.get("GOOGLE_CLIENT_SECRET");
    }

    public static String getRedirectUri() {
        return dotenv.get("GOOGLE_REDIRECT_URI");
    }

    public static String getTokenUri() {
        return dotenv.get("GOOGLE_TOKEN_URI");
    }

    public static String getUserInfoUri() {
        return dotenv.get("GOOGLE_USERINFO_URI");
    }

    public static String getAuthUri() {
        return dotenv.get("GOOGLE_AUTH_URI");
    }

    public static void main(String[] args) {
        System.out.println(getClientId());
        System.out.println(getClientSecret());
        System.out.println(getRedirectUri());
        System.out.println(getTokenUri());
        System.out.println(getUserInfoUri());
        System.out.println(getAuthUri());
    }
}