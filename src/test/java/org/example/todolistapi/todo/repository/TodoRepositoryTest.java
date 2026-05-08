package org.example.todolistapi.todo.repository;

import org.example.todolistapi.todo.entity.Todo;
import org.example.todolistapi.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findByUser_returnsPagedTodosForGivenUser() {
        User user = new User("test", "test@example.com", "encoded-password");
        User otherUser = new User("other", "other@example.com", "encoded-password");

        testEntityManager.persist(user);
        testEntityManager.persist(otherUser);

        Todo todo = new Todo("title", "description", user);
        Todo otherTodo = new Todo("other title", "other description", otherUser);

        testEntityManager.persist(todo);
        testEntityManager.persist(otherTodo);
        testEntityManager.flush();
        testEntityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Todo> todoPage = todoRepository.findByUser(user, pageable);

        assertEquals(1, todoPage.getTotalElements());
        assertEquals("title", todoPage.getContent().getFirst().getTitle());
        assertEquals("description", todoPage.getContent().getFirst().getDescription());
    }


}