# Guía del Cliente SOAP - Arquitectura y Flujo de Información

## Contenido
1. [Conceptos Fundamentales](#conceptos-fundamentales)
2. [Flujo Completo de una Petición SOAP](#flujo-completo-de-una-petición-soap)
3. [Guía del Cliente de Consola](#guía-del-cliente-de-consola)
4. [Guía del Cliente Desktop](#guía-del-cliente-desktop)
5. [Cómo se Generan los Stubs](#cómo-se-generan-los-stubs)
6. [Arquitectura del Sistema](#arquitectura-del-sistema)

---

## Conceptos Fundamentales

### ¿Qué es SOAP?
**SOAP (Simple Object Access Protocol)** es un protocolo de mensajería que permite la comunicación entre sistemas distribuidos a través de servicios web.

### ¿Qué es wsimport?
**wsimport** es una herramienta de JDK que genera automáticamente código Java (stubs) a partir de un archivo WSDL. Estos stubs permiten al cliente comunicarse con el servicio SOAP como si fuera una llamada a método local.

### ¿Qué es el WSDL?
**WSDL (Web Services Description Language)** es un documento XML que describe:
- Las operaciones disponibles del servicio
- Los parámetros de entrada y salida
- La ubicación del servicio (URL)
- El formato de los mensajes

---

## Flujo Completo de una Petición SOAP

### Paso a Paso: Desde el Botón hasta la Respuesta

```
┌─────────────────────────────────────────────────────────────────────┐
│                     FLUJO DE UNA PETICIÓN SOAP                      │
└─────────────────────────────────────────────────────────────────────┘

    CLIENTE                                         SERVIDOR (Payara)
    =======                                         ================

    ┌──────────────┐                                 ┌──────────────┐
    │ 1. Usuario   │                                 │              │
    │ selecciona   │                                 │              │
    │ valores y    │                                 │              │
    │ presiona     │                                 │              │
    │ "Convertir"  │                                 │              │
    └──────┬───────┘                                 └──────┬───────┘
           │                                                │
           │ 2. Cliente crea objeto de                     │
           │    petición SOAP                               │
           │ (serialización Java→XML)                       │
           │                                                │
           ▼                                                ▼
    ┌──────────────┐                                 ┌──────────────┐
    │ Stub generado│─── Petición HTTP POST ──────────▶│ Payara       │
    │ por wsimport │    Content-Type: text/xml        │ Server       │
    └──────┬───────┘                                 └──────┬───────┘
           │                                                │
           │                                                │ 3. Payara receive
           │                                                │    la petición
           │                                                ▼
           │                                         ┌──────────────┐
           │                                         │ sun-jaxws.xml│
           │                                         │ (mapeo URL   │
           │                                         │ → clase Java)│
           │                                         └──────┬───────┘
           │                                                │
           │                                                │ 4. JAX-WS
           │                                                │    deserializa
           │                                                │    XML→Java
           │                                                ▼
           │                                         ┌──────────────┐
           │                                         │ Conversion   │
           │                                         │ SoapWS.java  │
           │                                         │ @WebService  │
           │                                         └──────┬───────┘
           │                                                │
           │                                                │ 5. Ejecuta
           │                                                │    lógica
           │                                                ▼
           │                                         ┌──────────────┐
           │                                         │ Model        │
           │                                         │ (fórmulas    │
           │                                         │ matemáticas) │
           │                                         └──────┬───────┘
           │                                                │
           │ 6. Serializa                                 │ 7. Crea
           │    respuesta Java→XML                        │    respuesta
           │                                                ▼
           │                                         ┌──────────────┐
           │    ┌────────────────────────────────────│ SOAP         │
           │    │         Petición HTTP response     │ Response     │
           │    │         (XML en body)              │ (XML)        │
           │    ▼                                    └──────┬───────┘
           │                                                │
    ┌──────┴───────┐                                        │
    │ Stub genera  │◀────────────────────────────────────────┘
    │ objeto Java  │    8. Payara envía respuesta HTTP
    │ desde XML    │       con XML en el body
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │ 9. Interfaz  │
    │ muestra      │
    │ resultado    │
    └──────────────┘
```

---

## Guía del Cliente de Consola

### Archivo: `clients/console-client/src/main/java/ec/edu/grupo3/client/ConsoleClient.java`

### Estructura del Código

```java
public class ConsoleClient {
    // URL del WSDL - define la ubicación del contrato del servicio
    private static final String WSDL_URL = "http://localhost:8080/04.SERVIDOR/conversion?wsdl";
    
    // Namespace URI - identifica el espacio de nombres del servicio
    private static final String NAMESPACE_URI = "http://ws.grupo3.edu.ec/";
    
    // Nombre del servicio (del WSDL)
    private static final String SERVICE_NAME = "ConversionService";
```

### Enfoque 1: Con Stubs Generados

```java
// 1. Crear instancia del servicio
// El servicio es una clase generada por wsimport que representa el WSDL
ConversionService service = new ConversionService();

// 2. Obtener el puerto
// El puerto es el endpoint donde están los métodos del servicio
// JAX-WS crea un proxy local que representa el servicio remoto
ConversionSoapWS port = service.getConversionSoapWSPort();

// 3. Llamar al método
// Se llama como si fuera un método local, pero internamente
// JAX-WS serializa los parámetros, envía HTTP, recibe respuesta, etc.
ConversionResponseView response = port.convertMass(value, fromUnit, toUnit);
```

**Flujo interno de JAX-WS:**
1. Serializa los parámetros (Java → XML)
2. Crea mensaje SOAP completo
3. Envía HTTP POST al servidor
4. Recibe respuesta
5. Deserializa (XML → Java)
6. Retorna el objeto de respuesta

### Enfoque 2: Sin Stubs (Dinámico)

```java
// 1. Crear URL del WSDL
URL wsdlUrl = new URL(WSDL_URL);

// 2. Crear QName (Qualified Name)
// Identifica el servicio en el namespace
QName serviceName = new QName(NAMESPACE_URI, SERVICE_NAME);

// 3. Crear servicio dinámicamente
Service service = Service.create(wsdlUrl, serviceName);

// 4. Obtener puerto dinámicamente
// JAX-WS crea un proxy dinámico en tiempo de ejecución
ConversionSoapWS port = service.getPort(ConversionSoapWS.class);
```

---

## Guía del Cliente Desktop

### Archivo: `clients/desktop-client/src/main/java/ec/edu/grupo3/client/DesktopClient.java`

### Arquitectura JavaFX

```
┌──────────────────────────────────────────────────────────────────┐
│                    DesktopClient (JavaFX)                        │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Application.start(Stage)                                        │
│       │                                                          │
│       ├── createHeader()        → Encabezado con título          │
│       ├── createTabPane()       → Pestañas (Mass/Length/Temp)    │
│       │       ├── createMassConversionTab()                     │
│       │       ├── createLengthConversionTab()                   │
│       │       └── createTemperatureConversionTab()              │
│       │                                                          │
│       └── createStatusBar()     → Barra de estado                │
│                                                                   │
│  Método de Conversión (ejemplo: handleMassConversion)            │
│       │                                                          │
│       ├── 1. Obtener valores de la UI                           │
│       ├── 2. Validar entrada del usuario                        │
│       ├── 3. Crear CompletableFuture (hilo asíncrono)           │
│       │       ├── Llamar servicio SOAP                          │
│       │       └── Platform.runLater() → actualizar UI           │
│       └── 4. Mostrar resultado o error                          │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

### Flujo de Eventos en JavaFX

```java
// Cuando el usuario presiona "Convertir":
convertButton.setOnAction(e -> {
    // 1. Deshabilitar botón (evitar doble clic)
    convertButton.setDisable(true);
    
    // 2. Mostrar indicador de progreso
    progressBar.setVisible(true);
    
    // 3. Ejecutar en hilo separado (NO el hilo de JavaFX)
    CompletableFuture.runAsync(() -> {
        try {
            // LLAMADA SOAP AQUÍ
            ConversionResponseView response = soapService.convertMass(...);
            
            // 4. Actualizar UI en el hilo de JavaFX
            Platform.runLater(() -> {
                displayResult(response);
                convertButton.setDisable(false);
                progressBar.setVisible(false);
            });
        } catch (Exception ex) {
            Platform.runLater(() -> {
                showError(ex.getMessage());
                convertButton.setDisable(false);
                progressBar.setVisible(false);
            });
        }
    });
});
```

### Componentes de la Interfaz

| Componente | Descripción |
|------------|-------------|
| `TabPane` | Contenedor con pestañas para cada tipo de conversión |
| `TextField` | Campo para ingresar el valor numérico |
| `ComboBox<String>` | Lista desplegable para seleccionar unidades |
| `Button` | Botón para ejecutar la conversión |
| `ProgressBar` | Indicador de progreso durante la petición |
| `Label` | Muestra el resultado o errores |

---

## Cómo se Generan los Stubs

### Comando wsimport

```bash
wsimport -keep \
         -d target/classes \
         -s src/main/java \
         -p ec.edu.grupo3.client.generated \
         http://localhost:8080/04.SERVIDOR/conversion?wsdl
```

### Parámetros:
- `-keep`: Mantiene los archivos .java generados
- `-d`: Directorio de salida para clases compiladas
- `-s`: Directorio de salida para código fuente
- `-p`: Paquete Java para las clases generadas

### Clases Generadas

El wsimport genera automáticamente:

```
generated/
├── ConversionService.java        # Clase del servicio (punto de entrada)
├── ConversionSoapWS.java         # Interfaz del puerto
├── ConversionSoapWSPort.java     # Implementación del proxy
├── ConversionResponseView.java   # Clase de respuesta
└── ObjectFactory.java            # Factory para JAXB
```

### Relación con el WSDL

```
WSDL (http://localhost:8080/04.SERVIDOR/conversion?wsdl)
    │
    ├── <service name="ConversionService">
    │       └───→ GenerationService.java
    │
    ├── <portType name="ConversionSoapWS">
    │       └───→ ConversionSoapWS.java (interfaz)
    │
    ├── <binding name="ConversionSoapWSPortBinding">
    │       └───→ ConversionSoapWSPort.java (implementación proxy)
    │
    └── <types>
            └───→ ConversionResponseView.java, ObjectFactory.java
```

---

## Arquitectura del Sistema

### Diagrama General

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ARQUITECTURA SOAP                           │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────┐                          ┌─────────────────────┐
│   CLIENTE (Java)    │                          │   SERVIDOR          │
│                     │                          │   (Payara 7)        │
│  ┌───────────────┐  │    HTTP(S) + XML/SOAP   │                     │
│  │ Interfaz UI   │  │ ──────────────────────▶ │  ┌───────────────┐  │
│  │ (Console/FX)  │  │                          │  │ web.xml       │  │
│  └───────┬───────┘  │ ◀────────────────────── │  │ (servlet)     │  │
│          │          │    HTTP(S) + XML/SOAP   │  └───────┬───────┘  │
│          ▼          │                          │          │          │
│  ┌───────────────┐  │                          │          ▼          │
│  │ Cliente SOAP  │  │                          │  ┌───────────────┐  │
│  │ (Stubs o      │  │                          │  │ sun-jaxws.xml │  │
│  │  Dinámico)    │  │                          │  │ (endpoint)    │  │
│  └───────┬───────┘  │                          │  └───────┬───────┘  │
│          │          │                          │          │          │
│          ▼          │                          │          ▼          │
│  ┌───────────────┐  │                          │  ┌───────────────┐  │
│  │ Proxy JAX-WS  │  │                          │  │ Conversion    │  │
│  │ (Runtime)     │  │                          │  │ SoapWS.java   │  │
│  └───────┬───────┘  │                          │  │ @WebService   │  │
└──────────│──────────┘                          └──────────┬──────────┘
           │                                                │
           │         Serialización/Deserialización          │
           │         (Java Object ↔ XML)                    │
           ▼                                                ▼
    ┌───────────────┐                          ┌─────────────────────┐
    │ Request XML   │                          │ Lógica de Negocio   │
    │ Response XML  │                          │ (Model classes)     │
    └───────────────┘                          └─────────────────────┘
```

---

## Configuración y Ejecución

### Prerrequisitos

1. **Servidor Payara corriendo:**
   ```bash
   sudo systemctl start payara.service
   ```

2. **Servicio desplegado** en: `http://localhost:8080/04.SERVIDOR/`

### Compilar y Ejecutar Cliente de Consola

```bash
# En el directorio del cliente de consola
cd clients/console-client

# Generar stubs y compilar (primera vez)
./build.sh

# O manualmente:
wsimport -keep -s src/main/java -p ec.edu.grupo3.client.generated \
    http://localhost:8080/04.SERVIDOR/conversion?wsdl

mvn clean package

# Ejecutar
java -jar target/conversion-console-client.jar
```

### Compilar y Ejecutar Cliente Desktop

```bash
# En el directorio del cliente desktop
cd clients/desktop-client

# Generar stubs y compilar (primera vez)
./build.sh

# O manualmente:
wsimport -keep -s src/main/java -p ec.edu.grupo3.client.generated \
    http://localhost:8080/04.SERVIDOR/conversion?wsdl

mvn clean package

# Ejecutar (requiere JavaFX)
java -jar target/conversion-desktop-client.jar
```

---

## Archivos Clave Explicados

### 1. ConversionSoapWS.java (Servidor)

```java
@WebService(serviceName = "ConversionService")  // Define el nombre del servicio
public class ConversionSoapWS {

    @WebMethod(operationName = "convertMass")    // Define la operación
    public ConversionResponseView convertMass(
        @WebParam(name = "value") double value,  // Parámetro con nombre
        @WebParam(name = "fromUnit") String fromUnit,
        @WebParam(name = "toUnit") String toUnit
    ) {
        // La lógica real está en los Models
        double result = massConverterModel.convert(value, from, to);
        return new ConversionResponseView(...)
    }
}
```

### 2. sun-jaxws.xml (Servidor)

```xml
<endpoints version="2.0">
    <endpoint name="ConversionService"
              implementation="ec.edu.grupo3.ws.ConversionSoapWS"
              url-pattern="/conversion"/>
</endpoints>
```
**Este archivo mapea:**
- Nombre lógico → Clase Java
- URL relativa (/conversion) → Endpoint HTTP

### 3. web.xml (Servidor)

```xml
<listener>
    <listener-class>com.sun.xml.ws.transport.http.servlet.WSServletContextListener</listener-class>
</listener>

<servlet>
    <servlet-name>jaxws-servlet</servlet-name>
    <servlet-class>com.sun.xml.ws.transport.http.servlet.WSServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>jaxws-servlet</servlet-name>
    <url-pattern>/soap/*</url-pattern>
</servlet-mapping>
```

---

## Solución de Problemas

### Error: "Connection refused"
- El servidor Payara no está corriendo
- Ejecutar: `sudo systemctl start payara.service`

### Error: "404 Not Found"
- La aplicación no está desplegada
- Verificar: `/opt/payara7/bin/asadmin list-applications`

### Error: "Cannot find destination"
- Los stubs no están generados
- Ejecutar: `wsimport` con la URL del WSDL

### Error: "NullPointerException" en cliente
- El servicio SOAP no está inicializado
- Verificar que `initializeSoapService()` se llamó

---

## Resumen del Aprendizaje

| Concepto | Descripción |
|----------|-------------|
| **SOAP** | Protocolo para intercambio de mensajes XML sobre HTTP |
| **WSDL** | Contrato que describe el servicio en XML |
| **wsimport** | Herramienta que genera código Java desde WSDL |
| **Stubs** | Clases Java generadas que representan el servicio |
| **Proxy** | Objeto local que representa el servicio remoto |
| **Serialización** | Conversión Java → XML |
| **Deserialización** | Conversión XML → Java |
| **JAX-WS** | API Java para servicios web SOAP |

---

*Esta guía forma parte del proyecto de Arquitectura de Software - Grupo 3*