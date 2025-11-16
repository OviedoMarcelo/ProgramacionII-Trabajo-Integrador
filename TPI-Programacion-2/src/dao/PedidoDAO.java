package dao;

import config.DatabaseConnection;
import dao.GenericDAO;
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

    /**
     * SQL para insertar un nuevo pedido.
     */
    private static final String INSERT_SQL
            = "INSERT INTO pedidos (numero,fecha,clienteNombre,total,estado,envio) VALUES (?,?,?,?,?,?)";

    /**
     * SQL para actualizar un pedido existente.
     */
    private static final String UPDATE_SQL
            = "UPDATE pedidos SET numero=?, fecha=?, clienteNombre=?, total=?, estado=?, envio=? WHERE id=?";

    /**
     * SQL para eliminación lógica de un pedido.
     */
    private static final String DELETE_SQL
            = "UPDATE pedidos SET eliminado = TRUE WHERE id = ?";

    /**
     * SQL para búsqueda por ID.
     */
    private static final String SELECT_BY_ID_SQL
            = "SELECT * FROM pedidos WHERE id = ? AND eliminado = FALSE";

    /**
     * SQL para obtener todos los pedidos activos.
     */
    private static final String SELECT_ALL_SQL
            = "SELECT * FROM pedidos WHERE eliminado = FALSE";

    /**
     * Constructor por defecto del DAO.
     */
    public PedidoDAO() {
    }
/*
    public PedidoDAO(EnvioDAO envioDAO) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
*/
    /**
     * Guarda un pedido en la base de datos y asigna su ID generado.
     *
     * @param pedido objeto Pedido a insertar
     * @throws SQLException si ocurre un error en la operación SQL
     */
    @Override
    public void save(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setPedidoValues(stmt, pedido);
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }

    /**
     * Guarda un pedido dentro de una transacción ya iniciada.
     *
     * @param pedido pedido a guardar
     * @param conn conexión activa perteneciente a la transacción
     * @throws SQLException si ocurre un error en la operación SQL
     */
    @Override
    public void saveTx(Pedido pedido, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setPedidoValues(stmt, pedido);
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }

    /**
     * Busca un pedido por su ID.
     *
     * @param id identificador del pedido
     * @return el pedido encontrado o null si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
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

    /**
     * Obtiene todos los pedidos no eliminados.
     *
     * @return lista de pedidos activos
     * @throws SQLException si ocurre un error de acceso a datos
     */
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

    /**
     * Actualiza un pedido existente.
     *
     * @param pedido pedido actualizado
     * @throws SQLException si no se encontró el registro o ocurre un error SQL
     */
    @Override
    public void update(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, pedido.getNumero());
            stmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
            stmt.setString(3, pedido.getClienteNombre());
            stmt.setDouble(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado().name());
            stmt.setLong(6, pedido.getEnvio().getId());
            stmt.setLong(7, pedido.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el pedido con ID: " + pedido.getId());
            }
        }
    }

    /**
     * Elimina lógicamente un pedido por su ID.
     *
     * @param id identificador del pedido a eliminar
     * @throws SQLException si no existe o si ocurre un error SQL
     */
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

    /**
     * Busca un pedido por su número identificador único.
     *
     * @param numero número del pedido
     * @return pedido encontrado o null
     * @throws SQLException si ocurre un error SQL
     */
    public Pedido findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM pedidos WHERE numero = ? AND eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPedido(rs);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los pedidos realizados por un cliente específico.
     *
     * @param cliente nombre del cliente
     * @return lista de pedidos
     * @throws SQLException si ocurre un error SQL
     */
    public List<Pedido> findByCliente(String cliente) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedidos WHERE clienteNombre = ? AND eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapResultSetToPedido(rs));
                }
            }
        }
        return pedidos;
    }

    /**
     * Cuenta cuántos pedidos están activos (no eliminados).
     *
     * @return número de pedidos activos
     * @throws SQLException si ocurre un error SQL
     */
    public long contarActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * Calcula la suma total del campo "total" de todos los pedidos activos.
     *
     * @return suma de los totales
     * @throws SQLException si ocurre un error SQL
     */
    public double sumarTotalActivos() throws SQLException {
        String sql = "SELECT SUM(total) FROM pedidos WHERE eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
    /**
     * Asigna los valores del pedido al PreparedStatement en el orden correcto.
     *
     * @param stmt PreparedStatement al que se le asignan los datos
     * @param pedido pedido cuyos datos se insertarán
     * @throws SQLException si ocurre un error al asignar valores
     */
    private void setPedidoValues(PreparedStatement stmt, Pedido pedido) throws SQLException {
        stmt.setString(1, pedido.getNumero());
        stmt.setDate(2, java.sql.Date.valueOf(pedido.getFecha()));
        stmt.setString(3, pedido.getClienteNombre());
        stmt.setDouble(4, pedido.getTotal());
        stmt.setString(5, pedido.getEstado().name());
        stmt.setLong(6, pedido.getEnvio().getId());
    }

    /**
     * Asigna el ID autogenerado al pedido luego de una inserción exitosa.
     *
     * @param stmt statement que contiene la clave generada
     * @param pedido instancia a la cual se asignará la clave
     * @throws SQLException si no se generó clave primaria
     */
    private void setGeneratedId(PreparedStatement stmt, Pedido pedido) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                pedido.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del pedido falló, no se obtuvo ID generado.");
            }
        }
    }

    /**
     * Mapea una fila de la tabla pedidos a un objeto Pedido.
     *
     * @param rs ResultSet posicionado en la fila actual
     * @return objeto Pedido mapeado
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {

        // Como la columna envio es NOT NULL, siempre debe haber un ID válido
        int envioId = rs.getInt("envio");
        Envio envio = new EnvioDAO().findById(envioId);

        // Por seguridad, por si la BD se desincroniza y no existe ese envio
        if (envio == null) {
            throw new SQLException("No se encontró un Envío con id = " + envioId
                    + " para el pedido id = " + rs.getInt("id"));
        }

        return new Pedido(
                rs.getLong("id"),
                rs.getString("numero"),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("clienteNombre"),
                EstadoDePedido.valueOf(rs.getString("estado")),
                envio,
                rs.getDouble("total")
        );

    }
}
