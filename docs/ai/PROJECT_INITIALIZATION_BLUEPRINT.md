# Blueprint Generico de Inicializacion Full-Stack

## 1. Proposito

Este documento define una plantilla reutilizable para iniciar proyectos full-stack desde cero mediante entregas MVP. Debe guiar a un Agente de implementacion para crear una base tecnica con monorepo, backend, frontend, seguridad inicial, despliegue y documentacion canonica antes de agregar cualquier dominio de negocio.

Usa placeholders como `[NOMBRE_PROYECTO]`, `[OWNER/REPO]`, `[RAMA]`, `[DOMINIO]`, `[BACKEND_DIR]` y `[FRONTEND_DIR]`. No copies este documento como changelog y no agregues entidades de negocio ficticias.

Parametros minimos que el Agente debe pedir o inferir antes de iniciar:

- `[NOMBRE_PROYECTO]`: nombre humano y tecnico del proyecto.
- `[OWNER/REPO]`: repositorio remoto objetivo.
- `[RAMA]`: rama inicial de trabajo.
- `[DOMINIO]`: dominio de negocio real, si ya esta definido.
- `[BACKEND_DIR]`: directorio del servicio backend.
- `[FRONTEND_DIR]`: directorio del servicio frontend.

## 2. Principios

- Construir una base minima, segura y extensible.
- Separar backend, frontend, variables y despliegue por servicio.
- Mantener usuarios, roles y permisos como baseline de seguridad inicial cuando el proyecto lo requiera.
- Documentar decisiones canonicas en `docs/ai/`.
- Confirmar comandos desde manifiestos antes de ejecutarlos.
- No versionar secretos.
- No hardcodear URLs productivas.
- No inventar dominio de negocio durante la inicializacion.
- No usar dependencias inestables salvo autorizacion explicita.

Regla obligatoria de versiones:

> Al iniciar un proyecto nuevo, el Agente debe consultar las fuentes oficiales o los manifiestos generados para seleccionar las ultimas versiones estables compatibles disponibles en ese momento. No debe quemar versiones en la plantilla ni copiar versiones antiguas de este repositorio sin verificarlas.

## 3. Alcance

Incluido:

- Monorepo con `[BACKEND_DIR]/`, `[FRONTEND_DIR]/`, `docs/ai/`, `docs/despliegue/`, CI y soporte local.
- Backend Spring Boot con REST, persistencia, migraciones, healthcheck y seguridad.
- Frontend React/Vite con router, estado, cliente API, formularios, tema y estados UI.
- PostgreSQL local y administrado en despliegue.
- Railway separado por servicio.
- Dockerfile por servicio.
- Variables por servicio.
- Documentacion canonica para humanos y agentes.

Fuera de alcance:

- Entidades de negocio especificas.
- Dashboards o KPIs decorativos.
- Integraciones externas no solicitadas.
- Migraciones de dominio inventadas.
- Optimizaciones prematuras o abstracciones sin uso.

## 4. Stack Base Esperado Sin Versiones Quemadas

Backend:

- Java estable compatible con la version de Spring Boot seleccionada.
- Spring Boot estable.
- Maven.
- Spring Web.
- Spring Data JPA.
- Spring Security.
- OAuth2 Resource Server o equivalente para validar JWT cuando aplique.
- Validation.
- PostgreSQL driver.
- Flyway.
- Actuator.
- Springdoc OpenAPI y Scalar solo en desarrollo, si se desea documentacion interactiva.
- Base de datos en memoria o contenedor descartable para tests.

Frontend:

- React estable.
- Vite estable.
- TypeScript estable.
- Tailwind CSS estable.
- TanStack Router.
- TanStack Query.
- Zustand.
- React Hook Form.
- Zod.
- Sonner.
- `openapi-fetch`.
- Vitest.
- Testing Library.
- Iconos desde una libreria instalada o aprobada.

Infraestructura:

- PostgreSQL.
- Docker por servicio.
- Caddy u otro servidor estatico para SPA.
- Railway por servicio.
- GitHub Actions u otro CI equivalente.

## 5. Estructura Monorepo Objetivo

