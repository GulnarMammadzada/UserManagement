package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserEventDTO;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.kafka.UserEventProducer;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .role(request.getRole())
                .status(request.getStatus() != null ? request.getStatus() : User.UserStatus.ACTIVE)
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        userEventProducer.sendUserEvent(createUserEvent(
                savedUser,
                UserEventDTO.EventType.USER_CREATED.name()
        ));

        return UserResponse.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<User> userPage = userRepository.findAll(pageable);
        return buildPageResponse(userPage);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setPostalCode(request.getPostalCode());
        user.setRole(request.getRole());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setBio(request.getBio());
        user.setAvatarUrl(request.getAvatarUrl());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        userEventProducer.sendUserEvent(createUserEvent(
                updatedUser,
                UserEventDTO.EventType.USER_UPDATED.name()
        ));

        return UserResponse.fromEntity(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);

        userEventProducer.sendUserEvent(createUserEvent(
                user,
                UserEventDTO.EventType.USER_DELETED.name()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching users with term: {}", searchTerm);
        Page<User> userPage = userRepository.searchUsers(searchTerm, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersByRole(User.UserRole role, Pageable pageable) {
        log.info("Fetching users with role: {}", role);
        Page<User> userPage = userRepository.findByRole(role, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersByStatus(User.UserStatus status, Pageable pageable) {
        log.info("Fetching users with status: {}", status);
        Page<User> userPage = userRepository.findByStatus(status, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsersByRoleAndStatus(
            User.UserRole role,
            User.UserStatus status,
            Pageable pageable
    ) {
        log.info("Fetching users with role: {} and status: {}", role, status);
        Page<User> userPage = userRepository.findByRoleAndStatus(role, status, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByCity(String city) {
        log.info("Fetching users from city: {}", city);
        return userRepository.findByCity(city)
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByCountry(String country) {
        log.info("Fetching users from country: {}", country);
        return userRepository.findByCountry(country)
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }

    private PageResponse<UserResponse> buildPageResponse(Page<User> userPage) {
        List<UserResponse> userResponses = userPage.getContent()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(userResponses)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .empty(userPage.isEmpty())
                .build();
    }

    private UserEventDTO createUserEvent(User user, String eventType) {
        return UserEventDTO.builder()
                .eventType(eventType)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .performedBy("system")
                .build();
    }
}
