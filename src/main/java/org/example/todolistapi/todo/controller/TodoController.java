package org.example.todolistapi.todo.controller;

import lombok.RequiredArgsConstructor;
import org.example.todolistapi.todo.dto.TodoCreateRequest;
import org.example.todolistapi.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<Void> createTodo(@RequestBody TodoCreateRequest request,
                                           Authentication authentication) {
        todoService.createTodo(
                request.title(),
                request.description(),
                authentication.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
