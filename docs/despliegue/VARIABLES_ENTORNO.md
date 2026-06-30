# Variables de Entorno - Producción (Railway)

Este documento detalla las variables de entorno requeridas para el correcto funcionamiento del backend y frontend en el entorno de producción de Railway.

> [!WARNING]
> Nunca incluyas valores reales de contraseñas, tokens o secretos en este documento ni en ningún archivo que se suba a Git. Los valores reales deben configurarse directamente en el panel de control de Railway.

---

## 1. Servicio Backend (`backend`)

| Variable | Descripción | Valor Recomendado / Ejemplo | Origen en Railway |
| :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Activa el perfil de producción en Spring Boot. | `prod` | Manual |
| `PORT` | Puerto expuesto del contenedor. | `8080` (Railway lo asigna automáticamente) | Automático |
| `SPRING_DATASOURCE_URL` | URL de conexión JDBC para PostgreSQL. | `jdbc:postgresql://${{Postgres.DATABASE_URL}}` | Vinculación de Servicio |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos PostgreSQL. | `${{Postgres.PGUSER}}` | Vinculación de Servicio |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos PostgreSQL. | `${{Postgres.PGPASSWORD}}` | Vinculación de Servicio |
| `JWT_SECRET` | Clave secreta para firmar los tokens JWT. | `CHANGE_ME_MUST_BE_AT_LEAST_256_BITS_LONG` | Manual (Generar un secreto fuerte) |
| `JWT_EXPIRATION_MS` | Tiempo de expiración del token JWT en milisegundos. | `86400000` (24 horas) | Manual |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos para peticiones CORS (URL del frontend). | `https://${{frontend.RAILWAY_PUBLIC_DOMAIN}}` | Vinculación de Servicio / Manual |

---

> Nota: el backend tambien soporta `JWT_REFRESH_EXPIRATION_MS` para configurar la duracion del refresh token opaco. Valor recomendado inicial: `604800000` (7 dias).

## 2. Servicio Frontend (`frontend`)

> [!IMPORTANT]
> El frontend es una SPA estática construida en tiempo de compilación. Por lo tanto, la variable `VITE_API_BASE_URL` se inyecta **durante el build** del contenedor Docker y no puede ser cambiada dinámicamente en tiempo de ejecución sin reconstruir el contenedor.

| Variable | Descripción | Valor Recomendado / Ejemplo | Origen en Railway |
| :--- | :--- | :--- | :--- |
| `VITE_API_BASE_URL` | URL base pública de la API del backend. | `https://${{backend.RAILWAY_PUBLIC_DOMAIN}}` o dominio directo de producción del backend. | Manual |
