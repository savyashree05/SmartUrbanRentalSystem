package com.urbana.helmetrental.api;

import com.urbana.helmetrental.model.DepositRequest;
import com.urbana.helmetrental.model.DepositResponse;
import com.urbana.helmetrental.model.Helmet;
import com.urbana.helmetrental.model.LicenseVerificationResponse;
import com.urbana.helmetrental.model.Location;
import com.urbana.helmetrental.model.OtpRequest;
import com.urbana.helmetrental.model.OtpResponse;
import com.urbana.helmetrental.model.OtpVerificationRequest;
import com.urbana.helmetrental.model.OtpVerificationResponse;
import com.urbana.helmetrental.model.RegisterRequest;
import com.urbana.helmetrental.model.VerifyOtpRequest;
import com.urbana.helmetrental.model.ApiResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    
    @POST("api/users/send-otp")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("/api/users/register")
    Call<ApiResponse> registerUser(@Body RegisterRequest request);

    @POST("/api/users/verifyOtp")
    Call<ApiResponse> verifyOtp(@Body VerifyOtpRequest request);
    
    @POST("api/users/verify-otp")
    Call<OtpVerificationResponse> verifyOtp(@Body OtpVerificationRequest request);
    
    @GET("api/locations")
    Call<List<Location>> getLocations(@Header("Authorization") String token);
    
    @Multipart
    @POST("api/users/verify-license")
    Call<LicenseVerificationResponse> verifyLicense(
            @Header("Authorization") String token,
            @Part MultipartBody.Part license,
            @Part("userId") RequestBody userId
    );

    @POST("api/deposit")  // Replace with the actual endpoint for making a deposit
    Call<DepositResponse> makeDeposit(
            @Header("Authorization") String token,
            @Body DepositRequest request
    );
    
    @GET("api/helmets/available")
    Call<List<Helmet>> getAvailableHelmets(
            @Header("Authorization") String token,
            @Query("locationId") long locationId
    );
}
