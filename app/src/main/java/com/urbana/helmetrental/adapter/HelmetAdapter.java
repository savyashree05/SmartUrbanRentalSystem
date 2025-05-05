package com.urbana.helmetrental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urbana.helmetrental.R;
import com.urbana.helmetrental.model.Helmet;

import java.util.List;

public class HelmetAdapter extends RecyclerView.Adapter<HelmetAdapter.HelmetViewHolder> {

    private Context context;
    private List<Helmet> helmets;

    public HelmetAdapter(Context context, List<Helmet> helmets) {
        this.context = context;
        this.helmets = helmets;
    }

    @NonNull
    @Override
    public HelmetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_helmet, parent, false);
        return new HelmetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelmetViewHolder holder, int position) {
        Helmet helmet = helmets.get(position);
        holder.tvHelmetCode.setText("Helmet #" + helmet.getCode());
        holder.tvHelmetStatus.setText(helmet.isAvailable() ? "Available" : "In Use");
        
        holder.btnRent.setEnabled(helmet.isAvailable());
        holder.btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Renting helmet " + helmet.getCode(), Toast.LENGTH_SHORT).show();
                // Implement rental logic here
            }
        });
    }

    @Override
    public int getItemCount() {
        return helmets.size();
    }

    public static class HelmetViewHolder extends RecyclerView.ViewHolder {
        TextView tvHelmetCode, tvHelmetStatus;
        Button btnRent;

        public HelmetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHelmetCode = itemView.findViewById(R.id.tvHelmetCode);
            tvHelmetStatus = itemView.findViewById(R.id.tvHelmetStatus);
            btnRent = itemView.findViewById(R.id.btnRent);
        }
    }
}
