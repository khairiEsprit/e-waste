package com.example.ewaste.Utils;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;

public class OAuthCallbackServer extends NanoHTTPD {
    private String authCode = null;

    public OAuthCallbackServer() throws IOException {
        super(8081);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started, waiting for OAuth callback...");
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getUri().equals("/callback")) {
            // Extract the authorization code from the URL parameters
            String queryParams = session.getQueryParameterString();
            if (queryParams != null && queryParams.contains("code=")) {
                authCode = queryParams.split("code=")[1].split("&")[0];
                System.out.println("Authorization Code received: " + authCode);

                // Stop the server once the code is received
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // Give some time before stopping
                        stop();
                        System.out.println("Server stopped.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                return newFixedLengthResponse(Response.Status.OK, "text/html",
                        "<h2>Authentication successful! You can close this tab.</h2>");
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
    }

    public String getAuthCode() {
        return authCode;
    }
}
