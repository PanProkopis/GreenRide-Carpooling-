package gr.hua.dit.greenride.repository;

import gr.hua.dit.greenride.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}