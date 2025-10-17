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
 * JDBC implementation of the IOperatorRepository interface.
 * It handles all the database operations for the Operator entity.
 */
public class OperatorRepositoryImpl implements OperatorRepository {

    /**
     * Finds an operator by their username by querying the database.
     *
     * @param username The username to search for.
     * @return An Optional<Operator> with the user's data if found, otherwise empty.
     */
    @Override
    public Optional<Operator> findByUsername(String username) {
        // The SQL query to select the operator from the 'Operadores' table.
        String sql = "SELECT id_operador, nombre_usuario, contrasena, nombre_completo, correo, activo FROM Operadores WHERE nombre_usuario = ?";

        // Using try-with-resources to ensure database resources are closed automatically.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the username parameter in the query to prevent SQL injection.
            pstmt.setString(1, username);

            // Execute the query and get the result set.
            try (ResultSet rs = pstmt.executeQuery()) {
                // If a record is found, map it to an Operator object.
                if (rs.next()) {
                    Operator operator = new Operator();
                    operator.setId(rs.getInt("id_operador"));
                    operator.setUsername(rs.getString("nombre_usuario"));
                    operator.setPassword(rs.getString("contrasena")); // Retrieving the password hash.
                    operator.setFullName(rs.getString("nombre_completo"));
                    operator.setEmail(rs.getString("correo"));
                    operator.setActive(rs.getBoolean("activo"));

                    // Return the operator wrapped in an Optional.
                    return Optional.of(operator);
                }
            }
        } catch (SQLException e) {
            // In case of a database error, print the stack trace.
            // A more robust error handling mechanism (like logging) should be used in a production app.
            e.printStackTrace();
        }

        // If no operator was found or an error occurred, return an empty Optional.
        return Optional.empty();
    }
}
