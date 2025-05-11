package com.example.ewaste.Config;

import com.example.ewaste.Utils.DotenvConfig;
public class GoogleConfig {
    public static String getClientId() {
        return DotenvConfig.get("GOOGLE_CLIENT_ID", "dummy-google-client-id");
    }

    public static String getClientSecret() {
        return DotenvConfig.get("GOOGLE_CLIENT_SECRET", "dummy-google-client-secret");
    }

    public static String getRedirectUri() {
        return DotenvConfig.get("GOOGLE_REDIRECT_URI", "http://localhost:8080/callback");
    }

    public static String getTokenUri() {
        return DotenvConfig.get("GOOGLE_TOKEN_URI", "https://oauth2.googleapis.com/token");
    }

    public static String getUserInfoUri() {
        return DotenvConfig.get("GOOGLE_USERINFO_URI", "https://www.googleapis.com/oauth2/v3/userinfo");
    }

    public static String getAuthUri() {
        return DotenvConfig.get("GOOGLE_AUTH_URI", "https://accounts.google.com/o/oauth2/auth");
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