```text
/
|-- .github/workflows/
|-- [BACKEND_DIR]/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- railway.json
|   |-- .env.example
|-- [FRONTEND_DIR]/
|   |-- src/
|   |-- package.json
|   |-- Dockerfile
|   |-- Caddyfile
|   |-- railway.json
|   |-- .env.example
|-- docs/
|   |-- ai/
|   |-- despliegue/
|-- docker-compose.yml
|-- README.md
```

No crees una plantilla raiz de variables si los servicios ya tienen variables propias. Si existe una plantilla raiz por decision del proyecto, debe ser solo agregada local y no reemplazar las plantillas por servicio.

## 6. Variables de Entorno por Nivel

Raiz:

- Usar solo variables que realmente apliquen al monorepo completo.
- No mezclar secretos de backend con valores publicos de frontend.
- Preferir plantillas por servicio.

Backend:

- `PORT`.
- `SPRING_PROFILES_ACTIVE`.
- `SPRING_DATASOURCE_URL`.
- `SPRING_DATASOURCE_USERNAME`.
- `SPRING_DATASOURCE_PASSWORD`.
- `JWT_SECRET`.
- Variable de expiracion del access token.
- Variable de expiracion del refresh token.
- `CORS_ALLOWED_ORIGINS`.
- Variables de bootstrap del administrador inicial.

Frontend:

- `VITE_API_BASE_URL`.
- Solo valores seguros para el navegador.
- Nunca credenciales, secretos JWT ni datos de base de datos.

Railway backend:

- Perfil productivo.
- Datasource desde PostgreSQL administrado.
- Secretos JWT generados fuera del repo.
- CORS con el origen exacto del frontend.
- Healthcheck del backend.

Railway frontend:

- URL publica del backend para el build.
- Variables de build seguras.
- Dominio publico del frontend.

## 7. Seguridad Base

Usuarios:

- Entidad persistente para cuentas.
- Identificador unico, email o username segun el producto.
- Password hash, nunca password plano.
- Estado de habilitacion.

Roles:

- Rol administrativo inicial.
- Roles persistentes si el proyecto requiere RBAC desde el inicio.
- No asignar rol administrador a registros publicos por defecto.

Permisos:

- Permisos atomicos en formato consistente.
- Endpoints administrativos protegidos por permisos.
- Catalogo base documentado.

JWT:

- Access token corto.
- Claims minimos: subject, identidad publica, roles, permisos y expiracion.
- Firma con secreto fuerte fuera del repo.

Refresh tokens:

- Token opaco de alta entropia.
- Persistir solo hash.
- Revocacion individual y global.
- Expiracion configurable.

Bootstrap admin:

- Controlado por variables.
- Deshabilitado por defecto salvo decision explicita.
- Nunca versionar credenciales reales.

CORS:

- Configurable por variable.
- En produccion, usar el origen exacto del frontend.

Secretos:

- Fuera del repo.
- Placeholders `CHANGE_ME` solo en `.env.example`.
- Railway o gestor de secretos para valores reales.

## 8. Backend Base

Objetivo:

- Servicio REST inicial que compila, levanta contexto, valida migraciones y expone healthcheck.

Elementos esperados:

- Entry point Spring Boot.
- Configuracion por perfiles.
- Datasource comun y perfiles `dev`/`prod`.
- Flyway con migraciones inmutables.
- Seguridad stateless cuando auth este incluida.
- Controladores auth/admin si seguridad inicial esta incluida.
- DTOs validados.
- Servicios transaccionales.
- Repositorios JPA.
- Tests de contexto, auth, RBAC y bootstrap si existen esos modulos.

Reglas:

- No usar `ddl-auto=update` en produccion.
- No exponer Scalar ni OpenAPI en produccion.
- No editar migraciones ya aplicadas.
- No hardcodear credenciales.

## 9. Frontend Base

Objetivo:

- SPA inicial usable con autenticacion, layout, rutas protegidas, cliente API y UI base.

Elementos esperados:

- `main.tsx`.
- Router.
- Store de sesion.
- Cliente `openapi-fetch`.
- Helpers de tokens.
- Guards de autenticacion y permisos.
- Paginas de login, registro, dashboard y administracion inicial si auth/admin existen.
- Componentes UI base.
- Tests de permisos, sesion y componentes.

