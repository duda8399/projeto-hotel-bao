package edu.ifmg.com.services;
import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.repositories.ClientRepository;
import edu.ifmg.com.services.exceptions.DatabaseException;
import edu.ifmg.com.services.exceptions.ResourceNotFound;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "Obtenha todos os clientes",
            summary = "Listar todos os clientes cadastrados",
            responses = {
                    @ApiResponse(description = "ok", responseCode = "200"),
            }
    )
    public Page<ClientDTO> findAll(Pageable pageable) {
        Page<Client> list = clientRepository.findAll(pageable);
        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Cliente não encontrado"));
        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        Client entity = new Client();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity = clientRepository.save(entity);
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try {
            Client entity = clientRepository.getReferenceById(id);
            entity.setName(dto.getName());
            entity.setEmail(dto.getEmail());
            entity.setPassword(dto.getPassword());
            entity.setPhone(dto.getPhone());
            entity = clientRepository.save(entity);
            return new ClientDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Cliente não encontrado: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFound("Cliente não encontrado - ID: " + id);
        }
        try {
            clientRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integridade violada");
        }
    }
}
