package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.RegisterRequest;
import gr.hua.dit.greenride.dto.RideRequest;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.service.AuthService;
import gr.hua.dit.greenride.service.BookingService;
import gr.hua.dit.greenride.service.RideService;
import gr.hua.dit.greenride.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final AuthService authService;
    private final UserService userService;
    private final RideService rideService;
    private final BookingService bookingService;

    public WebController(
            AuthService authService,
            UserService userService,
            RideService rideService,
            BookingService bookingService) {

        this.authService = authService;
        this.userService = userService;
        this.rideService = rideService;
        this.bookingService = bookingService;
    }

    // ---------------- LOGIN ----------------

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    // ---------------- REGISTER ----------------

    @GetMapping("/register")
    public String registerPage(
            Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid
            @ModelAttribute
            RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            return "register";
        }

        try {

            authService.register(
                    registerRequest
            );

            redirectAttributes
                    .addFlashAttribute(
                            "success",
                            "Registration completed. You can now login."
                    );

            return "redirect:/login";

        } catch (RuntimeException ex) {

            bindingResult.reject(
                    "registerError",
                    ex.getMessage()
            );

            return "register";
        }
    }

    // ---------------- HOME ----------------

    @GetMapping("/")
    public String home(
            Authentication authentication,
            Model model) {

        User user =
                getAuthenticatedUser(
                        authentication
                );

        model.addAttribute(
                "user",
                user
        );

        return "dashboard";
    }

    // ---------------- RIDES ----------------

    @GetMapping("/rides")
    public String availableRides(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String origin,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String destination,

            Model model) {

        String cleanOrigin =
                origin.trim();

        String cleanDestination =
                destination.trim();

        boolean searching =
                !cleanOrigin.isBlank()
                        || !cleanDestination.isBlank();

        if (searching) {

            model.addAttribute(
                    "rides",
                    rideService.searchAvailableRides(
                            cleanOrigin,
                            cleanDestination
                    )
            );

        } else {

            model.addAttribute(
                    "rides",
                    rideService.getAvailableRides()
            );
        }

        model.addAttribute(
                "origin",
                cleanOrigin
        );

        model.addAttribute(
                "destination",
                cleanDestination
        );

        model.addAttribute(
                "searching",
                searching
        );

        return "rides";
    }

    @GetMapping("/rides/create")
    public String createRidePage(
            Model model) {

        model.addAttribute(
                "rideRequest",
                new RideRequest()
        );

        return "create-ride";
    }

    @PostMapping("/rides/create")
    public String createRide(
            @Valid
            @ModelAttribute
            RideRequest rideRequest,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            return "create-ride";
        }

        User driver =
                getAuthenticatedUser(
                        authentication
                );

        Ride ride =
                new Ride(
                        rideRequest.getOrigin(),
                        rideRequest.getDestination(),
                        rideRequest.getDepartureTime(),
                        rideRequest.getAvailableSeats(),
                        driver
                );

        try {

            rideService.createRide(
                    ride
            );

            redirectAttributes
                    .addFlashAttribute(
                            "success",
                            "Ride created successfully."
                    );

            return "redirect:/my-rides";

        } catch (RuntimeException ex) {

            bindingResult.reject(
                    "rideError",
                    ex.getMessage()
            );

            return "create-ride";
        }
    }

    @GetMapping("/my-rides")
    public String myRides(
            Authentication authentication,
            Model model) {

        User user =
                getAuthenticatedUser(
                        authentication
                );

        model.addAttribute(
                "rides",
                rideService
                        .getRidesCreatedByUser(
                                user
                        )
        );

        return "my-rides";
    }

    // ---------------- BOOKINGS ----------------

    @PostMapping("/rides/{id}/book")
    public String bookRide(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user =
                getAuthenticatedUser(
                        authentication
                );

        try {

            bookingService.createBooking(
                    user,
                    id
            );

            redirectAttributes
                    .addFlashAttribute(
                            "success",
                            "Booking completed successfully."
                    );

        } catch (RuntimeException ex) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            ex.getMessage()
                    );
        }

        return "redirect:/rides";
    }

    @GetMapping("/bookings")
    public String myBookings(
            Authentication authentication,
            Model model) {

        User user =
                getAuthenticatedUser(
                        authentication
                );

        model.addAttribute(
                "bookings",
                bookingService
                        .getBookingsForUser(
                                user
                        )
        );

        return "bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user =
                getAuthenticatedUser(
                        authentication
                );

        try {

            bookingService.cancelBooking(
                    id,
                    user
            );

            redirectAttributes
                    .addFlashAttribute(
                            "success",
                            "Booking cancelled successfully."
                    );

        } catch (RuntimeException ex) {

            redirectAttributes
                    .addFlashAttribute(
                            "error",
                            ex.getMessage()
                    );
        }

        return "redirect:/bookings";
    }

    // ---------------- HELPER ----------------

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }
}