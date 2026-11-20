package com.softech.softech.DTOs;


public class UsuarioDTO {
    private Long id;
    private String identificacion;
    private String nombreUsuario;
    private String telefono;
    private String email;
    private String contrasena;
    private String tipoUsuario;

    public UsuarioDTO(Long id, String identificacion, String nombreUsuario, String telefono, String email, String contrasena, String tipoUsuario) {
        this.id = id;
        this.identificacion = identificacion;
        this.nombreUsuario = nombreUsuario;
        this.telefono = telefono;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" + "id=" + id + ", " + "identificacion=" + identificacion + ", " + "nombreUsuario=" + nombreUsuario + ", " + "telefono=" + telefono + ", " + "email=" + email + ", " + "tipoUsuario=" + tipoUsuario + "}";
    }
}
