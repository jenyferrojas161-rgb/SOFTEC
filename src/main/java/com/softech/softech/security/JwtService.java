package com.softech.softech.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration expiration;

    public JwtService(
            @Value("${softec.security.jwt-secret}") String secret,
            @Value("${softec.security.jwt-expiration-minutes:480}") long expirationMinutes) {
        byte[] decoded = Decoders.BASE64.decode(secret);
        if (decoded.length < 32) {
            throw new IllegalArgumentException("softec.security.jwt-secret debe tener al menos 256 bits en Base64.");
        }
        this.key = Keys.hmacShaKeyFor(decoded);
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generar(Long idUsuario, String rol, String nombre) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim("rol", normalizarRol(rol))
                .claim("nombre", nombre)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    public Claims validar(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long obtenerUsuarioId(String token) {
        return Long.valueOf(validar(token).getSubject());
    }

    public String obtenerRol(String token) {
        Object rol = validar(token).get("rol");
        return rol == null ? "" : rol.toString();
    }

    private String normalizarRol(String rol) {
        if (rol == null) return "";
        return switch (rol) {
            case "Administrador", "ADMIN" -> "ADMIN";
            case "Tecnico", "Técnico", "TECNICO" -> "TECNICO";
            case "Cliente", "CLIENTE" -> "CLIENTE";
            case "Recepcionista", "RECEPCIONISTA" -> "RECEPCIONISTA";
            default -> rol.toUpperCase();
        };
    }
}
