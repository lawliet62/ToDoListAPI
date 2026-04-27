package org.example.todolistapi.todo.dto;

import java.util.List;

public record TodoListResponse(
        List<TodoResponse> data,
        int page,
        int limit,
        long total
) {
}
