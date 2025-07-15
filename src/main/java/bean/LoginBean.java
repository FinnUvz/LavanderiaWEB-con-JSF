package bean;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import dto.UsuarioDTO;
import enums.RolUsuario;
import util.NavigationController;


import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Managed Bean para el manejo del login de usuarios
 */
@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Campos del formulario
    private String correo;
    private String contrasena;

    // Usuario logueado
    private UsuarioDTO usuarioLogueado;

    // DAO para acceso a datos
    private UsuarioDAO usuarioDAO;

    // Constructor
    public LoginBean() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Método para autenticar usuario
     *
     * @return String para navegación
     */
    public String login() {
        try {
            // Validar campos obligatorios
            if (correo == null || correo.trim().isEmpty()) {
                mostrarMensajeError("El correo electrónico es obligatorio");
                return null;
            }

            if (contrasena == null || contrasena.trim().isEmpty()) {
                mostrarMensajeError("La contraseña es obligatoria");
                return null;
            }

            // Buscar usuario por correo
            UsuarioDTO usuario = usuarioDAO.buscarPorCorreo(correo.trim());

            if (usuario == null) {
                mostrarMensajeError("Usuario o contraseña incorrectos");
                return null;
            }

            if (!contrasena.equals(usuario.getContrasena())) {
                mostrarMensajeError("Usuario o contraseña incorrectos");
                return null;
            }

            // Autenticación exitosa
            this.usuarioLogueado = usuario;
            NavigationController.mostrarMensajeExito("¡Bienvenido " + usuario.getNombre() + "!");

            // Limpiar campos
            limpiarCampos();

            // Redirigir según rol
            return NavigationController.redirigirSegunRol(usuario);

        } catch (Exception e) {
            mostrarMensajeError("Error interno del sistema. Intente nuevamente.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método para cerrar sesión
     *
     * @return String para navegación
     */
    public String logout() {
        try {
            this.usuarioLogueado = null;
            limpiarCampos();
            NavigationController.mostrarMensajeExito("Sesión cerrada exitosamente");
            return NavigationController.logout();
        } catch (Exception e) {
            mostrarMensajeError("Error al cerrar sesión");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si hay un usuario logueado
     *
     * @return true si hay usuario logueado
     */
    public boolean isLogueado() {
        return usuarioLogueado != null || NavigationController.hayUsuarioLogueado();
    }

    /**
     * Obtiene el usuario logueado
     *
     * @return UsuarioDTO del usuario logueado
     */
    public UsuarioDTO getUsuarioLogueado() {
        if (usuarioLogueado == null) {
            usuarioLogueado = NavigationController.obtenerUsuarioDeSesion();
        }
        return usuarioLogueado;
    }

    /**
     * Verifica si el usuario es administrador
     *
     * @return true si es administrador
     */
    public boolean isAdministrador() {
        return NavigationController.esAdministrador();
    }

    /**
     * Verifica si el usuario es cliente
     *
     * @return true si es cliente
     */
    public boolean isCliente() {
        return NavigationController.esCliente();
    }

    /**
     * Obtiene información de la sesión actual
     *
     * @return String con información de sesión
     */
    public String getInfoSesion() {
        return NavigationController.getInfoSesion();
    }

    /**
     * Navega a la página de registro
     *
     * @return String para navegación
     */
    public String irARegistro() {
        return "/registro.xhtml?faces-redirect=true";
    }

    /**
     * Limpia los campos del formulario
     */
    private void limpiarCampos() {
        this.correo = null;
        this.contrasena = null;
    }

    /**
     * Muestra un mensaje de error
     *
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

}
