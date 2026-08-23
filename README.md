# GreenRide

GreenRide is a Spring Boot carpooling application developed for the
Distributed Systems course.

The application allows users to act both as drivers and passengers,
create and search rides, book available seats, cancel bookings and
rate other participants after a completed ride.

Administrators can view system statistics and manage malicious users.

---

## Technologies

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT Authentication
- Thymeleaf
- H2 Database
- Hibernate
- Bean Validation
- OpenAPI / Swagger UI
- Open-Meteo Geocoding REST API
- Maven
- JUnit 5
- Mockito

---

## Architecture

The project follows a layered architecture:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

For the external geocoding service a Port / Adapter approach is used:

```text
RouteInfoService
       |
       v
GeocodingPort
       |
       v
OpenMeteoGeocodingAdapter
       |
       v
Open-Meteo REST API
```

This keeps the core application independent from the external
provider.

---

## User Roles

### USER

A normal user can act as both driver and passenger.

Available functionality:

- Register
- Login / logout
- Create rides
- Search available rides
- View available rides
- Book a seat
- Cancel a booking
- View created rides
- View personal bookings
- View ride route information
- Rate drivers
- Rate passengers

### ADMIN

An administrator can additionally:

- View all users
- Block users
- Unblock users
- View total number of users
- View total number of rides
- View total number of bookings
- View average ride occupancy

---

## Business Rules

GreenRide implements the following rules:

- A ride must have a future departure time.
- A ride must have at least one available seat.
- A ride may contain at most 8 passenger seats.
- A driver may have at most 3 active future rides.
- A driver cannot book their own ride.
- A passenger cannot book the same ride twice.
- A ride cannot be booked after departure.
- A booking cannot exceed the available ride capacity.
- Booking creation uses database locking to reduce the risk of
  concurrent overbooking.
- A passenger may cancel only their own booking.
- Cancellation is not allowed 10 minutes or less before departure.
- Ratings are allowed only after a ride has taken place.
- A user cannot rate themselves.
- Ratings are allowed only between the driver and passengers who
  participated in the same ride.
- Duplicate ratings for the same ride and user pair are rejected.

---

## Authentication and Security

GreenRide uses two authentication mechanisms.

### Web UI

The Thymeleaf Web UI uses stateful Spring Security authentication.

Authentication state is stored using an HTTP session and cookie.

### REST API

The REST API uses stateless JWT authentication.

After login:

```text
POST /api/auth/login
```

the server returns a JWT.

Protected API requests use:

```text
Authorization: Bearer <JWT>
```

The JWT contains the authenticated user's email and role.

Administrative API endpoints require the `ADMIN` role.

---

## Database

The project currently uses an in-memory H2 database.

Configuration:

```properties
spring.datasource.url=jdbc:h2:mem:greenride
spring.datasource.username=sa
spring.datasource.password=
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Because the database is in-memory, its data is reset whenever the
application restarts.

---

## Running the Application

Requirements:

- Java 21
- Maven

Run using Maven:

```bash
./mvnw spring-boot:run
```

or run:

```text
GreenrideApplication
```

directly from IntelliJ IDEA.

The application starts at:

```text
http://localhost:8080
```

---

## Web UI

Main pages:

```text
/register
/login
/
/rides
/rides/create
/my-rides
/bookings
/ratings
/admin
```

---

## REST API

Main API groups:

```text
/api/auth
/api/rides
/api/bookings
/api/ratings
/api/admin
```

Examples:

### Register

```http
POST /api/auth/register
```

### Login

```http
POST /api/auth/login
```

### Available rides

```http
GET /api/rides/available
```

### Search rides

```http
GET /api/rides/search?origin=Athens&destination=Piraeus
```

### Create booking

```http
POST /api/bookings
```

### Route information

```http
GET /api/rides/{id}/route-info
```

### Admin statistics

```http
GET /api/admin/stats
```

---

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Protected endpoints can be tested through Swagger using the
`Authorize` button and a valid JWT.

---

## External REST Service

GreenRide consumes the Open-Meteo Geocoding API.

The external API converts location names such as:

```text
Athens
Piraeus
```

into geographical coordinates.

GreenRide then calculates the straight-line distance between origin
and destination using their latitude and longitude.

The displayed distance is geographical straight-line distance and
must not be interpreted as actual driving distance.

The external service is accessed through:

```text
GeocodingPort
```

and:

```text
OpenMeteoGeocodingAdapter
```

so that the external provider remains separated from the application's
business logic.

---

## Tests

The project contains automated unit tests for important business
rules.

Tests cover:

- Valid ride creation
- Maximum active rides
- Past ride rejection
- Booking creation
- Seat reduction after booking
- Booking past rides
- Cancellation cutoff
- Valid ratings
- Invalid ratings from users who did not participate in a ride

Run all tests with:

```bash
./mvnw test
```

---

## Main Project Structure

```text
src/main/java/gr/hua/dit/greenride
|
|-- config
|-- controller
|-- dto
|-- entity
|-- exception
|-- external
|-- repository
|-- security
|-- service
|
`-- GreenrideApplication.java
```

Web resources:

```text
src/main/resources
|
|-- templates
|-- static/css
`-- application.properties
```

Tests:

```text
src/test/java/gr/hua/dit/greenride
```

---

## GreenRide

University project for the Distributed Systems course.