package dao.repository;

import model.Tarifa;
import java.util.Optional;

public interface ITarifaRepository {
    Optional<Tarifa> findActiveTariff();
    Optional<Tarifa> findActiveByVehicleType(String vehicleType);
}
