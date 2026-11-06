# Phase 1 — Authentication & RBAC: change notes for interviews

> Branch: `fix/phase1-auth-rbac-2026-11-06`. Source review and automated CI tests; do not claim production deployment. The React UI, MongoDB feature, advanced search, and full Docker validation remain separate work.

## System in one sentence

A Java 17/Spring Boot REST backend where a client registers or signs in, receives a signed JWT, sends it in the Authorization header, and Spring Security verifies identity and enforces role/permission rules before the service and repository interact with the database.

## Changes, why they matter, and concepts

| Step | Change | What it does / role in system | Interview concept |
|---|---|---|---|
| 1 | Fixed `RoleRepository` entity type | Reads actual `Role` records rather than querying `User` for roles. | Spring Data JPA, generics, repository abstraction |
| 2 | Added `PermissionRepository` | Lets the app persist and retrieve individual permissions. | RBAC, many-to-many mapping |
| 3 | Added a role/permission initializer | On startup, creates missing USER/ADMIN roles and permissions without shipping an admin password. | Idempotent initialization, least privilege, ORM |
| 4 | Reworked registration request DTO | Accepts structured username, email, name, password, optional phone; applies validation. | DTOs, Bean Validation, input contract |
| 5 | Corrected registration flow | Detects duplicates, BCrypt-hashes passwords, assigns USER role, persists the user, returns a JWT. | Dependency injection, transactions, password hashing |
| 6 | Fixed create/update service defaults | Ensures admin-created users receive a default role; only hashes a provided replacement password. | Service layer, invariants, password management |
| 7 | Updated legacy JWT library | Replaced JJWT 0.9.1 with modular JJWT 0.12.6 and validates minimum HMAC secret length. | HS256, signature verification, cryptographic key strength |
| 8 | Restricted public endpoints | Public: register/login/ping; all other requests require authentication. | SecurityFilterChain, default-deny, attack surface |
| 9 | Hardened JWT request filter | Reads Bearer tokens, validates them, and maps invalid or missing-subject tokens to 401. | OncePerRequestFilter, SecurityContext, authentication |
| 10 | Limited user-record access | Users may read `/users/me`; arbitrary user-ID lookup requires ADMIN_READ. | Authorization, least privilege, horizontal access control |
| 11 | Corrected error responses | API errors return the proper request path and hide unexpected exception internals. | Exception handling, information disclosure |
| 12 | Added test configuration | Runs the application with local H2, a test-only key, and no production credentials. | Spring profiles, test isolation |
| 13 | Added registration and token unit tests | Checks duplicate rejection, safe role assignment, password hashing, signing and invalid tokens. | JUnit 5, Mockito, unit testing |
| 14 | Added HTTP security tests | Checks public/private endpoint boundaries and a newly registered user's authorization. | MockMvc, integration testing, RBAC |
| 15 | Added GitHub Actions workflow | Automatically runs Maven tests on code pushes and PRs. | CI, shift-left testing, regression detection |

## Request flow

```text
POST /auth/register -> @Valid DTO -> RegisterService
    -> UserRepository + RoleRepository
    -> BCrypt password -> save user -> signed JWT
GET /users/me + Authorization: Bearer <JWT>
    -> JwtAuthenticationFilter -> JwtUtil verifies signature/expiry
    -> UserAuthService loads current User -> SecurityContext
    -> UserController -> DTO response
GET /users/{id} -> @PreAuthorize(ADMIN_READ)
    -> UserService -> UserRepository -> response
```

## Interview-ready summary

"I hardened a Spring Boot user-management API by fixing JPA role/permission persistence, designing a validated registration flow, upgrading JWT signing and verification, applying least-privilege RBAC, and adding unit/integration tests through GitHub Actions. I kept secrets externalized and verified the auth flows in CI."

## Remaining work

- Validate real runtime startup and Docker packaging beyond CI.
- Revisit H2/MongoDB separation and use a single clearly documented data source for the user service.
- Add migrations, stronger pagination/filter validations, observability and richer integration tests.
- Complete React frontend, OpenAPI contract and README; add screenshots only when the UI works.
- No production administrator password is seeded. Admin provisioning needs a secure explicit workflow.
