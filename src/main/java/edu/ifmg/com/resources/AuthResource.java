package edu.ifmg.com.resources;

import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.dto.LoginRequestDTO;
import edu.ifmg.com.dto.LoginResponseDTO;
import edu.ifmg.com.oauth.JwtService;
import edu.ifmg.com.services.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para autenticação de clientes")
public class AuthResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<ClientDTO> register(@RequestBody ClientDTO dto) {
        ClientDTO newClient = clientService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newClient.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newClient);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            log.info("Login bem-sucedido para o e-mail: {}", loginRequest.getEmail());
            return new LoginResponseDTO(token);
        } catch (Exception e) {
            log.error("Erro ao autenticar {}: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }
}
