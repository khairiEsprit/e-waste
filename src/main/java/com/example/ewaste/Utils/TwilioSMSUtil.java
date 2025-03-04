package com.example.ewaste.Utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMSUtil {
    // Twilio credentials - should be stored in configuration file in production
    Dotenv dotenv = Dotenv.configure()
            .directory("C:/Users/User/Documents/e-waste/e-waste") // Adjust the path accordingly
            .filename(".env")
            .load();
    String  ACCOUNT_SID= dotenv.get("TWILIO_ACCOUNT_SID");
    String  AUTH_TOKEN= dotenv.get("Twilio_AUTH_TOKEN");


    private static final String FROM_NUMBER = "+15157094542";

    private static boolean initialized = false;

    /**
     * Initialize the Twilio client with credentials
     */
    public static void init() {
        if (!initialized) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            initialized = true;
        }
    }

    /**
     * Send an SMS message using Twilio
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
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(FROM_NUMBER),
                    messageBody
            ).create();

            return message.getSid();
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            return null;
        }
    }

    /**
     * Send transport reservation confirmation SMS
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
     * Send transport reservation update notification
     */
    public static String sendTransportUpdateNotification(String phoneNumber, String transportName) {
        String message = String.format(
                "Votre réservation pour %s a été modifiée. Veuillez consulter l'application pour les détails.",
                transportName
        );

        return sendSMS(phoneNumber, message);
    }
}