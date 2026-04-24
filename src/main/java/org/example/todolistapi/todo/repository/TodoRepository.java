package org.example.todolistapi.todo.repository;

import org.example.todolistapi.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
