package com.example.ewaste.utils;

public class Validator {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{8}$";  // Matches exactly 8 digits
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }
}
