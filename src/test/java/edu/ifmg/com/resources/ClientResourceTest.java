package edu.ifmg.com.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.services.ClientService;
import edu.ifmg.com.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientResourceTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientResource clientResource;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientResource)
                .setControllerAdvice(new ExceptionHandlerController()) // opcional para tratamento de erros globais
                .build();

        clientDTO = new ClientDTO("Maria", "maria@example.com", "senha123", "31999999999", "Rua X, 123", "BH");
        clientDTO.setId(1L);
    }

    @Test
    void findAll_ShouldReturnPage() throws Exception {
        Page<ClientDTO> page = new PageImpl<>(List.of(clientDTO));
        when(clientService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/client")
                        .param("page", "0")
                        .param("size", "10")
                        .param("direction", "ASC")
                        .param("orderBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.content[0].name").value(clientDTO.getName()));

        verify(clientService).findAll(any(Pageable.class));
    }

    @Test
    void findById_ShouldReturnClient_WhenFound() throws Exception {
        when(clientService.findById(1L)).thenReturn(clientDTO);

        mockMvc.perform(get("/client/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.email").value(clientDTO.getEmail()));

        verify(clientService).findById(1L);
    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(clientService.findById(99L)).thenThrow(new ResourceNotFound("Cliente não encontrado"));

        mockMvc.perform(get("/client/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clientService).findById(99L);
    }

    @Test
    void insert_ShouldCreateClient() throws Exception {
        ClientDTO inputDTO = new ClientDTO("João", "joao@example.com", "senha", "31988887777", "Rua B, 45", "São Paulo");
        ClientDTO savedDTO = new Client(inputDTO);
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
        doNothing().when(clientService).delete(1L);

        mockMvc.perform(delete("/client/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clientService).delete(1L);
    }

    // Opcional: Tratador global de exceções para retornar 404 em ResourceNotFound
    // Se não tiver, pode criar uma classe assim:
    public static class ExceptionHandlerController {
        @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFound.class)
        public org.springframework.http.ResponseEntity<String> handleNotFound(ResourceNotFound ex) {
            return org.springframework.http.ResponseEntity.status(404).body(ex.getMessage());
        }
    }
}
