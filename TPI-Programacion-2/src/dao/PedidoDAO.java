package dao;

import config.DatabaseConnection;
import entities.Envio;
import entities.EstadoDePedido;
import entities.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO implements GenericDAO<Pedido> {

    private static final String INSERT_SQL
            = "INSERT INTO pedidos (numero,fecha,clienteNombre,total,estado,envio) VALUES (?,?,?,?,?,?)";

    private static final String UPDATE_SQL
            = "UPDATE pedidos SET numero=?, fecha=?, clienteNombre=?, total=?, estado=?, envio=? WHERE id=?";

    private static final String DELETE_SQL
            = "UPDATE pedidos SET eliminado = TRUE WHERE id = ?";

    private static final String SELECT_BY_ID_SQL
            = "SELECT * FROM pedidos WHERE id = ? AND eliminado = FALSE";

    private static final String SELECT_ALL_SQL
            = "SELECT * FROM pedidos WHERE eliminado = FALSE";

    public PedidoDAO() {
    }

    @Override
    public void save(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setPedidoValues(stmt, pedido);
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido); // este helper lo definimos igual que en EnvioDAO
        }
    }

    @Override
    public void saveTx(Pedido pedido, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setPedidoValues(stmt, pedido);
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }

    @Override
    public Pedido findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPedido(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Pedido> findAll() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
        }

        return pedidos;
    }

    @Override
    public void update(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, pedido.getNumero());
            stmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
            stmt.setString(3, pedido.getClienteNombre());
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado().name());
            stmt.setInt(6, pedido.getEnvio().getId());
            stmt.setInt(7, pedido.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el pedido con ID: " + pedido.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id);
            }
        }
    }

    // Orden: numero, fecha, clienteNombre, total, estado, envio
    private void setPedidoValues(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setString(1, pedido.getNumero());
        stmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
        stmt.setString(3, pedido.getClienteNombre());
        stmt.setDouble(4, pedido.getTotal());
        stmt.setString(5, pedido.getEstado().name());
        stmt.setInt(6, pedido.getEnvio().getId());
    }

    private void setGeneratedId(PreparedStatement stmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("La inserción del pedido falló, no se obtuvo ID generado.");
            }
        }
    }

    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {

        int envioId = rs.getInt("envio");
        Envio envio = new EnvioDAO().findById(envioId);
        return new Pedido(
                rs.getInt("id"),
                rs.getString("numero"),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("clienteNombre"),
                rs.getDouble("total"),
                EstadoDePedido.valueOf(rs.getString("estado")),
                envio
        );
    }

}
