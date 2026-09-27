package prog2.bodega_frontend.dtos;

import java.io.Serializable;

public class LicorDTO implements Serializable {

    private int id;
    private String tipo;
    private String marca;
    private String foto;

    public LicorDTO(String tipo, String marca, String foto) {
        this.tipo = tipo;
        this.marca = marca;
        this.foto = foto;
    }

    public LicorDTO(int id, String tipo, String marca, String foto) {
        this.id = id;
        this.tipo = tipo;
        this.marca = marca;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
