/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import entities.EmpresaDeEnvio;
import entities.Envio;
import entities.EstadoDeEnvio;
import entities.EstadoDePedido;
import entities.Pedido;
import entities.TipoDeEnvio;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import service.PedidoService;

/**
 *
 * @author fede
 */
public class MenuHandler {

    private final Scanner scanner;
    private final PedidoService pedidoService;

    public MenuHandler(Scanner scanner, PedidoService pedidoService) {
        this.scanner = scanner;
        this.pedidoService = pedidoService;
    }

    public void crearPedidoConEnvio() {
        try {
            System.out.println("\n--- CREAR NUEVO PEDIDO CON ENVÍO ---");

            // Datos del pedido
            System.out.print("Número de pedido: ");
            String numero = scanner.nextLine().toUpperCase();

            System.out.print("Nombre del cliente: ");
            String cliente = scanner.nextLine();

            System.out.print("Total del pedido: ");
            double total = Double.parseDouble(scanner.nextLine());

            // Datos del envío
            System.out.print("Número de tracking: ");
            String tracking = scanner.nextLine().toUpperCase();

            System.out.println("Empresas disponibles: " + Arrays.toString(EmpresaDeEnvio.values()));
            System.out.print("Empresa de envío: ");
            String empresa = scanner.nextLine().toUpperCase();

            System.out.println("Tipos disponibles: " + Arrays.toString(TipoDeEnvio.values()));
            System.out.print("Tipo de envío: ");
            String tipo = scanner.nextLine().toUpperCase();

            System.out.print("Costo de envío: ");
            double costo = Double.parseDouble(scanner.nextLine());

            // Crear pedido
            Pedido pedido = new Pedido();
            pedido.setNumero(numero);
            pedido.setClienteNombre(cliente);
            pedido.setTotal(total);

            Envio envio = new Envio();
            envio.setTracking(tracking);
            envio.setEmpresa(EmpresaDeEnvio.valueOf(empresa));
            envio.setTipo(TipoDeEnvio.valueOf(tipo));
            envio.setCosto(costo);
            envio.setEstado(EstadoDeEnvio.EN_PREPARACION); // valor por defecto
            

            pedidoService.crearPedidoConEnvio(pedido, envio);
            System.out.println("✓ Pedido creado exitosamente!");

        } catch (Exception e) {
            System.out.println("Error al crear pedido: " + e.getMessage());
        }
    }

