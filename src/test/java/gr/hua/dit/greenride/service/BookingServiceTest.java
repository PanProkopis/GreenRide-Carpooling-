package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Booking;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.BookingRepository;
import gr.hua.dit.greenride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private User driver;

    @Mock
    private User passenger;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {

        bookingService =
                new BookingService(
                        bookingRepository,
                        rideRepository
                );
    }

    @Test
    void createBookingShouldDecreaseAvailableSeats() {

        when(driver.getId())
                .thenReturn(1L);

        when(passenger.getId())
                .thenReturn(2L);

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .plusHours(2),
                        3,
                        driver
                );

        when(
                rideRepository
                        .findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(ride)
        );

        when(
                bookingRepository
                        .existsByPassengerIdAndRideId(
                                2L,
                                1L
                        )
        ).thenReturn(false);

        when(
                bookingRepository.save(
                        any(Booking.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Booking booking =
                bookingService.createBooking(
                        passenger,
                        1L
                );

        assertEquals(
                passenger,
                booking.getPassenger()
        );

        assertEquals(
                ride,
                booking.getRide()
        );

        assertEquals(
                2,
                ride.getAvailableSeats()
        );

        verify(rideRepository)
                .save(ride);

        verify(bookingRepository)
                .save(
                        any(Booking.class)
                );
    }

    @Test
    void createBookingShouldRejectPastRide() {

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .minusMinutes(5),
                        3,
                        driver
                );

        when(
                rideRepository
                        .findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(ride)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        bookingService.createBooking(
                                passenger,
                                1L
                        )
        );

        verify(
                bookingRepository,
                never()
        ).save(any());
    }

    @Test
    void cancelBookingShouldRejectCancellationInsideTenMinutes() {

        when(passenger.getId())
                .thenReturn(2L);

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .plusMinutes(5),
                        2,
                        driver
                );

        Booking booking =
                new Booking(
                        LocalDateTime.now()
                                .minusHours(1),
                        passenger,
                        ride
                );

        when(
                bookingRepository
                        .findByIdForUpdate(1L)
        ).thenReturn(
                Optional.of(booking)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                bookingService.cancelBooking(
                                        1L,
                                        passenger
                                )
                );

        assertEquals(
                "Booking cannot be cancelled 10 minutes or less before departure",
                exception.getMessage()
        );

        verify(
                bookingRepository,
                never()
        ).delete(any());
    }

    @Test
    void cancelBookingShouldUseLockingRepositoryMethod() {

        when(passenger.getId()).thenReturn(2L);
        Ride ride = futureRideWithAvailableSeats(2);
        Booking booking = bookingForPassengerAndRide(ride);
        when(bookingRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, passenger);

        verify(bookingRepository).findByIdForUpdate(1L);
        verify(bookingRepository, never()).findById(1L);
    }

    @Test
    void cancelBookingShouldReturnExactlyOneAvailableSeat() {

        when(passenger.getId()).thenReturn(2L);
        Ride ride = futureRideWithAvailableSeats(2);
        Booking booking = bookingForPassengerAndRide(ride);
        when(bookingRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, passenger);

        assertEquals(3, ride.getAvailableSeats());
        verify(rideRepository).save(ride);
    }

    @Test
    void cancelBookingShouldDeleteCancelledBooking() {

        when(passenger.getId()).thenReturn(2L);
        Ride ride = futureRideWithAvailableSeats(2);
        Booking booking = bookingForPassengerAndRide(ride);
        when(bookingRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, passenger);

        verify(bookingRepository).delete(booking);
    }

    private Ride futureRideWithAvailableSeats(
            int availableSeats) {

        return new Ride(
                "Athens",
                "Piraeus",
                LocalDateTime.now().plusHours(2),
                availableSeats,
                driver
        );
    }

    private Booking bookingForPassengerAndRide(
            Ride ride) {

        return new Booking(
                LocalDateTime.now().minusHours(1),
                passenger,
                ride
        );
    }
}
