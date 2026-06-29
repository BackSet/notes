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
├── .env.example                 # Variables de entorno de la raíz
└── README.md                    # Instrucciones iniciales de ejecución
```

## 3. Stack Tecnológico Confirmado
* **Backend**:
  * Java 26 (OpenJDK 26.0.1) `[verificado en documentación]`
  * Spring Boot 4.1.0 `[verificado en documentación]`
  * Maven 3.9.16 `[verificado en documentación]`
  * Spring Security `[verificado en documentación]`
  * Spring Data JPA `[verificado en documentación]`
  * Flyway Migration `[verificado en documentación]`
  * Spring Boot Actuator `[verificado en documentación]`
  * Springdoc OpenAPI (v2.6.0) `[verificado en documentación]`
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
* Generación de contraseña aleatoria por defecto en consola durante inicio de pruebas si no se configura la seguridad `[verificado en documentación]`.
* Autenticación basada en JWT `[inferido]`.
* Rutas protegidas y roles de usuario `[pendiente de confirmar]`.

## 7. Base de Datos y Migraciones
* PostgreSQL 16 para desarrollo y producción `[verificado en documentación]`.
* Flyway configurado para migraciones automatizadas en el backend `[verificado en documentación]`.
* Las migraciones de base de datos se almacenan en `backend/src/main/resources/db/migration/` `[inferido]`.
* H2 Database en memoria se utiliza para el entorno de pruebas unitarias/integración para evitar depender de una base de datos PostgreSQL real `[verificado en documentación]`.

## 8. Infraestructura y CI/CD
* **Local**: Docker Compose levanta base de datos PostgreSQL en el puerto 5432 `[verificado en documentación]`.
* **CI/CD**: GitHub Actions ejecuta pruebas del backend (Java 26, Maven) y frontend (Node 20, npm run build) en cada push o PR a `main` o `master` `[verificado en documentación]`.
* **Producción**: Configurado para desplegarse en Railway `[verificado en documentación]`.
  * El backend corre en un contenedor Docker con perfil `prod`.
  * El frontend se compila y se sirve usando Caddy para optimizar el rendimiento de la SPA.

## 9. Comandos Confirmados
### Backend `[verificado en documentación]`
* Ejecutar pruebas: `mvn clean test` (ejecutado desde el directorio `backend/`).
* Ejecutar en desarrollo: `mvn spring-boot:run` (ejecutado desde el directorio `backend/`).

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
* No utilizar `axios` en el frontend; el cliente HTTP oficial es `openapi-fetch`.
* Evitar el uso de colores hex literales en componentes; usar las variables CSS mapeadas en Tailwind.

## 12. Pendientes de Confirmar
* Flujo de autenticación e inicio de sesión en el frontend `[pendiente de confirmar]`.
* Modelo de datos inicial y estructura de tablas específicas de notas `[pendiente de confirmar]`.
