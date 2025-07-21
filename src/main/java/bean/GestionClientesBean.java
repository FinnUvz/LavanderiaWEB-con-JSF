package bean;

import dao.impl.UsuarioDAOImpl;
import dto.UsuarioDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class GestionClientesBean implements Serializable {

    private List<UsuarioDTO> listaClientes;
    private UsuarioDTO clienteSeleccionado;
    private List<UsuarioDTO> clientesSeleccionados;
    
    private final UsuarioDAOImpl usuarioDAO;

    public GestionClientesBean() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    @PostConstruct
    public void init() {
        cargarClientes();
        this.clienteSeleccionado = new UsuarioDTO();
    }

    public void cargarClientes() {
        this.listaClientes = usuarioDAO.obtenerUsuariosPorRol("cliente");
    }

    public void prepararNuevoCliente() {
        this.clienteSeleccionado = new UsuarioDTO();
        this.clienteSeleccionado.setRol("cliente");
    }

    public void prepararEdicionCliente(UsuarioDTO cliente) {
        this.clienteSeleccionado = usuarioDAO.buscarPorId(cliente.getIdUsuario());
    }

    public void prepararEliminacionCliente(UsuarioDTO cliente) {
        this.clienteSeleccionado = cliente;
    }

    public void guardarCliente() {
        try {
            if (this.clienteSeleccionado.getIdUsuario() == 0) {
                // Validar correo único
                if (usuarioDAO.existeCorreo(clienteSeleccionado.getCorreo())) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El correo ya está registrado"));
                    return;
                }
                
                if (usuarioDAO.registrarUsuario(clienteSeleccionado)) {
                    cargarClientes();
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage("Cliente creado exitosamente"));
                    PrimeFaces.current().executeScript("PF('dlgCliente').hide()");
                }
            } else {
                if (usuarioDAO.actualizarUsuario(clienteSeleccionado)) {
                    cargarClientes();
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage("Cliente actualizado exitosamente"));
                    PrimeFaces.current().executeScript("PF('dlgCliente').hide()");
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al guardar el cliente"));
        } finally {
            PrimeFaces.current().ajax().update("form:messages", "form:dtClientes");
        }
    }

    public void eliminarCliente() {
        try {
            if (usuarioDAO.eliminarUsuario(this.clienteSeleccionado.getIdUsuario())) {
                cargarClientes();
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("Cliente eliminado exitosamente"));
                PrimeFaces.current().executeScript("PF('dlgConfirmar').hide()");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al eliminar el cliente"));
        } finally {
            PrimeFaces.current().ajax().update("form:messages", "form:dtClientes");
        }
    }

    public void eliminarClientesSeleccionados() {
        try {
            boolean todosEliminados = true;
            for (UsuarioDTO cliente : clientesSeleccionados) {
                if (!usuarioDAO.eliminarUsuario(cliente.getIdUsuario())) {
                    todosEliminados = false;
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
                            "No se pudo eliminar el cliente: " + cliente.getNombre()));
                }
            }
            
            if (todosEliminados) {
                cargarClientes();
                clientesSeleccionados = null;
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("Clientes seleccionados eliminados exitosamente"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al eliminar los clientes"));
        } finally {
            PrimeFaces.current().ajax().update("form:messages", "form:dtClientes");
        }
    }

    // Getters y Setters
    public List<UsuarioDTO> getListaClientes() {
        return listaClientes;
    }

    public UsuarioDTO getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(UsuarioDTO clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    public List<UsuarioDTO> getClientesSeleccionados() {
        return clientesSeleccionados;
    }

    public void setClientesSeleccionados(List<UsuarioDTO> clientesSeleccionados) {
        this.clientesSeleccionados = clientesSeleccionados;
    }
}