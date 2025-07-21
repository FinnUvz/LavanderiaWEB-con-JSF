package dao.impl;

import dao.UsuarioDAO;
import dto.UsuarioDTO;
import util.PostgreSQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para operaciones de usuario
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    
    // Consultas SQL
    private static final String INSERT_USUARIO = 
        "INSERT INTO usuarios (nombre, correo, telefono, direccion, contrasena, rol) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_USUARIO_BY_CORREO = 
        "SELECT id_usuario, nombre, correo, telefono, direccion, contrasena, rol FROM usuarios WHERE correo = ?";
    
    private static final String SELECT_USUARIO_BY_ID = 
        "SELECT id_usuario, nombre, correo, telefono, direccion, contrasena, rol FROM usuarios WHERE id_usuario = ?";
    
    private static final String SELECT_ALL_USUARIOS = 
        "SELECT id_usuario, nombre, correo, telefono, direccion, contrasena, rol FROM usuarios";
    
    private static final String SELECT_USUARIOS_BY_ROL = 
        "SELECT id_usuario, nombre, correo, telefono, direccion, contrasena, rol FROM usuarios WHERE rol = ?";
    
    private static final String UPDATE_USUARIO = 
        "UPDATE usuarios SET nombre = ?, telefono = ?, direccion = ? WHERE id_usuario = ?";
    
    private static final String DELETE_USUARIO = 
        "DELETE FROM usuarios WHERE id_usuario = ?";
    
    private static final String UPDATE_PASSWORD = 
        "UPDATE usuarios SET contrasena = ? WHERE correo = ?";
    
    private static final String COUNT_CORREO = 
        "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
    /**
     * Método auxiliar para crear un UsuarioDTO desde un ResultSet
     */
    private UsuarioDTO crearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        return new UsuarioDTO(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("correo"),
            rs.getString("telefono"),
            rs.getString("direccion"),
            rs.getString("contrasena"),
            rs.getString("rol")
        );
    }
    
    @Override
    public boolean registrarUsuario(UsuarioDTO usuario) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USUARIO)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getDireccion());
            stmt.setString(5, usuario.getContrasena()); // Ya debe venir encriptada
            stmt.setString(6, usuario.getRol());
            
            int resultado = stmt.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public UsuarioDTO autenticarUsuario(String correo, String contrasena) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USUARIO_BY_CORREO)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String contrasenaDB = rs.getString("contrasena");
               
                return crearUsuarioDesdeResultSet(rs);
                
            }
            return null;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public UsuarioDTO buscarPorCorreo(String correo) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USUARIO_BY_CORREO)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }
            return null;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public UsuarioDTO buscarPorId(int id) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USUARIO_BY_ID)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return crearUsuarioDesdeResultSet(rs);
            }
            return null;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean existeCorreo(String correo) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_CORREO)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<UsuarioDTO> obtenerTodosUsuarios() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USUARIOS)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    @Override
    public List<UsuarioDTO> obtenerUsuariosPorRol(String rol) {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USUARIOS_BY_ROL)) {
            
            stmt.setString(1, rol);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    @Override
    public boolean actualizarUsuario(UsuarioDTO usuario) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USUARIO)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getTelefono());
            stmt.setString(3, usuario.getDireccion());
            stmt.setInt(4, usuario.getIdUsuario());
            
            int resultado = stmt.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean eliminarUsuario(int id) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USUARIO)) {
            
            stmt.setInt(1, id);
            int resultado = stmt.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean cambiarContrasena(String correo, String nuevaContrasena) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PASSWORD)) {
            
            stmt.setString(1, nuevaContrasena); // Ya debe venir encriptada
            stmt.setString(2, correo);
            
            int resultado = stmt.executeUpdate();
            return resultado > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}