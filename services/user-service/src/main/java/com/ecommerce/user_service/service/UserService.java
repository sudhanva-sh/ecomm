package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.UserRequestDto;
import com.ecommerce.user_service.dto.UserResponseDto;
import com.ecommerce.user_service.entity.User;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto user);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserRequestDto user);
    void deleteUser(Long id);
}
