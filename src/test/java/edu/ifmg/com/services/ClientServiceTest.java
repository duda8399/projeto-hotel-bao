package edu.ifmg.com.services;

import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.entities.Role;
import edu.ifmg.com.repositories.ClientRepository;
import edu.ifmg.com.services.exceptions.DatabaseException;
import edu.ifmg.com.services.exceptions.ResourceNotFound;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new Client();
        client.setId(1L);
        client.setName("Maria");
        client.setEmail("maria@example.com");
        client.setPassword("encodedPassword");
        client.setPhone("31999999999");
        client.setAddress("Rua X, 123");
        client.setCity("BH");
        client.setRole(Role.CLIENT);

        clientDTO = new ClientDTO();
        clientDTO.setName("Maria");
        clientDTO.setEmail("maria@example.com");
        clientDTO.setPassword("rawPassword");
        clientDTO.setPhone("31999999999");
        clientDTO.setAddress("Rua X, 123");
        clientDTO.setCity("BH");
    }

    @Test
    void findAll_ShouldReturnPageOfClientDTO() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Client> clients = List.of(client);
        Page<Client> page = new PageImpl<>(clients, pageable, clients.size());

        when(clientRepository.findAll(pageable)).thenReturn(page);

        Page<ClientDTO> result = clientService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(client.getName());

        verify(clientRepository).findAll(pageable);
    }

    @Test
    void findById_ShouldReturnClientDTO_WhenClientExists() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientDTO result = clientService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(client.getName());

        verify(clientRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(99L))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Cliente não encontrado");

        verify(clientRepository).findById(99L);
    }

    @Test
    void insert_ShouldSaveAndReturnClientDTO() {
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.insert(clientDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(client.getName());
        assertThat(result.getPassword()).isEqualTo(client.getPassword());

        verify(passwordEncoder).encode("rawPassword");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_ShouldReturnUpdatedClientDTO_WhenClientExists() {
        ClientDTO updatedDTO = new ClientDTO();
        updatedDTO.setName("Maria Atualizada");
        updatedDTO.setEmail("maria.atualizada@example.com");
        updatedDTO.setPassword("newRawPass");
        updatedDTO.setPhone("31911112222");
        updatedDTO.setAddress("Rua Atualizada, 456");
        updatedDTO.setCity("Contagem");

        when(clientRepository.getReferenceById(1L)).thenReturn(client);
        when(passwordEncoder.encode("newRawPass")).thenReturn("newEncodedPass");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientDTO result = clientService.update(1L, updatedDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Maria Atualizada");
        assertThat(result.getEmail()).isEqualTo("maria.atualizada@example.com");
        assertThat(result.getPassword()).isEqualTo("newEncodedPass");
        assertThat(result.getPhone()).isEqualTo("31911112222");
        assertThat(result.getAddress()).isEqualTo("Rua Atualizada, 456");
        assertThat(result.getCity()).isEqualTo("Contagem");

        verify(clientRepository).getReferenceById(1L);
        verify(passwordEncoder).encode("newRawPass");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void update_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        when(clientRepository.getReferenceById(99L)).thenThrow(EntityNotFoundException.class);

        ClientDTO dto = new ClientDTO();
        dto.setName("Teste");

        assertThatThrownBy(() -> clientService.update(99L, dto))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(clientRepository).getReferenceById(99L);
    }

    @Test
    void delete_ShouldCallDelete_WhenClientExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);

        clientService.delete(1L);

        verify(clientRepository).existsById(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        when(clientRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clientService.delete(99L))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(clientRepository).existsById(99L);
        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_ShouldThrowDatabaseException_WhenDataIntegrityViolation() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(clientRepository).deleteById(1L);

        assertThatThrownBy(() -> clientService.delete(1L))
                .isInstanceOf(DatabaseException.class)
                .hasMessage("Integridade violada");

        verify(clientRepository).existsById(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void customerList_ShouldReturnFormattedStrings() {
        Client client2 = new Client();
        client2.setId(2L);
        client2.setName(null);
        client2.setAddress(null);
        client2.setPhone(null);

        when(clientRepository.findAll()).thenReturn(List.of(client, client2));

        List<String> list = clientService.customerList();

        assertThat(list).hasSize(2);
        assertThat(list.get(0)).contains("Cliente - Código: 1");
        assertThat(list.get(0)).contains("Maria");
        assertThat(list.get(0)).contains("Rua X, 123");
        assertThat(list.get(0)).contains("31999999999");

        assertThat(list.get(1)).contains("Cliente - Código: 2");
        assertThat(list.get(1)).contains("N/A"); // null replaced by N/A
    }
}
