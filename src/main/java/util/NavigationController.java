package util;

import dto.UsuarioDTO;
import enums.RolUsuario;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;

/**
 * Controlador para manejar la navegación y sesiones del usuario
 */
public class NavigationController {
    
    public static final String LOGIN_PAGE = "/login.xhtml";
    public static final String REGISTRO_PAGE = "/registro.xhtml";
    public static final String CLIENTE_INICIO = "/inicio.xhtml";
    public static final String ADMIN_DASHBOARD = "/dashboard.xhtml";
    public static final String ERROR_PAGE = "/error.xhtml";
    

    public static final String USUARIO_SESSION = "usuarioLogueado";
    public static final String ROL_SESSION = "rolUsuario";
    
    
    public static String redirigirSegunRol(UsuarioDTO usuario) {
        if (usuario == null) {
            return LOGIN_PAGE;
        }
        
        // Guardar usuario en sesión
        guardarUsuarioEnSesion(usuario);
        
        // Redirigir según el rol
        switch (usuario.getRol().toLowerCase()) {
            case "cliente":
                return CLIENTE_INICIO + "?faces-redirect=true";
            case "administrador":
                return ADMIN_DASHBOARD + "?faces-redirect=true";
            default:
                mostrarMensajeError("Rol de usuario no reconocido");
                return LOGIN_PAGE;
        }
    }
    
    /**
     * Guarda el usuario en la sesión
     * @param usuario El usuario a guardar
     */
    public static void guardarUsuarioEnSesion(UsuarioDTO usuario) {
        HttpSession session = obtenerSesion();
        session.setAttribute(USUARIO_SESSION, usuario);
        session.setAttribute(ROL_SESSION, usuario.getRol());
    }
    
    
    public static UsuarioDTO obtenerUsuarioDeSesion() {
        HttpSession session = obtenerSesion();
        return (UsuarioDTO) session.getAttribute(USUARIO_SESSION);
    }
    
   
    public static String obtenerRolDeSesion() {
        HttpSession session = obtenerSesion();
        return (String) session.getAttribute(ROL_SESSION);
    }
    
    /**
     * Verifica si hay un usuario logueado
     * @return true si hay usuario logueado, false en caso contrario
     */
    public static boolean hayUsuarioLogueado() {
        return obtenerUsuarioDeSesion() != null;
    }
    
    
    public static boolean usuarioTieneRol(String rol) {
        String rolSession = obtenerRolDeSesion();
        return rolSession != null && rolSession.equalsIgnoreCase(rol);
    }
    
   
    public static boolean esAdministrador() {
        return usuarioTieneRol(RolUsuario.ADMINISTRADOR.getValor());
    }
    
   
    public static boolean esCliente() {
        return usuarioTieneRol(RolUsuario.CLIENTE.getValor());
    }
    
    
    public static void cerrarSesion() {
        HttpSession session = obtenerSesion();
        session.invalidate();
    }
    
   
    public static String logout() {
        cerrarSesion();
        return LOGIN_PAGE + "?faces-redirect=true";
    }
    
    
    public static boolean tieneAcceso(String paginaRequerida, String rolRequerido) {
        if (!hayUsuarioLogueado()) {
            return false;
        }
        
        return usuarioTieneRol(rolRequerido);
    }
    
    
    public static void redirigir(String pagina) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            mostrarMensajeError("Error al redirigir: " + e.getMessage());
        }
    }
    
    
    public static void verificarSesion() {
        if (!hayUsuarioLogueado()) {
            redirigir(LOGIN_PAGE);
        }
    }
   
    public static void verificarAcceso(String rolRequerido) {
        if (!hayUsuarioLogueado()) {
            mostrarMensajeError("Debe iniciar sesión para acceder a esta página");
            redirigir(LOGIN_PAGE);
            return;
        }
        
        if (!usuarioTieneRol(rolRequerido)) {
            mostrarMensajeError("No tiene permisos para acceder a esta página");
            redirigir(LOGIN_PAGE);
        }
    }
    
    private static HttpSession obtenerSesion() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }
    
    public static void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }
    
    public static void mostrarMensajeExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }
    
    public static void mostrarMensajeAdvertencia(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensaje));
    }
    
    public static String getInfoSesion() {
        UsuarioDTO usuario = obtenerUsuarioDeSesion();
        if (usuario != null) {
            return "Usuario: " + usuario.getNombre() + " (" + usuario.getRol() + ")";
        }
        return "No hay usuario logueado";
    }
}
