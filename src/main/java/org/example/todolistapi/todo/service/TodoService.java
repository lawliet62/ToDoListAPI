package org.example.todolistapi.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.todolistapi.global.exception.AuthenticatedUserNotFoundException;
import org.example.todolistapi.global.exception.ForbiddenTodoAccessException;
import org.example.todolistapi.global.exception.TodoNotFoundException;
import org.example.todolistapi.todo.dto.TodoListResponse;
import org.example.todolistapi.todo.dto.TodoResponse;
import org.example.todolistapi.todo.entity.Todo;
import org.example.todolistapi.todo.repository.TodoRepository;
import org.example.todolistapi.user.entity.User;
import org.example.todolistapi.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTodo(Long userId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authenticated user not found"));

        Todo todo = new Todo(title, description, user);
        todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public TodoListResponse getTodos(Long userId, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authenticated user not found"));

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Todo> todoPage = todoRepository.findByUser(user, pageable);

        List<TodoResponse> data = todoPage.getContent().stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getTitle(),
                        todo.getDescription()
                ))
                .toList();

        return new TodoListResponse(
                data,
                page,
                limit,
                todoPage.getTotalElements()
        );
    }

    @Transactional
    public void updateTodo(Long userId, Long todoId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authenticated user not found"));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId()))
            throw new ForbiddenTodoAccessException("You are not allowed to update this todo");

        todo.setTitle(title);
        todo.setDescription(description);
    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException("Authenticated user not found"));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId()))
            throw new ForbiddenTodoAccessException("You are not allowed to delete this todo");

        todoRepository.delete(todo);
    }



}


