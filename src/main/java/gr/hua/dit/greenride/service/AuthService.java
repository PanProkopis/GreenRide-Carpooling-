package gr.hua.dit.greenride.service;

import gr.hua.dit.greenride.dto.RegisterRequest;
import gr.hua.dit.greenride.entity.Role;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.exception.DuplicateUserException;
import gr.hua.dit.greenride.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException(
                    "A user with this email already exists"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName().trim(),
                email,
                encodedPassword,
                Role.USER
        );

        return userRepository.save(user);
    }
}