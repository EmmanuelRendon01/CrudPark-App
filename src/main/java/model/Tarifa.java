package model;

public class Tarifa {
    private int id;
    private String description;
    private double valuePerHour;
    private double valuePerFraction;
    private double dailyTop;
    private int gracePeriodMinutes;
    private boolean isActive;

    // --- Getters and Setters for all fields ---
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
