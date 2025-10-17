package dao.repository;


import model.Estancia;

import java.util.List;

public interface IEstanciaService {
    /**
     * Registers a new vehicle entry.
     * @param plate The vehicle's plate.
     * @param operatorId The ID of the operator registering the entry.
     * @return The newly created Estancia record.
     * @throws Exception if the vehicle is already inside or another error occurs.
     */
    public Estancia registerVehicleEntry(String plate, String vehicleType, int operatorId) throws Exception;

    /**
     * Retrieves all vehicles currently inside the parking.
     * @return A list of active Estancia objects.
     */
    List<Estancia> getActiveStays();

    Estancia calculateExitDetails(String plate) throws Exception;
    void finalizeExit(Estancia estancia, int operatorId, double amountPaid, String paymentMethod) throws Exception;
}
