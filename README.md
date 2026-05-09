# KidCare — Microservicio de Acceso

Microservicio encargado de gestionar los accesos de delegados sobre menores y la generación de enlaces temporales para que el médico acceda a la bitácora durante la consulta.

---

## Equipo

| Nombre | Rol |
|---|---|
| Génesis Rojas | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Benjamín Peña | Backend / Integración IA / DevOps |

---

## Tecnologías

- Java 21
- Spring Boot 3.5.14
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA
- MySQL 8.0 (Docker)
- Lombok
- Maven

**Puerto:** `8082`

---

## Estructura del proyecto

```
src/main/java/com/kidcare/acceso_service/
│
├── model/
│   ├── Acceso.java           → Permiso otorgado por un tutor sobre un menor
│   ├── Delegado.java         → Vincula un acceso con un usuario delegado registrado
│   ├── TokenMedico.java      → Enlace temporal generado para el médico
│   └── LogAccesoMedico.java  → Registra eventos del ciclo de vida de un token médico
│
├── repository/
│   ├── AccesoRepository.java          → Búsqueda por tutor y menor
│   ├── DelegadoRepository.java        → Búsqueda por acceso y usuario
│   ├── TokenMedicoRepository.java     → Búsqueda por token y estado
│   └── LogAccesoMedicoRepository.java → Logs por token
│
├── dto/
│   ├── AccesoRequestDTO.java          → Datos para crear un acceso de delegado
│   ├── TokenMedicoRequestDTO.java     → Datos para generar enlace temporal
│   ├── TokenMedicoResponseDTO.java    → Respuesta del enlace generado
│   └── VerificarAccesoRequestDTO.java → Token y ubicación del médico
│
├── security/
│   ├── JwtUtil.java        → Genera, valida y extrae datos de tokens JWT
│   ├── JwtFilter.java      → Intercepta requests y valida el JWT del header
│   └── SecurityConfig.java → Rutas públicas y protegidas, política de sesión stateless
│
├── service/
│   ├── AccesoService.java       → Creación, revocación y listado de accesos de delegados
│   └── TokenMedicoService.java  → Generación, verificación geográfica y revocación de tokens
│
├── controller/
│   ├── AccesoController.java       → CRUD /api/acceso
│   └── TokenMedicoController.java  → /api/acceso/medico
│
└── exception/
    └── GlobalExceptionHandler.java → Errores de validación → 400 Bad Request
```

---

## Endpoints

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| POST | `/api/acceso` | Autenticado | Crea un acceso de delegado sobre un menor |
| GET | `/api/acceso` | Autenticado | Lista todos los accesos del tutor |
| DELETE | `/api/acceso/{id}` | Autenticado | Revoca el acceso de un delegado |
| POST | `/api/acceso/medico/generar` | Autenticado | Genera un enlace temporal para el médico |
| POST | `/api/acceso/medico/verificar` | Público | Verifica proximidad geográfica del médico |
| DELETE | `/api/acceso/medico/revocar/{token}` | Autenticado | Revoca manualmente un token activo |

---

## Lógica del enlace temporal

1. El tutor genera un enlace desde la app ingresando el nombre del médico.
2. El backend genera un token único con `SecureRandom` y construye la URL.
3. Solo puede existir un enlace activo a la vez por menor — el anterior se invalida automáticamente.
4. El enlace expira automáticamente a los **20 minutos**.
5. Al abrir el enlace, el médico debe estar a **100 metros o menos** del tutor (verificación por geoposición con fórmula de Haversine).
6. La posición del médico no se almacena en ninguna base de datos.

---

## Cómo iniciar en otro equipo

### Prerrequisitos

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| Java JDK | 21 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org/download.cgi |
| Docker Desktop | 4.x | https://www.docker.com/products/docker-desktop |
| Git | cualquiera | https://git-scm.com |

Verifica la instalación:
```bash
java -version    # debe decir openjdk 21
mvn -version     # debe decir Apache Maven 3.9.x
docker --version # debe decir Docker version 24.x o superior
```

---

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/vareeth227/KidCare_Acceso_Backend.git
cd KidCare_Acceso_Backend
```

---

### Paso 2 — Iniciar MySQL con Docker

Crea el archivo `docker-compose.yml` en la carpeta raíz del proyecto:

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: kidcare-mysql
    environment:
      MYSQL_ROOT_PASSWORD: kidcare123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: >
      --default-authentication-plugin=mysql_native_password
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_unicode_ci

volumes:
  mysql_data:
```

Inicia el contenedor:

```bash
docker compose up -d
```

Espera 15–20 segundos y verifica:

```bash
docker ps
```

Debes ver `kidcare-mysql` con estado `Up`.

---

### Paso 3 — Crear la base de datos

```bash
docker exec -it kidcare-mysql mysql -u root -pkidcare123 -e "CREATE DATABASE IF NOT EXISTS db_acceso CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Verifica:

```bash
docker exec -it kidcare-mysql mysql -u root -pkidcare123 -e "SHOW DATABASES;"
```

Debes ver `db_acceso` en la lista.

---

### Paso 4 — Revisar application.properties

El archivo `src/main/resources/application.properties` ya está configurado para conectarse a MySQL local con las credenciales del Paso 2. No necesitas cambiar nada para desarrollo local.

---

### Paso 5 — Compilar

```bash
mvn clean install -DskipTests
```

Espera a que aparezca `BUILD SUCCESS`.

---

### Paso 6 — Ejecutar

```bash
mvn spring-boot:run
```

Espera a que aparezca:

```
Started AccesoServiceApplication in X.XXX seconds
```

El servicio queda disponible en `http://localhost:8082`.

---

### Paso 7 — Verificar

Necesitas un token JWT válido del usuario-service (puerto 8081). Con ese token:

**PowerShell:**
```powershell
$token = "eyJ..."  # pega tu token JWT aquí
Invoke-RestMethod -Uri "http://localhost:8082/api/acceso" -Method GET -Headers @{Authorization="Bearer $token"}
```

Respuesta esperada: lista vacía `[]` si no hay accesos registrados — esto confirma que el JWT fue validado correctamente.

---

## Notas importantes

- El token JWT debe enviarse en el header `Authorization: Bearer <token>` en todas las rutas protegidas.
- La ruta `/api/acceso/medico/verificar` es pública porque el médico no tiene cuenta en el sistema.
- La clave `jwt.secret` debe ser la misma en todos los microservicios de KidCare: `kidcare-secret-key-2024-segura-32chars`
- El JwtFilter extrae el claim `idUsuario` del JWT como principal — los controladores usan `Integer.parseInt(authentication.getName())` para obtener el ID del usuario autenticado.
