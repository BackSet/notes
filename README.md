# Notes Fullstack Project

Aplicacion full-stack para notas, organizada como monorepo con backend Spring Boot, frontend React/Vite y PostgreSQL local mediante Docker Compose.

## Stack Actual

- Backend: Java 26, Spring Boot 4.1.0, Maven, Spring Web, Spring Security, OAuth2 Resource Server, JPA, PostgreSQL, Flyway, Actuator, Springdoc OpenAPI y Scalar.
- Frontend: React 19, Vite 8, TypeScript, Tailwind CSS, TanStack Router, TanStack Query, Zustand, React Hook Form, Zod, Sonner y openapi-fetch.
- Infraestructura: Docker Compose para PostgreSQL local, Dockerfile por servicio y guias Railway.

## Estructura

```text
/
|-- backend/            # Servicio Spring Boot
|-- frontend/           # SPA React + Vite
|-- docs/ai/            # Documentacion canonica para agentes y humanos
|-- docs/despliegue/    # Guias de despliegue y variables
|-- docker-compose.yml  # PostgreSQL local
|-- README.md
```

## Blueprint Reutilizable

El archivo `docs/ai/PROJECT_INITIALIZATION_BLUEPRINT.md` contiene una plantilla generica para iniciar nuevos proyectos full-stack. No depende de este dominio ni fija versiones; el agente debe consultar fuentes oficiales o manifiestos generados al iniciar cada proyecto nuevo.

## Requisitos Locales

- Java 26.
- Maven 3.9+.
- Node.js 18+ y npm.
- Docker y Docker Compose.

## Base de Datos Local

```bash
docker compose up -d
```

El contenedor local usa PostgreSQL y los valores por defecto documentados en `backend/.env.example`.

## Backend

1. Copia la plantilla de entorno:

   ```bash
   cp backend/.env.example backend/.env
   ```

2. Ejecuta el servicio:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

El backend queda disponible en `http://localhost:8080`.

En perfil `dev`, Scalar queda disponible en:

```text
http://localhost:8080/docs
```

La especificacion OpenAPI usada por Scalar queda disponible en:

```text
http://localhost:8080/v3/api-docs
```

En perfil `prod`, Scalar (`/docs`) y OpenAPI (`/v3/api-docs`) estan deshabilitados. El healthcheck `/actuator/health` sigue disponible sin detalles sensibles.

## Perfiles Backend

Si `SPRING_PROFILES_ACTIVE` no se define, el backend usa `dev`.

Forzar desarrollo en PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
cd backend
mvn spring-boot:run
```

Validar comportamiento productivo en PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
cd backend
mvn spring-boot:run
```

## Frontend

1. Copia la plantilla de entorno:

   ```bash
   cp frontend/.env.example frontend/.env
   ```

2. Instala dependencias y ejecuta el modo desarrollo:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

El frontend queda disponible en `http://localhost:5173`.

## Comandos de Verificacion

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm run lint
npm run test
npm run build
```

## Diagnostico de Conexion a Base de Datos

Si el backend reporta error de autenticacion o conexion con PostgreSQL, verifica primero que `SPRING_DATASOURCE_PASSWORD` en `backend/.env` coincida con `POSTGRES_PASSWORD` en `docker-compose.yml`. Por defecto ambos usan `postgres`.

En PowerShell puedes revisar si variables de entorno de la sesion estan sobrescribiendo el archivo `.env`:

```powershell
Get-ChildItem Env:SPRING_DATASOURCE*
```

Si cambiaste la contrasena despues de crear el volumen local, PostgreSQL puede conservar la anterior. Este comando elimina los datos locales del volumen:

```bash
docker compose down -v
docker compose up -d
```

Para probar conexion desde el contenedor:

```bash
docker exec -e PGPASSWORD=postgres -it notes-db psql -h localhost -U postgres -d notes -c "select current_user, current_database();"
```
