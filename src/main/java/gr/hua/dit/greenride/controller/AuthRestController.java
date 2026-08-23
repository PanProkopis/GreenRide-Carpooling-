package gr.hua.dit.greenride.controller;

import gr.hua.dit.greenride.dto.LoginRequest;
import gr.hua.dit.greenride.dto.LoginResponse;
import gr.hua.dit.greenride.dto.RegisterRequest;
import gr.hua.dit.greenride.dto.UserResponse;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.ResourceNotFoundException;
import gr.hua.dit.greenride.security.JwtService;
import gr.hua.dit.greenride.service.AuthService;
import gr.hua.dit.greenride.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirements
public class AuthRestController {

    private final AuthService authService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthRestController(
            AuthService authService,
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authService = authService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken
                        .unauthenticated(
                                email,
                                request.getPassword()
                        );

        authenticationManager.authenticate(
                authenticationRequest
        );

        User user = userService
                .getUserByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        String token =
                jwtService.generateToken(user);

        LoginResponse response =
                new LoginResponse(
                        token,
                        "Bearer",
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                );

        return ResponseEntity.ok(response);
    }
}