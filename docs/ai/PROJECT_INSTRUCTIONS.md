# Project Instructions & Guidelines

## 1. Identidad del Proyecto y Entorno `[verificado en Git]`
* **Proyecto**: `notes`
* **Repositorio**: `BackSet/notes`
* **Rama Base**: `dev`
* **Entorno**: Desarrollo local / Producción en Railway
* **Rol del Agente**: Asistente de codificación AI (Antigravity), actuando como Agente de Implementación Senior en pareja con el usuario.
* **Seguridad en Producción**: 
    * En producción, siempre asegure que `CORS_ALLOWED_ORIGINS` en el backend coincida con la URL de producción del frontend.
    * En producción, asegure que Scalar (`/docs`) y los endpoints de OpenAPI JSON (`/v3/api-docs`) estén deshabilitados (ejecutar con `SPRING_PROFILES_ACTIVE=prod`).

---

## 2. Fuentes de Verdad `[verificado en documentación]`
Para cualquier implementación técnica o consulta, se debe priorizar la información en el siguiente orden:
1. Código fuente real del repositorio en la rama `dev`.
2. Archivos de configuración y manifiestos de dependencias ([backend/pom.xml](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/pom.xml), [frontend/package.json](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/package.json)).
3. Guías de despliegue y variables de entorno ([docs/despliegue/RAILWAY_PRODUCCION_GUIA.md](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/docs/despliegue/RAILWAY_PRODUCCION_GUIA.md), [docs/despliegue/VARIABLES_ENTORNO.md](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/docs/despliegue/VARIABLES_ENTORNO.md)).
4. Archivos de contexto de la carpeta `docs/ai/`.

---

## 3. Reglas de Aislamiento y Restricciones `[verificado en documentación]`
* **Aislamiento**: No interactuar ni modificar archivos fuera del alcance definido por la tarea del usuario.
* **Seguridad**: Está estrictamente prohibido versionar claves secretas, contraseñas o tokens reales. Usa siempre variables de entorno y documenta en plantillas por módulo (`backend/.env.example`, `frontend/.env.example`).
* **Código Funcional**: No modificar ni refactorizar código funcional existente a menos que sea explícitamente solicitado para corregir un bug o implementar una característica.
* **Migraciones**: Las migraciones de base de datos creadas históricamente por Flyway en `db/migration/` son inmutables. Nunca edites un archivo de migración ya aplicado.
* **Dependencias**: No agregues nuevas dependencias de terceros sin evaluar primero si la funcionalidad se puede lograr con las librerías ya instaladas.
* **Perfiles Backend**: El backend se controla con `SPRING_PROFILES_ACTIVE`; `dev` es el valor local por defecto y `prod` es obligatorio en Railway. Swagger UI y `/v3/api-docs` solo deben estar disponibles en `dev`.

---

## 4. Obligación de Mantenimiento de Contexto
Después de cada ciclo de implementación o cambio en el repositorio, el Agente tiene la **obligación estricta** de:
1. Revisar si el cambio afecta la arquitectura, el mapa de módulos, las convenciones de nombres o las instrucciones del proyecto.
2. Actualizar los correspondientes archivos de contexto en `docs/ai/` (`PROJECT_CONTEXT.md`, `MODULE_MAP.md`, `NAMING.md`, `PROJECT_INSTRUCTIONS.md`).
3. Asegurar que no quede documentación desactualizada.

---

## 5. Formato de Reporte Final Obligatorio
Al finalizar cualquier tarea de desarrollo o inicialización, el Agente debe entregar un reporte estructurado con la siguiente información:
1. Rama revisada.
2. Archivos creados o actualizados.
3. Archivos canónicos revisados sin cambios, si aplica.
4. Fuentes del repositorio revisadas.
5. Decisiones importantes tomadas.
6. Contradicciones o desalineaciones detectadas.
7. Datos pendientes de confirmar.
8. Comandos ejecutados y resultados.
9. Pruebas no ejecutadas y motivo.
10. Riesgos y pendientes.
11. Confirmación de que no hubo cambios fuera del alcance.

---

## 6. Protocolo de Continuidad
Si una sesión de desarrollo se interrumpe antes de completar la tarea (por límite de contexto, expiración de tokens, etc.), el Agente **no debe declarar la tarea como terminada**. En su lugar, debe entregar el siguiente bloque estructurado en su último mensaje:

```markdown
RESUMEN DE CONTINUIDAD

Proyecto: notes
Repo: BackSet/notes
Rama: dev
Iteración: [Nombre de la iteración]

Objetivo actual:
[Descripción del objetivo]

Estado:
[no iniciado | en progreso | bloqueado | casi completo]

Completado y validado:
* [Logro 1]

Archivos modificados y propósito:
* [ruta]: [propósito]

Comandos y pruebas ejecutados:
* [comando]: [resultado]

Cambios parciales que no deben considerarse terminados:
* [detalle]

Pendientes en orden exacto de ejecución:
1. [pendiente 1]
2. [pendiente 2]

Archivos que aún deben revisarse o modificarse:
* [ruta]

Pruebas pendientes:
* [comando de prueba]

Errores, bloqueos, decisiones abiertas y riesgos:
* [detalle]

Siguiente acción concreta:
[siguiente paso]

Prompt de reanudación autocontenido:
Actúa como Agente de implementación senior. Continúa la tarea [Nombre] en BackSet/notes, rama dev. Antes de editar, revisa la rama activa, git status --short, el diff y los archivos indicados en este resumen. Verifica qué cambios ya existen y no repitas trabajo...
```
