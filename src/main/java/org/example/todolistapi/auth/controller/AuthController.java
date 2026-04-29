package org.example.todolistapi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.todolistapi.auth.dto.AuthResponse;
import org.example.todolistapi.auth.dto.LoginRequest;
import org.example.todolistapi.auth.dto.RegistrationRequest;
import org.example.todolistapi.auth.service.AuthService;
import org.example.todolistapi.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegistrationRequest request) {
        AuthResponse response = authService.registerUser(
                request.name(),
                request.email(),
                request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("You have successfully registered", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(ApiResponse.success("You have successfully logged in", response));
    }

}
