# Project Instructions & Guidelines

## General Workflows
1. **Security & Secrets**:
   - Never commit actual secrets or credentials to Git.
   - All sensitive configurations must be loaded via environment variables.
   - Fallbacks in `application.properties` are allowed for local development only.

2. **Database Migrations**:
   - Use Flyway for all database schema changes.
   - Do not use `ddl-auto=update` in production; keep it as `validate`.
   - Migration files must be named using the format `V[Version]__[Description].sql` under `backend/src/main/resources/db/migration/`.

## Backend Guidelines
- Keep controllers thin; delegate business logic to services.
- Always validate incoming requests using `@Valid` and standard validation annotations.
- Document APIs using Springdoc OpenAPI annotations.
- Write unit tests for services and integration tests for controllers.

## Frontend Guidelines
- **HTTP Client**: Do not use Axios. Use the central `openapi-fetch` client located in `src/lib/api/client.ts`.
- **Styling**: Use Tailwind CSS with the design system tokens defined in `src/index.css`. Do not use hex colors directly in components.
- **Routing**: Use TanStack Router for type-safe routing.
- **State Management**: Use Zustand for lightweight global state and TanStack Query for server-state caching.
- **Form Handling**: Use React Hook Form with Zod schema validation via `@hookform/resolvers/zod`.
- **Transitions**: Avoid `transition-all`. Use specific transition properties (e.g., `transition-colors`, `transition-opacity`) to optimize performance and prevent layout shifts.
- **Responsiveness**: Design mobile-first and prevent global horizontal scrolling.

## Deployment Guidelines (Railway)
- Do not modify `Dockerfile` or `railway.json` files without verifying local compilation.
- Ensure `VITE_API_BASE_URL` is passed as a build-time argument in the frontend Dockerfile.
- The frontend Caddyfile must maintain its security headers, compression, and SPA routing fallback.
- In production, always ensure that `CORS_ALLOWED_ORIGINS` in the backend matches the production URL of the frontend.

