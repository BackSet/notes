# Module Map: Notes

Este mapa detalla la organización de los módulos del sistema, separando lo que ya está implementado en código de lo que se espera implementar.

## 1. Estructura de Directorios Técnicos `[verificado en Git]`
* **Backend (`backend/src/main/java/com/notes/backend/`)**:
  * Contiene únicamente el punto de entrada principal [BackendApplication.java](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/src/main/java/com/notes/backend/BackendApplication.java).
* **Frontend (`frontend/src/`)**:
  * Contiene los archivos base creados por el scaffold de Vite: [main.tsx](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/src/main.tsx), [App.tsx](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/src/App.tsx), y los componentes atómicos en `src/components/ui/`.

---

## 2. Módulos Funcionales y Técnicos

### A. Módulo de Autenticación y Usuarios `[esperado]`
* **Descripción**: Permitirá el registro, inicio de sesión y gestión de sesiones de usuario de forma segura.
* **Componentes Frontend**:
  * Páginas de Login y Registro `[pendiente de confirmar]`.
  * Store de autenticación `useAuthStore` en Zustand `[pendiente de confirmar]`.
* **Componentes Backend**:
  * `AuthController` y endpoints como `/api/auth/login`, `/api/auth/register` `[pendiente de confirmar]`.
  * Entidad `User` y tabla `users` `[pendiente de confirmar]`.
  * Filtros de Spring Security y validación de tokens JWT `[pendiente de confirmar]`.
* **Pruebas**:
  * Tests de integración para controladores de autenticación `[pendiente de confirmar]`.

### B. Módulo de Gestión de Notas `[esperado]`
* **Descripción**: Permitirá a los usuarios crear, leer, actualizar, borrar y organizar sus notas personales.
* **Componentes Frontend**:
  * Vista de panel/dashboard `[pendiente de confirmar]`.
  * Editor de notas, tarjetas de notas y listados `[pendiente de confirmar]`.
* **Componentes Backend**:
  * `NoteController` con endpoints CRUD (`/api/notes`) `[pendiente de confirmar]`.
  * `NoteService` y `NoteRepository` `[pendiente de confirmar]`.
  * Entidad `Note` y tabla `notes` `[pendiente de confirmar]`.
* **Pruebas**:
  * Tests unitarios para la lógica del servicio de notas `[pendiente de confirmar]`.

---

## 3. Endpoints del Backend Reales `[verificado en documentación]`
* **Actuator**:
  * `/actuator/health` - Comprobación de estado del servicio.
  * `/actuator/info` - Información del servicio.
  * `/actuator/prometheus` - Métricas de rendimiento.

---

## 4. Rutas del Frontend Reales `[verificado en Git]`
* `/` - Ruta raíz que renderiza el componente base [App.tsx](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/src/App.tsx). No hay rutas dinámicas configuradas aún.

---

## 5. Entidades y Persistencia Reales `[verificado en Git]`
* No existen entidades JPA ni tablas de base de datos mapeadas en el código actual de la rama `dev`.

---

## 6. Flujos Críticos `[inferido]`
1. **Inicio de Sesión y Carga de App**: El usuario se autentica, obtiene un JWT, este se almacena en `localStorage` y el cliente HTTP lo inyecta en las cabeceras de cada petición subsiguiente.
2. **Sincronización de Notas**: Carga de notas mediante TanStack Query, operaciones CRUD y mutaciones que invalidan la caché del servidor para mantener la interfaz actualizada.

---

## 7. Zonas de Búsqueda y Pruebas
* **Backend Tests**: Ubicados en `backend/src/test/java/com/notes/backend/` `[verificado en Git]`. Contiene [BackendApplicationTests.java](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/backend/src/test/java/com/notes/backend/BackendApplicationTests.java) que verifica la carga inicial del contexto.
* **Frontend Tests**: Ubicados en `frontend/src/components/ui/__tests__/` `[verificado en Git]`. Contiene [Button.test.tsx](file:///c:/Users/crist/OneDrive/Documents/proyects/notes/frontend/src/components/ui/__tests__/Button.test.tsx) que verifica el renderizado básico del botón.
