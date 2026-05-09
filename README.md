# KidCare — Microservicio de Acceso

Microservicio encargado de gestionar los accesos de delegados sobre menores y la generación de enlaces temporales para que el médico acceda a la bitácora durante la consulta.

---

## Tecnologías

- Java 21
- Spring Boot 3.5.14
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA
- MySQL
- Lombok
- Maven

---

## Puerto

```
8082
```

---

## Estructura del proyecto

```
src/main/java/com/kidcare/acceso_service/
│
├── model/
│   ├── Acceso.java           → Entidad que representa el permiso otorgado por un tutor sobre un menor
│   ├── Delegado.java         → Entidad que vincula un acceso con un usuario delegado registrado
│   ├── TokenMedico.java      → Entidad que almacena el enlace temporal generado para el médico
│   └── LogAccesoMedico.java  → Entidad que registra los eventos del ciclo de vida de un token médico
│
├── repository/
│   ├── AccesoRepository.java          → Acceso a datos de Acceso (búsqueda por tutor y menor)
│   ├── DelegadoRepository.java        → Acceso a datos de Delegado (búsqueda por acceso y usuario)
│   ├── TokenMedicoRepository.java     → Acceso a datos de TokenMedico (búsqueda por token y estado)
│   └── LogAccesoMedicoRepository.java → Acceso a datos de LogAccesoMedico (logs por token)
│
├── dto/
│   ├── AccesoRequestDTO.java          → Datos para crear un acceso de delegado
│   ├── TokenMedicoRequestDTO.java     → Datos para generar un enlace temporal para el médico
│   ├── TokenMedicoResponseDTO.java    → Datos de respuesta del enlace temporal generado
│   └── VerificarAccesoRequestDTO.java → Token y ubicación del médico para verificar proximidad
│
├── security/
│   ├── JwtUtil.java        → Genera, valida y extrae datos de tokens JWT
│   ├── JwtFilter.java      → Intercepta cada request y valida el token JWT del header
│   └── SecurityConfig.java → Configura rutas públicas, protegidas y política de sesión
│
├── service/
│   ├── AccesoService.java       → Lógica de creación, revocación y listado de accesos de delegados
│   └── TokenMedicoService.java  → Lógica de generación, verificación geográfica y revocación de tokens
│
├── controller/
│   ├── AccesoController.java       → Endpoints CRUD de /api/acceso
│   └── TokenMedicoController.java  → Endpoints de /api/acceso/medico (generar, verificar, revocar)
│
└── exception/
    └── GlobalExceptionHandler.java → Maneja errores de validación y excepciones de negocio
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
5. Al abrir el enlace, el médico debe estar a **100 metros o menos** del tutor (verificación por geoposición).
6. La posición del médico no se almacena en ninguna base de datos.

---

## Requisitos previos

- Java 21 instalado
- Maven instalado
- MySQL corriendo (cuando se conecte la BD)
- VS Code con Extension Pack for Java y Spring Boot Extension Pack

---

## Cómo iniciar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/vareeth227/KidCare_Acceso_Backend.git
cd KidCare_Acceso_Backend
```

### 2. Configurar variables de entorno

Edita el archivo `src/main/resources/application.properties` con tus datos de MySQL cuando tengas la base de datos lista:

```properties
server.port=8082
spring.application.name=acceso-service
spring.datasource.url=jdbc:mysql://localhost:3306/db_access
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=kidcare-secret-key-2024-segura-32chars
jwt.expiration=86400000
```

### 3. Compilar el proyecto

```bash
mvn clean install -DskipTests
```

### 4. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

El microservicio estará disponible en `http://localhost:8082`

---

## Notas importantes

- El token JWT debe enviarse en el header `Authorization: Bearer <token>` en todas las rutas protegidas.
- La ruta `/api/acceso/medico/verificar` es pública porque el médico no tiene cuenta en el sistema.
- La clave `jwt.secret` debe ser la misma en todos los microservicios de KidCare.
- Por ahora la base de datos está desactivada en `application.properties`. Cuando se conecte Docker hay que eliminar la línea `spring.autoconfigure.exclude`.

---

## Integrantes

| Nombre | Rol |
|--------|-----|
| Génesis Rojas | Líder de Proyecto / DBA / Analista Funcional |
| Francisco Monsalve | Frontend Mobile / QA |
| Benjamín Peña | Backend / Integración IA / DevOps |
