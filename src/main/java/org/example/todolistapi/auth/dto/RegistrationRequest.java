package org.example.todolistapi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(@NotBlank String name, @Email String email, @NotBlank String password) {
}
