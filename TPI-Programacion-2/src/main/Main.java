package main;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Punto de entrada principal de la aplicación de gestión Pedido-Envío.
 *
 * Responsabilidad única: Iniciar la aplicación delegando a AppMenu.
 *
 * Patrón: Facade - proporciona una interfaz simple para un sistema complejo.
 *
 * Flujo de ejecución: 1. JVM ejecuta main() 2. Crea instancia de AppMenu
 * (ensambla todas las dependencias) 3. Ejecuta el menú principal 4. La
 * aplicación termina cuando el usuario selecciona "Salir"
 */
public class Main {

    /**
     * Método principal - punto de entrada estándar de Java
     *
     * @param args Argumentos de línea de comandos (no utilizados en esta
     * aplicación)
     */
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        AppMenu app = new AppMenu();
        app.ejecutar();
    }
}
