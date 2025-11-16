# 💰 GroupGrow

Plataforma de inversión colaborativa que permite a grupos de personas invertir juntos de manera organizada, transparente y segura.

## 🚀 Características

- **Gestión de Grupos**: Crea y administra grupos de inversión con diferentes perfiles de riesgo
- **Dashboard Interactivo**: Visualiza el rendimiento de tus inversiones en tiempo real
- **Pagos y Contribuciones**: Sistema integrado de pagos con QR y seguimiento de transacciones
- **Autenticación Segura**: JWT + 2FA (TOTP) para máxima seguridad
- **IA Integrada**: Asistente inteligente con Gemini AI para asesoramiento financiero
- **Perfiles Personalizados**: Gestión de perfil de usuario y preferencias de inversión

## 🛠️ Tecnologías

### Backend
- **Spring Boot 3.5.6** (Java 17)
- **Spring Security** con JWT
- **MySQL** para persistencia de datos
- **Maven** para gestión de dependencias

### Frontend
- **React 18** con TypeScript
- **Vite** para build optimizado
- **Tailwind CSS** + **shadcn/ui** para UI moderna
- **Axios** para peticiones HTTP
- **React Router** para navegación

## 📋 Requisitos Previos

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

## ⚙️ Instalación

### Backend

1. Clona el repositorio y navega al directorio del backend:
```bash
cd groupgrow
```

2. Configura la base de datos en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/groupgrow
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. Ejecuta la aplicación:
```bash
./mvnw spring-boot:run
```

El backend estará disponible en `http://localhost:8080`

### Frontend

1. Navega al directorio del frontend:
```bash
cd react_groupgrow
```

2. Instala las dependencias:
```bash
npm install
```

3. Inicia el servidor de desarrollo:
```bash
npm run dev
```

El frontend estará disponible en `http://localhost:5173`

## 🔐 Variables de Entorno

Crea un archivo `.env` en el directorio del backend con:

```env
JWT_SECRET=tu_secreto_jwt_aqui
GEMINI_API_KEY=tu_api_key_de_gemini
```

## 📁 Estructura del Proyecto

```
groupgrow/
├── src/main/java/com/groupgrow/groupgrow/
│   ├── controller/     # Endpoints REST API
│   ├── service/        # Lógica de negocio
│   ├── repository/     # Acceso a datos
│   ├── model/          # Entidades JPA
│   ├── security/       # Configuración de seguridad
│   └── dto/            # Objetos de transferencia de datos

react_groupgrow/
├── src/
│   ├── components/     # Componentes React
│   ├── contexts/       # Context API
│   ├── services/       # Servicios API
│   └── styles/         # Estilos globales
```

## 🔑 Funcionalidades Principales

### Autenticación
- Registro de usuarios con validación
- Login con JWT
- Autenticación de dos factores (2FA)
- Gestión de sesiones seguras

### Grupos de Inversión
- Creación y configuración de grupos
- Invitación de miembros
- Dashboard de grupo con métricas
- Votaciones para decisiones de inversión

### Gestión Financiera
- Registro de contribuciones
- Generación de QR para pagos
- Historial de transacciones
- Cálculo automático de rendimientos

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es de código privado. Todos los derechos reservados.

## 👥 Autor

Desarrollado con ❤️ por el equipo de GroupGrow

---

**Nota**: Este proyecto está en desarrollo activo. Algunas funcionalidades pueden estar sujetas a cambios.

