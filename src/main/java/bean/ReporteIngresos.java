package bean;

import dao.impl.PedidoDAOImpl;
import dto.PedidoDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class ReporteIngresos implements Serializable {
    
    private PedidoDAOImpl pedidoDAO;
    private List<PedidoDTO> pedidos;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalGanancias;
    private Map<String, BigDecimal> gananciasPorMetodoPago;
    private Map<String, BigDecimal> gananciasPorServicio;
    
    @PostConstruct
    public void init() {
        pedidoDAO = new PedidoDAOImpl();
        pedidos = List.of();
        fechaFin = LocalDate.now();
        fechaInicio = fechaFin.minusMonths(1);
        totalGanancias = BigDecimal.ZERO;
        gananciasPorMetodoPago = new HashMap<>();
        gananciasPorServicio = new HashMap<>();
        cargarReporte();
    }
    
    public void cargarReporte() {
        pedidos = pedidoDAO.obtenerTodosPedidos();
        if (pedidos != null) {
            calcularGanancias();
        }
    }
    
    public void filtrarPorFecha() {
        if (fechaInicio != null && fechaFin != null) {
            pedidos = pedidoDAO.obtenerTodosPedidos();
            pedidos.removeIf(p -> p.getFechaPedido().isBefore(fechaInicio) || 
                                p.getFechaPedido().isAfter(fechaFin));
            calcularGanancias();
        }
    }
    
    private void calcularGanancias() {
        totalGanancias = BigDecimal.ZERO;
        gananciasPorMetodoPago.clear();
        gananciasPorServicio.clear();
        
        for (PedidoDTO pedido : pedidos) {
            if (pedido.getTotal() != null && "Completado".equalsIgnoreCase(pedido.getEstado())) {
                // Sumar al total
                totalGanancias = totalGanancias.add(pedido.getTotal());
                
                // Sumar por método de pago
                String metodoPago = pedido.getMetodoPago();
                gananciasPorMetodoPago.merge(metodoPago, pedido.getTotal(), BigDecimal::add);
                
                // Sumar por servicio
                String servicio = pedido.getNombreServicio();
                gananciasPorServicio.merge(servicio, pedido.getTotal(), BigDecimal::add);
            }
        }
    }
    
    // Getters y Setters
    public List<PedidoDTO> getPedidos() {
        return pedidos;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public BigDecimal getTotalGanancias() {
        return totalGanancias;
    }
    
    public Map<String, BigDecimal> getGananciasPorMetodoPago() {
        return gananciasPorMetodoPago;
    }
    
    public Map<String, BigDecimal> getGananciasPorServicio() {
        return gananciasPorServicio;
    }
}