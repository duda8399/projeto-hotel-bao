package edu.ifmg.com.services;

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
}
