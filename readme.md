## 1. Project Overview

This project is a travel agency application built with Java and Spring Boot. The application consists of several microservices:
* **user-service**: manages user profiles, persons, and authentication
* **tour-service**: manages tours, tour details, tour capacity, activities and trips
* **booking-service**: handles booking creation, price calculation, booking state changes and communication with other services
* **accommodation-service**: manages accommodations and reservations, calculates accommodation prices and checks availability

## 2. Build and run

### Required Technologies:
* Java 17
* Maven
* PostgreSQL (via Docker)
* Kafka (via Docker)
* Docker & Docker Compose

### Infrastructure Setup (Docker)
Before starting the microservices, you need to start the infrastructure. The project includes a configured `docker-compose.yml` file, which contains the database, Kafka, Schema Registry, and monitoring tools.

1. Make sure Docker Desktop is running.
2. Run the following command from the root directory of the project:
   ```bash
   docker-compose up -d
   ```

This will start:
* **PostgreSQL**: available on port `5433`
* **Kafka**: broker available on port `9092`
* **Schema Registry**: available on port `8081`, used for Avro serialization
* **AKHQ**: GUI for managing Kafka, available at http://localhost:8080

### Database Migration
The project uses Flyway. Tables and initial data will be created automatically when each microservice starts.

### Build and Run Services
1. Build the entire project:
   ```bash
   mvn clean install
   ```
2. **Pořadí spouštění (IntelliJ IDEA Quick Run)**:
   Postupujte podle pořadí v README:
    * `DiscoveryServerApplication`
    * `ApiGatewayApplication`
    * Všechny ostatní (`UserServiceApplication`, `TourServiceApplication`,`AccommodationServiceApplication`,`BookingServiceApplication` )


### Service Discovery
The Eureka dashboard is available at: http://localhost:8761

**Service Ports:**
* `discovery-server`: 8761
* `api-gateway`: 8765
* `user-service`: 8087
* `tour-service`: 8083
* `booking-service`: 8082
* `accommodation-service`: 8084
* `AKHQ` (Kafka GUI): 8080
* `PostgreSQL`: 5433

## 3. Database initialization

The project uses PostgreSQL. Each service has its own database:
* `user_db`
* `tour_db`
* `accommodation_db`
* `booking_db`

All databases run within a single PostgreSQL container started via Docker Compose.

### Step-by-step instructions:
1. Start Docker
2. Start the services

   **Important**: Data initialization, meaning the creation of tables and filling them with test records, is performed automatically by Flyway immediately after each service starts. You do not need to run SQL scripts manually.

3. **Connect the database in IntelliJ IDEA**
   To view the data directly in the IDE, follow these steps:
    * Open the **Database** tab, usually on the right, and click `+` → `Data Source` → `PostgreSQL`.
    * Fill in the connection parameters:
        * **Host**: `localhost`
        * **Port**: `5433`
        * **User**: `postgres`
        * **Password**: `123456`
    * In the **Database** field, enter the name of the specific database: `user_db`, `accommodation_db`, `tour_db`, or `booking_db`.
    * Click **Test Connection**, then click **OK**. Now you can see the tables and data inserted by Flyway.

## 4. Endpoints to call
Each database is pre-initialized with 10 records (via Flyway). Therefore, when testing endpoints that require an ID (e.g., `{id}`), you can safely use any value from 1 to 10.

Detailed request examples, including request bodies, headers and Basic Auth configuration, are stored in the Postman collection. Collection is stored in the directory `Postman`. All endpoints are called through API Gateway at `http://localhost:8765`.

### Accommodation
| Action | Method | Endpoint |
| :--- | :--- | :--- |
| Create accommodation | POST | http://localhost:8765/accommodations |
| Delete accommodation | DELETE | http://localhost:8765/accommodations/{id} |
| Calculate price | POST | http://localhost:8765/reservations/calculate-price |
| Create reservations | POST | http://localhost:8765/reservations?bookingId={bookingId} |

### Booking
| Action | Method | Endpoint |
| :--- | :--- | :--- |
| Get booking by ID | GET | http://localhost:8765/api/bookings/{id} |
| Create booking | POST | http://localhost:8765/api/bookings |
| Get bookings between dates | GET | http://localhost:8765/api/bookings?from=2026-01-01&to=2026-12-31 |
| Get bookings by user ID | GET | http://localhost:8765/api/bookings/user/{userId} |
| Cancel booking by user | DELETE | http://localhost:8765/api/bookings/user/{id} |

### Tour
| Action | Method | Endpoint |
| :--- | :--- | :--- |
| Get tour by ID | GET | http://localhost:8765/tours/{id} |
| Get tours by date | GET | http://localhost:8765/tours/date?startDate=2027-06-01&endDate=2027-12-31 |
| Create tour | POST | http://localhost:8765/tours |
| Delete tour | DELETE | http://localhost:8765/tours/{id} |

