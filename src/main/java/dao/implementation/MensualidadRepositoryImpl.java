package dao.implementation;

import config.DatabaseConnection;
import dao.repository.IMensualidadRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementación JDBC de la interfaz IMensualidadRepository.
 * Esta clase ha sido actualizada para funcionar con la nueva tabla 'memberships'.
 * SUGERENCIA: Considera renombrar esta clase a 'MembershipRepositoryImpl' para mayor consistencia.
 */
public class MensualidadRepositoryImpl implements IMensualidadRepository {

    /**
     * Verifica si una membresía está actualmente activa para una placa específica.
     * Una membresía se considera activa si está marcada como 'is_active = true'
     * y la fecha actual se encuentra dentro del rango de start_date y end_date.
     *
     * @param plate La placa del vehículo a verificar.
     * @return Un Optional<Boolean> que es true si la membresía está activa, false si no,
     *         o un Optional vacío si ocurre un error de SQL.
     */
    @Override
    public Optional<Boolean> isCurrentlyActive(String plate) {
        // Consulta SQL actualizada para la tabla 'memberships'.
        // Verifica que la membresía esté marcada como activa y que estemos dentro del período de validez.
        String sql = "SELECT 1 FROM memberships WHERE license_plate = ? AND is_active = TRUE AND CURRENT_TIMESTAMP BETWEEN start_date AND end_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plate);

            try (ResultSet rs = pstmt.executeQuery()) {
                // rs.next() será true si la consulta devuelve al menos una fila (es decir, está activa),
                // y false si no devuelve ninguna.
                return Optional.of(rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Retorna un Optional vacío en caso de un error de base de datos.
            return Optional.empty();
        }
    }
}
