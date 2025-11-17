package dao;

import config.DatabaseConnection;
import entities.EmpresaDeEnvio;
import entities.Envio;
import entities.EstadoDeEnvio;
import entities.TipoDeEnvio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad {@link Envio}. <br>
 *
 * Gestiona las operaciones CRUD sobre la tabla {@code envios}, incluyendo:
 * <ul>
 *   <li>Inserción con generación automática de ID</li>
 *   <li>Actualización</li>
 *   <li>Borrado lógico mediante la columna {@code eliminado}</li>
 *   <li>Consultas individuales y listados</li>
 * </ul>
 *
 * También se encarga del mapeo entre:
 * <ul>
 *   <li>Valores {@code ENUM} de MySQL</li>
 *   <li>Enums Java ({@link EmpresaDeEnvio}, {@link TipoDeEnvio}, {@link EstadoDeEnvio})</li>
 * </ul>
 *
 * La tabla utiliza borrado lógico, por lo que este DAO solo devuelve envíos
 * que no estén marcados como eliminados.
 *
 * @author Oviedo Marcelo
 * @date 14 nov 2025
 */
public class EnvioDAO implements GenericDAO<Envio> {

    /** Sentencia SQL para insertar un nuevo envío. */
    private static final String INSERT_SQL =
            "INSERT INTO envios (tracking,empresa,tipo,costo,fecha_despacho,fecha_estimada,estado) "
            + "VALUES (?,?,?,?,?,?,?)";

    /** Sentencia SQL para actualizar un envío existente. */
    private static final String UPDATE_SQL =
            "UPDATE envios SET tracking=?,empresa=?,tipo=?,costo=?,fecha_despacho=?,fecha_estimada=?,estado=? "
            + "WHERE id = ?";

    /** Sentencia SQL para realizar borrado lógico. */
    private static final String DELETE_SQL =
            "UPDATE envios SET eliminado = TRUE WHERE id = ?";

    /** Sentencia SQL para buscar un envío por ID. */
    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM envios WHERE id = ? AND eliminado = FALSE";

    /** Sentencia SQL para obtener todos los envíos activos. */
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM envios WHERE eliminado = FALSE";

    // -------------------------------------------------------------------------
    // Métodos CRUD del GenericDAO
    // -------------------------------------------------------------------------

