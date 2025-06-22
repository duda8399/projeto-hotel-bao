package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Reservation;
import edu.ifmg.com.entities.Accommodation;
import edu.ifmg.com.entities.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

public class ReservationDTO extends RepresentationModel<ReservationDTO> {

    @Schema(description = "ID da reserva gerado pelo banco de dados")
    private long id;

    @Schema(description = "Cliente associado à reserva")
    @NotNull(message = "O cliente é obrigatório")
    private Client client;

    @Schema(description = "Acomodação associada à reserva")
    @NotNull(message = "A acomodação é obrigatória")
    private Accommodation accommodation;

    @Schema(description = "Data de check-in do cliente")
    @NotNull(message = "A data de check-in é obrigatória")
    private Instant checkInDate;

    @Schema(description = "Data de check-out do cliente")
    @NotNull(message = "A data de check-out é obrigatória")
    private Instant checkOutDate;

    public ReservationDTO() {}

    public ReservationDTO(Client client, Accommodation bedroom, Instant checkInDate, Instant checkOutDate) {
        this.client = client;
        this.accommodation = bedroom;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public ReservationDTO(Reservation accommodation) {
        this.id = accommodation.getId();
        this.client = accommodation.getClient();
        this.accommodation = accommodation.getBedroom();
        this.checkInDate = accommodation.getCheckInDate();
        this.checkOutDate = accommodation.getCheckOutDate();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Accommodation getBedroom() {
        return accommodation;
    }

    public void setBedroom(Accommodation bedroom) {
        this.accommodation = bedroom;
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
                ", client=" + client +
                ", accommodation=" + accommodation +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
