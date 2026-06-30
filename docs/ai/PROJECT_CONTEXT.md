# Project Context: Notes

## 1. Identificación del Proyecto
* **Nombre del Proyecto**: notes `[verificado en documentación]`
* **Repositorio**: `BackSet/notes` `[verificado en Git]`
* **Rama Base**: `dev` `[verificado en Git]`
* **Fecha de Auditoría**: 2026-06-29 `[verificado en documentación]`

## 2. Estructura Principal del Repositorio `[verificado en Git]`
```
/
├── .github/workflows/build.yml  # Workflow de CI/CD para GitHub Actions
├── backend/                     # Código del backend (Spring Boot)
│   ├── src/
│   │   ├── main/java/com/notes/backend/  # Código fuente Java
│   │   └── test/java/com/notes/backend/  # Código de pruebas unitarias/integración
│   ├── pom.xml                  # Manifiesto Maven
│   ├── Dockerfile               # Construcción de contenedor Docker
│   └── railway.json             # Configuración de Railway
├── frontend/                    # Código del frontend (React + Vite)
│   ├── src/                     # Código fuente TypeScript/React
│   ├── package.json             # Manifiesto de dependencias Node.js
│   ├── Dockerfile               # Construcción de contenedor Docker
│   ├── Caddyfile                # Servidor web en producción
│   └── railway.json             # Configuración de Railway
├── docker-compose.yml           # Base de datos PostgreSQL para desarrollo local
└── README.md                    # Instrucciones iniciales de ejecución
```

## 3. Stack Tecnológico Confirmado
* **Backend**:
  * Java 26 (OpenJDK 26.0.1) `[verificado en documentación]`
  * Spring Boot 4.1.0 `[verificado en documentación]`
  * Maven 3.9.16 `[verificado en documentación]`
  * Spring Security `[verificado en documentación]`
  * Spring Security OAuth2 Resource Server (validación de JWT, Nimbus JOSE) `[verificado en documentación]`
  * Spring Data JPA `[verificado en documentación]`
  * Flyway Migration `[verificado en documentación]`
  * Spring Boot Actuator `[verificado en documentación]`
  * Springdoc OpenAPI (v2.6.0, API únicamente, sin Swagger UI) `[verificado en documentación]`
  * Scalar API Reference (v0.6.47, en `/docs` para desarrollo) `[verificado en documentación]`
  * H2 Database (ámbito de test) `[verificado en documentación]`
* **Frontend**:
  * React 19.2.7 `[verificado en documentación]`
  * Vite 8.1.0 `[verificado en documentación]`
  * TypeScript 6.0.2 `[verificado en documentación]`
  * Tailwind CSS 3.4.19 `[verificado en documentación]`
  * TanStack Router 1.170.16 `[verificado en documentación]`
  * TanStack Query 5.101.2 `[verificado en documentación]`
  * Zustand 5.0.14 `[verificado en documentación]`
  * React Hook Form 7.80.0 `[verificado en documentación]`
  * Zod 4.4.3 `[verificado en documentación]`
  * Sonner 2.0.7 `[verificado en documentación]`
  * openapi-fetch 0.17.0 `[verificado en documentación]`
  * Vitest 4.1.9 y JSDOM `[verificado en documentación]`
* **Base de Datos**:
  * PostgreSQL 16 (vía Docker) `[verificado en documentación]`

## 4. Arquitectura por Capas `[inferido]`
* **Backend**:
  * `Controller`: Exposición de endpoints REST e integración con Springdoc OpenAPI.
  * `Service`: Lógica de negocio y transaccionalidad.
  * `Repository`: Interfaces de Spring Data JPA para persistencia.
  * `Model`: Entidades JPA.
  * `DTO`: Objetos de transferencia de datos.
* **Frontend**:
  * `Components`: UI reutilizable en `src/components/ui/` y componentes de página.
  * `Routes`: Enrutamiento de páginas estructurado.
  * `Store`: Estado global administrado por Zustand.
  * `API`: Cliente centralizado en `src/lib/api/client.ts` consumiendo endpoints a través de `openapi-fetch`.