Reglas:

- No usar Axios si el cliente oficial es `openapi-fetch`.
- No hacer llamadas HTTP directas desde componentes si existe capa API.
- No duplicar librerias UI.
- Mantener TypeScript estricto.
- Evitar colores hardcodeados en componentes.
- Evitar `transition-all`.

## 10. Sistema UI Base

Tema general:

- Tokens para background, foreground, card, popover, primary, secondary, muted, accent, destructive, success, warning, info, border, input y ring.
- Radius, sombras y motion.
- Mapeo Tailwind semantico.

Dark mode:

- Soporte con `.dark`.
- No depender de colores hardcodeados en componentes.

Responsive mobile-first:

- Layout raiz adaptable.
- Navegacion mobile-first.
- Formularios con ancho controlado.
- Tablas con `overflow-x-auto` cuando sea necesario.

Skeleton pattern:

- Componente `Skeleton`.
- Variantes estructurales para pagina, tabla/lista y tarjetas.

Estados:

- `LoadingState`.
- `EmptyState`.
- `ErrorState`.
- Acciones de reintento cuando aplique.

Otros componentes:

- `PageHeader`.
- `ConfirmDialog`.
- `SurfaceCard`.
- `Toaster`.

Accesibilidad:

- Labels en formularios.
- Mensajes con `role="alert"` cuando aplique.
- Foco visible.
- Dialogos con roles adecuados.

Reduced motion:

- Respetar `prefers-reduced-motion`.
- Usar `motion-safe` para animaciones.

## 11. Railway y Despliegue

Arquitectura:

- Servicio PostgreSQL administrado.
- Servicio backend.
- Servicio frontend.

Backend:

- Root directory: `[BACKEND_DIR]`.
- Dockerfile propio.
- Perfil productivo.
- Variables datasource desde PostgreSQL.
- Variables JWT y CORS configuradas en Railway.
- Healthcheck.
- Scalar/OpenAPI deshabilitados en produccion.

Frontend:

- Root directory: `[FRONTEND_DIR]`.
- Dockerfile propio.
- SPA servida por Caddy u otro servidor estatico.
- `VITE_API_BASE_URL` definido en build.
- Rebuild cuando cambie la URL del backend.

Orden:

1. Crear PostgreSQL.
2. Configurar backend.
3. Publicar backend.
4. Configurar frontend con URL del backend.
5. Publicar frontend.
6. Actualizar CORS backend con origen frontend.
7. Verificar healthcheck, login, rutas SPA y bloqueo de docs API en prod.

## 12. Testing y Validacion

Confirmar comandos desde manifiestos.

Backend esperado:

```bash
cd [BACKEND_DIR]
[COMANDO_TEST_BACKEND]
```

Frontend esperado:

```bash
cd [FRONTEND_DIR]
[COMANDO_LINT_FRONTEND]
[COMANDO_TEST_FRONTEND]
[COMANDO_BUILD_FRONTEND]
```

Validaciones minimas:

- Backend compila.
- Tests backend pasan.
- Frontend lint pasa.
- Tests frontend pasan.
- Build frontend pasa.
- No secretos en diff.
- No referencias a dominios productivos hardcodeados.

## 13. Documentacion `docs/ai`

Archivos canonicos:

- `PROJECT_CONTEXT.md`.
- `MODULE_MAP.md`.
- `NAMING.md`.
- `PROJECT_INSTRUCTIONS.md`.
- `PROJECT_INITIALIZATION_BLUEPRINT.md`.

Cada archivo debe reflejar hechos actuales, no deseos. Marca como pendiente lo no confirmado.

## 14. Mantenimiento del Contexto

Despues de cada MVP:

1. Revisar cambios reales.
2. Actualizar docs canonicos afectados.
3. Confirmar comandos desde manifiestos.
4. Registrar pendientes sin convertirlos en implementados.
5. Mantener el blueprint generico sin versiones quemadas.

## 15. Entregas MVP

### MVP 1 - Estructura monorepo y backend base

Objetivo:

- Crear el monorepo y un backend base ejecutable.

Valor entregado:

