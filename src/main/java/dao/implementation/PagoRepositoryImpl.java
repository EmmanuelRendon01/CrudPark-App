package dao.implementation;

import config.DatabaseConnection;
import dao.repository.IPagoRepository;
import model.Pago;
import java.sql.*;

/**
 * Implementación JDBC de la interfaz IPagoRepository.
 * Esta clase ha sido actualizada para funcionar con la nueva tabla 'payments'.
 * SUGERENCIA: Considera renombrar esta clase a 'PaymentRepositoryImpl' para mayor consistencia.
 */
public class PagoRepositoryImpl implements IPagoRepository {

    /**
     * Guarda un nuevo registro de pago en la base de datos.
     * @param pago El objeto Pago que contiene la información a guardar.
     */
    @Override
    public void save(Pago pago) {
        // Consulta SQL actualizada para insertar en la tabla 'payments' con las nuevas columnas.
        String sql = "INSERT INTO payments (stay_id, amount, payment_timestamp, payment_method, operator_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Asignar los valores del objeto 'pago' a los parámetros de la consulta.
            // Los métodos 'get' del modelo 'Pago' se mapean a las nuevas columnas.
            pstmt.setInt(1, pago.getEstanciaId());      // Mapea a stay_id
            pstmt.setDouble(2, pago.getAmount());       // Mapea a amount
            pstmt.setTimestamp(3, pago.getPaymentDate()); // Mapea a payment_timestamp
            pstmt.setString(4, pago.getPaymentMethod());  // Mapea a payment_method
            pstmt.setInt(5, pago.getOperatorId());      // Mapea a operator_id

            pstmt.executeUpdate();

        } catch (SQLException e) {
            // En una aplicación real, sería ideal loggear el error o lanzar una excepción personalizada.
            e.printStackTrace();
        }
    }
}