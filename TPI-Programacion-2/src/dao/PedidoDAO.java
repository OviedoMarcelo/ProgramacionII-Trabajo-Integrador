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
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad {@link Pedido}. <br>
 *
 * Se encarga de realizar las operaciones de acceso a datos sobre la tabla
 * {@code pedidos}: inserción, actualización, borrado lógico y consultas
 * específicas. Implementa la interfaz {@link GenericDAO}.
 *
 * La tabla {@code pedidos} utiliza borrado lógico mediante la columna
 * {@code eliminado}, por lo que los métodos de consulta sólo devuelven
 * registros no eliminados.
 *
 * Además, este DAO resuelve la relación unidireccional 1:1 entre
 * {@link Pedido} y {@link Envio}, cargando el envío asociado para cada
 * pedido a partir de la clave foránea {@code envio}.
 *
 * @author Oviedo Marcelo
 * @date 14 nov 2025
 */
public class PedidoDAO implements GenericDAO<Pedido> {

    /** Sentencia SQL para insertar un nuevo pedido. */
    private static final String INSERT_SQL
            = "INSERT INTO pedidos (numero,fecha,clienteNombre,total,estado,envio) VALUES (?,?,?,?,?,?)";

    /** Sentencia SQL para actualizar un pedido existente. */
    private static final String UPDATE_SQL
            = "UPDATE pedidos SET numero=?, fecha=?, clienteNombre=?, total=?, estado=?, envio=? WHERE id=?";

    /** Sentencia SQL para realizar borrado lógico de un pedido. */
    private static final String DELETE_SQL
            = "UPDATE pedidos SET eliminado = TRUE WHERE id = ?";

    /** Sentencia SQL para buscar un pedido por ID (sólo no eliminados). */
    private static final String SELECT_BY_ID_SQL
            = "SELECT * FROM pedidos WHERE id = ? AND eliminado = FALSE";

    /** Sentencia SQL para obtener todos los pedidos no eliminados. */
    private static final String SELECT_ALL_SQL
            = "SELECT * FROM pedidos WHERE eliminado = FALSE";

    /**
     * Constructor por defecto. <br>
     * No realiza ninguna inicialización especial; se provee para cumplir
     * con las convenciones de los DAOs.
     */
    public PedidoDAO() {
    }

    /**
     * Guarda un nuevo pedido en la base de datos utilizando una conexión propia.
     * <br>
     * Este método crea la conexión, ejecuta el INSERT y asigna al objeto
     * {@link Pedido} el ID generado automáticamente por la base de datos.
     *
     * @param pedido pedido a persistir
     * @throws SQLException si ocurre un error al conectarse o al ejecutar la sentencia SQL
     */
    @Override
    public void save(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setPedidoValues(stmt, pedido);
            stmt.executeUpdate();
            setGeneratedId(stmt, pedido);
        }
    }

    /**
     * Guarda un nuevo pedido utilizando una conexión existente, por ejemplo
     * dentro de una transacción que agrupa varias operaciones (pedido + envío,
     * etc.). <br>
     * Este método no crea la conexión, sólo la reutiliza.
     *
     * @param pedido pedido a persistir
     * @param conn conexión a reutilizar dentro de un contexto transaccional
     * @throws SQLException si ocurre un error al preparar o ejecutar la sentencia SQL
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
     * Busca un pedido por su identificador, siempre que no esté marcado como
     * eliminado.
     *
     * @param id identificador del pedido
     * @return el pedido encontrado o {@code null} si no existe o está eliminado
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    @Override
    public Pedido findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

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
     * Obtiene todos los pedidos que no están marcados como eliminados.
     *
     * @return lista de pedidos activos
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    @Override
    public List<Pedido> findAll() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
        }

        return pedidos;
    }

    /**
     * Actualiza los datos de un pedido existente en la base de datos.
     *
     * @param pedido pedido con los datos actualizados; se utiliza su ID para
     *               identificar el registro a modificar
     * @throws SQLException si no se encuentra el pedido o si ocurre un error SQL
     */
    @Override
    public void update(Pedido pedido) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

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
     * Realiza un borrado lógico de un pedido, marcándolo como eliminado.
     *
     * @param id identificador del pedido a eliminar
     * @throws SQLException si no se encuentra el pedido o si ocurre un error SQL
     */
    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró pedido con ID: " + id);
            }
        }
    }

    /**
     * Obtiene todos los pedidos realizados por un cliente específico.
     *
     * @param cliente nombre del cliente (valor de la columna {@code clienteNombre})
     * @return lista de pedidos asociados a ese cliente; puede ser vacía si no hay resultados
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    public List<Pedido> findByClient(String cliente) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE clienteNombre = ? AND eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
     * Busca un pedido por su número identificador lógico (no el ID interno).
     *
     * @param numero número del pedido (columna {@code numero})
     * @return el pedido encontrado o {@code null} si no existe o está eliminado
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    public Pedido findByNumber(String numero) throws SQLException {
        String sql = "SELECT * FROM pedidos WHERE numero = ? AND eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
     * Cuenta cuántos pedidos están activos (no eliminados).
     *
     * @return cantidad de pedidos activos; 0 si no hay resultados
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    public long countActives() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * Calcula la suma total del campo {@code total} de todos los pedidos activos.
     *
     * @return suma de los valores de {@code total} de los pedidos no eliminados;
     *         0 si no hay registros
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    public double totalActivesValue() throws SQLException {
        String sql = "SELECT SUM(total) FROM pedidos WHERE eliminado = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares (helpers)
    // -------------------------------------------------------------------------

    /**
     * Asigna los valores del objeto {@link Pedido} a los parámetros del
     * {@link PreparedStatement} respetando el orden definido en {@link #INSERT_SQL}. <br>
     *
     * Orden de los parámetros: numero, fecha, clienteNombre, total, estado, envio.
     *
     * @param stmt sentencia preparada sobre la que se setearán los parámetros
     * @param pedido pedido cuyos datos se utilizarán
     * @throws SQLException si ocurre un error al setear alguno de los parámetros
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
     * Obtiene el ID generado automáticamente por la base de datos luego de un
     * INSERT y lo asigna al objeto {@link Pedido}. <br>
     * Requiere que el {@link PreparedStatement} haya sido creado con la
     * opción {@link Statement#RETURN_GENERATED_KEYS}.
     *
     * @param stmt sentencia preparada que ejecutó el INSERT
     * @param pedido pedido al que se le asignará el ID generado
     * @throws SQLException si no se obtiene ningún ID o si ocurre un error al
     *                      acceder al conjunto de claves generadas
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
     * Mapea la fila actual de un {@link ResultSet} a una instancia de
     * {@link Pedido}. <br>
     * Además de los datos propios del pedido, resuelve la clave foránea
     * {@code envio} cargando el {@link Envio} asociado mediante {@link EnvioDAO}.
     *
     * @param rs resultado de la consulta posicionado en una fila válida
     * @return una instancia de {@link Pedido} construida a partir de la fila actual
     * @throws SQLException si ocurre un error al leer las columnas o al cargar el envío asociado
     */
    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        int envioId = rs.getInt("envio");
        Envio envio = new EnvioDAO().findById(envioId);

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
