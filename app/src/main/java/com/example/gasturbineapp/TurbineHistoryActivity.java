package com.example.gasturbineapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TurbineHistoryActivity extends AppCompatActivity {
    private TurbineDatabase database;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private Button filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbine_history);

        String turbineId = getIntent().getStringExtra("turbineId");
        String turbineName = getIntent().getStringExtra("turbineName");
        setTitle(turbineName + " History");

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        filterButton = findViewById(R.id.filterButton);

        database = new TurbineDatabase(this);
        List<TurbineData> history = database.getTurbineHistory(turbineId);

        historyAdapter = new HistoryAdapter(history);
        historyRecyclerView.setAdapter(historyAdapter);

        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryFilterActivity.class);
            intent.putExtra("turbineId", turbineId);
            intent.putExtra("turbineName", turbineName);
            startActivity(intent);
        });
    }
}