# Phase 2–4 changes: one-line interview notes

| Step | Change | Purpose and how it fits | Concept |
|---|---|---|---|
| 1 | Replaced JPA/H2 with MongoDB documents and repositories | User and role data now lives in MongoDB collections rather than relational tables. | NoSQL, Document Model, Spring Data MongoDB |
| 2 | Updated user IDs to strings | MongoDB-generated identifiers travel through DTOs, controllers and frontend consistently. | ObjectId, API contract consistency |
| 3 | Implemented MongoTemplate-based filtering | Admins can search across usernames/names/emails with pagination and allowlisted sorting. | Query composition, Criteria, pagination |
| 4 | Added unique indexes | Username, email and phone uniqueness is enforced by the database as well as services. | Data integrity, unique indexes |
| 5 | Added optional admin bootstrap | Operators can seed the first administrator via environment variables, avoiding hardcoded credentials. | Bootstrap process, environment configuration, least privilege |
| 6 | Extended REST endpoints | Added account activation, consistent paging responses and Mongo-compatible CRUD. | REST, HTTP verbs, DTO mapping |
| 7 | Implemented React login/register | The browser obtains a JWT and presents an authenticated dashboard. | React hooks, controlled forms, API integration |
| 8 | Added administrator directory | The UI manages users, filters, paging, activation and soft deletion. | RBAC-aware UI, state management |
| 9 | Created Docker Compose services | MongoDB, Spring Boot and React can run together in local containers. | Multi-container orchestration, networking |
| 10 | Added API documentation | Swagger UI allows API inspection with bearer token authentication. | OpenAPI 3, API contracts |
| 11 | Added MongoDB integration tests and CI | GitHub Actions checks the backend against a real MongoDB service and builds the frontend. | CI, integration tests, regression prevention |
| 12 | Wrote architecture and setup documentation | Reviewers can understand decisions, trade-offs and local setup without guessing. | ADRs, technical communication |

**Interview pitch:** "I built a document-backed Spring Boot user-management application with JWT/RBAC, configurable admin provisioning, searchable MongoDB repositories, a React admin dashboard, and automated validation in GitHub Actions."

**Scope note:** This is a demonstration application; do not claim production security, deployed cloud infrastructure, or confirmed user adoption.
