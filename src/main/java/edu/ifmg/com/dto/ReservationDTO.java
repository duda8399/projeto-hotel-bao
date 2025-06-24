package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

public class ReservationDTO extends RepresentationModel<ReservationDTO> {

    @Schema(description = "ID da reserva gerado pelo banco de dados")
    private long id;

    @Schema(description = "ID do cliente associado à reserva")
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clientId;

    @Schema(description = "ID da acomodação associada à reserva")
    @NotNull(message = "O ID da acomodação é obrigatório")
    private Long accommodationId;

    @Schema(description = "Data de check-in da estadia")
    @NotNull(message = "A data de check-in é obrigatória")
    private Instant checkInDate;

    @Schema(description = "Data de check-out da estadia")
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
