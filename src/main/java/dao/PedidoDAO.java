package dao;

import dto.PedidoDTO;
import java.util.List;

public interface PedidoDAO {
    
    /**
     * Crea un nuevo pedido
     * @param pedido Datos del pedido
     * @return true si se creó correctamente
     */
    boolean crearPedido(PedidoDTO pedido);
    
    /**
     * Obtiene todos los pedidos de un usuario
     * @param idUsuario ID del usuario
     * @return Lista de pedidos del usuario
     */
    List<PedidoDTO> obtenerPedidosPorUsuario(int idUsuario);
    
    /**
     * Obtiene un pedido por su ID
     * @param idPedido ID del pedido
     * @return PedidoDTO o null si no existe
     */
    PedidoDTO obtenerPedidoPorId(int idPedido);
    
    /**
     * Obtiene todos los pedidos del sistema
     * @return Lista de todos los pedidos
     */
    List<PedidoDTO> obtenerTodosPedidos();
    
    /**
     * Actualiza el estado de un pedido
     * @param idPedido ID del pedido
     * @param nuevoEstado Nuevo estado del pedido
     * @return true si se actualizó correctamente
     */
    boolean actualizarEstadoPedido(int idPedido, String nuevoEstado);
    
    /**
     * Obtiene pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos con ese estado
     */
    List<PedidoDTO> obtenerPedidosPorEstado(String estado);
    
    /**
     * Actualiza un pedido completo
     * @param pedido Datos del pedido
     * @return true si se actualizó correctamente
     */
    boolean actualizarPedido(PedidoDTO pedido);
}