package prog2.bodega_frontend.daos;

import prog2.bodega_frontend.dtos.LicorDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LicorDAO {

    private final String URL_BACKEND = "http://localhost:8080/Bodega_backend/";
    private final HttpClient cliente = HttpClient.newHttpClient();

    public List<LicorDTO> buscarLicores(String tipo) throws Exception {
        List<LicorDTO> lista = new ArrayList<>();
        String parametro = (tipo != null && !tipo.trim().isEmpty()) ? "?tipo=" + tipo.trim().replace(" ", "%20") : "";
        String urlConsulta = URL_BACKEND + "listar" + parametro;

        HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(urlConsulta)).GET().build();
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        String jsonRaw = respuesta.body().trim();

        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP " + respuesta.statusCode() + " en la ruta: " + urlConsulta);
        }
        if (!jsonRaw.startsWith("[")) {
            throw new Exception("El backend no envió un array JSON.");
        }

        if (jsonRaw.length() > 2) {
            String limpio = jsonRaw.substring(1, jsonRaw.length() - 1);
            String[] objetosJson = limpio.split("\\},\\{");

            for (String obj : objetosJson) {
                obj = obj.replace("{", "").replace("}", "");
                String[] propiedades = obj.split(",");
                
                int idVal = 0;
                String tipoVal = "";
                String marcaVal = "";
                String fotoVal = "";

                for (String prop : propiedades) {
                    String[] claveValor = prop.split(":");
                    if (claveValor.length >= 2) {
                        String clave = claveValor[0].replace("\"", "").trim();
                        String valor = claveValor[1].replace("\"", "").trim();
                        
                        // Usamos equalsIgnoreCase para evitar problemas de mayúsculas/minúsculas del backend
                        if (clave.equalsIgnoreCase("id")) {
                            try { idVal = Integer.parseInt(valor); } catch (Exception ignored) {}
                        }
                        if (clave.equalsIgnoreCase("tipo") || clave.equalsIgnoreCase("categoria")) {
                            tipoVal = valor;
                        }
                        if (clave.equalsIgnoreCase("marca")) {
                            marcaVal = valor;
                        }
                        if (clave.equalsIgnoreCase("foto") || clave.equalsIgnoreCase("imagen")) {
                            fotoVal = valor;
                        }
                    }
                }
                lista.add(new LicorDTO(idVal, tipoVal, marcaVal, fotoVal));
            }
        }
        return lista;
    }

    public void crear(LicorDTO licor) throws Exception {
        enviarFormularioMultipart(URL_BACKEND + "insertar", licor, "POST", null);
    }

    public void actualizar(LicorDTO licor, String id) throws Exception {
        enviarFormularioMultipart(URL_BACKEND + "actualizar", licor, "PUT", id);
    }

    public void eliminar(int id) throws Exception {
        String urlDelete = URL_BACKEND + "eliminar?id=" + id;
        HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(urlDelete)).DELETE().build();
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        
        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP " + respuesta.statusCode() + " al eliminar.");
        }
    }

    private void enviarFormularioMultipart(String url, LicorDTO licor, String metodo, String id) throws Exception {
        String boundary = "----VaadinFormBoundary123456";
        String nombreFoto = (licor.getFoto() != null && !licor.getFoto().trim().isEmpty()) ? licor.getFoto() : "sin_foto.png";

        StringBuilder body = new StringBuilder();
        
        if (id != null && !id.isEmpty()) {
            body.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"id\"\r\n\r\n")
                .append(id).append("\r\n");
        }

        body.append("--").append(boundary).append("\r\n")
            .append("Content-Disposition: form-data; name=\"tipo\"\r\n\r\n")
            .append(licor.getCategoria()).append("\r\n")
            .append("--").append(boundary).append("\r\n")
            .append("Content-Disposition: form-data; name=\"marca\"\r\n\r\n")
            .append(licor.getMarca()).append("\r\n")
            .append("--").append(boundary).append("\r\n")
            .append("Content-Disposition: form-data; name=\"foto\"; filename=\"").append(nombreFoto).append("\"\r\n")
            .append("Content-Type: application/octet-stream\r\n\r\n")
            .append("contenido_simulado\r\n")
            .append("--").append(boundary).append("--\r\n");

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary);

        if (metodo.equals("POST")) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body.toString()));
        } else {
            builder.PUT(HttpRequest.BodyPublishers.ofString(body.toString()));
        }
        
        HttpResponse<String> respuesta = cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP " + respuesta.statusCode() + " en: " + url);
        }
    }
}