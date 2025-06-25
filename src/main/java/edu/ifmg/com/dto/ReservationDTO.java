package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

@Schema(description = "DTO que representa uma reserva feita por um cliente para uma acomodação.")
public class ReservationDTO extends RepresentationModel<ReservationDTO> {

    @Schema(description = "ID da reserva gerado pelo banco de dados", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "ID do cliente associado à reserva", example = "1", required = true)
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clientId;

    @Schema(description = "ID da acomodação associada à reserva", example = "10", required = true)
    @NotNull(message = "O ID da acomodação é obrigatório")
    private Long accommodationId;

    @Schema(description = "Data de check-in da estadia no formato ISO-8601", example = "2025-07-20T14:00:00Z", required = true)
    @NotNull(message = "A data de check-in é obrigatória")
    private Instant checkInDate;

    @Schema(description = "Data de check-out da estadia no formato ISO-8601", example = "2025-07-25T11:00:00Z")
    private Instant checkOutDate;

    public ReservationDTO() {}

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.clientId = reservation.getClient().getId();
        this.accommodationId = reservation.getAccommodation().getId();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }

    public Instant getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Instant checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Instant getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Instant checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReservationDTO other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ReservationDTO {" +
                "id=" + id +
                ", clientId=" + clientId +
                ", accommodationId=" + accommodationId +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}