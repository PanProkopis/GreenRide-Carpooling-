package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    private static final int MAX_ACTIVE_RIDES_PER_DRIVER = 3;

    private final RideRepository rideRepository;

    public RideService(
            RideRepository rideRepository) {

        this.rideRepository = rideRepository;
    }

    public Ride createRide(Ride ride) {

        if (ride.getOrigin() == null
                || ride.getOrigin().isBlank()) {

            throw new IllegalArgumentException(
                    "Origin cannot be empty"
            );
        }

        if (ride.getDestination() == null
                || ride.getDestination().isBlank()) {

            throw new IllegalArgumentException(
                    "Destination cannot be empty"
            );
        }

        if (ride.getDepartureTime() == null
                || !ride.getDepartureTime()
                .isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Departure time must be in the future"
            );
        }

        if (ride.getAvailableSeats() <= 0) {

            throw new IllegalArgumentException(
                    "Available seats must be greater than 0"
            );
        }

        if (ride.getDriver() == null) {

            throw new IllegalArgumentException(
                    "Ride must have a driver"
            );
        }

        long activeRides =
                rideRepository
                        .countByDriverIdAndDepartureTimeAfter(
                                ride.getDriver().getId(),
                                LocalDateTime.now()
                        );

        if (activeRides >= MAX_ACTIVE_RIDES_PER_DRIVER) {

            throw new IllegalArgumentException(
                    "Driver cannot have more than "
                            + MAX_ACTIVE_RIDES_PER_DRIVER
                            + " active rides"
            );
        }

        return rideRepository.save(ride);
    }

    public List<Ride> getAllRides() {

        return rideRepository.findAll();
    }

    public Optional<Ride> getRideById(
            Long id) {

        return rideRepository.findById(id);
    }

    public List<Ride> getAvailableRides() {

        return rideRepository
                .findByDepartureTimeAfterAndAvailableSeatsGreaterThanOrderByDepartureTimeAsc(
                        LocalDateTime.now(),
                        0
                );
    }

    public List<Ride> searchAvailableRides(
            String origin,
            String destination) {

        String safeOrigin =
                origin == null
                        ? ""
                        : origin.trim();

        String safeDestination =
                destination == null
                        ? ""
                        : destination.trim();

        return rideRepository
                .findByOriginContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndDepartureTimeAfterAndAvailableSeatsGreaterThanOrderByDepartureTimeAsc(
                        safeOrigin,
                        safeDestination,
                        LocalDateTime.now(),
                        0
                );
    }

    public List<Ride> getRidesCreatedByUser(
            User user) {

        return rideRepository
                .findByDriverIdOrderByDepartureTimeDesc(
                        user.getId()
                );
    }

    public void deleteRide(Long id) {

        rideRepository.deleteById(id);
    }
}