package com.example.usermanagement.service;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable);

    PageResponse<UserResponse> getUsersByRole(User.UserRole role, Pageable pageable);

    PageResponse<UserResponse> getUsersByStatus(User.UserStatus status, Pageable pageable);

    PageResponse<UserResponse> getUsersByRoleAndStatus(User.UserRole role, User.UserStatus status, Pageable pageable);

    List<UserResponse> getUsersByCity(String city);

    List<UserResponse> getUsersByCountry(String country);

    long countUsersByStatus(User.UserStatus status);

    long countUsersByRole(User.UserRole role);
}
