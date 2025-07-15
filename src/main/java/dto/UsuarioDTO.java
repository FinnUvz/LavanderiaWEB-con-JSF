package dto;

import java.io.Serializable;

/**
 * DTO para representar un usuario del sistema
 */
public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private String contrasena;
    private String rol;
    
    // Constructor vacío
    public UsuarioDTO() {}
    
    // Constructor completo
    public UsuarioDTO(int idUsuario, String nombre, String correo, String telefono, 
                     String direccion, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.contrasena = contrasena;
        this.rol = rol;
    }
    
    // Constructor para registro (sin ID)
    public UsuarioDTO(String nombre, String correo, String telefono, 
                     String direccion, String contrasena, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.contrasena = contrasena;
        this.rol = rol;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
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
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    // Método para verificar si es administrador
    public boolean isAdministrador() {
        return "administrador".equals(this.rol);
    }
    
    // Método para verificar si es cliente
    public boolean isCliente() {
        return "cliente".equals(this.rol);
    }
    
    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UsuarioDTO that = (UsuarioDTO) obj;
        return idUsuario == that.idUsuario && 
               correo != null ? correo.equals(that.correo) : that.correo == null;
    }
    
    @Override
    public int hashCode() {
        int result = idUsuario;
        result = 31 * result + (correo != null ? correo.hashCode() : 0);
        return result;
    }
}
