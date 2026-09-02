package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private User driver;

    private RideService rideService;

    @BeforeEach
    void setUp() {

        rideService =
                new RideService(
                        rideRepository
                );
    }

    @Test
    void createRideShouldSaveValidRide() {

        when(driver.getId())
                .thenReturn(1L);

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .plusDays(1),
                        3,
                        driver
                );

        when(
                rideRepository
                        .countByDriverIdAndDepartureTimeAfter(
                                eq(1L),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(0L);

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result =
                rideService.createRide(
                        ride
                );

        assertEquals(
                "Athens",
                result.getOrigin()
        );

        assertEquals(
                "Piraeus",
                result.getDestination()
        );

        assertEquals(
                3,
                result.getAvailableSeats()
        );

        verify(rideRepository)
                .save(ride);
    }

    @Test
    void createRideShouldRejectFourthActiveRide() {

        when(driver.getId())
                .thenReturn(1L);

        Ride ride =
                new Ride(
                        "Athens",
                        "University",
                        LocalDateTime.now()
                                .plusDays(1),
                        2,
                        driver
                );

        when(
                rideRepository
                        .countByDriverIdAndDepartureTimeAfter(
                                eq(1L),
                                any(LocalDateTime.class)
                        )
        ).thenReturn(3L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                rideService.createRide(
                                        ride
                                )
                );

        assertEquals(
                "Driver cannot have more than 3 active rides",
                exception.getMessage()
        );

        verify(
                rideRepository,
                never()
        ).save(any());
    }

    @Test
    void createRideShouldRejectPastDeparture() {

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .minusHours(1),
                        3,
                        driver
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        rideService.createRide(
                                ride
                        )
        );

        verify(
                rideRepository,
                never()
        ).save(any());
    }

    @Test
    void createRideShouldRejectEqualOriginAndDestinationIgnoringCaseAndSpaces() {

        Ride ride =
                new Ride(
                        " Athens ",
                        " aTHENS ",
                        LocalDateTime.now()
                                .plusDays(1),
                        3,
                        driver
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rideService.createRide(ride)
                );

        assertEquals(
                "Origin and destination must be different",
                exception.getMessage()
        );

        verify(rideRepository, never()).save(any());
    }

    @Test
    void createRideShouldRejectMoreThanEightAvailableSeats() {

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .plusDays(1),
                        9,
                        driver
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rideService.createRide(ride)
                );

        assertEquals(
                "Available seats must be between 1 and 8",
                exception.getMessage()
        );

        verify(rideRepository, never()).save(any());
    }

    @Test
    void createRideShouldTrimOriginAndDestinationBeforeSaving() {

        when(driver.getId()).thenReturn(1L);

        Ride ride =
                new Ride(
                        " Athens ",
                        " Piraeus ",
                        LocalDateTime.now()
                                .plusDays(1),
                        3,
                        driver
                );

        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.createRide(ride);

        assertEquals("Athens", result.getOrigin());
        assertEquals("Piraeus", result.getDestination());
        verify(rideRepository).save(ride);
    }
}
