package edu.ifmg.com.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ifmg.com.dto.AccommodationDTO;
import edu.ifmg.com.oauth.JwtAuthenticationFilter;
import edu.ifmg.com.oauth.JwtService;
import edu.ifmg.com.services.AccommodationService;
import edu.ifmg.com.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccommodationResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AccommodationResourceTest.TestExceptionHandler.class)
class AccommodationResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccommodationService accommodationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private AccommodationDTO accommodationDTO;

    @BeforeEach
    void setUp() {
        accommodationDTO = new AccommodationDTO();
        accommodationDTO.setId(1L);
        accommodationDTO.setDescription("Suíte com vista para o mar e ar-condicionado");
        accommodationDTO.setValue(299.90);
        accommodationDTO.setImageUrl("https://example.com/images/quarto1.jpg");
    }

    @Test
    void findAll_ShouldReturnPage() throws Exception {
        Page<AccommodationDTO> page = new PageImpl<>(List.of(accommodationDTO));
        when(accommodationService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/accommodation")
                        .param("page", "0")
                        .param("size", "10")
                        .param("direction", "ASC")
                        .param("orderBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(accommodationDTO.getId()))
                .andExpect(jsonPath("$.content[0].description").value(accommodationDTO.getDescription()))
                .andExpect(jsonPath("$.content[0].value").value(accommodationDTO.getValue()));

        verify(accommodationService).findAll(any(Pageable.class));
    }

    @Test
    void findById_ShouldReturnAccommodation_WhenFound() throws Exception {
        when(accommodationService.findById(1L)).thenReturn(accommodationDTO);

        mockMvc.perform(get("/accommodation/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accommodationDTO.getId()))
                .andExpect(jsonPath("$.description").value(accommodationDTO.getDescription()))
                .andExpect(jsonPath("$.value").value(accommodationDTO.getValue()))
                .andExpect(jsonPath("$.imageUrl").value(accommodationDTO.getImageUrl()));

        verify(accommodationService).findById(1L);
    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(accommodationService.findById(99L)).thenThrow(new ResourceNotFound("Acomodação não encontrada"));

        mockMvc.perform(get("/accommodation/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(accommodationService).findById(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void insert_ShouldCreateAccommodation() throws Exception {
        AccommodationDTO inputDTO = new AccommodationDTO();
        inputDTO.setDescription("Quarto Standard com TV e frigobar");
        inputDTO.setValue(199.50);
        inputDTO.setImageUrl("https://example.com/images/quarto2.jpg");

        AccommodationDTO savedDTO = new AccommodationDTO();
        savedDTO.setId(2L);
        savedDTO.setDescription("Quarto Standard com TV e frigobar");
        savedDTO.setValue(199.50);
        savedDTO.setImageUrl("https://example.com/images/quarto2.jpg");

        when(accommodationService.insert(any(AccommodationDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/accommodation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/accommodation/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("Quarto Standard com TV e frigobar"))
                .andExpect(jsonPath("$.value").value(199.50));

        verify(accommodationService).insert(any(AccommodationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdatedAccommodation() throws Exception {
        AccommodationDTO updatedDTO = new AccommodationDTO();
        updatedDTO.setId(1L);
        updatedDTO.setDescription("Suíte Presidencial com jacuzzi");
        updatedDTO.setValue(599.90);
        updatedDTO.setImageUrl("https://example.com/images/suite-presidencial.jpg");

        when(accommodationService.update(eq(1L), any(AccommodationDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/accommodation/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Suíte Presidencial com jacuzzi"))
                .andExpect(jsonPath("$.value").value(599.90));

        verify(accommodationService).update(eq(1L), any(AccommodationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(accommodationService).delete(1L);

        mockMvc.perform(delete("/accommodation/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(accommodationService).delete(1L);
    }

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(ResourceNotFound.class)
        public ResponseEntity<String> handleNotFound(ResourceNotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}