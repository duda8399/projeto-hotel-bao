package edu.ifmg.com.resources;

import edu.ifmg.com.dto.AccommodationDTO;
import edu.ifmg.com.services.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.net.URI;

@RestController
@RequestMapping(value = "/accommodation")
@Tag(name = "Acomodações", description = "API para gerenciamento de acomodações")
public class AccommodationResource {

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<Page<AccommodationDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);

        Page<AccommodationDTO> accommodations = accommodationService.findAll(pageable);
        return ResponseEntity.ok().body(accommodations);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccommodationDTO> findById(@PathVariable Long id) {
        AccommodationDTO accommodation = accommodationService.findById(id);
        return ResponseEntity.ok().body(accommodation);
    }

    @PostMapping
    public ResponseEntity<AccommodationDTO> insert(@RequestBody AccommodationDTO dto) {
        AccommodationDTO newAccommodation = accommodationService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAccommodation.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newAccommodation);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AccommodationDTO> update(@PathVariable Long id, @RequestBody AccommodationDTO dto) {
        AccommodationDTO updatedAccommodation = accommodationService.update(id, dto);
        return ResponseEntity.ok().body(updatedAccommodation);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accommodationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
