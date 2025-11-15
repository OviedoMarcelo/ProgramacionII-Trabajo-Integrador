/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import dao.EnvioDAO;
import dao.PedidoDAO;
import java.util.Scanner;
import service.EnvioService;
import service.PedidoService;

/**
 * Orquestador principal del sistema de gestión Pedido-Envío.
 *
 * Responsabilidades: - Gestionar el ciclo de vida de la aplicación -
 * Inicializar y conectar todas las dependencias (DAO → Service → Handler) -
 * Controlar el loop principal del menú - Manejar errores a nivel de aplicación
 *
 * Arquitectura: Controller + Dependency Injection manual Capas: Main → Service
 * → DAO → Database
 *
 * @author fede
 */
public class AppMenu {

    /**
     * Scanner único para toda la aplicación. CRÍTICO: Una sola instancia evita
     * problemas de buffering con System.in
     */
    private final Scanner scanner;
    /**
     * Handler que contiene toda la lógica de interacción con el usuario
     */
    private final MenuHandler menuHandler;
    /**
     * Flag que controla el estado de ejecución de la aplicación true =
     * aplicación corriendo, false = aplicación debe terminar
     */
    private boolean ejecutando;

    /**
     * Constructor que inicializa toda la aplicación.
     *
     * Flujo de inicialización (Dependency Injection manual): 1. Crea Scanner
     * único 2. Construye cadena de dependencias: DAOs → Services → Handler 3.
     * Establece estado inicial de ejecución
     */
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.ejecutando = true;

        // Inicializar servicios
        PedidoService pedidoService = crearServicios();
        this.menuHandler = new MenuHandler(scanner, pedidoService);
    }

    /**
     * Loop principal de la aplicación.
     *
     * Flujo: 1. Muestra menú principal 2. Lee y valida entrada del usuario 3.
     * Ejecuta operación correspondiente 4. Repite hasta que usuario selecciona
     * salir 5. Cierra recursos antes de terminar
     *
     * Manejo de errores robusto: - NumberFormatException: entrada no numérica -
     * Exception genérica: errores inesperados - La aplicación NUNCA se cae por
     * excepciones no controladas
     */
    public void ejecutar() {
        System.out.println("=== SISTEMA DE GESTIÓN PEDIDOS-ENVÍOS ===");

        while (ejecutando) {
            try {
                mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
        // Cleanup de recursos
        scanner.close();
        System.out.println("Aplicación finalizada.");
    }

    /**
     * Muestra el menú principal de opciones al usuario.
     *
     * CRUD Completo requerido por el TP: - Crear (1), Leer (2,3,4), Actualizar
     * (5,6), Eliminar (7) - Búsquedas específicas (3,4,8) cumplen con requisito
     * de búsqueda por campo relevante
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Crear Pedido con Envío");
        System.out.println("2. Listar todos los Pedidos");
        System.out.println("3. Buscar Pedido por Número");
        System.out.println("4. Buscar Pedido por Cliente");
        System.out.println("5. Actualizar Pedido");
        System.out.println("6. Actualizar Estado de Envío");
        System.out.println("7. Eliminar Pedido (lógico)");
        System.out.println("8. Listar Envíos por Empresa");
        System.out.println("9. Ver Estadísticas");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Procesa la opción seleccionada delegando al MenuHandler.
     *
     * Switch expression: más conciso y legible Cada caso delega a un método
     * específico del handler
     *
     * @param opcion Número entre 0-9 seleccionado por el usuario
     */
    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 ->
                menuHandler.crearPedidoConEnvio();
            case 2 ->
                menuHandler.listarTodosLosPedidos();
            case 3 ->
                menuHandler.buscarPedidoPorNumero();
            case 4 ->
                menuHandler.buscarPedidoPorCliente();
            case 5 ->
                menuHandler.actualizarPedido();
            case 6 ->
                menuHandler.actualizarEstadoEnvio();
            case 7 ->
                menuHandler.eliminarPedido();
            case 8 ->
                menuHandler.listarEnviosPorEmpresa();
            case 9 ->
                menuHandler.mostrarEstadisticas();
            case 0 -> {
                System.out.println("Saliendo del sistema...");
                ejecutando = false;
            }
            default ->
                System.out.println("Opción no válida. Intente nuevamente.");
        }
    }

    /**
     * Factory method que construye la cadena de dependencias.
     *
     * Arquitectura en capas resultante: MenuHandler → PedidoService →
     * [PedidoDAO, EnvioService] → [EnvioDAO]
     *
     * Relación 1→1 unidireccional implementada: - PedidoService coordina la
     * creación de Pedido y Envío en misma transacción - Pedido referencia a
     * Envio, pero Envio NO referencia a Pedido
     *
     * @return PedidoService completamente inicializado con todas sus
     * dependencias
     */
    private PedidoService crearServicios() {
        // 1) Crear los DAOs que hablan con la base de datos
        EnvioDAO envioDAO = new EnvioDAO();
        PedidoDAO pedidoDAO = new PedidoDAO();

        // 2) Crear el servicio de pedidos usando esos DAOs
        return new PedidoService(pedidoDAO, envioDAO);
    
    }
}
