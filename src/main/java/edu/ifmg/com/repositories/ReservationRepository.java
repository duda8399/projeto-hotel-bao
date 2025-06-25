package edu.ifmg.com.repositories;

import edu.ifmg.com.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reservation r
        WHERE r.accommodation.id = :accommodationId
          AND r.checkInDate < :checkOutDate
          AND r.checkOutDate > :checkInDate
    """)
    boolean existsByAccommodationIdAndDateRange(
            @Param("accommodationId") Long accommodationId,
            @Param("checkInDate") Instant checkInDate,
            @Param("checkOutDate") Instant checkOutDate
    );

    List<Reservation> findByClientId(Long clientId);

    @Query("SELECT r FROM Reservation r WHERE r.client.id = :clientId ORDER BY r.accommodation.value DESC")
    List<Reservation> findTopByClientIdOrderByAccommodationValueDesc(@Param("clientId") Long clientId);

    @Query("SELECT r FROM Reservation r WHERE r.client.id = :clientId ORDER BY r.accommodation.value ASC")
    List<Reservation> findTopByClientIdOrderByAccommodationValueAsc(@Param("clientId") Long clientId);
}
