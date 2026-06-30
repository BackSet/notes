# Module Map: Notes

Este mapa detalla la organización de los módulos del sistema, separando lo que ya está implementado en código de lo que se espera implementar.

## 1. Estructura de Directorios Técnicos `[verificado en Git]`
* **Backend (`backend/src/main/java/com/notes/backend/`)**:
  * Contiene el punto de entrada principal [BackendApplication.java](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/src/main/java/com/notes/backend/BackendApplication.java).
  * `user/`: Entidad `User` y su `UserRepository`.
  * `security/`: Entidades `Role`, `Permission` y `RefreshToken` con sus repositorios; catálogo `BasePermission` y constante `RoleName`.
  * `bootstrap/`: Sembrado idempotente de RBAC (`RbacSeeder`) y alta/actualización del administrador inicial (`InitialAdminService`, `InitialAdminProperties`, `SecurityBootstrapRunner`).
  * `security/` (autenticación): `JwtService` (emisión de access tokens HMAC-SHA256), `RefreshTokenService` (emisión/verificación/revocación de refresh tokens por hash), `AppUserDetailsService`/`AppUserPrincipal` (login por email o username), `JwtProperties` e `InvalidRefreshTokenException`.
  * `auth/`: `AuthController` (`/api/auth/**`), `AuthService`, DTOs en `auth/dto/`, `AuthExceptionHandler` y excepciones (`EmailAlreadyUsedException`, `UsernameAlreadyUsedException`, `InvalidCredentialsException`).
  * `admin/`: endpoints administrativos (`AdminUserController`, `AdminCatalogController`), servicios (`UserAdminService`, `CatalogService`), DTOs en `admin/dto/`, `AdminExceptionHandler` y excepciones (`UserNotFoundException`, `LastAdminException`). Autorización por permisos con `@PreAuthorize`.
  * `config/SecurityConfig.java`: cadena de filtros stateless como OAuth2 Resource Server (validación de Bearer JWT), `AuthenticationManager` (DAO + BCrypt), `JwtEncoder`/`JwtDecoder`, conversor de claims `roles`/`permissions` a authorities, method security (`@EnableMethodSecurity`) y CORS basado en `app.cors.allowed-origins`.
* **Frontend (`frontend/src/`)**:
  * `main.tsx`: monta `RouterProvider` (TanStack Router) dentro de `AuthProvider`.
  * `router.tsx`: árbol de rutas (`/`, `/login`, `/register`, `/admin/users`).
  * `lib/api/`: `client.ts` (cliente `openapi-fetch` tipado + middleware que adjunta el access token e intenta un refresh ante 401) y `types.ts` (contrato consumido).
  * `lib/auth/`: `session.ts` (tokens en localStorage + pub/sub) y `permissions.ts` (catálogo y `userHasPermission`).
  * `stores/authStore.ts`: estado de sesión en Zustand (`hydrate`, `login`, `register`, `logout`, `hasPermission`).
  * `components/auth/`: `AuthProvider`, `RequireAuth`, `RequirePermission` (guards).
  * `components/layout/RootLayout.tsx`: cabecera/navegación condicionada por sesión y permisos.
  * `components/admin/UsersTable.tsx`: tabla pura con controles habilitar/deshabilitar.
  * `pages/`: `LoginPage`, `RegisterPage`, `DashboardPage`, `AdminUsersPage`.
  * `components/ui/`: componentes atómicos base.
  * `components/ui/` ampliado: componentes atomicos (`Button`, `Input`, `Label`, `Textarea`), superficies (`SurfaceCard`), feedback (`LoadingState`, `EmptyState`, `ErrorState`, `Skeleton`), estructura (`PageHeader`), confirmacion (`ConfirmDialog`) y notificaciones (`Toaster`).
  * `index.css` + `tailwind.config.js`: tokens semanticos de tema, dark mode con `.dark`, sombras, radio, motion y utilidades reutilizables de superficie/foco/alerta.

---

## 2. Módulos Funcionales y Técnicos

