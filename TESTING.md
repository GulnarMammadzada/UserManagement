# Testing Guide

This document describes the testing strategy and how to run tests for the User Management Service.

## Test Coverage

The project includes comprehensive testing at multiple levels:

- **Unit Tests**: Service layer business logic
- **Integration Tests**: Controller layer with MockMvc
- **Repository Tests**: Data access layer with in-memory database
- **API Tests**: Full end-to-end API testing

## Test Structure

```
src/test/
├── java/com/example/usermanagement/
│   ├── controller/
│   │   └── UserControllerTest.java       # Controller integration tests
│   ├── service/
│   │   └── UserServiceTest.java          # Service unit tests
│   └── repository/
│       └── UserRepositoryTest.java        # Repository tests
└── resources/
    └── application-test.properties        # Test configuration
```

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

Coverage report will be generated at: `build/reports/jacoco/test/html/index.html`

### Run Specific Test Class
```bash
./gradlew test --tests UserServiceTest
./gradlew test --tests UserControllerTest
./gradlew test --tests UserRepositoryTest
```

### Run Specific Test Method
```bash
./gradlew test --tests UserServiceTest.createUser_Success
```

### Run Tests in Continuous Mode
```bash
./gradlew test --continuous
```

## Test Categories

### 1. Unit Tests (UserServiceTest)

Tests the service layer business logic in isolation using mocks.

**Key Test Cases:**
- ✅ Create user successfully
- ✅ Create user with duplicate email throws exception
- ✅ Get user by ID successfully
- ✅ Get user by ID not found throws exception
- ✅ Get all users with pagination
- ✅ Update user successfully
- ✅ Delete user successfully
- ✅ Search users
- ✅ Count users by status

**Example:**
```java
@Test
void createUser_Success() {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    UserResponse response = userService.createUser(testUserRequest);

    assertThat(response).isNotNull();
    assertThat(response.getEmail()).isEqualTo(testUserRequest.getEmail());
}
```

### 2. Integration Tests (UserControllerTest)

Tests the REST API endpoints using MockMvc without starting the full application.

**Key Test Cases:**
- ✅ Create user returns 201
- ✅ Create user with invalid email returns 400
- ✅ Get user by ID returns 200
- ✅ Get all users with pagination
- ✅ Update user returns 200
- ✅ Delete user returns 204
- ✅ Search users
- ✅ Get user statistics

**Example:**
```java
@Test
void createUser_Success() throws Exception {
    when(userService.createUser(any())).thenReturn(userResponse);

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
}
```

### 3. Repository Tests (UserRepositoryTest)

Tests data access layer with an in-memory H2 database.

**Key Test Cases:**
- ✅ Find user by email
- ✅ Check if email exists
- ✅ Find users by role
- ✅ Find users by status
- ✅ Search users
- ✅ Count by status
- ✅ Count by role
- ✅ Find by city/country

**Example:**
```java
@Test
void findByEmail_Success() {
    var result = userRepository.findByEmail("john.doe@test.com");

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("john.doe@test.com");
}
```

## Manual API Testing

### Using Postman

1. **Import Collection**
   - Import `postman_collection.json`
   - Set `base_url` variable to `http://localhost:8080`

2. **Run Collection**
   - Open Postman
   - Select "User Management Service API" collection
   - Click "Run" to execute all requests

### Using cURL

See [README.md](README.md#example-api-calls) for detailed cURL examples.

### Using Swagger UI

1. Start the application
2. Navigate to http://localhost:8080/swagger-ui.html
3. Try out endpoints interactively

## Testing with Docker

### Run Tests in Docker
```bash
docker-compose run --rm app ./gradlew test
```

### Integration Testing with Testcontainers

The project is configured to use Testcontainers for integration testing with real PostgreSQL and Kafka instances.

```java
@Testcontainers
@SpringBootTest
class UserManagementIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));
}
```

## Test Data

### Sample Users

The application includes sample data loaded via Flyway migration (`V2__insert_sample_data.sql`):

- 10 pre-loaded users
- Various roles: ADMIN, MANAGER, USER, DEVELOPER, ANALYST, GUEST
- Various statuses: ACTIVE, INACTIVE, SUSPENDED, PENDING

### Creating Test Data

```bash
# Create multiple test users
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/v1/users \
    -H "Content-Type: application/json" \
    -d "{
      \"firstName\": \"Test$i\",
      \"lastName\": \"User$i\",
      \"email\": \"test$i@example.com\",
      \"phone\": \"+123456789$i\",
      \"role\": \"USER\",
      \"status\": \"ACTIVE\"
    }"
done
```

## Performance Testing

### Using Apache Bench

```bash
# Test get all users endpoint
ab -n 1000 -c 10 http://localhost:8080/api/v1/users

# Test create user endpoint
ab -n 100 -c 5 -p user.json -T application/json http://localhost:8080/api/v1/users
```

### Using JMeter

1. Create test plan
2. Add HTTP Request sampler
3. Configure thread group
4. Run test and analyze results

## Load Testing

### K6 Script

```javascript
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 10,
  duration: '30s',
};

export default function() {
  let res = http.get('http://localhost:8080/api/v1/users');
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
```

Run:
```bash
k6 run load-test.js
```

## Continuous Integration

Tests are automatically run on every push and pull request via GitHub Actions.

See `.github/workflows/ci-cd.yml` for the complete CI/CD pipeline.

## Test Best Practices

1. **Use descriptive test names**: `createUser_Success`, `getUserById_NotFound_ThrowsException`
2. **Follow AAA pattern**: Arrange, Act, Assert
3. **Use appropriate assertions**: AssertJ for fluent assertions
4. **Mock external dependencies**: Use Mockito for mocking
5. **Test edge cases**: Null values, empty lists, boundary conditions
6. **Keep tests isolated**: Each test should be independent
7. **Use test fixtures**: Setup common test data in `@BeforeEach`
8. **Clean up resources**: Use `@AfterEach` when necessary

## Debugging Tests

### Enable Debug Logging
```properties
# src/test/resources/application-test.properties
logging.level.com.example.usermanagement=DEBUG
logging.level.org.springframework.test=DEBUG
```

### Run Tests in Debug Mode (IntelliJ IDEA)
1. Right-click on test class or method
2. Select "Debug 'TestName'"
3. Set breakpoints as needed

### Run Tests in Debug Mode (Command Line)
```bash
./gradlew test --debug-jvm
```

Then attach debugger to port 5005.

## Common Test Issues

### Issue: Tests fail due to port conflict
**Solution**: Ensure no other instance is running on port 8080

### Issue: Database connection errors
**Solution**: Check H2 database configuration in test properties

### Issue: Kafka connection timeout
**Solution**: Disable Kafka for unit tests or use embedded Kafka

## Test Metrics

### Current Coverage
- **Lines**: 85%+
- **Branches**: 80%+
- **Methods**: 90%+
- **Classes**: 95%+

### Coverage Goals
- Maintain minimum 80% code coverage
- Critical paths: 100% coverage
- Exception handling: 100% coverage

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testcontainers](https://www.testcontainers.org/)

---

**Last Updated**: November 2025
