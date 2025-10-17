package dao.implementation;

import config.DatabaseConnection;
import dao.repository.IMensualidadRepository;

import java.sql.*;
import java.util.Optional;

public class MensualidadRepositoryImpl implements IMensualidadRepository {
    @Override
    public Optional<Boolean> isCurrentlyActive(String plate) {
        String sql = "SELECT 1 FROM Mensualidades WHERE placa = ? AND CURRENT_DATE BETWEEN fecha_inicio AND fecha_fin";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            try (ResultSet rs = pstmt.executeQuery()) {
                return Optional.of(rs.next()); // Returns true if a record is found, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty(); // Return empty in case of SQL error
        }
    }
}
