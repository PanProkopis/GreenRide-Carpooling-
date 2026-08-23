package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.entity.Rating;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.BookingRepository;
import gr.hua.dit.greenride.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private User driver;

    @Mock
    private User passenger;

    @Mock
    private User unrelatedUser;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {

        ratingService =
                new RatingService(
                        ratingRepository,
                        bookingRepository
                );
    }

    @Test
    void passengerShouldBeAbleToRateDriver() {

        when(driver.getId())
                .thenReturn(1L);

        when(passenger.getId())
                .thenReturn(2L);

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .minusHours(1),
                        2,
                        driver
                );

        when(
                bookingRepository
                        .existsByPassengerIdAndRideId(
                                2L,
                                null
                        )
        ).thenReturn(true);

        when(
                ratingRepository.save(
                        any(Rating.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Rating rating =
                ratingService.createRating(
                        5,
                        "Great driver",
                        passenger,
                        driver,
                        ride
                );

        assertEquals(
                5,
                rating.getScore()
        );

        assertEquals(
                passenger,
                rating.getFromUser()
        );

        assertEquals(
                driver,
                rating.getToUser()
        );

        verify(ratingRepository)
                .save(
                        any(Rating.class)
                );
    }

    @Test
    void unrelatedUserShouldNotBeAbleToRateDriver() {

        when(driver.getId())
                .thenReturn(1L);

        when(unrelatedUser.getId())
                .thenReturn(3L);

        Ride ride =
                new Ride(
                        "Athens",
                        "Piraeus",
                        LocalDateTime.now()
                                .minusHours(1),
                        2,
                        driver
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                ratingService.createRating(
                                        5,
                                        "Fake rating",
                                        unrelatedUser,
                                        driver,
                                        ride
                                )
                );

        assertEquals(
                "Users must have participated in this ride as driver and passenger",
                exception.getMessage()
        );

        verify(
                ratingRepository,
                never()
        ).save(any());
    }
}