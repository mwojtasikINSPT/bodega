package prog2.bodega_backend.daos;

import prog2.bodega_backend.conection.ConexionBD;
import prog2.bodega_backend.DTOs.LicorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LicorDAO {

    public void insertar(LicorDTO licor) {
        String sql = "INSERT INTO licores (tipo, marca, foto) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, licor.getTipo());
            pstmt.setString(2, licor.getMarca());
            pstmt.setString(3, licor.getFoto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<LicorDTO> obtenerPorTipo(String tipoBuscado) {
        List<LicorDTO> licores = new ArrayList<>();
        String sql = "SELECT id, tipo, marca, foto FROM licores WHERE tipo = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tipoBuscado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LicorDTO licor = new LicorDTO();
                    licor.setId(rs.getInt("id"));
                    licor.setTipo(rs.getString("tipo"));
                    licor.setMarca(rs.getString("marca"));
                    licor.setFoto(rs.getString("foto"));
                    licores.add(licor);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return licores;
    }

    public void actualizar(LicorDTO licor) {
    // La sentencia UPDATE sobreescribe todos los campos, pero como el Servlet 
    // ya recuperó los valores viejos, no hay pérdida de información.
    String sql = "UPDATE licores SET tipo=?, marca=?, foto=? WHERE id=?";
    
    try (Connection conn = ConexionBD.getConexion(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, licor.getTipo());
        stmt.setString(2, licor.getMarca());
        stmt.setString(3, licor.getFoto());
        stmt.setInt(4, licor.getId());
        
        stmt.executeUpdate();
        
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

    public boolean eliminar(int id) {
    // Definimos la sentencia SQL con un '?' para evitar inyección de código
    String sql = "DELETE FROM licores WHERE id = ?";
    
    // El bloque try() abre la conexión y el statement, cerrándolos solos al terminar
    try (Connection conn = ConexionBD.getConexion(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Asignamos el número de ID al '?' de la consulta
        stmt.setInt(1, id);
        
        // executeUpdate() ejecuta la modificación y devuelve la cantidad de filas afectadas
        int filasBorradas = stmt.executeUpdate();
        
        // Si borró 1 o más filas, devuelve true. Si el ID no existía y borró 0 filas, devuelve false.
        return filasBorradas > 0;
        
    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
}
    public LicorDTO obtenerPorId(int id) {
    LicorDTO licor = null;
    String sql = "SELECT * FROM licores WHERE id = ?";
    
    try (Connection conn = ConexionBD.getConexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                licor = new LicorDTO();
                licor.setId(rs.getInt("id"));
                licor.setTipo(rs.getString("tipo"));
                licor.setMarca(rs.getString("marca"));
                licor.setFoto(rs.getString("foto"));
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return licor;
}
}