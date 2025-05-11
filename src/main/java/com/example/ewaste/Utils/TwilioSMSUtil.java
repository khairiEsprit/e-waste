package com.example.ewaste.Utils;

// No need to import DotenvConfig since it's in the same package

// Stub implementation of TwilioSMSUtil without actual Twilio dependencies
public class TwilioSMSUtil {
    // Twilio credentials - loaded from DotenvConfig
    static String ACCOUNT_SID = DotenvConfig.get("TWILIO_ACCOUNT_SID", "dummy-twilio-sid");
    static String AUTH_TOKEN = DotenvConfig.get("Twilio_AUTH_TOKEN", "dummy-twilio-token");

    private static final String FROM_NUMBER = "+15157094542";
    private static boolean initialized = false;

    /**
     * Initialize the Twilio client with credentials (stub implementation)
     */
    public static void init() {
        if (!initialized) {
            System.out.println("Twilio initialized with SID: " + ACCOUNT_SID);
            initialized = true;
        }
    }

    /**
     * Send an SMS message using Twilio (stub implementation)
     *
     * @param toNumber The recipient's phone number (in E.164 format)
     * @param messageBody The content of the SMS message
     * @return The message SID if successful, null if failed
     */
    public static String sendSMS(String toNumber, String messageBody) {
        if (!initialized) {
            init();
        }

        try {
            System.out.println("STUB SMS: To: " + toNumber + ", From: " + FROM_NUMBER + ", Message: " + messageBody);
            // Generate a fake message SID
            return "SM" + System.currentTimeMillis();
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            return null;
        }
    }

    /**
     * Send transport reservation confirmation SMS (stub implementation)
     */
    public static String sendTransportReservationConfirmation(String phoneNumber,
                                                              String transportName, String departurePoint, String departureDate, String price) {

        String message = String.format(
                "Confirmation: Votre réservation pour %s de %s le %s a été confirmée. Prix: %s",
                transportName, departurePoint, departureDate, price
        );

        return sendSMS(phoneNumber, message);
    }

    /**
     * Send transport reservation update notification (stub implementation)
     */
    public static String sendTransportUpdateNotification(String phoneNumber, String transportName) {
        String message = String.format(
                "Votre réservation pour %s a été modifiée. Veuillez consulter l'application pour les détails.",
                transportName
        );

        return sendSMS(phoneNumber, message);
    }
}