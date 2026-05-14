# Payroll System API

Java 21 Spring Boot payroll API for managing employees, work shifts, shift assignments, and payroll records.

## Requirements

- Java 21
- Internet access the first time Maven Wrapper downloads Maven

## Run the Project

Use the Maven wrapper included in the repository.

### Windows PowerShell

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

### Windows Command Prompt

```bat
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### macOS / Linux

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The default API port is `8080`.

## Run Tests

### Windows PowerShell

```powershell
.\mvnw.cmd test
```

### macOS / Linux

```bash
./mvnw test
```

Tests use an isolated in-memory H2 database configuration from `src/test/resources/application.properties`.

## Profiles

### Shared config

`src/main/resources/application.properties`

Contains only shared application settings and environment variable bindings.

### Development

`src/main/resources/application-dev.properties`

Use for local development:

- SQL logging enabled
- Hibernate schema validation enabled
- Flyway migrations enabled
- Application log level set to debug

### Production

`src/main/resources/application-prod.properties`

Use for deployed environments:

- SQL logging disabled
- Hibernate schema validation enabled
- Flyway migrations enabled
- Safer log levels

## Local Overrides

You can create a local untracked override file at:

`src/main/resources/application-local.properties`

Typical use cases:

- local database URL
- local JWT secret for legacy HS256 setups
- local Supabase URL for JWKS-based verification
- machine-specific overrides

This file is gitignored and should not contain shared team configuration.

## Environment Variables

- `PORT`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SUPABASE_URL`
- `SUPABASE_BUCKET`
- `SUPABASE_API_KEY`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `APP_SECURITY_JWT_ISSUER_URI`
- `APP_SECURITY_JWT_JWK_SET_URI`
- `APP_SECURITY_ALLOWED_ORIGINS`

## Security

The API now expects Bearer JWT tokens for protected `/api/**` routes.

### Access rules

- `GET /api/workshifts/**` requires `ADMIN` or `EMPLOYEE`
- `GET /api/employees` requires `ADMIN`
- `GET /api/payrolls` requires `ADMIN`
- `GET /api/shift-assignments` requires `ADMIN`
- `GET /api/employees/me` returns the authenticated employee's own profile
- `GET /api/payrolls/me` returns the authenticated employee's own payrolls
- `GET /api/shift-assignments/me` returns the authenticated employee's own shift assignments
- `POST /api/**` requires `ADMIN`
- `PUT /api/**` requires `ADMIN`
- `DELETE /api/**` requires `ADMIN`

### JWT expectations

The API supports two verification modes:

1. Supabase JWKS verification.
   If `SUPABASE_URL` is configured, the backend automatically verifies user JWTs against:
   `https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json`
   and expects the issuer:
   `https://<project-ref>.supabase.co/auth/v1`
2. Legacy shared-secret verification.
   If no JWKS configuration is available, the API falls back to `JWT_SECRET` and verifies HS256 tokens.

You can override the defaults explicitly with:

- `APP_SECURITY_JWT_ISSUER_URI`
- `APP_SECURITY_JWT_JWK_SET_URI`

The API reads role information from either:

- `role`
- `roles`
- `employeeId` for employee-scoped access checks
- `email` as a fallback lookup when `employeeId` is not present

Examples:

```json
{
  "sub": "user-123",
  "role": "ADMIN"
}
```

```json
{
  "sub": "user-456",
  "roles": ["EMPLOYEE"],
  "employeeId": 42
}
```

Spring Security converts those values into authorities like `ROLE_ADMIN` and `ROLE_EMPLOYEE`.
For employee-scoped access, the backend first tries `employeeId`. If it is missing, it falls back to the user's `email` claim and maps that to the matching employee record in the payroll database.

### Auth flow for real use

This project does not expose a login endpoint yet because the current employee model does not store passwords or external identity references.

Recommended production flow:

1. Authenticate users in a dedicated identity provider or auth service.
2. When using Supabase Auth, let Supabase issue the access token and send it to this API as a Bearer token.
3. Include the employee's application role in `role` or `roles`.
4. Prefer including the employee's internal record id in `employeeId`.
5. If `employeeId` is not included, make sure the Supabase user's email matches the employee email stored in this API database.
6. Send the token as `Authorization: Bearer <token>` when calling the payroll API.

Notes for Supabase:

- A real Supabase user access token commonly contains `email`, `sub`, `iss`, `aud`, and `role`.
- Default Supabase user tokens often use `role: "authenticated"`, which is enough for the authenticated `/me` endpoints in this project.
- If you want an employee to call endpoints guarded by `hasRole("EMPLOYEE")`, you must add that application role in the JWT claims or adjust the authorization rules.

If you later add a real login flow to this project, the next safe step is still to keep Supabase as the identity provider and map its user identity to your employee records instead of issuing tokens directly from controller code.

## Data Integrity

This project now uses a small set of database guardrails to prevent invalid data:

- employee email must be unique
- one payroll per employee per month/year
- one shift assignment per employee per date
- important columns such as salary, role, payroll status, and processed date are required

Validation is also applied at request level:

- `baseSalary` must be greater than `0`
- `extraHourRate` must be greater than or equal to `0`
- `startTime` must be before `endTime`
- ids in request payloads must be positive

The goal is to catch mistakes early in the API layer and still let the database enforce the final safety net.

## Notes on Maven Wrapper

`mvnw` and `mvnw.cmd` are the standard Maven Wrapper scripts generated for the project. They should normally not be edited manually unless the wrapper itself is corrupted or intentionally upgraded.
