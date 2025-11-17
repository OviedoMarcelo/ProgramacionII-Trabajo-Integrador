/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 * Se encarga de realizar las operaciones de acceso a datos sobre la tabla
 * {@code envios}: inserción, actualización, borrado lógico y consultas.
 * Implementa la interfaz {@code GenericDAO<Envio>}.
 *
 * La tabla {@code envios} utiliza borrado lógico mediante la columna
 * {@code eliminado}, por lo que los métodos de consulta sólo devuelven
 * registros no eliminados.
 *
 * @author Oviedo Marcelo
 * @date 14 nov 2025
 */
public class EnvioDAO implements GenericDAO<Envio> {

    /**
     * Sentencia SQL para insertar un nuevo envío.
     */
    private static final String INSERT_SQL
            = "INSERT INTO envios (tracking,empresa,tipo,costo,fecha_despacho,fecha_estimada,estado) "
            + "VALUES (?, ?,?,?,?,?,?)";

    /**
     * Sentencia SQL para actualizar un envío existente.
     */
    private static final String UPDATE_SQL
            = "UPDATE envios SET tracking=?,empresa=?,tipo=?,costo=?,fecha_despacho=?,fecha_estimada=?,estado=? "
            + "WHERE id = ?";

    /**
     * Sentencia SQL para realizar borrado lógico de un envío.
     */
    private static final String DELETE_SQL
            = "UPDATE envios SET eliminado = TRUE WHERE id = ?";

    /**
     * Sentencia SQL para buscar un envío por ID (sólo no eliminados).
     */
    private static final String SELECT_BY_ID_SQL
            = "SELECT * FROM envios WHERE id = ? AND eliminado = FALSE";

    /**
     * Sentencia SQL para obtener todos los envíos no eliminados.
     */
    private static final String SELECT_ALL_SQL
            = "SELECT * FROM envios WHERE eliminado = FALSE";

    /**
     * Guarda un nuevo envío en la base de datos utilizando una conexión propia.
     * <br>
     * Este método crea la conexión, ejecuta el INSERT y asigna al objeto
     * {@link Envio} el ID generado de forma automática por la base de datos.
     *
     * @param envio envío a persistir
     * @throws java.sql.SQLException
     */
    @Override
    public void save(Envio envio) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEnvioValues(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt, envio);
        }
    }

    /**
     * Guarda un nuevo envío en la base de datos utilizando una conexión
     * existente, típica de un contexto transaccional. <br>
     * Este método NO crea la conexión, sino que reutiliza la recibida por
     * parámetro.
     *
     * @param envio envío a persistir
     * @param conn conexión a reutilizar dentro de una transacción
     * @throws java.sql.SQLException
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
     * Actualiza los datos de un envío existente en la base de datos.
     *
     * @param envio envío con los datos actualizados; se utiliza su ID para
     * identificar el registro a modificar
     * @throws java.sql.SQLException
     */
    @Override
    public void update(Envio envio) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

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
     * Realiza un borrado lógico de un envío, marcándolo como eliminado.
     *
     * @param id identificador del envío a eliminar
     * @throws java.sql.SQLException si no se encuentra el envío o si ocurre un
     * error SQL
     */
    @Override
    public void delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró envío con ID: " + id);
            }
        }
    }

    /**
     * Busca un envío por su identificador, siempre que no esté marcado como
     * eliminado.
     *
     * @param id identificador del envío
     * @return el envío encontrado, o {@code null} si no existe o está eliminado
     * @throws java.sql.SQLException si ocurre un error de conexión o de
     * ejecución de la consulta
     */
    @Override
    public Envio findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

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
     * Obtiene todos los envíos que no están marcados como eliminados.
     *
     * @return lista de envíos activos
     * @throws java.sql.SQLException si ocurre un error al acceder a la base de
     * datos
     */
    @Override
    public List<Envio> findAll() throws SQLException {
        List<Envio> envios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                envios.add(mapResultSetToEnvio(rs));
            }
        }

        return envios;
    }

    public Envio findByTracking(String tracking, Connection connection) throws SQLException {
        String sql = "SELECT * FROM envios WHERE tracking = ? AND eliminado = false";
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
    // Métodos auxiliares (helpers)
    // -------------------------------------------------------------------------
    /**
     * Asigna los valores del objeto {@link Envio} a los parámetros del
     * {@link PreparedStatement} respetando el orden definido en
     * {@link #INSERT_SQL}.
     *
     * Orden de los parámetros: tracking, empresa, tipo, costo, fechaDespacho,
     * fechaEstimada, estado.
     *
     * @param stmt sentencia preparada sobre la que se setearán los parámetros
     * @param envio envío cuyos datos se utilizarán
     * @throws SQLException si ocurre un error al setear algún parámetro
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
     * Obtiene el ID generado automáticamente por la base de datos luego de un
     * INSERT y lo asigna al objeto {@link Envio}. <br>
     * Requiere que el {@link PreparedStatement} haya sido creado con la opción
     * {@link Statement#RETURN_GENERATED_KEYS}.
     *
     * @param stmt sentencia preparada que ejecutó el INSERT
     * @param envio envío al que se le asignará el ID generado
     * @throws SQLException si no se obtiene ningún ID o si ocurre un error al
     * acceder al conjunto de claves generadas
     */
    private void setGeneratedId(PreparedStatement stmt, Envio envio) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                envio.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción del envío falló, no se obtuvo ID generado.");
            }
        }
    }

    /**
     * Mapea la fila actual de un {@link ResultSet} a una instancia de
     * {@link Envio}. <br>
     * Convierte las columnas de la tabla {@code envios} a los tipos de la
     * entidad, incluyendo el mapeo de {@code ENUM} a {@code enum} de Java y de
     * {@code DATE} a {@link java.time.LocalDate}.
     *
     * @param rs resultado de la consulta posicionado en una fila válida
     * @return una instancia de {@link Envio} construida a partir de la fila
     * actual
     * @throws SQLException si ocurre un error al leer alguna de las columnas
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
