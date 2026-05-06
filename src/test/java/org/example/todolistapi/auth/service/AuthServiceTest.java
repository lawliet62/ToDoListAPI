package org.example.todolistapi.auth.service;

import org.example.todolistapi.auth.dto.AuthResponse;
import org.example.todolistapi.auth.security.JwtTokenProvider;
import org.example.todolistapi.global.exception.DuplicateEmailException;
import org.example.todolistapi.global.exception.InvalidCredentialsException;
import org.example.todolistapi.user.entity.User;
import org.example.todolistapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_throwsDuplicateEmailException_whenEmailAlreadyExists() {
        when(repository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> authService.registerUser("test", "test@example.com", "1234")
        );

        verify(repository).existsByEmail("test@example.com");
    }

    @Test
    void registerUser_savesEncodedPasswordAndReturnsToken() {
        when(repository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded-password");

        User savedUser = mock(User.class);
        when(savedUser.getId()).thenReturn(1L);

        when(repository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("token");

        AuthResponse response = authService.registerUser("test", "test@example.com", "1234");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("test", capturedUser.getName());
        assertEquals("test@example.com", capturedUser.getEmail());
        assertEquals("encoded-password", capturedUser.getEncodedPassword());

        verify(passwordEncoder).encode("1234");
        verify(jwtTokenProvider).generateToken(1L);
        assertEquals("token", response.token());
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = mock(User.class);
        when((user.getId())).thenReturn(1L);
        when(user.getEncodedPassword()).thenReturn("encoded-password");

        when(repository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("token");

        AuthResponse response = authService.login("test@example.com", "1234");

        assertEquals("token", response.token());
        verify(repository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("1234", "encoded-password");
        verify(jwtTokenProvider).generateToken(1L);
    }

    @Test
    void login_throwsInvalidCredentialsException_whenEmailDoesNotExist() {
        when(repository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login("test@example.com", "1234")
        );

        verify(repository).findByEmail("test@example.com");
    }

    @Test
    void login_throwsInvalidCredentialsException_whenPasswordIstInvalid() {
        User user = mock(User.class);
        when(user.getEncodedPassword()).thenReturn("encoded-password");

        when(repository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "encoded-password")).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login("test@example.com", "1234")
        );

        verify(repository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("1234", "encoded-password");
        verify(jwtTokenProvider, never()).generateToken(anyLong());
    }
}