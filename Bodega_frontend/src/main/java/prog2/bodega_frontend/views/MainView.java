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
import prog2.bodega_frontend.dtos.LicorDTO;
import prog2.bodega_frontend.controller.FrontendController;

import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private Grid<LicorDTO> grilla;
    private TextField campoId, campoCategoria, campoMarca;
    private MemoryBuffer bufferFoto;
    private Upload campoUploadFoto;
    private String nombreFotoSeleccionada = "";

    private Button botonAlta, botonConsulta, botonActualizar, botonEliminar;

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        grilla = new Grid<>(LicorDTO.class, false);
        grilla.addColumn(LicorDTO::getId).setHeader("ID");
        grilla.addColumn(LicorDTO::getCategoria).setHeader("Categoría");
        grilla.addColumn(LicorDTO::getMarca).setHeader("Marca");

        grilla.addComponentColumn(licor -> {
            String nombreArchivo = (licor.getFoto() != null && !licor.getFoto().isEmpty()) ? licor.getFoto() : "sin_foto.png";

            // Ruta relativa directa a la carpeta webapp/img del frontend
            String rutaImagen = "img/" + nombreArchivo;

            Image imagen = new Image(rutaImagen, licor.getMarca());
            imagen.setHeight("80px");
            return imagen;
        }).setHeader("Foto");

        grilla.setMinHeight("400px");
        grilla.setSizeFull();

        campoId = new TextField("ID (Para Actualizar / Eliminar)");
        campoCategoria = new TextField("Categoría");
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

        FormLayout formulario = new FormLayout(campoId, campoCategoria, campoMarca, campoUploadFoto);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 4)
        );
        formulario.setWidthFull();

        HorizontalLayout botonera = new HorizontalLayout(botonConsulta, botonAlta, botonActualizar, botonEliminar);
        botonera.setWidthFull();
        botonera.setFlexGrow(1, botonConsulta, botonAlta, botonActualizar, botonEliminar);

        add(formulario, botonera, grilla);

        new FrontendController(this);
    }

    public void setLicoresEnGrilla(List<LicorDTO> licores) {
        grilla.setItems(licores);
    }

    public String getIdIngresado() {
        return campoId.getValue();
    }

    public String getCategoriaIngresada() {
        return campoCategoria.getValue();
    }

    public String getMarcaIngresada() {
        return campoMarca.getValue();
    }

    public String getFotoIngresada() {
        return nombreFotoSeleccionada;
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
