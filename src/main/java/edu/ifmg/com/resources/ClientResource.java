package edu.ifmg.com.resources;

import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/client")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClientResource {

    @Autowired
    private ClientService clientService;

    @GetMapping("/")
    public String hello() {
        return "API do Hotel Bão está rodando!";
    }

    @PostMapping
    public ResponseEntity<ClientDTO> insert(@RequestBody ClientDTO clientDTO) {
        ClientDTO newClient = clientService.insert(clientDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newClient.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newClient);
    }
}