- Base backend compilable con perfiles, datasource, healthcheck y migraciones.

Alcance:

- `[BACKEND_DIR]/`.
- Maven.
- Spring Boot.
- PostgreSQL local.
- Flyway.
- Actuator.
- Dockerfile backend.

Fuera de alcance:

- UI.
- Dominio de negocio.
- Auth avanzada si se planifica para MVP 2.

Archivos esperados:

- `[BACKEND_DIR]/pom.xml`.
- `[BACKEND_DIR]/src/`.
- `[BACKEND_DIR]/.env.example`.
- `[BACKEND_DIR]/Dockerfile`.
- `docker-compose.yml`.

Pasos:

1. Confirmar versiones estables.
2. Crear backend.
3. Configurar perfiles.
4. Configurar datasource y Flyway.
5. Agregar healthcheck.
6. Agregar test de contexto.

Criterios de aceptacion:

- Backend compila.
- Test de contexto pasa.
- No hay secretos.

Comandos a confirmar:

- `[COMANDO_TEST_BACKEND]`.

Riesgos:

- Incompatibilidad entre Java y Spring Boot.
- Configuracion de datasource no portable.

Estado de salida:

- Backend base listo para seguridad inicial.

### MVP 2 - Seguridad inicial: usuarios, roles, permisos y autenticacion

Objetivo:

- Agregar baseline de seguridad.

Valor entregado:

- Registro/login, tokens, RBAC y admin inicial.

Alcance:

- Entidades `User`, `Role`, `Permission`, `RefreshToken`.
- Migracion de seguridad.
- JWT y refresh tokens.
- Bootstrap admin por variables.
- Endpoints auth/admin.
- Tests de seguridad.

Fuera de alcance:

- Entidades de negocio.
- UI final.

Archivos esperados:

- Paquetes auth/security/user/admin/bootstrap.
- Migraciones Flyway.
- Tests auth/admin/bootstrap.
- Variables en `[BACKEND_DIR]/.env.example`.

Pasos:

1. Modelar seguridad.
2. Crear migracion.
3. Configurar Spring Security.
4. Implementar auth.
5. Implementar admin inicial.
6. Probar flujos.

Criterios de aceptacion:

- Login y refresh funcionan.
- Permisos protegen endpoints.
- Admin bootstrap es opcional.
- Tests pasan.

Comandos a confirmar:

- `[COMANDO_TEST_BACKEND]`.

Riesgos:

- Exponer detalles de credenciales.
- Crear admin con secretos reales.

Estado de salida:

- Seguridad lista para integrar frontend.

### MVP 3 - Frontend base y sistema UI

Objetivo:

- Crear SPA base con layout y sistema visual.

Valor entregado:

- UI reutilizable con tema, estados y responsive.

Alcance:

- `[FRONTEND_DIR]/`.
- React/Vite.
- Router.
- Tailwind.
- Componentes UI base.
- Skeletons y estados.
- Tests UI.

Fuera de alcance:

- Dominio de negocio.
- Integracion completa si se planifica para MVP 4.

Archivos esperados:

- `src/main.tsx`.
- `src/router.tsx`.
- `src/index.css`.
- `tailwind.config.*`.
- `src/components/ui/`.

Pasos:

1. Confirmar dependencias estables.
2. Crear estructura frontend.
3. Configurar tema.
4. Crear componentes base.
5. Crear rutas iniciales.
6. Probar UI.

Criterios de aceptacion:

- Frontend compila.
- Lint y tests pasan.
- Dark mode y reduced motion contemplados.
- No colores hardcodeados en componentes nuevos.

Comandos a confirmar:

- `[COMANDO_LINT_FRONTEND]`.
- `[COMANDO_TEST_FRONTEND]`.
- `[COMANDO_BUILD_FRONTEND]`.

Riesgos:

- Duplicar librerias UI.
- Crear landing en vez de app usable.

Estado de salida:

- UI lista para integracion con API.

### MVP 4 - Integracion frontend-backend y cliente API

Objetivo:

- Conectar SPA con backend.

Valor entregado:

- Sesion frontend, cliente API y rutas protegidas.

Alcance:

