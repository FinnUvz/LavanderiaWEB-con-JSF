package dto;

import java.math.BigDecimal;

public class ServicioDTO {
    private int idServicio;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal precio;
    private boolean activo;
    
    // Constructor vacío
    public ServicioDTO() {}
    
    // Constructor con parámetros
    public ServicioDTO(int idServicio, String nombreServicio, String descripcion, BigDecimal precio, boolean activo) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
        this.precio = precio;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getIdServicio() {
        return idServicio;
    }
    
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }
    
    public String getNombreServicio() {
        return nombreServicio;
    }
    
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "ServicioDTO{" +
                "idServicio=" + idServicio +
                ", nombreServicio='" + nombreServicio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", activo=" + activo +
                '}';
    }
}