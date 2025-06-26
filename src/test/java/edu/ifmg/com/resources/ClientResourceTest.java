package edu.ifmg.com.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.oauth.JwtAuthenticationFilter;
import edu.ifmg.com.oauth.JwtService;
import edu.ifmg.com.services.ClientService;
import edu.ifmg.com.services.exceptions.ResourceNotFound;

@WebMvcTest(ClientResource.class)
@AutoConfigureMockMvc(addFilters = false) // desativa filtros como Jwt para simplificar o teste
@Import(ClientResourceTest.TestExceptionHandler.class) // Importa o handler
class ClientResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // Use @MockBean para o Spring injetar no controller
    private ClientService clientService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Long existingId;
    private ClientDTO clientDTO;
    private PageImpl<ClientDTO> page;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        clientDTO = new ClientDTO("Maria", "maria@example.com", "senha123", "31999999999", "Rua X, 123", "BH");
        clientDTO.setId(existingId);
        page = new PageImpl<>(List.of(clientDTO));
    }

    @Test
    void findAll_ShouldReturnPage() throws Exception {
        when(clientService.findAll(any(Pageable.class))).thenReturn(page);

        ResultActions result = mockMvc.perform(get("/client")
            .param("page", "0")
            .param("size", "10")
            .param("direction", "ASC")
            .param("orderBy", "id")
            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    void findById_ShouldReturnClient_WhenFound() throws Exception {
        when(clientService.findById(existingId)).thenReturn(clientDTO);

        ResultActions result = mockMvc.perform(
            get("/client/{id}", existingId).accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));

        verify(clientService).findById(existingId);
    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(clientService.findById(99L)).thenThrow(new ResourceNotFound("Cliente não encontrado"));

        ResultActions result = mockMvc.perform(
            get("/client/{id}", 99L).accept(MediaType.APPLICATION_JSON));

        // Agora pode testar o status HTTP ao invés da exceção
        result.andExpect(status().isNotFound())
            .andExpect(content().string("Cliente não encontrado"));

        verify(clientService).findById(99L);
    }

    @Test
    void insert_ShouldCreateClient() throws Exception {
        ClientDTO inputDTO = new ClientDTO("João", "joao@example.com", "senha", "31988887777", "Rua B, 45", "São Paulo");
        ClientDTO savedDTO = new ClientDTO("João", "joao@example.com", "senha", "31988887777", "Rua B, 45", "São Paulo");
        savedDTO.setId(2L);

        when(clientService.insert(any(ClientDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/client/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("João"));

        verify(clientService).insert(any(ClientDTO.class));
    }

    @Test
    void update_ShouldReturnUpdatedClient() throws Exception {
        ClientDTO updatedDTO = new ClientDTO("Ana Atualizada", "ana@example.com", "novaSenha", "31977776666", "Av. C, 789", "Rio");
        updatedDTO.setId(1L);

        when(clientService.update(eq(1L), any(ClientDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/client/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ana Atualizada"));

        verify(clientService).update(eq(1L), any(ClientDTO.class));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(clientService).delete(existingId);

        ResultActions result = mockMvc.perform(delete("/client/{id}", existingId));

        result.andExpect(status().isNoContent());
        
        verify(clientService).delete(existingId); // Add this verification 

    }

    // Handler local configurado como @ControllerAdvice
    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(ResourceNotFound.class)
        public ResponseEntity<String> handleNotFound(ResourceNotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
