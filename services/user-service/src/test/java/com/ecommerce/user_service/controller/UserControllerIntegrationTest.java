package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.UserRequestDto;
import com.ecommerce.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc

public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception{
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Sharad");
        dto.setEmail("sharad@gmail.com");
        dto.setPassword("abcd");
        dto.setRole("admin");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sharad"))
                .andExpect(jsonPath("$.email").value("sharad@gmail.com"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // First create user
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Sharad");
        dto.setEmail("sharad@gmail.com");
        dto.setPassword("abcd");
        dto.setRole("admin");

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).path("id").asLong();

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sharad"))
                .andExpect(jsonPath("$.email").value("sharad@gmail.com"));
    }

    @Test
    void getUserById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id 999"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("Sharad");
        dto.setEmail("sharad@gmail.com");
        dto.setPassword("abcd");
        dto.setRole("admin");

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).path("id").asLong();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());
    }
}
