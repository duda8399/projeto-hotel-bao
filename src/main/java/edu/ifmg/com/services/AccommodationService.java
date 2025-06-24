package edu.ifmg.com.services;
import edu.ifmg.com.dto.AccommodationDTO;
import edu.ifmg.com.entities.Accommodation;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.repositories.AccommodationRepository;
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

import java.util.List;

@Service
public class AccommodationService {
    @Autowired
    private AccommodationRepository accommodationRepository;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "Obtenha todas as acomodações",
            summary = "Listar todas as acomodações cadastradas",
            responses = {
                    @ApiResponse(description = "ok", responseCode = "200"),
            }
    )
    public Page<AccommodationDTO> findAll(Pageable pageable) {
        Page<Accommodation> list = accommodationRepository.findAll(pageable);
        return list.map(AccommodationDTO::new);
    }

    @Transactional(readOnly = true)
    public AccommodationDTO findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Acomodação não encontrada"));
        return new AccommodationDTO(accommodation);
    }

    @Transactional
    public AccommodationDTO insert(AccommodationDTO dto) {
        Accommodation entity = new Accommodation();
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
        entity.setImageUrl(dto.getImageUrl());
        entity = accommodationRepository.save(entity);
        return new AccommodationDTO(entity);
    }

    @Transactional
    public AccommodationDTO update(Long id, AccommodationDTO dto) {
        try {
            Accommodation entity = accommodationRepository.getReferenceById(id);
            entity.setDescription(dto.getDescription());
            entity.setValue(dto.getValue());
            entity.setImageUrl(dto.getImageUrl());
            entity = accommodationRepository.save(entity);
            return new AccommodationDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Acomodação não encontrada: " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new ResourceNotFound("Acomodação não encontrada - ID: " + id);
        }
        try {
            accommodationRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integridade violada");
        }
    }

    public List<String> accommodationList() {
        List<Accommodation> accommodations = accommodationRepository.findAll();

        return accommodations.stream()
                .map(c -> String.format("Quarto: - Código: %d  - Descrição: %s - Valor: R$%s",
                        c.getId(), c.getDescription(), c.getValue()))
                .toList();
    }
}
