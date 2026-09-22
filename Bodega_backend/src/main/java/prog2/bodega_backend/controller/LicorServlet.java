package prog2.bodega_backend.controller;

import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException; 
import java.sql.Connection; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.util.ArrayList; 
import java.util.List; 
import prog2.bodega_backend.conection.ConexionBD; 
import prog2.bodega_backend.model.Licor; 

// Mapeamos el Servlet a la URL de respuesta: 
@WebServlet("/buscarLicores") 
public class LicorServlet extends HttpServlet { 

    @Override     
    protected void doGet(HttpServletRequest request, HttpServletResponse response)             
            throws ServletException, IOException { 

        // 1. Capturamos el parámetro "tipo" que viaja por la URL desde el cliente:         
        String tipoSeleccionado = request.getParameter("tipo"); 

        List<Licor> listaLicores = new ArrayList<>();         
        String sql = "SELECT * FROM licores WHERE tipo = ?"; 

        // 2. Conexión JDBC y consulta con PreparedStatement:         
        try (Connection con = ConexionBD.getConexion();              
             PreparedStatement ps = con.prepareStatement(sql)) { 

            ps.setString(1, tipoSeleccionado); 

            try (ResultSet rs = ps.executeQuery()) {                 
                while (rs.next()) {                     
                    Licor licor = new Licor(                             
                            rs.getString("tipo"),                             
                            rs.getString("marca"),                             
                            rs.getString("foto")                     
                    );                     
                    listaLicores.add(licor);                 
                }             
            } 

            // 3. Configuramos las cabeceras HTTP de respuesta:             
            response.setContentType("application/json");             
            response.setCharacterEncoding("UTF-8"); 

            // 4. Creamos una lista temporal para guardar cada objeto JSON como texto:             
            List<String> jsonObjetos = new ArrayList<>(); 

            // 5. Unimos las propiedades de cada licor:             
            for (Licor l : listaLicores) {                 
                String obj = "{"                         
                        + "\"tipo\":\"" + l.getTipo() + "\","                         
                        + "\"marca\":\"" + l.getMarca() + "\","                         
                        + "\"foto\":\"" + l.getFoto() + "\""                         
                        + "}";                 
                jsonObjetos.add(obj);             
            } 

            // 6. Envolvemos en corchetes:             
            String jsonFinal = "[" + String.join(",", jsonObjetos) + "]"; 

            // 7. Enviamos el resultado:             
            response.getWriter().write(jsonFinal); 

        } catch (Exception e) {             
            // Manejo elemental de errores en el servidor:             
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);             
            response.getWriter().write("Error en el Servidor (Backend): " + e.getMessage());         
        }     
    } 
}