package bean;

import dao.PedidoDAO;
import dao.impl.PedidoDAOImpl;
import dto.PedidoDTO;
import dto.UsuarioDTO;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class MisPedidosBean implements Serializable {
    
    private List<PedidoDTO> pedidosActivos;
    private List<PedidoDTO> pedidosCancelados;
    private UsuarioDTO usuarioLogueado;
    
    @PostConstruct
    public void init() {
        // Verificar que el usuario esté autenticado
        usuarioLogueado = (UsuarioDTO) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");
        
        if (usuarioLogueado == null) {
            try {
                // Redireccionar a login si no hay usuario en sesión
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.xhtml");
            } catch (IOException e) {
                System.err.println("Error al redireccionar al login: " + e.getMessage());
            }
            return;
        }
        
        cargarPedidos();
    }
    
    /**
     * Carga los pedidos del usuario logueado separados por estado
     */
    private void cargarPedidos() {
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        
        // Cargar pedidos activos (Pendiente, En proceso, Listo para entrega, Entregado)
        pedidosActivos = pedidoDAO.obtenerPedidosPorUsuarioYEstados(
            usuarioLogueado.getIdUsuario(), 
            Arrays.asList("Pendiente", "En proceso", "Listo para entrega", "Entregado")
        );
        
        // Cargar pedidos cancelados (solo Cancelado)
        pedidosCancelados = pedidoDAO.obtenerPedidosPorUsuarioYEstados(
            usuarioLogueado.getIdUsuario(), 
            Arrays.asList("Cancelado")
        );
    }
    
    /**
     * Obtiene la clase CSS para el badge según el estado del pedido
     * @param estado Estado del pedido
     * @return Clase CSS correspondiente
     */
    public String obtenerClaseBadge(String estado) {
        if (estado == null) return "badge bg-secondary";
        
        switch (estado.toLowerCase()) {
            case "pendiente":
                return "badge bg-secondary";
            case "en proceso":
                return "badge bg-primary";
            case "listo para entrega":
                return "badge bg-warning";
            case "entregado":
                return "badge bg-success";
            case "cancelado":
                return "badge bg-danger";
            default:
                return "badge bg-secondary";
        }
    }
    
    /**
     * Obtiene el texto del badge según el estado
     * @param estado Estado del pedido
     * @return Texto a mostrar en el badge
     */
    public String obtenerTextoBadge(String estado) {
        if (estado == null) return "Sin estado";
        
        switch (estado.toLowerCase()) {
            case "pendiente":
                return "Pendiente";
            case "en proceso":
                return "En Proceso";
            case "listo para entrega":
                return "Listo para Entrega";
            case "entregado":
                return "Entregado";
            case "cancelado":
                return "Cancelado";
            default:
                return estado;
        }
    }
    
    /**
     * Navega de vuelta al dashboard
     * @return Página de destino
     */
    public String volverAlDashboard() {
        return "dashboard.xhtml?faces-redirect=true";
    }
    
    /**
     * Cierra la sesión del usuario
     * @return Página de login
     */
    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }
    
    /**
     * Refresca la lista de pedidos
     */
    public void refrescarPedidos() {
        cargarPedidos();
    }
    
    // Getters y Setters
    public List<PedidoDTO> getPedidosActivos() {
        return pedidosActivos;
    }
    
    public void setPedidosActivos(List<PedidoDTO> pedidosActivos) {
        this.pedidosActivos = pedidosActivos;
    }
    
    public List<PedidoDTO> getPedidosCancelados() {
        return pedidosCancelados;
    }
    
    public void setPedidosCancelados(List<PedidoDTO> pedidosCancelados) {
        this.pedidosCancelados = pedidosCancelados;
    }
    
    public UsuarioDTO getUsuarioLogueado() {
        return usuarioLogueado;
    }
    
    public void setUsuarioLogueado(UsuarioDTO usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
}
