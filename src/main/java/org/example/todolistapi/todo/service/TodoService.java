package org.example.todolistapi.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.todolistapi.todo.dto.TodoListResponse;
import org.example.todolistapi.todo.dto.TodoResponse;
import org.example.todolistapi.todo.entity.Todo;
import org.example.todolistapi.todo.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
class TodoService {

    private final TodoRepository repository;

    public void createTodo(String title, String description) {
        Todo todo = new Todo(title, description);
        repository.save(todo);
    }

    public TodoListResponse getTodos(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Todo> todoPage = repository.findAll(pageable);

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


