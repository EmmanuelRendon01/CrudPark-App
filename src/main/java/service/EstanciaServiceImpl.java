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
    public Estancia registerVehicleEntry(String plate, String vehicleType, int operatorId) throws Exception {
        // Business Rule 1: A vehicle cannot enter if it's already inside.
        if (estanciaRepository.findActiveByPlate(plate).isPresent()) {
            throw new Exception("El vehículo con placa " + plate + " ya se encuentra dentro del parqueadero.");
        }

        // Business Rule 2: Check for an active monthly plan.
        boolean hasMonthlyPlan = mensualidadRepository.isCurrentlyActive(plate).orElse(false);
        String stayType = hasMonthlyPlan ? "Membership" : "Guest";

        // Create the new Estancia record
        Estancia newEstancia = new Estancia();
        newEstancia.setLicense_plate(plate);
        newEstancia.setEntryDate(new Timestamp(System.currentTimeMillis()));
        newEstancia.setStayType(stayType);
        newEstancia.setStatus("INSIDE");
        newEstancia.setVehicleType(vehicleType);
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
        // 1. Obtener la estancia (ahora incluirá el vehicleType gracias al Paso 2)
        Estancia estancia = estanciaRepository.findActiveByPlate(plate)
                .orElseThrow(() -> new Exception("Vehículo con placa " + plate + " no encontrado o ya ha salido."));

        // 2. Si es miembro, no se cobra. Esta lógica no cambia.
        if ("Membership".equals(estancia.getStayType())) {
            estancia.setAmountToPay(0.0);
            return estancia;
        }

        // --- INICIO DE LA REFACTORIZACIÓN ---

        // 3. Obtener el tipo de vehículo desde el objeto estancia.
        String vehicleType = estancia.getVehicleType();
        if (vehicleType == null || vehicleType.isEmpty()) {
            throw new Exception("No se pudo determinar el tipo de vehículo para la placa " + plate + ".");
        }

        // 4. Buscar la tarifa activa CORRECTA usando el tipo de vehículo.
        //    Usamos el nuevo método del repositorio de tarifas.
        Tarifa tarifa = tarifaRepository.findActiveByVehicleType(vehicleType)
                .orElseThrow(() -> new Exception("No hay una tarifa activa para el tipo de vehículo: " + vehicleType));

        // --- FIN DE LA REFACTORIZACIÓN ---


        // 5. El resto de la lógica de cálculo permanece EXACTAMENTE IGUAL,
        //    porque ahora opera sobre la tarifa correcta (sea de carro o moto).
        long durationMillis = System.currentTimeMillis() - estancia.getEntryDate().getTime();
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        if (durationMinutes <= tarifa.getGracePeriodMinutes()) {
            estancia.setAmountToPay(0.0);
            return estancia;
        }

        long hours = TimeUnit.MINUTES.toHours(durationMinutes);
        long remainingMinutes = durationMinutes % 60;

        double totalCost = hours * tarifa.getValuePerHour();
        if (remainingMinutes > 0) {
            // Lógica mejorada: Redondear hacia arriba para las fracciones.
            // Si una hora son 4 fracciones de 15min, podemos calcularlo así:
            long fractions = (long) Math.ceil(remainingMinutes / 15.0); // Asumiendo fracciones de 15 min
            totalCost += fractions * tarifa.getValuePerFraction();
        }

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
            pago.setEstanciaId(estancia.getStay_id());
            pago.setAmount(amountPaid);
            pago.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            pago.setPaymentMethod(paymentMethod);
            pago.setOperatorId(operatorId);
            pagoRepository.save(pago);
        }

        estancia.setExitDate(new Timestamp(System.currentTimeMillis()));
        estancia.setStatus("OUTSIDE");
        estancia.setExitOperatorId(operatorId);
        estanciaRepository.update(estancia);
    }
}
