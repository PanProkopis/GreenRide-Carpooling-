package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.entity.Booking;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.service.BookingService;
import gr.hua.dit.greenride.service.RatingService;
import gr.hua.dit.greenride.service.RideService;
import gr.hua.dit.greenride.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ratings")
public class RatingWebController {

    private final UserService userService;
    private final RideService rideService;
    private final BookingService bookingService;
    private final RatingService ratingService;

    public RatingWebController(
            UserService userService,
            RideService rideService,
            BookingService bookingService,
            RatingService ratingService) {

        this.userService = userService;
        this.rideService = rideService;
        this.bookingService = bookingService;
        this.ratingService = ratingService;
    }

    @GetMapping
    public String ratings(
            Authentication authentication,
            Model model) {

        User user = getAuthenticatedUser(authentication);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> passengerBookings =
                bookingService
                        .getBookingsForUser(user)
                        .stream()
                        .filter(booking ->
                                booking.getRide()
                                        .getDepartureTime()
                                        .isBefore(now))
                        .toList();

        List<Ride> driverRides =
                rideService
                        .getRidesCreatedByUser(user)
                        .stream()
                        .filter(ride ->
                                ride.getDepartureTime()
                                        .isBefore(now))
                        .toList();

        Map<Long, List<Booking>> passengersByRide =
                new HashMap<>();

        for (Ride ride : driverRides) {

            passengersByRide.put(
                    ride.getId(),
                    bookingService.getBookingsForRide(
                            ride.getId()
                    )
            );
        }

        model.addAttribute(
                "passengerBookings",
                passengerBookings
        );

        model.addAttribute(
                "driverRides",
                driverRides
        );

        model.addAttribute(
                "passengersByRide",
                passengersByRide
        );

        return "ratings";
    }

    @PostMapping("/create")
    public String createRating(
            @RequestParam int score,
            @RequestParam(required = false) String comment,
            @RequestParam Long toUserId,
            @RequestParam Long rideId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User fromUser =
                getAuthenticatedUser(authentication);

        try {

            User toUser = userService
                    .getUserById(toUserId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User to rate not found"
                            ));

            Ride ride = rideService
                    .getRideById(rideId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Ride not found"
                            ));

            ratingService.createRating(
                    score,
                    comment,
                    fromUser,
                    toUser,
                    ride
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Rating submitted successfully."
            );

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/ratings";
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        return userService
                .getUserByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }
}