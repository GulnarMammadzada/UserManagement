# API Examples

Complete collection of example API calls for the User Management Service.

## Base URL

- **Local**: `http://localhost:8080`
- **Production**: `https://your-deployed-url.com`

## Authentication

Currently, the API does not require authentication. For production use, implement JWT or OAuth2.

## Table of Contents

- [Health Check](#health-check)
- [User Operations](#user-operations)
  - [Create User](#create-user)
  - [Get User by ID](#get-user-by-id)
  - [Get All Users](#get-all-users)
  - [Update User](#update-user)
  - [Delete User](#delete-user)
- [Search and Filter](#search-and-filter)
  - [Search Users](#search-users)
  - [Filter by Role](#filter-by-role)
  - [Filter by Status](#filter-by-status)
  - [Filter by City](#filter-by-city)
  - [Filter by Country](#filter-by-country)
- [Statistics](#statistics)
- [Error Examples](#error-examples)

## Health Check

### Check Service Health

```bash
curl http://localhost:8080/api/v1/health
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2025-11-17T10:30:45",
  "service": "User Management Service",
  "version": "1.0.0"
}
```

## User Operations

### Create User

Create a new user with all fields:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "address": "123 Main Street, Apt 4B",
    "city": "New York",
    "country": "USA",
    "postalCode": "10001",
    "role": "USER",
    "status": "ACTIVE",
    "bio": "Full Stack Software Engineer with 5 years of experience",
    "avatarUrl": "https://i.pravatar.cc/150?img=1"
  }'
```

**Response (201 Created):**
```json
{
  "id": 11,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "address": "123 Main Street, Apt 4B",
  "city": "New York",
  "country": "USA",
  "postalCode": "10001",
  "role": "USER",
  "status": "ACTIVE",
  "bio": "Full Stack Software Engineer with 5 years of experience",
  "avatarUrl": "https://i.pravatar.cc/150?img=1",
  "createdAt": "2025-11-17 10:30:45",
  "updatedAt": "2025-11-17 10:30:45",
  "lastLoginAt": null
}
```

### Create User (Minimal)

Create user with only required fields:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "role": "MANAGER"
  }'
```

### Get User by ID

```bash
curl http://localhost:8080/api/v1/users/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2025-11-17 08:00:00",
  "updatedAt": "2025-11-17 08:00:00"
}
```

### Get All Users

Get users with default pagination:

```bash
curl http://localhost:8080/api/v1/users
```

Get users with custom pagination and sorting:

```bash
curl "http://localhost:8080/api/v1/users?page=0&size=20&sortBy=email&sortDir=ASC"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "role": "ADMIN",
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane.smith@example.com",
      "role": "MANAGER",
      "status": "ACTIVE"
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 10,
  "totalPages": 1,
  "last": true,
  "first": true,
  "empty": false
}
```

### Update User

Update all user fields:

```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com",
    "phone": "+1987654321",
    "address": "456 Oak Avenue",
    "city": "Los Angeles",
    "country": "USA",
    "postalCode": "90001",
    "role": "ADMIN",
    "status": "ACTIVE",
    "bio": "Updated bio information",
    "avatarUrl": "https://i.pravatar.cc/150?img=2"
  }'
```

Update only specific fields:

```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "MANAGER"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "updatedAt": "2025-11-17 11:00:00"
}
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/api/v1/users/1
```

**Response (204 No Content)**

## Search and Filter

### Search Users

Search by name or email:

```bash
curl "http://localhost:8080/api/v1/users/search?q=john"
```

Search with pagination:

```bash
curl "http://localhost:8080/api/v1/users/search?q=developer&page=0&size=10&sortBy=createdAt&sortDir=DESC"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 4,
      "firstName": "John",
      "lastName": "Developer",
      "email": "john.dev@example.com",
      "bio": "Senior Developer"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1
}
```

### Filter by Role

Get all ADMIN users:

```bash
curl "http://localhost:8080/api/v1/users/filter/role/ADMIN?page=0&size=10"
```

Available roles:
- `ADMIN`
- `MANAGER`
- `USER`
- `GUEST`
- `DEVELOPER`
- `ANALYST`

### Filter by Status

Get all ACTIVE users:

```bash
curl "http://localhost:8080/api/v1/users/filter/status/ACTIVE?page=0&size=10"
```

Available statuses:
- `ACTIVE`
- `INACTIVE`
- `SUSPENDED`
- `PENDING`

### Filter by City

```bash
curl "http://localhost:8080/api/v1/users/filter/city/New%20York"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "city": "New York",
    "country": "USA"
  }
]
```

### Filter by Country

```bash
curl "http://localhost:8080/api/v1/users/filter/country/USA"
```

## Statistics

### Get User Statistics

```bash
curl http://localhost:8080/api/v1/users/stats
```

**Response (200 OK):**
```json
{
  "activeUsers": 50,
  "inactiveUsers": 10,
  "suspendedUsers": 5,
  "pendingUsers": 3,
  "admins": 2,
  "managers": 5,
  "regularUsers": 61
}
```

## Error Examples

### Validation Error (400)

Creating user with invalid email:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "invalid-email",
    "role": "USER"
  }'
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "timestamp": "2025-11-17 10:30:45",
  "validationErrors": [
    {
      "field": "email",
      "message": "Email must be valid"
    }
  ]
}
```

### User Not Found (404)

```bash
curl http://localhost:8080/api/v1/users/9999
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 9999",
  "path": "/api/v1/users/9999",
  "timestamp": "2025-11-17 10:30:45"
}
```

### Duplicate Email (409)

Creating user with existing email:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "USER"
  }'
```

**Response (409 Conflict):**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "User with email already exists: john.doe@example.com",
  "path": "/api/v1/users",
  "timestamp": "2025-11-17 10:30:45"
}
```

## Batch Operations Examples

### Create Multiple Users

```bash
#!/bin/bash
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/v1/users \
    -H "Content-Type: application/json" \
    -d "{
      \"firstName\": \"User$i\",
      \"lastName\": \"Test$i\",
      \"email\": \"user$i@example.com\",
      \"role\": \"USER\",
      \"status\": \"ACTIVE\"
    }"
  echo ""
done
```

### Export All Users

```bash
curl "http://localhost:8080/api/v1/users?size=1000" | jq > users_export.json
```

## Using with HTTPie

HTTPie provides a more user-friendly CLI:

### Create User
```bash
http POST localhost:8080/api/v1/users \
  firstName=John \
  lastName=Doe \
  email=john@example.com \
  role=USER
```

### Get User
```bash
http GET localhost:8080/api/v1/users/1
```

### Update User
```bash
http PUT localhost:8080/api/v1/users/1 \
  firstName=John \
  lastName=Smith \
  email=john.smith@example.com \
  role=MANAGER
```

## Using with JavaScript (Fetch API)

```javascript
// Create User
fetch('http://localhost:8080/api/v1/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    role: 'USER'
  })
})
.then(response => response.json())
.then(data => console.log(data));

// Get All Users
fetch('http://localhost:8080/api/v1/users?page=0&size=10')
  .then(response => response.json())
  .then(data => console.log(data));
```

## Using with Python (Requests)

```python
import requests

# Create User
user_data = {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "USER"
}

response = requests.post(
    'http://localhost:8080/api/v1/users',
    json=user_data
)
print(response.json())

# Get All Users
response = requests.get(
    'http://localhost:8080/api/v1/users',
    params={'page': 0, 'size': 10}
)
print(response.json())
```

---

**For interactive API testing, use Swagger UI**: http://localhost:8080/swagger-ui.html
