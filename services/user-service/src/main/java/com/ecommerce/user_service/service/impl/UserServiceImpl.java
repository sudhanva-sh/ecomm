package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.UserMapper;
import com.ecommerce.user_service.dto.UserRequestDto;
import com.ecommerce.user_service.dto.UserResponseDto;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.ResourceNotFoundException;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto){
        User user = UserMapper.toEntity(userRequestDto);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return UserMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id)));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());

        return UserMapper.toResponse(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
