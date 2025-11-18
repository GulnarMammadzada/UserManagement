package com.example.usermanagement.controller;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("REST request to create user: {}", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id
    ) {
        log.info("REST request to get user by id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        log.info("REST request to get all users with pagination");
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        log.info("REST request to update user: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id
    ) {
        log.info("REST request to delete user: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Searches users by name or email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @Parameter(description = "Search term") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        log.info("REST request to search users with term: {}", q);
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        PageResponse<UserResponse> response = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/role/{role}")
    @Operation(summary = "Filter by role", description = "Retrieves users by role with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<PageResponse<UserResponse>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable User.UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("REST request to get users by role: {}", role);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        PageResponse<UserResponse> response = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/status/{status}")
    @Operation(summary = "Filter by status", description = "Retrieves users by status with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<PageResponse<UserResponse>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable User.UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("REST request to get users by status: {}", status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        PageResponse<UserResponse> response = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/city/{city}")
    @Operation(summary = "Filter by city", description = "Retrieves users by city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<List<UserResponse>> getUsersByCity(
            @Parameter(description = "City name") @PathVariable String city
    ) {
        log.info("REST request to get users by city: {}", city);
        List<UserResponse> response = userService.getUsersByCity(city);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter/country/{country}")
    @Operation(summary = "Filter by country", description = "Retrieves users by country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<List<UserResponse>> getUsersByCountry(
            @Parameter(description = "Country name") @PathVariable String country
    ) {
        log.info("REST request to get users by country: {}", country);
        List<UserResponse> response = userService.getUsersByCountry(country);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Retrieves statistics about users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("REST request to get user statistics");
        Map<String, Object> stats = Map.of(
                "activeUsers", userService.countUsersByStatus(User.UserStatus.ACTIVE),
                "inactiveUsers", userService.countUsersByStatus(User.UserStatus.INACTIVE),
                "suspendedUsers", userService.countUsersByStatus(User.UserStatus.SUSPENDED),
                "pendingUsers", userService.countUsersByStatus(User.UserStatus.PENDING),
                "admins", userService.countUsersByRole(User.UserRole.ADMIN),
                "managers", userService.countUsersByRole(User.UserRole.MANAGER),
                "regularUsers", userService.countUsersByRole(User.UserRole.USER)
        );
        return ResponseEntity.ok(stats);
    }
}
