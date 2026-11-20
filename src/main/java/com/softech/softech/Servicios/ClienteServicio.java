package com.softech.softech.Servicios;

import com.softech.softech.DTOs.ClientesDTO;
import com.softech.softech.Mapper.ClientesMapper;
import com.softech.softech.modelo.clientes;
import com.softech.softech.repositorio.reposi_clientes;
import com.softech.softech.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServicio {

    @Autowired
    private reposi_clientes repo;

    public List<ClientesDTO> listar() {
        Long userId = CurrentUser.id();
        if (userId != null && "CLIENTE".equals(CurrentUser.role())) {
            return repo.findAll().stream()
                    .filter(c -> userId.equals(c.getIdUsuario()))
                    .map(ClientesMapper::toDTO)
                    .collect(Collectors.toList());
        }
        return repo.findAll().stream().map(ClientesMapper::toDTO).collect(Collectors.toList());
    }

    public ClientesDTO insertar(ClientesDTO dto) {
        clientes entidad = ClientesMapper.toEntity(dto);
        clientes guardado = repo.save(entidad);
        return ClientesMapper.toDTO(guardado);
    }

    public ClientesDTO actualizar(Long id, ClientesDTO dto) {
        Optional<clientes> existe = repo.findById(id);
        if (existe.isPresent()) {
            clientes existente = existe.get();
            existente.setIdUsuario(dto.getIdUsuario());
            existente.setNombreCliente(dto.getNombreCliente());
            clientes actualizado = repo.save(existente);
            return ClientesMapper.toDTO(actualizado);
        } else {
            throw new RuntimeException("Clientes no encontrado con id " + id);
        }
    }

    public String eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Clientes eliminado correctamente";
        } else {
            return "Clientes con id " + id + " no encontrado";
        }
    }
}