## 5. Contratos y Convenciones `[inferido]`
* Nombres de variables y funciones en inglés.
* Comunicación frontend-backend mediante JSON y cabecera `Authorization: Bearer <token>`.
* Códigos de estado HTTP estándar (200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 500 Internal Error).

## 6. Seguridad y Permisos
* Configuración de Spring Security activa `[verificado en documentación]`.
* Modelo de seguridad persistente (RBAC) `[verificado en Git]`: `User`, `Role`, `Permission`, `RefreshToken` con tablas puente `user_roles` y `role_permissions`.
* En cada arranque se siembra de forma idempotente el rol `ADMIN` y los permisos base (`USER_READ`, `USER_CREATE`, `USER_UPDATE`, `USER_DISABLE`, `ROLE_READ`, `PERMISSION_READ`), asignándolos todos a `ADMIN` `[verificado en Git]`.
* Administrador inicial opcional controlado por variables de entorno `INITIAL_ADMIN_ENABLED`, `INITIAL_ADMIN_UPDATE_EXISTING`, `INITIAL_ADMIN_EMAIL`, `INITIAL_ADMIN_USERNAME`, `INITIAL_ADMIN_PASSWORD` `[verificado en Git]`. Si está deshabilitado no se crea ni actualiza; si existe, solo se actualiza cuando `INITIAL_ADMIN_UPDATE_EXISTING=true`.
* Contraseñas almacenadas con `PasswordEncoder` (BCrypt); nunca en texto plano ni en logs `[verificado en Git]`.
* Autenticación stateless implementada `[verificado en Git]`:
  * Endpoints `/api/auth/register|login|refresh|logout|logout-all|me`. `register`, `login` y `refresh` son públicos; el resto requiere Bearer access token.
  * Access token JWT firmado con HMAC-SHA256, corto (default 15 min). Claims: `sub` (id de usuario), `username`, `email`, `roles`, `permissions`, `iat`, `exp`.
  * Refresh token opaco de alta entropía, persistido solo como hash SHA-256 (default 7 días). Logout revoca uno; logout-all revoca todos. Un refresh token revocado o expirado no emite nuevos access tokens.
  * Login acepta email o username; los errores de login son genéricos (no revelan si la cuenta existe). El registro público crea usuarios `enabled` sin rol ADMIN.
  * El backend actúa como OAuth2 Resource Server; los claims `roles`/`permissions` se mapean a authorities (`ROLE_*` y permisos).
* Variables JWT: `JWT_SECRET` (≥256 bits), `JWT_EXPIRATION_MS` (access, corto), `JWT_REFRESH_EXPIRATION_MS` (refresh, largo) `[verificado en Git]`.
* Autorización por permisos explícitos implementada `[verificado en Git]`:
  * Method security activada (`@EnableMethodSecurity`); los endpoints `/api/admin/**` exigen un permiso concreto con `@PreAuthorize("hasAuthority('...')")`, evaluado desde los claims del access token.
  * Endpoints admin mínimos: consulta/gestión de usuarios (`USER_READ`, `USER_UPDATE`, `USER_DISABLE`) y consulta de roles (`ROLE_READ`) y permisos (`PERMISSION_READ`).
  * Sin token → 401; autenticado sin el permiso → 403.
  * Protección del último administrador: no se puede deshabilitar (ni vía `PATCH enabled=false`) al único admin habilitado (409).
* CORS configurado en el backend a partir de `app.cors.allowed-origins` (`CORS_ALLOWED_ORIGINS`); permite el origen del frontend para `Authorization` y `Content-Type` `[verificado en Git]`.
* Frontend de autenticación implementado `[verificado en Git]`:
  * Login, registro público, panel de usuario y vista `/admin/users`; sesión en Zustand y tokens en `localStorage`.
  * Cliente `openapi-fetch` con refresh automático ante 401 (una vez) y limpieza de sesión si falla.
  * Guards de ruta por autenticación y por permiso; los controles admin se ocultan sin permisos. La autorización real permanece en el backend.
