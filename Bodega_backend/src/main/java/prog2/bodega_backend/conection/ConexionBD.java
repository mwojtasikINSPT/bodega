package prog2.bodega_backend.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionBD {

    public static Connection getConexion() throws SQLException {
        Properties properties = new Properties();
        
        // Cargamos el archivo desde el Classpath (src/main/resources)
        try (InputStream is = ConexionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new SQLException("No se encontró el archivo config.properties en src/main/resources.");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new SQLException("Error al leer el archivo de configuración config.properties.", e);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver de MySQL no encontrado en el proyecto.", e);
        }
        
        return DriverManager.getConnection(url, user, password);
    }
}