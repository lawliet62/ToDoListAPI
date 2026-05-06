package org.example.todolistapi.auth.controller;

import org.example.todolistapi.auth.dto.AuthResponse;
import org.example.todolistapi.auth.security.JwtAuthenticationFilter;
import org.example.todolistapi.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_returnsCreatedResponse_whenRequestIsValid() throws Exception {
        when(authService.registerUser("test", "test@example.com", "1234"))
                .thenReturn(new AuthResponse("token"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "test",
                                  "email": "test@example.com",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You have successfully registered"))
                .andExpect(jsonPath("$.data.token").value("token"));
    }


    @Test
    void register_returnsBadRequest_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "test@example.com",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_returnsOkResponse_whenRequestIsValid() throws Exception {
        when(authService.login("test@example.com", "1234"))
                .thenReturn(new AuthResponse("token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@example.com",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You have successfully logged in"))
                .andExpect(jsonPath("$.data.token").value("token"));

        verify(authService).login("test@example.com", "1234");
    }

    @Test
    void login_returnsBadRequest_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}