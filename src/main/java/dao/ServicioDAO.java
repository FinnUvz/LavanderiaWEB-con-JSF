package dao;

import dto.ServicioDTO;
import java.util.List;

public interface ServicioDAO {
    
    /**
     * Obtiene todos los servicios activos para mostrar en el ComboBox
     * @return Lista de servicios activos
     */
    List<ServicioDTO> obtenerServiciosActivos();
    
    /**
     * Obtiene un servicio por su ID
     * @param idServicio ID del servicio
     * @return ServicioDTO o null si no existe
     */
    ServicioDTO obtenerServicioPorId(int idServicio);
    
    /**
     * Obtiene todos los servicios (activos e inactivos)
     * @return Lista de todos los servicios
     */
    List<ServicioDTO> obtenerTodosServicios();
    
    /**
     * Crea un nuevo servicio
     * @param servicio Datos del servicio
     * @return true si se creó correctamente
     */
    boolean crearServicio(ServicioDTO servicio);
    
    /**
     * Actualiza un servicio existente
     * @param servicio Datos del servicio
     * @return true si se actualizó correctamente
     */
    boolean actualizarServicio(ServicioDTO servicio);
    
    /**
     * Desactiva un servicio (no lo elimina físicamente)
     * @param idServicio ID del servicio
     * @return true si se desactivó correctamente
     */
    boolean desactivarServicio(int idServicio);
}
