package com.urbana.helmetrental.model;

// VerifyOtpRequest.java
public class VerifyOtpRequest {
    private String phoneNumber;
    private String otp;
    private String name;

    public VerifyOtpRequest(String phoneNumber, String otp, String name) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.name = name;
    }

    // Getters & setters (optional)
}
