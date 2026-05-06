package org.example.todolistapi.user.repository;

import org.example.todolistapi.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    @Test
    void existsByEmail_returnsTrue_whenUserExists() {
        User user = new User("tester", "test@example.com", "encoded-password");
        repository.save(user);

        boolean result = repository.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_returnsFalse_whenUserDoesNotExist() {
        boolean result = repository.existsByEmail("missing@example.com");

        assertFalse(result);
    }

    @Test
    void findByEmail_returnsUser_whenUserExists() {
        User user = new User("tester", "test@example.com", "encoded-password");
        repository.save(user);

        Optional<User> result = repository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("tester", result.get().getName());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("encoded-password", result.get().getEncodedPassword());
    }

    @Test
    void findByEmail_returnsEmpty_whenUserDoesNotExist() {
        Optional<User> result = repository.findByEmail("missing@example.com");

        assertTrue(result.isEmpty());
    }
}