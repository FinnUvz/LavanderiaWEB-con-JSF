package dao.impl;

import dao.PedidoDAO;
import dto.PedidoDTO;
import util.PostgreSQLConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {
    
    @Override
    public boolean crearPedido(PedidoDTO pedido) {
        String sql = "INSERT INTO pedido (id_usuario, id_servicio, fecha_pedido, fecha_recojo, " +
                    "hora_recojo, fecha_entrega, hora_entrega, metodo_entrega, metodo_pago, " +
                    "observaciones, estado, total, cantidad) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedido.getIdUsuario());
            stmt.setInt(2, pedido.getIdServicio());
            stmt.setDate(3, Date.valueOf(pedido.getFechaPedido()));
            stmt.setDate(4, Date.valueOf(pedido.getFechaRecojo()));
            stmt.setTime(5, Time.valueOf(pedido.getHoraRecojo()));
            stmt.setDate(6, Date.valueOf(pedido.getFechaEntrega()));
            stmt.setTime(7, Time.valueOf(pedido.getHoraEntrega()));
            stmt.setString(8, pedido.getMetodoEntrega());
            stmt.setString(9, pedido.getMetodoPago());
            stmt.setString(10, pedido.getObservaciones());
            stmt.setString(11, pedido.getEstado());
            stmt.setBigDecimal(12, pedido.getTotal());
            stmt.setInt(13, pedido.getCantidad());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public List<PedidoDTO> obtenerPedidosPorUsuario(int idUsuario) {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, s.nombre_servicio, u.nombres || ' ' || u.apellidos as nombre_usuario " +
                    "FROM pedido p " +
                    "JOIN servicio s ON p.id_servicio = s.id_servicio " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.id_usuario = ? " +
                    "ORDER BY p.fecha_pedido DESC";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetAPedido(rs);
                    pedidos.add(pedido);
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener pedidos por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pedidos;
    }
    
    @Override
    public PedidoDTO obtenerPedidoPorId(int idPedido) {
        String sql = "SELECT p.*, s.nombre_servicio, u.nombres || ' ' || u.apellidos as nombre_usuario " +
                    "FROM pedido p " +
                    "JOIN servicio s ON p.id_servicio = s.id_servicio " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.id_pedido = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPedido);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPedido(rs);
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener pedido por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<PedidoDTO> obtenerTodosPedidos() {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, s.nombre_servicio, u.nombres || ' ' || u.apellidos as nombre_usuario " +
                    "FROM pedido p " +
                    "JOIN servicio s ON p.id_servicio = s.id_servicio " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "ORDER BY p.fecha_pedido DESC";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PedidoDTO pedido = mapearResultSetAPedido(rs);
                pedidos.add(pedido);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener todos los pedidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pedidos;
    }
    
    @Override
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) {
        String sql = "UPDATE pedido SET estado = ? WHERE id_pedido = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idPedido);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al actualizar estado del pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public List<PedidoDTO> obtenerPedidosPorEstado(String estado) {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, s.nombre_servicio, u.nombres || ' ' || u.apellidos as nombre_usuario " +
                    "FROM pedido p " +
                    "JOIN servicio s ON p.id_servicio = s.id_servicio " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.estado = ? " +
                    "ORDER BY p.fecha_pedido DESC";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetAPedido(rs);
                    pedidos.add(pedido);
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener pedidos por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pedidos;
    }
    
    @Override
    public boolean actualizarPedido(PedidoDTO pedido) {
        String sql = "UPDATE pedido SET id_servicio = ?, fecha_recojo = ?, hora_recojo = ?, " +
                    "fecha_entrega = ?, hora_entrega = ?, metodo_entrega = ?, metodo_pago = ?, " +
                    "observaciones = ?, estado = ?, total = ?, cantidad = ? " +
                    "WHERE id_pedido = ?";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedido.getIdServicio());
            stmt.setDate(2, Date.valueOf(pedido.getFechaRecojo()));
            stmt.setTime(3, Time.valueOf(pedido.getHoraRecojo()));
            stmt.setDate(4, Date.valueOf(pedido.getFechaEntrega()));
            stmt.setTime(5, Time.valueOf(pedido.getHoraEntrega()));
            stmt.setString(6, pedido.getMetodoEntrega());
            stmt.setString(7, pedido.getMetodoPago());
            stmt.setString(8, pedido.getObservaciones());
            stmt.setString(9, pedido.getEstado());
            stmt.setBigDecimal(10, pedido.getTotal());
            stmt.setInt(11, pedido.getCantidad());
            stmt.setInt(12, pedido.getIdPedido());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al actualizar pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Método auxiliar para mapear ResultSet a PedidoDTO
     */
    private PedidoDTO mapearResultSetAPedido(ResultSet rs) throws SQLException {
        PedidoDTO pedido = new PedidoDTO();
        
        pedido.setIdPedido(rs.getInt("id_pedido"));
        pedido.setIdUsuario(rs.getInt("id_usuario"));
        pedido.setIdServicio(rs.getInt("id_servicio"));
        pedido.setFechaPedido(rs.getDate("fecha_pedido").toLocalDate());
        pedido.setFechaRecojo(rs.getDate("fecha_recojo").toLocalDate());
        pedido.setHoraRecojo(rs.getTime("hora_recojo").toLocalTime());
        pedido.setFechaEntrega(rs.getDate("fecha_entrega").toLocalDate());
        pedido.setHoraEntrega(rs.getTime("hora_entrega").toLocalTime());
        pedido.setMetodoEntrega(rs.getString("metodo_entrega"));
        pedido.setMetodoPago(rs.getString("metodo_pago"));
        pedido.setObservaciones(rs.getString("observaciones"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setTotal(rs.getBigDecimal("total"));
        pedido.setCantidad(rs.getInt("cantidad"));
        pedido.setNombreServicio(rs.getString("nombre_servicio"));
        pedido.setNombreUsuario(rs.getString("nombre_usuario"));
        
        return pedido;
    }

    @Override
    public List<PedidoDTO> obtenerPedidosPorUsuarioYEstados(int idUsuario, List<String> estados) {
        List<PedidoDTO> pedidos = new ArrayList<>();
        
        if (estados == null || estados.isEmpty()) {
            return pedidos;
        }
        
        // Crear placeholders para la consulta IN
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < estados.size(); i++) {
            if (i > 0) placeholders.append(", ");
            placeholders.append("?");
        }
        
        String sql = "SELECT p.*, s.nombre_servicio, u.nombre as nombre_usuario " +
                    "FROM pedido p " +
                    "JOIN servicio s ON p.id_servicio = s.id_servicio " +
                    "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                    "WHERE p.id_usuario = ? AND p.estado IN (" + placeholders + ") " +
                    "ORDER BY p.fecha_pedido DESC";
        
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            // Establecer los parámetros para los estados
            for (int i = 0; i < estados.size(); i++) {
                stmt.setString(i + 2, estados.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetAPedido(rs);
                    pedidos.add(pedido);
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener pedidos por usuario y estados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pedidos;
    }
}