### A. Módulo de Autenticación y Usuarios `[implementado backend y frontend]`
* **Descripción**: Registro, inicio de sesión y gestión de sesiones de usuario de forma segura, con vista administrativa básica.
* **Componentes Frontend**:
  * Páginas de Login y Registro (React Hook Form + Zod, accesibles) `[implementado]`.
  * Store de sesión `useAuthStore` (Zustand) con hidratación, login, registro, logout y `hasPermission` `[implementado]`.
  * Cliente `openapi-fetch` tipado con refresh automático ante 401 y limpieza de sesión si el refresh falla `[implementado]`.
  * Guards `RequireAuth` y `RequirePermission`; vista `/admin/users` que oculta controles sin permisos `[implementado]`.
* **Componentes Backend**:
  * Modelo de seguridad persistente: entidades `User`, `Role`, `Permission`, `RefreshToken` y tablas asociadas `[implementado]`.
  * Bootstrap del rol `ADMIN`, permisos base y administrador inicial controlado por variables `INITIAL_ADMIN_*` `[implementado]`.
  * `AuthController` con endpoints `/api/auth/register|login|refresh|logout|logout-all|me` `[implementado]`.
  * Autenticación stateless con access token (JWT HMAC-SHA256, corto) y refresh token persistido por hash; login por email o username; registro público sin rol ADMIN `[implementado]`.
  * Endpoints administrativos `/api/admin/**` con autorización por permisos explícitos (`@PreAuthorize("hasAuthority('...')")`): consulta/gestión de usuarios y consulta de roles y permisos. Incluye protección del último administrador habilitado `[implementado]`.
* **Pruebas**:
  * Tests de bootstrap de seguridad (`InitialAdminServiceTest`, `RbacSeedingIntegrationTest`, `InitialAdminEnabledIntegrationTest`) `[implementado]`.
  * Tests de integración de autenticación: `AuthFlowIntegrationTest` (registro, login, me, refresh, logout, logout-all, token revocado) y `AdminLoginIntegrationTest` (login del admin inicial) `[implementado]`.

### B. Módulo de Gestión de Notas `[esperado]`
* **Descripción**: Permitirá a los usuarios crear, leer, actualizar, borrar y organizar sus notas personales.
* **Componentes Frontend**:
  * Vista de panel/dashboard `[pendiente de confirmar]`.
  * Editor de notas, tarjetas de notas y listados `[pendiente de confirmar]`.
* **Componentes Backend**:
  * `NoteController` con endpoints CRUD (`/api/notes`) `[pendiente de confirmar]`.
  * `NoteService` y `NoteRepository` `[pendiente de confirmar]`.
  * Entidad `Note` y tabla `notes` `[pendiente de confirmar]`.
* **Pruebas**:
  * Tests unitarios para la lógica del servicio de notas `[pendiente de confirmar]`.

---

## 3. Endpoints del Backend Reales `[verificado en documentación]`
* **Autenticación (`/api/auth`)** `[verificado en Git]`:
  * `POST /register` - Registro público (crea usuario `enabled`, sin rol ADMIN). Público.
  * `POST /login` - Login por email o username; devuelve access + refresh token. Público.
  * `POST /refresh` - Nuevo access token a partir de un refresh token válido. Público.
  * `POST /logout` - Revoca el refresh token indicado (requiere access token).
  * `POST /logout-all` - Revoca todos los refresh tokens del usuario (requiere access token).
  * `GET /me` - Devuelve el usuario actual (requiere access token).
* **Administración (`/api/admin`)** `[verificado en Git]` (cada endpoint exige un permiso explícito vía `@PreAuthorize`):
  * `GET /users` - Lista usuarios. Requiere `USER_READ`.
  * `GET /users/{id}` - Detalle de usuario. Requiere `USER_READ`.
  * `PATCH /users/{id}` - Actualización parcial (email/username/enabled). Requiere `USER_UPDATE`.
  * `POST /users/{id}/disable` - Deshabilita usuario (protege al último admin). Requiere `USER_DISABLE`.
  * `POST /users/{id}/enable` - Habilita usuario. Requiere `USER_UPDATE`.
  * `GET /roles` - Lista roles con sus permisos. Requiere `ROLE_READ`.
  * `GET /permissions` - Lista permisos. Requiere `PERMISSION_READ`.
