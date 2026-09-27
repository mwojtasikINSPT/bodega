package prog2.bodega_frontend.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import java.io.InputStream;
import prog2.bodega_frontend.dtos.LicorDTO;
import prog2.bodega_frontend.controller.FrontendController;

import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private Grid<LicorDTO> tablaLicores;
    private TextField campoId, campoTipo, campoMarca;
    private MemoryBuffer bufferFoto;
    private Upload campoUploadFoto;
    private String nombreFotoSeleccionada = "";

    private Button botonAlta, botonConsulta, botonActualizar, botonEliminar;

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        tablaLicores = new Grid<>(LicorDTO.class, false);
        tablaLicores.addColumn(LicorDTO::getId).setHeader("ID");
        tablaLicores.addColumn(LicorDTO::getTipo).setHeader("Tipo");
        tablaLicores.addColumn(LicorDTO::getMarca).setHeader("Marca");

        tablaLicores.addComponentColumn(licor -> {
            String nombreArchivo = (licor.getFoto() != null && !licor.getFoto().isEmpty()) ? licor.getFoto() : "noimage.png";

            // Ruta relativa directa a la carpeta webapp/img del frontend
            String rutaImagen = "img/" + nombreArchivo;
            if (nombreArchivo.equals("noimage.png")) {
                // Uso la imagen predeterminada del frontend.
                rutaImagen = "img/noimage.png";
            } else {
                // Uso la imagen almacenada en el backend.
                rutaImagen = "http://localhost:8080/Bodega_backend/resources/img/" + nombreArchivo;
            }

            Image imagen = new Image(rutaImagen, licor.getMarca());
            imagen.setHeight("80px");
            return imagen;
        }).setHeader("Foto");

        tablaLicores.setMinHeight("400px");
        tablaLicores.setSizeFull();

        campoId = new TextField("ID (Para Actualizar / Eliminar)");
        campoTipo = new TextField("Tipo");
        campoMarca = new TextField("Marca");

        bufferFoto = new MemoryBuffer();
        campoUploadFoto = new Upload(bufferFoto);
        campoUploadFoto.setAcceptedFileTypes("image/jpeg", "image/png");
        campoUploadFoto.setMaxFiles(1);

        campoUploadFoto.addSucceededListener(event -> {
            nombreFotoSeleccionada = event.getFileName();
        });

        botonAlta = new Button("Guardar Nuevo");
        botonConsulta = new Button("Mostrar Todos");
        botonActualizar = new Button("Actualizar");
        botonEliminar = new Button("Eliminar");

        botonAlta.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        botonActualizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        botonEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout formulario = new FormLayout(campoId, campoTipo, campoMarca, campoUploadFoto);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 4)
        );
        formulario.setWidthFull();

        HorizontalLayout botonera = new HorizontalLayout(botonConsulta, botonAlta, botonActualizar, botonEliminar);
        botonera.setWidthFull();
        botonera.setFlexGrow(1, botonConsulta, botonAlta, botonActualizar, botonEliminar);

        add(formulario, botonera, tablaLicores);

        new FrontendController(this);
    }

    public void setLicoresEnTabla(List<LicorDTO> licores) {
        tablaLicores.setItems(licores);
    }

    public String getIdIngresado() {
        return campoId.getValue();
    }

    public String getTipoIngresado() {
        return campoTipo.getValue();
    }

    public String getMarcaIngresada() {
        return campoMarca.getValue();
    }

    public String getFotoIngresada() {
        return nombreFotoSeleccionada;
    }

    public InputStream getFotoInputStream() {
        if (nombreFotoSeleccionada == null || nombreFotoSeleccionada.trim().isEmpty()) {
            return null;
        }
        return bufferFoto.getInputStream();
    }

    public void addBotonAltaListener(ComponentEventListener<ClickEvent<Button>> listener) {
        botonAlta.addClickListener(listener);
    }

    public void addBotonConsultaListener(ComponentEventListener<ClickEvent<Button>> listener) {
        botonConsulta.addClickListener(listener);
    }

    public void addBotonActualizarListener(ComponentEventListener<ClickEvent<Button>> listener) {
        botonActualizar.addClickListener(listener);
    }

    public void addBotonEliminarListener(ComponentEventListener<ClickEvent<Button>> listener) {
        botonEliminar.addClickListener(listener);
    }
}
