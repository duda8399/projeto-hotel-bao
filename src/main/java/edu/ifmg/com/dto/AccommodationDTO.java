package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Accommodation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Schema(description = "DTO que representa uma acomodação (quarto) disponível para reserva.")
public class AccommodationDTO extends RepresentationModel<AccommodationDTO> {

    @Schema(description = "ID do quarto gerado pelo banco de dados", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Descrição do quarto", example = "Suíte com vista para o mar e ar-condicionado")
    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @Schema(description = "Valor da diária da acomodação (em reais)", example = "299.90", minimum = "0.0")
    @Positive(message = "O valor deve ser maior que zero")
    private double value;

    @Schema(description = "URL da imagem do quarto", example = "https://example.com/images/quarto1.jpg")
    private String imageUrl;

    public AccommodationDTO() {}

    public AccommodationDTO(long id, String description, double value, String imageUrl) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.imageUrl = imageUrl;
    }

    public AccommodationDTO(Accommodation bedroom) {
        this.id = bedroom.getId();
        this.description = bedroom.getDescription();
        this.value = bedroom.getValue();
        this.imageUrl = bedroom.getImageUrl();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccommodationDTO bedroom)) return false;
        return id == bedroom.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AccommodationDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", value=" + value +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}