* Autorización en endpoints de negocio (notas) `[pendiente de confirmar]` (iteración posterior).

## 7. Base de Datos y Migraciones
* PostgreSQL 16 para desarrollo y producción `[verificado en documentación]`.
* Flyway configurado para migraciones automatizadas en el backend `[verificado en documentación]`.
* Las migraciones de base de datos se almacenan en `backend/src/main/resources/db/migration/` `[verificado en Git]`. La primera migración es `V1__create_security_schema.sql` (tablas y constraints del modelo de seguridad). El sembrado de datos (rol, permisos, admin) se realiza en el bootstrap de la aplicación, no en SQL, para mantener consistencia con el entorno de test (H2, Flyway deshabilitado).
* H2 Database en memoria se utiliza para el entorno de pruebas unitarias/integración para evitar depender de una base de datos PostgreSQL real `[verificado en documentación]`.

## 8. Infraestructura y CI/CD
* **Local**: Docker Compose levanta base de datos PostgreSQL en el puerto 5432 `[verificado en documentación]`.
* **CI/CD**: GitHub Actions ejecuta pruebas del backend (Java 26, Maven) y frontend (Node 20, npm run build) en cada push o PR a `main` o `master` `[verificado en documentación]`.
* **Producción**: Configurado para desplegarse en Railway `[verificado en documentación]`.
  * El backend corre en un contenedor Docker y selecciona entorno mediante `SPRING_PROFILES_ACTIVE`.
  * El perfil local por defecto es `dev`; el perfil de Railway debe ser `prod`.
  * Springdoc OpenAPI y Swagger UI están habilitados solo en `dev` y deshabilitados en `prod`.
  * El frontend se compila y se sirve usando Caddy para optimizar el rendimiento de la SPA.

## 9. Comandos Confirmados
### Backend `[verificado en documentación]`
* Ejecutar pruebas: `mvn clean test` (ejecutado desde el directorio `backend/`).
* Ejecutar en desarrollo: `mvn spring-boot:run` (ejecutado desde el directorio `backend/`; Spring importa `backend/.env` de forma opcional mediante `spring.config.import`; perfil por defecto `dev`).

### Frontend `[verificado en documentación]`
* Instalar dependencias: `npm install`
* Ejecutar en desarrollo: `npm run dev`
* Compilar aplicación: `npm run build`
* Ejecutar pruebas: `npm run test`
* Ejecutar linter: `npm run lint`

## 10. Fuentes Canónicas `[verificado en documentación]`
* [README.md](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/README.md)
* [backend/pom.xml](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/pom.xml)
* [frontend/package.json](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/package.json)
* [docs/despliegue/RAILWAY_PRODUCCION_GUIA.md](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/docs/despliegue/RAILWAY_PRODUCCION_GUIA.md)

## 11. Reglas Críticas `[verificado en documentación]`
* Nunca versionar credenciales o secretos en texto plano.
* No utilizar `ddl-auto=update` en entornos productivos.
* No exponer Swagger UI ni `/v3/api-docs` en el perfil `prod`.
* No utilizar `axios` en el frontend; el cliente HTTP oficial es `openapi-fetch`.
* Evitar el uso de colores hex literales en componentes; usar las variables CSS mapeadas en Tailwind.

## 12. Pendientes de Confirmar
* Autorización por permisos en endpoints de negocio y, eventualmente, rol no-administrador para usuarios públicos `[pendiente de confirmar]`.
* Módulo de notas completo (modelo, endpoints y UI) `[pendiente de confirmar]`.
* Generación del cliente tipado del frontend con `openapi-typescript` (hoy `paths`/`types` se mantienen a mano) `[pendiente de confirmar]`.
