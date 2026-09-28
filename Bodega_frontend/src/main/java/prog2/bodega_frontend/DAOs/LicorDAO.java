package prog2.bodega_frontend.daos;

import java.io.InputStream;
import prog2.bodega_frontend.dtos.LicorDTO;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;

public class LicorDAO {

    private final String URL_BACKEND = "http://localhost:8080/Bodega_backend/";
    private final HttpClient cliente = HttpClient.newHttpClient();

    public List<LicorDTO> buscarLicores(String tipo) throws Exception {
        List<LicorDTO> lista = new ArrayList<>();
        String parametro = (tipo != null && !tipo.trim().isEmpty())
                ? "?tipo=" + URLEncoder.encode(tipo.trim(), StandardCharsets.UTF_8)
                : "";
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
                    String[] claveValor = prop.split(":", 2);
                    if (claveValor.length >= 2) {
                        String clave = claveValor[0].replace("\"", "").trim();
                        String valor = claveValor[1].replace("\"", "").trim();

                        // Comparo sin distinguir mayúsculas de minúsculas.
                        if (clave.equalsIgnoreCase("id")) {
                            try {
                                idVal = Integer.parseInt(valor);
                            } catch (NumberFormatException e) {
                            }
                        }
                        if (clave.equalsIgnoreCase("tipo")) {
                            tipoVal = valor;
                        }
                        if (clave.equalsIgnoreCase("marca")) {
                            marcaVal = valor;
                        }
                        if (clave.equalsIgnoreCase("foto")) {
                            fotoVal = valor;
                        }
                    }
                }
                lista.add(new LicorDTO(idVal, tipoVal, marcaVal, fotoVal));
            }
        }
        return lista;
    }

    public void crear(LicorDTO licor, InputStream fotoInputStream) throws Exception {
        enviarFormularioMultipart(URL_BACKEND + "insertar", licor, "POST", null, fotoInputStream);
    }

    public void actualizar(LicorDTO licor, String id, InputStream fotoInputStream) throws Exception {
        enviarFormularioMultipart(URL_BACKEND + "actualizar", licor, "PUT", id, fotoInputStream);
    }

    public void eliminar(int id) throws Exception {
        String urlDelete = URL_BACKEND + "eliminar?id=" + id;
        HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(urlDelete)).DELETE().build();
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP " + respuesta.statusCode() + ": " + respuesta.body());
        }
    }

    private void enviarFormularioMultipart(String url, LicorDTO licor, String metodo, String id, InputStream fotoInputStream) throws Exception {
        String boundary = "----VaadinFormBoundary123456";
        String nombreFoto = (fotoInputStream != null && licor.getFoto() != null && !licor.getFoto().trim().isEmpty())
                ? licor.getFoto()
                : null;

        // Construyo manualmente el cuerpo multipart que voy a enviar al backend.
        ByteArrayOutputStream body = new ByteArrayOutputStream();

        if (id != null && !id.isEmpty()) {
            body.write(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"id\"\r\n\r\n"
                    + id + "\r\n").getBytes(StandardCharsets.UTF_8));
        }

        body.write(("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"tipo\"\r\n\r\n"
                + licor.getTipo() + "\r\n"
                + "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"marca\"\r\n\r\n"
                + licor.getMarca() + "\r\n").getBytes(StandardCharsets.UTF_8));

        // Envío la foto solamente si recibí un archivo desde el formulario.
        if (fotoInputStream != null && nombreFoto != null) {

            body.write(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"foto\"; filename=\""
                    + nombreFoto + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));

            // Leo los bytes reales de la imagen y los agrego al cuerpo de la petición.
            body.write(fotoInputStream.readAllBytes());

            body.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }

        // Cierro el cuerpo multipart.
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary);

        if (metodo.equals("POST")) {
            // Envío la petición mediante POST.
            builder.POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()));

        } else if (metodo.equals("PUT")) {
            // Envío la petición mediante PUT.
            builder.PUT(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()));

        } else {
            throw new IllegalArgumentException("Método HTTP no soportado: " + metodo);
        }

        HttpResponse<String> respuesta = cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (respuesta.statusCode() != 200) {
            throw new Exception("Error HTTP " + respuesta.statusCode() + ": " + respuesta.body());
        }

        // Obtengo el ID generado por el backend y lo asigno al licor para poder utilizarlo después en el front.
        String json = respuesta.body().trim();

        int posicionId = json.indexOf("\"id\"");

        if (posicionId == -1) {
            throw new Exception("El backend no devolvió el ID.");
        }

        int inicioNumero = json.indexOf(":", posicionId) + 1;
        int finNumero = json.indexOf("}", inicioNumero);

        int idGenerado = Integer.parseInt(
                json.substring(inicioNumero, finNumero).trim()
        );

        licor.setId(idGenerado);
    }
}
