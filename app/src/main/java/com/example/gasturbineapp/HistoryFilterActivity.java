package com.example.gasturbineapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryFilterActivity extends AppCompatActivity {
    private TurbineDatabase database;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private Spinner filterSpinner;
    private Button applyFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_filter);

        String turbineId = getIntent().getStringExtra("turbineId");
        String turbineName = getIntent().getStringExtra("turbineName");
        setTitle(turbineName + " Filtered History");

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        filterSpinner = findViewById(R.id.filterSpinner);
        applyFilterButton = findViewById(R.id.applyFilterButton);

        database = new TurbineDatabase(this);
        List<TurbineData> history = database.getTurbineHistory(turbineId);

        historyAdapter = new HistoryAdapter(history);
        historyRecyclerView.setAdapter(historyAdapter);

        // Set up filter options
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("All");
        filterOptions.add("Last Hour");
        filterOptions.add("Last Day");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        applyFilterButton.setOnClickListener(v -> {
            String selectedFilter = filterSpinner.getSelectedItem().toString();
            List<TurbineData> filteredHistory = filterHistory(history, selectedFilter);
            historyAdapter = new HistoryAdapter(filteredHistory);
            historyRecyclerView.setAdapter(historyAdapter);
        });
    }

    private List<TurbineData> filterHistory(List<TurbineData> history, String filter) {
        long currentTime = System.currentTimeMillis();
        List<TurbineData> filtered = new ArrayList<>();
        for (TurbineData data : history) {
            if (filter.equals("All")) {
                filtered.add(data);
            } else if (filter.equals("Last Hour") && (currentTime - data.getTimestamp()) <= 3600 * 1000) {
                filtered.add(data);
            } else if (filter.equals("Last Day") && (currentTime - data.getTimestamp()) <= 24 * 3600 * 1000) {
                filtered.add(data);
            }
        }
        return filtered;
    }
}