package edu.ifmg.com.services;
import edu.ifmg.com.dto.BedroomDTO;
import edu.ifmg.com.entities.Bedroom;
import edu.ifmg.com.repositories.BedroomRepository;
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
public class BedroomService {
    @Autowired
    private BedroomRepository bedroomRepository;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "Obtenha todos os quartos",
            summary = "Listar todos os quartos cadastrados",
            responses = {
                    @ApiResponse(description = "ok", responseCode = "200"),
            }
    )
    public Page<BedroomDTO> findAll(Pageable pageable) {
        Page<Bedroom> list = bedroomRepository.findAll(pageable);
        return list.map(BedroomDTO::new);
    }

    @Transactional(readOnly = true)
    public BedroomDTO findById(Long id) {
        Bedroom bedroom = bedroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Quarto não encontrado"));
        return new BedroomDTO(bedroom);
    }

    @Transactional
    public BedroomDTO insert(BedroomDTO dto) {
        Bedroom entity = new Bedroom();
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
        entity.setImageUrl(dto.getImageUrl());
        entity = bedroomRepository.save(entity);
        return new BedroomDTO(entity);
    }

    @Transactional
    public BedroomDTO update(Long id, BedroomDTO dto) {
        try {
            Bedroom entity = bedroomRepository.getReferenceById(id);
            entity.setDescription(dto.getDescription());
            entity.setValue(dto.getValue());
            entity.setImageUrl(dto.getImageUrl());
            entity = bedroomRepository.save(entity);
            return new BedroomDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Quarto não encontrado: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!bedroomRepository.existsById(id)) {
            throw new ResourceNotFound("Quarto não encontrado - ID: " + id);
        }
        try {
            bedroomRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integridade violada");
        }
    }
}
