package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.dto.AdminStatsResponse;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.Role;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.repository.BookingRepository;
import gr.hua.dit.greenride.repository.RideRepository;
import gr.hua.dit.greenride.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    public AdminService(
            UserRepository userRepository,
            RideRepository rideRepository,
            BookingRepository bookingRepository) {

        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
    }

    public AdminStatsResponse getStatistics() {

        long totalUsers =
                userRepository.count();

        long totalRides =
                rideRepository.count();

        long totalBookings =
                bookingRepository.count();

        List<Ride> rides =
                rideRepository.findAll();

        double totalOccupancyPercentage = 0.0;
        int ridesWithCapacity = 0;

        for (Ride ride : rides) {

            long bookedSeats =
                    bookingRepository
                            .countByRideId(
                                    ride.getId()
                            );

            long totalCapacity =
                    bookedSeats
                            + ride.getAvailableSeats();

            if (totalCapacity > 0) {

                double occupancy =
                        ((double) bookedSeats
                                / totalCapacity)
                                * 100.0;

                totalOccupancyPercentage +=
                        occupancy;

                ridesWithCapacity++;
            }
        }

        double averageOccupancy = 0.0;

        if (ridesWithCapacity > 0) {

            averageOccupancy =
                    totalOccupancyPercentage
                            / ridesWithCapacity;
        }

        return new AdminStatsResponse(
                totalUsers,
                totalRides,
                totalBookings,
                averageOccupancy
        );
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User blockUser(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        if (user.getRole() == Role.ADMIN) {

            throw new IllegalArgumentException(
                    "Admin accounts cannot be blocked"
            );
        }

        user.setBlocked(true);

        return userRepository.save(user);
    }

    public User unblockUser(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        user.setBlocked(false);

        return userRepository.save(user);
    }
}