package com.example.gasturbineapp;

public class TurbineData {
    private String turbineId;
    private long timestamp;
    private int rpm;
    private double temperature;
    private double fuelConsumption;
    private double efficiency;

    public String getTurbineId() {
        return turbineId;
    }

    public void setTurbineId(String turbineId) {
        this.turbineId = turbineId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }
}