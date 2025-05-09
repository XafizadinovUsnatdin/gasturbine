package com.example.gasturbineapp;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView turbineRecyclerView;
    private TurbineAdapter turbineAdapter;
    private List<Turbine> turbineList;
    private Handler handler;
    private Runnable updateRunnable;
    private TurbineDatabase database;
    private static final String CHANNEL_ID = "TurbineAlerts";
    private static final String PREFS_NAME = "TurbineAppPrefs";
    private static final String KEY_LAST_EXPORT_URI = "last_export_uri";

    private static final String USER_GUIDE_TEXT =
            "GasTurbineApp User Guide\n\n" +
                    "Overview\n" +
                    "The GasTurbineApp is a mobile application designed to monitor and manage gas turbines in real-time. It provides users with tools to track turbine performance, receive alerts for critical issues, manage turbine settings, and export historical data for analysis. This guide explains the app's features and how to use them effectively.\n\n" +
                    "Purpose\n" +
                    "The app ensures turbine safety and operational efficiency by:\n" +
                    "- Monitoring key turbine parameters (e.g., RPM, temperature, fuel consumption, efficiency).\n" +
                    "- Sending notifications for critical conditions (e.g., high temperature or RPM).\n" +
                    "- Allowing users to view and export turbine history for maintenance and analysis.\n" +
                    "- Enabling turbine control and settings adjustments.\n\n" +
                    "Features and Usage\n\n" +
                    "1. Real-Time Turbine Monitoring\n" +
                    "The app displays the status and performance metrics of each turbine on the main screen.\n" +
                    "- How It Works: The main screen shows a list of turbines (e.g., Turbine A, Turbine B, Turbine C) with their current status (Running, Idle, Maintenance, or Alert). Metrics like RPM, temperature (°C), fuel consumption (L/h), and efficiency (%) are updated every 5 seconds. If a turbine's temperature exceeds 750°C or RPM exceeds its maximum limit, the status changes to Alert, and a notification is sent.\n" +
                    "- Steps:\n" +
                    "  1. Open the app to view the list of turbines on the main screen.\n" +
                    "  2. Check each turbine’s status and metrics (e.g., Turbine A: Running, 3600 RPM, 650°C).\n\n" +
                    "2. Turbine Control\n" +
                    "Users can toggle a turbine’s operational state between Running and Idle.\n" +
                    "- How It Works: A turbine in Running or Alert state can be set to Idle, stopping its operation. A turbine in Idle state can be set to Running, resuming operation with default values.\n" +
                    "- Steps:\n" +
                    "  1. On the main screen, locate the turbine you want to control.\n" +
                    "  2. Tap the turbine card to toggle its state (Running/Alert to Idle, or Idle to Running).\n" +
                    "  3. The turbine’s metrics update immediately.\n\n" +
                    "3. View Turbine History\n" +
                    "Users can view the historical data for each turbine.\n" +
                    "- How It Works: Each turbine’s history is stored in a database and can be filtered and viewed.\n" +
                    "- Steps:\n" +
                    "  1. Long-press a turbine card on the main screen.\n" +
                    "  2. Select View History from the context menu.\n" +
                    "  3. The Turbine History screen opens, showing past records.\n" +
                    "  4. Use the Filter History button to apply filters (if available).\n\n" +
                    "4. Adjust Turbine Settings\n" +
                    "Users can modify the maximum RPM for each turbine.\n" +
                    "- How It Works: The settings screen allows changing the maximum RPM, affecting operational limits.\n" +
                    "- Steps:\n" +
                    "  1. Long-press a turbine card on the main screen.\n" +
                    "  2. Select Settings from the context menu.\n" +
                    "  3. Enter a new Max RPM value and tap Save.\n" +
                    "  4. Return to the main screen to see the updated Max RPM.\n\n" +
                    "5. Export Turbine History to CSV\n" +
                    "Users can export all turbine history to a CSV file.\n" +
                    "- How It Works: Exports history to a CSV file saved to Downloads or Documents.\n" +
                    "- Steps:\n" +
                    "  1. Tap the three-dot menu (top-right corner).\n" +
                    "  2. Select Export History.\n" +
                    "  3. Tap Export to CSV and choose a save location (Android 11+).\n" +
                    "  4. A toast confirms the export.\n\n" +
                    "6. View Exported CSV File\n" +
                    "Users can view the exported CSV file.\n" +
                    "- How It Works: Opens the file with a viewer app (e.g., Google Files).\n" +
                    "- Steps:\n" +
                    "  1. Install a CSV viewer app (e.g., Google Files).\n" +
                    "  2. Tap the three-dot menu and select View Exported File.\n" +
                    "  3. The file opens if available; otherwise, a message prompts export or app installation.\n\n" +
                    "7. Clear Turbine History\n" +
                    "Users can delete all turbine history data.\n" +
                    "- How It Works: Clears all history from the database.\n" +
                    "- Steps:\n" +
                    "  1. Tap the three-dot menu and select Clear History.\n" +
                    "  2. A toast confirms: All turbine history cleared.\n\n" +
                    "8. Receive Notifications\n" +
                    "The app sends alerts for critical turbine conditions.\n" +
                    "- How It Works: Sends notifications for temperature > 750°C or RPM > Max RPM.\n" +
                    "- Steps:\n" +
                    "  1. Run the app with a turbine in Running state.\n" +
                    "  2. Wait for a critical condition; a notification appears.\n" +
                    "  3. Tap to dismiss the notification.\n\n" +
                    "Requirements\n" +
                    "- Android 6.0 (API 23) or higher.\n" +
                    "- Storage permissions (Android 6.0–10) for CSV export/view.\n" +
                    "- Notification permissions for alerts.\n" +
                    "- A CSV viewer app (e.g., Google Files).\n\n" +
                    "Troubleshooting\n" +
                    "- \"No app found to view CSV file\": Install a CSV viewer (e.g., Google Files).\n" +
                    "- Export Fails: Select a valid save location (Android 11+) or grant permissions.\n" +
                    "- Notifications Not Appearing: Enable notification permissions.\n" +
                    "- History Not Clearing: Check toast messages and retry.\n\n" +
                    "Additional Notes\n" +
                    "The app updates data every 5 seconds. All layouts are centered for usability. Contact the developer for further assistance.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing MainActivity");
        setContentView(R.layout.activity_main);

        turbineRecyclerView = findViewById(R.id.turbineRecyclerView);
        if (turbineRecyclerView == null) {
            Log.e(TAG, "onCreate: turbineRecyclerView is null");
            return;
        }
        turbineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database
        Log.d(TAG, "onCreate: Initializing database");
        database = new TurbineDatabase(this);

        // Initialize turbine data
        Log.d(TAG, "onCreate: Initializing turbine data");
        turbineList = new ArrayList<>();
        turbineList.add(new Turbine("1", "Turbine A", "Running", 3600, 650, 101.3, 10.5, 85.0, 4000));
        turbineList.add(new Turbine("2", "Turbine B", "Idle", 0, 25, 101.3, 0.0, 0.0, 4000));
        turbineList.add(new Turbine("3", "Turbine C", "Maintenance", 0, 30, 101.3, 0.0, 0.0, 4000));

        // Initialize adapter
        Log.d(TAG, "onCreate: Setting up TurbineAdapter");
        turbineAdapter = new TurbineAdapter(this, turbineList, this::onTurbineAction, this::onViewHistory, this::onSettings);
        turbineRecyclerView.setAdapter(turbineAdapter);

        // Create notification channel
        Log.d(TAG, "onCreate: Creating notification channel");
        createNotificationChannel();

        // Periodic updates
        Log.d(TAG, "onCreate: Starting periodic updates");
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateTurbineData();
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateRunnable);
    }

    private void updateTurbineData() {
        Log.d(TAG, "updateTurbineData: Updating turbine data");
        Random random = new Random();
        for (Turbine turbine : turbineList) {
            if (turbine.getStatus().equals("Running")) {
                int currentRpm = turbine.getRpm();
                int rpmChange = random.nextInt(50) - 25; // ±25 RPM change
                turbine.setRpm(Math.max(3000, Math.min(turbine.getMaxRpm(), currentRpm + rpmChange)));

                double currentTemp = turbine.getTemperature();
                double tempChange = random.nextDouble() * 10 - 5; // ±5°C change
                turbine.setTemperature(Math.max(500, Math.min(800, currentTemp + tempChange)));

                turbine.setFuelConsumption(10.0 + random.nextDouble() * 5); // 10-15 L/h
                turbine.setEfficiency(80.0 + random.nextDouble() * 10); // 80-90%

                if (turbine.getTemperature() > 750 || turbine.getRpm() > turbine.getMaxRpm()) {
                    turbine.setStatus("Alert");
                    sendNotification(turbine);
                }
                database.insertTurbineData(turbine);
            } else {
                turbine.setRpm(0);
                turbine.setTemperature(25);
                turbine.setFuelConsumption(0.0);
                turbine.setEfficiency(0.0);
            }
        }
        turbineAdapter.notifyDataSetChanged();
    }

    private void onTurbineAction(Turbine turbine) {
        Log.d(TAG, "onTurbineAction: Action on turbine " + turbine.getName() + ", Status: " + turbine.getStatus());
        if (turbine.getStatus().equals("Running") || turbine.getStatus().equals("Alert")) {
            turbine.setStatus("Idle");
            turbine.setRpm(0);
            turbine.setTemperature(25);
            turbine.setFuelConsumption(0.0);
            turbine.setEfficiency(0.0);
        } else if (turbine.getStatus().equals("Idle")) {
            turbine.setStatus("Running");
            turbine.setRpm(3600);
            turbine.setTemperature(650);
            turbine.setFuelConsumption(10.5);
            turbine.setEfficiency(85.0);
        }
        turbineAdapter.notifyDataSetChanged();
        database.insertTurbineData(turbine);
    }

    private void onViewHistory(Turbine turbine) {
        Log.d(TAG, "onViewHistory: Opening history for " + turbine.getName());
        Intent intent = new Intent(this, TurbineHistoryActivity.class);
        intent.putExtra("turbineId", turbine.getId());
        intent.putExtra("turbineName", turbine.getName());
        startActivity(intent);
    }

    private void onSettings(Turbine turbine) {
        Log.d(TAG, "onSettings: Opening settings for " + turbine.getName());
        Intent intent = new Intent(this, TurbineSettingsActivity.class);
        intent.putExtra("turbineId", turbine.getId());
        intent.putExtra("turbineName", turbine.getName());
        intent.putExtra("maxRpm", turbine.getMaxRpm());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String turbineId = data.getStringExtra("turbineId");
            int maxRpm = data.getIntExtra("maxRpm", 4000);
            for (Turbine turbine : turbineList) {
                if (turbine.getId().equals(turbineId)) {
                    turbine.setMaxRpm(maxRpm);
                    break;
                }
            }
            turbineAdapter.notifyDataSetChanged();
        }
    }

    private void sendNotification(Turbine turbine) {
        Log.d(TAG, "sendNotification: Sending notification for " + turbine.getName());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Turbine Alert")
                .setContentText(turbine.getName() + ": " + (turbine.getTemperature() > 750 ? "High Temperature" : "High RPM"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        Log.d(TAG, "createNotificationChannel: Setting up notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Turbine Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for turbine issues");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Inflating main menu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: Menu item selected: " + item.getItemId());
        int itemId = item.getItemId();
        if (itemId == R.id.action_export) {
            Log.d(TAG, "onOptionsItemSelected: Export History selected");
            exportHistoryToCsv();
            return true;
        } else if (itemId == R.id.action_clear_history) {
            Log.d(TAG, "onOptionsItemSelected: Clear History selected");
            clearTurbineHistory();
            return true;
        } else if (itemId == R.id.action_view_exported_file) {
            Log.d(TAG, "onOptionsItemSelected: View Exported File selected");
            viewExportedFile();
            return true;
        } else if (itemId == R.id.action_help) {
            Log.d(TAG, "onOptionsItemSelected: Help selected");
            showUserGuide();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportHistoryToCsv() {
        Log.d(TAG, "exportHistoryToCsv: Starting ExportHistoryActivity");
        Intent intent = new Intent(this, ExportHistoryActivity.class);
        startActivity(intent);
    }

    private void clearTurbineHistory() {
        Log.d(TAG, "clearTurbineHistory: Clearing all turbine history");
        try {
            database.clearAllHistory();
            Log.d(TAG, "clearTurbineHistory: History cleared successfully");
            Toast.makeText(this, "All turbine history cleared", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "clearTurbineHistory: Failed to clear history", e);
            Toast.makeText(this, "Error clearing history: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void viewExportedFile() {
        Log.d(TAG, "viewExportedFile: Attempting to view exported CSV file");
        try {
            Uri fileUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String uriString = prefs.getString(KEY_LAST_EXPORT_URI, null);
                if (uriString == null) {
                    Log.w(TAG, "viewExportedFile: No exported file URI found in SharedPreferences");
                    Toast.makeText(this, "No exported file found. Please export a CSV file first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                fileUri = Uri.parse(uriString);
                Log.d(TAG, "viewExportedFile: Retrieved SAF URI: " + fileUri);

                if (!isValidUri(fileUri)) {
                    Log.w(TAG, "viewExportedFile: Invalid URI detected");
                    Toast.makeText(this, "Invalid exported file URI. Please export again.", Toast.LENGTH_SHORT).show();
                    prefs.edit().remove(KEY_LAST_EXPORT_URI).apply();
                    return;
                }
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File[] files = downloadsDir.listFiles((dir, name) -> name.startsWith("TurbineHistory_") && name.endsWith(".csv"));
                if (files == null || files.length == 0) {
                    Log.w(TAG, "viewExportedFile: No CSV files found in Downloads");
                    Toast.makeText(this, "No exported CSV file found. Please export a CSV file first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                File latestFile = files[0];
                for (File file : files) {
                    if (file.lastModified() > latestFile.lastModified()) {
                        latestFile = file;
                    }
                }
                Log.d(TAG, "viewExportedFile: Found file: " + latestFile.getAbsolutePath());
                fileUri = Uri.fromFile(latestFile);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/csv");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.d(TAG, "viewExportedFile: Opening with text/csv MIME type");
                startActivity(intent);
                return;
            }

            Log.d(TAG, "viewExportedFile: No app for text/csv, trying text/plain");
            intent.setDataAndType(fileUri, "text/plain");
            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.d(TAG, "viewExportedFile: Opening with text/plain MIME type");
                startActivity(intent);
                return;
            }

            Log.w(TAG, "viewExportedFile: No app found to view CSV or text file");
            Toast.makeText(this, "No app found to view CSV file. Please install a file viewer (e.g., Google Files).", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "viewExportedFile: Failed to view file", e);
            Toast.makeText(this, "Error viewing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidUri(Uri uri) {
        try {
            getContentResolver().getType(uri);
            return true;
        } catch (Exception e) {
            Log.w(TAG, "isValidUri: URI validation failed", e);
            return false;
        }
    }

    private void showUserGuide() {
        Log.d(TAG, "showUserGuide: Displaying user guide");
        TextView textView = new TextView(this);
        textView.setText(USER_GUIDE_TEXT);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(textView);

        new AlertDialog.Builder(this)
                .setTitle("User Guide")
                .setView(scrollView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Cleaning up");
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}