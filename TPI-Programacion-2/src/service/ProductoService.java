
package service;

import config.TransactionManager;
import dao.EnvioDAO;
import dao.PedidoDAO;
import entities.EmpresaDeEnvio;
import entities.Envio;
import entities.EstadoDeEnvio;
import entities.Pedido;
import java.util.List;

/**
 * Servicio encargado de gestionar los pedidos y sus envíos asociados.
 * Implementa las operaciones básicas definidas en GenericService, y añade
 * funcionalidades adicionales utilizadas por el menú principal.
 *
 * Realiza validaciones mínimas y coordina transacciones cuando es necesario.
 * Usa PedidoDAO y EnvioDAO para interactuar con la capa de datos.
 *
 * @author gonza
 */
public class PedidoService implements GenericService<Pedido> {
    
    /** DAO para operaciones de acceso a datos de pedidos. */
    private final PedidoDAO pedidoDAO;

    /** DAO para operaciones de acceso a datos de envíos. */
    private final EnvioDAO envioDAO;

    /**
     * Constructor del servicio.
     *
     * @param pedidoDAO DAO para manejo de pedidos
     * @param envioDAO DAO para manejo de envíos
     */
    public PedidoService(PedidoDAO pedidoDAO, EnvioDAO envioDAO) {
        this.pedidoDAO = pedidoDAO;
        this.envioDAO = envioDAO;
    }

    /**
     * Guarda un pedido aplicando validaciones básicas.
     *
     * @param pedido pedido a guardar
     * @throws Exception si los datos son inválidos o ocurre un error de BD
     */
    @Override
    public void save(Pedido pedido) throws Exception {
        if (pedido.getClienteNombre() == null || pedido.getClienteNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser menor a cero.");
        }
        pedidoDAO.save(pedido);
    }

    /**
     * Busca un pedido por ID.
     *
     * @param id identificador del pedido
     * @return pedido encontrado o null
     * @throws Exception si ocurre un error
     */
    @Override
    public Pedido findById(int id) throws Exception {
        return pedidoDAO.findById(id);
    }

    /**
     * Obtiene todos los pedidos activos.
     *
     * @return lista completa de pedidos no eliminados
     * @throws Exception si ocurre un error
     */
    @Override
    public List<Pedido> findAll() throws Exception {
        return pedidoDAO.findAll();
    }

    /**
     * Actualiza un pedido existente.
     *
     * @param pedido pedido con datos actualizados
     * @throws Exception si ocurre un error en BD
     */
    @Override
    public void update(Pedido pedido) throws Exception {
        pedidoDAO.update(pedido);
    }

    /**
     * Elimina lógicamente un pedido por ID.
     *
     * @param id id del pedido
     * @throws Exception si ocurre un error
     */
    @Override
    public void delete(int id) throws Exception {
        pedidoDAO.delete(id);
    }

    /**
     * Guarda un pedido utilizando una transacción.
     *
     * @param pedido pedido a guardar en transacción
     * @throws Exception si falla la operación
     */
    @Override
    public void saveTx(Pedido pedido) throws Exception {
        if (pedido.getClienteNombre() == null || pedido.getClienteNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor a cero.");
        }

        TransactionManager tx = new TransactionManager();

        try {
            tx.begin();
            pedidoDAO.saveTx(pedido, tx.getConnection());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }


    //  MÉTODOS ADICIONALES UTILIZADOS POR EL MENU HANDLER   

    /**
     * Crea un pedido junto con su envío asociado, dentro de una misma transacción.
     *
     * @param pedido pedido a registrar
     * @param envio envío asociado al pedido
     * @throws Exception si ocurre un error durante el proceso
     */
    public void crearPedidoConEnvio(Pedido pedido, Envio envio) throws Exception {
        TransactionManager tx = new TransactionManager();

        try {
            tx.begin();

            envioDAO.saveTx(envio, tx.getConnection());
            pedido.setEnvio(envio);
            pedidoDAO.saveTx(pedido, tx.getConnection());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    /**
     * Obtiene todos los pedidos activos (no eliminados).
     *
     * @return lista de pedidos
     * @throws Exception si ocurre un error de acceso a datos
     */
    public List<Pedido> obtenerTodosLosPedidos() throws Exception {
        return pedidoDAO.findAll();
    }

    /**
     * Busca un pedido utilizando su número único.
     *
     * @param numero número del pedido
     * @return pedido encontrado o null
     * @throws Exception si ocurre un error
     */
    public Pedido buscarPorNumero(String numero) throws Exception {
        return pedidoDAO.findByNumero(numero);
    }

    /**
     * Busca todos los pedidos pertenecientes a un cliente determinado.
     *
     * @param cliente nombre del cliente
     * @return lista de pedidos filtrados
     * @throws Exception si ocurre un error
     */
    public List<Pedido> buscarPorCliente(String cliente) throws Exception {
        return pedidoDAO.findByCliente(cliente);
    }

    /**
     * Actualiza el estado del envío asociado a un pedido específico.
     *
     * @param numeroPedido número del pedido
     * @param nuevoEstado nuevo estado del envío
     * @throws Exception si el pedido no existe o falla la actualización
     */
    public void actualizarEstadoEnvio(String numeroPedido, EstadoDeEnvio nuevoEstado) throws Exception {
        Pedido pedido = pedidoDAO.findByNumero(numeroPedido);

        if (pedido == null) {
            throw new Exception("Pedido no encontrado.");
        }

        Envio envio = pedido.getEnvio();
        envio.setEstado(nuevoEstado);
        envioDAO.update(envio);
    }

    /**
     * Elimina lógicamente un pedido según su número (no por ID).
     *
     * @param numero número del pedido
     * @throws Exception si el pedido no existe
     */
    public void eliminarPedido(String numero) throws Exception {
        Pedido pedido = pedidoDAO.findByNumero(numero);

        if (pedido == null) {
            throw new Exception("Pedido no encontrado.");
        }

        pedido.setEliminado(true);
        pedidoDAO.update(pedido);
    }

    /**
     * Obtiene todos los envíos pertenecientes a una empresa de envíos.
     *
     * @param empresa empresa a filtrar
     * @return lista de envíos de esa empresa
     * @throws Exception si ocurre un error de acceso
     */
    public List<Envio> listarEnviosPorEmpresa(EmpresaDeEnvio empresa) throws Exception {
        return envioDAO.findAll()
                .stream()
                .filter(e -> e.getEmpresa() == empresa)
                .toList();
    }

    /**
     * Cuenta la cantidad total de pedidos no eliminados.
     *
     * @return cantidad de pedidos activos
     * @throws Exception si ocurre un error
     */
    public long contarPedidosActivos() throws Exception {
        return pedidoDAO.contarActivos();
    }

    /**
     * Suma el monto total de los pedidos activos.
     *
     * @return suma total de pedidos
     * @throws Exception si ocurre un error
     */
    public double calcularValorTotalPedidos() throws Exception {
        return pedidoDAO.sumarTotalActivos();
    }
}   
