package prog2.bodega_frontend.controller;

import prog2.bodega_frontend.views.MainView;
import prog2.bodega_frontend.dtos.LicorDTO;
import prog2.bodega_frontend.daos.LicorDAO;
import com.vaadin.flow.component.notification.Notification;
import java.io.InputStream;

import java.util.List;

public class FrontendController {

    private MainView v;
    private LicorDAO dao;

    public FrontendController(MainView v) {
        this.v = v;
        this.dao = new LicorDAO();
        ejecutar();
    }

    public void ejecutar() {
        v.addBotonAltaListener(event -> procesarAlta());
        v.addBotonConsultaListener(event -> procesarConsulta());
        v.addBotonActualizarListener(event -> procesarActualizacion());
        v.addBotonEliminarListener(event -> procesarEliminacion());
    }

    private void procesarAlta() {
        String tipo = v.getTipoIngresado();
        String marca = v.getMarcaIngresada();
        String foto = v.getFotoIngresada();
        InputStream fotoInputStream = v.getFotoInputStream();

        if (tipo.isEmpty() || marca.isEmpty()) {
            Notification.show("Error: Tipo y Marca son obligatorios");
            return;
        }

        try {
            LicorDTO nuevoLicor = new LicorDTO(tipo, marca, foto);
            dao.crear(nuevoLicor, fotoInputStream);
            refrescarVista("");
            v.limpiarCampos();
            Notification.show("Registro creado correctamente");
         
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void procesarConsulta() {
        String tipoFiltro = v.getTipoIngresado();
        try {
            List<LicorDTO> listaActualizada = dao.buscarLicores(tipoFiltro);
            v.setLicoresEnTabla(listaActualizada);
            Notification.show("Éxito: " + listaActualizada.size() + " registros recuperados.");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 10000, Notification.Position.MIDDLE);
        }
    }

    private void procesarActualizacion() {
        String idTexto = v.getIdIngresado();
        String tipo = v.getTipoIngresado();
        String marca = v.getMarcaIngresada();
        String foto = v.getFotoIngresada();
        InputStream fotoInputStream = v.getFotoInputStream();

        if (idTexto.isEmpty()) {
            Notification.show("Error: Debe ingresar un ID para actualizar");
            return;
        }

        try {
            LicorDTO licorModificado = new LicorDTO(tipo, marca, foto);
            dao.actualizar(licorModificado, idTexto, fotoInputStream);
            refrescarVista("");
            v.limpiarCampos();
            Notification.show("Registro actualizado correctamente");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void procesarEliminacion() {
        String idTexto = v.getIdIngresado();
        if (idTexto == null || idTexto.trim().isEmpty()) {
            Notification.show("Error: Debe ingresar un ID para eliminar");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto.trim());

            dao.eliminar(id);
            refrescarVista("");
            v.limpiarCampos();
            Notification.show("Registro eliminado correctamente");
        } catch (NumberFormatException e) {
            Notification.show("Error: Formato de ID inválido");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void refrescarVista(String tipoActivo) throws Exception {
        List<LicorDTO> listaActualizada = dao.buscarLicores(tipoActivo);
        v.setLicoresEnTabla(listaActualizada);
    }
}
