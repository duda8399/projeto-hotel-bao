package edu.ifmg.com.services;
import edu.ifmg.com.dto.ClientDTO;
import edu.ifmg.com.entities.Client;
import edu.ifmg.com.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public ClientDTO insert(ClientDTO dto) {
        Client entity = new Client();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity = clientRepository.save(entity);
        return new ClientDTO(entity);
    }
}
