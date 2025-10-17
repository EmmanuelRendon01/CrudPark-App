package dao.implementation;

import config.DatabaseConnection;
import dao.repository.OperatorRepository;
import model.Operator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementación JDBC de la interfaz OperatorRepository.
 * Maneja todas las operaciones de base de datos para la entidad Operator.
 * NOTA: Esta clase asume que el objeto 'model.Operator' tiene los setters correspondientes
 * (ej. setId, setPassword, setFullName, setEmail, setActive).
 */
public class OperatorRepositoryImpl implements OperatorRepository {

    /**
     * Encuentra un operador por su email consultando la base de datos.
     * Este método ha sido actualizado para usar la nueva tabla 'operators'.
     *
     * @param email El email del operador a buscar.
     * @return Un Optional<Operator> con los datos del usuario si se encuentra, de lo contrario, estará vacío.
     */
    @Override
    public Optional<Operator> findByEmail(String email) {
        // La consulta SQL para seleccionar el operador de la tabla 'operators'.
        String sql = "SELECT operator_id, password_hash, full_name, email, is_active FROM operators WHERE email = ?";

        // Usando try-with-resources para asegurar que los recursos de la base de datos se cierren automáticamente.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Establecer el parámetro de email en la consulta para prevenir inyección SQL.
            pstmt.setString(1, email);

            // Ejecutar la consulta y obtener el conjunto de resultados.
            try (ResultSet rs = pstmt.executeQuery()) {
                // Si se encuentra un registro, se mapea a un objeto Operator.
                if (rs.next()) {
                    Operator operator = new Operator();
                    operator.setId(rs.getInt("operator_id"));
                    // Se asume que el setter setPassword puede manejar el hash.
                    operator.setPassword(rs.getString("password_hash"));
                    operator.setFullName(rs.getString("full_name"));
                    operator.setEmail(rs.getString("email"));
                    operator.setActive(rs.getBoolean("is_active"));

                    // Retorna el operador envuelto en un Optional.
                    return Optional.of(operator);
                }
            }
        } catch (SQLException e) {
            // En caso de un error de base de datos, imprime el stack trace.
            // En una aplicación de producción se debería usar un sistema de logging más robusto.
            e.printStackTrace();
        }

        // Si no se encontró ningún operador o si ocurrió un error, retorna un Optional vacío.
        return Optional.empty();
    }

    /**
     * NOTA: El método original findByUsername se deja aquí como referencia,
     * pero debería ser eliminado o adaptado si ya no se necesita.
     *
     * @param username The username to search for.
     * @return An Optional<Operator> with the user's data if found, otherwise empty.
     */
    @Override
    public Optional<Operator> findByUsername(String username) {
        // Este método necesitaría ser actualizado o eliminado según los nuevos requisitos.
        // Por ahora, retorna un Optional vacío para evitar errores de compilación si la interfaz lo requiere.
        return Optional.empty();
    }
}