package prog2.bodega_frontend.controller;

import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException; 
import java.net.URI; 
import java.net.http.HttpClient; 
import java.net.http.HttpRequest; 
import java.net.http.HttpResponse; 
import java.util.ArrayList; 
import java.util.List; 

@WebServlet("/buscar") 
public class NavigationServlet extends HttpServlet { 

    @Override     
    protected void doGet(HttpServletRequest request, HttpServletResponse response)             
            throws ServletException, IOException { 

        // 1. Capturamos el tipo que el usuario eligió en el index.jsp:         
        String ruta = "http://localhost:8080/Bodega_backend/buscarLicores?tipo=";         
        String tipo = request.getParameter("tipo");         
        String urlBackend = ruta + tipo;         
        List<String> listaLicores = new ArrayList<>(); 

        // Corregido: Usamos un try estándar para el cliente HTTP
        try {              
            HttpClient cliente = HttpClient.newHttpClient();              
            HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(urlBackend)).GET().build();                         
            
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());             
            String jsonRaw = respuesta.body().trim(); 

            // 3. Procesamiento y descifrado manual de la cadena JSON:                         
            if (jsonRaw.length() > 2) {                                 
                String limpio = jsonRaw.substring(1, jsonRaw.length() - 1);                 
                String[] objetosJson = limpio.split("\\},\\{");                 
                
                for (String obj : objetosJson) {                     
                    obj = obj.replace("{", "").replace("}", "");                     
                    String[] propiedades = obj.split(",");                     
                    String tipoVal = "";                     
                    String marcaVal = "";                     
                    String fotoVal = ""; 

                    for (String prop : propiedades) {                         
                        String[] claveValor = prop.split(":");                         
                        if (claveValor.length >= 2) {                             
                            String clave = claveValor[0].replace("\"", "").trim();                             
                            String valor = claveValor[1].replace("\"", "").trim(); 

                            if (clave.equals("tipo"))  tipoVal = valor;                             
                            if (clave.equals("marca")) marcaVal = valor;                             
                            if (clave.equals("foto"))  fotoVal = valor;                         
                        }                     
                    }                     
                    
                    String lineaLicor = tipoVal + "|" + marcaVal + "|" + fotoVal;                     
                    listaLicores.add(lineaLicor);                 
                }             
            } 

        } catch (Exception e) {             
            System.out.println("Error de comunicación: " + e.getMessage());         
        } 

        // Enviamos la lista final a la vista resultados.jsp
        request.setAttribute("listaParaMostrar", listaLicores);         
        request.getRequestDispatcher("resultados.jsp").forward(request, response);     
    } 
}