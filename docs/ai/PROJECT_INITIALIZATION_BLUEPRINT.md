# Project Initialization Blueprint

## 1. Purpose

Use `notes` as a reusable baseline for future full-stack projects. The goal is to start from a clean, production-oriented monorepo that already includes service separation, authentication, users, roles, permissions, deployment conventions, UI foundations, tests, and AI-facing documentation.

This blueprint is not a changelog and must not invent a business domain. It describes the initial technical foundation that a future project can adapt before adding its own domain modules.

## 2. Monorepo Structure

A new project should start with this shape:

```text
/
|-- .github/workflows/       # CI for backend and frontend
|-- backend/                 # Spring Boot service
|-- frontend/                # React/Vite SPA
|-- docs/ai/                 # Canonical AI/project documentation
|-- docs/despliegue/         # Deployment and environment guides
|-- docker-compose.yml       # Local infrastructure, usually PostgreSQL
|-- README.md                # Human getting-started guide
```

Each service owns its own runtime files:

- `backend/.env.example`, `backend/Dockerfile`, `backend/railway.json`.
- `frontend/.env.example`, `frontend/Dockerfile`, `frontend/railway.json`, `frontend/Caddyfile`.

## 3. Backend Baseline

Start with a Spring Boot backend that includes:

- Java and Maven.
- Spring Web for REST APIs.
- Spring Data JPA.
- PostgreSQL driver.
- Flyway migrations.
- Actuator health checks.
- Spring Security.
- OAuth2 Resource Server / JWT validation when JWT auth is used.
- Validation for request DTOs.
- OpenAPI/Scalar in development only, if API documentation is part of the project.
- H2 or equivalent test database only for automated tests.

The security baseline should include:

- Persistent `User`, `Role`, `Permission`, and `RefreshToken` models.
- RBAC seed data for an initial `ADMIN` role and base permissions.
- Public registration/login endpoints only if the product wants public accounts.
- JWT access tokens with short expiration.
- Opaque refresh tokens stored only as hashes.
- Logout and logout-all revocation.
- Login by email or username, with generic credential errors.
- Permission-protected admin endpoints.
- Initial admin bootstrap controlled only by environment variables.

The backend must keep variables in `backend/.env.example`, never in source code. Required categories:

- Server/profile: `PORT`, `SPRING_PROFILES_ACTIVE`.
- Database: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- JWT: `JWT_SECRET`, access expiration, refresh expiration.
- CORS: `CORS_ALLOWED_ORIGINS`.
- Initial admin: `INITIAL_ADMIN_ENABLED`, `INITIAL_ADMIN_UPDATE_EXISTING`, `INITIAL_ADMIN_EMAIL`, `INITIAL_ADMIN_USERNAME`, `INITIAL_ADMIN_PASSWORD`.

Production defaults:

- `SPRING_PROFILES_ACTIVE=prod`.
- `spring.jpa.hibernate.ddl-auto=validate`.
- Flyway enabled.
- API docs disabled unless explicitly approved behind protection.
- `/actuator/health` available without sensitive details.

## 4. Frontend Baseline

Start with a React/Vite SPA that includes:

- React.
- Vite.
- TypeScript with strict checks.
- Tailwind CSS.
- TanStack Router.
- TanStack Query.
- Zustand for client/session state.
- React Hook Form.
- Zod.
- Sonner.
- `openapi-fetch` as the HTTP client.
- Vitest and Testing Library.

The frontend baseline should include:

- Centralized API client in `src/lib/api/`.
- Auth/session helpers in `src/lib/auth/`.
- Route guards for authenticated and permission-gated routes.
- A root layout with responsive navigation.
- Login, register, dashboard, and admin/users screens when auth/admin are part of the baseline.
- UI primitives in `src/components/ui/`.

UI foundation requirements:

- Global theme tokens for background, foreground, card, popover, primary, secondary, muted, accent, destructive, success, warning, info, border, input, and ring.
- Radius, shadows, and motion tokens.
- Dark mode via `.dark`.
- `prefers-reduced-motion` support.
- Reusable utilities for surfaces, focus rings, motion, and alert variants.
- Mobile-first layouts.
- Visible focus states.
- No `transition-all`.
- No hardcoded hex colors in components.
- Tables/lists with controlled overflow on mobile.
- Avoid decorative KPI/stat cards unless the product actually needs metrics.

Reusable UI components expected in the baseline:

- `SurfaceCard`.
- `Skeleton`.
- `LoadingState`.
- `EmptyState`.
- `ErrorState`.
- `PageHeader`.
- `ConfirmDialog`.
- `Toaster`.
- Form controls such as `Button`, `Input`, `Label`, and `Textarea`.

The frontend must keep variables in `frontend/.env.example`. Only expose browser-safe values with the `VITE_` prefix. Never put secrets in `VITE_*`.

## 5. Version Policy

When initializing a future project, do not blindly copy the versions from `notes`.

Required process:

