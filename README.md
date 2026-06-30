# Notes Fullstack Project

This project is a fullstack notes application built with:
- **Backend**: Spring Boot (Java 26, Maven, JPA, PostgreSQL, Flyway, Actuator, Security)
- **Frontend**: React (Vite, TypeScript, Tailwind CSS, TanStack Router, TanStack Query, Zustand, Sonner, openapi-fetch)
- **Infrastructure**: Docker Compose (PostgreSQL)

## Project Structure

```
/
├── backend/            # Spring Boot backend
├── frontend/           # React + Vite frontend
├── docs/ai/            # AI-targeted documentation
├── docker-compose.yml  # Docker Compose for PostgreSQL
└── README.md           # This file
```

## Getting Started

### Prerequisites
- Java 26
- Maven 3.9+
- Node.js 18+ & npm
- Docker & Docker Compose

### 1. Database Setup
Start the PostgreSQL database using Docker Compose:
```bash
docker compose up -d
```

### 2. Backend Setup
1. Copy the environment template:
   ```bash
   cp backend/.env.example backend/.env
   ```
   > [!NOTE]
   > Spring Boot importa `backend/.env` de forma opcional cuando ejecutas Maven desde el directorio `backend/`. Si ejecutas Maven desde otra ubicación o prefieres no depender del archivo, exporta las variables en la sesión de terminal.
   > Solo se versiona `backend/.env.example`; el archivo real `backend/.env` no debe versionarse.

2. Ejecutar el backend:

   **En PowerShell (Windows):**
   ```powershell
   cd backend
   mvn spring-boot:run
   ```

   **En Bash (Linux/macOS/Git Bash):**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

   El backend estará disponible en `http://localhost:8080`.
   La referencia de la API interactiva (Scalar) estará disponible en `http://localhost:8080/docs` (solo en el perfil de desarrollo `dev`).

### Backend Profiles
El backend usa `SPRING_PROFILES_ACTIVE` para seleccionar entorno. Si no defines la variable, Spring Boot usa `dev`.

En `dev`, la documentación de API está habilitada:
```text
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

Para forzar desarrollo en PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
cd backend
mvn spring-boot:run
```

Para validar comportamiento de producción en PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
cd backend
mvn spring-boot:run
```

En `prod`, `/swagger-ui/index.html` y `/v3/api-docs` no deben devolver contenido público; una respuesta 401 o 404 es esperada. `/actuator/health` sigue disponible sin detalles sensibles.

### 3. Frontend Setup
1. Copy the environment template:
   ```bash
   cp frontend/.env.example frontend/.env
   ```
   Solo se versiona `frontend/.env.example`; el archivo real `frontend/.env` no debe versionarse.
2. Install dependencies and run in development mode:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   El frontend estará disponible en `http://localhost:5173`.

---

## Diagnóstico y Resolución de Problemas de Conexión (Base de Datos)

Si al ejecutar el backend obtienes el error `FATAL: la autenticación password falló para el usuario postgres` o problemas de conexión, sigue estos pasos:

### 1. Verificar Alineación de Contraseñas
La contraseña configurada en `backend/.env` (`SPRING_DATASOURCE_PASSWORD`) **debe coincidir** con la contraseña configurada en `docker-compose.yml` (`POSTGRES_PASSWORD`). Por defecto, ambas son `postgres`.

En PowerShell puedes verificar si hay variables de entorno sobrescribiendo el archivo `.env`:
```powershell
Get-ChildItem Env:SPRING_DATASOURCE*
```

Si necesitas exportarlas manualmente para la sesión actual:
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/notes"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
```

### 2. Recrear el Contenedor y su Volumen (Acción Destructiva)
Si cambiaste la contraseña en `docker-compose.yml` después de haber creado el contenedor por primera vez, PostgreSQL seguirá usando la contraseña vieja debido al volumen persistente `pgdata`. Resetear el volumen elimina todos los datos locales de la base de datos; hazlo solo si puedes perder esa información:
```bash
docker compose down -v
docker compose up -d
```

### 3. Conflicto de Puerto (Puerto 5432 Ocupado)
Si tienes una instancia local de PostgreSQL instalada directamente en tu sistema operativo (fuera de Docker), esta podría estar usando el puerto 5432 con una contraseña diferente.
- **Identificar el proceso en Windows (PowerShell):**
  ```powershell
  Get-Process -Id (Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue).OwningProcess -ErrorAction SilentlyContinue
  ```
- **Solución**: Detén el servicio de PostgreSQL local o cambia el puerto en `docker-compose.yml` (ej: `"5433:5432"`) y actualiza `SPRING_DATASOURCE_URL` en tu `.env` para apuntar al nuevo puerto.

### 4. Probar Conexión Manualmente
Puedes probar la conexión directa al contenedor PostgreSQL usando `psql` dentro del contenedor:
```bash
docker exec -e PGPASSWORD=postgres -it notes-db psql -h localhost -U postgres -d notes -c "select current_user, current_database();"
```
