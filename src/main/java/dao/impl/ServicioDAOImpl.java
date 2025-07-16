package dao.impl;

import dao.ServicioDAO;
import dto.ServicioDTO;
import util.PostgreSQLConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOImpl implements ServicioDAO {
    
    @Override
    public List<ServicioDTO> obtenerServiciosActivos() {
        List<ServicioDTO> servicios = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre_servicio, descripcion, precio, activo " +
                    "FROM servicio WHERE activo = true ORDER BY nombre_servicio";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ServicioDTO servicio = new ServicioDTO();
                servicio.setIdServicio(rs.getInt("id_servicio"));
                servicio.setNombreServicio(rs.getString("nombre_servicio"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecio(rs.getBigDecimal("precio"));
                servicio.setActivo(rs.getBoolean("activo"));
                servicios.add(servicio);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener servicios activos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return servicios;
    }
    
    @Override
    public ServicioDTO obtenerServicioPorId(int idServicio) {
        String sql = "SELECT id_servicio, nombre_servicio, descripcion, precio, activo " +
                    "FROM servicio WHERE id_servicio = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idServicio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ServicioDTO servicio = new ServicioDTO();
                    servicio.setIdServicio(rs.getInt("id_servicio"));
                    servicio.setNombreServicio(rs.getString("nombre_servicio"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecio(rs.getBigDecimal("precio"));
                    servicio.setActivo(rs.getBoolean("activo"));
                    return servicio;
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener servicio por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<ServicioDTO> obtenerTodosServicios() {
        List<ServicioDTO> servicios = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre_servicio, descripcion, precio, activo " +
                    "FROM servicio ORDER BY nombre_servicio";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ServicioDTO servicio = new ServicioDTO();
                servicio.setIdServicio(rs.getInt("id_servicio"));
                servicio.setNombreServicio(rs.getString("nombre_servicio"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecio(rs.getBigDecimal("precio"));
                servicio.setActivo(rs.getBoolean("activo"));
                servicios.add(servicio);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener todos los servicios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return servicios;
    }
    
    @Override
    public boolean crearServicio(ServicioDTO servicio) {
        String sql = "INSERT INTO servicio (nombre_servicio, descripcion, precio, activo) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, servicio.getNombreServicio());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setBigDecimal(3, servicio.getPrecio());
            stmt.setBoolean(4, servicio.isActivo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al crear servicio: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean actualizarServicio(ServicioDTO servicio) {
        String sql = "UPDATE servicio SET nombre_servicio = ?, descripcion = ?, " +
                    "precio = ?, activo = ? WHERE id_servicio = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, servicio.getNombreServicio());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setBigDecimal(3, servicio.getPrecio());
            stmt.setBoolean(4, servicio.isActivo());
            stmt.setInt(5, servicio.getIdServicio());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al actualizar servicio: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean desactivarServicio(int idServicio) {
        String sql = "UPDATE servicio SET activo = false WHERE id_servicio = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idServicio);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al desactivar servicio: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
