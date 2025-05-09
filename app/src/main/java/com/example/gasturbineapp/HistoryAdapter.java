package com.example.gasturbineapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<TurbineData> historyList;

    public HistoryAdapter(List<TurbineData> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        TurbineData data = historyList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date(data.getTimestamp()));

        holder.timestampText.setText("Time: " + timestamp);
        holder.rpmText.setText("RPM: " + data.getRpm());
        holder.tempText.setText("Temp: " + String.format("%.1f", data.getTemperature()) + "°C");
        holder.fuelText.setText("Fuel: " + String.format("%.1f", data.getFuelConsumption()) + " L/h");
        holder.efficiencyText.setText("Efficiency: " + String.format("%.1f", data.getEfficiency()) + "%");
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText, rpmText, tempText, fuelText, efficiencyText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.timestampText);
            rpmText = itemView.findViewById(R.id.rpmText);
            tempText = itemView.findViewById(R.id.tempText);
            fuelText = itemView.findViewById(R.id.fuelText);
            efficiencyText = itemView.findViewById(R.id.efficiencyText);
        }
    }
}