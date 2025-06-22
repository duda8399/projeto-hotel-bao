package edu.ifmg.com.repositories;

import edu.ifmg.com.entities.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}
