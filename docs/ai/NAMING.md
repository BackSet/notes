# Naming Conventions & Glossary

Este documento define los términos canónicos del dominio, convenciones de nomenclatura y mapeo técnico para el proyecto `notes`.

## 1. Glosario de Términos del Dominio `[inferido]`

| Término (Inglés) | Término (Español) | Descripción | Variantes Prohibidas |
| :--- | :--- | :--- | :--- |
| `User` | Usuario | Cuenta de usuario registrada en la aplicación. | `Account`, `Member` |
| `Role` | Rol | Conjunto nombrado de permisos asignable a usuarios (ej: `ADMIN`). | `Group`, `Profile` |
| `Permission` | Permiso | Autorización explícita y atómica (ej: `USER_READ`). | `Grant`, `Privilege`, `Authority` |
| `RefreshToken` | Token de refresco | Token persistido (solo su hash) para renovar la sesión. | `SessionToken` |
| `Note` | Nota | Elemento principal de información creado por un usuario. | `Memo`, `Post`, `Record` |
| `Category` | Categoría | Agrupación o etiqueta para organizar las notas. | `Tag`, `Label`, `Folder` |

---

## 2. Reglas de Nomenclatura por Contexto

### A. Interfaz de Usuario (UI) `[inferido]`
* Componentes de React en **PascalCase** (ej: `NoteCard.tsx`, `Sidebar.tsx`).
* Clases CSS: Clases utilitarias de Tailwind en minúsculas y separadas por espacios.
* Archivos de traducción u etiquetas: En español o inglés según lo defina el diseño final `[pendiente de confirmar]`.

### B. API y JSON `[inferido]`
* Endpoints REST en minúsculas y pluralizados, usando `kebab-case` para palabras compuestas (ej: `/api/users`, `/api/notes`, `/api/note-categories`).
* Atributos de respuesta/solicitud en **camelCase** (ej: `createdAt`, `updatedAt`, `isArchived`).

### C. Backend (Java) `[verificado en documentación]`
* **Clases**: PascalCase (ej: `BackendApplication`, `NoteController`, `NoteService`).
* **Interfaces**: PascalCase (ej: `NoteRepository`).
* **Métodos**: camelCase (ej: `findById()`, `saveNote()`).
* **Variables**: camelCase (ej: `noteId`, `title`, `content`).
* **Paquetes**: Minúsculas continuas, siguiendo la jerarquía inversa del dominio (ej: `com.notes.backend.controller`).

### D. Base de Datos `[inferido]`
* **Tablas**: snake_case en plural (ej: `notes`, `users`, `categories`).
* **Columnas**: snake_case en singular (ej: `id`, `title`, `content`, `user_id`, `created_at`).
* **Claves Primarias**: Siempre `id` `[pendiente de confirmar]`.
* **Claves Foráneas**: `[nombre_tabla_singular]_id` (ej: `user_id`).

---

## 3. Permisos y Enums Canónicos
* **Roles** `[verificado en Git]`: el nombre canónico se almacena sin prefijo `ROLE_` (ej: `ADMIN`, constante `RoleName.ADMIN`). El único rol existente es `ADMIN`; el rol para usuarios registrados públicamente se definirá en una iteración posterior y nunca será `ADMIN`.
* **Permisos** `[verificado en Git]`: nombres en `UPPER_SNAKE_CASE` con formato `RECURSO_ACCION`, catalogados en el enum `BasePermission`. Base actual: `USER_READ`, `USER_CREATE`, `USER_UPDATE`, `USER_DISABLE`, `ROLE_READ`, `PERMISSION_READ`. Todos se asignan al rol `ADMIN`.
* Enums de estado de la nota (ej: activa, archivada, papelera) `[pendiente de confirmar]`.
* **Claims del access token JWT** `[verificado en Git]`: `sub` (id de usuario), `username`, `email`, `roles`, `permissions`, `iat`, `exp`. El refresh token es opaco (no es un JWT) y se almacena solo como hash.
* **Claves de sesión en el frontend** `[verificado en Git]`: `auth_token` (access token) y `auth_refresh_token` (refresh token) en `localStorage`. Constantes de permiso en `lib/auth/permissions.ts` (objeto `Permission`).

---

## 4. Nombres Históricos Prohibidos `[verificado en documentación]`
* Evitar el uso de `master` para ramas principales; la rama por defecto ha sido renombrada a `main` `[verificado en Git]`.
* No utilizar términos como `todo` o `task` para referirse al objeto principal del negocio, el cual se denomina de manera exclusiva como `Note` `[inferido]`.
