# Cliente de Escritorio - Servicio SOAP de Conversión

Aplicación de escritorio JavaFX que se conecta a un servicio web SOAP para realizar conversiones de unidades (masa, longitud, temperatura).

## Requisitos Previos

- **Java Development Kit (JDK)** versión 21 o superior
- **Maven** 3.8 o superior
- **Servidor VPS** ejecutándose en `http://209.145.48.25:8081/ROOT/Conversion`

## Estructura del Proyecto

```
desktop-client/
├── pom.xml                    # Configuración de Maven
├── src/main/java/ec/edu/grupo3/client/
│   ├── DesktopClient.java     # Punto de entrada principal
│   ├── controller/
│   │   └── ConversionController.java  # Lógica de conversión
│   ├── model/
│   │   └── ConversionModel.java        # Modelo de datos
│   ├── view/
│   │   ├── LoginView.java    # Vista de inicio de sesión
│   │   └── MainView.java     # Vista principal de conversión
│   └── generated/             # Clases generadas desde WSDL
└── target/                    # Archivos compilados
```

## Tecnologías Utilizadas

- **JavaFX 21** - Framework de UI para escritorio
- **JAX-WS** - Cliente SOAP para comunicaciones web
- **Maven** - Gestión de dependencias y construcción

## Credenciales de Acceso

- **Usuario:** MONSTER
- **Contraseña:** MONSTER9

## Comandos para Ejecutar

### 1. Compilar el proyecto

```bash
cd /home/gyro/Documents/ULTIMO_SEMESTRE/ARQUITECTURA/04.SERVIDOR/clients/desktop-client

mvn clean compile
```

Este comando:
- Limpia compilaciones anteriores
- Descarga las dependencias necesarias
- Genera las clases stub desde el WSDL del servidor
- Compila el código fuente

### 2. Ejecutar la aplicación

```bash
mvn exec:java -Dexec.mainClass="ec.edu.grupo3.client.DesktopClient"
```

### 3. Ejecutar con el script incluido

```bash
./build.sh
```

### 4. Crear JAR ejecutable

```bash
mvn package
```

Esto genera `target/conversion-desktop-client.jar` que puede ejecutarse con:

```bash
java -jar target/conversion-desktop-client.jar
```

## Configuración del Servidor SOAP

El cliente está configurado para conectarse al VPS de producción:

```
http://209.145.48.25:8081/ROOT/Conversion?wsdl
```

Si necesitas modificar esta URL, edita el archivo `pom.xml`:

```xml
<wsdlUrl>http://209.145.48.25:8081/ROOT/Conversion?wsdl</wsdlUrl>
```

## Funcionalidades

### Pantalla de Login
- Autenticación con usuario y contraseña
- Validación de campos vacíos
- Mensajes de error visibles

### Conversión de Unidades

#### Masa
- KILOGRAM (Kilogramo)
- GRAM (Gramo)
- POUND (Libra)
- OUNCE (Onza)

#### Longitud
- METER (Metro)
- KILOMETER (Kilómetro)
- CENTIMETER (Centímetro)
- MILE (Milla)
- YARD (Yarda)
- FOOT (Pie)
- INCH (Pulgada)

#### Temperatura
- CELSIUS (Celsius)
- FAHRENHEIT (Fahrenheit)
- KELVIN (Kelvin)

### Interfaz de Usuario
- Diseño moderno con tema oscuro
- Barras de progreso durante las conversiones
- Indicador de estado de conexión
- Botón de cierre de sesión

## Solución de Problemas

### Error: "Cannot find WSDL"
Asegúrate de que el servidor VPS esté disponible:
```bash
curl http://209.145.48.25:8081/ROOT/Conversion?wsdl
```

### Error: "Unsupported JavaFX configuration"
Warning informativo, no afecta la funcionalidad.

### El botón de login no funciona
Presiona **Enter** en el campo de contraseña después de escribir las credenciales.

### La conversión se queda pensando
Verifica que el servidor SOAP esté respondiendo correctamente.

## Notas de Desarrollo

- Los eventos de los botones se configuran DESPUÉS de crear la vista para evitar NullPointerException
- Las llamadas al servicio SOAP se ejecutan en hilos separados para no bloquear la UI
- El modelo de datos (`ConversionModel`) maneja la comunicación con el servicio web

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    DesktopClient (Main)                     │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
        ┌─────────────┐                 ┌─────────────┐
        │ LoginView   │                 │  MainView   │
        └─────────────┘                 └─────────────┘
              │                               │
              ▼                               ▼
        ┌─────────────────────────────────────────────────┐
        │            ConversionController                 │
        └─────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────────────────┐
        │            ConversionModel                     │
        │     (Llamadas SOAP al servidor)                │
        └─────────────────────────────────────────────────┘
```

## autora

Este proyecto fue desarrollado como parte del curso de Arquitectura de Sistemas.