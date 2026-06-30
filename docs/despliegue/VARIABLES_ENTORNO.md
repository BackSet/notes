# Variables de Entorno - Produccion Railway

Este documento lista las variables requeridas para desplegar backend y frontend en Railway.

> Nunca incluyas valores reales de contrasenas, tokens o secretos en archivos versionados. Configura los valores reales directamente en Railway.

## Servicio Backend

| Variable | Descripcion | Ejemplo / valor esperado | Origen |
| :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring Boot. | `prod` | Manual |
| `PORT` | Puerto del contenedor. | `8080` o valor inyectado por Railway | Railway |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL. | `jdbc:postgresql://${{Postgres.DATABASE_URL}}` | PostgreSQL Railway |
| `SPRING_DATASOURCE_USERNAME` | Usuario PostgreSQL. | `${{Postgres.PGUSER}}` | PostgreSQL Railway |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL. | `${{Postgres.PGPASSWORD}}` | PostgreSQL Railway |
| `JWT_SECRET` | Secreto de firma JWT. | Generar secreto fuerte fuera del repo | Manual |
| `JWT_EXPIRATION_MS` | Duracion del access token. | `900000` | Manual |
| `JWT_REFRESH_EXPIRATION_MS` | Duracion del refresh token opaco. | `604800000` | Manual |
| `CORS_ALLOWED_ORIGINS` | Origen exacto del frontend. | `https://<frontend-domain>` | Manual |
| `INITIAL_ADMIN_ENABLED` | Crea el admin inicial al arrancar. | `false` por defecto, `true` solo para bootstrap controlado | Manual |
| `INITIAL_ADMIN_UPDATE_EXISTING` | Actualiza admin existente si coincide. | `false` por defecto | Manual |
| `INITIAL_ADMIN_EMAIL` | Email del admin inicial. | Valor real solo en Railway | Manual |
| `INITIAL_ADMIN_USERNAME` | Username del admin inicial. | Valor real solo en Railway | Manual |
| `INITIAL_ADMIN_PASSWORD` | Password del admin inicial. | Valor real solo en Railway | Manual |

Notas:

- `SPRING_PROFILES_ACTIVE=prod` es obligatorio para cargar configuracion productiva.
- En `prod`, Scalar (`/docs`) y OpenAPI (`/v3/api-docs`) estan deshabilitados.
- `CORS_ALLOWED_ORIGINS` no debe usar comodines en produccion.
- Desactiva `INITIAL_ADMIN_ENABLED` despues de completar el bootstrap inicial si ya no se necesita.

## Servicio Frontend

| Variable | Descripcion | Ejemplo / valor esperado | Origen |
| :--- | :--- | :--- | :--- |
| `VITE_API_BASE_URL` | URL publica del backend. | `https://<backend-domain>` | Manual |

Notas:

- `VITE_API_BASE_URL` se inyecta durante el build de Vite.
- Si cambia la URL del backend, reconstruye el frontend.
- No pongas secretos, credenciales JWT ni credenciales de base de datos en variables `VITE_*`.
