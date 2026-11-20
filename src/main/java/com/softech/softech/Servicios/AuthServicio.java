package com.softech.softech.Servicios;

import com.softech.softech.DTOs.AuthResponse;
import com.softech.softech.DTOs.LoginRequest;
import com.softech.softech.DTOs.RegistroRequest;
import com.softech.softech.DTOs.ResetPasswordRequest;
import com.softech.softech.modelo.usuario;
import com.softech.softech.repositorio.reposi_usuario;
import com.softech.softech.security.JwtService;
import com.softech.softech.security.LimitadorIntentos;
import com.softech.softech.security.PasswordPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthServicio {

    private final reposi_usuario repo;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;
    private final JwtService jwtService;
    private final LimitadorIntentos limitador;

    private static final int MAX_FALLOS_CUENTA = 5;
    private static final int MAX_FALLOS_IP = 20;
    private static final String MSG_BLOQUEO = "Demasiados intentos fallidos. Espera 15 minutos e inténtalo de nuevo.";

    public AuthServicio(
            reposi_usuario repo,
            PasswordEncoder passwordEncoder,
            PasswordPolicy passwordPolicy,
            JwtService jwtService,
            LimitadorIntentos limitador) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
        this.jwtService = jwtService;
        this.limitador = limitador;
    }

    public AuthResponse login(LoginRequest request) {
        String identificador = request.identificador();
        String password = request.contrasena();

        if (identificador == null || identificador.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ingresa tu correo electrónico y contraseña.");
        }

        String clave = "login:" + identificador.trim().toLowerCase(Locale.ROOT);
        if (limitador.bloqueado(clave, MAX_FALLOS_CUENTA)) {
            throw new IllegalStateException(MSG_BLOQUEO);
        }

        // El ingreso es SOLO con correo electrónico.
        usuario u = repo.findByEmailIgnoreCase(identificador.trim()).orElse(null);
        if (u == null || !passwordEncoder.matches(password, u.getContrasena())) {
            limitador.registrarFallo(clave);
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }
        limitador.limpiar(clave);

        String rol = normalizarRol(u.getTipoUsuario());
        String token = jwtService.generar(u.getId(), rol, u.getNombreUsuario());
        return new AuthResponse(token, rol, u.getNombreUsuario(), u.getId());
    }

    @Transactional
    public usuario registrar(RegistroRequest r) {
        passwordPolicy.validar(r.contrasena());

        if (repo.existsByEmailIgnoreCase(r.email())) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado.");
        }
        if (repo.existsByIdentificacion(r.identificacion())) {
            throw new IllegalArgumentException("El documento de identidad ya se encuentra registrado.");
        }

        // El registro público SOLO crea clientes. Técnicos y administradores los crea un
        // administrador autenticado (POST /api/usuarios), nunca el registro abierto.
        String rol = normalizarRol(r.rol());
        if (!rol.equals("CLIENTE")) {
            throw new IllegalArgumentException("Las cuentas de técnico y administrador las crea la administración de SOFTEC.");
        }

        usuario u = new usuario();
        u.setIdentificacion(r.identificacion().trim());
        u.setNombreUsuario(r.nombreUsuario().trim());
        u.setTelefono(r.telefono() == null ? null : r.telefono().trim());
        u.setEmail(r.email().trim().toLowerCase(Locale.ROOT));
        u.setContrasena(passwordEncoder.encode(r.contrasena()));
        u.setTipoUsuario(salidaRol(rol));
        return repo.save(u);
    }

    /**
     * Recuperación de contraseña con verificación de identidad: el usuario/correo, el documento
     * y el teléfono deben coincidir con los registrados. Con límite de intentos fallidos.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest r, String ip) {
        String identificador = r.identificador() == null ? "" : r.identificador().trim();
        String claveCuenta = "reset:" + identificador.toLowerCase(Locale.ROOT);
        String claveIp = "resetip:" + ip;
        if (limitador.bloqueado(claveCuenta, MAX_FALLOS_CUENTA) || limitador.bloqueado(claveIp, MAX_FALLOS_IP)) {
            throw new IllegalStateException(MSG_BLOQUEO);
        }
        if (identificador.isEmpty() || vacio(r.identificacion()) || vacio(r.telefono())) {
            throw new IllegalArgumentException("Completa tu usuario o correo, tu documento y tu teléfono registrado.");
        }
        passwordPolicy.validar(r.nuevaContrasena());

        usuario u = repo.buscarPorIdentificador(identificador).orElse(null);
        boolean verificado = u != null
                && coincide(u.getIdentificacion(), r.identificacion())
                && mismoTelefono(u.getTelefono(), r.telefono());
        if (!verificado) {
            limitador.registrarFallo(claveCuenta);
            limitador.registrarFallo(claveIp);
            throw new IllegalArgumentException("Los datos de verificación no coinciden con ninguna cuenta registrada.");
        }
        limitador.limpiar(claveCuenta);
        u.setContrasena(passwordEncoder.encode(r.nuevaContrasena()));
        repo.save(u);
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static boolean coincide(String a, String b) {
        return !vacio(a) && !vacio(b) && a.trim().equalsIgnoreCase(b.trim());
    }

    /** Compara solo dígitos; acepta que uno de los dos lleve indicativo de país (+57). */
    private static boolean mismoTelefono(String guardado, String enviado) {
        String x = guardado == null ? "" : guardado.replaceAll("\\D", "");
        String y = enviado == null ? "" : enviado.replaceAll("\\D", "");
        return x.length() >= 7 && y.length() >= 7 && (x.endsWith(y) || y.endsWith(x));
    }

    private String normalizarRol(String rol) {
        if (rol == null) return "CLIENTE";
        return switch (rol.trim().toUpperCase(Locale.ROOT)) {
            case "ADMIN", "ADMINISTRADOR" -> "ADMIN";
            case "TECNICO", "TÉCNICO" -> "TECNICO";
            case "RECEPCIONISTA" -> "RECEPCIONISTA";
            default -> "CLIENTE";
        };
    }

    private String salidaRol(String rol) {
        return switch (rol) {
            case "ADMIN" -> "Administrador";
            case "TECNICO" -> "Tecnico";
            case "RECEPCIONISTA" -> "Recepcionista";
            default -> "Cliente";
        };
    }
}
