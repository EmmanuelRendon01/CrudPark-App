package dao.implementation;

import config.DatabaseConnection;
import dao.repository.IPagoRepository;
import model.Pago;
import java.sql.*;

public class PagoRepositoryImpl implements IPagoRepository {
    @Override
    public void save(Pago pago) {
        String sql = "INSERT INTO Pagos (id_estancia, monto, fecha_pago, metodo_pago, id_operador_cobro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pago.getEstanciaId());
                pstmt.setDouble(2, pago.getAmount());
                pstmt.setTimestamp(3, pago.getPaymentDate());
                pstmt.setString(4, pago.getPaymentMethod());
                pstmt.setInt(5, pago.getOperatorId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
