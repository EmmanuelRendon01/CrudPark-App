package model;

import java.sql.Timestamp;

public class Pago {
    private int id;
    private int estanciaId;
    private double amount;
    private Timestamp paymentDate;
    private String paymentMethod;
    private int operatorId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstanciaId() {
        return estanciaId;
    }

    public void setEstanciaId(int estanciaId) {
        this.estanciaId = estanciaId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }
}
