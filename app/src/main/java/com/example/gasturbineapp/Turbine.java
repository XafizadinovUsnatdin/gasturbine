package com.example.gasturbineapp;

public class Turbine {
    private String id;
    private String name;
    private String status;
    private int rpm;
    private double temperature;
    private double pressure;
    private double fuelConsumption;
    private double efficiency;
    private int maxRpm;

    public Turbine(String id, String name, String status, int rpm, double temperature, double pressure,
                   double fuelConsumption, double efficiency, int maxRpm) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.rpm = rpm;
        this.temperature = temperature;
        this.pressure = pressure;
        this.fuelConsumption = fuelConsumption;
        this.efficiency = efficiency;
        this.maxRpm = maxRpm;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public double getPressure() {
        return pressure;
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

    public int getMaxRpm() {
        return maxRpm;
    }

    public void setMaxRpm(int maxRpm) {
        this.maxRpm = maxRpm;
    }
}