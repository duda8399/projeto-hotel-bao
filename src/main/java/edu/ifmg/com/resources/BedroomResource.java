package edu.ifmg.com.resources;

import edu.ifmg.com.dto.BedroomDTO;
import edu.ifmg.com.services.BedroomService;
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
@RequestMapping(value = "/bedroom")
@Tag(name = "Quartos", description = "API para gerenciamento de quartos")
public class BedroomResource {

    @Autowired
    private BedroomService bedroomService;

    @GetMapping
    public ResponseEntity<Page<BedroomDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);

        Page<BedroomDTO> rooms = bedroomService.findAll(pageable);
        return ResponseEntity.ok().body(rooms);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BedroomDTO> findById(@PathVariable Long id) {
        BedroomDTO bedroom = bedroomService.findById(id);
        return ResponseEntity.ok().body(bedroom);
    }

    @PostMapping
    public ResponseEntity<BedroomDTO> insert(@RequestBody BedroomDTO bedroomDTO) {
        BedroomDTO newBedroom = bedroomService.insert(bedroomDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newBedroom.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newBedroom);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<BedroomDTO> update(@PathVariable Long id, @RequestBody BedroomDTO bedroomDTO) {
        BedroomDTO updatedBedroom = bedroomService.update(id, bedroomDTO);
        return ResponseEntity.ok().body(updatedBedroom);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bedroomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
