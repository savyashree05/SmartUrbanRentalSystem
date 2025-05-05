package com.urbana.helmetrental.model;

public class OtpVerificationResponse {
    private boolean success;
    private String message;
    private long userId;
    private String token;

    public OtpVerificationResponse(boolean success, String message, long userId, String token) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
