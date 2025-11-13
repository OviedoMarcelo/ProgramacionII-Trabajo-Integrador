package main;

import dao.GenericDAO;
import dao.PedidoDAO;
import entities.Producto;
import service.GenericService;
import service.ProductoServiceImpl;

public class Main {
    public static void main(String[] args) {

        // 1️⃣ Instanciamos la implementación del DAO que maneja el acceso a la base de datos
        GenericDAO<Producto> productoDAO = new PedidoDAO();

        // 2️⃣ Creamos el servicio de producto, inyectando la dependencia del DAO
        GenericService<Producto> productoService = new ProductoServiceImpl(productoDAO);

        try {
            // 3️⃣ Creamos un nuevo objeto Producto (sin ID porque aún no está en la base)
            Producto nuevo = new Producto(
                "Notebook i7",                // nombre
                "Notebook Lenovo i7",      // descripción
                900000.0,                  // precio
                100                         // cantidad
            );

            // 4️⃣ Delegamos el trabajo de guardar el producto
            productoService.save(nuevo);
        
            System.out.println("✅ Producto guardado exitosamente.");

            // 5️⃣ Listamos todos los productos para verificar que se haya insertado correctamente
            System.out.println("📦 Listado de productos:");
            for (Producto p : productoService.findAll()) {
                System.out.println("🔹 " + p);
            }

        } catch (Exception e) {
            // 6️⃣ En caso de error (validación, conexión, SQL...), lo mostramos por consola
            System.err.println("❌ Error al guardar o recuperar productos: " + e.getMessage());
        }
    }
}

