package org.example.todolistapi.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.todolistapi.auth.dto.AuthResponse;
import org.example.todolistapi.user.entity.User;
import org.example.todolistapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String secretKey;

    @Transactional
    public AuthResponse registerUser(String name, String email, String password) {
        if (repository.existsByEmail(email))
            throw new IllegalArgumentException("Email already exists");

        String encodedPassword = passwordEncoder.encode(password);

        repository.save(new User(name, email, encodedPassword));

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject(email).signWith(key).compact();

        return new AuthResponse(token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new IllegalArgumentException("Invalid email or password");

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder().subject(user.getEmail()).signWith(key).compact();

        return new AuthResponse(token);
    }

}
