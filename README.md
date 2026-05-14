# Eagle Bank API (Spring Boot)

A contract-first REST API implementation for the Eagle Bank take-home task using Java 17 and Spring Boot.

## What is implemented

- `POST /v1/auth/login` for JWT authentication.
- User endpoints:
  - `POST /v1/users`
  - `GET /v1/users/{userId}`
  - `PATCH /v1/users/{userId}`
  - `DELETE /v1/users/{userId}`
- Account endpoints:
  - `POST /v1/accounts`
  - `GET /v1/accounts`
  - `GET /v1/accounts/{accountNumber}`
  - `PATCH /v1/accounts/{accountNumber}`
  - `DELETE /v1/accounts/{accountNumber}`
- Transaction endpoints:
  - `POST /v1/accounts/{accountNumber}/transactions`
  - `GET /v1/accounts/{accountNumber}/transactions`
  - `GET /v1/accounts/{accountNumber}/transactions/{transactionId}`

The implementation follows `openapi.yaml` and adds an auth endpoint in that same file.

## Tech stack

- Java 17
- Spring Boot 3
- Spring Security + JWT (`jjwt`)
- Jakarta Bean Validation
- JUnit 5 + MockMvc
- In-memory repositories (replaceable with persistent repositories)

## Project structure

- `src/main/java/com/eaglebank/common` - error model and global exception handling
- `src/main/java/com/eaglebank/security` - JWT auth filter/config/current-user helper
- `src/main/java/com/eaglebank/modules/auth` - authentication API and service
- `src/main/java/com/eaglebank/modules/users` - user API/service/repository/model
- `src/main/java/com/eaglebank/modules/accounts` - account API/service/repository/model
- `src/main/java/com/eaglebank/modules/transactions` - transaction API/service/repository/model
- `src/test/java/com/eaglebank` - integration tests

## Run locally

```bash
mvn spring-boot:run
```

## Run tests

```bash
mvn test
```

## Security notes

- JWT is required for all endpoints except creating a user and login.
- Ownership is enforced in service layer (user/account/transaction isolation).
- Passwords are hashed using BCrypt before storage.
- JWT secret defaults from `application.yml` and should be overridden in production based on Secrets Manager.

## Future improvements
- Add persistent storage (e.g. PostgreSQL) and JPA repositories.
- Implement refresh tokens for better security.
- Add more comprehensive validation and error handling.
- Add more tests, especially for edge cases and error scenarios.
- Implement rate limiting and other security best practices for production readiness.
- Implement role-based access control (e.g. admin vs regular users) if needed in the future.
- Add logging and monitoring for better observability in production.
- Implement transaction rollbacks and error handling for failed transactions to ensure data integrity.
