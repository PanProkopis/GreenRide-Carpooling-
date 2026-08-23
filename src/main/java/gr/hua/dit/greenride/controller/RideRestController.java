package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.RideRequest;
import gr.hua.dit.greenride.dto.RideResponse;
import gr.hua.dit.greenride.dto.RideRouteInfoResponse;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.service.RideService;
import gr.hua.dit.greenride.service.RouteInfoService;
import gr.hua.dit.greenride.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideRestController {

    private final RideService rideService;
    private final UserService userService;
    private final RouteInfoService routeInfoService;

    public RideRestController(
            RideService rideService,
            UserService userService,
            RouteInfoService routeInfoService) {

        this.rideService = rideService;
        this.userService = userService;
        this.routeInfoService = routeInfoService;
    }

    @GetMapping
    public List<RideResponse> getAllRides() {

        return rideService.getAllRides()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/available")
    public List<RideResponse> getAvailableRides() {

        return rideService.getAvailableRides()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/search")
    public List<RideResponse> searchRides(
            @RequestParam String origin,
            @RequestParam String destination) {

        return rideService
                .searchAvailableRides(
                        origin,
                        destination
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/my")
    public List<RideResponse> getMyRides(
            @AuthenticationPrincipal Jwt jwt) {

        User user = getAuthenticatedUser(jwt);

        return rideService
                .getRidesCreatedByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}/route-info")
    public RideRouteInfoResponse getRouteInfo(
            @PathVariable Long id) {

        return routeInfoService
                .getRouteInfo(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(
            @PathVariable Long id) {

        return rideService
                .getRideById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody RideRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User driver =
                getAuthenticatedUser(jwt);

        Ride ride = new Ride(
                request.getOrigin(),
                request.getDestination(),
                request.getDepartureTime(),
                request.getAvailableSeats(),
                driver
        );

        Ride savedRide =
                rideService.createRide(ride);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedRide));
    }

    private User getAuthenticatedUser(
            Jwt jwt) {

        String email = jwt.getSubject();

        return userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));
    }

    private RideResponse toResponse(
            Ride ride) {

        return new RideResponse(
                ride.getId(),
                ride.getOrigin(),
                ride.getDestination(),
                ride.getDepartureTime(),
                ride.getAvailableSeats(),
                ride.getDriver().getId(),
                ride.getDriver().getName()
        );
    }
}