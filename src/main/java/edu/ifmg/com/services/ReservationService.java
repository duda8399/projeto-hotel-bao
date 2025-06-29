package edu.ifmg.com.services;

import ch.qos.logback.core.util.StringUtil;
import edu.ifmg.com.dto.ReservationDTO;
import edu.ifmg.com.entities.Accommodation;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.entities.Reservation;
import edu.ifmg.com.repositories.AccommodationRepository;
import edu.ifmg.com.repositories.ClientRepository;
import edu.ifmg.com.repositories.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "Obtenha todas as reservas",
            summary = "Listar todas as reservas cadastradas",
            responses = {
                    @ApiResponse(description = "ok", responseCode = "200"),
            }
    )
    public Page<ReservationDTO> findAll(Pageable pageable) {
        Page<Reservation> page = reservationRepository.findAll(pageable);
        return page.map(ReservationDTO::new);
    }

    @Transactional(readOnly = true)
    public ReservationDTO findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
        return new ReservationDTO(reservation);
    }

    @Transactional
    public ReservationDTO insert(ReservationDTO dto) {
        boolean exists = reservationRepository.existsByAccommodationIdAndDateRange(
                dto.getAccommodationId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma reserva nesse período");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acomodação não encontrada"));

        Reservation reservation = new Reservation(
                client,
                accommodation,
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );

        reservation = reservationRepository.save(reservation);
        return new ReservationDTO(reservation);
    }

    @Transactional
    public ReservationDTO update(Long id, ReservationDTO dto) {
        try {
            boolean exists = reservationRepository.existsByAccommodationIdAndDateRange(
                    dto.getAccommodationId(),
                    dto.getCheckInDate(),
                    dto.getCheckOutDate()
            );

            if (exists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma reserva nesse período");
            }

            Reservation reservation = reservationRepository.getReferenceById(id);

            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

            Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Acomodação não encontrada"));

            reservation.setClient(client);
            reservation.setAccommodation(accommodation);
            reservation.setCheckInDate(dto.getCheckInDate());
            reservation.setCheckOutDate(dto.getCheckOutDate());

            return new ReservationDTO(reservationRepository.save(reservation));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada");
        }
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada");
        }
        reservationRepository.deleteById(id);
    }

    public List<String> reservationList() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ZoneId zoneId = ZoneId.systemDefault();

        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .map(c -> {
                    LocalDateTime checkInDate = LocalDateTime.ofInstant(c.getCheckInDate(), zoneId);
                    return String.format("Estadia: - Código: %d  - Cliente: %s - Quarto: %s - Data: %s",
                            c.getId(),
                            c.getClient().getName(),
                            c.getAccommodation().getDescription(),
                            checkInDate.format(formatter));
                })
                .toList();
    }

    public String generateInvoice(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (isNullOrEmpty(client.getName()) ||
                isNullOrEmpty(client.getAddress()) ||
                isNullOrEmpty(client.getCity())) {
            throw new IllegalArgumentException("Todos os dados do cliente devem ser preenchidos.");
        }

        List<Reservation> reservations = reservationRepository.findByClientId(clientId);

        List<Reservation> validReservations = reservations.stream()
                .filter(r -> r.getAccommodation() != null &&
                        r.getAccommodation().getDescription() != null &&
                        !r.getAccommodation().getDescription().isBlank() &&
                        r.getAccommodation().getValue() != 0)
                .toList();

        if (validReservations.isEmpty()) {
            throw new IllegalArgumentException("Deve haver pelo menos uma estadia com descrição e valor informados.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===============================\n");
        sb.append("         NOTA FISCAL\n");
        sb.append("===============================\n");
        sb.append("Nome: ").append(client.getName()).append("\n");
        sb.append("Endereço: ").append(client.getAddress()).append("\n");
        sb.append("Cidade: ").append(client.getCity()).append("\n");
        sb.append("===============================\n");
        sb.append("        ==== ESTADIAS ====\n");

        double total = 0.0;
        for (Reservation reservation : validReservations) {
            sb.append("Quarto: ").append(reservation.getAccommodation().getDescription());
            sb.append("    Valor: ").append(String.format("%.2f", reservation.getAccommodation().getValue())).append("\n");
            total += reservation.getAccommodation().getValue();
        }

        sb.append("===============================\n");
        sb.append("Total: R$ ").append(String.format("%.2f", total)).append("\n");
        sb.append("===============================");

        return sb.toString();
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    public Optional<Reservation> getReservationWithHighestAccommodationValue(Long clientId) {
        List<Reservation> reservations = reservationRepository.findTopByClientIdOrderByAccommodationValueDesc(clientId);
        return reservations.isEmpty() ? Optional.empty() : Optional.of(reservations.get(0));
    }

    public Optional<Reservation> getReservationWithLowerAccommodationValue(Long clientId) {
        List<Reservation> reservations = reservationRepository.findTopByClientIdOrderByAccommodationValueAsc(clientId);
        return reservations.isEmpty() ? Optional.empty() : Optional.of(reservations.get(0));
    }

    public Double getTotalReservationValueByClient(Long clientId) {
        List<Reservation> reservations = reservationRepository.findByClientId(clientId);

        return reservations.stream()
                .mapToDouble(r -> r.getAccommodation().getValue())
                .sum();
    }

}
