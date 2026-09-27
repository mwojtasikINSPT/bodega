package prog2.bodega_backend.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionBD {

    public static Connection getConexion() throws SQLException {
        // Creo un objeto para almacenar los datos del archivo de configuración.
        Properties properties = new Properties();

        // Cargo el archivo desde el Classpath de la app
        try (InputStream is = ConexionBD.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            // Verifico que el archivo de configuración exista.
            if (is == null) {
                throw new SQLException("No se encontró el archivo config.properties en src/main/resources.");
            }
            //Cargo las propiedades del archivo.
            properties.load(is);
        } catch (IOException e) {
            throw new SQLException("Error al leer config.properties.", e);
        }

        //Obtengo datos para la conex
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        // Verifico que las propiedades necesarias estén definidas
        if (url == null || user == null || password == null) {
            throw new SQLException("Faltan propiedades de conexión en config.properties.");
        }
        
        // Cargo driver JDBC de MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver de MySQL no encontrado en el proyecto.", e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
