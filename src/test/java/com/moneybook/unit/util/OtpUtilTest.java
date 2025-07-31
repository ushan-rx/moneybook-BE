package com.moneybook.unit.util;

import com.moneybook.util.OtpUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OtpUtilTest {

    @Test
    void generateOtp_ShouldGenerateOtpOfCorrectLength() {
        String otp = OtpUtil.generateOtp();
        assertEquals(6, otp.length());
    }

    @Test
    void generateOtp_ShouldGenerateNumericOtp() {
        String otp = OtpUtil.generateOtp();
        assertTrue(otp.matches("\\d+"), "OTP should contain only digits");
    }

    @Test
    void hashOtp_ShouldGenerateDifferentHashesForSameOtp() {
        String otp = "123456";
        String hash1 = OtpUtil.hashOtp(otp);
        String hash2 = OtpUtil.hashOtp(otp);
        assertNotEquals(hash1, hash2, "Different hashes should be generated for the same OTP");
    }

    @Test
    void verifyOtp_ShouldReturnTrueForCorrectOtp() {
        String otp = "123456";
        String hashedOtp = OtpUtil.hashOtp(otp);
        assertTrue(OtpUtil.verifyOtp(otp, hashedOtp));
    }

    @Test
    void verifyOtp_ShouldReturnFalseForIncorrectOtp() {
        String correctOtp = "123456";
        String incorrectOtp = "654321";
        String hashedOtp = OtpUtil.hashOtp(correctOtp);
        assertFalse(OtpUtil.verifyOtp(incorrectOtp, hashedOtp));
    }
}
