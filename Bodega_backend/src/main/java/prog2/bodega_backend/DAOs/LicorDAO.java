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

    public void insertar(LicorDTO licor) throws SQLException {

        // el ID lo genera BBDD
        String sql = "INSERT INTO licores (tipo, marca, foto) VALUES (?, ?, ?)";

        // Abro la conexión y preparo la sentencia.
        // El try-with-resources se encarga de cerrar ambos recursos al finalizar.
        try (Connection con = ConexionBD.getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            //Asigno campo a cada parametro
            pstmt.setString(1, licor.getTipo());
            pstmt.setString(2, licor.getMarca());
            pstmt.setString(3, licor.getFoto());

            // Ejecuto el INSERT y guardo la cantidad de filas afectadas.
            int filasAfectadas = pstmt.executeUpdate();

            // Verifico que realmente se haya insertado un registro.
            if (filasAfectadas != 1) {
                throw new SQLException("No se pudo insertar el licor.");
            }
        }
    }

    public List<LicorDTO> obtenerPorTipo(String tipoBuscado) throws SQLException {

        // Creo una lista donde voy a guardar los licores encontrados.
        List<LicorDTO> licores = new ArrayList<>();

        // Defino la consulta y filtro los resultados por el tipo recibido.
        String sql = "SELECT id, tipo, marca, foto FROM licores WHERE tipo = ?";

        try (Connection con = ConexionBD.getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Asigno el tipo buscado al parámetro de la consulta.
            pstmt.setString(1, tipoBuscado);

            // Ejecuto la consulta y obtengo el conjunto de resultados.
            try (ResultSet rs = pstmt.executeQuery()) {

                // Recorro cada fila que devuelve la base de datos.
                while (rs.next()) {

                    // Creo un DTO para representar la fila actual.
                    LicorDTO licor = new LicorDTO();

                    // Obtengo datos almacenados
                    licor.setId(rs.getInt("id"));
                    licor.setTipo(rs.getString("tipo"));
                    licor.setMarca(rs.getString("marca"));
                    licor.setFoto(rs.getString("foto"));

                    // Agrego el DTO completo a la lista de resultados.
                    licores.add(licor);
                }
            }
        }
        //Devuelvo la lista completa
        return licores;
    }

    public void actualizar(LicorDTO licor) throws SQLException {

        // sentencia SQL para actualizar el licor.
        // El ID determina qué registro voy a modificar.
        String sql = "UPDATE licores SET tipo = ?, marca = ?, foto = ? WHERE id = ?";

        try (Connection conn = ConexionBD.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asigno params
            stmt.setString(1, licor.getTipo());
            stmt.setString(2, licor.getMarca());
            stmt.setString(3, licor.getFoto());
            stmt.setInt(4, licor.getId());

            // Ejecuto el UPDATE y guardo la cantidad de filas modificadas.
            int filasAfectadas = stmt.executeUpdate();

            // Verifico que la operación haya modificado exactamente un registro.
            if (filasAfectadas != 1) {
                throw new SQLException("No se pudo actualizar el licor con ID " + licor.getId());
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {

        // Uso ? como parám para colocar el ID de forma segura mediante PreparedStatement.
        String sql = "DELETE FROM licores WHERE id = ?";

        try (Connection conn = ConexionBD.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asigno el ID recibido al parámetro de la consulta.
            stmt.setInt(1, id);

            // Ejecuto el DELETE y obtengo la cantidad de filas eliminadas.
            int filasBorradas = stmt.executeUpdate();

            // Si eliminé una fila, informo que la operación fue exitosa.
            if (filasBorradas == 1) {
                return true;
            }
            // Si no eliminé ninguna fila, el ID no existe en la base de datos.
            return false;
        }
    }

    public LicorDTO obtenerPorId(int id) throws SQLException {

        // Inicio sin ningún licor porque todavía no sé si el ID existe.
        LicorDTO licor = null;

        String sql = "SELECT id, tipo, marca, foto FROM licores WHERE id = ?";

        try (Connection conn = ConexionBD.getConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asigno el ID recibido al primer parámetro de la consulta.
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                // Si encontré una fila, creo el DTO y cargo sus datos.
                if (rs.next()) {

                    // Creo el DTO que voy a devolver.
                    licor = new LicorDTO();

                    // Recupero datos almacenados
                    licor.setId(rs.getInt("id"));
                    licor.setTipo(rs.getString("tipo"));
                    licor.setMarca(rs.getString("marca"));
                    licor.setFoto(rs.getString("foto"));
                }
            }
        }
        // Devuelvo el licor encontrado o null si el ID no existe.
        return licor;
    }

    public List<LicorDTO> obtenerTodos() throws SQLException {

        // Creo una lista donde voy a guardar todos los licores encontrados.
        List<LicorDTO> licores = new ArrayList<>();

        // Selecciono todos los registros de la tabla licores.
        String sql = "SELECT id, tipo, marca, foto FROM licores";

        try (Connection con = ConexionBD.getConexion(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {

                // Recorro todas las filas devueltas por la base de datos.
                while (rs.next()) {

                    // Creo un DTO para representar la fila actual.
                    LicorDTO licor = new LicorDTO();

                    // Recupero datos x fila
                    licor.setId(rs.getInt("id"));
                    licor.setTipo(rs.getString("tipo"));
                    licor.setMarca(rs.getString("marca"));
                    licor.setFoto(rs.getString("foto"));

                    // Agrego el DTO a la lista de resultados.
                    licores.add(licor);
                }
            }
        }
        // Devuelvo  lista completa
        return licores;
    }

}
