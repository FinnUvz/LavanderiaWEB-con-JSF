package bean;

import dao.PedidoDAO;
import dao.ServicioDAO;
import dao.impl.PedidoDAOImpl;
import dao.impl.ServicioDAOImpl;
import dto.PedidoDTO;
import dto.ServicioDTO;
import dto.UsuarioDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static util.NavigationController.CLIENTE_INICIO;
import static util.NavigationController.LOGIN_PAGE;

@Named
@ViewScoped
public class NuevoPedidoBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // DAOs
    private PedidoDAO pedidoDAO;
    private ServicioDAO servicioDAO;
    
    // Datos del formulario
    private PedidoDTO pedido;
    private List<ServicioDTO> serviciosDisponibles;
    private ServicioDTO servicioSeleccionado;
    private int cantidad;
    private String observaciones;
    private String metodoEntrega;
    private String metodoPago;
    private LocalDate fechaRecojo;
    private LocalTime horaRecojo;
    private LocalDate fechaEntrega;
    private LocalTime horaEntrega;
    private BigDecimal total;
    private boolean mostrarTotal;
    
    // Opciones para los selectores
    private final String[] metodosEntrega = {"Recojo a domicilio", "Llevar a la tienda"};
    private final String[] metodosPago = {"Efectivo", "Tarjeta", "Transferencia", "Yape", "Plin"};
    
    public NuevoPedidoBean() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.servicioDAO = new ServicioDAOImpl();
        this.pedido = new PedidoDTO();
        this.cantidad = 1;
        this.total = BigDecimal.ZERO;
        this.mostrarTotal = false;
        
        // Cargar servicios disponibles
        cargarServicios();
        
        // Establecer fechas por defecto
        this.fechaRecojo = LocalDate.now().plusDays(1);
        this.fechaEntrega = LocalDate.now().plusDays(3);
        this.horaRecojo = LocalTime.of(9, 0);
        this.horaEntrega = LocalTime.of(17, 0);
    }
    
    /**
     * Carga los servicios activos desde la base de datos
     */
    public void cargarServicios() {
        try {
            this.serviciosDisponibles = servicioDAO.obtenerServiciosActivos();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "No se pudieron cargar los servicios disponibles"));
        }
    }
    
    /**
     * Se ejecuta cuando se selecciona un servicio del ComboBox
     */
    public void onServicioSeleccionado() {
        if (servicioSeleccionado != null) {
            calcularTotal();
        } else {
            total = BigDecimal.ZERO;
            mostrarTotal = false;
        }
    }
    
    /**
     * Se ejecuta cuando cambia la cantidad
     */
    public void onCantidadCambiada() {
        if (servicioSeleccionado != null && cantidad > 0) {
            calcularTotal();
        }
    }
    
    /**
     * Calcula el total del pedido
     */
    public void calcularTotal() {
        if (servicioSeleccionado != null && cantidad > 0) {
            total = servicioSeleccionado.getPrecio().multiply(BigDecimal.valueOf(cantidad));
            mostrarTotal = true;
        } else {
            total = BigDecimal.ZERO;
            mostrarTotal = false;
        }
    }
    
    /**
     * Valida los datos del formulario
     */
    public boolean validarDatos() {
        FacesContext context = FacesContext.getCurrentInstance();
        boolean valido = true;
        
        // Validar servicio seleccionado
        if (servicioSeleccionado == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un servicio"));
            valido = false;
        }
        
        // Validar cantidad
        if (cantidad <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "La cantidad debe ser mayor a 0"));
            valido = false;
        }
        
        // Validar método de entrega
        if (metodoEntrega == null || metodoEntrega.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un método de entrega"));
            valido = false;
        }
        
        // Validar método de pago
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar un método de pago"));
            valido = false;
        }
        
        // Validar fechas
        if (fechaRecojo == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una fecha de recojo"));
            valido = false;
        } else if (fechaRecojo.isBefore(LocalDate.now())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "La fecha de recojo no puede ser anterior a hoy"));
            valido = false;
        }
        
        if (fechaEntrega == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una fecha de entrega"));
            valido = false;
        } else if (fechaEntrega.isBefore(fechaRecojo)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "La fecha de entrega no puede ser anterior a la fecha de recojo"));
            valido = false;
        }
        
        // Validar horas
        if (horaRecojo == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una hora de recojo"));
            valido = false;
        }
        
        if (horaEntrega == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Debe seleccionar una hora de entrega"));
            valido = false;
        }
        
        return valido;
    }
    
    /**
     * Registra el pedido en la base de datos
     */
    public String registrarPedido() {
        try {
            if (!validarDatos()) {
                return null;
            }
            
            // Obtener usuario de la sesión
            UsuarioDTO usuario = (UsuarioDTO) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");
            
            if (usuario == null) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "Sesión expirada. Por favor, inicie sesión nuevamente"));
                return "login?faces-redirect=true";
            }
            
            // Configurar el pedido
            pedido.setIdUsuario(usuario.getIdUsuario());
            pedido.setIdServicio(servicioSeleccionado.getIdServicio());
            pedido.setCantidad(cantidad);
            pedido.setObservaciones(observaciones);
            pedido.setMetodoEntrega(metodoEntrega);
            pedido.setMetodoPago(metodoPago);
            pedido.setFechaRecojo(fechaRecojo);
            pedido.setHoraRecojo(horaRecojo);
            pedido.setFechaEntrega(fechaEntrega);
            pedido.setHoraEntrega(horaEntrega);
            pedido.setTotal(total);
            pedido.setEstado("Pendiente");
            pedido.setFechaPedido(LocalDate.now());
            
            // Guardar en base de datos
            boolean exito = pedidoDAO.crearPedido(pedido);
            
            if (exito) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Éxito", "Pedido registrado correctamente"));
                
                // Limpiar formulario
                limpiarFormulario();
                
                // Redirigir a la página de confirmación o dashboard
                return "inicio?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "No se pudo registrar el pedido. Intente nuevamente"));
            }
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Error", "Error inesperado: " + e.getMessage()));
        }
        
        return null;
    }
    
    /**
     * Limpia el formulario después de registrar el pedido
     */
    public void limpiarFormulario() {
        this.pedido = new PedidoDTO();
        this.servicioSeleccionado = null;
        this.cantidad = 1;
        this.observaciones = "";
        this.metodoEntrega = null;
        this.metodoPago = null;
        this.total = BigDecimal.ZERO;
        this.mostrarTotal = false;
        this.fechaRecojo = LocalDate.now().plusDays(1);
        this.fechaEntrega = LocalDate.now().plusDays(3);
        this.horaRecojo = LocalTime.of(9, 0);
        this.horaEntrega = LocalTime.of(17, 0);
    }
    
    public String irInicio() {
        return CLIENTE_INICIO+"?faces-redirect=true";
    }
    public String cancelar() {
        return CLIENTE_INICIO+"?faces-redirect=true";
    }
    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return LOGIN_PAGE+"?faces-redirect=true";
    }
    
    // Getters y Setters
    public PedidoDTO getPedido() {
        return pedido;
    }
    
    public void setPedido(PedidoDTO pedido) {
        this.pedido = pedido;
    }
    
    public List<ServicioDTO> getServiciosDisponibles() {
        return serviciosDisponibles;
    }
    
    public void setServiciosDisponibles(List<ServicioDTO> serviciosDisponibles) {
        this.serviciosDisponibles = serviciosDisponibles;
    }
    
    public ServicioDTO getServicioSeleccionado() {
        return servicioSeleccionado;
    }
    
    public void setServicioSeleccionado(ServicioDTO servicioSeleccionado) {
        this.servicioSeleccionado = servicioSeleccionado;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getMetodoEntrega() {
        return metodoEntrega;
    }
    
    public void setMetodoEntrega(String metodoEntrega) {
        this.metodoEntrega = metodoEntrega;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public LocalDate getFechaRecojo() {
        return fechaRecojo;
    }
    
    public void setFechaRecojo(LocalDate fechaRecojo) {
        this.fechaRecojo = fechaRecojo;
    }
    
    public LocalTime getHoraRecojo() {
        return horaRecojo;
    }
    
    public void setHoraRecojo(LocalTime horaRecojo) {
        this.horaRecojo = horaRecojo;
    }
    
    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }
    
    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public LocalTime getHoraEntrega() {
        return horaEntrega;
    }
    
    public void setHoraEntrega(LocalTime horaEntrega) {
        this.horaEntrega = horaEntrega;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public boolean isMostrarTotal() {
        return mostrarTotal;
    }
    
    public void setMostrarTotal(boolean mostrarTotal) {
        this.mostrarTotal = mostrarTotal;
    }
    
    public String[] getMetodosEntrega() {
        return metodosEntrega;
    }
    
    public String[] getMetodosPago() {
        return metodosPago;
    }
}