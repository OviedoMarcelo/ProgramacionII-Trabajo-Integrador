package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DatabaseConnection {

    //  ResourceBundle permite leer archivos .properties sin necesidad de hardcodear valores
    //  Carga automáticamente el archivo "database.properties" desde el classpath
    //  Cargar el archivo de propiedades desde la carpeta config
    private static final ResourceBundle config = ResourceBundle.getBundle("config/database");
    
    // Datos de conexión - Se configuran directamente en el código
    private static final String URL = config.getString("db.url");
    private static final String USER = config.getString("db.user"); 
    private static final String PASSWORD = config.getString("db.password");

    static {
        try {
            // Carga del driver JDBC de MySQL una sola vez
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            //  Se lanza una excepción en caso de que el driver no esté disponible
            throw new RuntimeException("Error: No se encontró el driver JDBC.", e);
        }
    }

    /**
     * Método para obtener una conexión a la base de datos.
     * @return Connection si la conexión es exitosa.
     * @throws SQLException Si hay un problema al conectarse.
     */
    public static Connection getConnection() throws SQLException {
        // Validación adicional para asegurarse de que las credenciales no estén vacías
        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException("Configuración de la base de datos incompleta o inválida.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}