# Repository Guidelines

## Project Structure & Module Organization

This is a Java 21 Spring Boot payroll API built with Maven. Application code lives under `src/main/java/com/myoffice/payroll_system`.

- `config/` contains security and application configuration.
- `controller/` exposes REST endpoints such as `/api/employees`.
- `dto/` defines request and response transfer objects.
- `entity/` contains JPA entities and enums.
- `repository/` contains Spring Data repositories.
- `service/` contains business logic.
- `exception/` contains custom exceptions and global handling.
- `src/main/resources/` holds Spring configuration.
- `src/test/java/com/myoffice/payroll_system/` mirrors production packages for tests.

Do not edit generated build output in `target/`.

## Build, Test, and Development Commands

Use the Maven wrapper so contributors run the same Maven version:

- `.\mvnw.cmd spring-boot:run` runs the API locally on the configured port, default `8080`.
- `.\mvnw.cmd test` runs the JUnit test suite.
- `.\mvnw.cmd clean package` compiles, tests, and builds the application artifact.

On Unix-like shells, use `./mvnw` instead of `.\mvnw.cmd`.

## Coding Style & Naming Conventions

Follow the existing Spring style: constructor injection with Lombok where already used, REST controllers ending in `Controller`, services ending in `Service`, repositories ending in `Repository`, DTO containers ending in `DTO`, and tests ending in `Test`.

Use 4-space indentation for production Java files. Keep controller methods thin; place business rules in services and persistence access in repositories. Prefer clear method names such as `getEmployeeById` and test names like `getEmployeeById_shouldReturnEmployeeById`.

## Testing Guidelines

Tests use JUnit 5, Mockito, and Spring Boot test dependencies. Add or update tests with behavior changes, especially in `service/` and `controller/`. Mirror package structure under `src/test/java`, and keep unit tests focused on one class or endpoint behavior at a time.

Run `.\mvnw.cmd test` before submitting changes.

## Commit & Pull Request Guidelines

Recent history uses Conventional Commit-style messages, for example `feat(controller): adds initial API endpoints for payroll entities` and `feat(test): adds unit tests for employee, payroll, and shift controllers`. Use `type(scope): short present-tense summary`.

Pull requests should include a concise description, the commands run for verification, linked issues when applicable, and notes for API or configuration changes.

## Security & Configuration Tips

`application.properties` supports environment variables such as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `SUPABASE_API_KEY`, and `JWT_SECRET`. Do not add new secrets to committed files; use local environment variables or an untracked local override.

## Agent-Specific Instructions

Preserve user changes already present in the working tree. Keep edits scoped to the requested task, and avoid broad refactors unless they are required for correctness.
