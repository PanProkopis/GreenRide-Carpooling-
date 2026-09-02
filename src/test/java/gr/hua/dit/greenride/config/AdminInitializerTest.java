package gr.hua.dit.greenride.config;

import gr.hua.dit.greenride.entity.Role;
import gr.hua.dit.greenride.entity.User;
import gr.hua.dit.greenride.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminInitializerTest {

    @Test
    void shouldCreateAdministratorWithNormalizedEmailAndAdminRole() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("strong-password"))
                .thenReturn("encoded-password");

        AdminInitializer initializer = new AdminInitializer(
                userRepository,
                passwordEncoder,
                " Administrator ",
                " Admin@Example.COM ",
                "strong-password"
        );

        initializer.run();

        verify(userRepository).save(any(User.class));
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.getName().equals("Administrator")
                        && user.getEmail().equals("admin@example.com")
                        && user.getRole() == Role.ADMIN
        ));
    }

    @Test
    void shouldStorePasswordAsBcryptHash() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.empty());

        AdminInitializer initializer = new AdminInitializer(
                userRepository,
                passwordEncoder,
                "Administrator",
                "admin@example.com",
                "strong-password"
        );

        initializer.run();

        org.mockito.ArgumentCaptor<User> userCaptor =
                org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        String storedPassword = userCaptor.getValue().getPassword();

        assertNotEquals("strong-password", storedPassword);
        assertTrue(storedPassword.startsWith("$2"));
        assertTrue(passwordEncoder.matches("strong-password", storedPassword));
    }

    @Test
    void shouldNotDuplicateExistingAdministrator() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        User existingAdministrator = new User(
                "Administrator",
                "admin@example.com",
                "existing-hash",
                Role.ADMIN
        );
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(existingAdministrator));

        AdminInitializer initializer = new AdminInitializer(
                userRepository,
                passwordEncoder,
                "Administrator",
                " ADMIN@example.com ",
                "strong-password"
        );

        initializer.run();

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldNotCreateAdministratorWhenCredentialsAreEmpty() {

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        AdminInitializer initializer = new AdminInitializer(
                userRepository,
                passwordEncoder,
                "Administrator",
                "",
                ""
        );

        initializer.run();

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
