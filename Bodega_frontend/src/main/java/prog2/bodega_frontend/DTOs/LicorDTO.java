package prog2.bodega_frontend.dtos;

import java.io.Serializable;

public class LicorDTO implements Serializable {
    private int id;
    private String categoria;
    private String marca;
    private String foto;

    public LicorDTO(String categoria, String marca, String foto) {
        this.categoria = categoria;
        this.marca = marca;
        this.foto = foto;
    }

    public LicorDTO(int id, String categoria, String marca, String foto) {
        this.id = id;
        this.categoria = categoria;
        this.marca = marca;
        this.foto = foto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}