package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.Objects;

public class ClientDTO extends RepresentationModel<ClientDTO> {
    @Schema(description = "ID do cliente gerado pelo banco de dados")
    private long id;

    @Schema(description = "Nome do cliente")
    @Size(min = 3, max = 255, message = "Deve ter entre 3 e 255 caracteres.")
    private String name;

    @Schema(description = "E-mail do cliente de acesso ao sistema")
    @Email(message = "Favor informar um e-mail válido")
    private String email;

    @Schema(description = "Senha do cliente de acesso ao sistema")
    @NotBlank(message = "A senha é obrigatória")
    private String password;

    @Schema(description = "Número de celular do cliente")
    private String phone;

    public ClientDTO() {}

    public ClientDTO(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.email = client.getEmail();
        this.password = client.getPassword();
        this.phone = client.getPhone();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClientDTO client)) return false;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password=" + password +
                ", phone=" + phone +
                '}';
    }
}