    /**
     * Guarda un nuevo envío utilizando una conexión propia. <br>
     * Asigna al objeto {@link Envio} el ID generado por la base de datos.
     *
     * @param envio envío a persistir
     * @throws SQLException si ocurre un error al preparar la sentencia,
     *                      ejecutar el INSERT o recuperar la clave generada
     */
    @Override
    public void save(Envio envio) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEnvioValues(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt, envio);
        }
    }

    /**
     * Guarda un nuevo envío utilizando una conexión existente,
     * normalmente dentro de una transacción que incluye otras operaciones.
     *
     * @param envio envío a persistir
     * @param conn conexión transaccional a reutilizar
     * @throws SQLException si ocurre un error al preparar o ejecutar la sentencia SQL
     */
    @Override
    public void saveTx(Envio envio, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setEnvioValues(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt, envio);
        }
    }

    /**
     * Actualiza los datos de un envío existente en la base.
     *
     * @param envio envío con datos actualizados
     * @throws SQLException si el envío no existe o si ocurre un error SQL
     */
    @Override
    public void update(Envio envio) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, envio.getTracking());
            stmt.setString(2, envio.getEmpresa().name());
            stmt.setString(3, envio.getTipo().name());
            stmt.setDouble(4, envio.getCosto());

            stmt.setDate(5,
                    envio.getFechaDespacho() != null
                            ? java.sql.Date.valueOf(envio.getFechaDespacho())
                            : null
            );

            stmt.setDate(6,
                    envio.getFechaEstimada() != null
                            ? java.sql.Date.valueOf(envio.getFechaEstimada())
                            : null
            );

            stmt.setString(7, envio.getEstado().name());
            stmt.setLong(8, envio.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el envío con ID: " + envio.getId());
            }
        }
    }

    /**
     * Marca un envío como eliminado (borrado lógico).
     *
     * @param id identificador del envío
     * @throws SQLException si no se encuentra el envío o si ocurre un error SQL
     */
    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró envío con ID: " + id);
            }
        }
    }

    /**
     * Busca un envío por su identificador (siempre que no esté eliminado).
     *
     * @param id identificador del envío
     * @return el envío encontrado o {@code null} si no existe
     * @throws SQLException si ocurre un error al ejecutar la consulta SQL
     */
    @Override
    public Envio findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnvio(rs);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los envíos activos (no eliminados).
     *
     * @return lista de envíos
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    @Override
    public List<Envio> findAll() throws SQLException {
        List<Envio> envios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                envios.add(mapResultSetToEnvio(rs));
            }
        }

        return envios;
    }

    // -------------------------------------------------------------------------
    // Consultas adicionales
    // -------------------------------------------------------------------------

    /**
     * Busca un envío por su número de tracking.
     *
     * @param tracking valor de la columna {@code tracking}
     * @param connection conexión existente (ideal para uso transaccional)
     * @return el envío encontrado o {@code null} si no existe
     * @throws SQLException si ocurre un error en la consulta SQL
     */
    public Envio findByTracking(String tracking, Connection connection) throws SQLException {
        String sql = "SELECT * FROM envios WHERE tracking = ? AND eliminado = FALSE";
        Envio envio = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tracking);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    envio = mapResultSetToEnvio(resultSet);
                }
            }
        }
        return envio;
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    /**
     * Setea los parámetros del {@link PreparedStatement} con los valores del envío,
     * respetando el orden definido en {@link #INSERT_SQL}.
     *
     * @param stmt sentencia preparada
     * @param envio envío cuyos datos se asignarán
     * @throws SQLException si ocurre un error al setear un parámetro
     */
    private void setEnvioValues(PreparedStatement stmt, Envio envio) throws SQLException {
        stmt.setString(1, envio.getTracking());
        stmt.setString(2, envio.getEmpresa().name());
        stmt.setString(3, envio.getTipo().name());
        stmt.setDouble(4, envio.getCosto());

        stmt.setDate(5,
                envio.getFechaDespacho() != null ? java.sql.Date.valueOf(envio.getFechaDespacho()) : null
        );
        stmt.setDate(6,
                envio.getFechaEstimada() != null ? java.sql.Date.valueOf(envio.getFechaEstimada()) : null
        );

        stmt.setString(7, envio.getEstado().name());
    }

    /**
     * Recupera el ID generado automáticamente tras un INSERT y lo asigna al
     * objeto {@link Envio}.
     *
     * @param stmt sentencia preparada que ejecutó el INSERT
     * @param envio envío al que se asignará el ID generado
     * @throws SQLException si no se obtiene ningún ID generado
     */
    private void setGeneratedId(PreparedStatement stmt, Envio envio) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                envio.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del envío falló: no se obtuvo ID generado.");
            }
        }
    }

    /**
     * Mapea la fila actual de un {@link ResultSet} a una instancia de {@link Envio}.
     * Maneja correctamente valores {@code null} en columnas DATE y convierte
     * valores {@code ENUM} de la base a los enums de Java.
     *
     * @param rs fila del resultado
     * @return instancia de {@link Envio}
     * @throws SQLException si ocurre un error al leer una columna
     */
    private Envio mapResultSetToEnvio(ResultSet rs) throws SQLException {

        java.sql.Date fechaDespachoSql = rs.getDate("fecha_despacho");
        java.sql.Date fechaEstimadaSql = rs.getDate("fecha_estimada");

        return new Envio(
                rs.getLong("id"),
                rs.getString("tracking"),
                EmpresaDeEnvio.valueOf(rs.getString("empresa")),
                TipoDeEnvio.valueOf(rs.getString("tipo")),
                rs.getDouble("costo"),
                fechaDespachoSql != null ? fechaDespachoSql.toLocalDate() : null,
                fechaEstimadaSql != null ? fechaEstimadaSql.toLocalDate() : null,
                EstadoDeEnvio.valueOf(rs.getString("estado"))
        );
    }

}
