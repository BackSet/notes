# Contexto del Proyecto

## Identidad

- Nombre del proyecto: `notes`.
- Repositorio remoto: `BackSet/notes`.
- Rama de trabajo revisada: `dev`.
- Tipo: monorepo full-stack con backend, frontend, documentacion y soporte de despliegue.

## Estado Actual

El proyecto tiene una base tecnica implementada para autenticacion, usuarios, roles, permisos, bootstrap de administrador inicial, refresh tokens, UI base y despliegue separado por servicio. No hay modulo de dominio de notas implementado todavia; cualquier CRUD de notas debe tratarse como pendiente y no como funcionalidad existente.

## Estructura

```text
/
|-- .github/workflows/build.yml
|-- backend/
|   |-- src/main/java/com/notes/backend/
|   |-- src/main/resources/
|   |-- src/test/java/com/notes/backend/
|   |-- Dockerfile
|   |-- railway.json
|   |-- pom.xml
|   |-- .env.example
|-- frontend/
|   |-- src/
|   |-- Dockerfile
|   |-- Caddyfile
|   |-- railway.json
|   |-- package.json
|   |-- .env.example
|-- docs/ai/
|-- docs/despliegue/
|-- docker-compose.yml
|-- README.md
```

## Backend

- Lenguaje/runtime: Java 26.
- Framework: Spring Boot 4.1.0.
- Build: Maven.
- Dependencias principales: Spring Web, Spring Security, OAuth2 Resource Server, Spring Data JPA, Validation, Flyway, Actuator, PostgreSQL, H2 para tests.
- Documentacion API en desarrollo: Springdoc OpenAPI 2.6.0 y Scalar 0.6.47.
- Paquete base: `com.notes.backend`.
- Perfil local por defecto: `dev`.
- Perfil productivo esperado: `prod`.

Reglas backend vigentes:

- Flyway controla el esquema.
- `spring.jpa.hibernate.ddl-auto=validate`.
- En `prod`, Scalar y OpenAPI estan deshabilitados.
- `/actuator/health` queda disponible sin detalles sensibles.
- Secrets reales no se versionan.

## Seguridad Implementada

- `User`, `Role`, `Permission` y `RefreshToken` persistentes.
- Roles base: `ADMIN` y `USER`.
- Permisos base definidos en `BasePermission`.
- Login con email o username.
- Registro publico con rol `USER`.
- Access token JWT.
- Refresh token opaco almacenado como hash.
- Logout individual y revocacion de sesiones.
- Bootstrap de administrador inicial controlado por variables.
- Endpoints administrativos protegidos por permisos.

## Frontend

- Framework: React 19 con Vite 8.
- Lenguaje: TypeScript.
- UI: Tailwind CSS, componentes propios y lucide-react.
- Router: TanStack Router.
- Datos remotos: TanStack Query.
- Estado de sesion: Zustand.
- Formularios: React Hook Form y Zod.
- Notificaciones: Sonner.
- Cliente HTTP: openapi-fetch.
- Tests: Vitest y Testing Library.

Elementos implementados:

- Cliente API centralizado en `frontend/src/lib/api/`.
- Helpers de sesion y permisos en `frontend/src/lib/auth/`.
- Store de autenticacion en `frontend/src/stores/authStore.ts`.
- Guards `RequireAuth` y `RequirePermission`.
- Paginas de login, registro, dashboard y administracion de usuarios.
- Componentes UI base: botones, inputs, labels, textarea, tarjetas, skeletons, estados loading/empty/error, page header, dialogo de confirmacion y toaster.

## Variables

- Backend: `backend/.env.example`.
- Frontend: `frontend/.env.example`.
- No hay plantilla raiz `.env.example` requerida para el monorepo.
- Variables reales deben vivir fuera del repo.
- El frontend solo debe usar valores seguros expuestos con prefijo `VITE_`.

## Despliegue

- Railway esperado con tres servicios: PostgreSQL, backend y frontend.
- Backend con root directory `backend`.
- Frontend con root directory `frontend`.
- Frontend servido como SPA estatica con Caddy.
- `VITE_API_BASE_URL` se inyecta en build.
- `CORS_ALLOWED_ORIGINS` debe apuntar al origen exacto del frontend en produccion.

## Comandos

Confirmar desde manifiestos antes de ejecutar:

```bash
cd backend
mvn test
```

```bash
cd frontend
npm run lint
npm run test
npm run build
```

## Documentos Canonicos

- `README.md`.
- `docs/ai/PROJECT_CONTEXT.md`.
- `docs/ai/MODULE_MAP.md`.
- `docs/ai/NAMING.md`.
- `docs/ai/PROJECT_INSTRUCTIONS.md`.
- `docs/ai/PROJECT_INITIALIZATION_BLUEPRINT.md`.
- `docs/despliegue/RAILWAY_PRODUCCION_GUIA.md`.
- `docs/despliegue/VARIABLES_ENTORNO.md`.

## Pendientes Conocidos

- Definir e implementar el dominio funcional de notas.
- Agregar endpoints y UI del dominio solo cuando se soliciten explicitamente.
- Mantener el blueprint generico sin nombres de este proyecto ni versiones fijas.
