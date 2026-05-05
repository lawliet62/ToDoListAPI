package org.example.todolistapi.global.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.todolistapi.auth.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ControllerAdviceTest.TestController.class)
@Import({ControllerAdvice.class, ControllerAdviceTest.TestController.class})
@AutoConfigureMockMvc(addFilters = false)
class ControllerAdviceTest {

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    // 예외를 강제로 던지는 테스트용 컨트롤러
    @RestController
    static class TestController {

        @GetMapping("/test/invalid-credentials")
        void invalidCredentials() {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        @GetMapping("/test/duplicate-email")
        void duplicateEmail() {
            throw new DuplicateEmailException("Duplicate E-mail");
        }

        @PostMapping("/test/validation")
        void validationException(@Valid @RequestBody ValidationRequest request) {
        }

        @GetMapping("/test/forbidden-todo-access")
        void forbiddenTodoAccessException() {
            throw new ForbiddenTodoAccessException("Forbidden access");
        }

        @GetMapping("/test/authenticated-user-not-found")
        void authenticatedUserNotFoundException() {
            throw new AuthenticatedUserNotFoundException("Authenticated user not found");
        }

        @GetMapping("/test/todo-not-found")
        void todoNotFoundException() {
            throw new TodoNotFoundException("Todo not found");
        }

    }

    // validation 테스트용 요청 DTD
    @Getter
    @Setter
    static class ValidationRequest {
        @NotBlank(message = "Title is required")
        private String title;
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void handleInvalidCredentials() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void handleDuplicateEmailException() throws Exception {
        mockMvc.perform(get("/test/duplicate-email"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Duplicate E-mail"));
    }

    @Test
    void handleValidationException() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Title is required"));
    }

    @Test
    void handleForbiddenAccess() throws Exception {
        mockMvc.perform(get("/test/forbidden-todo-access"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Forbidden access"));
    }

    @Test
    void handleAuthenticatedUserNotFound() throws Exception {
        mockMvc.perform(get("/test/authenticated-user-not-found"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Authenticated user not found"));
    }

    @Test
    void handleTodoNotFound() throws Exception {
        mockMvc.perform(get("/test/todo-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Todo not found"));
    }
}