1. Check the latest stable versions from official sources, package managers, or freshly generated manifests.
2. Use stable releases only.
3. Do not use alpha, beta, RC, canary, nightly, experimental, or snapshot releases unless the user explicitly approves the risk.
4. If two libraries are incompatible, prioritize a stable compatible set over the newest individual version.
5. Record the selected versions in the service manifests and in `docs/ai/PROJECT_CONTEXT.md`.

Examples of authoritative sources:

- Spring Boot and Java documentation or generated Maven metadata.
- npm package metadata for frontend dependencies.
- Official Docker image tags.
- Railway documentation for deployment behavior.

## 6. Environment Variables

Keep environment variables separated by service.

Backend:

- Use `backend/.env.example`.
- Keep database credentials, JWT secrets, CORS origins, and admin bootstrap values here.
- Use `CHANGE_ME` placeholders for secrets.
- Never commit `backend/.env`.

Frontend:

- Use `frontend/.env.example`.
- Keep only browser-safe values.
- `VITE_API_BASE_URL` points to the backend public URL.
- Never commit `frontend/.env`.
- Never put JWT secrets, database credentials, or admin credentials in frontend variables.

Railway:

- Configure variables per service in the Railway dashboard.
- Link PostgreSQL variables to the backend only.
- Inject frontend build-time variables in the frontend service.

## 7. Railway Deployment

Use separate Railway services:

1. PostgreSQL managed database.
2. Backend service.
3. Frontend service.

Backend Railway setup:

- Root directory: `backend`.
- Build with `backend/Dockerfile`.
- Set `SPRING_PROFILES_ACTIVE=prod`.
- Set datasource variables from the PostgreSQL service.
- Set a strong `JWT_SECRET`.
- Set JWT access and refresh expirations.
- Set `CORS_ALLOWED_ORIGINS` to the exact frontend public origin.
- Use `/actuator/health` as healthcheck.
- Do not expose API docs in production unless explicitly approved and protected.

Frontend Railway setup:

- Root directory: `frontend`.
- Build with `frontend/Dockerfile`.
- Serve SPA with Caddy and `frontend/Caddyfile`.
- Set `VITE_API_BASE_URL` to the backend public URL at build time.
- Rebuild the frontend when the backend URL changes.

Deployment order:

1. Create PostgreSQL.
2. Deploy backend with production profile and database variables.
3. Generate backend public domain.
4. Deploy frontend with `VITE_API_BASE_URL`.
5. Generate frontend public domain.
6. Update backend `CORS_ALLOWED_ORIGINS` with the exact frontend origin.
7. Verify frontend routes, API calls, `/actuator/health`, and that API docs are not public in prod.

## 8. UI/UX Baseline

Every generated project should start with a usable application shell, not a marketing page.

Baseline UI rules:

- Mobile-first layout.
- Semantic theme tokens.
- Dark mode support.
- Reduced motion support.
- Consistent surfaces and spacing.
- Loading, error, empty, and skeleton states.
- Accessible forms with labels and validation messages.
- Visible keyboard focus.
- Responsive navigation.
- Tables with horizontal overflow containers when needed.
- No invented business dashboards, charts, KPIs, or demo domain entities.

## 9. Testing and Validation

Confirm commands from the manifests before running them.

Backend:

```bash
cd backend
mvn clean test
```

Frontend:

```bash
cd frontend
npm run lint
npm run test
npm run build
```

CI should validate at least:

- Backend tests.
- Frontend build.
- Frontend lint and tests when available.

Recommended test coverage for the baseline:

- Backend context load.
- Auth flow.
- RBAC/admin authorization.
- Bootstrap idempotency.
- Frontend auth/session helpers.
- Permission checks.
- UI primitives and feedback states.
- Responsive structure for tables/lists.

## 10. AI Documentation

Keep `docs/ai/` canonical and up to date:

- `PROJECT_CONTEXT.md`: current stack, architecture, commands, rules, and sources.
- `MODULE_MAP.md`: real modules, routes, endpoints, tests, and pending modules.
- `NAMING.md`: glossary, naming, permissions, roles, claims, and storage keys.
- `PROJECT_INSTRUCTIONS.md`: permanent implementation rules and continuity protocol.
- `PROJECT_INITIALIZATION_BLUEPRINT.md`: reusable initialization baseline for future projects.

After any implementation cycle, update these files only when canonical facts changed. Do not use them as a changelog.

## 11. What Future Agents Must Not Do

- Do not delete users, roles, permissions, auth, admin, RBAC, or migrations from the baseline unless the user explicitly changes the baseline.
- Do not invent a business domain.
- Do not create fake `Note`, `Task`, `Todo`, CRM, shop, or analytics entities just to demonstrate the stack.
- Do not version real secrets.
- Do not put secrets in frontend `VITE_*` variables.
- Do not mix backend variables into frontend configuration.
- Do not hardcode production URLs.
- Do not assume commands without reading `pom.xml` and `package.json`.
- Do not use unstable dependency versions without explicit approval.
- Do not expose API docs in production by accident.
- Do not treat documentation updates as a changelog.
