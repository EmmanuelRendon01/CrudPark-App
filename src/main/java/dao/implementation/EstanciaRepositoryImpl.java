package dao.implementation;


import config.DatabaseConnection;
import dao.repository.IEstanciaRepository;
import model.Estancia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstanciaRepositoryImpl implements IEstanciaRepository {

    @Override
    public Estancia save(Estancia estancia) {
        String sql = "INSERT INTO Estancias (placa, fecha_ingreso, tipo_estancia, estado, id_operador_ingreso) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, estancia.getPlate());
            pstmt.setTimestamp(2, estancia.getEntryDate());
            pstmt.setString(3, estancia.getStayType());
            pstmt.setString(4, estancia.getStatus());
            pstmt.setInt(5, estancia.getEntryOperatorId());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    estancia.setId(generatedKeys.getInt(1));
                }
            }
            return estancia;
        } catch (SQLException e) {
            e.printStackTrace();
            // In a real app, you'd throw a custom exception
            return null;
        }
    }

    @Override
    public Optional<Estancia> findActiveByPlate(String plate) {
        String sql = "SELECT * FROM Estancias WHERE placa = ? AND estado = 'DENTRO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Estancia estancia = new Estancia();
                    estancia.setId(rs.getInt("id_estancia"));
                    estancia.setPlate(rs.getString("placa"));
                    estancia.setEntryDate(rs.getTimestamp("fecha_ingreso"));
                    estancia.setStayType(rs.getString("tipo_estancia"));
                    estancia.setStatus(rs.getString("estado"));
                    estancia.setEntryOperatorId(rs.getInt("id_operador_ingreso"));
                    return Optional.of(estancia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Estancia> findAllActive() {
        List<Estancia> activeStays = new ArrayList<>();
        // Ordenamos por fecha de ingreso para que la tabla se vea ordenada cronológicamente
        String sql = "SELECT * FROM Estancias WHERE estado = 'DENTRO' ORDER BY fecha_ingreso ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Estancia estancia = new Estancia();
                estancia.setId(rs.getInt("id_estancia"));
                estancia.setPlate(rs.getString("placa"));
                estancia.setEntryDate(rs.getTimestamp("fecha_ingreso"));
                estancia.setStayType(rs.getString("tipo_estancia"));
                estancia.setStatus(rs.getString("estado"));
                estancia.setEntryOperatorId(rs.getInt("id_operador_ingreso"));
                activeStays.add(estancia);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return activeStays;
    }

    @Override
    public void update(Estancia estancia) {
        String sql = "UPDATE Estancias SET fecha_salida = ?, estado = ?, id_operador_salida = ? WHERE id_estancia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, estancia.getExitDate());
            pstmt.setString(2, estancia.getStatus());
            pstmt.setInt(3, estancia.getExitOperatorId());
            pstmt.setInt(4, estancia.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}