package com.moneybook.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;

public class OtpUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10)); // Generate a digit between 0-9
        }
        return otp.toString();
    }

    public static String hashOtp(String otp) {
        return BCrypt.hashpw(otp, BCrypt.gensalt());
    }

    public static boolean verifyOtp(String rawOtp, String hashedOtp) {
        return BCrypt.checkpw(rawOtp, hashedOtp);
    }
}
