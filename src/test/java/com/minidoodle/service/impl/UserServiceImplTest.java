package com.minidoodle.service.impl;

import com.minidoodle.dto.UserDTO;
import com.minidoodle.helper.TestDataHelper;
import com.minidoodle.mapper.UserMapper;
import com.minidoodle.model.User;
import com.minidoodle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = TestDataHelper.createDefaultTestUser();
        userDTO = TestDataHelper.createDefaultTestUserDTO();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userDTO.getId());
        assertThat(result.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(result.getName()).isEqualTo(userDTO.getName());

        verify(userMapper).toEntity(userDTO);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userDTO.getId());
        verify(userRepository).findById(1L);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserById(1L);

        assertThat(result).isEmpty();
        verify(userRepository).findById(1L);
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.getUserByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(userDTO.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toDTO(user);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = Collections.singletonList(user);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(userDTO.getId());
        verify(userRepository).findAll();
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.updateUser(1L, userDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userDTO.getId());
        verify(userRepository).existsById(1L);
        verify(userMapper).toEntity(userDTO);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.updateUser(1L, userDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(userRepository).existsById(1L);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }
} 