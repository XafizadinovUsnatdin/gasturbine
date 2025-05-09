package com.example.gasturbineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportHistoryActivity extends AppCompatActivity {
    private static final String TAG = "ExportHistoryActivity";
    private TurbineDatabase database;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> createFileLauncher;
    private static final String PREFS_NAME = "TurbineAppPrefs";
    private static final String KEY_LAST_EXPORT_URI = "last_export_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing ExportHistoryActivity");
        setContentView(R.layout.activity_export_history);

        setTitle("Export Turbine History");

        database = new TurbineDatabase(this);

        // Initialize SAF launcher
        createFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                Log.d(TAG, "createFileLauncher: SAF file URI received: " + uri);
                exportToCsvWithSAF(uri);
            } else {
                Log.w(TAG, "createFileLauncher: SAF file creation cancelled");
                Toast.makeText(this, "File creation cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        Button exportButton = findViewById(R.id.exportButton);
        if (exportButton == null) {
            Log.e(TAG, "onCreate: exportButton is null");
            return;
        }
        exportButton.setOnClickListener(v -> {
            Log.d(TAG, "exportButton: Clicked");
            checkStoragePermissions();
        });
    }

    private void checkStoragePermissions() {
        Log.d(TAG, "checkStoragePermissions: Checking storage permissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            // For Android 6.0 to 10, use legacy storage permissions
            boolean writePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean readPermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (!writePermission || !readPermission) {
                Log.d(TAG, "checkStoragePermissions: Permissions not granted (Write: " + writePermission + ", Read: " + readPermission + ")");
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d(TAG, "checkStoragePermissions: Showing permission rationale");
                    new AlertDialog.Builder(this)
                            .setTitle("Storage Permission Required")
                            .setMessage("This app needs storage access to export turbine history to a CSV file in the Downloads folder.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                Log.d(TAG, "checkStoragePermissions: User clicked OK on rationale dialog");
                                requestPermissions(new String[]{
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                                }, STORAGE_PERMISSION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Log.d(TAG, "checkStoragePermissions: User cancelled rationale dialog");
                                Toast.makeText(this, "Storage permission required to export", Toast.LENGTH_SHORT).show();
                            })
                            .show();
                } else {
                    Log.d(TAG, "checkStoragePermissions: Requesting permissions directly");
                    requestPermissions(new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }, STORAGE_PERMISSION_REQUEST_CODE);
                }
                return;
            }
            Log.d(TAG, "checkStoragePermissions: Legacy permissions granted, proceeding with export");
            exportToCsvLegacy();
        } else {
            // For Android 11+, use SAF
            Log.d(TAG, "checkStoragePermissions: Using SAF for Android 11+");
            startSAFExport();
        }
    }

    private void startSAFExport() {
        Log.d(TAG, "startSAFExport: Initiating SAF file creation");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String fileName = "TurbineHistory_" + sdf.format(new Date()) + ".csv";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        createFileLauncher.launch(intent);
    }

    private void exportToCsvLegacy() {
        Log.d(TAG, "exportToCsvLegacy: Starting legacy CSV export");
        try {
            List<TurbineData> allHistory = database.getAllTurbineHistory();
            Log.d(TAG, "exportToCsvLegacy: Retrieved " + allHistory.size() + " history records");
            if (allHistory.isEmpty()) {
                Toast.makeText(this, "No history data to export", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "exportToCsvLegacy: No history data available");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String fileName = "TurbineHistory_" + sdf.format(new Date()) + ".csv";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                Log.w(TAG, "exportToCsvLegacy: Downloads directory does not exist, attempting to create");
                if (!downloadsDir.mkdirs()) {
                    Log.e(TAG, "exportToCsvLegacy: Failed to create Downloads directory");
                    Toast.makeText(this, "Failed to create Downloads directory", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            File file = new File(downloadsDir, fileName);
            Log.d(TAG, "exportToCsvLegacy: Writing to file: " + file.getAbsolutePath());

            FileWriter writer = new FileWriter(file);
            writer.append("Turbine ID,Timestamp,RPM,Temperature (°C),Fuel Consumption (L/h),Efficiency (%)\n");

            for (TurbineData data : allHistory) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(data.getTimestamp()));
                writer.append(String.format("%s,%s,%d,%.1f,%.1f,%.1f\n",
                        data.getTurbineId(), timestamp, data.getRpm(), data.getTemperature(),
                        data.getFuelConsumption(), data.getEfficiency()));
            }

            writer.flush();
            writer.close();

            Toast.makeText(this, "History exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "exportToCsvLegacy: Export successful");
        } catch (Exception e) {
            Toast.makeText(this, "Error exporting history: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "exportToCsvLegacy: Export failed", e);
        }
    }

    private void exportToCsvWithSAF(Uri uri) {
        Log.d(TAG, "exportToCsvWithSAF: Starting SAF CSV export");
        try {
            List<TurbineData> allHistory = database.getAllTurbineHistory();
            Log.d(TAG, "exportToCsvWithSAF: Retrieved " + allHistory.size() + " history records");
            if (allHistory.isEmpty()) {
                Toast.makeText(this, "No history data to export", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "exportToCsvWithSAF: No history data available");
                return;
            }

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Log.e(TAG, "exportToCsvWithSAF: Failed to open output stream");
                Toast.makeText(this, "Failed to open file for writing", Toast.LENGTH_LONG).show();
                return;
            }

            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Turbine ID,Timestamp,RPM,Temperature (°C),Fuel Consumption (L/h),Efficiency (%)\n");

            for (TurbineData data : allHistory) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(data.getTimestamp()));
                csvContent.append(String.format("%s,%s,%d,%.1f,%.1f,%.1f\n",
                        data.getTurbineId(), timestamp, data.getRpm(), data.getTemperature(),
                        data.getFuelConsumption(), data.getEfficiency()));
            }

            outputStream.write(csvContent.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // Save URI to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putString(KEY_LAST_EXPORT_URI, uri.toString()).apply();
            Log.d(TAG, "exportToCsvWithSAF: Saved URI to SharedPreferences: " + uri);

            Toast.makeText(this, "History exported successfully", Toast.LENGTH_LONG).show();
            Log.d(TAG, "exportToCsvWithSAF: Export successful");
        } catch (Exception e) {
            Toast.makeText(this, "Error exporting history: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "exportToCsvWithSAF: Export failed", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode + ", grantResults=" + grantResults.length);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d(TAG, "onRequestPermissionsResult: All permissions granted, retrying export");
                exportToCsvLegacy();
            } else {
                Log.w(TAG, "onRequestPermissionsResult: Permissions denied");
                if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        !shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d(TAG, "onRequestPermissionsResult: Permissions permanently denied, prompting settings");
                    new AlertDialog.Builder(this)
                            .setTitle("Permissions Denied")
                            .setMessage("Storage permissions are required to export history. Please enable them in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                Log.d(TAG, "onRequestPermissionsResult: Opening app settings");
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Log.d(TAG, "onRequestPermissionsResult: User cancelled settings prompt");
                                Toast.makeText(this, "Storage permissions denied", Toast.LENGTH_LONG).show();
                            })
                            .show();
                } else {
                    Toast.makeText(this, "Storage permissions denied. Cannot export history.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Log.w(TAG, "onRequestPermissionsResult: Invalid request code or no results");
        }
    }
}