- Cliente HTTP.
- Token storage.
- Refresh automatico.
- Login/registro.
- Guards auth/permiso.
- Vista admin inicial si aplica.

Fuera de alcance:

- Dominio de negocio.

Archivos esperados:

- `src/lib/api/`.
- `src/lib/auth/`.
- `src/stores/`.
- `src/components/auth/`.
- Paginas auth/admin.

Pasos:

1. Definir contrato API.
2. Crear cliente centralizado.
3. Implementar sesion.
4. Implementar formularios.
5. Implementar guards.
6. Probar flujos.

Criterios de aceptacion:

- Login funciona.
- Refresh funciona.
- Logout limpia sesion.
- Rutas protegidas redirigen.
- Permisos ocultan controles sin reemplazar autorizacion backend.

Comandos a confirmar:

- `[COMANDO_TEST_FRONTEND]`.
- `[COMANDO_BUILD_FRONTEND]`.
- `[COMANDO_TEST_BACKEND]`.

Riesgos:

- Guardar secretos en frontend.
- Saltar capa API.

Estado de salida:

- App base integrada y lista para despliegue.

### MVP 5 - Railway, Docker, variables y despliegue

Objetivo:

- Preparar despliegue productivo por servicio.

Valor entregado:

- Backend, frontend y PostgreSQL desplegables separadamente.

Alcance:

- Dockerfile backend.
- Dockerfile frontend.
- Caddyfile.
- `railway.json` por servicio.
- Docs de variables y despliegue.

Fuera de alcance:

- Observabilidad avanzada.
- Dominios definitivos si no existen.

Archivos esperados:

- `[BACKEND_DIR]/Dockerfile`.
- `[BACKEND_DIR]/railway.json`.
- `[FRONTEND_DIR]/Dockerfile`.
- `[FRONTEND_DIR]/Caddyfile`.
- `[FRONTEND_DIR]/railway.json`.
- `docs/despliegue/`.

Pasos:

1. Configurar Docker backend.
2. Configurar Docker frontend.
3. Configurar Railway backend.
4. Configurar Railway frontend.
5. Documentar variables.
6. Verificar CORS.

Criterios de aceptacion:

- Healthcheck backend definido.
- Frontend sirve SPA.
- Variables separadas por servicio.
- Docs API bloqueadas en prod.

Comandos a confirmar:

- `[COMANDO_TEST_BACKEND]`.
- `[COMANDO_BUILD_FRONTEND]`.

Riesgos:

- Variable frontend inyectada en runtime cuando debe ser build-time.
- CORS con comodines en produccion.

Estado de salida:

- Proyecto listo para verificacion productiva.

### MVP 6 - Documentacion canonica, pruebas y cierre

Objetivo:

- Dejar contexto completo para humanos y agentes.

Valor entregado:

- Documentacion canonica alineada con codigo y despliegue.

Alcance:

- README.
- `docs/ai/`.
- `docs/despliegue/`.
- Validaciones finales.

Fuera de alcance:

- Nuevas features.
- Refactors funcionales.

Archivos esperados:

- `README.md`.
- `docs/ai/PROJECT_CONTEXT.md`.
- `docs/ai/MODULE_MAP.md`.
- `docs/ai/NAMING.md`.
- `docs/ai/PROJECT_INSTRUCTIONS.md`.
- `docs/ai/PROJECT_INITIALIZATION_BLUEPRINT.md`.

Pasos:

1. Revisar estructura real.
2. Revisar manifiestos.
3. Revisar variables.
4. Actualizar docs canonicos.
5. Ejecutar validaciones.
6. Revisar diff y secretos.

Criterios de aceptacion:

- Docs no contradicen codigo.
- Blueprint no contiene versiones quemadas.
- Blueprint no contiene nombres de proyecto concretos.
- No hay secretos.
- Tests/builds relevantes pasan o quedan diagnosticados.

Comandos a confirmar:

- `git diff -- README.md docs`.
- `git status --short`.
- Comandos de backend/frontend si hubo cambios funcionales.

Riesgos:

- Documentar deseos como implementados.
- Copiar rutas locales o nombres privados.

Estado de salida:

- Baseline listo para reutilizarse en nuevos proyectos.
