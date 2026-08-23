package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Booking;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.DuplicateBookingException;
import gr.hua.dit.greenride.exception.ForbiddenOperationException;
import gr.hua.dit.greenride.exception.NoAvailableSeatsException;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.repository.BookingRepository;
import gr.hua.dit.greenride.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RideRepository rideRepository) {

        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
    }

    @Transactional
    public Booking createBooking(
            User passenger,
            Long rideId) {

        Ride ride = rideRepository
                .findByIdForUpdate(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride not found"
                        ));

        LocalDateTime now =
                LocalDateTime.now();

        if (!ride.getDepartureTime().isAfter(now)) {

            throw new IllegalArgumentException(
                    "Cannot book a ride that has already departed"
            );
        }

        if (ride.getAvailableSeats() <= 0) {

            throw new NoAvailableSeatsException(
                    "No available seats"
            );
        }

        if (ride.getDriver()
                .getId()
                .equals(passenger.getId())) {

            throw new IllegalArgumentException(
                    "Driver cannot book a seat in their own ride"
            );
        }

        if (bookingRepository
                .existsByPassengerIdAndRideId(
                        passenger.getId(),
                        rideId)) {

            throw new DuplicateBookingException(
                    "Passenger already has a booking for this ride"
            );
        }

        Booking booking =
                new Booking(
                        now,
                        passenger,
                        ride
                );

        ride.setAvailableSeats(
                ride.getAvailableSeats() - 1
        );

        rideRepository.save(ride);

        return bookingRepository.save(
                booking
        );
    }

    public List<Booking> getBookingsForUser(
            User user) {

        return bookingRepository
                .findByPassengerId(
                        user.getId()
                );
    }

    public List<Booking> getBookingsForRide(
            Long rideId) {

        return bookingRepository
                .findByRideId(
                        rideId
                );
    }

    public Booking getBookingByIdForUser(
            Long bookingId,
            User user) {

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"
                                ));

        if (!booking.getPassenger()
                .getId()
                .equals(user.getId())) {

            throw new ForbiddenOperationException(
                    "You cannot view another user's booking"
            );
        }

        return booking;
    }

    @Transactional
    public void cancelBooking(
            Long bookingId,
            User authenticatedUser) {

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found"
                                ));

        if (!booking.getPassenger()
                .getId()
                .equals(authenticatedUser.getId())) {

            throw new ForbiddenOperationException(
                    "You can only cancel your own booking"
            );
        }

        Ride ride = booking.getRide();

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime cancellationLimit =
                ride.getDepartureTime()
                        .minusMinutes(10);

        if (!now.isBefore(cancellationLimit)) {

            throw new IllegalArgumentException(
                    "Booking cannot be cancelled 10 minutes or less before departure"
            );
        }

        ride.setAvailableSeats(
                ride.getAvailableSeats() + 1
        );

        rideRepository.save(ride);

        bookingRepository.delete(
                booking
        );
    }
}