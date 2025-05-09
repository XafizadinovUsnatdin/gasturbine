package com.example.gasturbineapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TurbineDatabase extends SQLiteOpenHelper {
    private static final String TAG = "TurbineDatabase";
    private static final String DATABASE_NAME = "turbine.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_HISTORY = "turbine_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TURBINE_ID = "turbine_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_RPM = "rpm";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_FUEL_CONSUMPTION = "fuel_consumption";
    private static final String COLUMN_EFFICIENCY = "efficiency";

    public TurbineDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "TurbineDatabase: Initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating database table");
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TURBINE_ID + " TEXT, " +
                COLUMN_TIMESTAMP + " INTEGER, " +
                COLUMN_RPM + " INTEGER, " +
                COLUMN_TEMPERATURE + " REAL, " +
                COLUMN_FUEL_CONSUMPTION + " REAL, " +
                COLUMN_EFFICIENCY + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void insertTurbineData(Turbine turbine) {
        Log.d(TAG, "insertTurbineData: Inserting data for turbine " + turbine.getId());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TURBINE_ID, turbine.getId());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_RPM, turbine.getRpm());
        values.put(COLUMN_TEMPERATURE, turbine.getTemperature());
        values.put(COLUMN_FUEL_CONSUMPTION, turbine.getFuelConsumption());
        values.put(COLUMN_EFFICIENCY, turbine.getEfficiency());
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public List<TurbineData> getTurbineHistory(String turbineId) {
        Log.d(TAG, "getTurbineHistory: Fetching history for turbine " + turbineId);
        List<TurbineData> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY,
                new String[]{COLUMN_TURBINE_ID, COLUMN_TIMESTAMP, COLUMN_RPM, COLUMN_TEMPERATURE,
                        COLUMN_FUEL_CONSUMPTION, COLUMN_EFFICIENCY},
                COLUMN_TURBINE_ID + " = ?",
                new String[]{turbineId},
                null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                TurbineData data = new TurbineData();
                data.setTurbineId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TURBINE_ID)));
                data.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                data.setRpm(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RPM)));
                data.setTemperature(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE)));
                data.setFuelConsumption(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FUEL_CONSUMPTION)));
                data.setEfficiency(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EFFICIENCY)));
                history.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getTurbineHistory: Retrieved " + history.size() + " records");
        return history;
    }

    public List<TurbineData> getAllTurbineHistory() {
        Log.d(TAG, "getAllTurbineHistory: Fetching all turbine history");
        List<TurbineData> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY,
                new String[]{COLUMN_TURBINE_ID, COLUMN_TIMESTAMP, COLUMN_RPM, COLUMN_TEMPERATURE,
                        COLUMN_FUEL_CONSUMPTION, COLUMN_EFFICIENCY},
                null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                TurbineData data = new TurbineData();
                data.setTurbineId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TURBINE_ID)));
                data.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                data.setRpm(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RPM)));
                data.setTemperature(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE)));
                data.setFuelConsumption(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FUEL_CONSUMPTION)));
                data.setEfficiency(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EFFICIENCY)));
                history.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "getAllTurbineHistory: Retrieved " + history.size() + " records");
        return history;
    }

    public void clearAllHistory() {
        Log.d(TAG, "clearAllHistory: Deleting all turbine history");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HISTORY);
        db.close();
        Log.d(TAG, "clearAllHistory: All history deleted");
    }
}