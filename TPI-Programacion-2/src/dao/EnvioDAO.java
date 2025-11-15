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
 * Clase EnvioDAO
 *
 * @author Oviedo Marcelo
 * @date 14 nov 2025
 *
 * Descripción: TODO: Agregar descripción de la clase
 */
public class EnvioDAO implements GenericDAO<Envio> {

    private static final String INSERT_SQL = "INSERT INTO envios (tracking,empresa,tipo,costo,fechaDespacho,fechaEstimada,estado) VALUES (?, ?,?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE envios SET tracking=?,empresa=?,tipo=?,costo=?,fechaDespacho=?,fechaEstimada=?,estado=? WHERE id = ?";
    private static final String DELETE_SQL = "UPDATE envios SET eliminado = TRUE WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM envios WHERE id = ? AND eliminado = FALSE";
    private static final String SELECT_ALL_SQL = "SELECT * FROM envios WHERE eliminado = FALSE";

    @Override
    public void save(Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setEnvioValues(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt, envio);
        }
    }

    @Override
    public void saveTx(Envio envio, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setEnvioValues(stmt, envio);
            stmt.executeUpdate();
            setGeneratedId(stmt, envio);
        }
    }

    @Override
    public void update(Envio envio) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, envio.getTracking());                // VARCHAR
            stmt.setString(2, envio.getEmpresa().name());          // ENUM -> String
            stmt.setString(3, envio.getTipo().name());             // ENUM -> String
            stmt.setDouble(4, envio.getCosto());                   // DOUBLE
            stmt.setDate(5, java.sql.Date.valueOf(envio.getFechaDespacho()));
            stmt.setDate(6, java.sql.Date.valueOf(envio.getFechaEstimada()));
            stmt.setString(7, envio.getEstado().name());           // ENUM -> String
            stmt.setInt(8, envio.getId());           // ENUM -> String

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el envio con ID: " + envio.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró envio con ID: " + id);
            }
        }
    }

    @Override
    public Envio findById(int id) throws Exception {
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

    @Override
    public List<Envio> findAll() throws Exception {
        List<Envio> envios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                envios.add(mapResultSetToEnvio(rs));
            }
        }
        return envios;
    }

    //Helpers 
    //Setea los valores antes de enviarlos a la query 
    //Orden de los valores tracking,empresa,tipo,costo,fechaDespacho,fechaEstimada,estado    
    private void setEnvioValues(PreparedStatement stmt, Envio envio) throws SQLException {
        stmt.setString(1, envio.getTracking());                // VARCHAR
        stmt.setString(2, envio.getEmpresa().name());          // ENUM -> String
        stmt.setString(3, envio.getTipo().name());             // ENUM -> String
        stmt.setDouble(4, envio.getCosto());                   // DOUBLE
        stmt.setDate(5, java.sql.Date.valueOf(envio.getFechaDespacho()));
        stmt.setDate(6, java.sql.Date.valueOf(envio.getFechaEstimada()));
        stmt.setString(7, envio.getEstado().name());           // ENUM -> String
    }

    private void setGeneratedId(PreparedStatement stmt, Envio envio) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                envio.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("La inserción del envío falló, no se obtuvo ID generado");
            }
        }
    }

    private Envio mapResultSetToEnvio(ResultSet rs) throws SQLException {
        return new Envio(
                rs.getInt("id"),
                rs.getString("tracking"),
                // convertir Strings de la BD a enums de Java
                EmpresaDeEnvio.valueOf(rs.getString("empresa")),
                TipoDeEnvio.valueOf(rs.getString("tipo")),
                rs.getDouble("costo"),
                // convertir java.sql.Date a LocalDate
                rs.getDate("fechaDespacho").toLocalDate(),
                rs.getDate("fechaEstimada").toLocalDate(),
                EstadoDeEnvio.valueOf(rs.getString("estado"))
        );
    }

}
