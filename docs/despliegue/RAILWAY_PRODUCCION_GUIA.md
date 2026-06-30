# Guia de Despliegue en Produccion - Railway

Esta guia describe el despliegue del monorepo `notes` en Railway.

## Arquitectura

El despliegue esperado usa tres servicios:

1. PostgreSQL administrado.
2. Backend Spring Boot desde `backend/`.
3. Frontend React/Vite desde `frontend/`, servido como SPA estatica con Caddy.

## Paso 1 - Crear Proyecto Railway

1. Inicia sesion en Railway.
2. Crea un proyecto vacio.
3. Usa un nombre operativo como `notes-prod`.

## Paso 2 - Crear PostgreSQL

1. Agrega una base de datos PostgreSQL al proyecto.
2. Railway generara variables como `DATABASE_URL`, `PGUSER` y `PGPASSWORD`.

## Paso 3 - Desplegar Backend

1. Agrega el repositorio GitHub del proyecto.
2. Configura el servicio:
   - Service name: `backend`.
   - Root directory: `backend`.
   - Dockerfile: `backend/Dockerfile`.
3. Configura variables:
   - `SPRING_PROFILES_ACTIVE=prod`.
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.DATABASE_URL}}`.
   - `SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}`.
   - `SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}`.
   - `JWT_SECRET=<secreto fuerte generado fuera del repo>`.
   - `JWT_EXPIRATION_MS=900000`.
   - `JWT_REFRESH_EXPIRATION_MS=604800000`.
   - `CORS_ALLOWED_ORIGINS=https://<frontend-domain>`.
   - Variables `INITIAL_ADMIN_*` solo si necesitas bootstrap controlado.
4. Genera el dominio publico del backend.
5. Verifica `/actuator/health`.

## Paso 4 - Desplegar Frontend

1. Agrega otro servicio desde el mismo repositorio.
2. Configura el servicio:
   - Service name: `frontend`.
   - Root directory: `frontend`.
   - Dockerfile: `frontend/Dockerfile`.
3. Configura:
   - `VITE_API_BASE_URL=https://<backend-domain>`.
4. Genera el dominio publico del frontend.
5. Reconstruye el frontend si cambia `VITE_API_BASE_URL`.

## Paso 5 - Ajustar CORS

1. Regresa al servicio backend.
2. Actualiza `CORS_ALLOWED_ORIGINS` con el origen exacto del frontend.
3. Redeploy backend.

No uses comodines para CORS en produccion.

## Verificacion

- Frontend carga desde su dominio publico.
- Login, registro y rutas protegidas se comunican con el backend.
- `/actuator/health` responde sin detalles sensibles.
- `/docs` no entrega Scalar en `prod`.
- `/v3/api-docs` no entrega OpenAPI en `prod`.
- Las rutas de la SPA funcionan al recargar gracias al fallback de Caddy.

## Seguridad Operativa

- No versionar secretos.
- Mantener `SPRING_PROFILES_ACTIVE=prod` en Railway.
- Deshabilitar el bootstrap admin cuando ya no se necesite.
- Rotar `JWT_SECRET` si hubo exposicion accidental.
