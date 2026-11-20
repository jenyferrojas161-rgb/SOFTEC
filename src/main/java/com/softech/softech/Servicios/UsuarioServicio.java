package com.softech.softech.Servicios;

import com.softech.softech.DTOs.UsuarioDTO;
import com.softech.softech.Mapper.UsuarioMapper;
import com.softech.softech.modelo.usuario;
import com.softech.softech.repositorio.reposi_usuario;
import com.softech.softech.security.PasswordPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio {

    @Autowired private reposi_usuario repo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PasswordPolicy passwordPolicy;

    public List<UsuarioDTO> listar() {
        return repo.findAll().stream().map(UsuarioMapper::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO insertar(UsuarioDTO dto) {
        passwordPolicy.validar(dto.getContrasena());
        if (repo.existsByEmailIgnoreCase(dto.getEmail())) throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado.");
        if (repo.existsByIdentificacion(dto.getIdentificacion())) throw new IllegalArgumentException("El documento de identidad ya se encuentra registrado.");
        usuario entidad = UsuarioMapper.toEntity(dto);
        entidad.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario guardado = repo.save(entidad);
        return UsuarioMapper.toDTO(guardado);
    }

    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        usuario existente = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));
        existente.setIdentificacion(dto.getIdentificacion());
        existente.setNombreUsuario(dto.getNombreUsuario());
        existente.setTelefono(dto.getTelefono());
        existente.setEmail(dto.getEmail());
        existente.setTipoUsuario(dto.getTipoUsuario());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            passwordPolicy.validar(dto.getContrasena());
            existente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        return UsuarioMapper.toDTO(repo.save(existente));
    }

    public String eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Usuario eliminado correctamente";
        }
        return "Usuario con id " + id + " no encontrado";
    }
}
