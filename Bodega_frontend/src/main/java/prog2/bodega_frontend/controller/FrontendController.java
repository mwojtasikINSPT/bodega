package prog2.bodega_frontend.controller;

import prog2.bodega_frontend.views.MainView;
import prog2.bodega_frontend.dtos.LicorDTO;
import prog2.bodega_frontend.daos.LicorDAO;
import com.vaadin.flow.component.notification.Notification;

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
        String categoria = v.getCategoriaIngresada();
        String marca = v.getMarcaIngresada();
        String foto = v.getFotoIngresada();

        if (categoria.isEmpty() || marca.isEmpty()) {
            Notification.show("Error: Categoría y Marca son obligatorios");
            return;
        }

        try {
            LicorDTO nuevoLicor = new LicorDTO(categoria, marca, foto);
            dao.crear(nuevoLicor);
            refrescarGrilla("");
            Notification.show("Registro creado correctamente");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void procesarConsulta() {
        String categoriaFiltro = v.getCategoriaIngresada();
        try {
            List<LicorDTO> listaActualizada = dao.buscarLicores(categoriaFiltro);
            v.setLicoresEnGrilla(listaActualizada);
            Notification.show("Éxito: " + listaActualizada.size() + " registros recuperados.");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 10000, Notification.Position.MIDDLE);
        }
    }

    private void procesarActualizacion() {
        String idTexto = v.getIdIngresado();
        String categoria = v.getCategoriaIngresada();
        String marca = v.getMarcaIngresada();
        String foto = v.getFotoIngresada();

        if (idTexto.isEmpty() || categoria.isEmpty() || marca.isEmpty()) {
            Notification.show("Error: ID, Categoría y Marca son obligatorios para actualizar");
            return;
        }

        try {
            LicorDTO licorModificado = new LicorDTO(categoria, marca, foto);
            dao.actualizar(licorModificado, idTexto);
            refrescarGrilla("");
            Notification.show("Registro actualizado correctamente");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void procesarEliminacion() {
        String idTexto = v.getIdIngresado();
        if (idTexto.isEmpty()) {
            Notification.show("Error: Debe ingresar un ID para eliminar");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            dao.eliminar(id);
            refrescarGrilla("");
            Notification.show("Registro eliminado definitivamente");
        } catch (NumberFormatException e) {
            Notification.show("Error: Formato de ID inválido");
        } catch (Exception e) {
            Notification.show("FALLO: " + e.getMessage(), 8000, Notification.Position.MIDDLE);
        }
    }

    private void refrescarGrilla(String categoriaActiva) throws Exception {
        List<LicorDTO> listaActualizada = dao.buscarLicores(categoriaActiva);
        v.setLicoresEnGrilla(listaActualizada);
    }
}