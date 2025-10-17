package dao.implementation;

import config.DatabaseConnection;
import dao.repository.ITarifaRepository;
import model.Tarifa;
import java.sql.*;
import java.util.Optional;

/**
 * Implementación JDBC de la interfaz ITarifaRepository.
 * Esta clase ha sido actualizada para funcionar con la nueva tabla 'rates'.
 * SUGERENCIA: Considera renombrar esta clase a 'RateRepositoryImpl' para mayor consistencia.
 */
public class TarifaRepositoryImpl implements ITarifaRepository {

    /**
     * Busca la tarifa activa para un tipo de vehículo específico.
     * La nueva tabla 'rates' permite diferentes tarifas por tipo de vehículo.
     *
     * @param vehicleType El tipo de vehículo para el cual se busca la tarifa (ej. "Coche", "Moto").
     * @return Un Optional<Tarifa> con los datos de la tarifa si se encuentra, de lo contrario, estará vacío.
     */
    @Override
    public Optional<Tarifa> findActiveByVehicleType(String vehicleType) {
        // Consulta SQL actualizada para la tabla 'rates', filtrando por estado y tipo de vehículo.
        String sql = "SELECT * FROM rates WHERE is_active = true AND vehicle_type = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignamos el parámetro del tipo de vehículo a la consulta.
            pstmt.setString(1, vehicleType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Tarifa tarifa = new Tarifa();
                    // Mapeo de las nuevas columnas al objeto Tarifa.
                    tarifa.setId(rs.getInt("rate_id"));
                    tarifa.setDescription(rs.getString("description"));

                    // NOTA: Asegúrate de que tu modelo 'Tarifa' tenga un campo para vehicle_type.
                    tarifa.setVehicle_type(rs.getString("vehicle_type"));

                    tarifa.setValuePerHour(rs.getDouble("hourly_rate"));
                    tarifa.setValuePerFraction(rs.getDouble("fraction_rate"));
                    tarifa.setDailyTop(rs.getDouble("daily_cap"));
                    tarifa.setGracePeriodMinutes(rs.getInt("grace_period_minutes"));
                    tarifa.setActive(rs.getBoolean("is_active"));

                    return Optional.of(tarifa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * NOTA: Este es el método original. Debería ser eliminado o adaptado
     * a la nueva lógica de 'findActiveByVehicleType'. Se deja como referencia.
     */
    @Override
    public Optional<Tarifa> findActiveTariff() {
        // Este método ya no es ideal porque no diferencia por tipo de vehículo.
        // Se recomienda usar findActiveByVehicleType en su lugar.
        // Podrías adaptarlo para que busque una tarifa por defecto si lo necesitas.
        return Optional.empty();
    }
}