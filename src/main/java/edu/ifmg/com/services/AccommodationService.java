package edu.ifmg.com.services;

import edu.ifmg.com.dto.AccommodationDTO;
import edu.ifmg.com.entities.Accommodation;
import edu.ifmg.com.repositories.AccommodationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Transactional(readOnly = true)
    public Page<AccommodationDTO> findAll(Pageable pageable) {
        Page<Accommodation> page = accommodationRepository.findAll(pageable);
        return page.map(AccommodationDTO::new);
    }

    @Transactional(readOnly = true)
    public AccommodationDTO findById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acomodação não encontrada"));
        return new AccommodationDTO(accommodation);
    }

    @Transactional
    public AccommodationDTO insert(AccommodationDTO dto) {
        Accommodation accommodation = new Accommodation(
                dto.getClient(),
                dto.getBedroom(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );
        accommodation = accommodationRepository.save(accommodation);
        return new AccommodationDTO(accommodation);
    }

    @Transactional
    public AccommodationDTO update(Long id, AccommodationDTO dto) {
        try {
            Accommodation accommodation = accommodationRepository.getReferenceById(id);
            accommodation.setClient(dto.getClient());
            accommodation.setBedroom(dto.getBedroom());
            accommodation.setCheckInDate(dto.getCheckInDate());
            accommodation.setCheckOutDate(dto.getCheckOutDate());
            return new AccommodationDTO(accommodationRepository.save(accommodation));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Acomodação não encontrada");
        }
    }

    public void delete(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Acomodação não encontrada");
        }
        accommodationRepository.deleteById(id);
    }
}
