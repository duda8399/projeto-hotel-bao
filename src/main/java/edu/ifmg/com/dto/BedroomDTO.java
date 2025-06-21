package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Bedroom;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.Objects;

public class BedroomDTO extends RepresentationModel<BedroomDTO> {
    @Schema(description = "ID do quarto gerado pelo banco de dados")
    private long id;

    @Schema(description = "Descrição do quarto")
    private String description;

    @Schema(description = "Valor do quarto")
    @NotBlank(message = "O valor é obrigatório")
    private double value;

    @Schema(description = "Imagem do quarto")
    private String imageUrl;

    public BedroomDTO() {}

    public BedroomDTO(long id, String description, double value, String imageUrl) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.imageUrl = imageUrl;
    }

    public BedroomDTO(Bedroom bedroom) {
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
        if (!(o instanceof BedroomDTO bedroom)) return false;
        return id == bedroom.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BedroomDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", imageUrl=" + imageUrl +
                '}';
    }
}