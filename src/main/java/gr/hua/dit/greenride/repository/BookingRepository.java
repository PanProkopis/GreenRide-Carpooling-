package gr.hua.dit.greenride.repository;

import gr.hua.dit.greenride.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByPassengerIdAndRideId(
            Long passengerId,
            Long rideId
    );

    List<Booking> findByPassengerId(
            Long passengerId
    );

    List<Booking> findByRideId(
            Long rideId
    );

    long countByRideId(
            Long rideId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.id = :id
            """)
    Optional<Booking> findByIdForUpdate(
            @Param("id") Long id
    );
}
