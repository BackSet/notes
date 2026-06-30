# Nomenclatura Canonica

## Proyecto

- Proyecto: `notes`.
- Repo: `BackSet/notes`.
- Monorepo: `backend/`, `frontend/`, `docs/`.

## Paquetes Backend

- Paquete base: `com.notes.backend`.
- Paquetes por responsabilidad: `auth`, `admin`, `bootstrap`, `config`, `security`, `user`.
- Clases Java en `PascalCase`.
- Metodos, campos y variables en `camelCase`.
- Constantes Java en `UPPER_SNAKE_CASE`.
- DTOs de entrada terminan en `Request`.
- DTOs de salida terminan en `Response`.
- Excepciones terminan en `Exception`.
- Repositorios Spring Data terminan en `Repository`.
- Servicios terminan en `Service`.
- Controladores terminan en `Controller`.

## Base de Datos

- Tablas en `snake_case` plural cuando representan colecciones.
- Columnas en `snake_case`.
- Migraciones Flyway con formato `V<numero>__descripcion.sql`.
- Migraciones aplicadas no deben editarse; crear una nueva migracion para cambios posteriores.

Tablas de seguridad actuales:

- `users`.
- `roles`.
- `permissions`.
- `user_roles`.
- `role_permissions`.
- `refresh_tokens`.

## API

- Prefijo principal: `/api`.
- Recursos en minusculas y plural cuando aplique.
- Palabras compuestas en `kebab-case`.
- Endpoints auth actuales bajo `/api/auth`.
- Endpoints admin actuales bajo `/api/admin`.
- Healthcheck bajo `/actuator/health`.
- Scalar bajo `/docs` solo en `dev`.
- OpenAPI bajo `/v3/api-docs` solo en `dev`.

## Roles y Permisos

Roles actuales:

- `ADMIN`.
- `USER`.

Convencion de permisos:

- Formato `scope:action`.
- Scope en minusculas.
- Action en minusculas.
- Ejemplo vigente: permisos administrativos definidos por `BasePermission`.

## JWT y Sesion

- Access token: JWT firmado por backend.
- Refresh token: opaco, persistido solo como hash.
- Claims deben mantenerse minimos y no incluir secretos.
- Storage frontend centralizado mediante helpers de `frontend/src/lib/auth/session.ts`.

## Frontend

- Componentes React en `PascalCase`.
- Hooks en `useCamelCase`.
- Utilidades en `camelCase`.
- Archivos de tests con sufijo `.test.ts` o `.test.tsx`.
- Componentes UI base en `frontend/src/components/ui/`.
- Cliente API en `frontend/src/lib/api/`.
- Estado global en `frontend/src/stores/`.

## Variables

Backend:

- Variables en `UPPER_SNAKE_CASE`.
- Plantilla versionada: `backend/.env.example`.
- Archivo real local ignorado: `backend/.env`.

Frontend:

- Variables publicas con prefijo `VITE_`.
- Plantilla versionada: `frontend/.env.example`.
- Archivo real local ignorado: `frontend/.env`.
- Nunca usar `VITE_` para secretos, JWT secrets, passwords o credenciales de base de datos.

## Dominio de Negocio

El dominio funcional de notas aun no esta implementado. No documentar `Note`, categorias, etiquetas ni endpoints de notas como existentes hasta que el codigo los incluya.
