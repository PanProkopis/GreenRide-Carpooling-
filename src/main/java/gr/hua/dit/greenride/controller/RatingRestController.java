package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.RatingRequest;
import gr.hua.dit.greenride.dto.RatingResponse;
import gr.hua.dit.greenride.entity.Rating;
import gr.hua.dit.greenride.entity.Ride;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.service.RatingService;
import gr.hua.dit.greenride.service.RideService;
import gr.hua.dit.greenride.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingRestController {

    private final RatingService ratingService;
    private final UserService userService;
    private final RideService rideService;

    public RatingRestController(
            RatingService ratingService,
            UserService userService,
            RideService rideService) {

        this.ratingService = ratingService;
        this.userService = userService;
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();

        User fromUser = userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        ));

        User toUser = userService
                .getUserById(request.getToUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User to rate not found"
                        ));

        Ride ride = rideService
                .getRideById(request.getRideId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride not found"
                        ));

        Rating rating = ratingService.createRating(
                request.getScore(),
                request.getComment(),
                fromUser,
                toUser,
                ride
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(rating));
    }

    @GetMapping
    public List<RatingResponse> getAllRatings() {

        return ratingService.getAllRatings()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingResponse> getRatingById(
            @PathVariable Long id) {

        return ratingService.getRatingById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    private RatingResponse toResponse(Rating rating) {

        return new RatingResponse(
                rating.getId(),
                rating.getScore(),
                rating.getComment(),
                rating.getFromUser().getId(),
                rating.getFromUser().getName(),
                rating.getToUser().getId(),
                rating.getToUser().getName(),
                rating.getRide().getId()
        );
    }
}