### User
| Action | Method | Endpoint |
| :--- | :--- | :--- |
| Find or create persons | POST | http://localhost:8765/users/find-or-create |

## 5. Use cases

* **UC1: Create Accommodation**
    * **Description**: administrator can create a new accommodation in the system.
    * **Endpoint**: `POST http://localhost:8765/accommodations`
    * **Implemented in code**: `AccommodationService`
* **UC2: Calculate Price**
    * **Description**: user can calculate the total price of accommodation based on selected dates and reservation details.
    * **Endpoint**: `POST http://localhost:8765/reservations/calculate-price`
    * **Implemented in code**: `AccommodationService`
* **UC3: Create Booking**
    * **Description**: user can create a new booking for selected accommodation and tour.
    * **Endpoint**: `POST http://localhost:8765/api/bookings`
    * **Implemented in code**: `BookingService`
* **UC4: Get Booking by ID**
    * **Description**: user or administrator can get detailed information about a specific booking using its ID.
    * **Endpoint**: `GET http://localhost:8765/api/bookings/{id}`
    * **Implemented in code**: `BookingService`
* **UC5: Delete Booking**
    * **Description**: user can cancel or delete an existing booking.
    * **Endpoint**: `DELETE http://localhost:8765/api/bookings/user/{id}`
    * **Implemented in code**: `BookingService`
* **UC6: Delete Tour**
    * **Description**: administrator can delete an existing tour from the system
    * **Endpoint**: `DELETE http://localhost:8765/tours/{id}`
    * **Implemented in code**: `TourService`
* **UC7: Find or Create Person**
    * **Description**: the system can find an existing person by provided user data or create a new person if they do not already exist.
    * **Endpoint**: `POST http://localhost:8765/users/find-or-create`
    * **Implemented in code**: `UserService`

## 6. Cache
The project uses passive caching in the Tour service. Cache is not filled automatically in advance.

**GET endpoints using cache:**
* `GET http://localhost:8765/tours/{id}`
* `GET http://localhost:8765/tours/date?startDate={startDate}&endDate={endDate}`

## 7. Kafka
Pro monitoring Kafky se používá AKHQ UI, které je dostupné na adrese http://localhost:8080. Tento nástroj slouží ke kontrole topiců a zpráv v rámci vývoje.

| Flow | Producer | Topic | Consumer | How to trigger |
| :--- | :--- | :--- | :--- | :--- |
| Booking creation | booking-service | tour-capacity | tour-service | `POST http://localhost:8765/api/bookings` |
| Booking cancellation | booking-service | tour-capacity | tour-service | `DELETE http://localhost:8765/api/bookings/user/{id}` |
| Booking cancellation | booking-service | booking-cancelled | accommodation-service | `DELETE http://localhost:8765/api/bookings/user/{id}` |
| Tour cancellation | tour-service | tour-cancelled | booking-service | `DELETE http://localhost:8765/tours/{id}` |
| Accommodation cancellation | accommodation-service | accommodation-cancel | booking-service | `DELETE http://localhost:8765/accommodations/{id}` |

## 8. Design patterns

| Pattern | Classes | Purpose |
| :--- | :--- | :--- |
| Builder | `LoggerBuilder` | Step-by-step creation of log messages in API Gateway |
| State | `BookingState`, `CreatedState`, `PaidState`, `CancelledState` | Managing the lifecycle and statuses of bookings |
| Strategy | `DiscountStrategy`, `EarlyBookingDiscountStrategy`, `LastMinuteDiscountStrategy`, `NoDiscountStrategy` | Calculation of different types of discounts for bookings |
| Factory | `BookingStateFactory` | Creating state instances based on the booking status |
| Facade | `BookingManagerFacade` | Manages the booking workflow |

## 9. Security
Security is used to protect REST endpoints by roles and to forward the `Authorization` header between services through a Feign interceptor.

The project uses Spring Security with HTTP Basic Authentication.

**Roles:**
* **Customer** - Username: `customer` | Password: `password`
* **Admin** - Username: `admin` | Password: `admin`

Security configuration is implemented in `SecurityConfig` classes in the microservices.

## 10. Elasticsearch
Elasticsearch is not implemented.

## 11. Interceptor
* **Type**: Feign `RequestInterceptor` and Spring Cloud Gateway `GlobalFilter`.
* **Location**:
    * `cz.cvut.fel.nss.booking.config.SecurityConfig` (Feign)
    * `cz.cvut.fel.nss.apigateway.interceptor.LoggerInterceptor` (Gateway)
* **Purpose**:
    * **Feign interceptor**: Automatically forwards the `Authorization` header from the incoming request to downstream microservice calls.
    * **Logger interceptor**: Logs incoming request details (URI, Method, Headers) and outgoing response status codes in the API Gateway using the Builder pattern (`LoggerBuilder`).
