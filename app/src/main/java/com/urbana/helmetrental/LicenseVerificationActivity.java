package com.urbana.helmetrental;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.LicenseVerificationRequest;
import com.urbana.helmetrental.model.LicenseVerificationResponse;
import com.urbana.helmetrental.util.FileUtil;
import com.urbana.helmetrental.util.SessionManager;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LicenseVerificationActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private ImageView ivLicenseImage;
    private Button btnCamera, btnGallery, btnVerify;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SessionManager sessionManager;
    private Uri licenseImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_verification);

        ivLicenseImage = findViewById(R.id.ivLicenseImage);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnVerify = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (licenseImageUri != null) {
                    verifyLicense();
                } else {
                    Toast.makeText(LicenseVerificationActivity.this, "Please select or capture a license image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    // Get the captured image from the camera
                    licenseImageUri = FileUtil.getImageUri(this, (android.graphics.Bitmap) extras.get("data"));
                    ivLicenseImage.setImageURI(licenseImageUri);
                    btnVerify.setEnabled(true);
                }
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                // Get the selected image from gallery
                licenseImageUri = data.getData();
                ivLicenseImage.setImageURI(licenseImageUri);
                btnVerify.setEnabled(true);
            }
        }
    }

    private void verifyLicense() {
        progressBar.setVisibility(View.VISIBLE);
        
        try {
            File licenseFile = FileUtil.getFileFromUri(this, licenseImageUri);
            
            // Create request body for file
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), licenseFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("license", licenseFile.getName(), requestFile);
            
            // Create request body for user ID
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sessionManager.getUserId()));
            
            Call<LicenseVerificationResponse> call = apiService.verifyLicense(
                    "Bearer " + sessionManager.getToken(),
                    filePart,
                    userId
            );
            
            call.enqueue(new Callback<LicenseVerificationResponse>() {
                @Override
                public void onResponse(Call<LicenseVerificationResponse> call, Response<LicenseVerificationResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        LicenseVerificationResponse verificationResponse = response.body();
                        
                        if (verificationResponse.isVerified()) {
                            // License verified successfully
                            sessionManager.setLicenseVerified(true);
                            
                            // Navigate to dashboard
                            Intent intent = new Intent(LicenseVerificationActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LicenseVerificationActivity.this, 
                                    "License verification failed: " + verificationResponse.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LicenseVerificationActivity.this, "Failed to verify license. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LicenseVerificationResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LicenseVerificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
