package org.example.todolistapi.todo.service;

import org.example.todolistapi.global.exception.AuthenticatedUserNotFoundException;
import org.example.todolistapi.global.exception.ForbiddenTodoAccessException;
import org.example.todolistapi.global.exception.TodoNotFoundException;
import org.example.todolistapi.todo.dto.TodoListResponse;
import org.example.todolistapi.todo.dto.TodoResponse;
import org.example.todolistapi.todo.entity.Todo;
import org.example.todolistapi.todo.repository.TodoRepository;
import org.example.todolistapi.user.entity.User;
import org.example.todolistapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createTodo_savesTodoWithAuthenticatedUser() {
        User user = new User("test", "test@example.com", "encoded-password");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        todoService.createTodo(1L, "title", "description");

        ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(todoCaptor.capture());

        Todo savedTodo = todoCaptor.getValue();

        assertEquals("title", savedTodo.getTitle());
        assertEquals("description", savedTodo.getDescription());
        assertEquals(user, savedTodo.getUser());
    }

    @Test
    void createTodo_throwsException_whenAuthenticatedUserNotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> todoService.createTodo(1L, "title", "description")
        );

        verify(todoRepository, never()).save(any());
    }

    @Test
    void getTodos_returnsTodoListResponseWithAuthenticatedUser() {
        User user = new User("test", "test@example.com", "encoded-password");

        Todo todo = new Todo("title", "description", user);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Todo> todoPage = new PageImpl<>(
                List.of(todo),
                pageRequest,
                1
        );

        when(userRepository.findById(1L)).
                thenReturn(Optional.of(user));

        when(todoRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(todoPage);

        TodoListResponse todoListResponse = todoService.getTodos(1L, 1, 10);

        assertEquals(1, todoListResponse.page());
        assertEquals(10, todoListResponse.limit());
        assertEquals(1, todoListResponse.total());
        assertEquals(1, todoListResponse.data().size());

        TodoResponse todoResponse = todoListResponse.data().getFirst();
        assertEquals("title", todoResponse.title());
        assertEquals("description", todoResponse.description());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(todoRepository).findByUser(eq(user), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
    }

    @Test
    void updateTodo_updatesTodoAndReturnsResponse_whenUserOwnsTodo() {
        User user = new User("test", "test@example.com", "encoded-password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "description", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        TodoResponse response = todoService.updateTodo(
                1L, 1L, "new title", "new description"
        );

        assertEquals("new title", todo.getTitle());
        assertEquals("new description", todo.getDescription());

        assertEquals(1L, response.id());
        assertEquals("new title", response.title());
        assertEquals("new description", response.description());
    }

    @Test
    void updateTodo_throwsAuthenticatedUserNotFoundException_whenAuthenticatedUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> todoService.updateTodo(1L, 1L, "new title", "new description")
        );
    }

    @Test
    void updateTodo_throwsTodoNotFoundException_whenTodoNotFound() {
        User user = new User("test", "test@example.com", "encoded-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.updateTodo(1L, 1L, "new title", "new description")
        );
    }

    @Test
    void updateTodo_throwsForbiddenTodoAccessException_whenUserDoesNotOwnTodo() {
        User user = new User("test", "test@example.com", "encoded-password");
        ReflectionTestUtils.setField(user, "id", 1L);

        User otherUser = new User(
                "otherTest", "OtherTest@example.com", "encoded-password"
        );
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        Todo todo = new Todo("title", "description", otherUser);
        ReflectionTestUtils.setField(todo, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        assertThrows(
                ForbiddenTodoAccessException.class,
                () -> todoService.updateTodo(1L, 1L, "new title", "new description")
        );

        assertEquals("title", todo.getTitle());
        assertEquals("description", todo.getDescription());
    }

    @Test
    void deleteTodo_deletesTodo_whenUserOwnsTodo() {
        User user = new User("test", "test@example.com", "encoded-password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("title", "description", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(1L, 1L);

        verify(todoRepository).delete(todo);
    }

    @Test
    void deleteTodo_throwsAuthenticatedUserNotFoundException_whenAuthenticatedUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> todoService.deleteTodo(1L, 1L)
        );
    }

    @Test
    void deleteTodo_throwsTodoNotFoundException_whenTodoNotFound() {
        User user = new User("test", "test@example.com", "encoded-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.deleteTodo(1L, 1L)
        );
    }

    @Test
    void deleteTodo_throwsForbiddenTodoAccessException_whenUserDoesNotOwnTodo() {
        User user = new User("test", "test@example.com", "encoded-password");
        ReflectionTestUtils.setField(user, "id", 1L);

        User otherUser = new User(
                "otherTest", "otherTest@example.com", "encoded-password"
        );
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        Todo todo = new Todo("title", "description", otherUser);
        ReflectionTestUtils.setField(todo, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        assertThrows(
                ForbiddenTodoAccessException.class,
                () -> todoService.deleteTodo(1L, 1L)
        );

        verify(todoRepository, never()).delete(any());
    }


}
