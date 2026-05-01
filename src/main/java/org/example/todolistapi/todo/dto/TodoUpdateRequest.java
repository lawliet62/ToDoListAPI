package org.example.todolistapi.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TodoUpdateRequest(@NotBlank String title, @NotNull String description) {
}
