package model;

import java.sql.Timestamp;

/**
 * Represents the Estancia (Stay) entity.
 * Maps to the 'Estancias' table in the database.
 */
public class Estancia {

    // --- Persistent Fields (Mapped to DB columns) ---
    private int id;
    private String plate;
    private Timestamp entryDate;
    private Timestamp exitDate;
    private String stayType; // "Mensualidad" or "Invitado"
    private String status;   // "DENTRO" or "FUERA"
    private int entryOperatorId;
    private Integer exitOperatorId; // Use Integer to allow null

    // --- Transient Field (For in-memory calculations, not in DB) ---
    private double amountToPay;

    // Getters and Setters for all fields

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }
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

    // --- Getters and Setters for the Transient Field ---
    public double getAmountToPay() { return amountToPay; }
    public void setAmountToPay(double amountToPay) { this.amountToPay = amountToPay; }
}