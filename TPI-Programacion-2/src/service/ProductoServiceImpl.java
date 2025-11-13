package service;

import config.TransactionManager;
import dao.GenericDAO;
import entities.Producto;
import java.util.List;

public class ProductoServiceImpl implements GenericService<Producto> {

    private final GenericDAO<Producto> productoDAO;

    public ProductoServiceImpl(GenericDAO<Producto> productoDAO) {
        this.productoDAO = productoDAO;
    }

    @Override
    public void save(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor a cero.");
        }
        productoDAO.save(producto);
    }
    
    
    

    @Override
    public Producto findById(int id) throws Exception {
        return productoDAO.findById(id);
    }

    @Override
    public List<Producto> findAll() throws Exception {
        return productoDAO.findAll();
    }

    @Override
    public void update(Producto producto) throws Exception {
        productoDAO.update(producto);
    }

    @Override
    public void delete(int id) throws Exception {
        productoDAO.delete(id);
    }

    @Override
    public void saveTx(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser menor a cero.");
        }

        TransactionManager tx = new TransactionManager();

        try {
            tx.begin();
            productoDAO.saveTx(producto, tx.getConnection());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
}
