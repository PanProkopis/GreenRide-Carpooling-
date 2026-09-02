# GreenRide

GreenRide is a Spring Boot carpooling application developed for a Distributed
Systems course. Users can offer and search for rides, book and cancel seats,
view route information, and rate participants after completed rides.

## Requirements

- Java 21
- No separate Maven installation is required; the Maven Wrapper is included.

## Run and Test

On Windows PowerShell:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

The application starts at `http://localhost:8080`. Stop it from the terminal
with `Ctrl+C`.

## Web UI

Main pages:

- Registration: `http://localhost:8080/register`
- Login: `http://localhost:8080/login`
- Dashboard: `http://localhost:8080/`
- Available rides: `http://localhost:8080/rides`
- Create ride: `http://localhost:8080/rides/create`
- My rides: `http://localhost:8080/my-rides`
- My bookings: `http://localhost:8080/bookings`
- Ratings: `http://localhost:8080/ratings`
- Administration: `http://localhost:8080/admin`

## Roles

- `USER`: registers and signs in, acts as a driver or passenger, creates and
  searches rides, books and cancels seats, views route information, and rates
  eligible participants.
- `ADMIN`: has the user capabilities and can also view system statistics and
  users, and block or unblock non-administrator accounts.

Public registration always creates a `USER` account.

## Initial Administrator

The initial administrator is created at startup only when an administrator
email and password are provided and the normalized email does not already
exist. The administrator password must contain between 8 and 72 characters.

Example for Windows PowerShell:

```powershell
$env:ADMIN_NAME="Administrator"
$env:ADMIN_EMAIL="admin@example.com"
$env:ADMIN_PASSWORD="replace-with-a-strong-password"
$env:JWT_SECRET="replace-with-a-long-random-secret"
.\mvnw.cmd spring-boot:run
```

These are example values. Use secure credentials and a long random JWT secret.
Secrets and passwords must never be committed to the repository.

## Authentication and Security

The Web UI uses stateful Spring Security form login. Authentication is retained
in an HTTP session and session cookie.

Requests under `/api/**` use stateless JWT authentication. Passwords for both
registered users and the initial administrator are hashed with BCrypt before
storage. To call a protected REST endpoint:

1. Register with `POST /api/auth/register`, if needed.
2. Sign in with `POST /api/auth/login` to receive a JWT.
3. Send the token with protected requests:

```http
Authorization: Bearer <token>
```

Administrative REST endpoints under `/api/admin/**` require the `ADMIN` role.

## REST API and Swagger

Swagger UI provides the available endpoints, request schemas, and an
`Authorize` button:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

The main API groups are:

- `/api/auth` for registration and login
- `/api/rides` for rides, searches, and route information
- `/api/bookings` for viewing, creating, and cancelling bookings
- `/api/ratings` for ratings
- `/api/admin` for administrator statistics and user management

Use Swagger to submit request bodies and inspect responses. For protected
operations, first call `/api/auth/login`, copy the returned token, select
`Authorize`, and enter the token as a bearer credential.

## Database

GreenRide uses an in-memory H2 database. Open the H2 console at:

```text
http://localhost:8080/h2-console
```

Use these connection details:

```text
JDBC URL: jdbc:h2:mem:greenride
Driver class: org.h2.Driver
User name: sa
Password: leave blank
```

Because the database is in memory, all application data is reset when the
application restarts.

## External Geocoding

GreenRide uses the Open-Meteo Geocoding API to resolve ride origin and
destination names to coordinates. The application then calculates the
straight-line geographical distance between them; this is not a driving-route
distance.
