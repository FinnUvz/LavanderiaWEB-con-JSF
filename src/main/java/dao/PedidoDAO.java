package dao;

import dto.PedidoDTO;
import java.util.List;

public interface PedidoDAO {
    
    boolean crearPedido(PedidoDTO pedido);
    
    List<PedidoDTO> obtenerPedidosPorUsuario(int idUsuario);
    
    List<PedidoDTO> obtenerPedidosPorUsuarioYEstados(int idUsuario, List<String> estados);
    
    PedidoDTO obtenerPedidoPorId(int idPedido);
    
    List<PedidoDTO> obtenerTodosPedidos();
    
    boolean actualizarEstadoPedido(int idPedido, String nuevoEstado);
    
    List<PedidoDTO> obtenerPedidosPorEstado(String estado);
    
    boolean actualizarPedido(PedidoDTO pedido);
}