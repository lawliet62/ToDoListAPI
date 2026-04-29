package org.example.todolistapi.todo.repository;

import org.example.todolistapi.todo.entity.Todo;
import org.example.todolistapi.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByUser(User user, Pageable pageable);
}
