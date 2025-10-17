package service;

import dao.implementation.EstanciaRepositoryImpl;
import dao.implementation.MensualidadRepositoryImpl;
import dao.implementation.PagoRepositoryImpl;
import dao.implementation.TarifaRepositoryImpl;
import dao.repository.*;
import model.Estancia;
import model.Pago;
import model.Tarifa;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EstanciaServiceImpl implements IEstanciaService {

    private final IEstanciaRepository estanciaRepository;
    private final IMensualidadRepository mensualidadRepository;
    private final ITarifaRepository tarifaRepository;
    private final IPagoRepository pagoRepository;

    public EstanciaServiceImpl() {
        this.estanciaRepository = new EstanciaRepositoryImpl();
        this.mensualidadRepository = new MensualidadRepositoryImpl();
        this.tarifaRepository = new TarifaRepositoryImpl();
        this.pagoRepository = new PagoRepositoryImpl();
    }

    @Override
    public Estancia registerVehicleEntry(String plate, int operatorId) throws Exception {
        // Business Rule 1: A vehicle cannot enter if it's already inside.
        if (estanciaRepository.findActiveByPlate(plate).isPresent()) {
            throw new Exception("El vehículo con placa " + plate + " ya se encuentra dentro del parqueadero.");
        }

        // Business Rule 2: Check for an active monthly plan.
        boolean hasMonthlyPlan = mensualidadRepository.isCurrentlyActive(plate).orElse(false);
        String stayType = hasMonthlyPlan ? "Mensualidad" : "Invitado";

        // Create the new Estancia record
        Estancia newEstancia = new Estancia();
        newEstancia.setPlate(plate);
        newEstancia.setEntryDate(new Timestamp(System.currentTimeMillis()));
        newEstancia.setStayType(stayType);
        newEstancia.setStatus("DENTRO");
        newEstancia.setEntryOperatorId(operatorId);

        return estanciaRepository.save(newEstancia);
    }

    @Override
    public List<Estancia> getActiveStays() {
        // Por ahora, solo pasamos la llamada al repositorio.
        // En el futuro, podríamos añadir lógica de negocio aquí si fuera necesario.
        return estanciaRepository.findAllActive();
    }

    @Override
    public Estancia calculateExitDetails(String plate) throws Exception {
        Estancia estancia = estanciaRepository.findActiveByPlate(plate)
                .orElseThrow(() -> new Exception("Vehículo con placa " + plate + " no encontrado o ya ha salido."));

        // If it's a monthly member, no calculation is needed.
        if ("Mensualidad".equals(estancia.getStayType())) {
            estancia.setAmountToPay(0.0); // We'll add this transient field to Estancia model
            return estancia;
        }

        Tarifa tarifa = tarifaRepository.findActiveTariff()
                .orElseThrow(() -> new Exception("No hay una tarifa activa configurada en el sistema."));

        long durationMillis = System.currentTimeMillis() - estancia.getEntryDate().getTime();
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        if (durationMinutes <= tarifa.getGracePeriodMinutes()) {
            estancia.setAmountToPay(0.0);
            return estancia;
        }

        // --- Basic Pricing Logic ---
        long hours = TimeUnit.MINUTES.toHours(durationMinutes);
        long remainingMinutes = durationMinutes % 60;

        double totalCost = hours * tarifa.getValuePerHour();
        if (remainingMinutes > 0) {
            totalCost += tarifa.getValuePerFraction(); // Simplified: any fraction costs the fraction value
        }

        // Check against daily top
        if (tarifa.getDailyTop() > 0 && totalCost > tarifa.getDailyTop()) {
            totalCost = tarifa.getDailyTop();
        }

        estancia.setAmountToPay(totalCost);
        return estancia;
    }

    @Override
    public void finalizeExit(Estancia estancia, int operatorId, double amountPaid, String paymentMethod) throws Exception {
        if (amountPaid > 0) {
            Pago pago = new Pago();
            pago.setEstanciaId(estancia.getId());
            pago.setAmount(amountPaid);
            pago.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            pago.setPaymentMethod(paymentMethod);
            pago.setOperatorId(operatorId);
            pagoRepository.save(pago);
        }

        estancia.setExitDate(new Timestamp(System.currentTimeMillis()));
        estancia.setStatus("FUERA");
        estancia.setExitOperatorId(operatorId);
        estanciaRepository.update(estancia);
    }
}
