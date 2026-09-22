package prog2.bodega_backend.model;

public class Licor { 
    private String tipo;     
    private String marca;     
    private String foto; 

    // Constructor vacío (Obligatorio para estándares web):     
    public Licor() {}     
    
    // Constructor completo:     
    public Licor(String tipo, String marca, String foto) {         
        this.tipo = tipo;         
        this.marca = marca;         
        this.foto = foto;     
    }     
    
    // Getters y Setters     
    public String getTipo() { return tipo; }     
    public void setTipo(String tipo) { this.tipo = tipo; } 

    public String getMarca() { return marca; }     
    public void setMarca(String marca) { this.marca = marca; } 

    public String getFoto() { return foto; }     
    public void setFoto(String foto) { this.foto = foto; } 
}