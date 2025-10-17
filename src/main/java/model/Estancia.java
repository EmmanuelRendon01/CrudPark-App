package model;

import java.sql.Timestamp;

public class Estancia {

    // --- Persistent Fields (Mapped to DB columns) ---
    private int stay_id;
    private String license_plate;
    private Timestamp entryDate;
    private Timestamp exitDate;
    private String stayType; // "Membership" or "Guest"
    private String status;   // "INSIDE" or "OUTSIDE"
    private int entryOperatorId;
    private Integer exitOperatorId;

    // --- NUEVO CAMPO AÑADIDO ---
    private String vehicleType; // "Carro" o "Moto"

    // --- Transient Field (For in-memory calculations, not in DB) ---
    private double amountToPay;

    // Getters and Setters for all fields...
    // (Asegúrate de añadir el getter y setter para el nuevo campo)

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    // ... (El resto de tus getters y setters existentes) ...
    public int getStay_id() { return stay_id; }
    public void setStay_id(int stay_id) { this.stay_id = stay_id; }
    public String getLicense_plate() { return license_plate; }
    public void setLicense_plate(String license_plate) { this.license_plate = license_plate; }
    public Timestamp getEntryDate() { return entryDate; }
    public void setEntryDate(Timestamp entryDate) { this.entryDate = entryDate; }
    public Timestamp getExitDate() { return exitDate; }
    public void setExitDate(Timestamp exitDate) { this.exitDate = exitDate; }
    public String getStayType() { return stayType; }
    public void setStayType(String stayType) { this.stayType = stayType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getEntryOperatorId() { return entryOperatorId; }
    public void setEntryOperatorId(int entryOperatorId) { this.entryOperatorId = entryOperatorId; }
    public Integer getExitOperatorId() { return exitOperatorId; }
    public void setExitOperatorId(Integer exitOperatorId) { this.exitOperatorId = exitOperatorId; }
    public double getAmountToPay() { return amountToPay; }
    public void setAmountToPay(double amountToPay) { this.amountToPay = amountToPay; }
}