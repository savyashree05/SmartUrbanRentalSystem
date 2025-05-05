package com.urbana.helmetrental.model;

public class DepositResponse {
    private boolean success;
    private String message;
    private double walletBalance;
    private String transactionId;

    public DepositResponse(boolean success, String message, double walletBalance, String transactionId) {
        this.success = success;
        this.message = message;
        this.walletBalance = walletBalance;
        this.transactionId = transactionId;
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

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
