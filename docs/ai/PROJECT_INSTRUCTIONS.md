# Instrucciones Permanentes del Proyecto

## Identidad

- Proyecto: `notes`.
- Repositorio: `BackSet/notes`.
- Rama activa esperada para trabajo actual: `dev`.
- Tipo: monorepo full-stack.

## Antes de Editar

1. Confirmar rama con `git branch --show-current`.
2. Revisar `git status --short`.
3. Leer el diff existente si el arbol no esta limpio.
4. Leer manifiestos antes de asumir comandos o dependencias:
   - `backend/pom.xml`.
   - `frontend/package.json`.
5. Leer documentacion canonica afectada:
   - `README.md`.
   - `docs/ai/PROJECT_CONTEXT.md`.
   - `docs/ai/MODULE_MAP.md`.
   - `docs/ai/NAMING.md`.
   - `docs/ai/PROJECT_INSTRUCTIONS.md`.
   - `docs/ai/PROJECT_INITIALIZATION_BLUEPRINT.md` si la tarea toca baseline reutilizable.

## Reglas de Implementacion

- No borrar auth, usuarios, roles, permisos, refresh tokens, bootstrap admin ni migraciones de seguridad salvo instruccion explicita.
- No inventar entidades de dominio como implementadas.
- No editar migraciones Flyway ya aplicadas; agregar nuevas migraciones.
- No versionar secretos.
- No poner secretos en variables `VITE_*`.
- No mezclar variables backend y frontend.
- No hardcodear URLs productivas.
- Confirmar comandos desde `pom.xml` y `package.json`.
- Mantener el frontend usando el cliente API centralizado.
- Mantener permisos en backend como fuente de verdad; ocultar controles en frontend no reemplaza autorizacion.

## API Docs y Produccion

- En `dev`, Scalar puede estar disponible en `/docs` y OpenAPI en `/v3/api-docs`.
- En `prod`, Scalar y OpenAPI deben estar deshabilitados.
- No documentar interfaces interactivas obsoletas; el proyecto usa Scalar para referencia interactiva en desarrollo.
- `/actuator/health` puede quedar expuesto sin detalles sensibles.

## Documentacion

Actualizar documentacion solo cuando cambien hechos canonicos:

- `PROJECT_CONTEXT.md`: stack, estructura, reglas y estado real.
- `MODULE_MAP.md`: modulos, endpoints, rutas, tests y pendientes reales.
- `NAMING.md`: convenciones vigentes.
- `PROJECT_INSTRUCTIONS.md`: reglas permanentes.
- `PROJECT_INITIALIZATION_BLUEPRINT.md`: baseline generico para proyectos futuros.

No usar estos documentos como changelog.

## Blueprint Generico

`PROJECT_INITIALIZATION_BLUEPRINT.md` debe:

- Ser reutilizable para proyectos nuevos.
- Usar placeholders.
- No depender del nombre `notes`.
- No usar rutas locales.
- No fijar versiones concretas.
- Indicar que el agente debe consultar fuentes oficiales o manifiestos generados antes de elegir versiones.
- Mantener entregas MVP independientes.

## Validacion

Si solo cambian documentos:

- Revisar `git diff -- README.md docs`.
- Revisar `git status --short`.
- No ejecutar tests funcionales salvo que se haya cambiado codigo, manifiestos o configuracion ejecutable.

Si cambia backend:

```bash
cd backend
mvn test
```

Si cambia frontend:

```bash
cd frontend
npm run lint
npm run test
npm run build
```

## Protocolo de Continuidad

Cuando una tarea continue en otro turno o agente, dejar claro:

- Rama.
- Estado `git status --short`.
- Archivos modificados.
- Que se valido y que no.
- Pendientes reales.
- Riesgos conocidos.

Formato sugerido:

```text
Continua la tarea en BackSet/notes, rama dev. Antes de editar, revisa git status --short, el diff y los documentos canonicos. No borres auth/RBAC/admin/bootstrap ni documentes dominio de notas como implementado si no existe en codigo.
```
