package org.example.todolistapi.todo.service;

import lombok.RequiredArgsConstructor;
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
    public void createTodo(String title, String description, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Todo todo = new Todo(title, description, user);
        todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public TodoListResponse getTodos(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Todo> todoPage = todoRepository.findAll(pageable);

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


}


