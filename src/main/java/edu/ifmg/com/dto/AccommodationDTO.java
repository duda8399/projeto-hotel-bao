package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Accommodation;
import edu.ifmg.com.entities.Bedroom;
import edu.ifmg.com.entities.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.Objects;

public class AccommodationDTO extends RepresentationModel<AccommodationDTO> {

    @Schema(description = "ID da acomodação gerado pelo banco de dados")
    private long id;

    @Schema(description = "Cliente associado à acomodação")
    @NotNull(message = "O cliente é obrigatório")
    private Client client;

    @Schema(description = "Quarto associado à acomodação")
    @NotNull(message = "O quarto é obrigatório")
    private Bedroom bedroom;

    @Schema(description = "Data de check-in do cliente")
    @NotNull(message = "A data de check-in é obrigatória")
    private Instant checkInDate;

    @Schema(description = "Data de check-out do cliente")
    @NotNull(message = "A data de check-out é obrigatória")
    private Instant checkOutDate;

    public AccommodationDTO() {}

    public AccommodationDTO(Client client, Bedroom bedroom, Instant checkInDate, Instant checkOutDate) {
        this.client = client;
        this.bedroom = bedroom;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public AccommodationDTO(Accommodation accommodation) {
        this.id = accommodation.getId();
        this.client = accommodation.getClient();
        this.bedroom = accommodation.getBedroom();
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

    public Bedroom getBedroom() {
        return bedroom;
    }

    public void setBedroom(Bedroom bedroom) {
        this.bedroom = bedroom;
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
        if (!(o instanceof AccommodationDTO other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AccommodationDTO{" +
                "id=" + id +
                ", client=" + client +
                ", bedroom=" + bedroom +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
