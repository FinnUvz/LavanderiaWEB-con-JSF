package bean;

import dao.PedidoDAO;
import dao.UsuarioDAO;
import dao.ServicioDAO;
import dao.impl.PedidoDAOImpl;
import dao.impl.UsuarioDAOImpl;
import dao.impl.ServicioDAOImpl;
import dto.PedidoDTO;
import dto.UsuarioDTO;
import dto.ServicioDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class GestionPedidosAdminBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // DAOs
    private PedidoDAO pedidoDAO;
    private UsuarioDAO usuarioDAO;
    private ServicioDAO servicioDAO;
    
    // Listas principales
    private List<PedidoDTO> pedidos;
    private List<PedidoDTO> pedidosFiltrados;
    private List<UsuarioDTO> usuarios;
    private List<ServicioDTO> servicios;
    
    // Filtros
    private String filtroCliente = "";
    private String filtroEstado = "";
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    
    // Estados disponibles
    private List<String> estadosDisponibles = Arrays.asList(
        "Pendiente", "En proceso", "Listo para entrega", "Entregado", "Cancelado"
    );
    
    private PedidoDTO pedidoSeleccionado;
    private PedidoDTO pedidoEditar;
    private PedidoDTO nuevoPedido;
    private UsuarioDTO clienteSeleccionado;
    
private String estadoSeleccionado;


public void abrirModalCambiarEstado(PedidoDTO pedido) {
    this.pedidoSeleccionado = pedido;
    this.estadoSeleccionado = pedido.getEstado();
}

public void cambiarEstadoSeleccionado() {
    if (pedidoSeleccionado != null && estadoSeleccionado != null && !estadoSeleccionado.isEmpty()) {

        if (!validarCambioEstado(pedidoSeleccionado.getEstado(), estadoSeleccionado)) {
            return;
        }
        if (pedidoDAO.actualizarEstadoPedido(pedidoSeleccionado.getIdPedido(), estadoSeleccionado)) {
            pedidoSeleccionado.setEstado(estadoSeleccionado);
            cargarDatos();
            aplicarFiltros();

            mostrarMensaje(FacesMessage.SEVERITY_INFO, 
                "Éxito", "Estado actualizado correctamente a: " + estadoSeleccionado);

            pedidoSeleccionado = null;
            estadoSeleccionado = null;
        } else {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "No se pudo actualizar el estado del pedido.");
        }
    } else {
        mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
            "Error", "Debe seleccionar un estado válido.");
    }
}

