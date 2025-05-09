package com.example.gasturbineapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class TurbineSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbine_settings);

        String turbineId = getIntent().getStringExtra("turbineId");
        String turbineName = getIntent().getStringExtra("turbineName");
        int maxRpm = getIntent().getIntExtra("maxRpm", 4000);

        setTitle(turbineName + " Settings");

        EditText maxRpmInput = findViewById(R.id.maxRpmInput);
        maxRpmInput.setText(String.valueOf(maxRpm));

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            int newMaxRpm;
            try {
                newMaxRpm = Integer.parseInt(maxRpmInput.getText().toString());
            } catch (NumberFormatException e) {
                newMaxRpm = 4000;
            }

            Intent result = new Intent();
            result.putExtra("turbineId", turbineId);
            result.putExtra("maxRpm", newMaxRpm);
            setResult(RESULT_OK, result);
            finish();
        });
    }
}