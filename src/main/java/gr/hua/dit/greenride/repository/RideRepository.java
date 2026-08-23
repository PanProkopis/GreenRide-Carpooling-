package gr.hua.dit.greenride.repository;

import gr.hua.dit.greenride.entity.Ride;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRepository
        extends JpaRepository<Ride, Long> {

    List<Ride>
    findByDepartureTimeAfterAndAvailableSeatsGreaterThanOrderByDepartureTimeAsc(
            LocalDateTime now,
            int minimumSeats
    );

    List<Ride>
    findByOriginContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeAfterAndAvailableSeatsGreaterThanOrderByDepartureTimeAsc(
            String origin,
            String destination,
            LocalDateTime now,
            int minimumSeats
    );

    List<Ride> findByDriverIdOrderByDepartureTimeDesc(
            Long driverId
    );

    long countByDriverIdAndDepartureTimeAfter(
            Long driverId,
            LocalDateTime now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM Ride r
            WHERE r.id = :id
            """)
    Optional<Ride> findByIdForUpdate(
            @Param("id") Long id
    );
}