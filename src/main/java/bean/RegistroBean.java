package bean;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import dto.UsuarioDTO;
import enums.RolUsuario;
import util.NavigationController;


import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Managed Bean para el manejo del registro de usuarios
 */
@Named
@ViewScoped
public class RegistroBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Campos del formulario
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private String contrasena;
    private String confirmarContrasena;
    
    // DAO para acceso a datos
    private UsuarioDAO usuarioDAO;
    
    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{9,15}$");
    
    // Constructor
    public RegistroBean() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }
    
    /**
     * Método para registrar un nuevo usuario
     * @return String para navegación
     */
    public String registrar() {
        try {
            // Validar campos
            if (!validarCampos()) {
                return null;
            }
            
            // Validar que el correo no exista
            if (usuarioDAO.existeCorreo(correo.trim())) {
                mostrarMensajeError("El correo electrónico ya está registrado");
                return null;
            }
            
            // Validar confirmación de contraseña
            if (!contrasena.equals(confirmarContrasena)) {
                mostrarMensajeError("Las contraseñas no coinciden");
                return null;
            }
            
            // Crear nuevo usuario
            UsuarioDTO nuevoUsuario = new UsuarioDTO(
                nombre.trim(),
                correo.trim().toLowerCase(),
                telefono.trim(),
                direccion.trim(),
                contrasena,
                RolUsuario.CLIENTE.getValor()
            );
            
            // Registrar usuario
            if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
                NavigationController.mostrarMensajeExito("¡Registro exitoso! Ahora puedes iniciar sesión.");
                limpiarCampos();
                return NavigationController.LOGIN_PAGE + "?faces-redirect=true";
            } else {
                mostrarMensajeError("Error al registrar el usuario. Intente nuevamente.");
                return null;
            }
            
        } catch (Exception e) {
            mostrarMensajeError("Error interno del sistema. Intente nuevamente.");
            e.printStackTrace();
            return null;
        }
    }
    
   
    private boolean validarCampos() {
        boolean esValido = true;
        
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarMensajeError("El nombre completo es obligatorio");
            esValido = false;
        } else if (nombre.trim().length() < 2) {
            mostrarMensajeError("El nombre debe tener al menos 2 caracteres");
            esValido = false;
        }
        
        // Validar correo
        if (correo == null || correo.trim().isEmpty()) {
            mostrarMensajeError("El correo electrónico es obligatorio");
            esValido = false;
        } else if (!EMAIL_PATTERN.matcher(correo.trim()).matches()) {
            mostrarMensajeError("El formato del correo electrónico no es válido");
            esValido = false;
        }
        
        // Validar teléfono
        if (telefono == null || telefono.trim().isEmpty()) {
            mostrarMensajeError("El teléfono es obligatorio");
            esValido = false;
        } else if (!PHONE_PATTERN.matcher(telefono.trim()).matches()) {
            mostrarMensajeError("El teléfono debe contener solo números (9-15 dígitos)");
            esValido = false;
        }
        
        // Validar dirección
        if (direccion == null || direccion.trim().isEmpty()) {
            mostrarMensajeError("La dirección es obligatoria");
            esValido = false;
        } else if (direccion.trim().length() < 5) {
            mostrarMensajeError("La dirección debe tener al menos 5 caracteres");
            esValido = false;
        }
        
        // Validar contraseña
        if (contrasena == null || contrasena.isEmpty()) {
            mostrarMensajeError("La contraseña es obligatoria");
            esValido = false;
        } else if (contrasena.length() < 6) {
            mostrarMensajeError("La contraseña debe tener al menos 6 caracteres");
            esValido = false;
        }
        
        // Validar confirmación de contraseña
        if (confirmarContrasena == null || confirmarContrasena.isEmpty()) {
            mostrarMensajeError("Debe confirmar la contraseña");
            esValido = false;
        }
        
        return esValido;
    }
    
    /**
     * Navega a la página de login
     * @return String para navegación
     */
    public String irALogin() {
        return NavigationController.LOGIN_PAGE + "?faces-redirect=true";
    }
    
    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        this.nombre = null;
        this.correo = null;
        this.telefono = null;
        this.direccion = null;
        this.contrasena = null;
        this.confirmarContrasena = null;
    }
    
    /**
     * Muestra un mensaje de error
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }
    
    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
}
