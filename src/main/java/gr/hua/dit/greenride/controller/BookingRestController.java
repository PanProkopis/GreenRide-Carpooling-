package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.BookingRequest;
import gr.hua.dit.greenride.dto.BookingResponse;
import gr.hua.dit.greenride.entity.Booking;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.service.BookingService;
import gr.hua.dit.greenride.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingRestController(
            BookingService bookingService,
            UserService userService) {

        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User passenger = getAuthenticatedUser(jwt);

        Booking booking =
                bookingService.createBooking(
                        passenger,
                        request.getRideId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(booking));
    }

    @GetMapping
    public List<BookingResponse> getMyBookings(
            @AuthenticationPrincipal Jwt jwt) {

        User user = getAuthenticatedUser(jwt);

        return bookingService
                .getBookingsForUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User user = getAuthenticatedUser(jwt);

        Booking booking =
                bookingService.getBookingByIdForUser(
                        id,
                        user
                );

        return ResponseEntity.ok(
                toResponse(booking)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        User user = getAuthenticatedUser(jwt);

        bookingService.cancelBooking(
                id,
                user
        );

        return ResponseEntity.noContent().build();
    }

    private User getAuthenticatedUser(Jwt jwt) {

        String email = jwt.getSubject();

        return userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }

    private BookingResponse toResponse(
            Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getBookingTime(),
                booking.getPassenger().getId(),
                booking.getPassenger().getName(),
                booking.getRide().getId()
        );
    }
}