package com.example.usermanagement.service;

import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.kafka.UserEventProducer;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        testUserRequest = UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .role(User.UserRole.USER)
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(userEventProducer).sendUserEvent(any());

        UserResponse response = userService.createUser(testUserRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testUserRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userEventProducer, times(1)).sendUserEvent(any());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(testUserRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        var response = userService.getAllUsers(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void updateUser_Success() {
        UserRequest updateRequest = UserRequest.builder()
                .firstName("John")
                .lastName("Updated")
                .email("john.updated@example.com")
                .phone("+1234567890")
                .role(User.UserRole.USER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(userEventProducer).sendUserEvent(any());

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(userEventProducer, times(1)).sendUserEvent(any());
        verify(userRepository, times(1)).existsByEmail("john.updated@example.com");
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));
        doNothing().when(userEventProducer).sendUserEvent(any());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(testUser);
        verify(userEventProducer, times(1)).sendUserEvent(any());
    }

    @Test
    void searchUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.searchUsers(anyString(), any(Pageable.class))).thenReturn(userPage);

        var response = userService.searchUsers("john", pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        verify(userRepository, times(1)).searchUsers(anyString(), any(Pageable.class));
    }

    @Test
    void countUsersByStatus_Success() {
        when(userRepository.countByStatus(User.UserStatus.ACTIVE)).thenReturn(5L);

        long count = userService.countUsersByStatus(User.UserStatus.ACTIVE);

        assertThat(count).isEqualTo(5L);
        verify(userRepository, times(1)).countByStatus(User.UserStatus.ACTIVE);
    }
}
