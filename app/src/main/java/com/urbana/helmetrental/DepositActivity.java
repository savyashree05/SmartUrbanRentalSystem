package com.urbana.helmetrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.DepositRequest;
import com.urbana.helmetrental.model.DepositResponse;
import com.urbana.helmetrental.util.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private TextView tvDepositAmount;
    private RadioGroup rgPaymentMethods;
    private RadioButton rbUPI, rbCard, rbNetBanking;
    private Button btnProceed;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    private static final double DEPOSIT_AMOUNT = 1000.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        // Initialize UI components
        tvDepositAmount = findViewById(R.id.tvDepositAmount);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        rbUPI = findViewById(R.id.rbUPI);
        rbCard = findViewById(R.id.rbCard);
        rbNetBanking = findViewById(R.id.rbNetBanking);
        btnProceed = findViewById(R.id.btnProceed);
        progressBar = findViewById(R.id.progressBar);

        // Initialize API client and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Set deposit amount on the UI
        tvDepositAmount.setText("₹" + String.format("%.2f", DEPOSIT_AMOUNT));

        // If deposit is already paid, skip to the next screen
        if (sessionManager.isDepositPaid()) {
            proceedToNextScreen();
            return;
        }

        // Set click listener for the Proceed button
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rgPaymentMethods.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(DepositActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with making the deposit
                makeDeposit();
            }
        });
    }

    private void makeDeposit() {
        progressBar.setVisibility(View.VISIBLE);

        // Determine selected payment method
        String paymentMethod;
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
        if (selectedId == R.id.rbUPI) {
            paymentMethod = "UPI";
        } else if (selectedId == R.id.rbCard) {
            paymentMethod = "CARD";
        } else {
            paymentMethod = "NETBANKING";
        }

        // Create deposit request with user data and selected payment method
        DepositRequest request = new DepositRequest(
                sessionManager.getUserId(),
                DEPOSIT_AMOUNT,
                paymentMethod
        );

        // Make the deposit API call
        Call<DepositResponse> call = apiService.makeDeposit(
                "Bearer " + sessionManager.getToken(),
                request
        );

        // Handle the API response
        call.enqueue(new Callback<DepositResponse>() {
            @Override
            public void onResponse(Call<DepositResponse> call, Response<DepositResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    DepositResponse depositResponse = response.body();

                    if (depositResponse.isSuccess()) {
                        // Save deposit status and wallet balance
                        sessionManager.setDepositPaid(true);
                        sessionManager.setWalletBalance(depositResponse.getWalletBalance());

                        Toast.makeText(DepositActivity.this,
                                "Deposit successful! Your wallet balance: ₹" + depositResponse.getWalletBalance(),
                                Toast.LENGTH_LONG).show();

                        // Proceed to the next screen
                        proceedToNextScreen();
                    } else {
                        // Handle failure to process the deposit
                        Toast.makeText(DepositActivity.this,
                                "Deposit failed: " + depositResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle failure and log the error response
                    try {
                        // Make sure errorBody is not null before converting to string
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";

                        // Log the error message for debugging
                        Log.e("DepositActivity", "Error processing deposit: " + errorBody);

                        // Display a message to the user
                        Toast.makeText(DepositActivity.this,
                                "Failed to process deposit. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // Handle error when reading error body
                        Log.e("DepositActivity", "Error reading error body", e);
                        Toast.makeText(DepositActivity.this,
                                "An error occurred. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DepositResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                // Log the network error message with the full stack trace
                Log.e("DepositActivity", "Network error: " + t.getMessage(), t);

                // Display a message to the user
                Toast.makeText(DepositActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToNextScreen() {
        // Go to the LocationSelectionActivity after successful deposit
        Intent intent = new Intent(DepositActivity.this, LocationSelectionActivity.class);
        startActivity(intent);
        finish();
    }
}
