package edu.ifmg.com.dto;

import edu.ifmg.com.entities.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ClientDTOTest {

    @Test
    void testEmptyConstructorAndSettersGetters() {
        ClientDTO dto = new ClientDTO();
        dto.setId(10L);
        dto.setName("Maria Silva");
        dto.setEmail("maria@example.com");
        dto.setPassword("pass123");
        dto.setPhone("31999999999");
        dto.setAddress("Rua A, 123");
        dto.setCity("Belo Horizonte");

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Maria Silva");
        assertThat(dto.getEmail()).isEqualTo("maria@example.com");
        assertThat(dto.getPassword()).isEqualTo("pass123");
        assertThat(dto.getPhone()).isEqualTo("31999999999");
        assertThat(dto.getAddress()).isEqualTo("Rua A, 123");
        assertThat(dto.getCity()).isEqualTo("Belo Horizonte");
    }

    @Test
    void testConstructorWithParams() {
        ClientDTO dto = new ClientDTO("João", "joao@example.com", "abc123", "31988887777", "Rua B, 45", "São Paulo");

        assertThat(dto.getName()).isEqualTo("João");
        assertThat(dto.getEmail()).isEqualTo("joao@example.com");
        assertThat(dto.getPassword()).isEqualTo("abc123");
        assertThat(dto.getPhone()).isEqualTo("31988887777");
        assertThat(dto.getAddress()).isEqualTo("Rua B, 45");
        assertThat(dto.getCity()).isEqualTo("São Paulo");
    }

    @Test
    void testConstructorFromClientEntity() {
        Client client = new Client();
        client.setName("Ana");
        client.setEmail("ana@example.com");
        client.setPassword("senha");
        client.setPhone("31977776666");
        client.setAddress("Av. C, 789");
        client.setCity("Rio de Janeiro");

        ClientDTO dto = new ClientDTO(client);

        assertThat(dto.getName()).isEqualTo(client.getName());
        assertThat(dto.getEmail()).isEqualTo(client.getEmail());
        assertThat(dto.getPassword()).isEqualTo(client.getPassword());
        assertThat(dto.getPhone()).isEqualTo(client.getPhone());
        assertThat(dto.getAddress()).isEqualTo(client.getAddress());
        assertThat(dto.getCity()).isEqualTo(client.getCity());
    }

    @Test
    void testEqualsAndHashCode() {
        ClientDTO dto1 = new ClientDTO();
        dto1.setId(1L);

        ClientDTO dto2 = new ClientDTO();
        dto2.setId(1L);

        ClientDTO dto3 = new ClientDTO();
        dto3.setId(2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());

        assertThat(dto1).isNotEqualTo(null);
        assertThat(dto1).isNotEqualTo("some string");
    }

    @Test
    void testToStringContainsAllFields() {
        ClientDTO dto = new ClientDTO("Lucas", "lucas@example.com", "pass456", "31966665555", "Rua D, 101", "Curitiba");
        dto.setId(5L);

        String toString = dto.toString();

        assertThat(toString).contains("id=5");
        assertThat(toString).contains("Lucas");
        assertThat(toString).contains("lucas@example.com");
        assertThat(toString).contains("pass456");
        assertThat(toString).contains("31966665555");
        assertThat(toString).contains("Rua D, 101");
        assertThat(toString).contains("Curitiba");
    }
}

