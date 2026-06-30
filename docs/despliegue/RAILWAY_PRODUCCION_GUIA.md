# Guía de Despliegue en Producción - Railway

Esta guía describe el proceso paso a paso para desplegar el proyecto fullstack `notes` en la plataforma Railway.

## Arquitectura en Railway
El proyecto consta de tres servicios principales en Railway:
1. **Base de Datos**: PostgreSQL administrado.
2. **Servicio Backend**: Aplicación Spring Boot (Java 26) ejecutándose en un contenedor Docker.
3. **Servicio Frontend**: Aplicación React + Vite servida por un servidor web Caddy en un contenedor Docker.

---

## Paso a Paso del Despliegue

### Paso 1: Crear el Proyecto en Railway
1. Inicia sesión en [Railway](https://railway.app/).
2. Haz clic en **New Project** y selecciona **Empty Project**.
3. Cambia el nombre del proyecto a `notes-prod` en la configuración del proyecto.

### Paso 2: Agregar la Base de Datos PostgreSQL
1. Dentro del proyecto, haz clic en **+ Add** -> **Database** -> **Add PostgreSQL**.
2. Railway creará una base de datos administrada y generará automáticamente las variables de conexión (`DATABASE_URL`, `PGPASSWORD`, etc.).

### Paso 3: Desplegar el Servicio Backend
1. Haz clic en **+ Add** -> **GitHub Repo**.
2. Selecciona tu repositorio `notes`.
3. En la configuración del servicio recién creado (haz clic sobre él y ve a **Settings**):
   - **Service Name**: Cambia el nombre a `backend`.
   - **Root Directory**: Configúralo como `backend`.
4. Ve a la pestaña **Variables** y agrega las siguientes variables de entorno:
   - `SPRING_PROFILES_ACTIVE=prod` (obligatorio para cargar configuracion de produccion y deshabilitar Scalar/OpenAPI)
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.DATABASE_URL}}` (Railway resolverá esto automáticamente usando la base de datos vinculada)
   - `SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}`
   - `SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}`
   - `JWT_SECRET` (Genera una cadena aleatoria y segura de al menos 256 bits)
   - `JWT_EXPIRATION_MS=86400000` (24 horas)
   - `CORS_ALLOWED_ORIGINS=https://${{frontend.RAILWAY_PUBLIC_DOMAIN}}` (Se actualizará en el paso 6)
5. Ve a **Settings** y en la sección **Environment** expón el puerto agregando una **Public Domain** (esto generará el dominio público del backend, ej: `backend-production.up.railway.app`).
6. Copia esta URL del backend para usarla en el frontend.

### Paso 4: Desplegar el Servicio Frontend
1. Haz clic en **+ Add** -> **GitHub Repo**.
2. Selecciona el mismo repositorio `notes`.
3. En la configuración de este servicio (haz clic sobre él y ve a **Settings**):
   - **Service Name**: Cambia el nombre a `frontend`.
   - **Root Directory**: Configúralo como `frontend`.
4. Ve a la pestaña **Variables** y agrega la variable necesaria para el build de Vite:
   - `VITE_API_BASE_URL=https://<tu-dominio-backend>.up.railway.app` (Usa el dominio público copiado en el Paso 3).
5. Ve a **Settings** y genera una **Public Domain** para el frontend.
6. Copia esta URL del frontend.

### Paso 5: Actualizar el CORS en el Backend
1. Regresa al servicio `backend` en Railway.
2. Ve a la pestaña **Variables**.
3. Actualiza la variable `CORS_ALLOWED_ORIGINS` con el dominio público del frontend (ej: `https://frontend-production.up.railway.app`).
4. Railway reiniciará y redesplegará automáticamente el backend con la nueva configuración.

---

## Verificación del Despliegue
- Accede al dominio público del frontend en tu navegador.
- Verifica que las llamadas a la API (ej: `/actuator/health` o endpoints de negocio) funcionen correctamente a través de la pestaña Red de las herramientas de desarrollo del navegador.
- Verifica que `/docs` (Scalar) y la documentacion OpenAPI no entreguen contenido publico en el dominio del backend (deben responder con error 404 o similar).
- Verifica que `/actuator/health` responda sin exponer detalles sensibles.
- Comprueba que las rutas de la SPA funcionen al recargar la página (gracias a la regla de fallback en `Caddyfile`).
