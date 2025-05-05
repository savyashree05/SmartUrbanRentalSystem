package com.urbana.helmetrental.model;

public class LicenseVerificationRequest {
    private String licenseNumber;
    private String userId;

    // Constructor
    public LicenseVerificationRequest(String licenseNumber, String userId) {
        this.licenseNumber = licenseNumber;
        this.userId = userId;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
