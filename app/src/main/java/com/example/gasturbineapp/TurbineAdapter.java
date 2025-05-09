package com.example.gasturbineapp;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TurbineAdapter extends RecyclerView.Adapter<TurbineAdapter.TurbineViewHolder> {
    private static final String TAG = "TurbineAdapter";
    private List<Turbine> turbineList;
    private OnTurbineActionListener actionListener;
    private OnHistoryListener historyListener;
    private OnSettingsListener settingsListener;
    private Context context;

    public interface OnTurbineActionListener {
        void onTurbineAction(Turbine turbine);
    }

    public interface OnHistoryListener {
        void onViewHistory(Turbine turbine);
    }

    public interface OnSettingsListener {
        void onSettings(Turbine turbine);
    }

    public TurbineAdapter(Context context, List<Turbine> turbineList, OnTurbineActionListener actionListener,
                          OnHistoryListener historyListener, OnSettingsListener settingsListener) {
        this.context = context;
        this.turbineList = turbineList;
        this.actionListener = actionListener;
        this.historyListener = historyListener;
        this.settingsListener = settingsListener;
        Log.d(TAG, "TurbineAdapter: Initialized with " + turbineList.size() + " turbines");
    }

    @NonNull
    @Override
    public TurbineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Creating view holder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.turbine_item, parent, false);
        return new TurbineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurbineViewHolder holder, int position) {
        Turbine turbine = turbineList.get(position);
        Log.d(TAG, "onBindViewHolder: Binding turbine " + turbine.getName() + " at position " + position);
        holder.turbineName.setText(turbine.getName());
        holder.turbineStatus.setText("Status: " + turbine.getStatus());
        holder.turbineRpm.setText("RPM: " + turbine.getRpm());
        holder.turbineTemp.setText("Temp: " + String.format("%.1f", turbine.getTemperature()) + "°C");
        holder.turbinePressure.setText("Pressure: " + String.format("%.1f", turbine.getPressure()) + " kPa");
        holder.turbineFuel.setText("Fuel: " + String.format("%.1f", turbine.getFuelConsumption()) + " L/h");
        holder.turbineEfficiency.setText("Efficiency: " + String.format("%.1f", turbine.getEfficiency()) + "%");

        // Holatga qarab rangli indikator
        int statusColor;
        switch (turbine.getStatus()) {
            case "Running":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
                break;
            case "Alert":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                break;
            case "Maintenance":
                statusColor = ContextCompat.getColor(context, android.R.color.holo_orange_dark);
                break;
            default:
                statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                break;
        }
        holder.turbineStatus.setTextColor(statusColor);

        holder.actionButton.setText(turbine.getStatus().equals("Running") || turbine.getStatus().equals("Alert") ? "Stop" : "Start");
        holder.actionButton.setEnabled(!turbine.getStatus().equals("Maintenance"));
        holder.actionButton.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: Action button clicked for " + turbine.getName());
            if (actionListener != null) {
                actionListener.onTurbineAction(turbine);
            }
        });

        // Kontekst menyusini o'rnatish
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            Log.d(TAG, "onCreateContextMenu: Creating context menu for " + turbine.getName());
            menu.setHeaderTitle(turbine.getName());
            menu.add(0, 1, 0, "View History").setOnMenuItemClickListener(item -> {
                Log.d(TAG, "onCreateContextMenu: View History selected for " + turbine.getName());
                if (historyListener != null) {
                    historyListener.onViewHistory(turbine);
                }
                return true;
            });
            menu.add(0, 2, 1, "Settings").setOnMenuItemClickListener(item -> {
                Log.d(TAG, "onCreateContextMenu: Settings selected for " + turbine.getName());
                if (settingsListener != null) {
                    settingsListener.onSettings(turbine);
                }
                return true;
            });
        });
    }

    @Override
    public int getItemCount() {
        return turbineList.size();
    }

    static class TurbineViewHolder extends RecyclerView.ViewHolder {
        TextView turbineName, turbineStatus, turbineRpm, turbineTemp, turbinePressure, turbineFuel, turbineEfficiency;
        Button actionButton;

        public TurbineViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "TurbineViewHolder: Initializing view holder");
            turbineName = itemView.findViewById(R.id.turbineName);
            turbineStatus = itemView.findViewById(R.id.turbineStatus);
            turbineRpm = itemView.findViewById(R.id.turbineRpm);
            turbineTemp = itemView.findViewById(R.id.turbineTemp);
            turbinePressure = itemView.findViewById(R.id.turbinePressure);
            turbineFuel = itemView.findViewById(R.id.turbineFuel);
            turbineEfficiency = itemView.findViewById(R.id.turbineEfficiency);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}