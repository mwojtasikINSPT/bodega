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
import java.sql.SQLException;
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

    //peticiones GET para listar los licores.
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
            // Obtengo el tipo enviado como parámetro; si no se envía, obtengo null.
            String tipoSeleccionado = request.getParameter("tipo");

            List<LicorDTO> listaLicores;
            // Si recibo null muestro todos, si no, por tipo
            if (tipoSeleccionado == null || tipoSeleccionado.trim().isEmpty()) {
                listaLicores = dao.obtenerTodos();
            } else {
                listaLicores = dao.obtenerPorTipo(tipoSeleccionado);
            }

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

    // Peticiones POST para insertar licores
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
            // Obtengo los datos enviados para el nuevo licor.
            String tipo = request.getParameter("tipo");
            String marca = request.getParameter("marca");

            // Verifico que los datos obligatorios no sean nulos ni estén vacíos.
            if (tipo == null || tipo.trim().isEmpty()
                    || marca == null || marca.trim().isEmpty()) {

                // Informo que faltan datos necesarios para realizar la inserción.
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                        "{\"error\":\"Tipo y marca son datos obligatorios.\"}"
                );
                return;
            }

            // Elimino espacios innecesarios
            tipo = tipo.trim();
            marca = marca.trim();

            Part archivoPart = null;
            try {
                archivoPart = request.getPart("foto");
            } catch (ServletException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"La petición no fue enviada como multipart/form-data.\"}");
                return; // Cortamos la ejecución
            }

            // Guardo una referencia al archivo físico para poder eliminarlo si falla la inserción.
            File archivoGuardado = null;
            // Defino imagen predeterminada para los registros sin foto.
            String nombreArchivo = "noimage.png";

            // Verifico si recibí una foto válida.
            if (archivoPart != null
                    && archivoPart.getSize() > 0
                    && archivoPart.getSubmittedFileName() != null
                    && !archivoPart.getSubmittedFileName().isEmpty()) {

                // Obtengo solamente el nombre del archivo para evitar guardar rutas enviadas por el cliente.
                nombreArchivo = Path.of(archivoPart.getSubmittedFileName())
                        .getFileName()
                        .toString();

                // Obtengo la ruta física donde voy a guardar la foto recibida.
                String rutaUploads = getServletContext().getRealPath("/resources/img");
                //DEBUG 
                System.out.println("ARCHIVO: " + rutaUploads + File.separator + nombreArchivo);
                System.out.println("URL CONTEXTO: " + request.getContextPath());

                // Creo la carpeta de destino si todavía no existe.
                File carpetaDestino = new File(rutaUploads);

                if (!carpetaDestino.exists() && !carpetaDestino.mkdirs()) {
                    throw new IOException("No se pudo crear la carpeta de imágenes.");
                }

                // Guardo físicamente la foto y conservo una referencia al archivo creado.
                archivoPart.write(rutaUploads + File.separator + nombreArchivo);
                archivoGuardado = new File(rutaUploads, nombreArchivo);
            }

            LicorDTO nuevoLicor = new LicorDTO();
            nuevoLicor.setTipo(tipo);
            nuevoLicor.setMarca(marca);
            nuevoLicor.setFoto(nombreArchivo);

            try {
                dao.insertar(nuevoLicor);
                response.getWriter().write(
                        "{\"estado\":\"Insertado correctamente\", \"id\":" + nuevoLicor.getId() + "}"
                );
                
            } catch (Exception e) {

                // Si guardé una foto nueva y falló la inserción, elimino el archivo
                if (archivoGuardado != null && archivoGuardado.exists()) {
                    archivoGuardado.delete();
                }
                // Propago la excepción para que el manejo general del método informe el error.
                throw e;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    //Peticiones PUT  para actualizar registros
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

        LicorDAO dao = new LicorDAO();
        try {
            String idParametro = request.getParameter("id");

            if (idParametro == null || idParametro.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"El ID es obligatorio.\"}");
                return;
            }

            int id;
            try {
                // Convierto el ID recibido a número.
                id = Integer.parseInt(idParametro.trim());
            } catch (NumberFormatException e) {
                // Informo que el ID debe ser numérico.
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"El ID debe ser un número válido.\"}");
                return;
            }

            // 1. Busco el registro original en la BBDD
            LicorDTO licorActual = dao.obtenerPorId(id);
            if (licorActual == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"El ID proporcionado no existe.\"}");
                return;
            }

            // 2. Capturo los parámetros enviados 
            String tipo = request.getParameter("tipo");
            String marca = request.getParameter("marca");

            // 3. Si recibe algo, pisamos. Si vacío, queda el actual.
            if (tipo != null && !tipo.trim().isEmpty()) {
                licorActual.setTipo(tipo.trim());
            }
            if (marca != null && !marca.trim().isEmpty()) {
                licorActual.setMarca(marca.trim());
            }

            // 4. Misma Lógica para la foto: Si recibe nueva, guardo. Si no, queda la actual.
            Part archivoPart = null;
            try {
                archivoPart = request.getPart("foto");
            } catch (ServletException e) {
                // Continúo sin cambiar la foto si no puedo obtener una nueva.
                throw new IOException("No se pudo procesar el archivo enviado.", e);
            }

            // Guardo una referencia al archivo nuevo para eliminarlo si falla la actualización.
            //File archivoGuardado = null;
            if (archivoPart != null && archivoPart.getSize() > 0) {
                String submittedFileName = archivoPart.getSubmittedFileName();
                if (submittedFileName != null && !submittedFileName.isEmpty()) {
                    String nombreArchivo = Path.of(submittedFileName).getFileName().toString();
                    String rutaUploads = getServletContext().getRealPath("/resources/img");
                    File carpetaDestino = new File(rutaUploads);

                    if (!carpetaDestino.exists() && !carpetaDestino.mkdirs()) {
                        throw new IOException("No se pudo crear la carpeta de imágenes.");
                    }
                    archivoPart.write(rutaUploads + File.separator + nombreArchivo);
                    //archivoGuardado = new File(rutaUploads, nombreArchivo);
                    // Actualizo el objeto con el nombre de la nueva foto
                    licorActual.setFoto(nombreArchivo);
                }
            }

            // 5. Guardo el objeto
            dao.actualizar(licorActual);
            response.getWriter().write("{\"estado\":\"Registro actualizado correctamente\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // Peticiones DELETE -> /eliminar
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

            if (idStr != null && !idStr.trim().isEmpty()) {
                int id;

                try {
                    id = Integer.parseInt(idStr.trim());
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"El ID debe ser un número válido.\"}");
                    return;
                }

                // Evaluo si se eliminó algo en la BBDD
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
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
