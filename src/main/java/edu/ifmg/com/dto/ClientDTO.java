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
    @NotBlank(message = "O nome do cliente é obrigatório")
    private String name;

    @Schema(description = "E-mail do cliente de acesso ao sistema")
    @Email(message = "Favor informar um e-mail válido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @Schema(description = "Senha do cliente de acesso ao sistema")
    @Size(min = 6, max = 10, message = "Deve ter entre 6 e 10 caracteres.")
    private String password;

    @Schema(description = "Número de celular do cliente")
    private String phone;

    @Schema(description = "Endereço do cliente")
    private String address;

    @Schema(description = "Cidade do cliente")
    private String city;

    public ClientDTO() {}

    public ClientDTO(String name, String email, String password, String phone, String address, String city) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.city = city;
    }

    public ClientDTO(Client client) {
        this.name = client.getName();
        this.email = client.getEmail();
        this.password = client.getPassword();
        this.phone = client.getPhone();
        this.address = client.getAddress();
        this.city = client.getCity();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
                ", address=" + address +
                ", city=" + city +
                '}';
    }
}