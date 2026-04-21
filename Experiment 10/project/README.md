# 🍽️ Restaurant Table Booking System

A comprehensive restaurant table reservation system built with **Java (Spring Boot)** and **H2/MySQL Database**, designed following System Design principles.

---

## 📋 Table of Contents
- [System Architecture](#system-architecture)
- [ER Diagram](#er-diagram)
- [Technology Stack](#technology-stack)
- [Design Patterns](#design-patterns)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [How to Run](#how-to-run)
- [Features](#features)

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Browser)                         │
│               HTML5 / CSS3 / JavaScript                     │
│                  Single Page Application                    │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP (REST API)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER                          │
│              (REST Controllers)                             │
│   ┌──────────────────┐  ┌────────────────────────┐         │
│   │ TableController  │  │ ReservationController  │         │
│   └────────┬─────────┘  └───────────┬────────────┘         │
└────────────┼────────────────────────┼───────────────────────┘
             │                        │
             ▼                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   SERVICE LAYER                             │
│              (Business Logic)                               │
│   ┌──────────────────┐  ┌────────────────────────┐         │
│   │  TableService    │  │  ReservationService    │         │
│   └────────┬─────────┘  └───────────┬────────────┘         │
└────────────┼────────────────────────┼───────────────────────┘
             │                        │
             ▼                        ▼
┌─────────────────────────────────────────────────────────────┐
│                 REPOSITORY LAYER                            │
│           (Data Access / JPA Repositories)                  │
│   ┌────────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│   │ TableRepository│ │CustomerRepo  │ │ReservationRepo   │ │
│   └────────┬───────┘ └──────┬───────┘ └────────┬─────────┘ │
└────────────┼────────────────┼──────────────────┼────────────┘
             │                │                  │
             ▼                ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                   DATABASE LAYER                            │
│              H2 (Development) / MySQL (Production)          │
│   ┌──────────────────┐  ┌──────────┐  ┌──────────────────┐ │
│   │ restaurant_tables│  │customers │  │  reservations    │ │
│   └──────────────────┘  └──────────┘  └──────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 ER Diagram (Entity-Relationship)

```
┌──────────────────────┐       ┌──────────────────────────────┐
│     CUSTOMER         │       │       RESTAURANT_TABLE       │
├──────────────────────┤       ├──────────────────────────────┤
│ PK  id          LONG │       │ PK  id              LONG    │
│     name      STRING │       │     table_number     INT    │
│     email     STRING │       │     capacity         INT    │
│     phone     STRING │       │     location       STRING   │
└──────────┬───────────┘       │     is_active      BOOLEAN  │
           │                   └──────────┬───────────────────┘
           │  1                           │  1
           │                              │
           │  ╔═══════════════════════╗   │
           └──║    RESERVATION        ║───┘
         Many ╠═══════════════════════╣ Many
              ║ PK  id          LONG  ║
              ║ FK  customer_id LONG  ║
              ║ FK  table_id    LONG  ║
              ║     reservation_date  ║
              ║     reservation_time  ║
              ║     party_size   INT  ║
              ║     status     ENUM   ║
              ║     special_requests  ║
              ║     created_at        ║
              ║     updated_at        ║
              ╚═══════════════════════╝

Relationships:
  Customer    (1) ────< (Many) Reservation
  RestaurantTable (1) ────< (Many) Reservation
```

---

## 🛠️ Technology Stack

| Component       | Technology                    |
|----------------|-------------------------------|
| Language        | Java 17+                     |
| Framework       | Spring Boot 3.2.5            |
| ORM             | Spring Data JPA / Hibernate  |
| Database        | H2 (Dev) / MySQL (Prod)      |
| Build Tool      | Apache Maven 3.9+            |
| Frontend        | HTML5, CSS3, JavaScript      |
| API Style       | RESTful (JSON)               |
| Validation      | Jakarta Bean Validation      |

---

## 🎨 Design Patterns Used

| Pattern                | Where Used                                    |
|-----------------------|-----------------------------------------------|
| **MVC**               | Controller → Service → Repository → View     |
| **Repository Pattern**| JPA Repositories abstract data access         |
| **Service Layer**     | Business logic encapsulated in Service classes|
| **Dependency Injection** | Spring IoC container manages beans          |
| **Singleton**         | Spring beans are singleton by default         |
| **DTO Pattern**       | Request/Response payloads as Maps/Objects     |
| **Database Seeding**  | DataInitializer seeds sample data             |

---

## 📡 API Documentation

### Tables API

| Method | Endpoint                  | Description                |
|--------|--------------------------|----------------------------|
| GET    | `/api/tables`            | Get all active tables      |
| GET    | `/api/tables/all`        | Get all tables (admin)     |
| GET    | `/api/tables/available`  | Find available tables      |
| GET    | `/api/tables/{id}`       | Get table by ID            |
| POST   | `/api/tables`            | Create new table           |
| PUT    | `/api/tables/{id}`       | Update a table             |
| PATCH  | `/api/tables/{id}/toggle`| Toggle table active status |

### Reservations API

| Method | Endpoint                          | Description              |
|--------|----------------------------------|--------------------------|
| POST   | `/api/reservations`              | Create reservation       |
| GET    | `/api/reservations`              | Get all reservations     |
| GET    | `/api/reservations/{id}`         | Get reservation by ID    |
| GET    | `/api/reservations/search`       | Search by email          |
| GET    | `/api/reservations/date`         | Get by date              |
| PUT    | `/api/reservations/{id}/cancel`  | Cancel reservation       |

---

## 🗄️ Database Schema

```sql
CREATE TABLE customers (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(20) NOT NULL
);

CREATE TABLE restaurant_tables (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_number INT NOT NULL UNIQUE,
    capacity     INT NOT NULL,
    location     VARCHAR(50) NOT NULL,
    is_active    BOOLEAN DEFAULT TRUE
);

CREATE TABLE reservations (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id       BIGINT NOT NULL REFERENCES customers(id),
    table_id          BIGINT NOT NULL REFERENCES restaurant_tables(id),
    reservation_date  DATE NOT NULL,
    reservation_time  TIME NOT NULL,
    party_size        INT NOT NULL,
    status            VARCHAR(20) NOT NULL,
    special_requests  VARCHAR(500),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP
);
```

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.9+

### Steps
```bash
# 1. Clone/navigate to the project
cd restaurant-table-booking

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run

# 4. Open in browser
# http://localhost:8080
```

### Access H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/restaurant_booking`
- Username: `sa`
- Password: (empty)

---

## ✨ Features

1. **Browse Tables** — View all restaurant tables with location and capacity
2. **Check Availability** — Search available tables by date, time & party size
3. **Make Reservations** — Book a table with customer details
4. **My Reservations** — Look up bookings by email
5. **Cancel Bookings** — Cancel existing reservations
6. **Admin Dashboard** — View all reservations with statistics
7. **Conflict Detection** — Prevents double-booking with 2-hour time window
8. **Data Persistence** — H2 file-based database preserves data across restarts
9. **Responsive Design** — Works on desktop and mobile devices

---

## 👥 Team

| Role              | Component                |
|-------------------|--------------------------|
| System Design     | Architecture & ER Diagrams|
| Backend Developer | Spring Boot, JPA, REST API|
| Frontend Developer| HTML, CSS, JavaScript    |
| Database Admin    | Schema Design, H2/MySQL  |

---

*Built for System Design - Engineering*
