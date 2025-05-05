package com.urbana.helmetrental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.OtpVerificationRequest;
import com.urbana.helmetrental.model.OtpVerificationResponse;
import com.urbana.helmetrental.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private ProgressBar progressBar;
    private TextView tvPhoneNumber;
    private ApiService apiService;
    private String phoneNumber;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        progressBar = findViewById(R.id.progressBar);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);

        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Get phone number from intent
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        tvPhoneNumber.setText("Enter the OTP sent to " + phoneNumber);

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOtp.getText().toString().trim();
                if (validateOtp(otp)) {
                    verifyOtp(otp);
                }
            }
        });
    }

    private boolean validateOtp(String otp) {
        if (TextUtils.isEmpty(otp)) {
            etOtp.setError("OTP is required");
            return false;
        } else if (otp.length() < 4) {
            etOtp.setError("Enter a valid OTP");
            return false;
        }
        return true;
    }

    private void verifyOtp(String otp) {
        progressBar.setVisibility(View.VISIBLE);
        
        OtpVerificationRequest request = new OtpVerificationRequest(phoneNumber, otp);
        Call<OtpVerificationResponse> call = apiService.verifyOtp(request);
        
        call.enqueue(new Callback<OtpVerificationResponse>() {
            @Override
            public void onResponse(Call<OtpVerificationResponse> call, Response<OtpVerificationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    OtpVerificationResponse verificationResponse = response.body();
                    
                    // Save user session
                    sessionManager.createLoginSession(
                            verificationResponse.getUserId(),
                            phoneNumber,
                            verificationResponse.getToken()
                    );
                    
                    // Navigate to location selection
                    Intent intent = new Intent(OtpVerificationActivity.this, LocationSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OtpVerificationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OtpVerificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
