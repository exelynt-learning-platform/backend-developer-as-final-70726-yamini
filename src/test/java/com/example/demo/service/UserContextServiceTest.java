package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserContextServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserContextService userContextService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void currentUserId_WithCustomUserDetails() {
        User user = new User();
        user.setId(10L);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Long id = userContextService.currentUserId(authentication);
        assertEquals(10L, id);
    }

    @Test
    void currentUserId_WithEmailFallback_Success() {
        when(authentication.getPrincipal()).thenReturn("some-string-principal");
        when(authentication.getName()).thenReturn("test@example.com");

        User user = new User();
        user.setId(20L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Long id = userContextService.currentUserId(authentication);
        assertEquals(20L, id);
    }

    @Test
    void currentUserId_WithEmailFallback_NotFound() {
        when(authentication.getPrincipal()).thenReturn("some-string-principal");
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userContextService.currentUserId(authentication));
    }

    @Test
    void currentUserId_NullAuthentication() {
        assertThrows(UnauthorizedException.class, () -> userContextService.currentUserId(null));
    }

    @Test
    void isAdmin_True() {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        assertTrue(userContextService.isAdmin(authentication));
    }

    @Test
    void isAdmin_False() {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();
        assertFalse(userContextService.isAdmin(authentication));
    }

    @Test
    void isAdmin_NullAuthentication() {
        assertFalse(userContextService.isAdmin(null));
    }
}
