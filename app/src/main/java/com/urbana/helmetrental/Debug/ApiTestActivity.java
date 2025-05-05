package com.urbana.helmetrental.Debug;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.R;
import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.OtpRequest;
import com.urbana.helmetrental.model.OtpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiTestActivity extends AppCompatActivity {

    private EditText etApiUrl, etPhoneNumber;
    private Button btnTestApi;
    private TextView tvResult;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);

        etApiUrl = findViewById(R.id.etApiUrl);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnTestApi = findViewById(R.id.btnTestApi);
        tvResult = findViewById(R.id.tvResult);
        progressBar = findViewById(R.id.progressBar);

        // Set default API URL
        etApiUrl.setText("http://10.0.2.2:8080/");

        btnTestApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiUrl = etApiUrl.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(apiUrl)) {
                    etApiUrl.setError("API URL is required");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    etPhoneNumber.setError("Phone number is required");
                    return;
                }

                testApiConnection(apiUrl, phoneNumber);
            }
        });
    }

    private void testApiConnection(String apiUrl, String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        tvResult.setText("Testing API connection...");

        // Update API base URL
        ApiClient.setBaseUrl(apiUrl);

        // Create API service
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create request
        OtpRequest request = new OtpRequest(phoneNumber);

        // Make API call
        Call<OtpResponse> call = apiService.requestOtp(request);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                progressBar.setVisibility(View.GONE);

                StringBuilder resultBuilder = new StringBuilder();
                resultBuilder.append("API Response Code: ").append(response.code()).append("\n\n");

                if (response.isSuccessful() && response.body() != null) {
                    OtpResponse otpResponse = response.body();
                    resultBuilder.append("Success: ").append(otpResponse.isSuccess()).append("\n");
                    resultBuilder.append("Message: ").append(otpResponse.getMessage()).append("\n");

                    // Log success
                    Log.d("API_TEST", "API call successful: " + otpResponse.getMessage());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        resultBuilder.append("Error Body: ").append(errorBody).append("\n");

                        // Log error
                        Log.e("API_TEST", "API call failed: " + errorBody);
                    } catch (Exception e) {
                        resultBuilder.append("Error parsing error body: ").append(e.getMessage()).append("\n");
                        Log.e("API_TEST", "Error parsing error body: " + e.getMessage());
                    }
                }

                tvResult.setText(resultBuilder.toString());
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                StringBuilder resultBuilder = new StringBuilder();
                resultBuilder.append("API Call Failed\n\n");
                resultBuilder.append("Error: ").append(t.getMessage()).append("\n");
                resultBuilder.append("Error Type: ").append(t.getClass().getSimpleName()).append("\n");

                // Log failure
                Log.e("API_TEST", "API call failed with exception", t);
                t.printStackTrace();

                tvResult.setText(resultBuilder.toString());
            }
        });
    }
}
