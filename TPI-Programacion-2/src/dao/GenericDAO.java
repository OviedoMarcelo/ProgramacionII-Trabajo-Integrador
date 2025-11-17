package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz genérica para los Data Access Objects (DAO). <br>
 *
 * Define las operaciones básicas de acceso a datos que deben implementar
 * las clases DAO concretas para cada entidad del modelo. Centraliza las
 * firmas de los métodos CRUD y algunas variantes pensadas para usarse
 * dentro de contextos transaccionales.
 *
 * @param <T> tipo de la entidad que gestiona el DAO
 */
public interface GenericDAO<T> {

    /**
     * Persiste una nueva entidad en la base de datos utilizando una
     * conexión propia (no transaccional explícita).
     *
     * @param entity entidad a guardar
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    void save(T entity) throws SQLException;

    /**
     * Persiste una nueva entidad utilizando una conexión existente, típica
     * de un contexto transaccional que agrupa varias operaciones.
     *
     * @param entity entidad a guardar
     * @param conn   conexión a reutilizar dentro de la transacción
     * @throws SQLException si ocurre un error al preparar o ejecutar la sentencia SQL
     */
    void saveTx(T entity, Connection conn) throws SQLException;

    /**
     * Actualiza los datos de una entidad existente en la base de datos.
     *
     * @param entity entidad con los datos actualizados; se utiliza su ID interno
     *               para identificar el registro a modificar
     * @throws SQLException si ocurre un error al acceder a la base de datos
     *                      o no se puede actualizar el registro
     */
    void update(T entity) throws SQLException;

    /**
     * Marca como eliminado (o elimina físicamente, según la implementación)
     * el registro asociado al ID indicado.
     *
     * @param id identificador de la entidad a eliminar
     * @throws SQLException si ocurre un error al ejecutar la operación
     */
    void delete(int id) throws SQLException;

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad
     * @return la entidad encontrada o {@code null} si no existe
     * @throws SQLException si ocurre un error al ejecutar la consulta
     */
    T findById(int id) throws SQLException;

    /**
     * Obtiene todas las entidades del tipo gestionado por el DAO.
     *
     * @return lista de entidades; puede ser vacía si no hay registros
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    List<T> findAll() throws SQLException;
}

