package com.example.demo.config;

import com.example.demo.controller.AuthorizationCheck;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AppConfigTest {

    @Test
    void passwordConfig_PasswordEncoderBeanWorks() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();
        String encoded = encoder.encode("testpass");
        assertThat(encoder.matches("testpass", encoded)).isTrue();
    }

    @Test
    void viewResolverConfig_ViewResolverBeanWorks() {
        ViewResolverConfig config = new ViewResolverConfig();
        InternalResourceViewResolver resolver = config.viewResolver();
        assertThat(resolver).isNotNull();
    }

    @Test
    void openApiConfig_Instantiation() {
        OpenApiConfig config = new OpenApiConfig();
        assertThat(config).isNotNull();
    }

    @Test
    void corsConfig_AddsMappingsWhenConfigured() {
        CorsConfig corsConfig = new CorsConfig();
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", "http://localhost:3000,http://example.com");

        CorsRegistry registry = new CorsRegistry();
        corsConfig.addCorsMappings(registry);
        // Should configure without error
    }

    @Test
    void corsConfig_DoesNothingWhenEmpty() {
        CorsConfig corsConfig = new CorsConfig();
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", "");

        CorsRegistry registry = new CorsRegistry();
        corsConfig.addCorsMappings(registry);
    }

    @Test
    void dataInitializer_SeedsUsersWhenEnabled() throws Exception {
        DataInitializer initializer = new DataInitializer();
        ReflectionTestUtils.setField(initializer, "seedEnabled", true);

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);

        CommandLineRunner runner = initializer.seedUsers(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void dataInitializer_DoesNotSeedWhenDisabled() throws Exception {
        DataInitializer initializer = new DataInitializer();
        ReflectionTestUtils.setField(initializer, "seedEnabled", false);

        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        CommandLineRunner runner = initializer.seedUsers(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authorizationCheck_isReservationOwner() {
        ReservationRepository reservationRepo = mock(ReservationRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        AuthorizationCheck authCheck = new AuthorizationCheck(reservationRepo, userRepo);

        assertThat(authCheck.isReservationOwner(null, null)).isFalse();

        Authentication auth = new UsernamePasswordAuthenticationToken("user@example.com", "pass");
        assertThat(authCheck.isReservationOwner(1L, auth)).isFalse();

        User user = new User();
        user.setId(5L);
        user.setEmail("user@example.com");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Reservation res = new Reservation();
        res.setId(1L);
        res.setUser(user);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(res));

        assertThat(authCheck.isReservationOwner(1L, auth)).isTrue();

        User otherUser = new User();
        otherUser.setId(6L);
        res.setUser(otherUser);
        assertThat(authCheck.isReservationOwner(1L, auth)).isFalse();
    }
}
