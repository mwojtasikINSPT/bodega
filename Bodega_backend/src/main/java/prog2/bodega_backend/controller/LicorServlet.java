package prog2.bodega_backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import prog2.bodega_backend.daos.LicorDAO;
import prog2.bodega_backend.DTOs.LicorDTO;

// Habilitamos la configuración multipart y mapeamos las 4 rutas distintas al Servlet:
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
@WebServlet({"/listar", "/insertar", "/actualizar", "/eliminar"})
public class LicorServlet extends HttpServlet {

    // 1. GET -> /listar
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!request.getServletPath().equals("/listar")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Endpoint no válido para GET. Usa /listar\"}");
            return;
        }

        LicorDAO dao = new LicorDAO();
        try {
            String tipoSeleccionado = request.getParameter("tipo");
            List<LicorDTO> listaLicores = dao.obtenerPorTipo(tipoSeleccionado);
            List<String> jsonObjetos = new ArrayList<>();
            for (LicorDTO l : listaLicores) {
                String obj = "{"
                        + "\"id\":" + l.getId() + ","
                        + "\"tipo\":\"" + l.getTipo() + "\","
                        + "\"marca\":\"" + l.getMarca() + "\","
                        + "\"foto\":\"" + l.getFoto() + "\""
                        + "}";
                jsonObjetos.add(obj);
            }
            String jsonFinal = "[" + String.join(",", jsonObjetos) + "]";
            response.getWriter().write(jsonFinal);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // 2. POST -> /insertar
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!request.getServletPath().equals("/insertar")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Endpoint no válido para POST. Usa /insertar\"}");
            return;
        }

        LicorDAO dao = new LicorDAO();
        try {
            String tipo = request.getParameter("tipo");
            String marca = request.getParameter("marca");

            Part archivoPart = null;
            try {
                archivoPart = request.getPart("foto");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"La petición no fue enviada como multipart/form-data.\"}");
                return; // Cortamos la ejecución
            }

            String nombreArchivo = "";

// VALIDACIÓN ESTRICTA: Si la parte es nula, el archivo pesa 0 o no tiene nombre, cortamos.
            if (archivoPart == null || archivoPart.getSize() == 0 || archivoPart.getSubmittedFileName() == null || archivoPart.getSubmittedFileName().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"No se detectó el archivo físico adjunto en el campo 'foto'. Revisa Postman.\"}");
                return; // Cortamos la ejecución, el registro NO se guarda.
            }

// Si llega hasta aquí, el archivo vino bien. Lo procesamos:
            nombreArchivo = Path.of(archivoPart.getSubmittedFileName()).getFileName().toString();
            String rutaUploads = getServletContext().getRealPath("/resources/img");
            File carpetaDestino = new File(rutaUploads);
            if (!carpetaDestino.exists()) {
                carpetaDestino.mkdirs();
            }
            archivoPart.write(rutaUploads + File.separator + nombreArchivo);

// ... (continúa la creación del DTO y la llamada a dao.insertar)
            LicorDTO nuevoLicor = new LicorDTO();
            nuevoLicor.setTipo(tipo);
            nuevoLicor.setMarca(marca);
            nuevoLicor.setFoto(nombreArchivo);

            dao.insertar(nuevoLicor);
            response.getWriter().write("{\"estado\":\"Insertado correctamente\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // 3. PUT -> /actualizar
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!request.getServletPath().equals("/actualizar")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Endpoint no válido para PUT. Usa /actualizar\"}");
            return;
        }

        // ... inicio de tu método (doPut o doPost para /actualizar)
        LicorDAO dao = new LicorDAO();
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // 1. Buscamos el registro original en la base de datos
            LicorDTO licorActual = dao.obtenerPorId(id);
            if (licorActual == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"El ID proporcionado no existe.\"}");
                return; // Cortamos la ejecución
            }

            // 2. Capturamos los parámetros enviados desde Postman
            String tipo = request.getParameter("tipo");
            String marca = request.getParameter("marca");

            // 3. Mezclamos: Si enviaste algo, lo pisamos. Si lo dejaste vacío, mantenemos el actual.
            if (tipo != null && !tipo.trim().isEmpty()) {
                licorActual.setTipo(tipo);
            }
            if (marca != null && !marca.trim().isEmpty()) {
                licorActual.setMarca(marca);
            }

            // 4. Lógica para la foto: Si envías una nueva, la guardamos. Si no, queda la actual automáticamente.
            Part archivoPart = null;
            try {
                archivoPart = request.getPart("foto");
            } catch (Exception e) {
                // Ignoramos si Tomcat bloquea o no viene el archivo
            }

            if (archivoPart != null && archivoPart.getSize() > 0) {
                String submittedFileName = archivoPart.getSubmittedFileName();
                if (submittedFileName != null && !submittedFileName.isEmpty()) {
                    String nombreArchivo = Path.of(submittedFileName).getFileName().toString();
                    String rutaUploads = getServletContext().getRealPath("/resources/img");
                    File carpetaDestino = new File(rutaUploads);
                    if (!carpetaDestino.exists()) {
                        carpetaDestino.mkdirs();
                    }
                    archivoPart.write(rutaUploads + File.separator + nombreArchivo);
                    // Actualizamos el objeto con el nombre de la nueva foto
                    licorActual.setFoto(nombreArchivo);
                }
            }

            // 5. Guardamos el objeto ya mezclado en la base de datos
            dao.actualizar(licorActual);
            response.getWriter().write("{\"estado\":\"Registro actualizado de forma parcial correctamente\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // 4. DELETE -> /eliminar
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!request.getServletPath().equals("/eliminar")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Endpoint no válido para DELETE. Usa /eliminar\"}");
            return;
        }

        LicorDAO dao = new LicorDAO();
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);

                // Evaluamos si realmente se eliminó algo en la base de datos
                boolean borrado = dao.eliminar(id);

                if (borrado) {
                    response.getWriter().write("{\"estado\":\"Eliminado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"El ID " + id + " no existe o ya fue eliminado.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"ID no proporcionado en la URL (ej: ?id=11)\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
