package dao.implementation;

import config.DatabaseConnection;
import dao.repository.ITarifaRepository;
import model.Tarifa;
import java.sql.*;
import java.util.Optional;

public class TarifaRepositoryImpl implements ITarifaRepository {
    @Override
    public Optional<Tarifa> findActiveTariff() {
        String sql = "SELECT * FROM Tarifas WHERE es_activa = true LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Tarifa tarifa = new Tarifa();
                tarifa.setId(rs.getInt("id_tarifa"));
                tarifa.setDescription(rs.getString("descripcion"));
                tarifa.setValuePerHour(rs.getDouble("valor_hora"));
                tarifa.setValuePerFraction(rs.getDouble("valor_fraccion"));
                tarifa.setDailyTop(rs.getDouble("tope_diario"));
                tarifa.setGracePeriodMinutes(rs.getInt("tiempo_gracia_minutos"));
                tarifa.setActive(rs.getBoolean("es_activa"));
                return Optional.of(tarifa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
