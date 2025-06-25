package edu.ifmg.com.resources;

import edu.ifmg.com.dto.LoginRequestDTO;
import edu.ifmg.com.dto.LoginResponseDTO;
import edu.ifmg.com.oauth.JwtService;
import edu.ifmg.com.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthResourceTest {

    @InjectMocks
    private AuthResource authResource;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientService clientService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() throws Exception {
        String json = """
            {
              "name": "Maria Eduarda",
              "email": "maria@example.com",
              "password": "123456",
              "phone": "999999999",
              "address": "Rua A",
              "city": "Belo Horizonte"
            }
            """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Maria Eduarda"));
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("maria@example.com", "123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token123");

        LoginResponseDTO response = authResource.login(loginRequest);

        assertNotNull(response);
        assertEquals("token123", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("maria@example.com", "senhaErrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> {
            authResource.login(loginRequest);
        });

        assertEquals("Credenciais inválidas", thrown.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}