    public void listarTodosLosPedidos() {
        try {
            System.out.println("\n--- LISTA DE PEDIDOS ---");
            List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();

            if (pedidos.isEmpty()) {
                System.out.println("No hay pedidos registrados.");
                return;
            }

            for (Pedido pedido : pedidos) {
                if (!pedido.isEliminado()) {
                    System.out.printf("Pedido #%s - Cliente: %s - Total: $%.2f - Estado: %s%n",
                            pedido.getNumero(), pedido.getClienteNombre(),
                            pedido.getTotal(), pedido.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        }
    }

    public void buscarPedidoPorNumero() {
        try {
            System.out.print("\nIngrese número de pedido a buscar: ");
            String numero = scanner.nextLine().toUpperCase();

            Pedido pedido = pedidoService.buscarPorNumero(numero);

            if (pedido != null && !pedido.isEliminado()) {
                mostrarDetallesPedido(pedido);
            } else {
                System.out.println("Pedido no encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Error en la búsqueda: " + e.getMessage());
        }
    }

    public void buscarPedidoPorCliente() {
        try {
            System.out.print("\nIngrese nombre del cliente: ");
            String cliente = scanner.nextLine();

            List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente);

            if (pedidos.isEmpty()) {
                System.out.println("No se encontraron pedidos para ese cliente.");
                return;
            }

            System.out.println("Pedidos encontrados:");
            for (Pedido pedido : pedidos) {
                if (!pedido.isEliminado()) {
                    System.out.printf("- #%s - Total: $%.2f - Estado: %s%n",
                            pedido.getNumero(), pedido.getTotal(), pedido.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("Error en la búsqueda: " + e.getMessage());
        }
    }

    public void actualizarEstadoEnvio() {
        try {
            System.out.print("\nIngrese número de pedido: ");
            String numero = scanner.nextLine().toUpperCase();

            System.out.println("Estados disponibles: EN_PREPARACION, EN_TRANSITO, ENTREGADO");
            System.out.print("Nuevo estado: ");
            String estado = scanner.nextLine().toUpperCase();

            pedidoService.actualizarEstadoEnvio(numero, EstadoDeEnvio.valueOf(estado));
            System.out.println("✓ Estado actualizado exitosamente!");

        } catch (Exception e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
        }
    }

    public void eliminarPedido() {
        try {
            System.out.print("\nIngrese número de pedido a eliminar: ");
            String numero = scanner.nextLine().toUpperCase();

            System.out.print("¿Está seguro? (S/N): ");
            String confirmacion = scanner.nextLine().toUpperCase();

            if (confirmacion.equals("S")) {
                pedidoService.eliminarPedido(numero);
                System.out.println("✓ Pedido eliminado exitosamente!");
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar pedido: " + e.getMessage());
        }
    }

    public void listarEnviosPorEmpresa() {
        try {
            System.out.println("Empresas disponibles: ANDREANI, OCA, CORREO_ARG");
            System.out.print("Ingrese empresa: ");
            String empresa = scanner.nextLine().toUpperCase();

            List<Envio> envios = pedidoService.listarEnviosPorEmpresa(
                    EmpresaDeEnvio.valueOf(empresa));

            if (envios.isEmpty()) {
                System.out.println("No hay envíos para esa empresa.");
                return;
            }

            System.out.println("Envíos encontrados:");
            for (Envio envio : envios) {
                if (!envio.isEliminado()) {
                    System.out.printf("- Tracking: %s - Estado: %s - Costo: $%.2f%n",
                            envio.getTracking(), envio.getEstado(), envio.getCosto());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar envíos: " + e.getMessage());
        }
    }

    public void mostrarEstadisticas() {
        try {
            System.out.println("\n--- ESTADÍSTICAS ---");
            long totalPedidos = pedidoService.contarPedidosActivos();
            double valorTotal = pedidoService.calcularValorTotalPedidos();

            System.out.printf("Total de pedidos activos: %d%n", totalPedidos);
            System.out.printf("Valor total de pedidos: $%.2f%n", valorTotal);

        } catch (Exception e) {
            System.out.println("Error al calcular estadísticas: " + e.getMessage());
        }
    }

    // Métodos para las otras opciones (actualizarPedido, etc.)
    public void actualizarPedido() {
        // Implementar lógica de actualización
        //System.out.println("Funcionalidad en desarrollo...");
        
        try {
        System.out.println("\n=== ACTUALIZAR PEDIDO ===");
        System.out.print("Ingrese número de pedido a actualizar: ");
        String numero = scanner.nextLine().toUpperCase();

        // Buscar el pedido existente
        Pedido pedidoExistente = pedidoService.buscarPorNumero(numero);
        if (pedidoExistente == null) {
            System.out.println("Pedido no encontrado.");
            return;
        }

        // Mostrar datos actuales
        System.out.println("\n DATOS ACTUALES DEL PEDIDO:");
        System.out.println(pedidoExistente.toString());

        // Solicitar nuevos datos
        System.out.println("\n INGRESE LOS NUEVOS DATOS (dejar vacío para mantener actual):");

        // Cliente
        System.out.print("Cliente [" + pedidoExistente.getClienteNombre() + "]: ");
        String nuevoCliente = scanner.nextLine();
        if (nuevoCliente.isEmpty()) {
            nuevoCliente = pedidoExistente.getClienteNombre();
        }

        // Fecha
        System.out.print("Fecha [YYYY-MM-DD] [" + pedidoExistente.getFecha() + "]: ");
        String nuevaFechaStr = scanner.nextLine();
        LocalDate nuevaFecha = pedidoExistente.getFecha();
        if (!nuevaFechaStr.isEmpty()) {
            try {
                nuevaFecha = LocalDate.parse(nuevaFechaStr);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Se mantiene fecha actual.");
            }
        }

        // Estado
        System.out.println("Estado actual: " + pedidoExistente.getEstado());
        System.out.println("Estados disponibles:");
        System.out.println("1. NUEVO");
        System.out.println("2. FACTURADO"); 
        System.out.println("3. ENVIADO");
        System.out.print("Seleccione nuevo estado (1-3) o 0 para mantener actual: ");
        
        String opcionEstado = scanner.nextLine();
        EstadoDePedido nuevoEstado = pedidoExistente.getEstado();
        
        if (!opcionEstado.equals("0") && !opcionEstado.isEmpty()) {
            try {
                int opcion = Integer.parseInt(opcionEstado);
                switch (opcion) {
                    case 1: nuevoEstado = EstadoDePedido.NUEVO; break;
                    case 2: nuevoEstado = EstadoDePedido.FACTURADO; break;
                    case 3: nuevoEstado = EstadoDePedido.ENVIADO; break;
      
                    default: 
                        System.out.println("Opción inválida. Se mantiene estado actual.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Se mantiene estado actual.");
            }
        }

        // Total
        System.out.print("Total [" + pedidoExistente.getTotal() + "]: ");
        String nuevoTotalStr = scanner.nextLine();
        double nuevoTotal = pedidoExistente.getTotal();
        if (!nuevoTotalStr.isEmpty()) {
            try {
                nuevoTotal = Double.parseDouble(nuevoTotalStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ Formato de total inválido. Se mantiene total actual.");
            }
        }

        // Mostrar resumen de cambios
        System.out.println("\n RESUMEN DE CAMBIOS:");
        System.out.println("Cliente: " + pedidoExistente.getClienteNombre() + " → " + nuevoCliente);
        System.out.println("Fecha: " + pedidoExistente.getFecha() + " → " + nuevaFecha);
        System.out.println("Estado: " + pedidoExistente.getEstado() + " → " + nuevoEstado);
        System.out.println("Total: " + pedidoExistente.getTotal() + " → " + nuevoTotal);

        // Confirmar actualización
        System.out.print("\n¿Confirmar actualización? (S/N): ");
        String confirmacion = scanner.nextLine().toUpperCase();

        if (confirmacion.equals("S") || confirmacion.equals("SI")) {
            // Actualizar el pedido existente
            pedidoExistente.setClienteNombre(nuevoCliente);
            pedidoExistente.setFecha(nuevaFecha);
            pedidoExistente.setEstado(nuevoEstado);
            pedidoExistente.setTotal(nuevoTotal);
            
            pedidoService.actualizarPedido(pedidoExistente);
            System.out.println("✅ Pedido actualizado exitosamente!");
        } else {
            System.out.println("❌ Operación cancelada.");
        }

    } catch (Exception e) {
        System.out.println("❌ Error al actualizar pedido: " + e.getMessage());
        e.printStackTrace(); // Para debugging
    }
    }

    private void mostrarDetallesPedido(Pedido pedido) {
        System.out.println("\n--- DETALLES DEL PEDIDO ---");
        System.out.printf("Número: %s%n", pedido.getNumero());
        System.out.printf("Cliente: %s%n", pedido.getClienteNombre());
        System.out.printf("Total: $%.2f%n", pedido.getTotal());
        System.out.printf("Estado: %s%n", pedido.getEstado());

        if (pedido.getEnvio() != null) {
            Envio envio = pedido.getEnvio();
            System.out.println("\n--- INFORMACIÓN DE ENVÍO ---");
            System.out.printf("Tracking: %s%n", envio.getTracking());
            System.out.printf("Empresa: %s%n", envio.getEmpresa());
            System.out.printf("Tipo: %s%n", envio.getTipo());
            System.out.printf("Costo: $%.2f%n", envio.getCosto());
            System.out.printf("Estado: %s%n", envio.getEstado());
        }
    }
}
