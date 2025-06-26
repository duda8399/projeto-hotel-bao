package edu.ifmg.com.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ifmg.com.dto.ReservationDTO;
import edu.ifmg.com.oauth.JwtAuthenticationFilter;
import edu.ifmg.com.oauth.JwtService;
import edu.ifmg.com.services.ReservationService;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ReservationResourceTest.TestExceptionHandler.class)
class ReservationResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        reservationDTO = new ReservationDTO();
        reservationDTO.setId(1L);
        reservationDTO.setClientId(1L);
        reservationDTO.setAccommodationId(1L);
        reservationDTO.setCheckInDate(Instant.parse("2025-07-20T14:00:00Z"));
        reservationDTO.setCheckOutDate(Instant.parse("2025-07-25T11:00:00Z"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnPage() throws Exception {
        Page<ReservationDTO> page = new PageImpl<>(List.of(reservationDTO));
        when(reservationService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/reservation")
                        .param("page", "0")
                        .param("size", "10")
                        .param("direction", "ASC")
                        .param("orderBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(reservationDTO.getId()))
                .andExpect(jsonPath("$.content[0].clientId").value(reservationDTO.getClientId()));

        verify(reservationService).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ShouldReturnReservation_WhenFound() throws Exception {
        when(reservationService.findById(1L)).thenReturn(reservationDTO);

        mockMvc.perform(get("/reservation/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationDTO.getId()))
                .andExpect(jsonPath("$.clientId").value(reservationDTO.getClientId()))
                .andExpect(jsonPath("$.accommodationId").value(reservationDTO.getAccommodationId()));

        verify(reservationService).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(reservationService.findById(99L)).thenThrow(new ResourceNotFound("Reserva não encontrada"));

        mockMvc.perform(get("/reservation/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(reservationService).findById(99L);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void insert_ShouldCreateReservation() throws Exception {
        ReservationDTO inputDTO = new ReservationDTO();
        inputDTO.setClientId(2L);
        inputDTO.setAccommodationId(2L);
        inputDTO.setCheckInDate(Instant.parse("2025-08-01T14:00:00Z"));
        inputDTO.setCheckOutDate(Instant.parse("2025-08-05T11:00:00Z"));

        ReservationDTO savedDTO = new ReservationDTO();
        savedDTO.setId(2L);
        savedDTO.setClientId(2L);
        savedDTO.setAccommodationId(2L);
        savedDTO.setCheckInDate(Instant.parse("2025-08-01T14:00:00Z"));
        savedDTO.setCheckOutDate(Instant.parse("2025-08-05T11:00:00Z"));

        when(reservationService.insert(any(ReservationDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/reservation/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.clientId").value(2L))
                .andExpect(jsonPath("$.accommodationId").value(2L));

        verify(reservationService).insert(any(ReservationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdatedReservation() throws Exception {
        ReservationDTO updatedDTO = new ReservationDTO();
        updatedDTO.setId(1L);
        updatedDTO.setClientId(1L);
        updatedDTO.setAccommodationId(2L);
        updatedDTO.setCheckInDate(Instant.parse("2025-09-01T14:00:00Z"));
        updatedDTO.setCheckOutDate(Instant.parse("2025-09-10T11:00:00Z"));

        when(reservationService.update(eq(1L), any(ReservationDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/reservation/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accommodationId").value(2L));

        verify(reservationService).update(eq(1L), any(ReservationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(reservationService).delete(1L);

        mockMvc.perform(delete("/reservation/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reservationService).delete(1L);
    }

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(ResourceNotFound.class)
        public ResponseEntity<String> handleNotFound(ResourceNotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}