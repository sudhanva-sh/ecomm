package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.UserRequestDto;
import com.ecommerce.user_service.dto.UserResponseDto;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.ResourceNotFoundException;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("sharad")
                .email("sharad@gmail.com")
                .password("abcd")
                .role("admin")
                .build();

    }

    @Test
    void createUser_ShouldReturnResponseDto(){
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("sharad")
                .email("sharad@gmail.com")
                .password("abcd")
                .role("admin")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto savedUser = userService.createUser(userRequestDto);

        assertEquals("sharad", savedUser.getName());
        assertEquals("sharad@gmail.com", savedUser.getEmail());

    }

    @Test
    void getUserById_ShouldThrowIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }
}
