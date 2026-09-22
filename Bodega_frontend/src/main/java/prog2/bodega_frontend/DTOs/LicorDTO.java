package prog2.bodega_frontend.dtos;

public class LicorDTO {
    private String categoria;
    private String marca;
    private String foto;

    public LicorDTO(String categoria, String marca, String foto) {
        this.categoria = categoria;
        this.marca = marca;
        this.foto = foto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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