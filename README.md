# pma.261.auth

Feign client library for the `auth-service`. Other services import this artifact to communicate with the authentication service via HTTP without writing boilerplate REST code.

## Overview

The `auth` module exposes a `@FeignClient` interface (`AuthController`) that maps each auth endpoint to a typed Java method. Any Spring Boot service that needs to authenticate users, resolve JWT tokens, or query account identity can depend on this library instead of calling the auth service manually.

## Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.x + Spring Cloud OpenFeign |
| Utilities | Lombok |

## Endpoints

The client targets `http://auth:8080` (the `auth-service` container on the internal Docker network).

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/login` | Authenticate with email + password. Returns a `Set-Cookie` header with the JWT. |
| `POST` | `/auth/register` | Create a new account. Returns `201 Created`. |
| `GET` | `/auth/whoiam` | Retrieve account details for a given account ID. |
| `POST` | `/auth/solve` | Validate a JWT token and return the associated `idAccount`. |
| `GET` | `/auth/logout` | Clear the auth cookie (sets `maxAge=0`). |
| `GET` | `/auth/health-check` | Liveness probe. Returns `200 OK`. |

## Request / Response Models

### `LoginIn`
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

### `RegisterIn`
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "secret"
}
```

### `TokenOut`
Used as the request body for `/auth/solve`:
```json
{
  "token": "<jwt>"
}
```
The response is a map:
```json
{
  "idAccount": "<uuid>"
}
```

## JWT Cookie

After a successful login, the auth service sets an `HttpOnly`, `Secure`, `SameSite=None` cookie named `__store_jwt_token` that is valid for **24 hours**. The cookie is cleared automatically on logout by setting `maxAge=0`.

The constant `AuthController.AUTH_COOKIE_TOKEN` holds the cookie name and can be used in other services to read or clear the cookie without hardcoding the string.

## Usage

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>store</groupId>
    <artifactId>auth</artifactId>
    <version>1.0.0</version>
</dependency>
```

Enable Feign clients in your Spring Boot application:

```java
@EnableFeignClients(basePackages = "store.auth")
@SpringBootApplication
public class YourApplication { ... }
```

Inject and use:

```java
@Autowired
private AuthController authController;

// Resolve a token to an account ID
ResponseEntity<Map<String, String>> result = authController.solveToken(
    TokenOut.builder().token(jwt).build()
);
String idAccount = result.getBody().get("idAccount");
```

## Build

```bash
mvn clean install
```

The artifact is installed to the local Maven repository and can then be consumed by other services in this monorepo.
