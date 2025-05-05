package com.urbana.helmetrental.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String BASE_URL = "http://10.0.2.2:8080/";  // Use localhost for emulator
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Method to update the BASE_URL dynamically
    public static void setBaseUrl(String newBaseUrl) {
        BASE_URL = newBaseUrl;
        retrofit = null; // Force rebuild with new URL
    }
}
