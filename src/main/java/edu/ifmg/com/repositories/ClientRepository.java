package edu.ifmg.com.repositories;

import edu.ifmg.com.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
