package com.example.usermanagement.repository;

import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phone("+1234567890")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .city("New York")
                .country("USA")
                .build();

        userRepository.save(testUser);
    }

    @Test
    void findByEmail_Success() {
        var result = userRepository.findByEmail("john.doe@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void existsByEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("john.doe@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void findByRole_Success() {
        var users = userRepository.findByRole(User.UserRole.USER);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getRole()).isEqualTo(User.UserRole.USER);
    }

    @Test
    void findByStatus_Success() {
        var users = userRepository.findByStatus(User.UserStatus.ACTIVE);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getStatus()).isEqualTo(User.UserStatus.ACTIVE);
    }

    @Test
    void searchUsers_Success() {
        Page<User> result = userRepository.searchUsers("john", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void countByStatus_Success() {
        long count = userRepository.countByStatus(User.UserStatus.ACTIVE);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByRole_Success() {
        long count = userRepository.countByRole(User.UserRole.USER);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void findByCity_Success() {
        var users = userRepository.findByCity("New York");

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getCity()).isEqualTo("New York");
    }

    @Test
    void findByCountry_Success() {
        var users = userRepository.findByCountry("USA");

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getCountry()).isEqualTo("USA");
    }
}
