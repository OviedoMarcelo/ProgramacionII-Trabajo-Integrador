package service;

import config.TransactionManager;
import dao.GenericDAO;
import entities.Pedido;
import java.util.List;

public class PedidoService implements GenericService<Pedido> {

    private final GenericDAO<Pedido> pedidoDAO;

    public PedidoService(GenericDAO<Pedido> pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public void save(Pedido pedido) throws Exception {
        /*if (pedido.getNombre() == null || pedido.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del pedido no puede estar vacío.");
        }
        if (pedido.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor a cero.");
        }*/
        pedidoDAO.save(pedido);
    }
    
    
    

    @Override
    public Pedido findById(int id) throws Exception {
        return pedidoDAO.findById(id);
    }

    @Override
    public List<Pedido> findAll() throws Exception {
        return pedidoDAO.findAll();
    }

    @Override
    public void update(Pedido pedido) throws Exception {
        pedidoDAO.update(pedido);
    }

    @Override
    public void delete(int id) throws Exception {
        pedidoDAO.delete(id);
    }

    @Override
    public void saveTx(Pedido entity) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
