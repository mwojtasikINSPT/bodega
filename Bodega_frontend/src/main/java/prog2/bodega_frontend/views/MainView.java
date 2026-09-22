package prog2.bodega_frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import prog2.bodega_frontend.dtos.LicorDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        H1 titulo = new H1("Bodega Hielo, Dilema y Pasión");

        Select<String> selectorTipo = new Select<>();
        selectorTipo.setLabel("Seleccione un tipo de licor:");
        selectorTipo.setItems("Ron", "Whisky", "Pisco", "Cerveza", "Vino");
        selectorTipo.setValue("ron");

        Button botonMostrar = new Button("Mostrar");

        Grid<LicorDTO> grilla = new Grid<>(LicorDTO.class, false);
        grilla.addColumn(LicorDTO::getCategoria).setHeader("Categoría");
        grilla.addColumn(LicorDTO::getMarca).setHeader("Marca del Licor");

        //instanciar el archivo de imagen
        Image imagenSoldOut = new Image("img/soldout.png", "Sin inventario disponible");
        imagenSoldOut.setWidth("150px");
        imagenSoldOut.setVisible(false);

        grilla.addComponentColumn(licor -> {
            Image img = new Image("img/" + licor.getFoto(), licor.getMarca());
            img.setWidth("90px");
            img.setHeight("120px");
            img.getStyle().set("object-fit", "contain");
            return img;
        }).setHeader("Presentación");

        botonMostrar.addClickListener(event -> {
            String tipo = selectorTipo.getValue();
            List<LicorDTO> licores = buscarEnBackend(tipo);

            if (licores.isEmpty()) {
                grilla.setVisible(false);
                imagenSoldOut.setVisible(true);
            } else {
                imagenSoldOut.setVisible(false);
                grilla.setVisible(true);
                grilla.setItems(licores);
            }
        });

        add(titulo, selectorTipo, botonMostrar, grilla, imagenSoldOut);
    }

    private List<LicorDTO> buscarEnBackend(String tipo) {
        List<LicorDTO> lista = new ArrayList<>();
        String urlBackend = "http://localhost:8080/Bodega_backend/buscarLicores?tipo=" + tipo;

        try {
            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest peticion = HttpRequest.newBuilder().uri(URI.create(urlBackend)).GET().build();
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            String jsonRaw = respuesta.body().trim();

            if (jsonRaw.length() > 2) {
                String limpio = jsonRaw.substring(1, jsonRaw.length() - 1);
                String[] objetosJson = limpio.split("\\},\\{");

                for (String obj : objetosJson) {
                    obj = obj.replace("{", "").replace("}", "");
                    String[] propiedades = obj.split(",");
                    String tipoVal = "", marcaVal = "", fotoVal = "";

                    for (String prop : propiedades) {
                        String[] claveValor = prop.split(":");
                        if (claveValor.length >= 2) {
                            String clave = claveValor[0].replace("\"", "").trim();
                            String valor = claveValor[1].replace("\"", "").trim();
                            if (clave.equals("tipo")) {
                                tipoVal = valor;
                            }
                            if (clave.equals("marca")) {
                                marcaVal = valor;
                            }
                            if (clave.equals("foto")) {
                                fotoVal = valor;
                            }
                        }
                    }
                    lista.add(new LicorDTO(tipoVal, marcaVal, fotoVal));
                }
            }
        } catch (Exception e) {
            System.out.println("Error de comunicación: " + e.getMessage());
        }
        return lista;
    }
}