* **Actuator**:
  * `/actuator/health` - Comprobación de estado del servicio.
  * `/actuator/info` - Información del servicio.
  * `/actuator/prometheus` - Métricas de rendimiento.
* **Documentación y OpenAPI (solo en perfil `dev`)**:
  * `/docs` - Interfaz visual de documentación Scalar.
  * `/v3/api-docs` - Especificación OpenAPI en formato JSON.

---

## 4. Rutas del Frontend Reales `[verificado en Git]`
* `/` - Panel del usuario autenticado (protegida con `RequireAuth`).
* `/login` - Inicio de sesión (pública; redirige a `/` si ya hay sesión).
* `/register` - Registro público (pública; redirige a `/` si ya hay sesión).
* `/admin/users` - Administración de usuarios (protegida con `RequirePermission` `USER_READ`).
* Enrutado con TanStack Router (`router.tsx`); el layout raíz es `RootLayout`.

---

## 5. Entidades y Persistencia Reales `[verificado en Git]`
* Modelo de seguridad creado por la migración `V1__create_security_schema.sql` y mapeado en JPA:
  * `users` (`User`): `id`, `email` (único), `username` (único), `password_hash`, `enabled`, `created_at`, `updated_at`.
  * `roles` (`Role`): `id`, `name` (único), `description`.
  * `permissions` (`Permission`): `id`, `name` (único), `description`.
  * `user_roles`: tabla puente `users`↔`roles`.
  * `role_permissions`: tabla puente `roles`↔`permissions`.
  * `refresh_tokens` (`RefreshToken`): `id`, `user_id` (FK), `token_hash` (único), `expires_at`, `revoked_at` (nullable), `created_at`.
* Las contraseñas se almacenan solo como hash BCrypt; los refresh tokens solo como hash.
* No existen aún entidades de notas/categorías `[verificado en Git]`.

---

## 6. Flujos Críticos `[inferido]`
1. **Inicio de Sesión y Carga de App**: El usuario se autentica, se guardan access y refresh token en `localStorage`; el cliente HTTP inyecta el access token en cada petición. Ante un 401 intenta un refresh una vez y reintenta; si el refresh falla, limpia la sesión y redirige a `/login`.
2. **Sincronización de Notas**: Carga de notas mediante TanStack Query, operaciones CRUD y mutaciones que invalidan la caché del servidor para mantener la interfaz actualizada.

---

## 7. Zonas de Búsqueda y Pruebas
* **Backend Tests**: Ubicados en `backend/src/test/java/com/notes/backend/` `[verificado en Git]`. Contiene [BackendApplicationTests.java](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/src/test/java/com/notes/backend/BackendApplicationTests.java) (carga de contexto), el paquete `bootstrap/` con las pruebas del modelo de seguridad (`InitialAdminServiceTest` con Mockito, `RbacSeedingIntegrationTest`, `InitialAdminEnabledIntegrationTest`) el paquete `auth/` con las pruebas de autenticación (`AuthFlowIntegrationTest`, `AdminLoginIntegrationTest`) y el paquete `admin/` con `AdminAuthorizationIntegrationTest` (acceso permitido/denegado por permisos, 401 sin token, 403 sin permiso y protección del último admin). Las pruebas de auth construyen `MockMvc` manualmente con el `FilterChainProxy` real (en Spring Boot 4 no está disponible `@AutoConfigureMockMvc` en el classpath de test).
* **Frontend Tests** (Vitest + Testing Library) `[verificado en Git]`: `components/ui/__tests__/Button.test.tsx` (render base), `lib/auth/__tests__/permissions.test.ts` (`userHasPermission`), `lib/auth/__tests__/session.test.ts` (almacenamiento de tokens y pub/sub) y `components/admin/__tests__/UsersTable.test.tsx` (filas, estado vacío y ocultado de controles sin permisos).
* **Frontend UI Tests** `[verificado en Git]`: `components/ui/__tests__/FeedbackStates.test.tsx` cubre `Skeleton`, `LoadingState`, `ErrorState`, `PageHeader` y `ConfirmDialog`; `UsersTable.test.tsx` cubre tambien el contenedor responsive con `overflow-x-auto`.
