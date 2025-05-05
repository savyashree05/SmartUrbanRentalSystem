package com.urbana.helmetrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.urbana.helmetrental.adapter.LocationAdapter;
import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.Location;
import com.urbana.helmetrental.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationSelectionActivity extends AppCompatActivity {

    private ListView listViewLocations;
    private ProgressBar progressBar;
    private Button btnContinue;
    private ApiService apiService;
    private SessionManager sessionManager;
    private LocationAdapter adapter;
    private List<Location> locationList;
    private Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        listViewLocations = findViewById(R.id.listViewLocations);
        progressBar = findViewById(R.id.progressBar);
        btnContinue = findViewById(R.id.btnContinue);

        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        locationList = new ArrayList<>();
        adapter = new LocationAdapter(this, locationList);
        listViewLocations.setAdapter(adapter);

        // Fetch locations
        fetchLocations();

        listViewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = locationList.get(position);
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
                btnContinue.setEnabled(true);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null) {
                    // Save selected location
                    sessionManager.saveSelectedLocation(selectedLocation.getId(), selectedLocation.getName());
                    
                    // Navigate to license verification
                    Intent intent = new Intent(LocationSelectionActivity.this, LicenseVerificationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LocationSelectionActivity.this, "Please select a location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchLocations() {
        progressBar.setVisibility(View.VISIBLE);
        
        Call<List<Location>> call = apiService.getLocations("Bearer " + sessionManager.getToken());
        
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    locationList.clear();
                    locationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    if (locationList.isEmpty()) {
                        Toast.makeText(LocationSelectionActivity.this, "No locations available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LocationSelectionActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LocationSelectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
