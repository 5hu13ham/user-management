# Architecture — User Management

## Application boundary

```text
Browser / React + TypeScript
  ├─ Auth form -> POST /auth/login or /auth/register
  └─ Admin dashboard -> /users/* (Bearer JWT)
                  |
            Spring Security
              JwtAuthenticationFilter -> JwtUtil -> UserAuthService
                  |
       REST Controllers and @PreAuthorize
                  |
              UserService
                  |
      Spring Data MongoDB repositories
                  |
        MongoDB: users / roles / permissions
```

## Decisions

1. **MongoDB only.** Three collections store users, roles, and permissions. H2/JPA/MySQL are not part of the current application.
2. **Embedded roles on users.** Each user document stores a role snapshot with its permission set. This makes permission evaluation local to the user record but changes to role templates do *not* automatically propagate to existing users. A role-reference design would be preferable for dynamic centralized policies.
3. **JWT is signed with HS256.** Tokens expire in one hour; bearer tokens have no server-side revocation list. Requests load the current user so disabled/deleted accounts lose access.
4. **Least privilege.** New registrations get USER role with self-profile access, while directory operations require ADMIN_READ or ADMIN_WRITE.
5. **Admin bootstrap is opt-in.** A first administrator may be created from environment variables; no static admin password is included in source.
6. **Soft deletion.** Deleting a user sets `isDeleted=true` and disables the account; normal lists exclude deleted records.
7. **Pagination.** Search filters use MongoTemplate, bounded page sizes, allowlisted sort fields, and escaped search text to avoid regex injection.
8. **Frontend token storage.** The demo uses sessionStorage, which is not suitable for hardened authentication against XSS. A real deployed identity system should consider secure HttpOnly cookies, CSRF defenses, session lifecycle, audit logging and stronger account recovery.

## API

- `POST /auth/register`, `POST /auth/login`: public.
- `GET /api/ping`: public.
- `GET /users/me`: authenticated self-profile.
- `GET /users`, `GET /users/filter-search`, `GET /users/{id}`: ADMIN_READ.
- `POST /users`, `PUT /users/{id}`, `PATCH /users/{id}/active`, `DELETE /users/{id}`: ADMIN_WRITE.
- `GET /swagger-ui.html`: API documentation.
- `GET /v3/api-docs`: machine-readable OpenAPI schema.

## Tests and deployment

GitHub Actions provisions a MongoDB service and runs Maven tests; a second job installs and builds the Vite frontend. Docker Compose provides MongoDB, the Java API and an Nginx-hosted frontend.

## Deliberate out-of-scope items

Password resets, email verification, session revocation, rate limiting, audit trails, multi-tenant authorization, advanced admin role editing, production backup/restore, metrics and cloud deployment are not claimed as complete.
