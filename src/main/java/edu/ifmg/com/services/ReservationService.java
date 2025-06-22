package edu.ifmg.com.services;

import edu.ifmg.com.dto.ReservationDTO;
import edu.ifmg.com.entities.Reservation;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

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

        /*boolean exists = reservationRepository.existsByAccommodationIdAndDateRange(
                dto.getAccommodation().getId(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma reserva nesse período");
        }*/

        Reservation reservation = new Reservation(
                dto.getClient(),
                dto.getAccommodation(),
                dto.getCheckInDate(),
                dto.getCheckOutDate()
        );
        reservation = reservationRepository.save(reservation);
        return new ReservationDTO(reservation);
    }

    @Transactional
    public ReservationDTO update(Long id, ReservationDTO dto) {
        try {
            Reservation reservation = reservationRepository.getReferenceById(id);
            reservation.setClient(dto.getClient());
            reservation.setAccommodation(dto.getAccommodation());
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
