package dao;

import config.DatabaseConnectionPool;
import entities.Pedido;
import java.sql.*;
import java.util.*;

public class PedidoDAOImpl implements GenericDAO<Pedido> {

    @Override
    public void save(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            
            stmt.executeUpdate();
        }
    }

    
    
    
    
    @Override
    public Producto findById(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                );
            }
        }
        return null;
    }

    @Override
    public List<Producto> findAll() throws SQLException {
        String sql = "SELECT * FROM productos";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                ));
            }
        }
        return productos;
    }

    @Override
    public void update(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre=?, descripcion=?, precio=?, cantidad=? WHERE id=?";
        try (Connection conn = DatabaseConnectionPool.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            stmt.setInt(5, producto.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void saveTx(Producto producto, Connection conn) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getCantidad());
            stmt.executeUpdate();
        }
    }
}
