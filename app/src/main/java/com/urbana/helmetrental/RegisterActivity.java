package com.urbana.helmetrental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.OtpRequest;
import com.urbana.helmetrental.model.OtpResponse;
import com.urbana.helmetrental.Debug.ApiTestActivity;
import com.urbana.helmetrental.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnSendOtp;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.etPhone);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check network connectivity first
                if (!NetworkUtils.isNetworkAvailable(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this,
                            "No internet connection. Please check your network settings.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                String phoneNumber = etPhone.getText().toString().trim();
                if (validatePhoneNumber(phoneNumber)) {
                    requestOtp(phoneNumber);
                }
            }
        });

        // Add long press listener for debug purposes
        btnSendOtp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Open API test activity for debugging
                Intent intent = new Intent(RegisterActivity.this, ApiTestActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhone.setError("Phone number is required");
            return false;
        } else if (phoneNumber.length() < 10) {
            etPhone.setError("Enter a valid phone number");
            return false;
        }
        return true;
    }

    private void requestOtp(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);

        OtpRequest request = new OtpRequest(phoneNumber);
        Call<OtpResponse> call = apiService.requestOtp(request);

        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    OtpResponse otpResponse = response.body();
                    if (otpResponse.isSuccess()) {
                        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("PHONE_NUMBER", phoneNumber);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Failed to send OTP: " + otpResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Toast.makeText(RegisterActivity.this,
                                "Server error: " + errorBody,
                                Toast.LENGTH_LONG).show();
                        Log.e("OTP_ERROR", "Error code: " + response.code() + ", Body: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this,
                                "Failed to send OTP. Please check your connection and try again.",
                                Toast.LENGTH_LONG).show();
                        Log.e("OTP_ERROR", "Exception parsing error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("OTP_ERROR", "Network failure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
