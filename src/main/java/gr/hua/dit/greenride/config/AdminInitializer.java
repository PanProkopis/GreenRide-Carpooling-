package gr.hua.dit.greenride.config;

import gr.hua.dit.greenride.entity.Role;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 72;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.name:}") String adminName,
            @Value("${app.admin.email:}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {

        if (adminEmail == null
                || adminEmail.isBlank()
                || adminPassword == null
                || adminPassword.isEmpty()) {
            return;
        }

        String normalizedEmail = adminEmail
                .trim()
                .toLowerCase();

        if (normalizedEmail.isEmpty()) {
            return;
        }

        if (adminPassword.length() < MINIMUM_PASSWORD_LENGTH
                || adminPassword.length() > MAXIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Administrator password must contain between 8 and 72 characters"
            );
        }

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return;
        }

        User administrator = new User(
                adminName == null ? "" : adminName.trim(),
                normalizedEmail,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN
        );

        userRepository.save(administrator);
    }
}
