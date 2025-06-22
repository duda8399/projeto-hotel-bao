package edu.ifmg.com.resources;

import edu.ifmg.com.dto.ReservationDTO;
import edu.ifmg.com.services.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/reservation")
@Tag(name = "Reservas", description = "API para gerenciamento de reservas")
public class ReservationResource {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<Page<ReservationDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<ReservationDTO> reservations = reservationService.findAll(pageable);
        return ResponseEntity.ok().body(reservations);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationDTO> findById(@PathVariable Long id) {
        ReservationDTO dto = reservationService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> insert(@RequestBody ReservationDTO dto) {
        ReservationDTO newReservation = reservationService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newReservation.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newReservation);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ReservationDTO> update(@PathVariable Long id, @RequestBody ReservationDTO dto) {
        ReservationDTO updated = reservationService.update(id, dto);
        return ResponseEntity.ok().body(updated);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
