package org.example.todolistapi.todo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.todolistapi.todo.dto.TodoCreateRequest;
import org.example.todolistapi.todo.dto.TodoListResponse;
import org.example.todolistapi.todo.dto.TodoResponse;
import org.example.todolistapi.todo.dto.TodoUpdateRequest;
import org.example.todolistapi.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Void> createTodo(Authentication authentication,
                                           @Valid @RequestBody TodoCreateRequest request) {
        todoService.createTodo(
                (Long) authentication.getPrincipal(),
                request.title(),
                request.description()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<TodoListResponse> getTodos(Authentication authentication,
                                                     @RequestParam @Min(1) int page,
                                                     @RequestParam @Min(1) int limit) {
        TodoListResponse response = todoService.getTodos(
                (Long) authentication.getPrincipal(),
                page,
                limit
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponse> updateTodo(Authentication authentication,
                                                   @PathVariable Long todoId,
                                                   @Valid @RequestBody TodoUpdateRequest request) {
        TodoResponse response = todoService.updateTodo(
                (Long) authentication.getPrincipal(),
                todoId,
                request.title(),
                request.description()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(Authentication authentication,
                                           @PathVariable Long todoId) {
        todoService.deleteTodo(
                (Long) authentication.getPrincipal(),
                todoId
        );

        return ResponseEntity.noContent().build();
    }

}
