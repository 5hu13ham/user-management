# User Management · React dashboard

Vite + TypeScript + React frontend for the Spring Boot/MongoDB user management API.

## Start

```bash
npm ci
cp .env.example .env.local
npm run dev
```

Default URL: http://localhost:5173. The API is expected at http://localhost:8080 unless `VITE_API_BASE_URL` is changed.

## Flows

- Register a standard USER account.
- Sign in using username or email and password.
- View your own account after authentication.
- As an administrator, search/filter/page through users, create/edit users, update active status and soft-delete records.

To access the admin dashboard, initialize an administrator through the backend's environment-controlled bootstrap process described in the [repository README](../README.md). Normal sign-up does not grant administrator privileges.

## Verification

```bash
npm run lint
npm run build
```

## Limitations

This project stores the JWT in `sessionStorage` for a convenient local demo. Never copy that approach into a production identity platform without a careful XSS/session threat model. The UI is an API client; authorization decisions are enforced by Spring Security on the server.
