package org.example.todolistapi.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.todolistapi.auth.dto.AuthResponse;
import org.example.todolistapi.auth.security.JwtTokenProvider;
import org.example.todolistapi.global.exception.DuplicateEmailException;
import org.example.todolistapi.global.exception.InvalidCredentialsException;
import org.example.todolistapi.user.entity.User;
import org.example.todolistapi.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse registerUser(String name, String email, String password) {
        if (repository.existsByEmail(email))
            throw new DuplicateEmailException("Email already exists");

        String encodedPassword = passwordEncoder.encode(password);
        repository.save(new User(name, email, encodedPassword));

        String token = jwtTokenProvider.generateToken(email);
        return new AuthResponse(token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getEncodedPassword()))
            throw new InvalidCredentialsException("Invalid email or password");

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

}
