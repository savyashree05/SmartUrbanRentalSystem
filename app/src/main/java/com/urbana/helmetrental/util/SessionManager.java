package com.urbana.helmetrental.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "HelmetRentalPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOCATION_ID = "locationId";
    private static final String KEY_LOCATION_NAME = "locationName";
    private static final String KEY_LICENSE_VERIFIED = "licenseVerified";
    private static final String KEY_DEPOSIT_PAID = "depositPaid"; // Add deposit paid status
    private static final String KEY_WALLET_BALANCE = "walletBalance"; // Add wallet balance

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(long userId, String phone, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void saveSelectedLocation(long locationId, String locationName) {
        editor.putLong(KEY_LOCATION_ID, locationId);
        editor.putString(KEY_LOCATION_NAME, locationName);
        editor.apply();
    }

    public void setLicenseVerified(boolean verified) {
        editor.putBoolean(KEY_LICENSE_VERIFIED, verified);
        editor.apply();
    }

    public void setDepositPaid(boolean isPaid) {
        editor.putBoolean(KEY_DEPOSIT_PAID, isPaid);
        editor.apply();
    }

    public boolean isDepositPaid() {
        return pref.getBoolean(KEY_DEPOSIT_PAID, false); // Return deposit status (default false)
    }

    public void setWalletBalance(double balance) {
        editor.putFloat(KEY_WALLET_BALANCE, (float) balance); // Save wallet balance as float
        editor.apply();
    }

    public double getWalletBalance() {
        return pref.getFloat(KEY_WALLET_BALANCE, 0.0f); // Get wallet balance, default is 0.0
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isLicenseVerified() {
        return pref.getBoolean(KEY_LICENSE_VERIFIED, false);
    }

    public long getUserId() {
        return pref.getLong(KEY_USER_ID, 0);
    }

    public String getPhone() {
        return pref.getString(KEY_PHONE, null);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public long getSelectedLocationId() {
        return pref.getLong(KEY_LOCATION_ID, 0);
    }

    public String getSelectedLocationName() {
        return pref.getString(KEY_LOCATION_NAME, "Unknown");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
