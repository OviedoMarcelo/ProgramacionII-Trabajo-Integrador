package service;

import dao.EnvioDAO;
import entities.Envio;
import entities.EmpresaDeEnvio;
import entities.TipoDeEnvio;
import entities.EstadoDeEnvio;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EnvioService {

    private EnvioDAO envioDAO;

    public EnvioService() {
        this.envioDAO = new EnvioDAO();
    }

    public EnvioService(EnvioDAO envioDAO) {
        if (envioDAO == null) {
            throw new IllegalArgumentException("EnvioDAO no puede ser null");
        }
        this.envioDAO = envioDAO;
    }

    // Método para crear envío dentro de una transacción
    public void crearEnvio(Envio envio, Connection connection) throws SQLException, IllegalArgumentException {
        // Validaciones
        validarEnvio(envio);

        // Verificar que el tracking sea único
        Envio envioExistente = envioDAO.findByTracking(envio.getTracking(), connection);
        if (envioExistente != null) {
            throw new IllegalArgumentException("Ya existe un envío con el tracking: " + envio.getTracking());
        }

        // Crear el envío
        try {
            envioDAO.saveTx(envio, connection);
        } catch (Exception e) {
            throw new SQLException("Error al crear el envío: " + e.getMessage(), e);
        }
    }

    // Método para buscar envío por ID
    public Envio buscarEnvioPorId(int id) throws Exception {
        return envioDAO.findById(id);
    }

    // Método para buscar envío por tracking
    public Envio buscarEnvioPorTracking(String tracking) throws Exception {
        Connection connection = null;
        try {
            connection = config.DatabaseConnection.getConnection();
            return envioDAO.findByTracking(tracking, connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    // Método para listar todos los envíos
    public List<Envio> listarTodosLosEnvios() throws Exception {
        return envioDAO.findAll();
    }

    // Método para actualizar envío
    public void actualizarEnvio(Envio envio) throws Exception {
        // Validaciones
        validarEnvio(envio);

        envioDAO.update(envio);
    }

    // Método para eliminar envío
    public void eliminarEnvio(int id) throws Exception {
        envioDAO.delete(id);
    }

    // Método de validación
    private void validarEnvio(Envio envio) throws IllegalArgumentException {
        if (envio.getTracking() == null || envio.getTracking().trim().isEmpty()) {
            throw new IllegalArgumentException("El tracking es obligatorio");
        }

        if (envio.getTracking().length() > 40) {
            throw new IllegalArgumentException("El tracking no puede tener más de 40 caracteres");
        }

        if (envio.getEmpresa() == null) {
            throw new IllegalArgumentException("La empresa de envío es obligatoria");
        }

        if (envio.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de envío es obligatorio");
        }

        if (envio.getCosto() <= 0) {
            throw new IllegalArgumentException("El costo debe ser mayor a 0");
        }

        if (envio.getEstado() == null) {
            throw new IllegalArgumentException("El estado del envío es obligatorio");
        }

        // Validar fechas
        if (envio.getFechaDespacho() != null && envio.getFechaEstimada() != null) {
            if (envio.getFechaEstimada().isBefore(envio.getFechaDespacho())) {
                throw new IllegalArgumentException("La fecha estimada no puede ser anterior a la fecha de despacho");
            }
        }
    }

    // Métodos auxiliares para obtener opciones
    public EmpresaDeEnvio[] getEmpresasDeEnvio() {
        return EmpresaDeEnvio.values();
    }

    public TipoDeEnvio[] getTiposDeEnvio() {
        return TipoDeEnvio.values();
    }

    public EstadoDeEnvio[] getEstadosDeEnvio() {
        return EstadoDeEnvio.values();
    }
}
