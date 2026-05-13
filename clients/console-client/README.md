# Cliente de Consola Java - Conversor de Unidades SOAP

Cliente de consola Java puro que consume el servicio web SOAP del servidor Payara para realizar conversiones de unidades (Masa, Longitud, Temperatura).

## Características

- **Arquitectura MVC**: Código organizado en modelos, vistas y controladores
- **Autenticación segura**: Login con credenciales predefinidas y contraseña enmascarada
- **Menú interactivo por flechas**: Navegación TUI usando teclas de dirección (↑ ↓)
- **Consumo SOAP JAX-WS**: Usa stubs generados automáticamente desde el WSDL

## Estructura del Proyecto

```
src/main/java/ec/edu/grupo3/client/
├── MainApplication.java          # Punto de entrada
├── models/
│   ├── UserSession.java           # Estado de sesión del usuario
│   └── SoapServiceModel.java      # Capa de acceso al servicio SOAP
├── views/
│   ├── ConsoleHelper.java         # Utilidades ANSI y navegación por flechas
│   ├── AuthView.java              # Interfaz de autenticación
│   └── MenuView.java              # Interfaz de menús de conversión
└── controllers/
    ├── AuthController.java        # Controlador de autenticación
    └── ConversionController.java  # Controlador de conversiones
```

## Requisitos

- Java 21
- Maven 3.8+
- Servidor Payara ejecutándose en `localhost:8080`

## Credenciales de Acceso

- **Usuario**: `MONSTER`
- **Contraseña**: `MONSTER9`

## Compilación

```bash
# Desde el directorio del proyecto
cd /home/gyro/Documents/ULTIMO_SEMESTRE/ARQUITECTURA/04.SERVIDOR/clients/console-client

# Compilar y generar JAR ejecutable
mvn clean package
```

## Ejecución

```bash
# Ejecutar el JAR generado
java -jar target/conversion-console-client.jar

# O directamente con Maven
mvn exec:java -Dexec.mainClass="ec.edu.grupo3.client.MainApplication"
```

## Cómo Usar

### 1. Pantalla de Login
Al iniciar la aplicación, se muestra la pantalla de autenticación:
- Ingrese el usuario: `MONSTER`
- Ingrese la contraseña: `MONSTER9` (se muestra enmascarada con asteriscos)
- Presione Enter para iniciar sesión

### 2. Menú Principal
Una vez autenticado, verá el menú principal con 4 opciones:
- Conversión de Masa (kg, g, lb, oz)
- Conversión de Longitud (m, km, cm, mi, yd, ft, in)
- Conversión de Temperatura (C, F, K)
- Cerrar Sesión y Salir

**Navegación por flechas**:
- Use las flechas ↑ y ↓ para seleccionar una opción
- La opción seleccionada se muestra con fondo resaltado
- Presione Enter para confirmar la selección

### 3. Selección de Unidades
Para realizar una conversión:
1. Seleccione la unidad de origen usando las flechas
2. Seleccione la unidad de destino usando las flechas
3. Ingrese el valor numérico a convertir

### 4. Resultado
El sistema muestra el resultado formateado:
```
╔════════════════════════════════════════════════════════════╗
║  RESULTADO DE CONVERSIÓN SOAP DEVUELTO                     ║
╠════════════════════════════════════════════════════════════╣
║  Categoría : Mass                                           ║
║  Origen    : 1.0000 KILOGRAM                                ║
║  Destino   : 2.2046 POUND                                    ║
╚════════════════════════════════════════════════════════════╝
```

## Solución de Problemas

### Error: "El servidor SOAP no está disponible"
Asegúrese de que Payara esté ejecutándose:
```bash
sudo systemctl start payara
```

### Las flechas no funcionan
El menú por flechas requiere una terminal real. Si usa un IDE, ejecute el JAR desde una terminal real (gnome-terminal, konsole, xterm).

### Error de compilación con wsimport
Si el servidor no está disponible, puede generar stubs manualmente:
```bash
mvn generate-sources
```
Luego compile sin el plugin wsimport:
```bash
mvn clean package -DskipTests -Djaxws.skip=true
```

## Configuración del WSDL

El proyecto está configurado para conectar a:
```
http://localhost:8080/04.SERVIDOR/conversion?wsdl
```

Para cambiar la URL del servidor, modifique el archivo `pom.xml` en la sección de configuración del plugin `jaxws-maven-plugin`.

## Tecnologías Usadas

- **Jakarta XML Web Services 4.0.2** - API JAX-WS
- **Metro JAX-WS Runtime 4.0.3** - Implementación del servidor
- **Maven Shade Plugin** - Para crear JAR ejecutable con todas las dependencias

## Autor

Grupo 3 - Arquitectura de Software