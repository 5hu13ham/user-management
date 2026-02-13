# User Management System

A full-stack user management application built with Spring Boot and React, featuring JWT-based authentication, role-based access control, and Dockerized deployment.

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT, Role-Based Access Control)
- Hibernate / JPA
- MySQL
- Maven

### Frontend (Work in Progress)
- React (Vite)
- TypeScript
- Functional Components & Hooks

### DevOps / Deployment
- Docker

---

## Key Features

- User authentication and authorization using JWT
- Role-based access control (Admin / User)
- Secure password hashing
- Global exception handling with standardized API responses
- Pagination and filtering for user listing
- Soft delete support
- CORS configuration for frontend integration
- Dockerized Spring Boot backend

---

## System Architecture Overview

This project follows a **production-grade layered architecture** and is designed to be **cloud-native and Kubernetes deployable**.

Architecture:
- REST-based microservice backend (Spring Boot)
- JWT-based stateless authentication
- Role-based access control (RBAC)
- Dockerized packaging
- Kubernetes-ready deployment model
- CI/CD-friendly structure

Flow:
Client → API Gateway / Load Balancer → Spring Boot Service → Database

The system is designed keeping **scalability, security, observability, and cloud deployment best practices** in mind.


---

## Backend Project Structure

src/
 ├── main/
 │   ├── java/
 │   │   └── in/trendsnag/usermanagement/
 │   │       ├── controller/
 │   │       ├── service/
 │   │       ├── repository/
 │   │       ├── entity/
 │   │       ├── dto/
 │   │       ├── security/
 │   │       ├── exception/
 │   │       └── UserManagementApplication.java
 │   └── resources/
 │       ├── application.yml
 │       └── application-dev.yml
 └── test/



---

## Frontend Status

Work in Progress

The frontend is located in:
 - user-management-frontend/


Current status:
- React + TypeScript setup using Vite
- Planned admin dashboard for user management
- Will consume backend APIs for authentication, pagination, filtering, and role management

> Backend is fully functional and can be tested independently using Postman.

---

## Running the Application Locally

### Prerequisites
- Java 17+
- Maven
- MySQL
- Docker (optional)

---

## Backend (Without Docker)
-
git clone https://github.com/5hu13HAM/user-management.git
cd user-management

Update database configuration in application.yml:
-
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_management
    username: root
    password: your_password

Run the application:
- mvn spring-boot:run

Backend will start at:
- http://localhost:8080


## Backend (With Docker)
Build Docker image:
- docker build -t user-management-backend .

Run Docker container:
- docker run -p 8080:8080 user-management-backend


## API Overview
Method	Endpoint	          Description
-------------------------------------------------
POST	/api/auth/login	  Authenticate user & get JWT
POST	/api/auth/register	  Register new user
GET	    /api/users	Get users   (pagination, filter)
GET	    /api/users/{id}	      Get user by ID
PUT	    /api/users/{id}	      Update user
DELETE	/api/users/{id}	      Soft delete user


## Authorization Header

Authorization: Bearer <JWT_TOKEN>

Security Highlights:

- Stateless JWT authentication
- Password hashing using BCrypt
- Role-based authorization via Spring Security
- Centralized exception handling
- Secure CORS configuration
- Angular/React admin dashboard


Author
Shubham Kumar
Backend Engineer | Java | Spring Boot | AWS | React
📍 India


---