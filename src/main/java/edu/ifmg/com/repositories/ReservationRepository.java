package edu.ifmg.com.repositories;

import edu.ifmg.com.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

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
}
