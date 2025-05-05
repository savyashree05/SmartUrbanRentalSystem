package com.urbana.helmetrental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.urbana.helmetrental.R;
import com.urbana.helmetrental.model.Location;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<Location> {
    
    private Context context;
    private List<Location> locations;
    private int selectedPosition = -1;

    public LocationAdapter(Context context, List<Location> locations) {
        super(context, 0, locations);
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        }

        Location currentLocation = locations.get(position);

        TextView tvLocationName = listItem.findViewById(R.id.tvLocationName);
        TextView tvLocationAddress = listItem.findViewById(R.id.tvLocationAddress);
        RadioButton radioButton = listItem.findViewById(R.id.radioButton);

        tvLocationName.setText(currentLocation.getName());
        tvLocationAddress.setText(currentLocation.getAddress());
        radioButton.setChecked(position == selectedPosition);

        return listItem;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
