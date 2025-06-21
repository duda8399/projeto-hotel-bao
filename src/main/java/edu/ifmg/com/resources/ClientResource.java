package edu.ifmg.com.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClientResource {
    @GetMapping("/")
    public String hello() {
        return "API do Hotel Bão está rodando!";
    }
}
