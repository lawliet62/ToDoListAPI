package org.example.todolistapi.user.repository;

import org.example.todolistapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {
}
