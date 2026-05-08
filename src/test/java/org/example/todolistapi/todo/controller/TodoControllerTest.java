package org.example.todolistapi.todo.controller;

import org.example.todolistapi.auth.security.JwtTokenProvider;
import org.example.todolistapi.global.exception.ForbiddenTodoAccessException;
import org.example.todolistapi.global.exception.TodoNotFoundException;
import org.example.todolistapi.todo.dto.TodoListResponse;
import org.example.todolistapi.todo.dto.TodoResponse;
import org.example.todolistapi.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void createTodo_returnsCreated_whenRequestIsValid() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        mockMvc.perform(post("/todos")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title" : "title",
                                "description" : "description"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(todoService).createTodo(1L, "title", "description");
    }

    @Test
    void createTodo_returnsBadRequest_whenTitleIsBlank() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        mockMvc.perform(post("/todos")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title" : "",
                                "description" : "description"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).createTodo(anyLong(), anyString(), anyString());
    }

    @Test
    void getTodos_returnsOk_whenRequestIsValid() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        TodoListResponse response = new TodoListResponse(
                List.of(new TodoResponse(1L, "title", "description")),
                1,
                10,
                1
        );

        when(todoService.getTodos(1L, 1, 10))
                .thenReturn(response);

        mockMvc.perform(get("/todos")
                        .param("page", "1")
                        .param("limit", "10")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("title"))
                .andExpect(jsonPath("$.data[0].description").value("description"));


        verify(todoService).getTodos(1L, 1, 10);
    }

    @Test
    void getTodos_returnsBadRequest_whenPageIsInvalid() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        mockMvc.perform(get("/todos")
                        .param("page", "0")
                        .param("limit", "10")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).getTodos(anyLong(), anyInt(), anyInt());
    }

    @Test
    void updateTodo_returnsOk_whenRequestIsValid() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        TodoResponse response = new TodoResponse(1L, "new title", "new description");

        when(todoService.updateTodo(1L, 1L, "new title", "new description"))
                .thenReturn(response);

        mockMvc.perform(put("/todos/{todoId}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title" : "new title",
                                "description" : "new description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.description").value("new description"));

        verify(todoService).updateTodo(1L, 1L, "new title", "new description");
    }

    @Test
    void updateTodo_returnsNotFound_whenTodoNotFound() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        when(todoService.updateTodo(1L, 1L, "new title", "new description"))
                .thenThrow(new TodoNotFoundException("Todo not found"));

        mockMvc.perform(put("/todos/{todoId}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title" : "new title",
                                "description" : "new description"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(todoService).updateTodo(1L, 1L, "new title", "new description");
    }

    @Test
    void updateTodo_returnsForbidden_whenUserDoesNotOwnTodo() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        when(todoService.updateTodo(1L, 1L, "new title", "new description"))
                .thenThrow(new ForbiddenTodoAccessException("You are not allowed to update this todo"));

        mockMvc.perform(put("/todos/{todoId}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "title" : "new title",
                                "description" : "new description"
                                }
                                """))
                .andExpect(status().isForbidden());

        verify(todoService).updateTodo(1L, 1L, "new title", "new description");
    }

    @Test
    void deleteTodo_returnsNoContent_whenUserOwnsTodo() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        mockMvc.perform(delete("/todos/{todoId}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(1L, 1L);
    }

    @Test
    void deleteTodo_returnsNotFound_whenTodoNotFound() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        doThrow(new TodoNotFoundException("Todo not found"))
                .when(todoService).deleteTodo(1L, 1L);

        mockMvc.perform(delete("/todos/{todoId}", 1L)
                        .principal(authentication))
                .andExpect(status().isNotFound());

        verify(todoService).deleteTodo(1L, 1L);
    }

    @Test
    void deleteTodo_returnsForbidden_whenUserDoesNotOwnTodo() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(1L, null, List.of());

        doThrow(new ForbiddenTodoAccessException("You are not allowed to delete this todo"))
                .when(todoService).deleteTodo(1L, 1L);

        mockMvc.perform(delete("/todos/{todoId}", 1L)
                        .principal(authentication))
                .andExpect(status().isForbidden());

        verify(todoService).deleteTodo(1L, 1L);
    }
}
