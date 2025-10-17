package dao.implementation;

import config.DatabaseConnection;
import dao.repository.IEstanciaRepository;
import model.Estancia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC de la interfaz IEstanciaRepository.
 */
public class EstanciaRepositoryImpl implements IEstanciaRepository {

    @Override
    public Estancia save(Estancia estancia) {
        // 1. AÑADIR LA COLUMNA 'vehicle_type' a la consulta SQL y un nuevo '?'
        String sql = "INSERT INTO stays (license_plate, entry_timestamp, stay_type, status, entry_operator_id, vehicle_type) VALUES (?, ?, ?::stay_type_enum, ?::stay_status_enum, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Los índices de los parámetros no cambian hasta el final
            pstmt.setString(1, estancia.getLicense_plate());
            pstmt.setTimestamp(2, estancia.getEntryDate());
            pstmt.setString(3, estancia.getStayType());
            pstmt.setString(4, estancia.getStatus());
            pstmt.setInt(5, estancia.getEntryOperatorId());

            pstmt.setString(6, estancia.getVehicleType());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    estancia.setStay_id(generatedKeys.getInt(1));
                }
            }
            return estancia;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca una estancia activa por la placa del vehículo.
     * @param plate La placa a buscar.
     * @return Un Optional<Estancia> si se encuentra una estancia activa, de lo contrario, vacío.
     */
    @Override
    public Optional<Estancia> findActiveByPlate(String plate) {
        // Consulta actualizada para la tabla 'stays' y el nuevo estado 'INSIDE'
        String sql = "SELECT * FROM stays WHERE license_plate = ? AND status = 'INSIDE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Estancia estancia = new Estancia();
                    estancia.setStay_id(rs.getInt("stay_id"));
                    estancia.setLicense_plate(rs.getString("license_plate"));
                    estancia.setEntryDate(rs.getTimestamp("entry_timestamp"));

                    // --- LÍNEA A AÑADIR/MODIFICAR ---
                    // Asumiendo que tu tabla 'stays' ahora tiene una columna 'vehicle_type'
                    estancia.setVehicleType(rs.getString("vehicle_type"));

                    estancia.setStayType(rs.getString("stay_type"));
                    estancia.setStatus(rs.getString("status"));
                    estancia.setEntryOperatorId(rs.getInt("entry_operator_id"));
                    return Optional.of(estancia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Devuelve una lista de todas las estancias actualmente activas.
     * @return Una lista de objetos Estancia.
     */
    @Override
    public List<Estancia> findAllActive() {
        List<Estancia> activeStays = new ArrayList<>();
        // Consulta actualizada para ordenar por 'entry_timestamp' y filtrar por estado 'INSIDE'
        String sql = "SELECT * FROM stays WHERE status = 'INSIDE' ORDER BY entry_timestamp ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Estancia estancia = new Estancia();
                estancia.setStay_id(rs.getInt("stay_id"));
                estancia.setLicense_plate(rs.getString("license_plate"));
                estancia.setEntryDate(rs.getTimestamp("entry_timestamp"));
                estancia.setStayType(rs.getString("stay_type"));
                estancia.setStatus(rs.getString("status"));
                estancia.setEntryOperatorId(rs.getInt("entry_operator_id"));
                // No es necesario mapear los campos de salida ya que el estado es 'INSIDE'
                activeStays.add(estancia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeStays;
    }

    @Override
    public void update(Estancia estancia) {
        // --- LA SOLUCIÓN: Añadir el cast '::stay_status_enum' al parámetro del estado ---
        String sql = "UPDATE stays SET exit_timestamp = ?, status = ?::stay_status_enum, exit_operator_id = ? WHERE stay_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, estancia.getExitDate());

            // Aunque el cast está en el SQL, aquí seguimos enviando un String.
            // PostgreSQL se encargará de la conversión gracias al cast.
            pstmt.setString(2, estancia.getStatus());

            // Manejar el caso de que el ID del operador de salida pueda ser nulo
            if (estancia.getExitOperatorId() != null && estancia.getExitOperatorId() > 0) {
                pstmt.setInt(3, estancia.getExitOperatorId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }

            pstmt.setInt(4, estancia.getStay_id());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // En una aplicación real, sería bueno lanzar una excepción aquí para que la capa de servicio sepa que algo falló.
        }
    }
}