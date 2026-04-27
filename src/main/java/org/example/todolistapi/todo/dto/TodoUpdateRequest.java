package org.example.todolistapi.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;

public record TodoUpdateRequest(@PathVariable Long id, @NotBlank String title, @NotNull String description) {
}
