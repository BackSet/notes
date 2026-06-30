# Mapa de Modulos

## Backend

Raiz: `backend/src/main/java/com/notes/backend/`.

Modulos implementados:

- `BackendApplication`: entry point Spring Boot.
- `config`: configuracion de seguridad.
- `security`: JWT, principal autenticado, permisos, roles, refresh tokens y repositorios asociados.
- `user`: entidad y repositorio de usuarios.
- `auth`: registro, login, refresh, logout y consulta de sesion.
- `admin`: catalogos de roles/permisos y administracion de usuarios.
- `bootstrap`: seeding RBAC y administrador inicial por variables.

Recursos:

- `backend/src/main/resources/application.properties`: configuracion comun.
- `backend/src/main/resources/application-dev.properties`: datasource local, Flyway y Scalar/OpenAPI habilitados.
- `backend/src/main/resources/application-prod.properties`: datasource productivo, Flyway y Scalar/OpenAPI deshabilitados.
- `backend/src/main/resources/db/migration/V1__create_security_schema.sql`: esquema de seguridad inicial.

Endpoints implementados:

- `POST /api/auth/register`: registro de usuario.
- `POST /api/auth/login`: login por email o username.
- `POST /api/auth/refresh`: renovacion con refresh token.
- `POST /api/auth/logout`: revocacion de refresh token.
- `POST /api/auth/logout-all`: revocacion global de sesiones del usuario autenticado.
- `GET /api/auth/me`: usuario autenticado actual.
- `GET /api/admin/catalog/roles`: catalogo de roles.
- `GET /api/admin/catalog/permissions`: catalogo de permisos.
- `GET /api/admin/users`: listado administrativo de usuarios.
- `PUT /api/admin/users/{id}`: actualizacion administrativa de usuario.
- `GET /actuator/health`: healthcheck.
- `GET /docs`: Scalar solo en perfil `dev`.
- `GET /v3/api-docs`: OpenAPI solo en perfil `dev`.

No implementado:

- CRUD de notas.
- Categorias, etiquetas o adjuntos.
- Busqueda de contenido de notas.

## Frontend

Raiz: `frontend/src/`.

Modulos implementados:

- `main.tsx`: entrada React.
- `router.tsx`: rutas de la SPA.
- `components/layout/RootLayout.tsx`: layout raiz responsive.
- `components/auth/AuthProvider.tsx`: inicializacion de sesion.
- `components/auth/RequireAuth.tsx`: guard autenticado.
- `components/auth/RequirePermission.tsx`: guard por permiso.
- `components/admin/UsersTable.tsx`: tabla administrativa de usuarios.
- `components/ui/`: primitivas UI reutilizables.
- `lib/api/client.ts`: cliente openapi-fetch.
- `lib/api/types.ts`: tipos basicos de API.
- `lib/auth/session.ts`: persistencia de sesion.
- `lib/auth/permissions.ts`: helpers de permisos.
- `stores/authStore.ts`: estado de autenticacion.
- `pages/LoginPage.tsx`: login.
- `pages/RegisterPage.tsx`: registro.
- `pages/DashboardPage.tsx`: pantalla autenticada inicial.
- `pages/AdminUsersPage.tsx`: administracion de usuarios.

Rutas frontend implementadas:

- `/login`.
- `/register`.
- `/`.
- `/admin/users`.

No implementado:

- Pantallas CRUD de notas.
- Navegacion por categorias o etiquetas.
- Edicion enriquecida de contenido.

## Tests

Backend:

- `BackendApplicationTests`: carga de contexto.
- `auth/AuthFlowIntegrationTest`: flujos de autenticacion.
- `auth/AdminLoginIntegrationTest`: login administrador.
- `admin/AdminAuthorizationIntegrationTest`: autorizacion administrativa.
- `bootstrap/InitialAdminServiceTest`: servicio de admin inicial.
- `bootstrap/InitialAdminEnabledIntegrationTest`: bootstrap habilitado.
- `bootstrap/RbacSeedingIntegrationTest`: seeding RBAC.

Frontend:

- `components/admin/__tests__/UsersTable.test.tsx`.
- `components/ui/__tests__/Button.test.tsx`.
- `components/ui/__tests__/FeedbackStates.test.tsx`.
- `lib/auth/__tests__/permissions.test.ts`.
- `lib/auth/__tests__/session.test.ts`.

## CI

- `.github/workflows/build.yml` valida backend y frontend.

## Despliegue

- `backend/Dockerfile` y `backend/railway.json` pertenecen al servicio backend.
- `frontend/Dockerfile`, `frontend/Caddyfile` y `frontend/railway.json` pertenecen al servicio frontend.
- `docs/despliegue/` documenta Railway y variables.
