package model;

public class Tarifa {
    private int id;
    private String description;
    private double valuePerHour;
    private double valuePerFraction;
    private double dailyTop;
    private int gracePeriodMinutes;
    private boolean isActive;
    private String vehicle_type;


    // --- Getters and Setters for all fields ---
    public String getVehicle_type() {
        return vehicle_type;
    }
    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getValuePerHour() { return valuePerHour; }
    public void setValuePerHour(double valuePerHour) { this.valuePerHour = valuePerHour; }
    public double getValuePerFraction() { return valuePerFraction; }
    public void setValuePerFraction(double valuePerFraction) { this.valuePerFraction = valuePerFraction; }
    public double getDailyTop() { return dailyTop; }
    public void setDailyTop(double dailyTop) { this.dailyTop = dailyTop; }
    public int getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(int gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
