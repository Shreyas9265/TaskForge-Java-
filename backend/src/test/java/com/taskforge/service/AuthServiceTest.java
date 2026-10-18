package com.taskforge.service;

import com.taskforge.dto.AuthDtos.AuthResponse;
import com.taskforge.dto.AuthDtos.LoginRequest;
import com.taskforge.dto.AuthDtos.RegisterRequest;
import com.taskforge.entity.User;
import com.taskforge.exception.DuplicateResourceException;
import com.taskforge.repository.UserRepository;
import com.taskforge.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("alice")
                .password("encoded-pass")
                .authorities(List.of())
                .build();
    }

    @Test
    void register_createsUser_whenUsernameAndEmailAreFree() {
        RegisterRequest request = new RegisterRequest("alice", "alice@example.com", "secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-pass");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.username()).isEqualTo("alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throws_whenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest("alice", "alice@example.com", "secret123");
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throws_whenEmailAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest("alice", "alice@example.com", "secret123");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void login_returnsToken_whenCredentialsValid() {
        LoginRequest request = new LoginRequest("alice", "secret123");
        User user = User.builder().id(1L).username("alice").email("alice@example.com")
                .password("encoded-pass").role(User.Role.USER).build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.role()).isEqualTo("USER");
        verify(authenticationManager).authenticate(any());
    }
}
