package com.urbana.helmetrental;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urbana.helmetrental.adapter.HelmetAdapter;
import com.urbana.helmetrental.api.ApiClient;
import com.urbana.helmetrental.api.ApiService;
import com.urbana.helmetrental.model.Helmet;
import com.urbana.helmetrental.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvLocation;
    private RecyclerView recyclerViewHelmets;
    private ApiService apiService;
    private SessionManager sessionManager;
    private HelmetAdapter helmetAdapter;
    private List<Helmet> helmetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvLocation = findViewById(R.id.tvLocation);
        recyclerViewHelmets = findViewById(R.id.recyclerViewHelmets);

        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        
        // Set welcome message
        tvWelcome.setText("Welcome, User #" + sessionManager.getUserId());
        
        // Set selected location
        tvLocation.setText("Location: " + sessionManager.getSelectedLocationName());
        
        // Setup RecyclerView
        helmetList = new ArrayList<>();
        helmetAdapter = new HelmetAdapter(this, helmetList);
        recyclerViewHelmets.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHelmets.setAdapter(helmetAdapter);
        
        // Fetch available helmets
        fetchAvailableHelmets();
    }

    private void fetchAvailableHelmets() {
        Call<List<Helmet>> call = apiService.getAvailableHelmets(
                "Bearer " + sessionManager.getToken(),
                sessionManager.getSelectedLocationId()
        );
        
        call.enqueue(new Callback<List<Helmet>>() {
            @Override
            public void onResponse(Call<List<Helmet>> call, Response<List<Helmet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    helmetList.clear();
                    helmetList.addAll(response.body());
                    helmetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Helmet>> call, Throwable t) {
                // Handle error
            }
        });
    }
}