public List<String> getEstadosDisponiblesParaSeleccionado() {
    if (pedidoSeleccionado == null) {
        return Collections.emptyList();
    }
    
    String estadoActual = pedidoSeleccionado.getEstado();
    List<String> opciones = new ArrayList<>();

    switch (estadoActual) {
        case "Pendiente":
            opciones.add("En proceso");
            break;
        case "En proceso":
            opciones.add("Listo para entrega");
            break;
        case "Listo para entrega":
            opciones.add("Entregado");
            break;
        case "Entregado":
            opciones.add("Cancelado");
            break;
        default:
            break;
    }
    
    return opciones;
}


    
    // Variables para nuevo pedido
    private int idClienteSeleccionado;
    private int idServicioSeleccionado;
    
    @PostConstruct
    public void init() {
        pedidoDAO = new PedidoDAOImpl();
        usuarioDAO = new UsuarioDAOImpl();
        servicioDAO = new ServicioDAOImpl();
        
        cargarDatos();
    }
    
    private void cargarDatos() {
        pedidos = pedidoDAO.obtenerTodosPedidos();
        pedidosFiltrados = new ArrayList<>(pedidos);
        usuarios = usuarioDAO.obtenerTodosUsuarios();
        servicios = servicioDAO.obtenerTodosServicios();
    }
    
    /**
     * Aplicar filtros a la lista de pedidos
     */
    public void aplicarFiltros() {
        pedidosFiltrados = pedidos.stream()
            .filter(pedido -> {
                // Filtro por cliente (nombre o correo)
                if (!filtroCliente.isEmpty()) {
                    String clienteNombre = pedido.getNombreUsuario().toLowerCase();
                    String clienteCorreo = obtenerCorreoCliente(pedido.getIdUsuario()).toLowerCase();
                    String filtro = filtroCliente.toLowerCase();
                    
                    if (!clienteNombre.contains(filtro) && !clienteCorreo.contains(filtro)) {
                        return false;
                    }
                }
                
                // Filtro por estado
                if (!filtroEstado.isEmpty() && !pedido.getEstado().equals(filtroEstado)) {
                    return false;
                }
                
                // Filtro por fecha desde
                if (fechaDesde != null && pedido.getFechaPedido().isBefore(fechaDesde)) {
                    return false;
                }
                
                // Filtro por fecha hasta
                if (fechaHasta != null && pedido.getFechaPedido().isAfter(fechaHasta)) {
                    return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Limpiar todos los filtros
     */
    public void limpiarFiltros() {
        filtroCliente = "";
        filtroEstado = "";
        fechaDesde = null;
        fechaHasta = null;
        pedidosFiltrados = new ArrayList<>(pedidos);
    }
    
    /**
     * Mostrar detalles del pedido en modal
     */
    public void verDetalles(PedidoDTO pedido) {
        pedidoSeleccionado = pedido;
        clienteSeleccionado = usuarioDAO.buscarPorId(pedido.getIdUsuario());
       
    }
    
    /**
     * Abrir modal para editar pedido
     */
    public void abrirModalEditar(PedidoDTO pedido) {
        if (!puedeEditarPedido(pedido)) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, 
                "Advertencia", "No se puede editar un pedido entregado o cancelado.");
            return;
        }
        
        pedidoEditar = clonarPedido(pedido);
        idServicioSeleccionado = pedidoEditar.getIdServicio();
        
    }
    
    /**
     * Guardar cambios del pedido editado
     */
    public void guardarEdicion() {
        if (!validarPedido(pedidoEditar)) {
            return;
        }
        
        // Actualizar el servicio seleccionado
        pedidoEditar.setIdServicio(idServicioSeleccionado);
        
        // Recalcular total
        calcularTotal(pedidoEditar);
        
        if (pedidoDAO.actualizarPedido(pedidoEditar)) {
            mostrarMensaje(FacesMessage.SEVERITY_INFO, 
                "Éxito", "Pedido actualizado correctamente.");
            cargarDatos();
            aplicarFiltros();
            
        } else {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "No se pudo actualizar el pedido.");
        }
    }
    

    
    /**
     * Confirmar eliminación de pedido
     */
    public void confirmarEliminar(PedidoDTO pedido) {
        pedidoSeleccionado = pedido;
        
    }
    
    /**
     * Eliminar pedido
     */
    public void eliminarPedido() {
        if (pedidoDAO.eliminarPedido(pedidoSeleccionado.getIdPedido())) {
            mostrarMensaje(FacesMessage.SEVERITY_INFO,
                    "Éxito", "Pedido eliminado correctamente.");
            cargarDatos();
            aplicarFiltros();
        } else {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo eliminar el pedido.");
        }
        
    }
    
    /**
     * Abrir modal para crear nuevo pedido
     */
    public void abrirModalNuevo() {
        nuevoPedido = new PedidoDTO();
        idClienteSeleccionado = 0;
        idServicioSeleccionado = 0;
        
    }
    
    /**
     * Crear nuevo pedido
     */
    public void crearPedido() {
        if (idClienteSeleccionado == 0) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un cliente.");
            return;
        }
        
        if (idServicioSeleccionado == 0) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un servicio.");
            return;
        }
        
        if (!validarPedido(nuevoPedido)) {
            return;
        }
        
        nuevoPedido.setIdUsuario(idClienteSeleccionado);
        nuevoPedido.setIdServicio(idServicioSeleccionado);
        calcularTotal(nuevoPedido);
        
        if (pedidoDAO.crearPedido(nuevoPedido)) {
            mostrarMensaje(FacesMessage.SEVERITY_INFO, 
                "Éxito", "Pedido creado correctamente.");
            cargarDatos();
            aplicarFiltros();
            
        } else {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "No se pudo crear el pedido.");
        }
    }
    
    /**
     * Validar datos del pedido
     */
    private boolean validarPedido(PedidoDTO pedido) {
        if (pedido.getCantidad() <= 0) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "La cantidad debe ser mayor a 0.");
            return false;
        }
        
        if (pedido.getFechaRecojo() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una fecha de recojo.");
            return false;
        }
        
        if (pedido.getFechaEntrega() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una fecha de entrega.");
            return false;
        }
        
        if (pedido.getFechaRecojo().isAfter(pedido.getFechaEntrega())) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "La fecha de recojo no puede ser posterior a la fecha de entrega.");
            return false;
        }
        
        if (pedido.getMetodoEntrega() == null || pedido.getMetodoEntrega().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un método de entrega.");
            return false;
        }
        
        if (pedido.getMetodoPago() == null || pedido.getMetodoPago().isEmpty()) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un método de pago.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Validar cambio de estado
     */
    private boolean validarCambioEstado(String estadoActual, String nuevoEstado) {
        // Un pedido no puede pasar a "Entregado" sin haber pasado antes por "Listo para entrega"
        if (nuevoEstado.equals("Entregado") && !estadoActual.equals("Listo para entrega")) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, 
                "Advertencia", "El pedido debe estar 'Listo para entrega' antes de marcarlo como 'Entregado'.");
            return false;
        }
        
       
        
        return true;
    }
    

    private boolean puedeEditarPedido(PedidoDTO pedido) {
        return !pedido.getEstado().equals("Entregado") && !pedido.getEstado().equals("Cancelado");
    }
    
    
    private void calcularTotal(PedidoDTO pedido) {
        ServicioDTO servicio = servicios.stream()
            .filter(s -> s.getIdServicio() == pedido.getIdServicio())
            .findFirst()
            .orElse(null);
        
        if (servicio != null) {
            BigDecimal total = servicio.getPrecio().multiply(new BigDecimal(pedido.getCantidad()));
            pedido.setTotal(total);
        }
    }
    
    /**
     * Clonar pedido para edición
     */
    private PedidoDTO clonarPedido(PedidoDTO original) {
        PedidoDTO clon = new PedidoDTO();
        clon.setIdPedido(original.getIdPedido());
        clon.setIdUsuario(original.getIdUsuario());
        clon.setIdServicio(original.getIdServicio());
        clon.setFechaPedido(original.getFechaPedido());
        clon.setFechaRecojo(original.getFechaRecojo());
        clon.setHoraRecojo(original.getHoraRecojo());
        clon.setFechaEntrega(original.getFechaEntrega());
        clon.setHoraEntrega(original.getHoraEntrega());
        clon.setMetodoEntrega(original.getMetodoEntrega());
        clon.setMetodoPago(original.getMetodoPago());
        clon.setObservaciones(original.getObservaciones());
        clon.setEstado(original.getEstado());
        clon.setTotal(original.getTotal());
        clon.setCantidad(original.getCantidad());
        clon.setNombreServicio(original.getNombreServicio());
        clon.setNombreUsuario(original.getNombreUsuario());
        
        return clon;
    }
    
    /**
     * Obtener correo del cliente
     */
    private String obtenerCorreoCliente(int idUsuario) {
        UsuarioDTO usuario = usuarioDAO.buscarPorId(idUsuario);
        return usuario != null ? usuario.getCorreo() : "";
    }
    

    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    

   
    
    // Getters y Setters
    public List<PedidoDTO> getPedidos() {
        return pedidos;
    }
    
    public List<PedidoDTO> getPedidosFiltrados() {
        return pedidosFiltrados;
    }
    
    public List<UsuarioDTO> getUsuarios() {
        return usuarios;
    }
    
    public List<ServicioDTO> getServicios() {
        return servicios;
    }
    
    public String getFiltroCliente() {
        return filtroCliente;
    }
    
    public void setFiltroCliente(String filtroCliente) {
        this.filtroCliente = filtroCliente;
    }
    
    public String getFiltroEstado() {
        return filtroEstado;
    }
    
    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }
    
    public LocalDate getFechaDesde() {
        return fechaDesde;
    }
    
    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }
    
    public LocalDate getFechaHasta() {
        return fechaHasta;
    }
    
    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
    
    public List<String> getEstadosDisponibles() {
        return estadosDisponibles;
    }
    
    public PedidoDTO getPedidoSeleccionado() {
        return pedidoSeleccionado;
    }
    
    public PedidoDTO getPedidoEditar() {
        return pedidoEditar;
    }
    
    public PedidoDTO getNuevoPedido() {
        return nuevoPedido;
    }
    
    public UsuarioDTO getClienteSeleccionado() {
        return clienteSeleccionado;
    }
    
    
    
    public int getIdClienteSeleccionado() {
        return idClienteSeleccionado;
    }
    
    public void setIdClienteSeleccionado(int idClienteSeleccionado) {
        this.idClienteSeleccionado = idClienteSeleccionado;
    }
    
    public int getIdServicioSeleccionado() {
        return idServicioSeleccionado;
    }
    
    public void setIdServicioSeleccionado(int idServicioSeleccionado) {
        this.idServicioSeleccionado = idServicioSeleccionado;
    }

    public String getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    public void setEstadoSeleccionado(String estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }
    
}