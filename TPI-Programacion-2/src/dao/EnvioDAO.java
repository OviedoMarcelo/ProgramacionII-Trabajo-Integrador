package dao;

import entities.Envio;
import entities.EmpresaDeEnvio;
import entities.TipoDeEnvio;
import entities.EstadoDeEnvio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnvioDAO implements GenericDAO<Envio> {

    public EnvioDAO() {
    }

    
    @Override
    public void save(Envio envio) throws SQLException {
        throw new UnsupportedOperationException("Usar saveTx con Connection para transacciones");
    }

    @Override
    public Envio findById(int id) throws SQLException {
        Connection connection = null;
        try {
            connection = config.DatabaseConnection.getConnection();
            return findById(id, connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public List<Envio> findAll() throws SQLException {
        Connection connection = null;
        try {
            connection = config.DatabaseConnection.getConnection();
            return findAll(connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void update(Envio envio) throws SQLException {
        Connection connection = null;
        try {
            connection = config.DatabaseConnection.getConnection();
            update(envio, connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = null;
        try {
            connection = config.DatabaseConnection.getConnection();
            delete(id, connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void saveTx(Envio envio, Connection connection) throws SQLException {
        String sql = "INSERT INTO envios (tracking, empresa, tipo, costo, fecha_despacho, fecha_estimada, estado, eliminado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, envio.getTracking());
            statement.setString(2, envio.getEmpresa().name());
            statement.setString(3, envio.getTipo().name());
            statement.setDouble(4, envio.getCosto());
            statement.setDate(5, envio.getFechaDespacho() != null ? Date.valueOf(envio.getFechaDespacho()) : null);
            statement.setDate(6, envio.getFechaEstimada() != null ? Date.valueOf(envio.getFechaEstimada()) : null);
            statement.setString(7, envio.getEstado().name());
            statement.setBoolean(8, false); // eliminado = false por defecto
            
            statement.executeUpdate();
            
            // Obtener el ID generado
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    envio.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    // Métodos adicionales que trabajan con Connection
    public Envio findById(int id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM envios WHERE id = ? AND eliminado = false";
        Envio envio = null;
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    envio = mapearEnvio(resultSet);
                }
            }
        }
        return envio;
    }

    public List<Envio> findAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM envios WHERE eliminado = false";
        List<Envio> envios = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                envios.add(mapearEnvio(resultSet));
            }
        }
        return envios;
    }

    public void update(Envio envio, Connection connection) throws SQLException {
        String sql = "UPDATE envios SET tracking = ?, empresa = ?, tipo = ?, costo = ?, fecha_despacho = ?, fecha_estimada = ?, estado = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, envio.getTracking());
            statement.setString(2, envio.getEmpresa().name());
            statement.setString(3, envio.getTipo().name());
            statement.setDouble(4, envio.getCosto());
            statement.setDate(5, envio.getFechaDespacho() != null ? Date.valueOf(envio.getFechaDespacho()) : null);
            statement.setDate(6, envio.getFechaEstimada() != null ? Date.valueOf(envio.getFechaEstimada()) : null);
            statement.setString(7, envio.getEstado().name());
            statement.setLong(8, envio.getId());
            
            statement.executeUpdate();
        }
    }

    public void delete(int id, Connection connection) throws SQLException {
        String sql = "UPDATE envios SET eliminado = true WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Método adicional: buscar por tracking
    public Envio buscarPorTracking(String tracking, Connection connection) throws SQLException {
        String sql = "SELECT * FROM envios WHERE tracking = ? AND eliminado = false";
        Envio envio = null;
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tracking);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    envio = mapearEnvio(resultSet);
                }
            }
        }
        return envio;
    }

    // Método auxiliar para mapear ResultSet a objeto Envio
    private Envio mapearEnvio(ResultSet resultSet) throws SQLException {
        Envio envio = new Envio();
        envio.setId(resultSet.getLong("id"));
        envio.setTracking(resultSet.getString("tracking"));
        envio.setEmpresa(EmpresaDeEnvio.valueOf(resultSet.getString("empresa")));
        envio.setTipo(TipoDeEnvio.valueOf(resultSet.getString("tipo")));
        envio.setCosto(resultSet.getDouble("costo"));
        
        Date fechaDespacho = resultSet.getDate("fecha_despacho");
        if (fechaDespacho != null) {
            envio.setFechaDespacho(fechaDespacho.toLocalDate());
        }
        
        Date fechaEstimada = resultSet.getDate("fecha_estimada");
        if (fechaEstimada != null) {
            envio.setFechaEstimada(fechaEstimada.toLocalDate());
        }
        
        envio.setEstado(EstadoDeEnvio.valueOf(resultSet.getString("estado")));
        
        return envio;
    }
}

