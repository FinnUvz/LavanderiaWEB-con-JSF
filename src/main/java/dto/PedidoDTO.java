package dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class PedidoDTO {
    private int idPedido;
    private int idUsuario;
    private int idServicio;
    private LocalDate fechaPedido;
    private LocalDate fechaRecojo;
    private LocalTime horaRecojo;
    private LocalDate fechaEntrega;
    private LocalTime horaEntrega;
    private String metodoEntrega;
    private String metodoPago;
    private String observaciones;
    private String estado;
    private BigDecimal total;
    private int cantidad;
    
    // Para mostrar información adicional en la vista
    private String nombreServicio;
    private String nombreUsuario;
    
    // Constructor vacío
    public PedidoDTO() {
        this.estado = "Pendiente";
        this.fechaPedido = LocalDate.now();
    }
    
    // Constructor con parámetros principales
    public PedidoDTO(int idUsuario, int idServicio, LocalDate fechaRecojo, LocalTime horaRecojo,
                     LocalDate fechaEntrega, LocalTime horaEntrega, String metodoEntrega,
                     String metodoPago, String observaciones, BigDecimal total, int cantidad) {
        this();
        this.idUsuario = idUsuario;
        this.idServicio = idServicio;
        this.fechaRecojo = fechaRecojo;
        this.horaRecojo = horaRecojo;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.metodoEntrega = metodoEntrega;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.total = total;
        this.cantidad = cantidad;
    }
    
    // Getters y Setters
    public int getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public int getIdServicio() {
        return idServicio;
    }
    
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }
    
    public LocalDate getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
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
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getNombreServicio() {
        return nombreServicio;
    }
    
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    @Override
    public String toString() {
        return "PedidoDTO{" +
                "idPedido=" + idPedido +
                ", idUsuario=" + idUsuario +
                ", idServicio=" + idServicio +
                ", fechaPedido=" + fechaPedido +
                ", fechaRecojo=" + fechaRecojo +
                ", horaRecojo=" + horaRecojo +
                ", fechaEntrega=" + fechaEntrega +
                ", horaEntrega=" + horaEntrega +
                ", metodoEntrega='" + metodoEntrega + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", cantidad=" + cantidad +
                '}';
    }
}